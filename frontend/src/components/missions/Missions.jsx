import React, { useState, useEffect } from 'react';
import './Missions.css';

const Missions = () => {
  const [missions, setMissions] = useState([]);
  const [drones, setDrones] = useState([]);
  const [pilots, setPilots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: 'SURVEILLANCE',
    scheduledStartTime: '',
    scheduledEndTime: '',
    location: '',
    droneId: '',
    pilotId: ''
  });

  const API_BASE_URL = 'http://localhost:8080/api';

  // Mission types from backend enum
  const missionTypes = [
    'SURVEILLANCE',
    'DELIVERY',
    'INSPECTION',
    'MAPPING',
    'SEARCH_AND_RESCUE',
    'TRAINING',
    'PHOTOGRAPHY',
    'AGRICULTURE'
  ];

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [missionsRes, dronesRes, pilotsRes] = await Promise.all([
        fetch(`${API_BASE_URL}/missions`),
        fetch(`${API_BASE_URL}/drones`),
        fetch(`${API_BASE_URL}/pilots`)
      ]);

      if (!missionsRes.ok || !dronesRes.ok || !pilotsRes.ok) {
        throw new Error('Failed to fetch data');
      }

      const missionsData = await missionsRes.json();
      const dronesData = await dronesRes.json();
      const pilotsData = await pilotsRes.json();

      setMissions(missionsData);
      setDrones(dronesData);
      setPilots(pilotsData);
    } catch (error) {
      console.error('Error fetching data:', error);
      setError('Failed to load data. Make sure the backend is running.');
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
      // Create the mission first
      const missionPayload = {
        name: formData.name,
        description: formData.description,
        type: formData.type,
        scheduledStartTime: formData.scheduledStartTime,
        scheduledEndTime: formData.scheduledEndTime,
        location: formData.location
      };

      const response = await fetch(`${API_BASE_URL}/missions`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(missionPayload)
      });

      if (!response.ok) {
        throw new Error('Failed to create mission');
      }

      const newMission = await response.json();

      // Assign drone if selected
      if (formData.droneId) {
        await fetch(`${API_BASE_URL}/missions/${newMission.id}/assign-drone/${formData.droneId}`, {
          method: 'POST'
        });
      }

      // Assign pilot if selected
      if (formData.pilotId) {
        await fetch(`${API_BASE_URL}/missions/${newMission.id}/assign-pilot/${formData.pilotId}`, {
          method: 'POST'
        });
      }

      // Refresh missions list
      await fetchAllData();

      // Reset form
      setFormData({
        name: '',
        description: '',
        type: 'SURVEILLANCE',
        scheduledStartTime: '',
        scheduledEndTime: '',
        location: '',
        droneId: '',
        pilotId: ''
      });
      setShowForm(false);
      alert('✅ Mission created successfully!');

    } catch (error) {
      console.error('Error creating mission:', error);
      alert('❌ Error creating mission. Check console for details.');
    }
  };

  const handleStartMission = async (id) => {
    if (!window.confirm('Start this mission now?')) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/missions/${id}/start`, {
        method: 'POST'
      });

      if (response.ok) {
        await fetchAllData();
        alert('✅ Mission started!');
      } else {
        alert('❌ Error starting mission');
      }
    } catch (error) {
      console.error('Error starting mission:', error);
      alert('❌ Error starting mission');
    }
  };

  const handleCompleteMission = async (id) => {
    if (!window.confirm('Mark this mission as completed?')) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/missions/${id}/complete`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ notes: 'Mission completed successfully' })
      });

      if (response.ok) {
        await fetchAllData();
        alert('✅ Mission completed!');
      } else {
        alert('❌ Error completing mission');
      }
    } catch (error) {
      console.error('Error completing mission:', error);
      alert('❌ Error completing mission');
    }
  };

  const handleDeleteMission = async (id) => {
    if (!window.confirm('Are you sure you want to delete this mission?')) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/missions/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        setMissions(missions.filter(mission => mission.id !== id));
        alert('✅ Mission deleted!');
      } else {
        alert('❌ Error deleting mission');
      }
    } catch (error) {
      console.error('Error deleting mission:', error);
      alert('❌ Error deleting mission');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading missions...</p>
      </div>
    );
  }

  return (
    <div className="missions-page">
      <div className="section-header">
        <h2>📋 Missions</h2>
        <button 
          className="btn-primary" 
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ Create Mission'}
        </button>
      </div>

      {error && (
        <div className="error-banner">
          <strong>⚠️ Error:</strong> {error}
          <button onClick={fetchAllData} className="btn-retry">Retry</button>
        </div>
      )}

      {showForm && (
        <div className="form-card">
          <h3>Create New Mission</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Mission Name *</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                  placeholder="e.g., Downtown Surveillance"
                />
              </div>
              <div className="form-group">
                <label>Mission Type *</label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleInputChange}
                  required
                >
                  {missionTypes.map(type => (
                    <option key={type} value={type}>
                      {type.replace(/_/g, ' ')}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Brief description of the mission objectives..."
                rows="3"
              />
            </div>

            <div className="form-group">
              <label>Location *</label>
              <input
                type="text"
                name="location"
                value={formData.location}
                onChange={handleInputChange}
                required
                placeholder="e.g., Downtown Minneapolis, 44.9778,-93.2650"
              />
              <small className="form-hint">
                Enter location name or coordinates (lat,lng)
              </small>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Scheduled Start *</label>
                <input
                  type="datetime-local"
                  name="scheduledStartTime"
                  value={formData.scheduledStartTime}
                  onChange={handleInputChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Scheduled End</label>
                <input
                  type="datetime-local"
                  name="scheduledEndTime"
                  value={formData.scheduledEndTime}
                  onChange={handleInputChange}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Assign Drone</label>
                <select
                  name="droneId"
                  value={formData.droneId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Select Drone (Optional) --</option>
                  {drones
                    .filter(d => d.status === 'AVAILABLE')
                    .map(drone => (
                      <option key={drone.id} value={drone.id}>
                        {drone.serialNumber} - {drone.model} ({drone.batteryLevel}% battery)
                      </option>
                    ))}
                </select>
              </div>
              <div className="form-group">
                <label>Assign Pilot</label>
                <select
                  name="pilotId"
                  value={formData.pilotId}
                  onChange={handleInputChange}
                >
                  <option value="">-- Select Pilot (Optional) --</option>
                  {pilots
                    .filter(p => p.status === 'ACTIVE')
                    .map(pilot => (
                      <option key={pilot.id} value={pilot.id}>
                        {pilot.firstName} {pilot.lastName} ({pilot.totalFlightHours || 0}h experience)
                      </option>
                    ))}
                </select>
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-primary">
                🚀 Create Mission
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

      {missions.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">📋</div>
          <h3>No Missions Yet</h3>
          <p>Click "Create Mission" to schedule your first mission!</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Type</th>
                <th>Status</th>
                <th>Location</th>
                <th>Drone</th>
                <th>Pilot</th>
                <th>Scheduled Start</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {missions.map(mission => (
                <tr key={mission.id}>
                  <td>
                    <strong>{mission.name}</strong>
                    {mission.description && (
                      <div className="mission-description">
                        {mission.description.substring(0, 50)}
                        {mission.description.length > 50 && '...'}
                      </div>
                    )}
                  </td>
                  <td>
                    <span className="mission-type">
                      {mission.type?.replace(/_/g, ' ')}
                    </span>
                  </td>
                  <td>
                    <span className={`status-badge status-${mission.status?.toLowerCase()}`}>
                      {mission.status}
                    </span>
                  </td>
                  <td>{mission.location || 'N/A'}</td>
                  <td>{mission.droneSerialNumber || '—'}</td>
                  <td>{mission.pilotName || '—'}</td>
                  <td>
                    {mission.scheduledStartTime 
                      ? new Date(mission.scheduledStartTime).toLocaleString() 
                      : 'N/A'}
                  </td>
                  <td>
                    <div className="action-buttons">
                      {mission.status === 'PLANNED' && (
                        <button 
                          className="btn-action btn-start"
                          onClick={() => handleStartMission(mission.id)}
                        >
                          ▶️ Start
                        </button>
                      )}
                      {mission.status === 'IN_PROGRESS' && (
                        <button 
                          className="btn-action btn-complete"
                          onClick={() => handleCompleteMission(mission.id)}
                        >
                          ✅ Complete
                        </button>
                      )}
                      <button 
                        className="btn-action btn-delete"
                        onClick={() => handleDeleteMission(mission.id)}
                      >
                        🗑️
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="missions-stats">
        <div className="stat-item">
          <span className="stat-label">Total Missions:</span>
          <span className="stat-value">{missions.length}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">In Progress:</span>
          <span className="stat-value">
            {missions.filter(m => m.status === 'IN_PROGRESS').length}
          </span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Completed:</span>
          <span className="stat-value">
            {missions.filter(m => m.status === 'COMPLETED').length}
          </span>
        </div>
      </div>
    </div>
  );
};

export default Missions;
