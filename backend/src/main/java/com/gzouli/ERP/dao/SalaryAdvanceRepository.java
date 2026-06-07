package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.SalaryAdvance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryAdvanceRepository extends JpaRepository<SalaryAdvance, Long> {

    List<SalaryAdvance> findByEmployeeIdOrderByAdvanceDateDesc(Long employeeId);
}
