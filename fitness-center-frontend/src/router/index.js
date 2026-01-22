import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/client',
    name: 'client',
    component: () => import('@/views/ClientView.vue'),
    meta: { requiresAuth: true, role: 'CLIENT' }
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('@/views/AdminView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  },
  {
    path: '/trainer',
    name: 'trainer',
    component: () => import('@/views/TrainerView.vue'),
    meta: { requiresAuth: true, role: 'TRAINER' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  authStore.initAuth()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'login' })
    return
  }

  if (to.meta.role) {
    const userRole = authStore.userRole?.toUpperCase()
    const requiredRole = to.meta.role.toUpperCase()
    
    if (userRole !== requiredRole) {
      if (authStore.isAuthenticated) {
        if (userRole === 'CLIENT') {
          next({ name: 'client' })
        } else if (userRole === 'ADMIN') {
          next({ name: 'admin' })
        } else if (userRole === 'TRAINER') {
          next({ name: 'trainer' })
        } else {
          next({ name: 'login' })
        }
      } else {
        next({ name: 'login' })
      }
      return
    }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    const userRole = authStore.userRole?.toUpperCase()
    if (userRole === 'CLIENT') {
      next({ name: 'client' })
    } else if (userRole === 'ADMIN') {
      next({ name: 'admin' })
    } else if (userRole === 'TRAINER') {
      next({ name: 'trainer' })
    } else {
      next()
    }
    return
  }
  next()
})

export default router
