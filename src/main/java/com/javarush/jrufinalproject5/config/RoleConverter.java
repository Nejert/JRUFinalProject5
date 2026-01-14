package com.javarush.jrufinalproject5.config;

import com.javarush.jrufinalproject5.entity.Role;
import jakarta.persistence.AttributeConverter;

public class RoleConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role attribute) {
        return attribute.ordinal() + 1;
    }

    @Override
    public Role convertToEntityAttribute(Integer dbData) {
        return Role.values()[dbData - 1];
    }
}
