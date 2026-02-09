package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.project.EquipmentDTO;
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.service.project.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
//@RequiredArgsConstructor
public class InventoryController {

    private final EquipmentService equipmentService;

    public InventoryController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * LISTE COMPLÈTE (Stock)
     * Status: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAllInventory() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    /**
     * LISTE DES DISPONIBLES (Pour Select)
     * Status: 200 OK
     */
    @GetMapping("/available")
    public ResponseEntity<List<EquipmentDTO>> getAvailableInventory() {
        return ResponseEntity.ok(equipmentService.getAvailableEquipment());
    }

    /**
     * CRÉATION
     * Status: 201 CREATED (Meilleure pratique que 200)
     */
    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody EquipmentDTO dto) {
        try {
            System.out.println(dto.toString());
            EquipmentDTO created = equipmentService.createEquipment(dto);
            System.out.println(created.toString());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * MODIFICATION
     * Status: 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody EquipmentDTO dto) {
        try {
            return ResponseEntity.ok(equipmentService.updateEquipment(id, dto));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * SUPPRESSION
     * Status: 204 NO CONTENT (Standard REST pour un delete réussi)
     * ou 400 BAD REQUEST si BusinessException
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            equipmentService.deleteEquipment(id);
            return ResponseEntity.noContent().build();
        } catch (BusinessException e) {
            // Renvoie le message d'erreur métier (ex: "Lié à un historique")
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
