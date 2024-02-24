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

public class ExcelConverter {
    public static ObservableList<DataModel> dataBaseTSData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTIData = FXCollections.observableArrayList();
    public static ObservableList<TUDataModel> dataBaseTUData = FXCollections.observableArrayList();
    public static ObservableList<ObservableList> dataBaseList = FXCollections.observableArrayList();
    private String dataBaseName;
    public MainWindowController mainWindowController;
    public File file;

    public ExcelConverter(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    public ObservableList<ObservableList> openFile() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            dataBaseTSData.clear();
            dataBaseTIData.clear();
            dataBaseTUData.clear();
            fillSubstationParam(file);
            dataBaseName = file.getName();
            fillDataBase(file);
            dataBaseList.add(0, dataBaseTSData);
            dataBaseList.add(1, dataBaseTIData);
            dataBaseList.add(2, dataBaseTUData);
            return dataBaseList;
        }
        return null;
    }

    public ObservableList<ObservableList> openFile(File file) {
        if (file != null) {
            dataBaseTSData.clear();
            dataBaseTIData.clear();
            dataBaseTUData.clear();
            fillSubstationParam(file);
            dataBaseName = file.getName();
            fillDataBase(file);
        }
        dataBaseList.add(0, dataBaseTSData);
        dataBaseList.add(1, dataBaseTIData);
        dataBaseList.add(2, dataBaseTUData);
        return dataBaseList;
    }

    public void fillOutputBDFile(ObservableList<ObservableList> dataBaseModelObservableList) {
        try {
            ObservableList<DataModel> dataModelTS = dataBaseModelObservableList.get(0);
            ObservableList<DataModel> dataModelTI = dataBaseModelObservableList.get(1);
            ObservableList<TUDataModel> dataModelTU = dataBaseModelObservableList.get(2);
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheetTS = workbook.getSheet("ТС");
            for (int i = 0; i < dataModelTS.size(); i++) {
                if (dataModelTS.get(i).getCheck().isSelected()) {
                    if (sheetTS.getRow(i + 1).getCell(13).getRichStringCellValue().getString().equals("")) {
                        sheetTS.getRow(i + 1).getCell(13).setCellValue("Принято " + LocalDate.now());
                    }
                }
            }
            XSSFSheet sheetTI = workbook.getSheet("ТИ");
            for (int i = 0; i < dataModelTI.size(); i++) {
                if (dataModelTI.get(i).getCheck().isSelected()) {
                    if (sheetTI.getRow(i + 1).getCell(13).getRichStringCellValue().getString().equals("")) {
                        sheetTI.getRow(i + 1).getCell(13).setCellValue("Принято " + LocalDate.now());
                    }
                }
            }
            XSSFSheet sheetTU = workbook.getSheet("ТУ");
            for (int i = 0; i < dataModelTU.size(); i++) {
                if (dataModelTU.get(i).getCheck().isSelected()) {
                    if (sheetTU.getRow(i + 1).getCell(9).getRichStringCellValue().getString().equals("")) {
                        sheetTU.getRow(i+1).getCell(9).setCellValue("Принято " + LocalDate.now());
                    }
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillDataBase(File file) {
        CheckBox cb = new CheckBox();
        cb.setSelected(true);
        try {
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            DataModel dataModel = null;
            TUDataModel tuDataModel = null;
            XSSFSheet sheetTS = workbook.getSheet("ТС");
            if (sheetTS != null) {
                for (Row currentRow : sheetTS) {
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
                                    break;
                                default:
                                    printParsingBDError("ТС", "K" + (currentCell.getRowIndex() + 1));
                            }
                        }
                    }
                }
            } else mainWindowController.printConsoleErrorMessage("Ошибка чтения банка данных. Проверьте что лист \"ТC\" не удален и не переименован");

            XSSFSheet sheetTI = workbook.getSheet("ТИ");
            if (sheetTI != null) {
                for (Row currentRow : sheetTI) {
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
                                    printParsingBDError("ТИ", "I" + (currentCell.getRowIndex() + 1));
                            }
                        }
                    }
                }
            } else mainWindowController.printConsoleErrorMessage("Ошибка чтения банка данных. Проверьте что лист \"ТИ\" не удален и не переименован");

            XSSFSheet sheetTU = workbook.getSheet("ТУ");
            if (sheetTU != null) {
                for (Row currentRow : sheetTU) {
                    for (Cell currentCell : currentRow) {
                        if (currentCell.getColumnIndex() == 6) {
                            String strValue = currentCell.getRichStringCellValue().getString();
                            System.out.println(currentCell.getRichStringCellValue().getString());
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
                                    printParsingBDError("ТУ", "H" + (currentCell.getRowIndex() + 1));
                            }
                        }
                    }
                }
            } else mainWindowController.printConsoleErrorMessage("Ошибка чтения банка данных. Проверьте что лист \"ТУ\" не удален и не переименован");
        } catch (IOException e) {
            e.printStackTrace();
            mainWindowController.printConsoleErrorMessage("Ошибка чтения БД");
        }
    }

    private void printParsingBDError(String sheetName, String cellIndex) {
        mainWindowController.printConsoleErrorMessage("Ошибка чтения БД. Некорректное значение. Лист: " + sheetName + ", ячейка: " + cellIndex);
    }

    private void fillSubstationParam(File file) {
        try {
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            SubstationParamModel substationParamModel = new SubstationParamModel();
            if (workbook.getSheet("Оборудование") != null) {
                XSSFSheet sheet = workbook.getSheet("Оборудование");
                for (Row currentRow : sheet) {
                    for (Cell currentCell : currentRow) {
                        if (currentCell.getCellType() == CellType.STRING) {
                            switch (currentCell.getStringCellValue()) {
                                case "IP адрес":
                                    substationParamModel.setIpParam(sheet.getRow(currentRow.getRowNum() + 1).getCell(4).getStringCellValue());
                                case "Порт":
                                    substationParamModel.setPortParam((int) sheet.getRow(currentRow.getRowNum() + 1).getCell(5).getNumericCellValue() + "");
                            }
                        }
                    }
                    substationParamModel.setSubstationAddressParam(sheet.getRow(2).getCell(3).getStringCellValue());
                    if (sheet.getRow(2).getCell(2).getCellType() == CellType.NUMERIC) {
                        substationParamModel.setSubstationNumberParam((int) sheet.getRow(2).getCell(2).getNumericCellValue() + "");
                    } else {
                        substationParamModel.setSubstationNumberParam(sheet.getRow(2).getCell(2).getStringCellValue());
                    }
                    substationParamModel.setSubstationTypeParam(sheet.getRow(2).getCell(1).getStringCellValue());
                }
            } else {
                mainWindowController.printConsoleErrorMessage("Ошибка чтения банка данных. Проверьте что лист \"Оборудование\" не удален и не переименован");
            }
            mainWindowController.setSubstationParamFromBD(substationParamModel);
        } catch (IOException e) {
            e.printStackTrace();
            mainWindowController.printConsoleErrorMessage("Ошибка чтения БД");
        }
    }

    public String getDataBaseName() {
        return dataBaseName;
    }
}
