const baseURL = '/api'

async function request(url, options = {}) {
  const headers = {
    'Content-Type': 'application/json'
  }
  
  const config = {
    method: options.method || 'GET',
    headers: headers,
    body: options.body,
    credentials: 'include'
  }
  
  try {
    const response = await fetch(`${baseURL}${url}`, config)
    
    if (response.status === 401) {
      localStorage.removeItem('user')
      window.location.href = '/'
      throw new Error('Unauthorized')
    }
    
    if (!response.ok) {
      const errorText = await response.text()
      let errorMessage = `HTTP error! status: ${response.status}`
      try {
        const errorData = JSON.parse(errorText)
        errorMessage = errorData.message || errorMessage
      } catch {
        if (errorText) {
          errorMessage = errorText
        }
      }
      const error = new Error(errorMessage)
      error.response = { status: response.status, data: { message: errorMessage } }
      throw error
    }
    
    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
      const data = await response.json()
      return { data }
    }
    
    return { data: null }
  } catch (error) {
    throw error
  }
}

export const authApi = {
  login: async (username, password) => {
    const response = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
    
    return response.data
  },
  logout: async () => {
    await request('/auth/logout', {
      method: 'POST'
    })
    localStorage.removeItem('user')
  }
}

export const membershipsApi = {
  getByClientId: async (clientId) => {
    const response = await request(`/clients/${clientId}/membership`, {
      method: 'GET'
    })
    return response.data
  },
  create: async (clientId, membership) => {
    const response = await request(`/clients/${clientId}/membership`, {
      method: 'POST',
      body: JSON.stringify(membership)
    })
    return response.data
  },
  update: async (clientId, membershipId, membership) => {
    const response = await request(`/clients/${clientId}/membership/${membershipId}`, {
      method: 'PUT',
      body: JSON.stringify(membership)
    })
    return response.data
  }
}

export const workoutTypesApi = {
  getAll: async () => {
    const response = await request('/workout-types', {
      method: 'GET'
    })
    return response.data
  }
}

export const membershipTypesApi = {
  getAll: async () => {
    const response = await request('/membership-types', {
      method: 'GET'
    })
    return response.data
  },
  create: async (membershipType) => {
    const response = await request('/membership-types', {
      method: 'POST',
      body: JSON.stringify(membershipType)
    })
    return response.data
  },
  update: async (id, membershipType) => {
    const response = await request(`/membership-types/${id}`, {
      method: 'PUT',
      body: JSON.stringify(membershipType)
    })
    return response.data
  },
  delete: async (id) => {
    await request(`/membership-types/${id}`, {
      method: 'DELETE'
    })
  }
}

export const scheduleApi = {
  getSchedule: async (days) => {
    const url = days ? `/schedule?days=${days}` : '/schedule'
    const response = await request(url, {
      method: 'GET'
    })
    return response.data
  },
  createWorkout: async (workout) => {
    const response = await request('/schedule', {
      method: 'POST',
      body: JSON.stringify(workout)
    })
    return response.data
  },
  updateWorkout: async (id, workout) => {
    const response = await request(`/schedule/${id}`, {
      method: 'PUT',
      body: JSON.stringify(workout)
    })
    return response.data
  },
  deleteWorkout: async (id) => {
    await request(`/schedule/${id}`, {
      method: 'DELETE'
    })
  },
  getTrainerSchedule: async (trainerId, days) => {
    const url = days ? `/schedule/trainer/${trainerId}?days=${days}` : `/schedule/trainer/${trainerId}`
    const response = await request(url, {
      method: 'GET'
    })
    return response.data
  }
}

export const bookingsApi = {
  book: async (scheduleWorkoutId, workoutDate) => {
    const response = await request('/bookings', {
      method: 'POST',
      body: JSON.stringify({ 
        scheduleWorkoutId: scheduleWorkoutId,
        workoutDate: workoutDate
      })
    })
    return response.data
  },
  cancel: async (bookingId) => {
    await request(`/bookings/${bookingId}`, {
      method: 'DELETE'
    })
  },
  markAttendance: async (bookingId) => {
    const response = await request(`/bookings/${bookingId}/mark-attendance`, {
      method: 'POST'
    })
    return response.data
  },
  getByClientId: async (clientId) => {
    const response = await request(`/clients/${clientId}/bookings`, {
      method: 'GET'
    })
    return response.data
  }
}

export const visitsApi = {
  getByClientId: async (clientId) => {
    const response = await request(`/clients/${clientId}/visits`, {
      method: 'GET'
    })
    return response.data
  }
}

export const scheduleManagementApi = {
  verifyPassword: async (password) => {
    const response = await request('/schedule/verify-password', {
      method: 'POST',
      body: JSON.stringify({ password })
    })
    return response.data.valid
  }
}

export const usersApi = {
  getCurrentUser: async () => {
    const response = await request('/users/me', {
      method: 'GET'
    })
    return response.data
  },
  updateCurrentUser: async (userData) => {
    const response = await request('/users/me', {
      method: 'PUT',
      body: JSON.stringify(userData)
    })
    return response.data
  },
  createClient: async (clientData) => {
    const response = await request('/users/clients', {
      method: 'POST',
      body: JSON.stringify(clientData)
    })
    return response.data
  },
  createTrainer: async (trainerData) => {
    const response = await request('/users/trainers', {
      method: 'POST',
      body: JSON.stringify(trainerData)
    })
    return response.data
  }
}

export const trainersApi = {
  getAll: async () => {
    const response = await request('/trainers', {
      method: 'GET'
    })
    return response.data
  },
  getByUserId: async (userId) => {
    const response = await request(`/trainers/user/${userId}`, {
      method: 'GET'
    })
    return response.data
  },
  updateByUserId: async (userId, trainerData) => {
    const response = await request(`/trainers/user/${userId}`, {
      method: 'PUT',
      body: JSON.stringify(trainerData)
    })
    return response.data
  },
  delete: async (id) => {
    await request(`/trainers/${id}`, {
      method: 'DELETE'
    })
  }
}

export const clientsApi = {
  getAll: async () => {
    const response = await request('/clients', {
      method: 'GET'
    })
    return response.data
  },
  getByUserId: async (userId) => {
    const response = await request(`/clients/user/${userId}`, {
      method: 'GET'
    })
    return response.data
  },
  update: async (id, client) => {
    const response = await request(`/clients/${id}`, {
      method: 'PUT',
      body: JSON.stringify(client)
    })
    return response.data
  },
  updateByUserId: async (userId, client) => {
    const response = await request(`/clients/user/${userId}`, {
      method: 'PUT',
      body: JSON.stringify(client)
    })
    return response.data
  },
  delete: async (id) => {
    await request(`/clients/${id}`, {
      method: 'DELETE'
    })
  }
}
