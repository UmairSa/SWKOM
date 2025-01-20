package org.app.service;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.mapper.DocumentMapper;
import org.app.dal.entity.DocumentEntity;
import org.app.dal.repository.DocumentRepository;
import org.app.dto.DocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaperlessService {

    private static final Logger logger = LogManager.getLogger(PaperlessService.class);

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final EchoService echoService;

    @Autowired
    public PaperlessService(DocumentRepository documentRepository, DocumentMapper documentMapper, EchoService echoService) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
        this.echoService = echoService;
    }

    // Fetch all documents as DTOs
    public List<DocumentDto> getDocumentList() {
        logger.info("Collecting all documents.");
        try {
            List<DocumentEntity> entities = documentRepository.findAll();
            logger.info("Collected documents: {}", entities.size());
            return entities.stream()
                    .map(documentMapper::toDTO) // Map Entity to DTO
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error collecting documents", e);
            throw e;
        }
    }

    // Save a new document using DTO
    public void uploadDocument(@Valid DocumentDto documentDto) {
        logger.info("Uploading document: {}", documentDto.getName());
        try {
            // Map DTO to Entity
            DocumentEntity documentEntity = documentMapper.toEntity(documentDto);

            // Save to database
            echoService.processMessage(documentEntity.getName(), 0);
            documentRepository.save(documentEntity);
            logger.info("Document uploaded: {}", documentEntity.getId());
        } catch (Exception e) {
            logger.error("Upload failed: {}", documentDto.getName(), e);
            throw e;
        }
    }

    // Find a document by ID and return as DTO
    public Optional<DocumentDto> getDocumentById(Long id) {
        logger.info("Collecting document ID: {}", id);
        try {
            Optional<DocumentEntity> entity = documentRepository.findById(id);
            if (entity.isPresent()) {
                logger.info("Document collected with ID: {}", id);
                return entity.map(documentMapper::toDTO); // Convert Entity to DTO if present
            } else {
                logger.warn("Not found ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error collecting document with ID: {}", id, e);
            throw e;
        }
    }

    public boolean updateDocument(@Valid DocumentDto documentDTO) {
        logger.info("Updating document: {}", documentDTO.getId());
        try {
            DocumentEntity documentEntity = documentMapper.toEntity(documentDTO);
            if (documentRepository.existsById(documentEntity.getId())) {
                documentRepository.save(documentEntity);
                echoService.processMessage(documentEntity.getName(), 0);
                logger.info("Document updated: {}", documentDTO.getId());
                return true;
            } else {
                logger.warn("Document not found for update, ID: {}", documentDTO.getId());
                return false;
            }
        } catch (RuntimeException e) {
            logger.error("Update failed, ID: {}", documentDTO.getId(), e);
            throw e;
        }
    }

    // Delete a document by ID
    public boolean deleteDocumentById(Long id) {
        logger.info("Deleting document, ID: {}", id);
        if (documentRepository.existsById(id)) {
            try {
                documentRepository.deleteById(id);
                logger.info("Document deleted, ID: {}", id);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting document, ID: {}", id, e);
                throw e;
            }
        } else {
            logger.warn("Document not found, delete skipped, id: {} ", id);
            return false;
        }
    }
}
