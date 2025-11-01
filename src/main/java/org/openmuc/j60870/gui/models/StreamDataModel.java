package org.openmuc.j60870.gui.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StreamDataModel {
    private final StringProperty streamTimeProperty, streamTypeProperty, streamCauseProperty, streamValueProperty, streamQualityProperty, streamTimeTagProperty;
    private final IntegerProperty streamAsduProperty, streamAddressProperty;
    private boolean newlyAdded = false;
    public StreamDataModel() {
        this(null, null, null, 0, 0, null, null, null);
    }

    public StreamDataModel(String streamTimeProperty, String streamTypeProperty, String streamCauseProperty, Integer streamAsduProperty,
                           Integer streamAddressProperty, String streamValueProperty, String streamQualityProperty, String streamTimeTagProperty) {
        this.streamTimeProperty = new SimpleStringProperty(streamTimeProperty);
        this.streamTypeProperty = new SimpleStringProperty(streamTypeProperty);
        this.streamCauseProperty = new SimpleStringProperty(streamCauseProperty);
        this.streamAsduProperty = new SimpleIntegerProperty(streamAsduProperty);
        this.streamAddressProperty = new SimpleIntegerProperty(streamAddressProperty);
        this.streamValueProperty = new SimpleStringProperty(streamValueProperty);
        this.streamQualityProperty = new SimpleStringProperty(streamQualityProperty);
        this.streamTimeTagProperty = new SimpleStringProperty(streamTimeTagProperty);
    }

    public boolean isNewlyAdded() {
        return newlyAdded;
    }

    public void setNewlyAdded(boolean newlyAdded) {
        this.newlyAdded = newlyAdded;
    }

    public StringProperty streamTimeProperty() {
        return streamTimeProperty;
    }

    public void setStreamTimeProperty(String streamTimeProperty) {
        this.streamTimeProperty.set(streamTimeProperty);
    }

    public String getStreamTypeProperty() {
        return streamTypeProperty.get();
    }

    public StringProperty streamTypeProperty() {
        return streamTypeProperty;
    }

    public void setStreamTypeProperty(String streamTypeProperty) {
        this.streamTypeProperty.set(streamTypeProperty);
    }

    public StringProperty streamCauseProperty() {
        return streamCauseProperty;
    }

    public void setStreamCauseProperty(String streamCauseProperty) {
        this.streamCauseProperty.set(streamCauseProperty);
    }

    public IntegerProperty streamAsduProperty() {
        return streamAsduProperty;
    }

    public void setStreamAsduProperty(Integer streamAsduProperty) {
        this.streamAsduProperty.set(streamAsduProperty);
    }

    public Integer getStreamAddressProperty() {
        return streamAddressProperty.get();
    }

    public IntegerProperty streamAddressProperty() {
        return streamAddressProperty;
    }

    public void setStreamAddressProperty(Integer streamAddressProperty) {
        this.streamAddressProperty.set(streamAddressProperty);
    }

    public String getStreamValueProperty() {
        return streamValueProperty.get();
    }

    public StringProperty streamValueProperty() {
        return streamValueProperty;
    }

    public void setStreamValueProperty(String streamValueProperty) {
        this.streamValueProperty.set(streamValueProperty);
    }

    public StringProperty streamQualityProperty() {
        return streamQualityProperty;
    }

    public void setStreamQualityProperty(String streamQualityProperty) {
        this.streamQualityProperty.set(streamQualityProperty);
    }

    public StringProperty streamTimeTagProperty() {
        return streamTimeTagProperty;
    }

    public void setStreamTimeTagProperty(String streamTimeTagProperty) {
        this.streamTimeTagProperty.set(streamTimeTagProperty);
    }
}
