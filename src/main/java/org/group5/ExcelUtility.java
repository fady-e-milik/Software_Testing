package org.group5;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelUtility {

    /**
     * Reads data from an Excel sheet and converts it into a 2D Object array for TestNG.
     * @param excelFilePath The path to the XLSX file.
     * @param sheetName The name of the sheet to read.
     * @return Object[][] containing the data rows (excluding header).
     */
    public static Object[][] getTestData(String excelFilePath, String sheetName) {
        Object[][] data = null;
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.err.println("Sheet not found: " + sheetName);
                return new Object[0][0];
            }

            int rowCount = sheet.getLastRowNum(); // Total rows excluding header
            if (rowCount < 1) return new Object[0][0];

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow != null ? headerRow.getLastCellNum() : 0;
            if (colCount == 0) return new Object[0][0];

            // Initialize array (Rows = total rows - header row)
            data = new Object[rowCount][colCount];

            // Iterate over data rows (starting from row index 1)
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    data[i - 1][j] = getCellValueAsString(cell);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + excelFilePath);
            e.printStackTrace();
        }
        return data;
    }

    // Helper method to ensure all cell types are read as Strings
    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Handle numeric data (e.g., telephone) as String, removing floating point part
                return String.valueOf((long)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate formula if necessary, or just return the formula string
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}