package com.ls.converter.imagetopdf.service;

import com.ls.converter.imagetopdf.entity.Document;
import com.ls.converter.imagetopdf.repository.DocumentRepository;
import com.ls.converter.imagetopdf.response.DocumentResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public UUID upload(MultipartFile file) throws Exception {
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            Document document = Document.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .base64Data(base64)
                    .build();

            return documentRepository.save(document).getId();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public byte[] getGeneratePdf(List<UUID> id) throws Exception {
        List<Document> documents = documentRepository.findAllById(id);
        try (PDDocument pdf = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for(Document document: documents){
                byte[] imageBytes = Base64.getDecoder().decode(document.getBase64Data());
                PDPage page = new PDPage();
                pdf.addPage(page);
                PDImageXObject image = PDImageXObject.createFromByteArray(pdf, imageBytes ,document.getFileName());
                try(PDPageContentStream contentStream = new PDPageContentStream(pdf, page)) {
                    contentStream.drawImage(image,50,100,500,600);
                }
                System.out.println(document.getBase64Data().length());
                System.out.println(document.getBase64Data().substring(0, 50));
                System.out.println(
                        "Pages = " + pdf.getNumberOfPages());
            }
            pdf.save(out);
            byte[] pdfBytes = out.toByteArray();
            documentRepository.deleteAll(documents);
            System.out.println("PDF Size ="+pdfBytes.length);
            return pdfBytes;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<DocumentResponse> getDocuments() throws Exception {
        try {
            return documentRepository.findAll()
                    .stream()
                    .map(document -> DocumentResponse.builder()
                        .id(document.getId())
                        .fileName(document.getFileName())
                        .fileType(document.getFileType())
                        .build())
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public DocumentResponse getDocumentById(UUID id) throws Exception {
        try{
            return documentRepository.findById(id)
                    .stream()
                    .map(document -> DocumentResponse.builder()
                            .id(document.getId())
                            .fileName(document.getFileName())
                            .fileType(document.getFileType())
                            .base64Data(document.getBase64Data())
                            .build())
                    .collect(Collectors.toList()).get(0);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] generateTwoImagesPerPage(List<UUID> id) throws Exception {
        List<Document> documents = documentRepository.findAllById(id);
        try (PDDocument pdf = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = null;
            PDPageContentStream contentStream = null;
            int count = 0;
            for(Document document: documents){
                if(count %2 == 0){
                    if(contentStream != null){
                        contentStream.close();
                    }
                    page = new PDPage();
                    pdf.addPage(page);
                    contentStream = new PDPageContentStream(pdf, page);
                }
                byte[] imageBytes = Base64.getDecoder().decode(document.getBase64Data());
                PDImageXObject image = PDImageXObject.createFromByteArray(pdf, imageBytes, document.getFileName());
                float x = 50;
                float y = (count % 2 ==0) ? 420:50;
                contentStream.drawImage(image,x,y,400,350);
                count++;
            }
            if(contentStream != null){
                contentStream.close();
            }
            pdf.save(out);
            byte[] pdfBytes = out.toByteArray();
            return  pdfBytes;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
