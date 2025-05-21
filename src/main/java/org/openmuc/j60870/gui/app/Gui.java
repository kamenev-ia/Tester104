package org.openmuc.j60870.gui.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.utilities.License;
import java.io.IOException;
import java.net.URL;

public class Gui extends Application {
    private Stage mainStage;
    private final License license = new License();
    private static final String APP_TITLE = "TmTest";

    @Override
    public void start(Stage startStage) {
        this.mainStage = startStage;
        showMainWindow();
    }

    public void showMainWindow() {
        if (!license.isLicenseStatus()) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка лицензии");
                alert.setHeaderText("Срок действия лицензии истёк");
                alert.setContentText("Пожалуйста, обновите лицензию");
                System.exit(1);
            });
            return;
        }
         try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MainWindow.fxml"));
            AnchorPane connectPane = loader.load();
            Scene scene = new Scene(connectPane);
            mainStage.setScene(scene);
            mainStage.setResizable(true);
            mainStage.setTitle(APP_TITLE);
            mainStage.setMaximized(true);
             URL cssUrl = getClass().getResource("/view/DarkThemeRoot.css");
             if (cssUrl == null) {
                 System.err.println("Ошибка: CSS-ресурс '/view/DarkThemeRoot.css' не найден");
             } else {
                 scene.getStylesheets().add(cssUrl.toExternalForm());
             }
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
