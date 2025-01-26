package org.app.controller;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.app.dto.DocumentDto;
import org.app.service.MinioFileService;
import org.app.service.PaperlessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/api")
public class PaperlessController {

    private static final Logger logger = (Logger) LogManager.getLogger(PaperlessController.class);

    private PaperlessService paperlessService;
    @Autowired
    private MinioFileService minioFileService;


    @Autowired
    public PaperlessController(PaperlessService paperlessService) {
        this.paperlessService = paperlessService;
    }

    // Get all documents
    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDto>> getDocuments() {
        logger.info("Collecting documents");
        List<DocumentDto> documents = paperlessService.getDocumentList();
        logger.info("Collecting done: {}", documents.size());
        return ResponseEntity.ok(documents);
    }

    // Upload a new document
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadDocument(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file
    ) {
        logger.info("Uploading: {}", name);

        DocumentDto docDto = new DocumentDto();
        docDto.setName(name);
        docDto.setDateUploaded(LocalDateTime.now());

        try {
            // Z.B. generiere ein Object-Name; z.B. UUID + Original-Filename
            String objectName = UUID.randomUUID() + "_" + name + ".pdf";

            // 1) Hochladen zu MinIO
            minioFileService.upload(file.getBytes(), objectName);

            // 2) In DocumentDto speichern
            docDto.setMinioObjectName(objectName);

            // 3) In PaperlessService schreiben (DB-Speicherung)
            paperlessService.uploadDocument(docDto);
            logger.info("upload done: '{}'", name);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading: '{}'", name, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/documents/{id}/file")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Optional<DocumentDto> docOpt = paperlessService.getDocumentById(id);
        if (docOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        DocumentDto doc = docOpt.get();
        if (doc.getMinioObjectName() == null) {
            // Keine Datei hinterlegt
            return ResponseEntity.noContent().build();
        }

        byte[] pdfData = minioFileService.download(doc.getMinioObjectName());
        if (pdfData == null || pdfData.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }



    // Get a document by ID
    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        logger.info("Collecting id: {}", id);
        return paperlessService.getDocumentById(id)
                .map(documentDTO -> {
                    logger.info(" id collected: {}", id);
                    return ResponseEntity.ok(documentDTO);
                })
                .orElseGet(() -> {
                    logger.warn("id not found: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // Update an existing document
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentDto documentDto) {
        logger.info("Updating ID: {}", id);
        documentDto.setId(id);  // Ensure the ID in the path is used
        if (paperlessService.updateDocument(documentDto)) {
            logger.info("Updated ID successfully: {}", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("ID update failed not found: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Delete a document by ID
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        logger.info("delete ID: {}", id);
        if (paperlessService.deleteDocumentById(id)) {
            logger.info("ID deleted: {}", id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            logger.warn("ID not found, delete failed: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }
    }

}