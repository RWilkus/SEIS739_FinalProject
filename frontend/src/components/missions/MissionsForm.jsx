const MissionsForm = ({ onMissionCreated, onCancel }) => {
  // ... your form code ...
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    // ... create mission ...
    onMissionCreated(newMission); // Call parent
  };

  return (
    <div className="form-card">
      {/* your form JSX */}
    </div>
  );
};

export default MissionsForm;