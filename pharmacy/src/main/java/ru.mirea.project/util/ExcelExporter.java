package ru.mirea.project.util;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mirea.project.model.Booking;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {
    public static void export(List<Booking> bookings, String filename) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Bookings");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("User ID");
            header.createCell(2).setCellValue("Medicines");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Created At");

            int r = 1;
            for (Booking b : bookings) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(b.getId());
                row.createCell(1).setCellValue(b.getUserId());
                row.createCell(2).setCellValue(b.getMedicines());
                row.createCell(3).setCellValue(b.getStatus().getTitle());
                row.createCell(4).setCellValue(b.getCreatedAt().toString());
            }

            try (FileOutputStream fos = new FileOutputStream(filename)) {
                wb.write(fos);
            }
            System.out.println("Экспорт выполнен в файл: " + filename);
        }
    }
}