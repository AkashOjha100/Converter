package com.ls.converter.imagetopdf.api;

import com.ls.converter.imagetopdf.entity.Document;
import com.ls.converter.imagetopdf.request.PdfRequest;
import com.ls.converter.imagetopdf.response.DocumentResponse;
import com.ls.converter.imagetopdf.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
@Tag(name = "API Services (Image)")
public class ImageApiController {
    private final DocumentService documentService;

    public ImageApiController(DocumentService documentService) {
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
    public DocumentResponse  getDocumentById(@RequestParam("id") UUID id) throws  Exception {
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


}
