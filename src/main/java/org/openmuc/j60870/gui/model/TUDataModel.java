package org.openmuc.j60870.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class TUDataModel extends DataModel{
    private final StringProperty nameOfParam;
    private final Button value;
    private final Button quality;
    private final IntegerProperty ioa;
    private final CheckBox check;

    public TUDataModel() {
        this("", 0, new Button(""), new Button(""), false);
    }

    public TUDataModel(String nameOfParam, Integer ioa, Button value, Button quality, Boolean check) {
        this.nameOfParam = new SimpleStringProperty(nameOfParam);
        this.ioa = new SimpleIntegerProperty(ioa);
        this.value = new Button();
        this.quality = new Button();
        this.check = new CheckBox();
    }

    public StringProperty nameOfParamProperty() {
        return nameOfParam;
    }

    public void setNameOfParam(String nameOfParam) {
        this.nameOfParam.set(nameOfParam);
    }

    public String getValue() {
        return value.getText();
    }

    public String getQuality() {
        return quality.getText();
    }

    public int getIoa() {
        return ioa.get();
    }

    public IntegerProperty ioaProperty() {
        return ioa;
    }

    public void setIoa(int ioa) {
        this.ioa.set(ioa);
    }

    public CheckBox getCheck() {
        return check;
    }
}
