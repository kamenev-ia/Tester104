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

import java.io.*;
import java.time.LocalDate;

public class ExcelConverter {
    public static ObservableList<DataModel> dataBaseTSData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTIData = FXCollections.observableArrayList();
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
            fillSubstationParam(file);
            dataBaseName = file.getName();
            fillDataBase(file);
        }
        dataBaseList.add(0, dataBaseTSData);
        dataBaseList.add(1, dataBaseTIData);
        return dataBaseList;
    }

    public ObservableList<ObservableList> openFile(File file) {
        if (file != null) {
            dataBaseTSData.clear();
            dataBaseTIData.clear();
            fillSubstationParam(file);
            dataBaseName = file.getName();
            fillDataBase(file);
        }
        dataBaseList.add(0, dataBaseTSData);
        dataBaseList.add(1, dataBaseTIData);
        return dataBaseList;
    }

    public void fillExcel(ObservableList<ObservableList> dataBaseModelObservableList) {
        try {
            ObservableList<DataModel> dataModelTS = dataBaseModelObservableList.get(0);
            ObservableList<DataModel> dataModelTI = dataBaseModelObservableList.get(1);
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            XSSFSheet sheetTS = workbook.getSheet("ТС");
            for (int i = 0; i < dataModelTS.size(); i++) {
                if (dataModelTS.get(i).getCheck().isSelected()) {
                    if (sheetTS.getRow(i + 1).getCell(13).getRichStringCellValue().getString().equals("")) {
                        sheetTS.getRow(i+1).getCell(13).setCellValue("Принято " + LocalDate.now());
                    }
                }
            }
            XSSFSheet sheetTI = workbook.getSheet("ТИ");
            for (int i = 0; i < dataModelTI.size(); i++) {
                if (dataModelTI.get(i).getCheck().isSelected()) {
                    if (sheetTI.getRow(i + 1).getCell(13).getRichStringCellValue().getString().equals("")) {
                        sheetTI.getRow(i+1).getCell(13).setCellValue("Принято " + LocalDate.now());
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
            XSSFSheet sheetTS = workbook.getSheet("ТС");
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
                        }
                    }
                }
            }
            XSSFSheet sheetTI = workbook.getSheet("ТИ");
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
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillSubstationParam(File file) {
        try {
            FileInputStream excelFile = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
            SubstationParamModel substationParamModel = new SubstationParamModel();
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
                substationParamModel.setSubstationNumberParam((int) sheet.getRow(2).getCell(2).getNumericCellValue() + "");
                substationParamModel.setSubstationTypeParam(sheet.getRow(2).getCell(1).getStringCellValue());
            }
            mainWindowController.setSubstationParamFromBD(substationParamModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDataBaseName() {
        return dataBaseName;
    }
}
