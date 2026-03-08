package com.nishchay.identity_service.entity.convertor;

import com.nishchay.identity_service.enums.ERole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<ERole,String> {


    @Override
    public String convertToDatabaseColumn(ERole eRole) {
        if(eRole==null){
            return null;
        }
        return eRole.getLabel();
    }

    @Override
    public ERole convertToEntityAttribute(String label) {
        if(label==null){
            return null;
        }
        return switch (label){
            case "Administrator" -> ERole.ADMINISTRATOR;
            case "Customer"->ERole.CUSTOMER;
            case "Employee"->ERole.EMPLOYEE;
            default -> throw new IllegalArgumentException("Unknown Role"+label);
        };
    }
}
