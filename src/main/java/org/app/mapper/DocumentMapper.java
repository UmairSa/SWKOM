package org.app.mapper;

import org.mapstruct.Mapper;
import org.app.dal.entity.DocumentEntity;
import org.app.dto.DocumentDto;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    DocumentDto toDTO(DocumentEntity documentEntity);
    DocumentEntity toEntity(DocumentDto documentDTO);
}