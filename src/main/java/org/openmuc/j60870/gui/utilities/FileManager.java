package org.openmuc.j60870.gui.utilities;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileManager {
    public void downloadExcelFileTo(String sourceFile, Window parentWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Документ Excel", "*.xlsm")
        );
        File destFile = fileChooser.showSaveDialog(parentWindow);
        if (destFile != null) {
            try (InputStream is = getClass().getResourceAsStream(sourceFile);
                 OutputStream os = Files.newOutputStream(destFile.toPath())) {
                if (is == null) {
                    throw new IllegalArgumentException("Resource not found: " + sourceFile);
                }
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
