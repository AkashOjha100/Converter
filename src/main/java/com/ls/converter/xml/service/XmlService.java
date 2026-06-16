package com.ls.converter.xml.service;

import com.ls.converter.common.util.AppUtil;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

@Service
public class XmlService {
    private final AppUtil appUtil;
    private final ObjectMapper objectMapper;

    public XmlService(AppUtil appUtil, ObjectMapper objectMapper) {
        this.appUtil = appUtil;
        this.objectMapper = objectMapper;
    }

    //XML-TO-PDF
    public byte[] xmlToPdf(String xml) throws Exception {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xml);
            return appUtil.createPdf(node);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    //XML-TO-WORD
    public byte[] xmlToWord(String xml) throws Exception {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xml);
            return appUtil.createWord(node);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    //XML-TO-EXCEL
    public byte[] xmlToExcel(String xml) throws Exception {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xml);
            return appUtil.createExcel(node);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String xmlToJson(String xml) throws Exception {
        try{
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(xml);

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(node);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

//    public byte[] xmlToCsv(String xml) throws Exception {
//        try{
//            XmlMapper xmlMapper = new XmlMapper();
//            JsonNode node = xmlMapper.readTree(xml);
//            return createCsv(node);
//        }catch (Exception e){
//            throw new RuntimeException(e.getMessage());
//        }
//    }
}
