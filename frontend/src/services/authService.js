export const authService = {
  async login(email, motDePasse) { //mot_de_passe
    // Appel direct vers Oak 
    const response = await fetch('http://localhost:3000/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, motDePasse }) //mot_de_passe
    })
    if (!response.ok) throw new Error('Identifiants incorrects')
    return response.json()
  }
}