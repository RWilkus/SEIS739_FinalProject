package com.skycontrol.repository;

import com.skycontrol.model.Drone;
import com.skycontrol.model.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    Optional<Drone> findBySerialNumber(String serialNumber);
    List<Drone> findByStatus(DroneStatus status);
    boolean existsBySerialNumber(String serialNumber);
}
