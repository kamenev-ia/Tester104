package org.openmuc.j60870.gui.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class Gui extends Application {
    public Stage mainStage;

    @Override
    public void start(Stage startStage) {
        this.mainStage = startStage;
        showMainWindow();
    }

    public void showMainWindow() {
        if (LocalDate.now().getYear() <= 2030) {
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
