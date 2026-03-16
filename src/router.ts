import { Router } from "oak";
import { create, getNumericDate } from "djwt";
import { config } from "./config.ts";
import { initKeys, getPrivateKey, getPublicKeyJwk } from "./keys.ts";
import { findUserByEmail, verifierMotDePasse } from "./db.ts";

await initKeys();

export const router = new Router();

// ─────────────────────────────────────────
// POST /login
// ─────────────────────────────────────────
router.post("/login", async (ctx) => {
  const body = await ctx.request.body.json();
  const { email, mot_de_passe } = body;

  if (!email || !mot_de_passe) {
    ctx.response.status = 400;
    ctx.response.body = { error: "email et mot_de_passe requis" };
    return;
  }

  const user = await findUserByEmail(email);

  if (!user) {
    ctx.response.status = 401;
    ctx.response.body = { error: "Identifiants incorrects" };
    return;
  }

  // Vérifie le mot de passe avec BCrypt
  const motDePasseValide = await verifierMotDePasse(
    mot_de_passe,
    user.motDePasse,
  );

  if (!motDePasseValide) {
    ctx.response.status = 401;
    ctx.response.body = { error: "Identifiants incorrects" };
    return;
  }

  const token = await create(
    { alg: "RS256", typ: "JWT" },
    {
      sub: String(user.id),
      email: user.email,
      nom: user.nom,
      prenom: user.prenom,
      role: user.role,
      iss: config.jwt.issuer,
      aud: config.jwt.audience,
      iat: getNumericDate(0),
      exp: getNumericDate(60 * 60 * config.jwt.expirationHours),
    },
    getPrivateKey(),
  );

  ctx.response.status = 200;
  ctx.response.body = {
    access_token: token,
    token_type: "Bearer",
    user: {
      id: user.id,
      nom: user.nom,
      prenom: user.prenom,
      email: user.email,
      role: user.role,
    },
  };
});

// ─────────────────────────────────────────
// GET /.well-known/jwks.json
// ─────────────────────────────────────────
router.get("/.well-known/jwks.json", (ctx) => {
  const jwk = getPublicKeyJwk();
  ctx.response.headers.set("Content-Type", "application/json");
  ctx.response.body = {
    keys: [
      {
        ...jwk,
        use: "sig",
        alg: "RS256",
        kid: "auth-server-key-1",
      },
    ],
  };
});

// ─────────────────────────────────────────
// GET /health
// ─────────────────────────────────────────
router.get("/health", (ctx) => {
  ctx.response.body = { status: "ok" };
});
