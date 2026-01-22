<template>
  <div class="min-h-screen bg-zinc-950">
    <header class="page-header">
      <div class="page-header-content">
        <div class="header-title">
          <h1 class="header-user-name">{{ authStore.user?.name }}</h1>
          <p class="header-user-role">Тренер</p>
        </div>
        <div style="display: flex; gap: 0.5rem;">
          <button class="logout-btn" @click="$router.push('/profile')">
            <Settings :size="20" />
            <span class="hidden sm:inline">Профиль</span>
          </button>
          <button class="logout-btn" @click="handleLogout">
            <LogOut :size="20" />
            <span class="hidden sm:inline">Выйти</span>
          </button>
        </div>
      </div>
    </header>

    <main class="page-main">
      <TrainerScheduleTab
        v-if="authStore.user"
        :userId="authStore.user.id"
      />
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { LogOut, Settings } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import TrainerScheduleTab from '@/components/trainer/TrainerScheduleTab.vue'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = async () => {
  await authStore.logout()
  router.push('/')
}

onMounted(() => {
  if (!authStore.isAuthenticated || (authStore.userRole !== 'trainer' && authStore.userRole !== 'TRAINER')) {
    router.push('/')
  }
})
</script>
