import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

// Ajoute automatiquement le token JWT à chaque requête
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Gestion globale des erreurs
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Token expiré → redirection login
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }

    // Affiche un toast d'erreur
    const message =
      !error.response ? 'Serveur indisponible. Vérifiez votre connexion.' :
      error.response.status === 403 ? 'Accès refusé.' :
      error.response.status === 500 ? 'Erreur interne du serveur.' :
      error.response.status === 502 ? 'Le serveur est momentanément indisponible.' :
      null

    if (message) {
      window.dispatchEvent(new CustomEvent('app-error', { detail: message }))
    }

    return Promise.reject(error)
  }
)

export default api