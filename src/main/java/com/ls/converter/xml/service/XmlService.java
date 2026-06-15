package com.ls.converter.xml.service;

import com.ls.converter.common.util.AppUtil;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.dataformat.xml.XmlMapper;

@Service
public class XmlService {
    private final AppUtil appUtil;

    public XmlService(AppUtil appUtil) {
        this.appUtil = appUtil;
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
}
