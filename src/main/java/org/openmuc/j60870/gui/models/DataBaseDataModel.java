package org.openmuc.j60870.gui.models;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class DataBaseDataModel {
    private final StringProperty dbNameOfParamProperty, dbValueProperty, dbQualityProperty;
    private final IntegerProperty dbIoaProperty;
    private final CheckBox dbCheckBox;

    public DataBaseDataModel() {
        this("", 0, "", "", false);
    }

    public DataBaseDataModel(String dbNameOfParamProperty, Integer dbIoaProperty, String dbValueProperty, String dbQualityProperty, Boolean dbCheckBox) {
        this.dbNameOfParamProperty = new SimpleStringProperty(dbNameOfParamProperty);
        this.dbIoaProperty = new SimpleIntegerProperty(dbIoaProperty);
        this.dbValueProperty = new SimpleStringProperty(dbValueProperty);
        this.dbQualityProperty = new SimpleStringProperty(dbQualityProperty);
        this.dbCheckBox = new CheckBox();
        this.dbCheckBox.addEventHandler(MouseEvent.MOUSE_ENTERED,
                event -> {
                    if (event.isShiftDown()) {
                        getDbCheckBox().setSelected(true);
                    }
                    if (event.isControlDown()) {
                        getDbCheckBox().setSelected(false);
                    }
                });
    }

    public StringProperty dbNameOfParamProperty() {
        return dbNameOfParamProperty;
    }

    public void setDbNameOfParamProperty(String dbNameOfParamProperty) {
        this.dbNameOfParamProperty.set(dbNameOfParamProperty);
    }

    public String getDbValueProperty() {
        return dbValueProperty.get();
    }

    public StringProperty dbValueProperty() {
        return dbValueProperty;
    }

    public void setDbValueProperty(String dbValueProperty) {
        this.dbValueProperty.set(dbValueProperty);
    }

    public String getDbQualityProperty() {
        return dbQualityProperty.get();
    }

    public StringProperty dbQualityProperty() {
        return dbQualityProperty;
    }

    public void setDbQualityProperty(String dbQualityProperty) {
        this.dbQualityProperty.set(dbQualityProperty);
    }

    public int getDbIoaProperty() {
        return dbIoaProperty.get();
    }

    public IntegerProperty dbIoaProperty() {
        return dbIoaProperty;
    }

    public void setDbIoaProperty(int dbIoaProperty) {
        this.dbIoaProperty.set(dbIoaProperty);
    }

    public CheckBox getDbCheckBox() {
        return dbCheckBox;
    }
}
