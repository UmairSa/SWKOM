package org.app.dal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String minioObjectName;

    @Column(name = "content") // bei PostgreSQL
    private String content;

    private LocalDateTime dateUploaded;
}
