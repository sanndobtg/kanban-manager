import { Client } from "mysql";

const client = new Client();

await client.connect({
  hostname: "127.0.0.1",
  port: 3306,
  db: "kanban_db",
  username: "root",
  password: "root",
});

console.log("MySQL connecté ");

export async function findUserByEmail(email: string) {
  const result = await client.query(
    "SELECT id, nom, prenom, email, motDePasse, role FROM utilisateur WHERE email = ?",
    [email],
  );
  return result[0] ?? null;
}
