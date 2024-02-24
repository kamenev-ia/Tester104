package org.openmuc.j60870.gui.utilities;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileManager {
    public void downloadExcelFileTo(String sourceFile) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документ Excel", "*.xlsm")
        );
        File destFile = fileChooser.showSaveDialog(stage);
        if (destFile != null) {
            try {
                InputStream is = getClass().getResourceAsStream(sourceFile);
                OutputStream os = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
