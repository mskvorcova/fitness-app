<template>
  <div>
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem;">
      <h2 class="text-2xl font-bold text-white">Типы абонементов</h2>
      <button
        @click="showModal = true"
        class="action-btn action-btn-primary"
      >
        <Plus :size="20" />
        Создать абонемент
      </button>
    </div>

    <div class="membership-grid">
      <div
        v-for="membership in memberships"
        :key="membership.id"
        class="membership-type-card"
      >
        <div class="membership-type-header">
          <h3 class="membership-type-name">{{ membership.name }}</h3>
          <div class="membership-type-actions">
            <button
              @click="editMembership(membership)"
              class="icon-btn"
            >
              <Edit2 :size="16" />
            </button>
            <button
              @click="deleteMembership(membership.id)"
              class="icon-btn icon-btn-danger"
            >
              <Trash2 :size="16" />
            </button>
          </div>
        </div>
        <div class="membership-type-details">
          <div class="membership-type-row">
            <span class="membership-type-label">Срок действия</span>
            <span class="membership-type-value">{{ membership.durationDays }} дней</span>
          </div>
          <div v-if="membership.description" class="membership-type-row">
            <span class="membership-type-label">Описание</span>
            <span class="membership-type-value">{{ membership.description }}</span>
          </div>
          <div class="membership-type-divider">
            <div class="membership-type-price">
              <span class="membership-type-price-label">Стоимость</span>
              <span class="membership-type-price-value">{{ membership.price.toLocaleString() }} ₽</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <MembershipModal
      v-if="showModal"
      :membership="selectedMembership"
      @close="showModal = false"
      @save="handleSave"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit2, Trash2 } from 'lucide-vue-next'
import { membershipTypesApi } from '@/api/client'
import MembershipModal from './MembershipModal.vue'

const memberships = ref([])
const showModal = ref(false)
const selectedMembership = ref(null)

const loadMemberships = async () => {
  try {
    memberships.value = await membershipTypesApi.getAll()
  } catch (error) {
    console.error('Load membership types error:', error)
    memberships.value = []
  }
}

const editMembership = (membership) => {
  selectedMembership.value = membership
  showModal.value = true
}

const deleteMembership = async (id) => {
  if (!confirm('Удалить этот тип абонемента?')) return
  try {
    await membershipTypesApi.delete(id)
    await loadMemberships()
  } catch (error) {
    console.error('Delete error:', error)
  }
}

const handleSave = async () => {
  await loadMemberships()
  showModal.value = false
  selectedMembership.value = null
}

onMounted(() => {
  loadMemberships()
})
</script>
