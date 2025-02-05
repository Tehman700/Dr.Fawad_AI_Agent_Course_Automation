package com.azure;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import javax.swing.*;
import java.io.*;
import java.awt.*;

// Created on 28-01-2025 at 6:10 AM by Tehman
public class MarkdownToPdfConverter {
    AutoShowingDialog obb = new AutoShowingDialog();

    // Modify the changerss method to accept the save path as a parameter
    public void changerss(String savePath) {
        String markdownFilePath = "Analysis Report.md";
        String pdfOutput = savePath + File.separator + "final_PDF_Report.pdf"; // Use the provided save path

        try {
            String md_file_content = readFile(markdownFilePath);
            String html_converter = convertmdtohtml(md_file_content);
            createPDFfromHTML(html_converter, pdfOutput);

            obb.showAutoCloseDialog("PDF FILE CREATED SUCCESSFULLY!!! " + pdfOutput, "Final Step Completed", JOptionPane.INFORMATION_MESSAGE);

            File pdfFile = new File(pdfOutput);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Error: PDF file not found!", "File Not Found", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }

    public String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public String convertmdtohtml(String markdown) {
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }

    public void createPDFfromHTML(String html, String pdfOutput) throws IOException, DocumentException {
        Document docs = new Document(PageSize.A4);
        PdfWriter.getInstance(docs, new FileOutputStream(pdfOutput));
        docs.open();

        // This will convert the HTML to PDF using HTMLWorker
        HTMLWorker tt = new HTMLWorker(docs);
        tt.parse(new StringReader(html));

        docs.close();
    }
}