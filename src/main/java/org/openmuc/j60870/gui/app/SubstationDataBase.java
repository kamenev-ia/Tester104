package org.openmuc.j60870.gui.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.controllers.MainWindowController;
import org.openmuc.j60870.gui.models.DataBaseDataModel;
import org.openmuc.j60870.gui.models.SubstationParamModel;
import java.io.*;
import java.util.List;
import java.util.Map;

public class SubstationDataBase {
    private static MainWindowController mainWindowController;
    private String dataBaseName;
    private static File currentFile;

    public SubstationDataBase(MainWindowController mainWindowController) {
        SubstationDataBase.mainWindowController = mainWindowController;
    }

    /**
     * Открывает диалог для выбора файла и загружает данные.
     *
     * @return Список списков данных (ТС, ТИ, ТУ)
     */
    public ObservableList<ObservableList<DataBaseDataModel>> openFile() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        currentFile = fileChooser.showOpenDialog(stage);
        return currentFile != null ? processFile(currentFile) : null;
    }

    public ObservableList<ObservableList<DataBaseDataModel>> openFile(File file) {
        return file != null ? processFile(file) : null;
    }

    /**
     * Загружает данные из файла.
     *
     * @param file Файл для обработки
     * @return Список списков данных
     */
    private ObservableList<ObservableList<DataBaseDataModel>> processFile(File file) {
        try {
            ExcelDataHandler excelHandler = new ExcelDataHandler(file);

            ObservableList<DataBaseDataModel> tsData = excelHandler.readSheetData("ТС", 9, 10, 13);
            ObservableList<DataBaseDataModel> tiData = excelHandler.readSheetData("ТИ", 7, 8, 13);
            ObservableList<DataBaseDataModel> tuData = excelHandler.readSheetData("ТУ", 6, 7, 9);

            SubstationParamModel substationParams = excelHandler.readSubstationParams();
            mainWindowController.setSubstationParamFromBD(substationParams);

            ObservableList<ObservableList<DataBaseDataModel>> dataBaseList = FXCollections.observableArrayList();
            dataBaseList.addAll(tsData, tiData, tuData);

            this.dataBaseName = file.getName();
            return dataBaseList;

        } catch (Exception e) {
            mainWindowController.printConsoleErrorMessage("Ошибка обработки файла: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Сохраняет данные обратно в файл.
     *
     * @param dataLists Список данных для записи
     */
    public void saveDataToFile(ObservableList<ObservableList<DataBaseDataModel>> dataLists) {
        if (currentFile != null) {
            try {
                ExcelDataHandler excelHandler = new ExcelDataHandler(currentFile);
                excelHandler.writeDataToFile(currentFile, dataLists.get(0), "ТС", 13);
                excelHandler.writeDataToFile(currentFile, dataLists.get(1), "ТИ", 13);
                excelHandler.writeDataToFile(currentFile, dataLists.get(2), "ТУ",9);
            } catch (IOException e) {
                mainWindowController.printConsoleErrorMessage("Ошибка записи в файл: " + e.getMessage());
            }
        } else {
            mainWindowController.printConsoleErrorMessage("Файл для сохранения не выбран.");
        }
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public static List<Map<String, Serializable>> readTable(String commentText) {
        if (currentFile != null) {
            try {
                ExcelDataHandler excelDataHandler = new ExcelDataHandler(currentFile);
                return excelDataHandler.readTable(commentText);
            } catch (IOException e) {
                mainWindowController.printConsoleErrorMessage("Ошибка обработки файла: " + e.getMessage());
                return null;
            }
        }
        return null;
    }
}
