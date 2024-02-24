package org.openmuc.j60870.gui.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.openmuc.j60870.*;
import org.openmuc.j60870.gui.controller.MainWindowController;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.ProtocolDataModel;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.openmuc.j60870.ie.IeSingleCommand;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.ie.InformationObject;
import org.openmuc.j60870.internal.cli.AsduDate;
import org.openmuc.j60870.internal.cli.CliConParameter;
import static org.openmuc.j60870.ASduType.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;

public class Client104 {
    public Connection connection;
    public MainWindowController mainWindowController;
    public static ObservableList<ProtocolDataModel> protocolData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTSData = FXCollections.observableArrayList();
    public static ObservableList<DataModel> dataBaseTIData = FXCollections.observableArrayList();
    public static ObservableList<ProtocolDataModel> staticData = FXCollections.observableArrayList();

    public Client104(MainWindowController mwc) {
         mainWindowController = mwc;
    }

    public ClientConnectionBuilder createClientConnectionBuilder(InetAddress ipAddress, CliConParameter cliConParameter) {
        return new ClientConnectionBuilder(ipAddress)
                .setCommonAddressFieldLength(cliConParameter.getAsduLength())
                .setIoaFieldLength(cliConParameter.getIoaLength())
                .setPort(cliConParameter.getPort())
                .setMessageFragmentTimeout(cliConParameter.getMessageFragmentParameter())
                .setMaxTimeNoAckReceived(cliConParameter.getT1())
                .setMaxTimeNoAckSent(cliConParameter.getT2())
                .setMaxIdleTime(cliConParameter.getT3())
                .setMaxNumOfOutstandingIPdus(cliConParameter.getkParam())
                .setMaxUnconfirmedIPdusReceived(cliConParameter.getwParam());
    }

    public class ClientEventListener implements ConnectionEventListener {
        @Override
        public void newASdu(ASdu aSdu) {
            AsduDate asduDate = new AsduDate();
            int ioLength = aSdu.getInformationObjects().length;
            InformationObject io;
            for (int i = 0; i < ioLength; i++) {
                io = aSdu.getInformationObjects()[i];
                initData(aSdu, asduDate.getAsduDate(), io);
                if (mainWindowController.isGetToDataBase()) {
                    initDataBaseData(io);
                }
                if (mainWindowController.dataTab.isSelected()) {
                    initStaticData(aSdu, asduDate.getAsduDate(), io);
                }
            }
        }

        @Override
        public void connectionClosed(IOException e) {
            if (!e.getMessage().isEmpty()) {
                mainWindowController.printConsoleErrorMessage("Получен сигнал закрытия соединения. Причина: " + e.getMessage());
            }
            else {
                mainWindowController.printConsoleErrorMessage("Получен сигнал закрытия соединения. Причина неизвестна.");
            }
            mainWindowController.printConsoleInfoMessage("Соединение остановлено");
            mainWindowController.setIpFieldStyle(false);
        }
    }

