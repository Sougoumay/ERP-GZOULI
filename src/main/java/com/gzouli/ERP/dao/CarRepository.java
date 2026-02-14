package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
}
