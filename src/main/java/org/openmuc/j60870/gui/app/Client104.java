package org.openmuc.j60870.gui.app;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.EnumSet;

public class Client104 {

    private final BooleanProperty addToChart = new SimpleBooleanProperty(false);
    public Connection connection;
    public MainWindowController mainWindowController;
    public ObservableList<ProtocolDataModel> protocolData = FXCollections.observableArrayList();
    public ObservableList<ProtocolDataModel> staticData = FXCollections.observableArrayList();

    public Client104(MainWindowController mwc) {
         mainWindowController = mwc;
         addToChart.bindBidirectional(mainWindowController.isGetToChartProperty());
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
                if (mainWindowController.getDataTab().isSelected()) {
                    initStaticData(aSdu, asduDate.getAsduDate(), io);
                }
            }
        }

        @Override
        public void connectionClosed(IOException e) {
            Platform.runLater(() -> {
                if (!e.getMessage().isEmpty()) {
                    mainWindowController.printConsoleErrorMessage("Получен сигнал закрытия соединения. Причина: " + e.getMessage());
                }
                else {
                    mainWindowController.printConsoleErrorMessage("Получен сигнал закрытия соединения. Причина неизвестна.");
                }
                mainWindowController.printConsoleInfoMessage("Соединение остановлено");
                mainWindowController.setIpFieldStyle(false);
            });
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
            final ProtocolDataModel protocolDataModel = new ProtocolDataModel();
            fillProtocolDataModel(protocolDataModel, aSdu, date, io);
            Platform.runLater(() ->{
                protocolData.add(protocolDataModel);
                if (isAddToChart()) {
                    mainWindowController.addLineChartPoint(protocolDataModel);
                }
                mainWindowController.initialize(protocolData);
            });
        } catch (NullPointerException e) {
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
        }
    }

    public void initStaticData(ASdu aSdu, String date, InformationObject io) {
        if (EnumSet.of(M_SP_TB_1, M_DP_TB_1, M_ME_TF_1, M_ME_NC_1, M_SP_NA_1, M_DP_NA_1).contains(aSdu.getTypeIdentification())) {
            ProtocolDataModel pdm = findStaticDataByAddress(io.getInformationObjectAddress());
            if (pdm != null) {
                // Найдена существующая запись – обновляем её
                fillProtocolDataModel(pdm, aSdu, date, io);
            } else {
                // Если не найдена – создаём новую запись и помечаем её как новую
                try {
                    ProtocolDataModel newModel = new ProtocolDataModel();
                    newModel.setNewlyAdded(true);
                    fillProtocolDataModel(newModel, aSdu, date, io);
                    staticData.add(newModel);
                } catch (NullPointerException e) {
                    mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
                }
            }
            // После завершения обработки обновляем UI
            Platform.runLater(() -> mainWindowController.initStaticData(staticData));
        }
    }
    private ProtocolDataModel findStaticDataByAddress(int address) {
        for (ProtocolDataModel pdm : staticData) {
            if (pdm.getProtAddress() == address) {
                return pdm;
            }
        }
        return null;
    }

    /**
     * Вызывается при получении нового ASDU.
     *
     * @param io Объект информации полученный в составе нового ASDU
     */
    public void initDataBaseData(InformationObject io) {

        // Обновление для TS-данных (предположим, индексы для TS: value = 0, quality = 0, targetIndex = 0)
        ObservableList<DataModel> tsList = mainWindowController.getDataBaseList().get(0);
        updateDatabaseModels(tsList, io, 0, 0, 0);

        // Обновление для TI-данных (например, value = 0, quality = 1, targetIndex = 1)
        ObservableList<DataModel> tiList = mainWindowController.getDataBaseList().get(1);
        updateDatabaseModels(tiList, io, 1, 0, 1);
    }

    private void updateDatabaseModels(ObservableList<DataModel> dataList, InformationObject io,
                                      int qualityIndex, int valueIndex, int targetIndex) {
        // targetIndex – индекс списка, по которому потом будет вызван UI-метод обновления
        for (DataModel model : dataList) {
            if (model.getIoa() == io.getInformationObjectAddress()) {
                // Обновляем значение и качество в зависимости от индексов
                model.setValue(io.getInformationElements()[0][valueIndex].getValue());
                model.setQuality(io.getInformationElements()[0][qualityIndex].getQuality());
            }
        }

        Platform.runLater(() -> {
            if (mainWindowController.getIndexListDataBase() == targetIndex) {
                mainWindowController.initDataBase(dataList);
            }
        });
    }


    public void clearDataTable() {
        try {
            protocolData.clear();
            staticData.clear();
        } catch (NullPointerException e) {
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
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

//    public void createConnection(ClientConnectionBuilder clientConnectionBuilder) {
//        final int MAX_ATTEMPTS = 5;
//        final int RETRY_DELAY_MS = 2000;
//        int attempt = 1;
//
//        closeConnection();
//
//        try {
//            connection = clientConnectionBuilder.build();
//        } catch (IOException e) {
//            Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
//                    "Ошибка создания соединения: " + e.getMessage()));
//            return;
//        }
//
//        if (connection == null) {
//            Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
//                    "Указанный сервер недоступен. Проверьте подключение"));
//        } else {
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> connection.close()));
//
//            boolean connected = false;
//            int i = 1;
//            while (!connected && attempt < MAX_ATTEMPTS) {
//                try {
//                    Platform.runLater(() -> mainWindowController.printConsoleInfoMessage("Отправка START_DT. Попытка № " + i));
//                    connection.startDataTransfer(new ClientEventListener());
//                } catch (InterruptedIOException e2) {
//                    connection.close();
//                    return;
//                } catch (IOException e) {
//                    attempt++;
//                    Platform.runLater(() -> mainWindowController.printConsoleErrorMessage("Подключение было закрыто. Причина: " + e.getMessage()));
//                    return;
//                }
//                connected = true;
//            }
//        }
//    }

    public void createConnection(ClientConnectionBuilder clientConnectionBuilder) {
        final int MAX_ATTEMPTS = 3; // Максимальное количество попыток
        final int RETRY_DELAY_MS = 2000; // Задержка между попытками в миллисекундах
        int attempt = 1;

        // Закрываем предыдущее соединение, если оно существует
        if (connection != null) {
            closeConnection();
        }

        try {
            connection = clientConnectionBuilder.build();
        }
        catch (IOException e) {
            Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
                    "Ошибка создания соединения: " + e.getMessage()));
            return;
        }

        if (connection == null) {
            Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
                    "Соединение не было создано"));
            return;
        }

        boolean connected = false;
        while (!connected && attempt <= MAX_ATTEMPTS) {
            try {
                int finalAttempt = attempt;
                Platform.runLater(() -> mainWindowController.printConsoleInfoMessage(
                        "Попытка подключения №" + finalAttempt));

                connection.startDataTransfer(new ClientEventListener());
                connected = true;

                Platform.runLater(() -> {
                    mainWindowController.printConsoleInfoMessage("Подключение установлено");
                    mainWindowController.setIpFieldStyle(true);
                });
            }
            catch (InterruptedIOException e) {
                Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
                        "Подключение прервано"));
                closeConnection();
                return;
            }
            catch (IOException e) {
                if (attempt++ < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    }
                    catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
                                "Процесс подключения прерван"));
                        closeConnection();
                        return;
                    }
                }
            }
        }

        if (!connected) {
            Platform.runLater(() -> mainWindowController.printConsoleErrorMessage(
                    "Не удалось подключиться после " + MAX_ATTEMPTS + " попыток"));
            closeConnection();
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
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
        }
    }

    public void sendClockSynchronization(int commonAddress, IeTime56 ieTime56) {
        try {
            connection.synchronizeClocks(commonAddress, ieTime56);
        } catch (IOException e) {
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
        }
    }

    public void sendTuCommand(int commonAddress, int addressTu, boolean select, boolean cmdStateOn) {
        try {
            connection.singleCommand(commonAddress, CauseOfTransmission.ACTIVATION, addressTu,
                    new IeSingleCommand(cmdStateOn, 0, select));
        } catch (IOException e) {
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
        }
    }
    public BooleanProperty addToChartProperty() {
        return addToChart;
    }
    public boolean isAddToChart() {
        return addToChart.get();
    }

    public void setAddToChart(boolean value) {
        addToChart.set(value);
    }
}
