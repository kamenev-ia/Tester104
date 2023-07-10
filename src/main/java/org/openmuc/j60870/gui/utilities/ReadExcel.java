package org.openmuc.j60870.gui.utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadExcel {

    private final static CurrentDate currentDate = new CurrentDate();

    public static List<Map<String, java.io.Serializable>> readTable(String commentText, String currentDataBase) throws Exception {
        List<Map<String, java.io.Serializable>> list = new ArrayList<>();
        InputStream ips = new FileInputStream(currentDataBase);
        XSSFWorkbook wb = new XSSFWorkbook(ips);
        XSSFSheet sheet1 = wb.getSheetAt(0);
        XSSFSheet sheetTS = wb.getSheetAt(1);
        XSSFSheet sheetTI = wb.getSheetAt(2);
        XSSFSheet sheetTU = wb.getSheetAt(3);
        Row row = sheet1.getRow(2);
        Row equipRow = sheet1.getRow(5);
        Row ipRow = sheet1.getRow(8);

        //Подсчет количества ТС
        int spts = 0;
        int dpts = 0;
        for (Row currentRow : sheetTS) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 11) {
                    if (currentCell.getRowIndex() > 0) {
                        if (currentCell.getStringCellValue().equals("SPI(1,30)")) {
                            spts = spts + 1;
                        } else if (currentCell.getStringCellValue().equals("DPI(3,31)")) {
                            dpts = dpts + 1;
                        }
                    }
                }
            }
        }
        int ti = 0;
        for (Row currentRow : sheetTI) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 9) {
                    if (currentCell.getRowIndex() > 0) {
                        if (currentCell.getStringCellValue().equals("MFI (13,36)")) {
                            ti = ti + 1;
                        }
                    }
                }
            }
        }

        //Подсчет количества ТУ
        int tuka = 0;
        int tukvit = 0;
        int tuavr = 0;
        for (Row currentRow : sheetTU) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 5) {
                    if (currentCell.getRowIndex() > 0) {
                        switch (currentCell.getStringCellValue()) {
                            case "ТУ В":
                                tuka = tuka + 1;
                                break;
                            case "Квитирование РЗА":
                                tukvit = tukvit + 1;
                                break;
                            case "Вывод АВР":
                                tuavr = tuavr + 1;
                                break;
                        }
                    }
                }
            }
        }

        String object = row.getCell(1).getStringCellValue() + " " + (int)row.getCell(2).getNumericCellValue();
        String address = row.getCell(3).getStringCellValue();
        String equipment = equipRow.getCell(3).getStringCellValue();
        String region = row.getCell(0).getStringCellValue();
        String date = currentDate.getCurrentDate("dd.MM.yyyy");
        String ip = ipRow.getCell(4).getStringCellValue();

        Map<String, java.io.Serializable> dataMap = new HashMap<>();
        dataMap.put("object", object);
        dataMap.put("address", address);
        dataMap.put("equipment", equipment);
        dataMap.put("region", region);
        dataMap.put("date", date);
        dataMap.put("comment", commentText);
        dataMap.put("ipAddress", ip);
        dataMap.put("singlePoint", spts);
        dataMap.put("doublePoint", dpts);
        dataMap.put("ti", ti);
        dataMap.put("tuka", tuka);
        dataMap.put("tukvit", tukvit);
        dataMap.put("tuavr", tuavr);
        list.add(dataMap);
        return list;
    }
}
