package org.openmuc.j60870.gui.utilities;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.openmuc.j60870.internal.cli.AsduDate;

public class ConsolePrinter {
    private final AsduDate asduDate = new AsduDate();
    private static final Color SUCCESS_COLOR = Color.valueOf("#330066");
    private static final Color ERROR_COLOR = Color.valueOf("#FF5500");
    private static final Color INFO_COLOR = Color.LIGHTGRAY;

    public void printInfoMessage(ObservableList<? extends Node> list, String string) {
        printMessage(INFO_COLOR, string, list);
    }

    public void printErrorMessage(ObservableList<? extends Node> list, String string) {
        printMessage(ERROR_COLOR, string, list);
    }

    public void printSuccessMessage(ObservableList<? extends Node> list, String string) {
        printMessage(SUCCESS_COLOR, string, list);
    }

    private void printMessage(Color textColor, String string, ObservableList list) {
        Platform.runLater(() -> {
            try {
                Text text = new Text(asduDate.getAsduDate() + " ________ " + string + "\n");
                text.setFill(textColor);
                list.addAll(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
