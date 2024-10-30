package org.App;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaperlessController {

    @Autowired
    PaperlessService paperlessService;

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Paperless REST API");
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<String> uploadDocument() {
        String response = paperlessService.uploadDocument();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<String> getDocument(@PathVariable String id) {
        String response = paperlessService.getDocument(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<String> updateDocument(@PathVariable String id) {
        String response = paperlessService.updateDocument(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents/search")
    public ResponseEntity<String> searchDocuments(@RequestParam String query) {
        String response = paperlessService.searchDocuments(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable String id) {
        String response = paperlessService.deleteDocument(id);
        return ResponseEntity.ok(response);
    }

}