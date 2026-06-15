package com.ls.converter.common.util;

import com.ls.converter.imagetopdf.model.PdfWriterContext;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AppUtil {
    public void writeNode(JsonNode node, String path, PdfWriterContext context) throws Exception {
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

    public void createNewPage(PdfWriterContext context) throws Exception {
        try {
            context.getContentStream().endText();
            context.getContentStream().close();
            PDPage page = new PDPage();
            context.getDocument().addPage(page);

            PDPageContentStream newStream = new PDPageContentStream(context.getDocument(), page);
            newStream.beginText();
            newStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            newStream.newLineAtOffset(40, 750);
            context.setPage(page);
            context.setContentStream(newStream);
            context.setYPosition(500);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    public void writeLine(PdfWriterContext context, String text) throws Exception {
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

    public List<String> wordWrap(String text, int maxLength) throws Exception {
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

    public byte[] createPdf(JsonNode root) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("JSON/XML REPORT");
            contentStream.newLineAtOffset(0, -25);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
            contentStream.showText("Generated On : " + new Date());
            contentStream.newLineAtOffset(0, -30);
            PdfWriterContext context = new PdfWriterContext(document, page, contentStream, 695);
            writeNode(root, "", context);
            context.getContentStream().endText();
            context.getContentStream().close();
            document.save(out);
            return out.toByteArray();
        }
    }

    public byte[] createWord(JsonNode root) throws Exception {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()){
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setBold(true);
            run.setFontSize(18);
            run.setText("XML REPORT");
            XWPFParagraph content = document.createParagraph();

            content.createRun()
                    .setText("Generated On : " + new Date());
            writeWordNode(root , "" , document);
            document.write(out);
            return out.toByteArray();
        }
    }
    public void writeWordNode(JsonNode node, String path, XWPFDocument document) {
        try {
            if (node.isObject()) {
                for (Map.Entry<String, JsonNode> field : node.properties()) {
                    String currentPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
                    writeWordNode(field.getValue(), currentPath, document);
                }
            } else if (node.isArray()) {
                for (int i = 0; i < node.size(); i++) {
                    XWPFParagraph separator = document.createParagraph();
                    separator.createRun().setText("================================");
                    XWPFParagraph user = document.createParagraph();
                    XWPFRun run = user.createRun();
                    run.setBold(true);
                    run.setText("RECORD #" + (i + 1));
                    writeWordNode(node.get(i), "", document);
                }
            } else {
                String value = node.isNull() ? "null" : node.asText();
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(path + " : " + value);
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public void writeExcelNode(JsonNode node, String path, Sheet sheet , AtomicInteger rowNum) throws Exception {
        try {
            if (node.isObject()) {
                for (Map.Entry<String, JsonNode> field : node.properties()) {
                    String currentPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();
                    writeExcelNode(field.getValue(), currentPath, sheet, rowNum);
                }
            }
            else if (node.isArray()) {
                for (int i = 0; i < node.size(); i++) {
                    writeExcelNode(node.get(i), path +"[" + i + "]", sheet, rowNum);
                }
            }
            else{
                Row row = sheet.createRow(rowNum.getAndIncrement());
                row.createCell(0).setCellValue(path);
                row.createCell(1).setCellValue(node.isNull() ? "null" : node.asText());
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] createExcel(JsonNode root) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Report");
            AtomicInteger rowNum = new AtomicInteger(0);
            writeExcelNode(root, "", sheet, rowNum);
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
