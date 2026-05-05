import React, { useState, useEffect } from 'react';
import './Pilots.css';

const Pilots = () => {
  const [pilots, setPilots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    licenseNumber: ''
  });

  const API_BASE_URL = 'http://localhost:8080/api';

  useEffect(() => {
    fetchPilots();
  }, []);

  const fetchPilots = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await fetch(`${API_BASE_URL}/pilots`);
      
      if (!response.ok) {
        throw new Error('Failed to fetch pilots');
      }
      
      const data = await response.json();
      setPilots(data);
    } catch (error) {
      console.error('Error fetching pilots:', error);
      setError('Failed to load pilots. Make sure the backend is running.');
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
      const response = await fetch(`${API_BASE_URL}/pilots`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const newPilot = await response.json();
        setPilots([...pilots, newPilot]);
        setFormData({ firstName: '', lastName: '', email: '', licenseNumber: '' });
        setShowForm(false);
        alert('✅ Pilot added successfully!');
      } else {
        const errorData = await response.json();
        alert('❌ Error: ' + (errorData.message || 'Failed to add pilot'));
      }
    } catch (error) {
      console.error('Error adding pilot:', error);
      alert('❌ Error adding pilot. Check console for details.');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this pilot?')) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/pilots/${id}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        setPilots(pilots.filter(pilot => pilot.id !== id));
        alert('✅ Pilot deleted successfully!');
      } else {
        alert('❌ Error deleting pilot');
      }
    } catch (error) {
      console.error('Error deleting pilot:', error);
      alert('❌ Error deleting pilot');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading pilots...</p>
      </div>
    );
  }

  return (
    <div className="pilots-page">
      <div className="section-header">
        <h2>👨‍✈️ Pilots</h2>
        <button 
          className="btn-primary" 
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? '✕ Cancel' : '+ Add Pilot'}
        </button>
      </div>

      {error && (
        <div className="error-banner">
          <strong>⚠️ Error:</strong> {error}
          <button onClick={fetchPilots} className="btn-retry">Retry</button>
        </div>
      )}

      {showForm && (
        <div className="form-card">
          <h3>Add New Pilot</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>First Name *</label>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleInputChange}
                  required
                  placeholder="John"
                />
              </div>
              <div className="form-group">
                <label>Last Name *</label>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleInputChange}
                  required
                  placeholder="Smith"
                />
              </div>
            </div>
            <div className="form-group">
              <label>Email *</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                required
                placeholder="john.smith@example.com"
              />
            </div>
            <div className="form-group">
              <label>License Number *</label>
              <input
                type="text"
                name="licenseNumber"
                value={formData.licenseNumber}
                onChange={handleInputChange}
                required
                placeholder="PIL-12345"
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn-primary">
                💾 Save Pilot
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

      {pilots.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icon">👨‍✈️</div>
          <h3>No Pilots Yet</h3>
          <p>Click "Add Pilot" to register your first pilot!</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>License Number</th>
                <th>Status</th>
                <th>Flight Hours</th>
                <th>Certification Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pilots.map(pilot => (
                <tr key={pilot.id}>
                  <td className="pilot-name">
                    <strong>{pilot.firstName} {pilot.lastName}</strong>
                  </td>
                  <td>{pilot.email}</td>
                  <td>
                    <code className="license-code">{pilot.licenseNumber}</code>
                  </td>
                  <td>
                    <span className={`status-badge status-${pilot.status?.toLowerCase()}`}>
                      {pilot.status || 'ACTIVE'}
                    </span>
                  </td>
                  <td>{pilot.totalFlightHours || 0}h</td>
                  <td>
                    {pilot.certificationDate 
                      ? new Date(pilot.certificationDate).toLocaleDateString() 
                      : 'N/A'}
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button 
                        className="btn-action btn-view"
                        onClick={() => alert('View details coming soon!')}
                      >
                        👁️ View
                      </button>
                      <button 
                        className="btn-action btn-delete"
                        onClick={() => handleDelete(pilot.id)}
                      >
                        🗑️ Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="pilots-stats">
        <div className="stat-item">
          <span className="stat-label">Total Pilots:</span>
          <span className="stat-value">{pilots.length}</span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Active:</span>
          <span className="stat-value">
            {pilots.filter(p => p.status === 'ACTIVE').length}
          </span>
        </div>
        <div className="stat-item">
          <span className="stat-label">Total Flight Hours:</span>
          <span className="stat-value">
            {pilots.reduce((sum, p) => sum + (p.totalFlightHours || 0), 0)}h
          </span>
        </div>
      </div>
    </div>
  );
};

export default Pilots;
