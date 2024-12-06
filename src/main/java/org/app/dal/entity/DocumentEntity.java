package org.app.dal.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String content;

    @Column(name = "date_uploaded", nullable = false, updatable = false)
    private LocalDateTime dateUploaded;


    public DocumentEntity(Long id, String name, String content, LocalDateTime dateUploaded) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.dateUploaded = dateUploaded;
    }

    public DocumentEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