    /**
     * Вызывается при получении нового ASDU. Заполняет модель {@link ProtocolDataModel} данными
     * из нового ASDU.
     *
     * @param aSdu Новый полученный ASDU
     * @param date Время получения нового ASDU
     * @param io Объект информации полученный в составе нового ASDU
     */
    public void initData(ASdu aSdu, String date, InformationObject io) {
        try {
            ProtocolDataModel protocolDataModel = new ProtocolDataModel();
            fillProtocolDataModel(protocolDataModel, aSdu, date, io);
            protocolData.add(protocolDataModel);
            if (mainWindowController.isGetToChart) {
                mainWindowController.addLineChartPoint(protocolDataModel);
            }
            mainWindowController.initialize(protocolData);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Вызывается при получении нового ASDU, если активна страница статичного отображения данных.
     * Если в таблице статичного отображения данных уже содержится полученный объект информации,
     * то обновляет его параметры. Если нет - добавляет новый объект в {@link ProtocolDataModel}
     *
     * @param aSdu Новый полученный ASDU
     * @param date Время нового полученного ASDU
     * @param io Объект информации полученный в составе нового ASDU
     */
    public void initStaticData(ASdu aSdu, String date, InformationObject io) {
        int sizeStaticData = staticData.size();
        boolean isContains = false;
        ASduType aType = aSdu.getTypeIdentification();
        if (aType == M_SP_TB_1 || aType == M_DP_TB_1 || aType == M_ME_TF_1 || aType == M_ME_NC_1 || aType == M_SP_NA_1 || aType == M_DP_NA_1
        ) {
            for (int i = 0; i < sizeStaticData; i++) {
                ProtocolDataModel pdm = staticData.get(i);
                if (pdm.getProtAddress() == io.getInformationObjectAddress() || staticData.isEmpty()) {
                    fillProtocolDataModel(pdm, aSdu, date, io);
                    isContains = true;
                }
                mainWindowController.initSingleStaticData(staticData);
            }
            if (!isContains) {
                try {
                    ProtocolDataModel protocolDataModel = new ProtocolDataModel();
                    fillProtocolDataModel(protocolDataModel, aSdu, date, io);
                    staticData.add(protocolDataModel);
                    mainWindowController.initStaticData(staticData);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Вызывается при получении нового ASDU.
     *
     * @param io Объект информации полученный в составе нового ASDU
     */
    public void initDataBaseData(InformationObject io) {
        dataBaseTSData = mainWindowController.dataBaseList.get(0);
        int sizeDataBaseTSData = dataBaseTSData.size();
        for (int i = 0; i < sizeDataBaseTSData; i++) {
            DataModel dbm = dataBaseTSData.get(i);
            if (dbm.getIoa() == io.getInformationObjectAddress()) {
                dbm.setValue(io.getInformationElements()[0][0].getValue());
                dbm.setQuality(io.getInformationElements()[0][0].getQuality());
            }
            if (mainWindowController.indexListDataBase == 0) {
                mainWindowController.initDataBase(dataBaseTSData);
            }
        }
        dataBaseTIData = mainWindowController.dataBaseList.get(1);
        int sizeDataBaseTIData = dataBaseTIData.size();
        for (int i = 0; i < sizeDataBaseTIData; i++) {
            DataModel dbmTi = dataBaseTIData.get(i);
            if (dbmTi.getIoa() == io.getInformationObjectAddress()) {
                dbmTi.setValue(io.getInformationElements()[0][0].getValue());
                dbmTi.setQuality(io.getInformationElements()[0][1].getQuality());
            }
            if (mainWindowController.indexListDataBase == 1) {
                mainWindowController.initDataBase(dataBaseTIData);
            }
        }
    }

    public void clearDataTable() {
        try {
            protocolData.clear();
            staticData.clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void fillProtocolDataModel(ProtocolDataModel protocolDataModel, ASdu aSdu, String date, InformationObject io) {
        protocolDataModel.setProtTime(date);
        protocolDataModel.setProtAsdu(aSdu.getCommonAddress());
        protocolDataModel.setProtCause(aSdu.getCauseOfTransmission().toString());
        protocolDataModel.setProtQuality(io.getInformationElements()[0][0].getQuality());
        protocolDataModel.setProtType(aSdu.getTypeIdentification().getId() + " (" +
                aSdu.getTypeIdentification().getDescription() + ")");
        if (aSdu.getTypeIdentification() == M_SP_TB_1 || aSdu.getTypeIdentification() == M_DP_TB_1
        ) {
            protocolDataModel.setProtTimeTag(io.getInformationElements()[0][1].getValue());
        } else {
            protocolDataModel.setProtTimeTag("");
        }
        if (aSdu.getTypeIdentification() == M_ME_NC_1 || aSdu.getTypeIdentification() == M_ME_TF_1) {
            protocolDataModel.setProtQuality(io.getInformationElements()[0][1].getQuality());
        }
        if (aSdu.getTypeIdentification() == M_ME_TF_1) {
            protocolDataModel.setProtTimeTag(io.getInformationElements()[0][2].getValue());
        }
        protocolDataModel.setProtValue(io.getInformationElements()[0][0].getValue());
        protocolDataModel.setProtAddress(io.getInformationObjectAddress());
    }

    public void createConnection(ClientConnectionBuilder clientConnectionBuilder) {
        try {
            connection = clientConnectionBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            mainWindowController.printConsoleErrorMessage("Указанный сервер недоступен. Проверьте подключение");
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> connection.close()));

            boolean connected = false;
            int retries = 1;
            int i = 1;

            while (!connected && i <= retries) {
                try {
                    mainWindowController.printConsoleInfoMessage("Отправка START_DT. Попытка № " + i);
                    connection.startDataTransfer(new ClientEventListener());
                } catch (InterruptedIOException e2) {
                    connection.close();
                    return;
                } catch (IOException e) {
                    mainWindowController.printConsoleErrorMessage("Подключение было закрыто. Причина: " + e.getMessage());
                    return;
                }
                connected = true;
            }
        }
    }

    public void closeConnection() {
        connection.close();
    }

    public boolean isConnectionExist() {
        return (connection != null);
    }

    public boolean isConnectionOpen() {
        if (connection != null) {
            return (!connection.isClosed() && !connection.isStopped());
        }
        return false;
    }

    public void sendGI(int commonAddress) {
        try {
            connection.interrogation(commonAddress, CauseOfTransmission.ACTIVATION,
                    new IeQualifierOfInterrogation(20));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendClockSynchronization(int commonAddress, IeTime56 ieTime56) {
        try {
            connection.synchronizeClocks(commonAddress, ieTime56);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTuCommand(int commonAddress, int addressTu, boolean select, boolean cmdStateOn) {
        try {
            connection.singleCommand(commonAddress, CauseOfTransmission.ACTIVATION, addressTu,
                    new IeSingleCommand(cmdStateOn, 0, select));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
