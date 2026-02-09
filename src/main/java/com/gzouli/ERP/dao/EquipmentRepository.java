package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Trouver tout le matériel qui n'est PAS actuellement assigné quelque part
    @Query("SELECT e FROM Equipment e WHERE e.id NOT IN " +
            "(SELECT a.equipment.id FROM EquipmentAssignment a WHERE a.endDate IS NULL)")
    List<Equipment> findAvailableEquipment();
}
