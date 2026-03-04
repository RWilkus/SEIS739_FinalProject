-- ============================================================================
-- SkyControl Pro - Database Schema
-- PostgreSQL 14+
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- ENUMS
-- ============================================================================

CREATE TYPE drone_status AS ENUM ('available', 'in_flight', 'maintenance', 'charging', 'retired');
CREATE TYPE battery_status AS ENUM ('active', 'degraded', 'retired');
CREATE TYPE mission_status AS ENUM ('requested', 'approved', 'assigned', 'in_progress', 'completed', 'cancelled');
CREATE TYPE mission_type AS ENUM ('inspection', 'survey', 'photography', 'delivery', 'monitoring', 'other');
CREATE TYPE pilot_status AS ENUM ('active', 'inactive', 'suspended');
CREATE TYPE maintenance_type AS ENUM ('scheduled', 'unscheduled', 'repair', 'inspection');
CREATE TYPE maintenance_status AS ENUM ('scheduled', 'in_progress', 'completed', 'cancelled');
CREATE TYPE incident_severity AS ENUM ('low', 'medium', 'high', 'critical');
CREATE TYPE incident_status AS ENUM ('open', 'investigating', 'resolved', 'closed');
CREATE TYPE authorization_status AS ENUM ('pending', 'approved', 'denied', 'expired');

-- ============================================================================
-- TABLE: drones
-- ============================================================================

CREATE TABLE drones (
    drone_id SERIAL PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    serial_number VARCHAR(50) UNIQUE NOT NULL,
    registration_number VARCHAR(50),
    purchase_date DATE NOT NULL,
    max_flight_time_minutes INTEGER NOT NULL CHECK (max_flight_time_minutes > 0),
    max_range_meters INTEGER NOT NULL CHECK (max_range_meters > 0),
    max_altitude_meters INTEGER NOT NULL CHECK (max_altitude_meters > 0),
    payload_capacity_kg NUMERIC(5,2) NOT NULL CHECK (payload_capacity_kg >= 0),
    camera_specs JSONB,
    current_status drone_status NOT NULL DEFAULT 'available',
    total_flight_hours NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total_flight_hours >= 0),
    flight_hours_since_maintenance NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (flight_hours_since_maintenance >= 0),
    last_maintenance_date DATE,
    next_maintenance_due_hours NUMERIC(10,2) DEFAULT 50,
    next_maintenance_due_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_drones_status ON drones(current_status);
CREATE INDEX idx_drones_serial ON drones(serial_number);
CREATE INDEX idx_drones_maintenance_due ON drones(next_maintenance_due_date) WHERE current_status != 'retired';

-- ============================================================================
-- TABLE: batteries
-- ============================================================================

