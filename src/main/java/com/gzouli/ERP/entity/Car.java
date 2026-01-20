package com.gzouli.ERP.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
public class Car {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private String immatriculation;
    
    private String mark;

    private String model;

    @ToString.Exclude
    @OneToMany(mappedBy = "car")
    private List<CarAssignment> assignments;
    
}
