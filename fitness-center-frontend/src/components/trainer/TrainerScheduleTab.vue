<template>
  <ScheduleByDays v-if="trainerId" :canEdit="false" :trainerId="trainerId" />
  <div v-else class="loading">Загрузка...</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ScheduleByDays from '@/components/shared/ScheduleByDays.vue'
import { trainersApi } from '@/api/client'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const trainerId = ref(null)

const loadTrainerId = async () => {
  try {
    const trainer = await trainersApi.getByUserId(props.userId)
    trainerId.value = trainer.id
  } catch (error) {
    console.error('Load trainer error:', error)
  }
}

onMounted(() => {
  loadTrainerId()
})
</script>
