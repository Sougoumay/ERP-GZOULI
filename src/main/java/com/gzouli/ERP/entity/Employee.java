package com.gzouli.ERP.entity;

import com.gzouli.ERP.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cognitoId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String idCardNumber;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;
    private String address;
    private Double salary;
    private LocalDate birthday;
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private Role role;


    @ToString.Exclude
    @OneToMany(mappedBy = "employee")
    private List<CarAssignment> carAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude
    private List<SalaryAdvance> advances;

    @OneToMany(mappedBy = "assignee")
    @ToString.Exclude
    private List<Task> assignedTasks = new ArrayList<>();

    @ManyToMany(mappedBy = "supervisors")
    @ToString.Exclude
    private List<Project> monitoredProjects = new ArrayList<>();

    // 4. Les Comptes Rendus écrits par cet employé
    @OneToMany(mappedBy = "author")
    @ToString.Exclude
    private List<SiteJournal> journals = new ArrayList<>();

    public List<Car> getActiveEquipments() {
        if (carAssignments == null) return new ArrayList<>();
        return carAssignments.stream()
                .filter(a -> a.getEndDate() == null) // Seulement ceux actifs
                .map(CarAssignment::getCar)
                .toList();
    }
}