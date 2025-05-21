package org.openmuc.j60870.gui.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProtocolDataModel {
    private final StringProperty protTime, protType, protCause, protValue, protQuality, protTimeTag;
    private final IntegerProperty protAsdu, protAddress;
    private boolean newlyAdded = false;
    public ProtocolDataModel() {
        this(null, null, null, 0, 0, null, null, null);
    }

    public ProtocolDataModel(String protTime, String protType, String protCause, Integer protAsdu,
                             Integer protAddress, String protValue, String protQuality, String protTimeTag) {
        this.protTime = new SimpleStringProperty(protTime);
        this.protType = new SimpleStringProperty(protType);
        this.protCause = new SimpleStringProperty(protCause);
        this.protAsdu = new SimpleIntegerProperty(protAsdu);
        this.protAddress = new SimpleIntegerProperty(protAddress);
        this.protValue = new SimpleStringProperty(protValue);
        this.protQuality = new SimpleStringProperty(protQuality);
        this.protTimeTag = new SimpleStringProperty(protTimeTag);
    }

    public boolean isNewlyAdded() {
        return newlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        this.newlyAdded = newlyAdded;
    }

    public StringProperty protTimeProperty() {
        return protTime;
    }

    public void setProtTime(String protTime) {
        this.protTime.set(protTime);
    }

    public String getProtType() {
        return protType.get();
    }

    public StringProperty protTypeProperty() {
        return protType;
    }

    public void setProtType(String protType) {
        this.protType.set(protType);
    }

    public StringProperty protCauseProperty() {
        return protCause;
    }

    public void setProtCause(String protCause) {
        this.protCause.set(protCause);
    }

    public IntegerProperty protAsduProperty() {
        return protAsdu;
    }

    public void setProtAsdu(Integer protAsdu) {
        this.protAsdu.set(protAsdu);
    }

    public Integer getProtAddress() {
        return protAddress.get();
    }

    public IntegerProperty protAddressProperty() {
        return protAddress;
    }

    public void setProtAddress(Integer protAddress) {
        this.protAddress.set(protAddress);
    }

    public String getProtValue() {
        return protValue.get();
    }

    public StringProperty protValueProperty() {
        return protValue;
    }

    public void setProtValue(String protValue) {
        this.protValue.set(protValue);
    }

    public StringProperty protQualityProperty() {
        return protQuality;
    }

    public void setProtQuality(String protQuality) {
        this.protQuality.set(protQuality);
    }

    public StringProperty protTimeTagProperty() {
        return protTimeTag;
    }

    public void setProtTimeTag(String protTimeTag) {
        this.protTimeTag.set(protTimeTag);
    }
}
