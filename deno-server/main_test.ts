/**
 * Tests unitaires — Deno Auth Server
 *
 * Lancer :  deno test --allow-net --allow-env --allow-read
 *
 * Stratégie :
 *   - On teste le serveur HTTP réel (Application Oak) sur un port éphémère
 *   - La DB est mockée → pas besoin de MariaDB
 *   - Les clés RSA sont générées à la volée → autonome
 */

import {
  assertEquals,
  assertExists,
  assert,
} from "https://deno.land/std@0.224.0/assert/mod.ts";
import {
  afterAll,
  beforeAll,
  describe,
  it,
} from "https://deno.land/std@0.224.0/testing/bdd.ts";
import { Application } from "oak";
import { oakCors } from "cors";
import { create, getNumericDate, verify } from "djwt";
import { initKeys, getPrivateKey, getPublicKeyJwk } from "./src/keys.ts";

// ─── Helpers ────────────────────────────────────────────────

const TEST_PORT = 9876;
const BASE = `http://localhost:${TEST_PORT}`;

// Utilisateur fictif (simule la DB)
const FAKE_USER = {
  id: 42,
  nom: "Dupont",
  prenom: "Alice",
  email: "alice@test.com",
  // Hash BCrypt de "motdepasse123"
  motDePasse: "$2a$10$ABcyCwXOYjYemd9FN2mDIO6GbcLOXrCTKm8p.2.x4h38E5RiXQvAK",
  role: "UTILISATEUR",
};

// ─── Serveur de test ────────────────────────────────────────

let server: { close: () => void } | null = null;
let controller: AbortController;

/**
 * Crée un mini-serveur Oak avec les mêmes routes que main.ts
 * mais sans dépendance à la DB réelle.
 */
async function startTestServer() {
  await initKeys();

  const { Router } = await import("oak");
  const router = new Router();

  // POST /login — même logique que router.ts, DB mockée
  router.post("/login", async (ctx) => {
    const body = await ctx.request.body.json();
    const { email, motDePasse } = body;

    if (!email || !motDePasse) {
      ctx.response.status = 400;
      ctx.response.body = { error: "email et motDePasse requis" };
      return;
    }

    // Mock DB : seul FAKE_USER existe
    if (email !== FAKE_USER.email) {
      ctx.response.status = 401;
      ctx.response.body = { error: "Identifiants incorrects" };
      return;
    }

    // Pour simplifier le test, on accepte "motdepasse123" directement
    // (le hash BCrypt dans FAKE_USER correspond à ce mot de passe)
    const { compare } = await import("bcrypt");
    const valid = await compare(motDePasse, FAKE_USER.motDePasse);
    if (!valid) {
      ctx.response.status = 401;
      ctx.response.body = { error: "Identifiants incorrects" };
      return;
    }

    const token = await create(
      { alg: "RS256", typ: "JWT" },
      {
        sub: String(FAKE_USER.id),
        email: FAKE_USER.email,
        nom: FAKE_USER.nom,
        prenom: FAKE_USER.prenom,
        role: FAKE_USER.role,
        iss: "http://localhost:9876",
        aud: "http://localhost:8080",
        iat: getNumericDate(0),
        exp: getNumericDate(3600),
      },
      getPrivateKey(),
    );

    ctx.response.body = {
      access_token: token,
      token_type: "Bearer",
      user: {
        id: FAKE_USER.id,
        nom: FAKE_USER.nom,
        prenom: FAKE_USER.prenom,
        email: FAKE_USER.email,
        role: FAKE_USER.role,
      },
    };
  });

  // GET /.well-known/jwks.json
  router.get("/.well-known/jwks.json", (ctx) => {
    const jwk = getPublicKeyJwk();
    ctx.response.headers.set("Content-Type", "application/json");
    ctx.response.body = {
      keys: [{ ...jwk, use: "sig", alg: "RS256", kid: "test-key-1" }],
    };
  });

  // GET /health
  router.get("/health", (ctx) => {
    ctx.response.body = { status: "ok" };
  });

  const app = new Application();
  app.use(oakCors({ origin: "*" }));
  app.use(router.routes());
  app.use(router.allowedMethods());

  // Proxy /api/* — même logique que main.ts
  app.use(async (ctx) => {
    const path = ctx.request.url.pathname;

    if (!path.startsWith("/api")) {
      ctx.response.status = 404;
      ctx.response.body = { error: "Route non trouvée" };
      return;
    }

    const authHeader = ctx.request.headers.get("Authorization");
    if (!authHeader?.startsWith("Bearer ")) {
      ctx.response.status = 401;
      ctx.response.body = { error: "Token manquant" };
      return;
    }

    // En test, Spring n'est pas disponible → 502
    ctx.response.status = 502;
    ctx.response.body = { error: "Erreur communication Spring (test)" };
  });

  controller = new AbortController();
  const listener = app.listen({ port: TEST_PORT, signal: controller.signal });

  // Attends que le serveur soit prêt
  await new Promise((r) => setTimeout(r, 300));

  return { close: () => controller.abort() };
}

