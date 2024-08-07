package org.openmuc.j60870.gui.model;

import javafx.beans.property.*;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;

public class DataModel {
    private final StringProperty nameOfParam, value, quality;
    private final IntegerProperty ioa;
    private final CheckBox check;

    public DataModel() {
        this("", 0, "", "", false);
    }

    public DataModel(String nameOfParam, Integer ioa, String value, String quality, Boolean check) {
        this.nameOfParam = new SimpleStringProperty(nameOfParam);
        this.ioa = new SimpleIntegerProperty(ioa);
        this.value = new SimpleStringProperty(value);
        this.quality = new SimpleStringProperty(quality);
        this.check = new CheckBox();
        this.check.addEventHandler(MouseEvent.MOUSE_ENTERED,
                event -> {
                    if (event.isShiftDown()) {
                        getCheck().setSelected(true);
                    }
                    if (event.isControlDown()) {
                        getCheck().setSelected(false);
                    }
                });
    }

    public StringProperty nameOfParamProperty() {
        return nameOfParam;
    }

    public void setNameOfParam(String nameOfParam) {
        this.nameOfParam.set(nameOfParam);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getQuality() {
        return quality.get();
    }

    public StringProperty qualityProperty() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality.set(quality);
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
