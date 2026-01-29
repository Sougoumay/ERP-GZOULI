package com.gzouli.ERP.dto.project;

import com.gzouli.ERP.enums.Role;
import lombok.Data;

@Data
public class TeamMemberDTO {
    private Long id;
    private String fullName; // "Hamid ANDJA"
    private Role role;     // "INGENIEUR"
}
