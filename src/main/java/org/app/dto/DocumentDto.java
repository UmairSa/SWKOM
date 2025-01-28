package org.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    private Long id;

    @NotBlank(message = "Name cannot be empty!")
    @Size(max = 100, message = "Name cannot exceed 100 characters!")
    private String name;

    private String content;

    private String minioObjectName;

    private LocalDateTime dateUploaded;
}
