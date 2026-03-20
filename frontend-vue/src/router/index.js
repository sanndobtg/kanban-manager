import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.js";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import TableauxView from "../views/TableauxView.vue";
import TableauDetailView from "../views/TableauDetailView.vue";
import UtilisateursView from "../views/UtilisateursView.vue";
import ProfilView from "../views/ProfilView.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", component: LoginView, meta: { public: true } },
  { path: "/register", component: RegisterView, meta: { public: true } },
  { path: "/tableaux", component: TableauxView, meta: { requiresAuth: true } },
  {
    path: "/tableaux/:id",
    component: TableauDetailView,
    meta: { requiresAuth: true },
  },
  {
    path: "/utilisateurs",
    component: UtilisateursView,
    meta: { requiresAuth: true },
  },
  { path: "/profil", component: ProfilView, meta: { requiresAuth: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Guard global — redirige vers /login si pas connecté
router.beforeEach((to) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return "/login";
  }

  if (to.path === "/utilisateurs" && !authStore.isAdmin) {
    return "/tableaux"; // Redirige les non-admins vers les tableaux
  }
});

export default router;
