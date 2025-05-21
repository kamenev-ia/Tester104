package org.openmuc.j60870.gui.model;

import javafx.beans.property.SimpleIntegerProperty;

public class ServerModel {
    private final SimpleIntegerProperty aSduType, causeOfTransmission, commonAddress, ioa, quality, value;

    public ServerModel(int aSduType, int causeOfTransmission, int commonAddress, int ioa, int quality, int value) {
        this.aSduType = new SimpleIntegerProperty(aSduType);
        this.causeOfTransmission = new SimpleIntegerProperty(causeOfTransmission);
        this.commonAddress = new SimpleIntegerProperty(commonAddress);
        this.ioa = new SimpleIntegerProperty(ioa);
        this.quality = new SimpleIntegerProperty(quality);
        this.value = new SimpleIntegerProperty(value);
    }

    public int getaSduType() {
        return aSduType.get();
    }

    public SimpleIntegerProperty aSduTypeProperty() {
        return aSduType;
    }

    public void setASduType(int aSduType) {
        this.aSduType.set(aSduType);
    }

    public int getCauseOfTransmission() {
        return causeOfTransmission.get();
    }

    public SimpleIntegerProperty causeOfTransmissionProperty() {
        return causeOfTransmission;
    }

    public void setCauseOfTransmission(int causeOfTransmission) {
        this.causeOfTransmission.set(causeOfTransmission);
    }

    public int getCommonAddress() {
        return commonAddress.get();
    }

    public SimpleIntegerProperty commonAddressProperty() {
        return commonAddress;
    }

    public void setCommonAddress(int commonAddress) {
        this.commonAddress.set(commonAddress);
    }

    public int getIoa() {
        return ioa.get();
    }

    public SimpleIntegerProperty ioaProperty() {
        return ioa;
    }

    public void setIoa(int ioa) {
        this.ioa.set(ioa);
    }

    public int getQuality() {
        return quality.get();
    }

    public SimpleIntegerProperty qualityProperty() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality.set(quality);
    }

    public int getValue() {
        return value.get();
    }

    public SimpleIntegerProperty valueProperty() {
        return value;
    }

    public void setValue(int value) {
        this.value.set(value);
    }


}
