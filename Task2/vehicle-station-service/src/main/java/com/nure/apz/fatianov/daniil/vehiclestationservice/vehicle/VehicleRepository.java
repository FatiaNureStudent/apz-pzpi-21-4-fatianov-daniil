package com.nure.apz.fatianov.daniil.vehiclestationservice.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByNumber(String number);
}
