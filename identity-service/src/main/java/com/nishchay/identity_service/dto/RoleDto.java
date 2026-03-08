package com.nishchay.identity_service.dto;

import com.nishchay.identity_service.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private ERole authority;
}
