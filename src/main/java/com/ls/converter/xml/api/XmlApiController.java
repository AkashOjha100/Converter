package com.ls.converter.xml.api;

import com.ls.converter.xml.service.XmlService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xml")
@CrossOrigin(origins = "*")
@Tag(name = "API Services (XML)")
public class XmlApiController {
    private final XmlService xmlService;

    public XmlApiController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    @PostMapping(value = "/v1/xml-to-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> xmlToPdf(@RequestBody String xml) throws Exception {
        try{
            byte[] pdf = xmlService.xmlToPdf(xml);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=xml.pdf"
            ).contentType(MediaType.APPLICATION_PDF).body(pdf);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/xml-to-word",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> xmlToWord(@RequestBody String xml) throws Exception {
        try{
            byte[] doc = xmlService.xmlToWord(xml);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=word.docx"
            ).contentType(MediaType.APPLICATION_OCTET_STREAM).body(doc);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/xml-to-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> xmlToExcel(@RequestBody String xml) throws Exception {
        try{
            byte[] doc = xmlService.xmlToExcel(xml);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=data.xlsx"
            ).contentType(MediaType.APPLICATION_OCTET_STREAM).body(doc);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
