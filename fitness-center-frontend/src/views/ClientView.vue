<template>
  <div class="min-h-screen bg-zinc-950">
    <header class="page-header">
      <div class="page-header-content">
        <div class="header-title">
          <h1 class="header-user-name">{{ authStore.user?.name }}</h1>
          <p class="header-user-role">Клиент</p>
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
      <div class="tabs-container">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="['tab-btn', { active: activeTab === tab.id }]"
        >
          <component :is="tab.icon" :size="20" />
          {{ tab.label }}
        </button>
      </div>

      <ScheduleTab v-if="activeTab === 'schedule'" />
      <BookingsTab v-if="activeTab === 'bookings'" />
      <MembershipTab v-if="activeTab === 'membership'" />
      <VisitsTab v-if="activeTab === 'visits'" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {createRouter as $router, useRouter} from 'vue-router'
import { LogOut, Calendar, CreditCard, CheckCircle, Settings, BookOpen } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import ScheduleTab from '@/components/client/ScheduleTab.vue'
import MembershipTab from '@/components/client/MembershipTab.vue'
import VisitsTab from '@/components/client/VisitsTab.vue'
import BookingsTab from '@/components/client/BookingsTab.vue'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('schedule')

const tabs = [
  { id: 'schedule', label: 'Расписание', icon: Calendar },
  { id: 'bookings', label: 'Записи', icon: BookOpen },
  { id: 'membership', label: 'Абонемент', icon: CreditCard },
  { id: 'visits', label: 'Посещения', icon: CheckCircle }
]

const handleLogout = async () => {
  await authStore.logout()
  router.push('/')
}

onMounted(() => {
  if (!authStore.isAuthenticated || (authStore.userRole !== 'client' && authStore.userRole !== 'CLIENT')) {
    router.push('/')
  }
})
</script>
