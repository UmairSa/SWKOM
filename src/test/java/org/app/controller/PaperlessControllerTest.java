package org.app.controller;

import org.app.dto.DocumentDto;
import org.app.service.MinioFileService;
import org.app.service.PaperlessService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaperlessController.class)
class PaperlessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaperlessService paperlessService;

    @MockBean
    private MinioFileService minioFileService;

    // ---------------------------------------------
    // Beispiel: GET /api/documents
    // ---------------------------------------------
    @Test
    void testGetDocuments() throws Exception {
        // Arrange
        List<DocumentDto> mockDocs = new ArrayList<>();
        DocumentDto dto1 = new DocumentDto();
        dto1.setId(1L);
        dto1.setName("TestDoc1");
        mockDocs.add(dto1);

        when(paperlessService.getDocumentList()).thenReturn(mockDocs);

        // Act & Assert
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("TestDoc1"));
    }

    // ---------------------------------------------
    // Beispiel: GET /api/documents/{id}
    // ---------------------------------------------
    @Test
    void testGetDocumentById_Found() throws Exception {
        DocumentDto dto = new DocumentDto();
        dto.setId(42L);
        dto.setName("FoundDoc");
        dto.setDateUploaded(LocalDateTime.now());

        when(paperlessService.getDocumentById(42L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/documents/{id}", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42L))
                .andExpect(jsonPath("$.name").value("FoundDoc"));
    }

    @Test
    void testGetDocumentById_NotFound() throws Exception {
        when(paperlessService.getDocumentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/documents/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------
    // Beispiel: POST /api/upload (multipart)
    // ---------------------------------------------
    @Test
    void testUploadDocument() throws Exception {
        // MockMultipartFile aus dem Spring-Test-Framework
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",                  // Form Field Name
                "test.pdf",              // original Filename
                "application/pdf",       // MIME-Typ
                "PDF Dummy Content".getBytes()
        );

        // Wir mocken paperlessService.uploadDocument(...) => Nichts returned,
        // also wir prüfen nur, dass es 200 zurückgibt
        Mockito.doNothing().when(paperlessService).uploadDocument(any(DocumentDto.class));

        mockMvc.perform(multipart("/api/upload")
                        .file(multipartFile)
                        .param("name", "TestDocUpload"))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------
    // Beispiel: PUT /api/update/{id}
    // ---------------------------------------------
    @Test
    void testUpdateDocument() throws Exception {
        // Mock: paperlessService.updateDocument(...) -> true
        when(paperlessService.updateDocument(any(DocumentDto.class))).thenReturn(true);

        String jsonPayload = """
            {
              "id": 10,
              "name": "UpdatedName"
            }
            """;

        mockMvc.perform(put("/api/update/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateDocument_NotFound() throws Exception {
        when(paperlessService.updateDocument(any(DocumentDto.class))).thenReturn(false);

        String jsonPayload = """
            {
              "id": 999,
              "name": "NotExisting"
            }
            """;

        mockMvc.perform(put("/api/update/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------
    // Beispiel: DELETE /api/documents/{id}
    // ---------------------------------------------
    @Test
    void testDeleteDocument() throws Exception {
        when(paperlessService.deleteDocumentById(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/documents/{id}", 5L))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDocument_NotFound() throws Exception {
        when(paperlessService.deleteDocumentById(100L)).thenReturn(false);

        mockMvc.perform(delete("/api/documents/{id}", 100L))
                .andExpect(status().isNotFound());
    }
}
