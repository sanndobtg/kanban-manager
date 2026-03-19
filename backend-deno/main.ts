import { Application } from "oak";
import { load } from "https://deno.land/std@0.224.0/dotenv/mod.ts";
import { router } from "./src/router.ts";
import { logger } from "./src/middleware/logger.ts";
import { oakCors } from "cors";

// Charge les variables d'environnement
await load({ export: true });

const app = new Application();
const PORT = Number(Deno.env.get("PORT")) || 3000;

// Middleware global
app.use(logger);

// CORS
app.use(oakCors({
  origin: ["http://localhost:5173", "http://localhost:5174"],
  methods: ["GET", "POST", "PUT", "DELETE","PATCH"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));

// Routes
app.use(router.routes());
app.use(router.allowedMethods());

// Proxy /api/* vers Spring
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

  // TODO: ta vérification JWT ici

  const targetUrl = `http://localhost:8080${path}${ctx.request.url.search}`;
  const headers = new Headers();
  headers.set("Authorization", authHeader);

  const contentType = ctx.request.headers.get("Content-Type");
  if (contentType) headers.set("Content-Type", contentType);

  let body: BodyInit | null = null;
  if (!["GET", "HEAD", "DELETE"].includes(ctx.request.method)) {
    try { body = await ctx.request.body.arrayBuffer(); } catch { body = null; }
  }

  try {
    const res = await fetch(targetUrl, { method: ctx.request.method, headers, body });
    ctx.response.status = res.status;

    const respCT = res.headers.get("Content-Type");
    if (respCT) ctx.response.headers.set("Content-Type", respCT);
    const disp = res.headers.get("Content-Disposition");
    if (disp) ctx.response.headers.set("Content-Disposition", disp);

    if (respCT && !respCT.includes("application/json")) {
      ctx.response.body = res.body;
    } else {
      try { ctx.response.body = await res.json(); } catch { ctx.response.body = await res.text(); }
    }
  } catch (e) {
    console.error("Proxy error:", e);
    ctx.response.status = 502;
    ctx.response.body = { error: "Erreur communication Spring" };
  }
});

console.log(`Auth server running on http://localhost:${PORT}`);
await app.listen({ port: PORT });