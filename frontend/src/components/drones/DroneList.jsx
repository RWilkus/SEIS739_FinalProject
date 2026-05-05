import React from 'react';

const DronesList = ({ drones }) => {
  if (!drones || drones.length === 0) {
    return <div className="empty-state">No drones found</div>;
  }

  return (
    <div className="table-container">
      <table className="data-table">
        <thead>
          <tr>
            <th>Serial Number</th>
            <th>Model</th>
            <th>Manufacturer</th>
            <th>Status</th>
            <th>Battery</th>
          </tr>
        </thead>
        <tbody>
          {drones.map(drone => (
            <tr key={drone.id}>
              <td>{drone.serialNumber}</td>
              <td>{drone.model}</td>
              <td>{drone.manufacturer}</td>
              <td>{drone.status}</td>
              <td>{drone.batteryLevel}%</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DronesList;