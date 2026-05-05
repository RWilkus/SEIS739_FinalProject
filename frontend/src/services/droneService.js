// ============================================================================
// 4. API SERVICE EXAMPLE
// ============================================================================

// src/services/droneService.js
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add request interceptor for authentication
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const droneService = {
  getAllDrones: async (params) => {
    const response = await api.get('/drones', { params });
    return response.data;
  },

  getDroneById: async (id) => {
    const response = await api.get(`/drones/${id}`);
    return response.data;
  },

  createDrone: async (data) => {
    const response = await api.post('/drones', data);
    return response.data;
  },

  updateDrone: async (id, data) => {
    const response = await api.put(`/drones/${id}`, data);
    return response.data;
  },

  updateDroneStatus: async (id, status) => {
    const response = await api.patch(`/drones/${id}/status`, null, {
      params: { status }
    });
    return response.data;
  },

  deleteDrone: async (id) => {
    await api.delete(`/drones/${id}`);
  },

  getAvailableDrones: async () => {
    const response = await api.get('/drones/available');
    return response.data;
  },

  getMaintenanceHistory: async (id) => {
    const response = await api.get(`/drones/${id}/maintenance`);
    return response.data;
  },

  getFlightHistory: async (id, params) => {
    const response = await api.get(`/drones/${id}/flights`, { params });
    return response.data;
  }
};

export default droneService;