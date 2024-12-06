package org.app.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.dto.DocumentDto;
import org.app.service.PaperlessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/api")
public class PaperlessController {

    private static final Logger logger = (Logger) LogManager.getLogger(PaperlessController.class);

    private PaperlessService paperlessService;

    @Autowired
    public PaperlessController(PaperlessService paperlessService) {
        this.paperlessService = paperlessService;
    }

    // Get all documents
    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDto>> getDocuments() {
        logger.info("Fetching all documents.");
        List<DocumentDto> documents = paperlessService.getDocumentList();
        logger.info("Successfully fetched {} documents.", documents.size());
        return ResponseEntity.ok(documents);
    }

    // Upload a new document
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadDocument(@RequestParam("name") String name) {
        logger.info("Received request to upload document with name: {}", name);

        DocumentDto docDto = new DocumentDto();
        docDto.setName(name);
        docDto.setContent(null);
        docDto.setDateUploaded(LocalDateTime.now());


        try {
            paperlessService.uploadDocument(docDto);
            logger.info("Document with name '{}' uploaded successfully.", name);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error uploading document with name '{}'.", name, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Bad Request on failure
        }
    }

    // Get a document by ID
    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        logger.info("Fetching document with ID: {}", id);
        return paperlessService.getDocumentById(id)
                .map(documentDTO -> {
                    logger.info("Document with ID: {} found.", id);
                    return ResponseEntity.ok(documentDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Document with ID: {} not found.", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // Update an existing document
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentDto documentDto) {
        logger.info("Received request to update document with ID: {}", id);
        documentDto.setId(id);  // Ensure the ID in the path is used
        if (paperlessService.updateDocument(documentDto)) {
            logger.info("Document with ID: {} updated successfully.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Document with ID: {} not found, update failed.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a document by ID
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        logger.info("Received request to delete document with ID: {}", id);
        if (paperlessService.deleteDocumentById(id)) {
            logger.info("Document with ID: {} deleted successfully.", id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            logger.warn("Document with ID: {} not found, delete failed.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }
    }

}