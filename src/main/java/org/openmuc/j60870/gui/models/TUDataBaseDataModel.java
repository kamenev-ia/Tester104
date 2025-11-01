package org.openmuc.j60870.gui.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class TUDataBaseDataModel extends DataBaseDataModel {
    private final StringProperty tuDbNameOfParamProperty;
    private final Button tuDbValue;
    private final Button tuDbQuality;
    private final IntegerProperty tuDbIoaProperty;
    private final CheckBox tuDbCheckBox;

    public TUDataBaseDataModel() {
        this("", 0, new Button(""), new Button(""), false);
    }

    public TUDataBaseDataModel(String tuDbNameOfParamProperty, Integer tuDbIoaProperty, Button tuDbValue, Button tuDbQuality, Boolean tuDbCheckBox) {
        this.tuDbNameOfParamProperty = new SimpleStringProperty(tuDbNameOfParamProperty);
        this.tuDbIoaProperty = new SimpleIntegerProperty(tuDbIoaProperty);
        this.tuDbValue = new Button();
        this.tuDbQuality = new Button();
        this.tuDbCheckBox = new CheckBox();
    }

    public StringProperty dbNameOfParamProperty() {
        return tuDbNameOfParamProperty;
    }

    public void setDbNameOfParamProperty(String dbNameOfParamProperty) {
        this.tuDbNameOfParamProperty.set(dbNameOfParamProperty);
    }

    public String getDbValueProperty() {
        return tuDbValue.getText();
    }

    public String getDbQualityProperty() {
        return tuDbQuality.getText();
    }

    public int getDbIoaProperty() {
        return tuDbIoaProperty.get();
    }

    public IntegerProperty dbIoaProperty() {
        return tuDbIoaProperty;
    }

    public void setDbIoaProperty(int dbIoaProperty) {
        this.tuDbIoaProperty.set(dbIoaProperty);
    }

    public CheckBox getDbCheckBox() {
        return tuDbCheckBox;
    }
}
