package com.testautomationguru.utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFToExcel {

    public static void main(String[] args) {
        String inputFilePath1 = "C:\\Users\\vadde.a.kumar\\Downloads\\Sample_target (3).pdf"; // First PDF File
        String inputFilePath2 = "C:\\Users\\vadde.a.kumar\\Downloads\\Sample_original (2).pdf"; // Second PDF File
        String outputFilePath = "C:\\Users\\vadde.a.kumar\\Downloads\\pdf-data.xlsx"; // Output Excel File

        File inputFile1 = new File(inputFilePath1);
        File inputFile2 = new File(inputFilePath2);

        if (!inputFile1.exists() || !inputFile1.canRead()) {
            System.err.println("Error: The first file does not exist or cannot be read: " + inputFilePath1);
            return;
        }
        if (!inputFile2.exists() || !inputFile2.canRead()) {
            System.err.println("Error: The second file does not exist or cannot be read: " + inputFilePath2);
            return;
        }

        try {
            String text1 = extractTextFromPDF(inputFilePath1);
            String text2 = extractTextFromPDF(inputFilePath2);

            List<String> lines1 = List.of(text1.split("\\r?\\n"));
            List<String> lines2 = List.of(text2.split("\\r?\\n"));

            Workbook outputWorkbook = new XSSFWorkbook();
            Sheet outputSheet = outputWorkbook.createSheet("PDF Data");

            Row headerRow = outputSheet.createRow(0);
            headerRow.createCell(0).setCellValue("Source (File 1)");
            headerRow.createCell(1).setCellValue("Destination (File 2)");
            headerRow.createCell(2).setCellValue("Difference");

            int outputRowIndex = 1; // Start writing from the second row
            int maxLines = Math.max(lines1.size(), lines2.size());
            int differenceCounter = 0; // Counter for differences

            for (int i = 0; i < maxLines; i++) {
                String line1 = i < lines1.size() ? lines1.get(i) : "";
                String line2 = i < lines2.size() ? lines2.get(i) : "";

                int diffCount = countAndWriteDifferences(line1, line2, outputWorkbook, outputSheet, outputRowIndex++);
                differenceCounter += diffCount; // Accumulate total differences
            }

            // Add total differences row
            Row totalDifferencesRow = outputSheet.createRow(outputRowIndex);
            totalDifferencesRow.createCell(0).setCellValue("Total Differences:");
            totalDifferencesRow.createCell(1).setCellValue(differenceCounter);

            outputSheet.autoSizeColumn(0);
            outputSheet.autoSizeColumn(1);
            outputSheet.autoSizeColumn(2);

            try (FileOutputStream fos = new FileOutputStream(new File(outputFilePath))) {
                outputWorkbook.write(fos);
            }

            System.out.println("Data written successfully to " + outputFilePath);
            System.out.println("Total Differences: " + differenceCounter);

        } catch (IOException e) {
            System.err.println("Error processing the PDF files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extracts text from a PDF file using Apache PDFBox.
     */
    private static String extractTextFromPDF(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    /**
     * Counts and writes differences between two strings to the sheet.
     */
    private static int countAndWriteDifferences(String source, String target, Workbook workbook, Sheet sheet, int rowIndex) {
        List<String> differences = new ArrayList<>();
        int diffCount = 0;

        String[] sourceWords = source.split(" ");
        String[] targetWords = target.split(" ");

        int maxLength = Math.max(sourceWords.length, targetWords.length);

        for (int i = 0; i < maxLength; i++) {
            String sourceWord = i < sourceWords.length ? sourceWords[i] : "";
            String targetWord = i < targetWords.length ? targetWords[i] : "";

            if (!sourceWord.equals(targetWord)) {
                if (!sourceWord.isEmpty() && (target.isEmpty() || !target.contains(sourceWord))) {
                    differences.add("-" + sourceWord);
                    diffCount++; // Increment for source difference
                }
                if (!targetWord.isEmpty() && (source.isEmpty() || !source.contains(targetWord))) {
                    differences.add("+" + targetWord);
                    diffCount++; // Increment for target difference
                }
            }
        }

        // Write differences to the sheet
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(source); // Source (File 1)
        row.createCell(1).setCellValue(target); // Destination (File 2)
        row.createCell(2).setCellValue(String.join(", ", differences)); // Difference column

        return diffCount;
    }
}
