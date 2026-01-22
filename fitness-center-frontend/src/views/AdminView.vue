<template>
  <div class="min-h-screen bg-zinc-950">
    <header class="page-header">
      <div class="page-header-content">
        <div class="header-title">
          <h1 class="header-user-name">{{ authStore.user?.name }}</h1>
          <p class="header-user-role">Администратор</p>
        </div>
        <button class="logout-btn" @click="handleLogout">
          <LogOut :size="20" />
          <span class="hidden sm:inline">Выйти</span>
        </button>
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

      <ClientsTab v-if="activeTab === 'clients'" />
      <TrainersTab v-if="activeTab === 'trainers'" />
      <MembershipsTab v-if="activeTab === 'memberships'" />
      <AdminScheduleTab v-if="activeTab === 'schedule'" />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { LogOut, Users, CreditCard, Calendar, UserPlus } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import ClientsTab from '@/components/admin/ClientsTab.vue'
import MembershipsTab from '@/components/admin/MembershipsTab.vue'
import AdminScheduleTab from '@/components/admin/AdminScheduleTab.vue'
import TrainersTab from '@/components/admin/TrainersTab.vue'
import { clientsApi } from '@/api/client'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('clients')
const clients = ref([])

const tabs = [
  { id: 'clients', label: 'Клиенты', icon: Users },
  { id: 'trainers', label: 'Тренеры', icon: UserPlus },
  { id: 'memberships', label: 'Абонементы', icon: CreditCard },
  { id: 'schedule', label: 'Расписание', icon: Calendar }
]

const stats = computed(() => {
  const activeClients = clients.value.filter(c => c.status === 'active').length
  return {
    totalClients: clients.value.length,
    activeClients,
    totalRevenue: 156000,
    avgAttendance: 78
  }
})

const handleLogout = async () => {
  await authStore.logout()
  await router.push('/')
}

const loadClients = async () => {
  try {
    clients.value = await clientsApi.getAll()
  } catch (error) {
    console.error('Load clients error:', error)
    clients.value = []
  }
}

onMounted(() => {
  if (!authStore.isAuthenticated || authStore.userRole !== 'admin') {
    router.push('/')
  }
  loadClients()
})
</script>
