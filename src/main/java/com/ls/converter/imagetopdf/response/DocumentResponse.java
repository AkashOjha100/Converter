package com.ls.converter.imagetopdf.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponse {
    private UUID id;
    private String fileName;
    private String fileType;
}
