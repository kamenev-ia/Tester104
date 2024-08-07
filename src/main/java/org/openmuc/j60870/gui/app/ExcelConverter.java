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
import org.openmuc.j60870.gui.controller.MainWindowController;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.SubstationParamModel;
import org.openmuc.j60870.gui.model.TUDataModel;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelConverter {
    private static final String EQUIPMENT_SHEET_NAME = "Оборудование";
    private static final String TS_SHEET_NAME = "ТС";
    private static final String TI_SHEET_NAME = "ТИ";
    private static final String TU_SHEET_NAME = "ТУ";
    public static ObservableList<DataModel> dataBaseTSData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTIData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTUData = FXCollections.observableArrayList();
    public static ObservableList<ObservableList<DataModel>> dataBaseList = FXCollections.observableArrayList();
    private String dataBaseName;
    public MainWindowController mainWindowController;
    public File file;
    private XSSFWorkbook dbWorkBook;
    private XSSFSheet equipmentSheet, tsSheet, tiSheet, tuSheet;
    private final List<String> sheetsNameList = new ArrayList<>();
    private final HashMap<String, XSSFSheet> sheetHashMap = new HashMap<>();

    public ExcelConverter(MainWindowController mainWindowController) {
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
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheetTS = workbook.getSheet(TS_SHEET_NAME);
            XSSFSheet sheetTI = workbook.getSheet(TI_SHEET_NAME);
            XSSFSheet sheetTU = workbook.getSheet(TU_SHEET_NAME);
            fillCheckedCell(dataModelTS, sheetTS);
            fillCheckedCell(dataModelTI, sheetTI);
            fillCheckedCell(dataModelTU, sheetTU);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillCheckedCell(ObservableList<DataModel> ol, XSSFSheet sheet) {
        for (int i = 0; i < ol.size(); i++) {
            if (ol.get(i).getCheck().isSelected()) {
                if (sheet.getRow(i + 1).getCell(9).getRichStringCellValue().getString().equals("")) {
                    sheet.getRow(i + 1).getCell(9).setCellValue("Принято " + LocalDate.now());
                }
            }
        }
    }

    private void fillDataBase() {
        CheckBox cb = new CheckBox();
        cb.setSelected(true);
        DataModel dataModel = null;
        TUDataModel tuDataModel = null;
        for (Row currentRow : tsSheet) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 9) {
                    String strValue = currentCell.getRichStringCellValue().getString();
                    dataModel = new DataModel();
                    dataModel.setNameOfParam(strValue);
                }
                if (currentCell.getColumnIndex() == 13) {
                    if (!currentCell.getRichStringCellValue().getString().equals("")) {
                        if (dataModel != null) {
                            dataModel.getCheck().setSelected(true);
                        }
                    }
                }
                if (currentCell.getColumnIndex() == 10) {
                    switch (currentCell.getCellType()) {
                        case NUMERIC:
                            int ioaString = (int) currentCell.getNumericCellValue();
                            if (dataModel != null) {
                                dataModel.setIoa(ioaString);
                            }
                            dataBaseTSData.add(dataModel);
                            break;
                        case STRING:
                            if (currentCell.getRowIndex() > 1) {
                                printParsingBDError(TS_SHEET_NAME, "K" + (currentCell.getRowIndex() + 1));
                            }
                            break;
                        default:
                            printParsingBDError(TS_SHEET_NAME, "K" + (currentCell.getRowIndex() + 1));
                    }
                }
            }
        }

        for (Row currentRow : tiSheet) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 7) {
                    String strValue = currentCell.getRichStringCellValue().getString();
                    dataModel = new DataModel();
                    dataModel.setNameOfParam(strValue);
                }
                if (currentCell.getColumnIndex() == 13) {
                    if (!currentCell.getRichStringCellValue().getString().equals("")) {
                        if (dataModel != null) {
                            dataModel.getCheck().setSelected(true);
                        }
                    }
                }
                if (currentCell.getColumnIndex() == 8) {
                    switch (currentCell.getCellType()) {
                        case NUMERIC:
                            int ioaString = (int) currentCell.getNumericCellValue();
                            if (dataModel != null) {
                                dataModel.setIoa(ioaString);
                            }
                            dataBaseTIData.add(dataModel);
                            break;
                        case STRING:
                            break;
                        default:
                            printParsingBDError(TI_SHEET_NAME, "I" + (currentCell.getRowIndex() + 1));
                    }
                }
            }
        }

        for (Row currentRow : tuSheet) {
            for (Cell currentCell : currentRow) {
                if (currentCell.getColumnIndex() == 6) {
                    String strValue = currentCell.getRichStringCellValue().getString();
                    tuDataModel = new TUDataModel();
                    tuDataModel.setNameOfParam(strValue);
                }
                if (currentCell.getColumnIndex() == 9) {
                    if (!currentCell.getRichStringCellValue().getString().equals("")) {
                        if (tuDataModel != null) {
                            tuDataModel.getCheck().setSelected(true);
                        }
                    }
                }
                if (currentCell.getColumnIndex() == 7) {
                    switch (currentCell.getCellType()) {
                        case NUMERIC:
                            int ioaString = (int) currentCell.getNumericCellValue();
                            if (tuDataModel != null) {
                                tuDataModel.setIoa(ioaString);
                            }
                            dataBaseTUData.add(tuDataModel);
                            break;
                        case STRING:
                            break;
                        default:
                            printParsingBDError(TU_SHEET_NAME, "H" + (currentCell.getRowIndex() + 1));
                    }
                }
            }
        }
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
        tsSheet = sheetHashMap.get(TS_SHEET_NAME);
        tiSheet = sheetHashMap.get(TI_SHEET_NAME);
        tuSheet = sheetHashMap.get(TU_SHEET_NAME);
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
}
