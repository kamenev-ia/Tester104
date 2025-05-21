package org.openmuc.j60870.gui.customUI;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class CheckBoxPopupTableCell <S> extends TableCell<S, Integer> {
    private final Popup popup;
    private final VBox popupContent;
    private final CheckBox overflowCheckBox;
    private final CheckBox blockedCheckBox;
    private final CheckBox substitutedCheckBox;
    private final CheckBox notTopicalCheckBox;
    private final CheckBox invalidCheckBox;

    public CheckBoxPopupTableCell() {
        popup = new Popup();
        popupContent = new VBox(6);
        popupContent.setPadding(new Insets(10));
        popupContent.setStyle("-fx-background-color: #3c3c3c; -fx-border-color: black;");
        overflowCheckBox = new CheckBox("overflow");
        blockedCheckBox = new CheckBox("blocked");
        substitutedCheckBox = new CheckBox("substituted");
        notTopicalCheckBox = new CheckBox("notTopical");
        invalidCheckBox = new CheckBox("invalid");
        Button saveButton = new Button("Save");

        saveButton.setOnAction(event -> {
            int quality = 0;
            if (overflowCheckBox.isSelected()) {
                quality |= 0x01;
            }
            if (blockedCheckBox.isSelected()) {
                quality |= 0x10;
            }
            if (substitutedCheckBox.isSelected()) {
                quality |= 0x20;
            }
            if (notTopicalCheckBox.isSelected()) {
                quality |= 0x40;
            }
            if (invalidCheckBox.isSelected()) {
                quality |= 0x80;
            }
            int newValue = quality;
            commitEdit(newValue);
            popup.hide();
        });
        popupContent.getChildren().addAll(overflowCheckBox, blockedCheckBox, substitutedCheckBox, notTopicalCheckBox, invalidCheckBox, saveButton);
        popup.getContent().add(popupContent);
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            Bounds cellBounds = getBoundsInLocal();
            Bounds screenBounds = localToScreen(cellBounds);
            double popupX = screenBounds.getMaxX();
            double popupY = screenBounds.getMinY();
            popup.show(getScene().getWindow(), popupX, popupY);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        popup.hide();
    }

    @Override
    public void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
        } else {
            if (isEditing()) {
                setText(null);
            } else {
                setText(item.toString());
            }
        }
    }
}
