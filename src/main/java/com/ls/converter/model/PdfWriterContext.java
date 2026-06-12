package com.ls.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfWriterContext {
    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;
    private float yPosition;
}
