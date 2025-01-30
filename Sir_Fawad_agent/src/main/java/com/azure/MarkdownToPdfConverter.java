package com.azure;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

import java.io.*;


// Created on 28-01-2025 at 6:10 AM by Tehman
public class MarkdownToPdfConverter {

    public void changerss(){
        String markdownFilePath = "Analysis Report.md";
        String pdfOutput = "final_PDF_Report.pdf";

        try{
            String md_file_content = readFile(markdownFilePath);

            String html_converter = convertmdtohtml(md_file_content);


            createPDFfromHTML(html_converter,pdfOutput);

            System.out.println("PDF FILE CREATED SUCCESSFULLY!!!" + pdfOutput);

        }catch(IOException | DocumentException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
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

    public void createPDFfromHTML(String html, String pdfOutput) throws IOException, DocumentException{
        Document docs= new Document(PageSize.A4);
        PdfWriter.getInstance(docs, new FileOutputStream(pdfOutput));
        docs.open();


        // THis will convert the html to pdf using htmlworker

        HTMLWorker tt = new HTMLWorker(docs);
        tt.parse(new StringReader(html));

        docs.close();
    }





}


