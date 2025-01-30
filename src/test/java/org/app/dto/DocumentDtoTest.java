package org.app.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNameNotBlank() {
        DocumentDto dto = new DocumentDto();
        dto.setName(""); // Leer -> sollte fehlschlagen

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Es sollte einen Validierungsfehler geben!");
        assertTrue(violations.stream().anyMatch(
                        v -> v.getPropertyPath().toString().equals("name")),
                "Fehlermeldung sollte sich auf 'name' beziehen");
    }

    @Test
    void testNameTooLong() {
        DocumentDto dto = new DocumentDto();
        dto.setName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
                + "xxxxx"); // mehr als 100 Zeichen

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(
                        v -> v.getPropertyPath().toString().equals("name")),
                "Fehlermeldung sollte sich auf 'name' beziehen (Länge > 100)");
    }

    @Test
    void testValidName() {
        DocumentDto dto = new DocumentDto();
        dto.setName("ValidName");

        Set<ConstraintViolation<DocumentDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Keine Validierungsfehler erwartet");
    }
}
