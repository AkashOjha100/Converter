package com.ls.converter.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.ls.converter.entity.Document;
import com.ls.converter.request.PdfRequest;
import com.ls.converter.response.DocumentResponse;
import com.ls.converter.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class ApiController {
    private final DocumentService documentService;

    public ApiController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/v1/upload",consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public UUID uploadDocument(@RequestPart("file") MultipartFile file) throws Exception {
        try{
            return documentService.upload(file);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("v1/getAll")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() throws Exception{
        try{
            List<DocumentResponse> documents = documentService.getDocuments();
            return ResponseEntity.ok(documents);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("v1/get-by-id{id}")
    public Document  getDocumentById(@RequestParam("id") UUID id) throws  Exception {
        try{
            return documentService.getDocumentById(id);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/generate-pdf",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfRequest request) throws Exception {
        try {
            byte[] pdf = documentService.getGeneratePdf(request.getDocumentIds());
            return ResponseEntity.ok().header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=documents.pdf")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/two-image-per-page",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateTwoImagePerPage(@RequestBody PdfRequest request) throws Exception {
        try{
            byte[] pdf = documentService.generateTwoImagesPerPage(request.getDocumentIds());
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=two-images.pdf")
                    .contentType(MediaType.APPLICATION_PDF).body(pdf);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/json-to-pdf",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> jsonToPdf(@RequestBody String json) throws Exception {
        try{
            byte[] pdf = documentService.jsonToPdf(json);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=json.pdf"
            ).contentType(MediaType.APPLICATION_PDF).body(pdf);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
