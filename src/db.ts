import { Client } from "mysql";
import * as bcrypt from "bcrypt";
const client = new Client();

await client.connect({
  hostname: Deno.env.get("DB_HOST") || "localhost",
  port: Number(Deno.env.get("DB_PORT")) || 3306,
  db: Deno.env.get("DB_NAME") || "kanban_db",
  username: Deno.env.get("DB_USER") || root,
  password: "",
});

console.log("MySQL connecté ✅");

export async function findUserByEmail(email: string) {
  const result = await client.query(
    "SELECT id, nom, prenom, email, motDePasse, role FROM utilisateur WHERE email = ?",
    [email],
  );
  return result[0] ?? null;
}

// Vérifie le mot de passe en clair contre le hash BCrypt
export async function verifierMotDePasse(
  motDePasseClair: string,
  hash: string,
): Promise<boolean> {
  return await bcrypt.compare(motDePasseClair, hash);
}
