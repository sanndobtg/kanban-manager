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
  methods: ["GET", "POST", "PUT", "DELETE"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));

// Routes
app.use(router.routes());
app.use(router.allowedMethods());

console.log(`Auth server running on http://localhost:${PORT}`);
await app.listen({ port: PORT });