package com.ls.converter.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ls.converter.model.PdfWriterContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.ls.converter.entity.Document;
import com.ls.converter.repository.DocumentRepository;
import com.ls.converter.response.DocumentResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;

    public DocumentService(DocumentRepository documentRepository, ObjectMapper objectMapper, XmlMapper xmlMapper) {
        this.documentRepository = documentRepository;
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
    }

    public UUID upload(MultipartFile file) throws Exception {
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            Document document = Document.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .base64Data(base64)
                    .build();
            System.out.println("Base64 Length = " + base64.length());
            System.out.println(base64.substring(0, 50));

            return documentRepository.save(document).getId();
        }catch (Exception e){
            throw new RuntimeException("Failed to upload file");
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

    public Document getDocumentById(UUID id) throws Exception {
        try{
            return documentRepository.findById(id)
                    .stream()
                    .map(document -> Document.builder()
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

    public byte[] jsonToPdf(String json) throws Exception{
        JsonNode node = objectMapper.readTree(json);
        try(PDDocument document = new PDDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try(PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 15);
                //contentStream.setLeading(15);
                contentStream.newLineAtOffset(40, 750);
                contentStream.showText("JSON REPORT");
                contentStream.newLineAtOffset(0 , -25);
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.showText(
                        "Generated On :" +
                                LocalDateTime.now()
                );
                contentStream.newLineAtOffset(0 , -15);
                contentStream.showText(
                        "Generated By : JSON Converter");
                contentStream.newLineAtOffset(0, -30);
                PdfWriterContext context= new PdfWriterContext(
                        document,
                        page,
                        contentStream,
                        750
                );
                writeNode(node, "", context);
                context.getContentStream().endText();
                context.getContentStream().close();
                document.save(out);
                return out.toByteArray();
            }

//            for(Map.Entry<String , Object> entry: json.entrySet()){
//                String
//                contentStream.showText(entry.getKey() + ": " + String.valueOf(entry.getValue()));
//                contentStream.newLine();
//            }
//            contentStream.endText();
//            contentStream.close();
//            document.save(out);
//            return out.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    private void writeNode(JsonNode node, String path, PdfWriterContext context) throws Exception {
        try {
            if (node.isObject()) {
                for (Map.Entry<String, JsonNode> field : node.properties()) {
                    String currentPath = path.isEmpty() ? field.getKey() : path + ". " + field.getKey();
                    writeNode(field.getValue(), currentPath, context);
                }
            } else if (node.isArray()) {
                for (int i = 0; i < node.size(); i++) {
                    writeNode(node.get(i), path + "[" + i + "]", context);
                }
            } else {
                String value = node.isNull() ? "null" : node.asText();
                value = value.replace("\n", " ").replace("\r", " ");
                String line = path + " : " + value;
                List<String> wrappedLines = wordWrap(line, 90);
                for (String wrappedLine : wrappedLines) {
                    writeLine(context, wrappedLine);
                }
                //context.showText(path + " : " + value);
                //context.newLine();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createNewPage(PdfWriterContext context) throws Exception {
        try {
            context.getContentStream().endText();
            context.getContentStream().close();
            PDPage page = new PDPage();
            context.getDocument().addPage(page);

            PDPageContentStream newStream = new PDPageContentStream(context.getDocument(), page);
            newStream.beginText();
            newStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 15);
            newStream.newLineAtOffset(40, 750);
            context.setPage(page);
            context.setContentStream(newStream);
            context.setYPosition(500);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    private void writeLine(PdfWriterContext context, String text) throws Exception {
        try {
            if (context.getYPosition() < 50) {
                createNewPage(context);
            }
            context.getContentStream().showText(text);
            context.getContentStream().newLineAtOffset(0, -15);
            context.setYPosition(context.getYPosition() - 15);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<String> wordWrap(String text, int maxLength) throws Exception {
        try {
            List<String> result = new ArrayList<>();
            while (text.length() > maxLength) {
                result.add(text.substring(0, maxLength));
                text = text.substring(maxLength);
            }
            result.add(text);
            return result;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
