// Génère une paire de clés RSA au démarrage du serveur

let keyPair: CryptoKeyPair;
let publicKeyJwk: JsonWebKey;

export async function initKeys(): Promise<void> {
  keyPair = await crypto.subtle.generateKey(
    {
      name: "RSASSA-PKCS1-v1_5",
      modulusLength: 2048,
      publicExponent: new Uint8Array([1, 0, 1]),
      hash: "SHA-256",
    },
    true, // extractable = true pour pouvoir exporter en JWK
    ["sign", "verify"]
  );

  // Exporte la clé publique en JWK pour l'endpoint /jwks.json
  publicKeyJwk = await crypto.subtle.exportKey("jwk", keyPair.publicKey);
  console.log("RSA key pair generated ");
}

export function getPrivateKey(): CryptoKey {
  return keyPair.privateKey;
}

export function getPublicKeyJwk(): JsonWebKey {
  return publicKeyJwk;
}