package org.openmuc.j60870.gui.controller;

import freemarker.template.Template;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.utilities.DocumentHandler;
import org.openmuc.j60870.gui.utilities.ReadExcel;
import java.io.*;
import java.util.List;
import java.util.Map;

public class ProtocolWindowController {
    private String currentDataBase;
    private File file;

    @FXML
    Button createProtocolButton;

    @FXML
    private TextArea commentArea;

    @FXML
    private void createProtocolButton() throws Exception{
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документ Word", "*.doc")
        );
        file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            String comment = commentArea.getText();
            DocumentHandler dh = new DocumentHandler();
            Template t = dh.getTemplate();
            List<Map<String, Serializable>> list = ReadExcel.readTable(comment, currentDataBase);
            list.forEach(map -> {
                Writer out = dh.getWriter(file.getPath());
                dh.createDoc(t, map, out);
            });
            Stage protocolStage = (Stage) createProtocolButton.getScene().getWindow();
            protocolStage.close();
        }
    }

    void setCurrentDataBase(String currentDataBase) {
        this.currentDataBase = currentDataBase;
    }

}
