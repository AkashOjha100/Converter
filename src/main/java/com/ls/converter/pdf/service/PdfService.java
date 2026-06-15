package com.ls.converter.pdf.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public byte[] pdfToWord(MultipartFile file) throws Exception {
        try(PDDocument pdf = Loader.loadPDF(file.getBytes());
            XWPFDocument wordDocument = new XWPFDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdf);
                String[] lines = text.split("\\R");
                for(String line : lines) {
                    XWPFParagraph paragraph = wordDocument.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText(line);
                }
                wordDocument.write(out);

                return out.toByteArray();
        }
    }
}
