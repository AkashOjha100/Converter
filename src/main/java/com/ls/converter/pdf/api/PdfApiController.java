package com.ls.converter.pdf.api;

import com.ls.converter.pdf.service.PdfService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
@Tag(name = "API Services (Pdf)")
public class PdfApiController {
    private final PdfService pdfService;

    public PdfApiController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping(value = "/v1/pdf-to-word",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )
    public ResponseEntity<byte[]> pdfToWord(@RequestPart("file") MultipartFile file) throws Exception {
        try {
            byte[] doc = pdfService.pdfToWord(file);
            return ResponseEntity.ok().header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=report.docx")
                    .body(doc);
        }catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
