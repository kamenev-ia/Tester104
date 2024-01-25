package org.openmuc.j60870.gui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.utilities.License;

import java.io.IOException;

public class Gui extends Application {
    public Stage mainStage;
    private final License license = new License();

    @Override
    public void start(Stage startStage) {
        this.mainStage = startStage;
        showMainWindow();
    }

    public void showMainWindow() {
        if (license.isLicenseStatus()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/MainWindow.fxml"));
                AnchorPane connectPane = loader.load();
                Scene scene = new Scene(connectPane);
                mainStage.setScene(scene);
                mainStage.setResizable(true);
                mainStage.setTitle("TmTest");
                mainStage.setMaximized(true);
                mainStage.getScene().getStylesheets().add(getClass().getResource("/view/DarkThemeRoot.css").toExternalForm());
                mainStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Out of license");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
