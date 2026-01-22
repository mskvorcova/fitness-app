<template>
  <div>
    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 1.5rem;">
      <h2 class="text-2xl font-bold text-white">Управление тренерами</h2>
      <button
        @click="showTrainerModal = true"
        class="action-btn action-btn-primary"
      >
        <Plus :size="20" />
        Добавить тренера
      </button>
    </div>

    <div style="margin-bottom: 1.5rem;">
      <input
        v-model="searchQuery"
        @input="handleSearch"
        type="text"
        placeholder="Поиск по имени или email..."
        class="form-input"
      />
    </div>

    <div class="table-wrapper">
      <div style="overflow-x: auto;">
        <table class="table">
          <thead>
            <tr>
              <th>Имя</th>
              <th>Email</th>
              <th>Описание</th>
              <th>Стаж (лет)</th>
              <th>Действия</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="trainer in filteredTrainers"
              :key="trainer.id"
            >
              <td class="text-white">{{ trainer.name }}</td>
              <td class="text-muted">{{ trainer.email }}</td>
              <td class="text-muted">{{ trainer.description || '-' }}</td>
              <td class="text-muted">{{ trainer.experienceYears || '-' }}</td>
              <td>
                <div class="flex gap-2" @click.stop>
                  <button
                    @click="editTrainer(trainer)"
                    class="icon-btn"
                  >
                    <Edit2 :size="16" />
                  </button>
                  <button
                    @click="deleteTrainer(trainer.id)"
                    class="icon-btn icon-btn-danger"
                  >
                    <Trash2 :size="16" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="filteredTrainers.length === 0" class="empty-state">
      Нет тренеров
    </div>

    <TrainerModal
      v-if="showTrainerModal"
      :trainer="selectedTrainer"
      @close="showTrainerModal = false"
      @save="handleSaveTrainer"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus, Edit2, Trash2 } from 'lucide-vue-next'
import { trainersApi, usersApi } from '@/api/client'
import TrainerModal from './TrainerModal.vue'

const trainers = ref([])
const searchQuery = ref('')
const showTrainerModal = ref(false)
const selectedTrainer = ref(null)

const filteredTrainers = computed(() => {
  if (!searchQuery.value) return trainers.value
  const query = searchQuery.value.toLowerCase()
  return trainers.value.filter(
    t =>
      (t.name && t.name.toLowerCase().includes(query)) ||
      (t.email && t.email.toLowerCase().includes(query)) ||
      (t.username && t.username.toLowerCase().includes(query))
  )
})

const loadTrainers = async () => {
  try {
    trainers.value = await trainersApi.getAll()
  } catch (error) {
    console.error('Load trainers error:', error)
    trainers.value = []
  }
}

const handleSearch = () => {
}

const editTrainer = (trainer) => {
  selectedTrainer.value = trainer
  showTrainerModal.value = true
}

const deleteTrainer = async (id) => {
  if (!confirm('Вы уверены, что хотите удалить этого тренера?')) return
  try {
    await trainersApi.delete(id)
    await loadTrainers()
  } catch (error) {
    console.error('Delete error:', error)
    alert('Ошибка удаления тренера')
  }
}

const handleSaveTrainer = async () => {
  await loadTrainers()
  showTrainerModal.value = false
  selectedTrainer.value = null
}

onMounted(() => {
  loadTrainers()
})
</script>
