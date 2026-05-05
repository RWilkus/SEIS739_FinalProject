package com.skycontrol.repository;

import com.skycontrol.model.Pilot;
import com.skycontrol.model.PilotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    Optional<Pilot> findByLicenseNumber(String licenseNumber);
    Optional<Pilot> findByEmail(String email);
    List<Pilot> findByStatus(PilotStatus status);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
}
