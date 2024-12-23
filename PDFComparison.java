package com.testautomationguru.utility;

import com.testautomationguru.utility.PDFUtil;
import com.testautomationguru.utility.CompareMode;
import java.io.File;

public class PDFComparison {

    public static void main(String[] args) {
        String pdfFilePath1 = "C:\\Users\\vadde.a.kumar\\Downloads\\Sample1.pdf";
        String pdfFilePath2 = "C:\\Users\\vadde.a.kumar\\Downloads\\Sample2.pdf";
        String outputFolderPath = "C:\\Users\\vadde.a.kumar\\Downloads\\Images";

        File outputDir = new File(outputFolderPath);
        if (!outputDir.exists()) {
            if (outputDir.mkdir()) {
                System.out.println("Output folder created: " + outputFolderPath);
            } else {
                System.err.println("Failed to create output folder: " + outputFolderPath);
                return;
            }
        }

        try {
            PDFUtil pdfutilText = new PDFUtil();
            pdfutilText.setCompareMode(CompareMode.TEXT_MODE);
            System.out.println("Performing Text Comparison...");
            boolean textComparisonResult = pdfutilText.compare(pdfFilePath1, pdfFilePath2);
            System.out.println("Text Comparison Result: " + (textComparisonResult ? "Match" : "Mismatch"));

            PDFUtil pdfutilVisual = new PDFUtil();
            pdfutilVisual.setCompareMode(CompareMode.VISUAL_MODE);
            pdfutilVisual.highlightPdfDifference(true);
            pdfutilVisual.setImageDestinationPath(outputFolderPath); // Save visual differences in the output folder
            System.out.println("Performing Visual Comparison...");
            boolean visualComparisonResult = pdfutilVisual.compare(pdfFilePath1, pdfFilePath2);
            System.out.println("Visual Comparison Result: " + (visualComparisonResult ? "Match" : "Mismatch"));

            System.out.println("\n=== Comparison Summary ===");
            System.out.println("Text Comparison: " + (textComparisonResult ? "Match" : "Mismatch"));
            System.out.println("Visual Comparison: " + (visualComparisonResult ? "Match" : "Mismatch"));
            System.out.println("Highlighted images saved in: " + outputFolderPath);

            System.out.println("\n=== Files in Output Folder ===");
            File[] outputFiles = outputDir.listFiles();
            if (outputFiles != null && outputFiles.length > 0) {
                for (File file : outputFiles) {
                    System.out.println(" - " + file.getName());
                }
            } else {
                System.out.println("No files found in the output folder.");
            }

        } catch (Exception e) {
            System.err.println("Error during PDF comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
