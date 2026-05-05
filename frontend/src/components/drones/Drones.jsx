import React, { useState, useEffect } from 'react';
import './Drones.css';

const Drones = () => {
  const [drones, setDrones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    serialNumber: '',
    model: '',
    manufacturer: ''
  });

  const API_BASE_URL = 'http://localhost:8080/api';

  useEffect(() => {
    fetchDrones();
  }, []);

  const fetchDrones = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/drones`);
      const data = await response.json();
      setDrones(data);
    } catch (error) {
      console.error('Error fetching drones:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`${API_BASE_URL}/drones`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const newDrone = await response.json();
        setDrones([...drones, newDrone]);
        setFormData({ serialNumber: '', model: '', manufacturer: '' });
        setShowForm(false);
        alert('Drone added successfully!');
      } else {
        alert('Error adding drone');
      }
    } catch (error) {
      console.error('Error adding drone:', error);
      alert('Error adding drone');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading drones...</p>
      </div>
    );
  }

  return (
    <div className="drones-page">
      <div className="section-header">
        <h2>🚁 Drone Fleet</h2>
        <button 
          className="btn-primary" 
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ Add Drone'}
        </button>
      </div>

      {showForm && (
        <div className="form-card">
          <h3>Add New Drone</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Serial Number *</label>
              <input
                type="text"
                name="serialNumber"
                value={formData.serialNumber}
                onChange={handleInputChange}
                required
                placeholder="DRN-001"
              />
            </div>
            <div className="form-group">
              <label>Model *</label>
              <input
                type="text"
                name="model"
                value={formData.model}
                onChange={handleInputChange}
                required
                placeholder="Phantom 4 Pro"
              />
            </div>
            <div className="form-group">
              <label>Manufacturer *</label>
              <input
                type="text"
                name="manufacturer"
                value={formData.manufacturer}
                onChange={handleInputChange}
                required
                placeholder="DJI"
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn-primary">
                Save Drone
              </button>
              <button 
                type="button" 
                className="btn-secondary"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

{/* Temporary inline table until DronesList is fixed */}
{drones.length === 0 ? (
  <div className="empty-state">
    <p>No drones found. Add your first drone!</p>
  </div>
) : (
  <div className="table-container">
    <table className="data-table">
      <thead>
        <tr>
          <th>Serial Number</th>
          <th>Model</th>
          <th>Manufacturer</th>
          <th>Status</th>
          <th>Battery</th>
          <th>Flight Hours</th>
        </tr>
      </thead>
      <tbody>
        {drones.map(drone => (
          <tr key={drone.id}>
            <td>{drone.serialNumber}</td>
            <td>{drone.model}</td>
            <td>{drone.manufacturer}</td>
            <td>
              <span className={`status-badge status-${drone.status?.toLowerCase()}`}>
                {drone.status}
              </span>
            </td>
            <td>{drone.batteryLevel || 0}%</td>
            <td>{drone.flightHours || 0}h</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
)}
    </div>
  );
};

export default Drones;
