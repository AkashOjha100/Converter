package com.ls.converter.json.api;

import com.ls.converter.json.service.JsonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/json")
@CrossOrigin(origins = "*")
@Tag(name = "API Services (JSON)")
public class JsonApiController {
    private final JsonService jsonService;

    public JsonApiController(JsonService jsonService) {
        this.jsonService = jsonService;
    }

    @PostMapping(value = "/v1/json-to-pdf",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> jsonToPdf(@RequestBody String json) throws Exception {
        try{
            byte[] pdf = jsonService.jsonToPdf(json);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=json.pdf"
            ).contentType(MediaType.APPLICATION_PDF).body(pdf);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/json-to-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> jsonToExcel(@RequestBody String json) throws Exception {
        try{
            byte[] excel = jsonService.jsonToExcel(json);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=data.xlsx"
            ).contentType(MediaType.APPLICATION_OCTET_STREAM).body(excel);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/json-to-word",
            produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public ResponseEntity<byte[]> xmlToWord(@RequestBody String json) throws Exception {
        try{
            byte[] doc = jsonService.jsonToWord(json);
            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=word.docx"
            ).contentType(MediaType.APPLICATION_OCTET_STREAM).body(doc);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/v1/json-to-xml" , produces = MediaType.APPLICATION_XML_VALUE)
    public String jsonToXml(@RequestBody JsonNode json) throws Exception {
        try{
            return jsonService.jsonToXml(json);
        }catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