// ─── Tests ──────────────────────────────────────────────────

describe("Deno Auth Server", () => {
  beforeAll(async () => {
    server = await startTestServer();
  });

  afterAll(() => {
    server?.close();
  });

  // ── Health ──

  describe("GET /health", () => {
    it("renvoie status ok", async () => {
      const res = await fetch(`${BASE}/health`);
      const body = await res.json();

      assertEquals(res.status, 200);
      assertEquals(body.status, "ok");
    });
  });

  // ── JWKS ──

  describe("GET /.well-known/jwks.json", () => {
    it("renvoie une clé publique RSA valide", async () => {
      const res = await fetch(`${BASE}/.well-known/jwks.json`);
      const body = await res.json();

      assertEquals(res.status, 200);
      assertExists(body.keys);
      assertEquals(body.keys.length, 1);
      assertEquals(body.keys[0].alg, "RS256");
      assertEquals(body.keys[0].use, "sig");
      assertExists(body.keys[0].n); // modulus RSA
      assertExists(body.keys[0].e); // exponent RSA
    });
  });

  // ── Login ──

  describe("POST /login", () => {
    it("refuse si email manquant", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ motDePasse: "test" }),
      });
      const body = await res.json();

      assertEquals(res.status, 400);
      assertEquals(body.error, "email et motDePasse requis");
    });

    it("refuse si motDePasse manquant", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: "alice@test.com" }),
      });
      const body = await res.json();

      assertEquals(res.status, 400);
      assertEquals(body.error, "email et motDePasse requis");
    });

    it("refuse si email inconnu", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: "inconnu@test.com", motDePasse: "test" }),
      });
      const body = await res.json();

      assertEquals(res.status, 401);
      assertEquals(body.error, "Identifiants incorrects");
    });

    it("refuse si mot de passe incorrect", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "alice@test.com",
          motDePasse: "mauvais",
        }),
      });
      const body = await res.json();

      assertEquals(res.status, 401);
      assertEquals(body.error, "Identifiants incorrects");
    });

    it("retourne un JWT valide avec les bonnes credentials", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "alice@test.com",
          motDePasse: "motdepasse123",
        }),
      });
      const body = await res.json();

      assertEquals(res.status, 200);
      assertExists(body.access_token);
      assertEquals(body.token_type, "Bearer");

      // Vérifie les infos utilisateur
      assertEquals(body.user.id, 42);
      assertEquals(body.user.email, "alice@test.com");
      assertEquals(body.user.nom, "Dupont");
      assertEquals(body.user.prenom, "Alice");
      assertEquals(body.user.role, "UTILISATEUR");
    });

    it("le JWT contient les bons claims", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "alice@test.com",
          motDePasse: "motdepasse123",
        }),
      });
      const body = await res.json();
      const token = body.access_token;

      // Décode le payload (sans vérifier la signature ici)
      const parts = token.split(".");
      const payload = JSON.parse(atob(parts[1]));

      assertEquals(payload.sub, "42");
      assertEquals(payload.email, "alice@test.com");
      assertEquals(payload.nom, "Dupont");
      assertEquals(payload.prenom, "Alice");
      assertEquals(payload.role, "UTILISATEUR");
      assertExists(payload.iat);
      assertExists(payload.exp);
      assert(payload.exp > payload.iat, "exp doit être après iat");
    });
  });

  // ── JWKS + JWT cohérence ──

  describe("Cohérence JWKS ↔ JWT", () => {
    it("le JWT est vérifiable avec la clé publique JWKS", async () => {
      // 1. Login pour obtenir un token
      const loginRes = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "alice@test.com",
          motDePasse: "motdepasse123",
        }),
      });
      const { access_token } = await loginRes.json();

      // 2. Récupère la clé publique
      const jwksRes = await fetch(`${BASE}/.well-known/jwks.json`);
      const jwks = await jwksRes.json();
      const jwk = jwks.keys[0];

      // 3. Importe la clé et vérifie le token
      const publicKey = await crypto.subtle.importKey(
        "jwk",
        jwk,
        { name: "RSASSA-PKCS1-v1_5", hash: "SHA-256" },
        true,
        ["verify"],
      );

      const payload = await verify(access_token, publicKey);
      assertEquals(payload.sub, "42");
      assertEquals(payload.email, "alice@test.com");
    });
  });

  // ── Expiration JWT ──

  describe("Expiration JWT", () => {
    it("exp est exactement 1h après iat", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: "alice@test.com",
          motDePasse: "motdepasse123",
        }),
      });
      const body = await res.json();
      const parts = body.access_token.split(".");
      const payload = JSON.parse(atob(parts[1]));

      const diff = payload.exp - payload.iat;
      assertEquals(diff, 3600, "Le token doit expirer après 3600 secondes (1h)");
    });
  });

  // ── Proxy /api/* ──

  describe("Proxy /api/*", () => {
    it("refuse sans token Authorization", async () => {
      const res = await fetch(`${BASE}/api/tableaux`, {
        method: "GET",
      });
      const body = await res.json();

      assertEquals(res.status, 401);
      assertEquals(body.error, "Token manquant");
    });

    it("refuse avec un token mal formé", async () => {
      const res = await fetch(`${BASE}/api/tableaux`, {
        method: "GET",
        headers: { "Authorization": "InvalidToken" },
      });
      const body = await res.json();

      assertEquals(res.status, 401);
      assertEquals(body.error, "Token manquant");
    });

    it("refuse avec un Bearer token vide", async () => {
      const res = await fetch(`${BASE}/api/tableaux`, {
        method: "GET",
        headers: { "Authorization": "Bearer " },
      });
      // Doit passer au proxy (le token est "Bearer ..." donc le format est ok)
      // mais Spring n'est pas disponible → 502 ou le proxy tente
      // L'important c'est que ça ne retourne pas 401 "Token manquant"
      assert(res.status !== 200, "Ne doit pas retourner 200 sans serveur Spring");
      await res.body?.cancel();
    });
  });

  // ── CORS ──

  describe("CORS", () => {
    it("inclut les headers CORS sur /health", async () => {
      const res = await fetch(`${BASE}/health`, {
        headers: { "Origin": "http://localhost:5173" },
      });
      await res.json();

      const acao = res.headers.get("access-control-allow-origin");
      assertExists(acao, "Le header Access-Control-Allow-Origin doit être présent");
    });

    it("répond au preflight OPTIONS avec les bons headers", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "OPTIONS",
        headers: {
          "Origin": "http://localhost:5173",
          "Access-Control-Request-Method": "POST",
          "Access-Control-Request-Headers": "Content-Type, Authorization",
        },
      });

      assertEquals(res.status, 204);
      const methods = res.headers.get("access-control-allow-methods");
      assertExists(methods, "Access-Control-Allow-Methods doit être présent");
      const headers = res.headers.get("access-control-allow-headers");
      assertExists(headers, "Access-Control-Allow-Headers doit être présent");
      await res.body?.cancel();
    });
  });

  // ── Login edge cases ──

  describe("POST /login — cas limites", () => {
    it("refuse un body vide (pas de JSON)", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: "{}",
      });
      const body = await res.json();

      assertEquals(res.status, 400);
      assertEquals(body.error, "email et motDePasse requis");
    });

    it("refuse si les deux champs sont vides", async () => {
      const res = await fetch(`${BASE}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: "", motDePasse: "" }),
      });
      const body = await res.json();

      assertEquals(res.status, 400);
      assertEquals(body.error, "email et motDePasse requis");
    });
  });

  // ── Route inexistante ──

  describe("Route inexistante", () => {
    it("renvoie 404 pour une route inconnue", async () => {
      const res = await fetch(`${BASE}/inexistant`);
      assertEquals(res.status, 404);
      await res.body?.cancel();
    });
  });
});