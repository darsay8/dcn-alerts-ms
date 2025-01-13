package dev.rm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.rm.model.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

}
