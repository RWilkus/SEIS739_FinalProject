import React, { useState } from 'react';
import './App.css';

// Import components
import Dashboard from './components/dashboard/Dashboard';
import Drones from './components/drones/Drones';
import Missions from './components/missions/Missions';
import Pilots from './components/pilots/Pilots';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1>✈️ SkyControl Pro</h1>
          <p className="subtitle">Drone Fleet Management System</p>
        </div>
      </header>

      <nav className="nav-tabs">
        <button 
          className={`nav-tab ${activeTab === 'dashboard' ? 'active' : ''}`}
          onClick={() => setActiveTab('dashboard')}
        >
          📊 Dashboard
        </button>
        <button 
          className={`nav-tab ${activeTab === 'drones' ? 'active' : ''}`}
          onClick={() => setActiveTab('drones')}
        >
          🚁 Drones
        </button>
        <button 
          className={`nav-tab ${activeTab === 'missions' ? 'active' : ''}`}
          onClick={() => setActiveTab('missions')}
        >
          📋 Missions
        </button>
        <button 
          className={`nav-tab ${activeTab === 'pilots' ? 'active' : ''}`}
          onClick={() => setActiveTab('pilots')}
        >
          👨‍✈️ Pilots
        </button>
      </nav>

      <main className="main-content">
        {activeTab === 'dashboard' && <Dashboard />}
        {activeTab === 'drones' && <Drones />}
        {activeTab === 'missions' && <Missions />}
        {activeTab === 'pilots' && <Pilots />}
      </main>

      <footer className="app-footer">
        <p>© 2026 SkyControl Pro | SEIS 739 Project</p>
      </footer>
    </div>
  );
}

export default App;
