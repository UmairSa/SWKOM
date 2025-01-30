package org.app.service;

import org.app.RabbitMQConfig;
import org.app.dal.entity.DocumentEntity;
import org.app.dal.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OcrResultListener {

    private static final Logger log = LoggerFactory.getLogger(OcrResultListener.class);

    private final DocumentRepository documentRepository;

    public OcrResultListener(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.OCR_OUT_QUEUE_NAME)
    public void handleOcrResult(String message) {

        log.info("Received OCR result: {}", message);

        Long docId = null;
        String recognizedText = null;
        try {
            String[] parts = message.split("\\|TEXT:");
            docId = Long.parseLong(parts[0].replace("DOCID:", ""));
            recognizedText = parts[1];
        } catch (Exception e) {
            log.error("Failed to parse OCR result: {}", message, e);
            return;
        }

        // Nun "recognizedText" in der DB speichern
        Optional<DocumentEntity> opt = documentRepository.findById(docId);
        if (opt.isPresent()) {
            DocumentEntity entity = opt.get();
            entity.setContent(recognizedText);   // <--- in `content` speichern
            documentRepository.save(entity);
            log.info("Stored OCR result in DocumentEntity ID={}", docId);
        } else {
            log.warn("Document with ID={} not found, cannot store OCR result", docId);
        }
    }
}
