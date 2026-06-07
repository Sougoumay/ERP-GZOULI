package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Car;
import com.gzouli.ERP.entity.CarAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarAssignmentRepository extends JpaRepository<CarAssignment, Long> {

    List<CarAssignment> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
}
