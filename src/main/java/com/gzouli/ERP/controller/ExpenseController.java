package com.gzouli.ERP.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Important pour LocalDate
import com.gzouli.ERP.dto.expense.ExpenseDTO;
import com.gzouli.ERP.dto.expense.ExpenseRegistrationDTO;
import com.gzouli.ERP.service.expense.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    /**
     * AJOUT D'UNE DÉPENSE (Multipart)
     * Reçoit le JSON sous forme de String ("expense") et le fichier binaire ("file")
     */
    @PostMapping("")
    public ResponseEntity<?> addExpense(
            @PathVariable("projectId") Long projectId,
            @RequestBody ExpenseRegistrationDTO dto // Plus de RequestPart ou MultipartFile !
    ) {
        try {
            // Le DTO contient maintenant toutes les infos + le chemin S3 (fileKey)
            expenseService.addExpense(projectId, dto);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur technique");
        }
    }

    /**
     * LISTE DES DÉPENSES DU PROJET
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getProjectExpenses(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.ok(expenseService.getExpensesByProject(projectId));
    }

    /**
     * SUPPRESSION D'UNE DÉPENSE
     * (Accessible via /api/projects/{projectId}/expenses/{expenseId})
     * Note: projectId n'est pas utilisé ici mais gardé pour la cohérence de l'URL
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable("projectId") Long projectId, @PathVariable("expenseId") Long expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Impossible de supprimer la dépense.");
        }
    }
}