CREATE TABLE batteries (
    battery_id SERIAL PRIMARY KEY,
    drone_id INTEGER REFERENCES drones(drone_id) ON DELETE CASCADE,
    serial_number VARCHAR(50) UNIQUE NOT NULL,
    capacity_mah INTEGER NOT NULL CHECK (capacity_mah > 0),
    voltage NUMERIC(4,1) NOT NULL CHECK (voltage > 0),
    cycle_count INTEGER NOT NULL DEFAULT 0 CHECK (cycle_count >= 0),
    max_cycles INTEGER NOT NULL DEFAULT 300,
    health_percentage INTEGER NOT NULL DEFAULT 100 CHECK (health_percentage BETWEEN 0 AND 100),
    purchase_date DATE NOT NULL,
    last_charged_date TIMESTAMP,
    status battery_status NOT NULL DEFAULT 'active',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_batteries_drone ON batteries(drone_id);
CREATE INDEX idx_batteries_status ON batteries(status);
CREATE INDEX idx_batteries_health ON batteries(health_percentage) WHERE status = 'active';

-- ============================================================================
-- TABLE: pilots
-- ============================================================================

CREATE TABLE pilots (
    pilot_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    part_107_certification_number VARCHAR(50),
    certification_issue_date DATE,
    certification_expiry_date DATE,
    additional_endorsements JSONB,
    total_flight_hours NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total_flight_hours >= 0),
    status pilot_status NOT NULL DEFAULT 'active',
    hire_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pilots_email ON pilots(email);
CREATE INDEX idx_pilots_status ON pilots(status);
CREATE INDEX idx_pilots_cert_expiry ON pilots(certification_expiry_date) WHERE status = 'active';

-- ============================================================================
-- TABLE: missions
-- ============================================================================

CREATE TABLE missions (
    mission_id SERIAL PRIMARY KEY,
    mission_name VARCHAR(200) NOT NULL,
    mission_type mission_type NOT NULL,
    description TEXT,
    requested_by VARCHAR(100) NOT NULL,
    assigned_pilot_id INTEGER REFERENCES pilots(pilot_id) ON DELETE SET NULL,
    assigned_drone_id INTEGER REFERENCES drones(drone_id) ON DELETE SET NULL,
    flight_location VARCHAR(500) NOT NULL,
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
    planned_date DATE NOT NULL,
    planned_start_time TIME,
    estimated_duration_minutes INTEGER CHECK (estimated_duration_minutes > 0),
    actual_start_time TIMESTAMP,
    actual_end_time TIMESTAMP,
    actual_duration_minutes INTEGER,
    status mission_status NOT NULL DEFAULT 'requested',
    weather_conditions JSONB,
    max_altitude_planned INTEGER,
    airspace_authorization_required BOOLEAN DEFAULT false,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    completion_notes TEXT,
    client_name VARCHAR(200),
    cost_estimate NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_actual_times CHECK (actual_end_time IS NULL OR actual_end_time >= actual_start_time)
);

CREATE INDEX idx_missions_status ON missions(status);
CREATE INDEX idx_missions_pilot ON missions(assigned_pilot_id);
CREATE INDEX idx_missions_drone ON missions(assigned_drone_id);
CREATE INDEX idx_missions_date ON missions(planned_date);
CREATE INDEX idx_missions_location ON missions(latitude, longitude);

-- ============================================================================
-- TABLE: flight_logs
-- ============================================================================

CREATE TABLE flight_logs (
    log_id SERIAL PRIMARY KEY,
    mission_id INTEGER REFERENCES missions(mission_id) ON DELETE CASCADE,
    drone_id INTEGER NOT NULL REFERENCES drones(drone_id) ON DELETE CASCADE,
    pilot_id INTEGER NOT NULL REFERENCES pilots(pilot_id) ON DELETE CASCADE,
    battery_id INTEGER REFERENCES batteries(battery_id) ON DELETE SET NULL,
    takeoff_time TIMESTAMP NOT NULL,
    landing_time TIMESTAMP NOT NULL,
    flight_duration_minutes NUMERIC(6,2) NOT NULL CHECK (flight_duration_minutes > 0),
    max_altitude_reached INTEGER CHECK (max_altitude_reached >= 0),
    distance_traveled_meters INTEGER CHECK (distance_traveled_meters >= 0),
    battery_start_percentage INTEGER CHECK (battery_start_percentage BETWEEN 0 AND 100),
    battery_end_percentage INTEGER CHECK (battery_end_percentage BETWEEN 0 AND 100),
    battery_used_percentage INTEGER CHECK (battery_used_percentage BETWEEN 0 AND 100),
    weather_temperature NUMERIC(4,1),
    weather_wind_speed INTEGER,
    anomalies_noted TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_flight_times CHECK (landing_time > takeoff_time),
    CONSTRAINT check_battery_logic CHECK (battery_end_percentage <= battery_start_percentage)
);

CREATE INDEX idx_flight_logs_mission ON flight_logs(mission_id);
CREATE INDEX idx_flight_logs_drone ON flight_logs(drone_id);
CREATE INDEX idx_flight_logs_pilot ON flight_logs(pilot_id);
CREATE INDEX idx_flight_logs_date ON flight_logs(takeoff_time);

-- ============================================================================
-- TABLE: maintenance_records
-- ============================================================================

CREATE TABLE maintenance_records (
    maintenance_id SERIAL PRIMARY KEY,
    drone_id INTEGER NOT NULL REFERENCES drones(drone_id) ON DELETE CASCADE,
    maintenance_type maintenance_type NOT NULL,
    scheduled_date DATE NOT NULL,
    completed_date DATE,
    technician_name VARCHAR(100),
    work_performed TEXT NOT NULL,
    parts_replaced JSONB,
    labor_hours NUMERIC(5,2) CHECK (labor_hours >= 0),
    parts_cost NUMERIC(10,2) CHECK (parts_cost >= 0),
    labor_cost NUMERIC(10,2) CHECK (labor_cost >= 0),
    total_cost NUMERIC(10,2) GENERATED ALWAYS AS (COALESCE(parts_cost, 0) + COALESCE(labor_cost, 0)) STORED,
    next_maintenance_due_hours NUMERIC(10,2),
    next_maintenance_due_date DATE,
    status maintenance_status NOT NULL DEFAULT 'scheduled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_maintenance_drone ON maintenance_records(drone_id);
CREATE INDEX idx_maintenance_status ON maintenance_records(status);
CREATE INDEX idx_maintenance_scheduled ON maintenance_records(scheduled_date);

-- ============================================================================
-- TABLE: pre_flight_inspections
-- ============================================================================

CREATE TABLE pre_flight_inspections (
    inspection_id SERIAL PRIMARY KEY,
    mission_id INTEGER REFERENCES missions(mission_id) ON DELETE CASCADE,
    drone_id INTEGER NOT NULL REFERENCES drones(drone_id) ON DELETE CASCADE,
    pilot_id INTEGER NOT NULL REFERENCES pilots(pilot_id) ON DELETE CASCADE,
    battery_id INTEGER REFERENCES batteries(battery_id) ON DELETE SET NULL,
    inspection_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Inspection items
    propellers_condition BOOLEAN NOT NULL,
    motors_condition BOOLEAN NOT NULL,
    frame_condition BOOLEAN NOT NULL,
    camera_condition BOOLEAN NOT NULL,
    gimbal_condition BOOLEAN NOT NULL,
    battery_condition BOOLEAN NOT NULL,
    battery_charge_level INTEGER CHECK (battery_charge_level BETWEEN 0 AND 100),
    firmware_current BOOLEAN NOT NULL,
    gps_lock_acquired BOOLEAN NOT NULL,
    compass_calibrated BOOLEAN NOT NULL,
    control_link_quality BOOLEAN NOT NULL,
    
    overall_pass BOOLEAN NOT NULL,
    inspector_notes TEXT,
    photos JSONB, -- Array of photo URLs
    signature VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inspections_mission ON pre_flight_inspections(mission_id);
CREATE INDEX idx_inspections_drone ON pre_flight_inspections(drone_id);
CREATE INDEX idx_inspections_pilot ON pre_flight_inspections(pilot_id);
CREATE INDEX idx_inspections_pass ON pre_flight_inspections(overall_pass);
CREATE INDEX idx_inspections_date ON pre_flight_inspections(inspection_datetime);

-- ============================================================================
-- TABLE: incidents
-- ============================================================================

CREATE TABLE incidents (
    incident_id SERIAL PRIMARY KEY,
    mission_id INTEGER REFERENCES missions(mission_id) ON DELETE SET NULL,
    drone_id INTEGER NOT NULL REFERENCES drones(drone_id) ON DELETE CASCADE,
    pilot_id INTEGER REFERENCES pilots(pilot_id) ON DELETE SET NULL,
    incident_datetime TIMESTAMP NOT NULL,
    incident_location VARCHAR(500),
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
    severity incident_severity NOT NULL,
    incident_type VARCHAR(100) NOT NULL, -- crash, flyaway, equipment_failure, near_miss, etc.
    description TEXT NOT NULL,
    damage_assessment TEXT,
    injuries BOOLEAN NOT NULL DEFAULT false,
    injury_details TEXT,
    property_damage BOOLEAN NOT NULL DEFAULT false,
    property_damage_details TEXT,
    estimated_repair_cost NUMERIC(10,2),
    root_cause TEXT,
    contributing_factors TEXT,
    corrective_actions TEXT,
    preventive_measures TEXT,
    status incident_status NOT NULL DEFAULT 'open',
    reported_by VARCHAR(100) NOT NULL,
    investigated_by VARCHAR(100),
    investigation_completed_date DATE,
    photos JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_incidents_drone ON incidents(drone_id);
CREATE INDEX idx_incidents_pilot ON incidents(pilot_id);
CREATE INDEX idx_incidents_severity ON incidents(severity);
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_date ON incidents(incident_datetime);

-- ============================================================================
-- TABLE: parts_inventory
-- ============================================================================

CREATE TABLE parts_inventory (
    part_id SERIAL PRIMARY KEY,
    part_name VARCHAR(200) NOT NULL,
    part_number VARCHAR(100) UNIQUE NOT NULL,
    category VARCHAR(100), -- propeller, motor, battery, camera, etc.
    compatible_models JSONB NOT NULL, -- Array of drone models
    description TEXT,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    minimum_stock_level INTEGER NOT NULL DEFAULT 1 CHECK (minimum_stock_level >= 0),
    reorder_quantity INTEGER DEFAULT 5,
    unit_cost NUMERIC(10,2) CHECK (unit_cost >= 0),
    supplier_name VARCHAR(200),
    supplier_part_number VARCHAR(100),
    lead_time_days INTEGER,
    last_ordered_date DATE,
    last_received_date DATE,
    storage_location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_parts_number ON parts_inventory(part_number);
CREATE INDEX idx_parts_category ON parts_inventory(category);
CREATE INDEX idx_parts_low_stock ON parts_inventory(quantity_on_hand, minimum_stock_level) 
    WHERE quantity_on_hand <= minimum_stock_level;

-- ============================================================================
-- TABLE: airspace_authorizations
-- ============================================================================

CREATE TABLE airspace_authorizations (
    authorization_id SERIAL PRIMARY KEY,
    mission_id INTEGER REFERENCES missions(mission_id) ON DELETE CASCADE,
    authorization_type VARCHAR(50) NOT NULL, -- LAANC, Part 107 waiver, COA, etc.
    authorization_number VARCHAR(100) UNIQUE,
    requesting_pilot_id INTEGER REFERENCES pilots(pilot_id) ON DELETE SET NULL,
    request_date DATE NOT NULL,
    valid_from_date DATE,
    valid_until_date DATE,
    approved_altitude_feet INTEGER,
    area_description TEXT NOT NULL,
    area_boundaries JSONB, -- GeoJSON polygon
    latitude NUMERIC(10,8),
    longitude NUMERIC(11,8),
    radius_meters INTEGER,
    status authorization_status NOT NULL DEFAULT 'pending',
    approval_authority VARCHAR(100),
    approval_date DATE,
    denial_reason TEXT,
    conditions TEXT,
    documents JSONB, -- Array of document URLs
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_valid_dates CHECK (valid_until_date IS NULL OR valid_until_date >= valid_from_date)
);

CREATE INDEX idx_authorizations_mission ON airspace_authorizations(mission_id);
CREATE INDEX idx_authorizations_status ON airspace_authorizations(status);
CREATE INDEX idx_authorizations_dates ON airspace_authorizations(valid_from_date, valid_until_date);
CREATE INDEX idx_authorizations_location ON airspace_authorizations(latitude, longitude);

-- ============================================================================
-- TABLE: users (for authentication/authorization)
-- ============================================================================

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ADMIN, MANAGER, PILOT, TECHNICIAN, VIEWER
    pilot_id INTEGER REFERENCES pilots(pilot_id) ON DELETE SET NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Update timestamp trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply update trigger to all relevant tables
CREATE TRIGGER update_drones_updated_at BEFORE UPDATE ON drones
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_batteries_updated_at BEFORE UPDATE ON batteries
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_pilots_updated_at BEFORE UPDATE ON pilots
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_missions_updated_at BEFORE UPDATE ON missions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_maintenance_updated_at BEFORE UPDATE ON maintenance_records
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_incidents_updated_at BEFORE UPDATE ON incidents
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_parts_updated_at BEFORE UPDATE ON parts_inventory
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_authorizations_updated_at BEFORE UPDATE ON airspace_authorizations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS
-- ============================================================================

-- View: Drones needing maintenance soon
CREATE VIEW drones_maintenance_due AS
SELECT 
    d.drone_id,
    d.model,
    d.serial_number,
    d.current_status,
    d.flight_hours_since_maintenance,
    d.next_maintenance_due_hours,
    d.last_maintenance_date,
    d.next_maintenance_due_date,
    CASE 
        WHEN d.flight_hours_since_maintenance >= d.next_maintenance_due_hours THEN 'OVERDUE'
        WHEN d.flight_hours_since_maintenance >= (d.next_maintenance_due_hours * 0.9) THEN 'DUE_SOON'
        WHEN d.next_maintenance_due_date <= CURRENT_DATE THEN 'OVERDUE'
        WHEN d.next_maintenance_due_date <= CURRENT_DATE + INTERVAL '7 days' THEN 'DUE_SOON'
        ELSE 'OK'
    END AS maintenance_urgency
FROM drones d
WHERE d.current_status != 'retired';

-- View: Active pilots with current certifications
CREATE VIEW active_certified_pilots AS
SELECT 
    p.pilot_id,
    p.first_name,
    p.last_name,
    p.email,
    p.part_107_certification_number,
    p.certification_expiry_date,
    p.total_flight_hours,
    CASE 
        WHEN p.certification_expiry_date < CURRENT_DATE THEN 'EXPIRED'
        WHEN p.certification_expiry_date <= CURRENT_DATE + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'CURRENT'
    END AS certification_status
FROM pilots p
WHERE p.status = 'active';

-- View: Fleet status summary
CREATE VIEW fleet_status_summary AS
SELECT 
    current_status,
    COUNT(*) AS count,
    ROUND(AVG(total_flight_hours), 2) AS avg_flight_hours,
    ROUND(AVG(flight_hours_since_maintenance), 2) AS avg_hours_since_maintenance
FROM drones
WHERE current_status != 'retired'
GROUP BY current_status;

-- View: Battery health summary
CREATE VIEW battery_health_summary AS
SELECT 
    b.battery_id,
    b.serial_number,
    b.drone_id,
    d.model AS drone_model,
    b.cycle_count,
    b.max_cycles,
    b.health_percentage,
    b.status,
    ROUND((b.cycle_count::NUMERIC / b.max_cycles * 100), 2) AS cycle_usage_percentage,
    CASE 
        WHEN b.cycle_count >= b.max_cycles THEN 'REPLACE_NOW'
        WHEN b.cycle_count >= (b.max_cycles * 0.9) THEN 'REPLACE_SOON'
        WHEN b.health_percentage < 70 THEN 'DEGRADED'
        ELSE 'GOOD'
    END AS recommendation
FROM batteries b
LEFT JOIN drones d ON b.drone_id = d.drone_id
WHERE b.status = 'active';

-- View: Upcoming missions
CREATE VIEW upcoming_missions AS
SELECT 
    m.mission_id,
    m.mission_name,
    m.mission_type,
    m.planned_date,
    m.planned_start_time,
    m.status,
    p.first_name || ' ' || p.last_name AS pilot_name,
    d.model AS drone_model,
    d.serial_number AS drone_serial,
    m.flight_location,
    m.estimated_duration_minutes
FROM missions m
LEFT JOIN pilots p ON m.assigned_pilot_id = p.pilot_id
LEFT JOIN drones d ON m.assigned_drone_id = d.drone_id
WHERE m.planned_date >= CURRENT_DATE
    AND m.status IN ('approved', 'assigned', 'requested')
ORDER BY m.planned_date, m.planned_start_time;

-- ============================================================================
-- SAMPLE DATA (Optional - for testing)
-- ============================================================================

-- Insert sample drones
INSERT INTO drones (model, manufacturer, serial_number, registration_number, purchase_date, 
    max_flight_time_minutes, max_range_meters, max_altitude_meters, payload_capacity_kg, current_status) 
VALUES 
    ('Mavic 3 Enterprise', 'DJI', 'DJI-M3E-001', 'FA3X5Y7Z9', '2023-01-15', 45, 15000, 6000, 0.9, 'available'),
    ('Matrice 300 RTK', 'DJI', 'DJI-M300-001', 'FA4A8B2C6', '2023-02-20', 55, 15000, 7000, 2.7, 'available'),
    ('Phantom 4 RTK', 'DJI', 'DJI-P4R-001', 'FA5C3D1E4', '2022-11-10', 30, 7000, 6000, 0.5, 'maintenance'),
    ('Inspire 2', 'DJI', 'DJI-I2-001', 'FA6D4E2F5', '2023-03-05', 27, 7000, 5000, 1.5, 'available');

-- Insert sample pilots
INSERT INTO pilots (first_name, last_name, email, phone, part_107_certification_number, 
    certification_issue_date, certification_expiry_date, status) 
VALUES 
    ('Sarah', 'Johnson', 'sarah.johnson@skycontrol.com', '555-0101', 'P107-123456', '2023-01-15', '2025-01-15', 'active'),
    ('Michael', 'Chen', 'michael.chen@skycontrol.com', '555-0102', 'P107-234567', '2022-11-20', '2024-11-20', 'active'),
    ('Emily', 'Rodriguez', 'emily.rodriguez@skycontrol.com', '555-0103', 'P107-345678', '2023-06-10', '2025-06-10', 'active');

-- Insert sample batteries
INSERT INTO batteries (drone_id, serial_number, capacity_mah, voltage, cycle_count, purchase_date, status) 
VALUES 
    (1, 'BAT-M3E-001', 5000, 15.4, 45, '2023-01-15', 'active'),
    (1, 'BAT-M3E-002', 5000, 15.4, 38, '2023-01-15', 'active'),
    (2, 'BAT-M300-001', 5935, 44.4, 120, '2023-02-20', 'active'),
    (2, 'BAT-M300-002', 5935, 44.4, 115, '2023-02-20', 'active'),
    (3, 'BAT-P4R-001', 5870, 15.2, 250, '2022-11-10', 'degraded'),
    (4, 'BAT-I2-001', 4280, 22.8, 80, '2023-03-05', 'active');

-- Insert sample missions
INSERT INTO missions (mission_name, mission_type, description, requested_by, assigned_pilot_id, 
    assigned_drone_id, flight_location, latitude, longitude, planned_date, estimated_duration_minutes, status) 
VALUES 
    ('Cell Tower Inspection - Site A', 'inspection', 'Inspect cell tower for damage and maintenance needs', 
     'TechCom Solutions', 1, 1, '123 Tower Rd, Springfield', 39.7817, -89.6501, CURRENT_DATE + 1, 30, 'approved'),
    ('Solar Farm Survey', 'survey', 'Thermal imaging survey of solar panel array', 
     'GreenEnergy Inc', 2, 2, '456 Solar Way, Oakville', 38.5816, -89.9940, CURRENT_DATE + 2, 60, 'assigned'),
    ('Bridge Inspection', 'inspection', 'Visual inspection of bridge structure', 
     'State DOT', 1, 1, 'Highway 50 Bridge', 39.1234, -89.5678, CURRENT_DATE + 3, 45, 'requested');

-- Insert sample parts
INSERT INTO parts_inventory (part_name, part_number, category, compatible_models, 
    quantity_on_hand, minimum_stock_level, unit_cost, supplier_name) 
VALUES 
    ('Propeller Set - Mavic 3', 'PROP-M3-01', 'propeller', '["Mavic 3 Enterprise"]', 12, 5, 29.99, 'DJI Official'),
    ('Propeller Set - Matrice 300', 'PROP-M300-01', 'propeller', '["Matrice 300 RTK"]', 8, 3, 89.99, 'DJI Official'),
    ('Battery - Mavic 3', 'BAT-M3-NEW', 'battery', '["Mavic 3 Enterprise"]', 4, 2, 189.99, 'DJI Official'),
    ('Gimbal Camera - Phantom 4', 'CAM-P4-01', 'camera', '["Phantom 4 RTK"]', 1, 1, 899.99, 'DJI Official');

-- ============================================================================
-- COMMENTS
-- ============================================================================

COMMENT ON TABLE drones IS 'Core drone asset registry';
COMMENT ON TABLE batteries IS 'Battery inventory and health tracking';
COMMENT ON TABLE pilots IS 'Certified drone pilots';
COMMENT ON TABLE missions IS 'Flight missions and operations';
COMMENT ON TABLE flight_logs IS 'Detailed flight records';
COMMENT ON TABLE maintenance_records IS 'Maintenance and repair history';
COMMENT ON TABLE pre_flight_inspections IS 'Pre-flight safety checklists';
COMMENT ON TABLE incidents IS 'Incident and accident reports';
COMMENT ON TABLE parts_inventory IS 'Spare parts and consumables inventory';
COMMENT ON TABLE airspace_authorizations IS 'FAA airspace authorizations and waivers';

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
