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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addExpense(
            @PathVariable Long projectId,
            @RequestPart("expense") String expenseJson,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            // Conversion manuelle du JSON String en Objet Java
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // Gestion des dates Java 8

            ExpenseRegistrationDTO dto = mapper.readValue(expenseJson, ExpenseRegistrationDTO.class);

            // Appel service
            expenseService.addExpense(projectId, dto, file);

            return ResponseEntity.ok().build(); // 200 OK

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Format de données invalide (Date ou Montant incorrects).");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erreur technique lors de l'enregistrement.");
        }
    }

    /**
     * LISTE DES DÉPENSES DU PROJET
     */
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getProjectExpenses(@PathVariable Long projectId) {
        return ResponseEntity.ok(expenseService.getExpensesByProject(projectId));
    }

    /**
     * SUPPRESSION D'UNE DÉPENSE
     * (Accessible via /api/projects/{projectId}/expenses/{expenseId})
     * Note: projectId n'est pas utilisé ici mais gardé pour la cohérence de l'URL
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long projectId, @PathVariable Long expenseId) {
        try {
            expenseService.deleteExpense(expenseId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Impossible de supprimer la dépense.");
        }
    }
}
