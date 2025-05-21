package org.openmuc.j60870.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import org.openmuc.j60870.gui.commandShell.CommandExecutor;

public class CmdWindowController {
    @FXML
    private TextArea console;

    // позиция в тексте, с которой начинается пользовательский ввод
    private int promptPosition;

    // Логика работы с командами делегируется классу CommandExecutor
    private CommandExecutor executor;

    @FXML
    private void initialize() {
        executor = new CommandExecutor();

        console.setWrapText(true);
        console.setStyle("-fx-text-fill: rgba(255, 255, 255, 1); " +
                "-fx-font-size: 14px;");
        appendPrompt();

        // Не даём пользователю перемещаться в область, где уже есть вывод
        console.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() < promptPosition) {
                Platform.runLater(() -> console.positionCaret(console.getText().length()));
            }
        });

        console.setOnKeyPressed(event -> {
            // Обработка Ctrl+C для прерывания выполняемой команды
            if (event.isControlDown() && event.getCode() == KeyCode.C) {
                executor.cancelRunningCommand(
                        text -> Platform.runLater(() -> console.appendText(text))
                );
                event.consume();
                return;
            }
            // Обработка нажатия Enter – выполнение введенной команды
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                String currentText = console.getText();
                if (currentText.length() >= promptPosition) {
                    String command = currentText.substring(promptPosition).trim();
                    appendText("\n");
                    if (!command.isEmpty()) {
                        // Если по ошибке команда начинается с ">", отсекаем его
                        if (command.startsWith(">")) {
                            command = command.substring(2).trim();
                        }
                        executor.executeCommandAsync(
                                command,
                                text -> Platform.runLater(() -> console.appendText(text)),
                                () -> Platform.runLater(this::appendPrompt)
                        );
                    } else {
                        appendPrompt();
                    }
                }
            }
        });
    }

    /**
     * Добавляет приглашение "> " в конец TextArea и обновляет позицию ввода.
     */
    private void appendPrompt() {
        appendText("> ");
        promptPosition = console.getText().length();
    }

    /**
     * Добавляет текст в TextArea через UI-поток.
     *
     * @param text Текст для добавления.
     */
    private void appendText(String text) {
        Platform.runLater(() -> console.appendText(text));
    }
}
