package dev.rm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.rm.model.Alert;

public interface Repository extends JpaRepository<Alert, Long> {

}
