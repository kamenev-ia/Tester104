package org.openmuc.j60870.gui.controller;

import freemarker.template.Template;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.app.SubstationDataBase;
import org.openmuc.j60870.gui.utilities.DocumentHandler;

import java.io.*;
import java.util.List;
import java.util.Map;

public class ProtocolWindowController {
    private File file;
    @FXML
    Button createProtocolButton;

    @FXML
    private TextArea commentArea;

    @FXML
    private void onCreateProtocolButtonPressed() {
        Stage stage = (Stage) createProtocolButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документ Word", "*.doc")
        );
        file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            String comment = commentArea.getText();
            DocumentHandler dh = new DocumentHandler();
            Template t = dh.getTemplate();
            List<Map<String, Serializable>> list = SubstationDataBase.readTable(comment);
            try (Writer out = dh.getWriter(file.getPath())) {
                for (Map<String, Serializable> map : list) {
                    dh.createDoc(t, map, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage protocolStage = (Stage) createProtocolButton.getScene().getWindow();
            protocolStage.close();
        }
    }

    void setCurrentDataBase(String currentDataBase) {
    }

}

