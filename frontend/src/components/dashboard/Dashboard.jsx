import React, { useState, useEffect } from 'react';
import './Dashboard.css';

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalDrones: 0,
    totalMissions: 0,
    totalPilots: 0,
    availableDrones: 0
  });
  const [drones, setDrones] = useState([]);
  const [loading, setLoading] = useState(true);

  const API_BASE_URL = 'http://localhost:8080/api';

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [dronesRes, missionsRes, pilotsRes] = await Promise.all([
        fetch(`${API_BASE_URL}/drones`),
        fetch(`${API_BASE_URL}/missions`),
        fetch(`${API_BASE_URL}/pilots`)
      ]);

      const dronesData = await dronesRes.json();
      const missions = await missionsRes.json();
      const pilots = await pilotsRes.json();

      setDrones(dronesData);
      setStats({
        totalDrones: dronesData.length,
        totalMissions: missions.length,
        totalPilots: pilots.length,
        availableDrones: dronesData.filter(d => d.status === 'AVAILABLE').length
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  // Get status emoji
  const getStatusEmoji = (status) => {
    const emojiMap = {
      'AVAILABLE': '🟢',
      'IN_FLIGHT': '✈️',
      'MAINTENANCE': '🔧',
      'CHARGING': '🔋',
      'OFFLINE': '⚫',
      'RESERVED': '🟡'
    };
    return emojiMap[status] || '⚪';
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="spinner"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <h2>Dashboard Overview</h2>
      
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">🚁</div>
          <div className="stat-value">{stats.totalDrones}</div>
          <div className="stat-label">Total Drones</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">📋</div>
          <div className="stat-value">{stats.totalMissions}</div>
          <div className="stat-label">Total Missions</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👨‍✈️</div>
          <div className="stat-value">{stats.totalPilots}</div>
          <div className="stat-label">Total Pilots</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-value">{stats.availableDrones}</div>
          <div className="stat-label">Available Drones</div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="map-section">
          <h3>📍 Drone Locations</h3>
          <div className="map-container">
            <div className="map-placeholder">
              <svg viewBox="0 0 800 400" className="map-visual">
                {/* Background */}
                <rect width="800" height="400" fill="#e8f4f8"/>
                
                {/* Grid lines */}
                <g opacity="0.2">
                  {[...Array(10)].map((_, i) => (
                    <line key={`v${i}`} x1={i * 80} y1="0" x2={i * 80} y2="400" stroke="#667eea" strokeWidth="1"/>
                  ))}
                  {[...Array(5)].map((_, i) => (
                    <line key={`h${i}`} x1="0" y1={i * 80} x2="800" y2={i * 80} stroke="#667eea" strokeWidth="1"/>
                  ))}
                </g>

                {/* Map title */}
                <text x="400" y="30" textAnchor="middle" fill="#2c3e50" fontSize="20" fontWeight="bold">
                  Minneapolis Fleet Overview
                </text>

                {/* Drones on map */}
                {drones.map((drone, index) => {
                  // Position drones in a grid pattern for demo
                  // In production, use actual lat/lng coordinates
                  const x = 100 + (index % 4) * 150;
                  const y = 100 + Math.floor(index / 4) * 100;
                  
                  return (
                    <g key={drone.id}>
                      {/* Drone marker */}
                      <circle 
                        cx={x} 
                        cy={y} 
                        r="20" 
                        fill={drone.status === 'AVAILABLE' ? '#28a745' : 
                              drone.status === 'IN_FLIGHT' ? '#007bff' : 
                              drone.status === 'MAINTENANCE' ? '#ffc107' : '#dc3545'}
                        stroke="white"
                        strokeWidth="3"
                      />
                      
                      {/* Drone icon */}
                      <text 
                        x={x} 
                        y={y + 6} 
                        textAnchor="middle" 
                        fill="white" 
                        fontSize="16"
                        fontWeight="bold"
                      >
                        ✈
                      </text>

                      {/* Label */}
                      <rect 
                        x={x - 35} 
                        y={y + 25} 
                        width="70" 
                        height="20" 
                        fill="white" 
                        stroke="#667eea" 
                        strokeWidth="1"
                        rx="3"
                      />
                      <text 
                        x={x} 
                        y={y + 38} 
                        textAnchor="middle" 
                        fill="#2c3e50" 
                        fontSize="10"
                        fontWeight="600"
                      >
                        {drone.serialNumber}
                      </text>

                      {/* Battery indicator */}
                      <text 
                        x={x} 
                        y={y + 60} 
                        textAnchor="middle" 
                        fill="#666" 
                        fontSize="9"
                      >
                        🔋 {drone.batteryLevel || 0}%
                      </text>
                    </g>
                  );
                })}

                {/* Legend */}
                <g transform="translate(20, 320)">
                  <text x="0" y="0" fill="#2c3e50" fontSize="12" fontWeight="bold">Status Legend:</text>
                  
                  <circle cx="10" cy="20" r="6" fill="#28a745"/>
                  <text x="25" y="24" fill="#666" fontSize="11">Available</text>
                  
                  <circle cx="100" cy="20" r="6" fill="#007bff"/>
                  <text x="115" y="24" fill="#666" fontSize="11">In Flight</text>
                  
                  <circle cx="200" cy="20" r="6" fill="#ffc107"/>
                  <text x="215" y="24" fill="#666" fontSize="11">Maintenance</text>
                  
                  <circle cx="320" cy="20" r="6" fill="#dc3545"/>
                  <text x="335" y="24" fill="#666" fontSize="11">Offline</text>
                </g>
              </svg>
            </div>
          </div>
        </div>

        <div className="fleet-status-section">
          <h3>🚁 Fleet Status</h3>
          <div className="fleet-list">
            {drones.length === 0 ? (
              <p className="no-drones">No drones in fleet yet.</p>
            ) : (
              drones.map(drone => (
                <div key={drone.id} className="fleet-item">
                  <div className="fleet-item-icon">
                    {getStatusEmoji(drone.status)}
                  </div>
                  <div className="fleet-item-info">
                    <div className="fleet-item-name">
                      {drone.serialNumber}
                    </div>
                    <div className="fleet-item-model">
                      {drone.model} - {drone.manufacturer}
                    </div>
                  </div>
                  <div className="fleet-item-stats">
                    <div className="fleet-stat">
                      <span className="fleet-stat-label">Battery:</span>
                      <span className="fleet-stat-value">{drone.batteryLevel || 0}%</span>
                    </div>
                    <div className="fleet-stat">
                      <span className="fleet-stat-label">Hours:</span>
                      <span className="fleet-stat-value">{drone.flightHours || 0}h</span>
                    </div>
                  </div>
                  <div className="fleet-item-status">
                    <span className={`status-badge status-${drone.status?.toLowerCase()}`}>
                      {drone.status}
                    </span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
