import api from './api.js'

export const fichierService = {
  async download(pj) {
    const res = await api.get(pj.urlTelechargement, { responseType: 'blob' })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const a = document.createElement('a')
    a.href = url
    a.setAttribute('download', pj.nomFichier)
    document.body.appendChild(a)
    a.click()
    a.remove()
    window.URL.revokeObjectURL(url)
  },
}