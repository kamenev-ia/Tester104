package org.openmuc.j60870.gui.utilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.openmuc.j60870.internal.cli.AsduDate;

import java.util.concurrent.ExecutionException;

public class ConsolePrinter {
    private final AsduDate asduDate = new AsduDate();
    private static final Color SUCCESS_COLOR = Color.valueOf("#330066");
    private static final Color ERROR_COLOR = Color.valueOf("#881111");
    private static final Color INFO_COLOR = Color.LIGHTGRAY;


    public void printInfoMessage(ObservableList list, String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Text text = new Text(asduDate.getAsduDate() + " ________ " + string + "\n");
                    text.setFill(INFO_COLOR);
                    list.addAll(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void printErrorMessage(ObservableList list, String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Text text = new Text(asduDate.getAsduDate() + " ________ " + string + "\n");
                    text.setFill(ERROR_COLOR);
                    list.addAll(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void printSuccessMessage(ObservableList list, String string) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Text text = new Text(asduDate.getAsduDate() + " ________ " + string + "\n");
                    text.setFill(SUCCESS_COLOR);
                    list.addAll(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
