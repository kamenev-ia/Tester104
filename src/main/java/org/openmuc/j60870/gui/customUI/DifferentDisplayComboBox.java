package org.openmuc.j60870.gui.customUI;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

import java.util.List;

public class DifferentDisplayComboBox {
    public void createComboBox(ComboBox<Item> comboBox, List<Item> itemsList) {
        comboBox.getItems().addAll(itemsList);
        comboBox.setCellFactory(listView -> new ListCell<Item>(){
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Здесь отображаются данные при раскрытии списка
                    setText(item.getListText());
                }
            }
        });

        // Задаём кастомную кнопку (button cell) для отображения выбранного элемента.
        comboBox.setButtonCell(new ListCell<Item>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Здесь отображаются данные выбранного элемента
                    setText(item.getSelectedText());
                }
            }
        });
    }

    public static class Item{
        private final String listText;
        private final String selectedText;

        public Item(String listText, String selectedText) {
            this.listText = listText;
            this.selectedText = selectedText;
        }

        public String getListText() {
            return listText;
        }

        public String getSelectedText() {
            return selectedText;
        }
    }
}
