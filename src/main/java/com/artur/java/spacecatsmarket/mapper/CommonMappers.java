package com.artur.java.spacecatsmarket.mapper;

import org.mapstruct.*;

import java.util.UUID;

@MapperConfig(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommonMappers {
    default UUID toUuid(String s) {
        return s == null ? null : UUID.fromString(s);
    }

    default String fromUuid(UUID id) {
        return id == null ? null : id.toString();
    }
}