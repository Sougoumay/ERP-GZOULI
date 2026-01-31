package com.gzouli.ERP.dto.project;

import com.gzouli.ERP.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberDTO {
    private Long id;          // CRITIQUE : L'ID technique pour la suppression
    private String firstName;
    private String lastName;
    private Role role;      // "INGENIEUR" ou "TECHNICIEN"
    private String email;     // Utile pour l'info-bulle ou contact
}
