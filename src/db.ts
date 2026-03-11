import { Client } from "mysql";

const client = new Client();

await client.connect({
  hostname:  "127.0.0.1",
  port:     3306,
  db:       "kanban_db",  // <-- ici
  username:  "root",
  password: "root",     // <-- mot de passe vide si root n'en a pas
});

console.log("MySQL connecté ✅");

export async function findUserByEmail(email: string) {
  const result = await client.query(
    "SELECT id, nom, prenom, email, mot_de_passe, role FROM utilisateur WHERE email = ?",
    [email]
  );
  return result[0] ?? null;
}