package org.openmuc.j60870.gui.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SubstationParamModel {
    private final StringProperty ipParam, substationNumberParam, substationAddressParam, substationTypeParam;
    private final IntegerProperty portParam;

    public SubstationParamModel() {
        this("", "", "", 0, "");
    }

    public SubstationParamModel(String ipParam, String substationNumberParam, String substationAddressParam, int portParam, String typeParam) {
        this.ipParam = new SimpleStringProperty(ipParam);
        this.substationNumberParam = new SimpleStringProperty(substationNumberParam);
        this.substationAddressParam = new SimpleStringProperty(substationAddressParam);
        this.portParam = new SimpleIntegerProperty(portParam);
        this.substationTypeParam = new SimpleStringProperty(typeParam);
    }

    public String getSubstationTypeParam() {
        return substationTypeParam.get();
    }

    public void setSubstationTypeParam(String typeParam) {
        this.substationTypeParam.set(typeParam);
    }

    public String getIpParam() {
        return ipParam.get();
    }

    public void setIpParam(String ipParam) {
        this.ipParam.set(ipParam);
    }

    public String getSubstationNumberParam() {
        return substationNumberParam.get();
    }

    public void setSubstationNumberParam(String substationNumberParam) {
        this.substationNumberParam.set(substationNumberParam);
    }

    public String getSubstationAddressParam() {
        return substationAddressParam.get();
    }

    public void setSubstationAddressParam(String substationAddressParam) {
        this.substationAddressParam.set(substationAddressParam);
    }

    public int getPortParam() {
        return portParam.get();
    }

    public void setPortParam(int portParam) {
        this.portParam.set(portParam);
    }
}
