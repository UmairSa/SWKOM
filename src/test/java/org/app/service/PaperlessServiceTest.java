package org.app.service;

import org.app.RabbitMQConfig;
import org.app.dal.entity.DocumentEntity;
import org.app.dal.repository.DocumentRepository;
import org.app.dto.DocumentDto;
import org.app.mapper.DocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaperlessServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaperlessService paperlessService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDocumentList() {
        // Arrange
        List<DocumentEntity> mockEntityList = new ArrayList<>();
        DocumentEntity docEntity = new DocumentEntity();
        docEntity.setId(1L);
        docEntity.setName("TestDoc");
        mockEntityList.add(docEntity);

        when(documentRepository.findAll()).thenReturn(mockEntityList);

        DocumentDto mockDto = new DocumentDto();
        mockDto.setId(1L);
        mockDto.setName("TestDoc");

        when(documentMapper.toDTO(docEntity)).thenReturn(mockDto);

        // Act
        List<DocumentDto> result = paperlessService.getDocumentList();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TestDoc", result.get(0).getName());
        verify(documentRepository, times(1)).findAll();
        verify(documentMapper, times(1)).toDTO(docEntity);
    }

    @Test
    void testUploadDocument() {
        // Arrange
        DocumentDto docDto = new DocumentDto();
        docDto.setName("NewDoc");

        DocumentEntity mockEntity = new DocumentEntity();
        mockEntity.setId(1L);
        mockEntity.setName("NewDoc");

        when(documentMapper.toEntity(docDto)).thenReturn(mockEntity);

        // Act
        paperlessService.uploadDocument(docDto);

        // Assert
        verify(documentRepository, times(1)).save(mockEntity);
        verify(rabbitTemplate, times(1))
                .convertAndSend(RabbitMQConfig.OCR_IN_QUEUE_NAME, "DOCID:1");
    }

    @Test
    void testGetDocumentById_Found() {
        // Arrange
        Long docId = 42L;
        DocumentEntity mockEntity = new DocumentEntity();
        mockEntity.setId(docId);
        mockEntity.setName("FoundDoc");

        DocumentDto mockDto = new DocumentDto();
        mockDto.setId(docId);
        mockDto.setName("FoundDoc");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(mockEntity));
        when(documentMapper.toDTO(mockEntity)).thenReturn(mockDto);

        // Act
        Optional<DocumentDto> result = paperlessService.getDocumentById(docId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("FoundDoc", result.get().getName());
        verify(documentRepository).findById(docId);
    }

    @Test
    void testGetDocumentById_NotFound() {
        // Arrange
        Long docId = 99L;
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        // Act
        Optional<DocumentDto> result = paperlessService.getDocumentById(docId);

        // Assert
        assertFalse(result.isPresent());
        verify(documentRepository).findById(docId);
    }

    @Test
    void testDeleteDocumentById() {
        // Arrange
        Long docId = 10L;
        when(documentRepository.existsById(docId)).thenReturn(true);

        // Act
        boolean success = paperlessService.deleteDocumentById(docId);

        // Assert
        assertTrue(success);
        verify(documentRepository).deleteById(docId);
    }

    @Test
    void testDeleteDocumentById_NotFound() {
        // Arrange
        Long docId = 10L;
        when(documentRepository.existsById(docId)).thenReturn(false);

        // Act
        boolean success = paperlessService.deleteDocumentById(docId);

        // Assert
        assertFalse(success);
        verify(documentRepository, never()).deleteById(docId);
    }
}
