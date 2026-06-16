package com.ls.converter.imagetopdf.entity;

import com.ls.converter.common.entity.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = true)
public class Document extends EntityBase {
    private String fileName;
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String base64Data;
}
