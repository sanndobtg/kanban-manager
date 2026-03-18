<template>
  <div class="page">
    <div class="page-header">
      <h1>Mon profil</h1>
    </div>

    <div class="profil-card">
      <div class="profil-avatar">
        {{ authStore.user?.prenom?.[0] }}{{ authStore.user?.nom?.[0] }}
      </div>

      <form @submit.prevent="sauvegarder" class="auth-form">
        <div class="form-row">
          <div class="form-group">
            <label>Nom</label>
            <input v-model="form.nom" type="text" required />
          </div>
          <div class="form-group">
            <label>Prénom</label>
            <input v-model="form.prenom" type="text" required />
          </div>
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" required />
        </div>
        <div class="form-group">
          <label
            >Nouveau mot de passe
            <span class="optional"
              >(laisser vide pour ne pas changer)</span
            ></label
          >
          <input
            v-model="form.motDePasse"
            type="password"
            placeholder="••••••••"
          />
        </div>
        <p v-if="succes" class="success-msg">✅ Profil mis à jour</p>
        <p v-if="erreur" class="error-msg">{{ erreur }}</p>
        <button type="submit" class="btn-primary">Sauvegarder</button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
import { useAuthStore } from "../stores/auth.js";
import { utilisateurService } from "../services/utilisateurService.js";

export default {
  setup() {
    const authStore = useAuthStore();
    const succes = ref(false);
    const erreur = ref("");

    const form = ref({
      nom: authStore.user?.nom || "",
      prenom: authStore.user?.prenom || "",
      email: authStore.user?.email || "",
      motDePasse: "",
      role: authStore.user?.role || "UTILISATEUR",
    });

    async function sauvegarder() {
      try {
        const dto = { ...form.value };
        if (!dto.motDePasse) delete dto.motDePasse;
        await utilisateurService.update(authStore.user.id, dto);
        // Met à jour le user en local
        authStore.user = { ...authStore.user, ...dto };
        localStorage.setItem("user", JSON.stringify(authStore.user));
        succes.value = true;
        erreur.value = "";
        setTimeout(() => (succes.value = false), 3000);
      } catch (e) {
        erreur.value = "Erreur lors de la mise à jour";
      }
    }

    return { form, authStore, succes, erreur, sauvegarder };
  },
};
</script>

<style scoped>
.page {
  padding: 2rem;
  max-width: 600px;
  margin: 0 auto;
}
.page-header {
  margin-bottom: 2rem;
}
.page-header h1 {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1f36;
}

.profil-card {
  background: #fff;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.profil-avatar {
  width: 72px;
  height: 72px;
  background: #1a1f36;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0 auto 2rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}
.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}
.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #4a5568;
}
.form-group input {
  padding: 0.75rem 1rem;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
  outline: none;
}
.form-group input:focus {
  border-color: #4c6ef5;
}
.optional {
  color: #a0aec0;
  font-weight: 400;
  font-size: 0.8rem;
}
.success-msg {
  color: #38a169;
  font-size: 0.85rem;
}
.error-msg {
  color: #e53e3e;
  font-size: 0.85rem;
}
.btn-primary {
  background: #1a1f36;
  color: #fff;
  border: none;
  padding: 0.85rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}
.btn-primary:hover {
  background: #2d3561;
}
</style>
