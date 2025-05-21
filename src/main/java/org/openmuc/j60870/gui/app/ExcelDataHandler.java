package org.openmuc.j60870.gui.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.SubstationParamModel;
import org.openmuc.j60870.gui.substationBase.TIIOSheet;
import org.openmuc.j60870.gui.substationBase.TSIOSheet;
import org.openmuc.j60870.gui.substationBase.TUIOSheet;
import org.openmuc.j60870.gui.utilities.CurrentDate;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ExcelDataHandler {
    private final XSSFWorkbook workbook;
    private final Map<String, XSSFSheet> sheetsMap = new HashMap<>();
    private final CurrentDate currentDate = new CurrentDate();

    /**
     * Загружает Excel-файл и проверяет наличие обязательных листов.
     *
     * @param file Файл Excel
     * @throws IOException если файл не удаётся открыть
     */
    public ExcelDataHandler(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            workbook = new XSSFWorkbook(fis);
            List<String> requiredSheets = Collections.unmodifiableList(Arrays.asList("Оборудование", "ТС", "ТИ", "ТУ"));
            for (String sheetName : requiredSheets) {
                XSSFSheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Лист '" + sheetName + "' отсутствует в файле " + file.getName());
                }
                sheetsMap.put(sheetName, sheet);
            }
        }
    }

    /**
     * Читает данные из указанного листа Excel.
     *
     * @param sheetName      Название листа
     * @param nameCol        Индекс столбца с именами параметров
     * @param ioaCol         Индекс столбца с IOA (адресами объектов)
     * @param checkCol       Индекс столбца с флажками
     * @return Список моделей данных
     */
    public ObservableList<DataModel> readSheetData(String sheetName, int nameCol, int ioaCol, int checkCol) {
        ObservableList<DataModel> dataList = FXCollections.observableArrayList();
        XSSFSheet sheet = sheetsMap.get(sheetName);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // пропускаем заголовок
            DataModel model = new DataModel();
            Cell nameCell = row.getCell(nameCol);
            if (nameCell != null) {
                model.setNameOfParam(nameCell.getRichStringCellValue().getString());
            }
            Cell ioaCell = row.getCell(ioaCol);
            if (ioaCell != null) {
                switch (ioaCell.getCellType()) {
                    case STRING:
                        try {
                            model.setIoa(Integer.parseInt(ioaCell.getStringCellValue()));
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка преобразования строки в число: " + ioaCell.getStringCellValue());
                        }
                        break;
                    case NUMERIC:
                        model.setIoa((int) ioaCell.getNumericCellValue());
                        break;
                }
            }
            Cell checkCell = row.getCell(checkCol);
            if (checkCell != null) {
                model.getCheck().setSelected(!checkCell.getStringCellValue().isEmpty());
            }
            dataList.add(model);
        }
        return dataList;
    }

    /**
     * Записывает обновлённые данные обратно в Excel-файл.
     *
     * @param file       Файл Excel для записи
     * @param dataList  Модель данных (например, ТС, ТИ, ТУ)
     * @param sheetName  Название листа для обновления
     * @param checkColIndex Индекс столбца с флажками
     * @throws IOException если файл не удаётся записать
     */
    public void writeDataToFile(File file, ObservableList<DataModel> dataList, String sheetName, int checkColIndex) throws IOException {
        XSSFSheet sheet = sheetsMap.get(sheetName);

        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.getRow(i + 1); // пропускаем заголовок
            Cell checkCell = row.getCell(checkColIndex);

            if (dataList.get(i).getCheck().isSelected()) {
                checkCell.setCellValue(LocalDate.now().toString());
            } else {
                checkCell.setCellValue("");
            }
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
    }

    /**
     * Читает параметры подстанции из листа "Оборудование".
     *
     * @return Модель параметров подстанции
     */
    public SubstationParamModel readSubstationParams() {
        XSSFSheet equipmentSheet = sheetsMap.get("Оборудование");
        SubstationParamModel substationParam = new SubstationParamModel();
        Row row = equipmentSheet.getRow(2);
        for (Row currentRow :
                equipmentSheet) {
            for (Cell cell :
                    currentRow) {
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    switch (cell.getStringCellValue()) {
                        case "IP адрес":
                            substationParam.setIpParam(equipmentSheet.getRow(currentRow.getRowNum() + 1).getCell(cell.getColumnIndex()).getStringCellValue());
                            break;
                        case "Порт":
                            substationParam.setPortParam((int)equipmentSheet.getRow(currentRow.getRowNum() + 1).getCell(cell.getColumnIndex()).getNumericCellValue());
                            break;
                    }
                }
            }
        }
        substationParam.setSubstationAddressParam(row.getCell(3).getStringCellValue());
        substationParam.setSubstationTypeParam(row.getCell(1).getStringCellValue());
        substationParam.setSubstationNumberParam(String.valueOf((int) row.getCell(2).getNumericCellValue()));
        return substationParam;
    }

    public List<Map<String, Serializable>> readTable(String commentText) {
        XSSFSheet equipmentSheet = sheetsMap.get("Оборудование");
        TSIOSheet tsSheet = new TSIOSheet(sheetsMap.get("ТС"));
        TIIOSheet tiSheet = new TIIOSheet(sheetsMap.get("ТИ"));
        TUIOSheet tuSheet = new TUIOSheet(sheetsMap.get("ТУ"));

        List<Map<String, java.io.Serializable>> list = new ArrayList<>();
        Row row = equipmentSheet.getRow(2);
        Row equipRow = equipmentSheet.getRow(5);
        Row ipRow = equipmentSheet.getRow(8);

        int singlePointTSCount = tsSheet.getSinglePointTSCount();
        int doublePointTSCount = tsSheet.getDoublePointTSCount();
        int tiCount = tiSheet.getTiCount();
        int circuitBreakerControlCount = tuSheet.getCircuitBreakerControlCount();
        int confirmationControlCount = tuSheet.getConfirmationControlCount();
        int testControlCount = tuSheet.getTestControlCount();
        String kvit = confirmationControlCount + "";
        if (testControlCount > 0) {
            kvit = kvit + "+" + testControlCount + "(тестирование)" ;
        }
        int avrControlCount = tuSheet.getAvrControlCount();

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
        dataMap.put("singlePoint", singlePointTSCount);
        dataMap.put("doublePoint", doublePointTSCount);
        dataMap.put("ti", tiCount);
        dataMap.put("tuka", circuitBreakerControlCount);
        dataMap.put("tukvit", kvit);
        dataMap.put("tuavr", avrControlCount);
        list.add(dataMap);
        return list;
    }
}
