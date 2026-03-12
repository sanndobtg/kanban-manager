export const config = {
  port: Number(Deno.env.get("PORT")) || 3000,
  jwt: {
    issuer: Deno.env.get("JWT_ISSUER") || "http://localhost:3000",
    audience: Deno.env.get("JWT_AUDIENCE") || "http://localhost:8080",
    expirationHours: Number(Deno.env.get("JWT_EXPIRATION_HOURS")) || 1,
  },
};