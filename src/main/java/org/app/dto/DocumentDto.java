package org.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;


public class DocumentDto {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String content;
    private LocalDateTime dateUploaded;



    public DocumentDto() {
    }

    public DocumentDto(Long id, String name, String content, LocalDateTime dateUploaded) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.dateUploaded = dateUploaded;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name must not be blank") @Size(max = 100, message = "Name must not exceed 100 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name must not be blank") @Size(max = 100, message = "Name must not exceed 100 characters") String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(LocalDateTime dateUploaded) {
        this.dateUploaded = dateUploaded;
    }
}
