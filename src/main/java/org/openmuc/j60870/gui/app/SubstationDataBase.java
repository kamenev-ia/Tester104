package org.openmuc.j60870.gui.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.Nullable;
import org.openmuc.j60870.gui.controller.MainWindowController;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.SubstationParamModel;
import org.openmuc.j60870.gui.model.TUDataModel;
import org.openmuc.j60870.gui.substationBase.IOSheet;
import org.openmuc.j60870.gui.substationBase.TIIOSheet;
import org.openmuc.j60870.gui.substationBase.TSIOSheet;
import org.openmuc.j60870.gui.substationBase.TUIOSheet;
import org.openmuc.j60870.gui.utilities.CurrentDate;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubstationDataBase {
    private static final String EQUIPMENT_SHEET_NAME = "Оборудование";
    private static final String TS_SHEET_NAME = "ТС";
    private static final int TS_NAME_COLUMN_INDEX = 9;
    private static final int TS_IOA_COLUMN_INDEX = 10;
    private static final int TS_TYPE_COLUMN_INDEX = 11;
    private static final int TS_CHECK_COLUMN_INDEX = 13;
    private static final String TI_SHEET_NAME = "ТИ";
    private static final int TI_NAME_COLUMN_INDEX = 7;
    private static final int TI_IOA_COLUMN_INDEX = 8;
    private static final int TI_TYPE_COLUMN_INDEX = 9;
    private static final int TI_CHECK_COLUMN_INDEX = 13;
    private static final String TU_SHEET_NAME = "ТУ";
    private static final int TU_NAME_COLUMN_INDEX = 6;
    private static final int TU_IOA_COLUMN_INDEX = 7;
    private static final int TU_CHECK_COLUMN_INDEX = 9;
    public static ObservableList<DataModel> dataBaseTSData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTIData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTUData = FXCollections.observableArrayList();
    public static ObservableList<ObservableList<DataModel>> dataBaseList = FXCollections.observableArrayList();
    private String dataBaseName;
    public MainWindowController mainWindowController;
    public File file;
    private DataModel dataModel;
    private XSSFWorkbook dbWorkBook;
    private static XSSFSheet equipmentSheet;
    private static TSIOSheet tsSheet;
    private static TIIOSheet tiSheet;
    private static TUIOSheet tuSheet;
    private final List<String> sheetsNameList = new ArrayList<>();
    private final HashMap<String, XSSFSheet> sheetHashMap = new HashMap<>();

    public SubstationDataBase(MainWindowController mainWindowController) {
        sheetsNameList.add(EQUIPMENT_SHEET_NAME);
        sheetsNameList.add(TS_SHEET_NAME);
        sheetsNameList.add(TI_SHEET_NAME);
        sheetsNameList.add(TU_SHEET_NAME);
        this.mainWindowController = mainWindowController;
    }

    public ObservableList<ObservableList<DataModel>> openFile() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(stage);
        return openFileProcess(file);
    }

    public ObservableList<ObservableList<DataModel>> openFile(File file) {
        this.file = file;
        return openFileProcess(file);
    }

    private ObservableList<ObservableList<DataModel>> openFileProcess(File file) {
        if (checkInputBDFile(file)) {
            if (checkBD(dbWorkBook)) {
                dataBaseTSData.clear();
                dataBaseTIData.clear();
                dataBaseTUData.clear();
                fillSubstationParam();
                dataBaseName = file.getName();
                fillDataBase();
                dataBaseList.add(0, dataBaseTSData);
                dataBaseList.add(1, dataBaseTIData);
                dataBaseList.add(2, dataBaseTUData);
                return dataBaseList;
            }
        }
        return null;
    }

    public void fillOutputBDFile(ObservableList<ObservableList<DataModel>> dataBaseModelObservableList) {
        try {
            ObservableList<DataModel> dataModelTS = dataBaseModelObservableList.get(0);
            ObservableList<DataModel> dataModelTI = dataBaseModelObservableList.get(1);
            ObservableList<DataModel> dataModelTU = dataBaseModelObservableList.get(2);
            fillCheckedCell(dataModelTS, tsSheet, TS_CHECK_COLUMN_INDEX);
            fillCheckedCell(dataModelTI, tiSheet, TI_CHECK_COLUMN_INDEX);
            fillCheckedCell(dataModelTU, tuSheet, TU_CHECK_COLUMN_INDEX);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            dbWorkBook.write(fileOutputStream);
            dbWorkBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillCheckedCell(ObservableList<DataModel> ol, IOSheet dbSheet, int checkColumnIndex) {
        XSSFSheet sheet = dbSheet.getSheet();
        for (int i = 0; i < ol.size(); i++) {
            if (ol.get(i).getCheck().isSelected()) {
                if (sheet.getRow(i + 1).getCell(checkColumnIndex).getRichStringCellValue().getString().equals("")) {
                    sheet.getRow(i + 1).getCell(checkColumnIndex).setCellValue("Принято " + LocalDate.now());
                }
            } else {
                sheet.getRow(i + 1).getCell(checkColumnIndex).setCellValue("");
            }
        }
    }

    private void fillDataBase() {
        CheckBox cb = new CheckBox();
        cb.setSelected(true);
        TUDataModel tuDataModel = null;
        fillModel(tsSheet, dataBaseTSData);
        fillModel(tiSheet, dataBaseTIData);

        for (Row currentRow : tuSheet) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == TU_NAME_COLUMN_INDEX) {
                    String strValue = currentCell.getRichStringCellValue().getString();
                    tuDataModel = new TUDataModel();
                    tuDataModel.setNameOfParam(strValue);
                }
                if (currentCell.getColumnIndex() == TU_CHECK_COLUMN_INDEX) {
                    if (!currentCell.getRichStringCellValue().getString().equals("")) {
                        if (tuDataModel != null) {
                            tuDataModel.getCheck().setSelected(true);
                        }
                    }
                }
                fillIOA(tuDataModel, TU_IOA_COLUMN_INDEX, dataBaseTUData, TU_SHEET_NAME, currentCell);
            }
        }
    }

    private void fillModel(IOSheet dbSheet, ObservableList<DataModel> dataBaseData) {
        XSSFSheet sheet = dbSheet.getSheet();
        for (Row currentRow : sheet) {
            for (Cell currentCell : currentRow) {
                dataModel = getDataModel(currentCell, dbSheet.getNameColumnIndex(), dbSheet.getCheckColumnIndex());
                fillIOA(dataModel, dbSheet.getIOAColumnIndex(), dataBaseData, dbSheet.getDBSheetName(), currentCell);
            }
        }
    }

    private void fillIOA(DataModel dataModel, int ioaColumnIndex, ObservableList<DataModel> dataBaseData, String sheetName, Cell currentCell) {
        if (currentCell.getColumnIndex() == ioaColumnIndex) {
            switch (currentCell.getCellType()) {
                case NUMERIC:
                    int ioaString = (int) currentCell.getNumericCellValue();
                    if (dataModel != null) {
                        dataModel.setIoa(ioaString);
                    }
                    dataBaseData.add(dataModel);
                    break;
                case STRING:
                    if (currentCell.getRowIndex() > 1) {
                        printParsingBDError(sheetName, currentCell.getAddress().formatAsString());
                    }
                    break;
                default:
                    printParsingBDError(sheetName, currentCell.getAddress().formatAsString());
            }
        }
    }

    @Nullable
    private DataModel getDataModel(Cell currentCell, int nameColumnIndex, int checkColumnIndex) {
        if (currentCell.getColumnIndex() == nameColumnIndex) {
            String strValue = currentCell.getRichStringCellValue().getString();
            dataModel = new DataModel();
            dataModel.setNameOfParam(strValue);
        }
        if (currentCell.getColumnIndex() == checkColumnIndex) {
            if (!currentCell.getRichStringCellValue().getString().equals("")) {
                if (dataModel != null) {
                    dataModel.getCheck().setSelected(true);
                }
            }
        }
        return dataModel;
    }

    private void printParsingBDError(String sheetName, String cellIndex) {
        mainWindowController.printConsoleErrorMessage("Ошибка чтения БД. Некорректное значение. Лист: " + sheetName + ", ячейка: " + cellIndex);
    }

    private void fillSubstationParam() {
        SubstationParamModel substationParamModel = new SubstationParamModel();
            for (Row currentRow : equipmentSheet) {
                for (Cell currentCell : currentRow) {
                    if (currentCell.getCellType() == CellType.STRING) {
                        switch (currentCell.getStringCellValue()) {
                            case "IP адрес":
                                substationParamModel.setIpParam(equipmentSheet.getRow(currentRow.getRowNum() + 1).getCell(4).getStringCellValue());
                            case "Порт":
                                substationParamModel.setPortParam((int) equipmentSheet.getRow(currentRow.getRowNum() + 1).getCell(5).getNumericCellValue() + "");
                        }
                    }
                }
                substationParamModel.setSubstationAddressParam(equipmentSheet.getRow(2).getCell(3).getStringCellValue());
                if (equipmentSheet.getRow(2).getCell(2).getCellType() == CellType.NUMERIC) {
                    substationParamModel.setSubstationNumberParam((int) equipmentSheet.getRow(2).getCell(2).getNumericCellValue() + "");
                } else {
                    substationParamModel.setSubstationNumberParam(equipmentSheet.getRow(2).getCell(2).getStringCellValue());
                }
                substationParamModel.setSubstationTypeParam(equipmentSheet.getRow(2).getCell(1).getStringCellValue());
            }
        mainWindowController.setSubstationParamFromBD(substationParamModel);
    }

    private boolean checkInputBDFile(File file) {
        if (file != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                this.dbWorkBook = new XSSFWorkbook(fileInputStream);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                mainWindowController.printConsoleErrorMessage("Ошибка открытия файла " + file.getName());
                return false;
            }
        }
        mainWindowController.printConsoleErrorMessage("Файл БД не найден");
        return false;
    }

    private boolean checkBD(XSSFWorkbook workbook) {
        boolean isBDChecked = false;
        for (String sheetName :
                sheetsNameList) {
            if (checkBDSheet(sheetName)) {
                sheetHashMap.put(sheetName, workbook.getSheet(sheetName));
                isBDChecked = true;
            } else {
                return false;
            }
        }
        equipmentSheet = sheetHashMap.get(EQUIPMENT_SHEET_NAME);
        tsSheet = new TSIOSheet(sheetHashMap.get(TS_SHEET_NAME));
        tiSheet = new TIIOSheet(sheetHashMap.get(TI_SHEET_NAME));
        tuSheet = new TUIOSheet(sheetHashMap.get(TU_SHEET_NAME));
        return isBDChecked;
    }

    private boolean checkBDSheet(String sheetName) {
        if (dbWorkBook.getSheet(sheetName) != null) {
            return true;
        }
        mainWindowController.printConsoleErrorMessage("Ошибка чтения банка данных. Проверьте что лист \"" + sheetName + "\" не удален и не переименован");
        return false;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }


    //Заполнение протокола, подсчет сигналов в БД
    private final static CurrentDate currentDate = new CurrentDate();

    public static List<Map<String, Serializable>> readTable(String commentText){
        List<Map<String, java.io.Serializable>> list = new ArrayList<>();
        Row row = equipmentSheet.getRow(2);
        Row equipRow = equipmentSheet.getRow(5);
        Row ipRow = equipmentSheet.getRow(8);

        int spts = tsSheet.getSinglePointTSCount();
        int dpts = tsSheet.getDoublePointTSCount();
        int ti = tiSheet.getTiCount();

        int tuka = tuSheet.getCircuitBreakerControlCount();
        int tukvit = tuSheet.getConfirmationControlCount();
        int tuavr = tuSheet.getAvrControlCount();

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
