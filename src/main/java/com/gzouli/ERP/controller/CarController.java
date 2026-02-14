package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.car.CarAssignmentDTO;
import com.gzouli.ERP.dto.car.CarDTO;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.service.car.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    // 1. Liste du Parc
    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    // 2. Ajouter un véhicule
    @PostMapping
    public ResponseEntity<?> createCar(@RequestBody CarDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(dto));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Assigner un véhicule à un employé
    @PostMapping("/assign")
    public ResponseEntity<?> assignCar(@RequestBody @Valid CarAssignmentDTO dto) {
        try {
            carService.assignCar(dto);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Libérer un véhicule (Récupération des clés)
    @PatchMapping("/{carId}/release")
    public ResponseEntity<?> releaseCar(
            @PathVariable Long carId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {
        try {
            carService.releaseCar(carId, returnDate);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCar(@PathVariable Long id, @RequestBody CarDTO dto) {
        try {
            return ResponseEntity.ok(carService.updateCar(id, dto));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. Supprimer un véhicule
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
