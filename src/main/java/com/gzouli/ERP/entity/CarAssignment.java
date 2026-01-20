package com.gzouli.ERP.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CarAssignment {

    @Id
    @GeneratedValue
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false) // Un assignment concerne obligatoirement une voiture
    @JoinColumn(name = "car_id")
    private Car car;

    @ToString.Exclude
    @ManyToOne(optional = false) // Un assignment concerne obligatoirement un employee
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate; // Si NULL = Toujours actif
}
