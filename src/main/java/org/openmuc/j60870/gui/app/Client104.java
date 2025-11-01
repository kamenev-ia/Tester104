package org.openmuc.j60870.gui.app;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.openmuc.j60870.*;
import org.openmuc.j60870.gui.controllers.MainWindowController;
import org.openmuc.j60870.gui.models.DataBaseDataModel;
import org.openmuc.j60870.gui.models.StreamDataModel;
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
import java.util.HashMap;
import java.util.Map;

public class Client104 {

    private final BooleanProperty addToChart = new SimpleBooleanProperty(false);
    public Connection connection;
    public MainWindowController mainWindowController;
    public ObservableList<StreamDataModel> streamData = FXCollections.observableArrayList();
    public ObservableList<StreamDataModel> staticData = FXCollections.observableArrayList();

    //HashMap - копия staticData для ускорения поиска и определения уникальности новых ASDU
    private final Map<Integer, StreamDataModel> staticDataMap = new HashMap<>();

    /**
     * Конструктор с контроллером главного окна
     * @param mwc
     *          Контроллер главного окна
     */
    public Client104(MainWindowController mwc) {
         mainWindowController = mwc;
         addToChart.bindBidirectional(mainWindowController.isGetToChartProperty());
    }

    /**
     * Создание и конфигурирование билдера клиентского соединения
     *
     * @param ipAddress
     *              IP-адрес сервера, с которым устанавливается соединение
     * @param cliConParameter
     *              Объект с параметрами для соединения по IEC-104
     * @return
     *              Настроенный экземпляр {@link ClientConnectionBuilder}
     */
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

    /**
     * Обработчик событий клиентского соединения по протоколу IEC-104.
     * Класс реализует интерфейс {@link ConnectionEventListener} и обеспечивает обработку входящих данных
     * и событий соединения.
     * Основные функции:
     * <ul>
     * <li>разбор входящих ASdu и извлечение информационных объектов</li>
     * <li>обработка данных (база данных, UI)</li>
     * <li>отслеживание разрыва соединения</li>
     * </ul>
     * @see ConnectionEventListener
     * @see ASdu
     * @see InformationObject
     */
    public class ClientEventListener implements ConnectionEventListener {
        /**
         * Обработка входящих {@link ASdu} сообщений
         * Вызывается при получении нового сообщения от сервера.
         * Функции:
         * <ul>
         * <li>извлечение всех {@link InformationObject} из ASDU</li>
         * <li>инициализация временной метки сообщения</li>
         * <li>обработка данных</li>
         * </ul>
         *
         * @param aSdu
         *          входящее ASDU сообщение
         */
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

        /**
         * Обработка разрыва соединения с сервером.
         * Вызывается при закрытии соединения по инициативе сервера или при ошибке связи.
         * Выдает информационное сообщение в консоль приложения и обновляет UI.
         *
         * @param e
         *      исключение, содержащее информацию о причине разрыва соединения
         */
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
     * Вызывается при получении нового ASDU. Заполняет модель {@link StreamDataModel} данными
     * из нового ASDU.
     *
     * @param aSdu
     *          Новый полученный ASDU
     * @param date
     *          Время получения нового ASDU
     * @param io
     *          Объект информации полученный в составе нового ASDU
     */
    public void initData(ASdu aSdu, String date, InformationObject io) {
        try {
            final StreamDataModel streamDataModel = new StreamDataModel();
            fillProtocolDataModel(streamDataModel, aSdu, date, io);
            Platform.runLater(() ->{
                streamData.add(streamDataModel);
                if (isAddToChart()) {
                    mainWindowController.addLineChartPoint(streamDataModel);
                }
                mainWindowController.initialize(streamData);
            });
        } catch (NullPointerException e) {
            mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Инициализирует и обновляет уникальные данные в UI на основе полученного {@link InformationObject}.
     * Обрабатывает входящие {@link ASdu} определенного типа (ТИ, ТС) обновляя существующие записи
     * в коллекции уникальных данных, создавая новые записи для InformationObject с неизвестными адресами и
     * обновляет UI после обработки данных
     *
     * @param aSdu
     *          объект {@link ASdu}
     * @param date
     *          дата/время в формате пригодном для отображения
     * @param io
     *          информационный объект, содержащий адрес и другие данные для обработки
     */
    public void initStaticData(ASdu aSdu, String date, InformationObject io) {
        if (EnumSet.of(M_SP_TB_1, M_DP_TB_1, M_ME_TF_1, M_ME_NC_1, M_SP_NA_1, M_DP_NA_1).contains(aSdu.getTypeIdentification())) {
            StreamDataModel pdm = findStaticDataByAddress(io.getInformationObjectAddress());
            if (pdm != null) {
                // Если найдена существующая запись – ее обновление
                fillProtocolDataModel(pdm, aSdu, date, io);
            } else {
                // Если не найдена – создание новой записи и пометка её как новой
                try {
                    StreamDataModel newModel = new StreamDataModel();
                    newModel.setNewlyAdded(true);
                    fillProtocolDataModel(newModel, aSdu, date, io);
                    staticData.add(newModel);
                    staticDataMap.put(newModel.getStreamAddressProperty(), newModel);
                } catch (NullPointerException e) {
                    mainWindowController.printConsoleErrorMessage("Ошибка: " + e.getMessage());
                }
            }
            // Обновление UI после завершения обработки
            Platform.runLater(() -> mainWindowController.initStaticData(staticData));
        }
    }

    private StreamDataModel findStaticDataByAddress(int address) {
        return staticDataMap.get(address);
    }

    /**
     * Обновляет данные в листах ТС и ТИ банка данных, полученного из контроллера
     *
     * @param io
     *          Информационный объект, содержащий данные для обновления
     * @see MainWindowController
     * @see InformationObject
     * @see DataBaseDataModel
     */
    public void initDataBaseData(InformationObject io) {

        // Обновление для TS-данных
        ObservableList<DataBaseDataModel> tsList = mainWindowController.getDataBaseList().get(0);
        updateDatabaseModels(tsList, io, 0, 0, 0);

        // Обновление для TI-данных
        ObservableList<DataBaseDataModel> tiList = mainWindowController.getDataBaseList().get(1);
        updateDatabaseModels(tiList, io, 1, 0, 1);
    }

    /**
     * Обновляет {@link DataBaseDataModel} в полученном списке на основе полученного {@link InformationObject}
     *
     * @param dataList
     *          {@link ObservableList< DataBaseDataModel >} моделей данных для обновления
     * @param io
     *          {@link InformationObject} с исходными данными
     * @param qualityIndex
     *          Индекс элемента информации "Качество"
     * @param valueIndex
     *          Индекс элемента информации "Значение"
     * @param targetIndex
     *          Индекс листа банка данных
     *
     */
    private void updateDatabaseModels(ObservableList<DataBaseDataModel> dataList, InformationObject io,
                                      int qualityIndex, int valueIndex, int targetIndex) {
        // targetIndex – индекс листа БД, по которому будет вызван UI-метод обновления
        for (DataBaseDataModel model : dataList) {
            if (model.getDbIoaProperty() == io.getInformationObjectAddress()) {
                // Обновление значения и качества в зависимости от индексов
                model.setDbValueProperty(io.getInformationElements()[0][valueIndex].getValue());
                model.setDbQualityProperty(io.getInformationElements()[0][qualityIndex].getQuality());
            }
        }

        Platform.runLater(() -> {
            if (mainWindowController.getIndexListDataBase() == targetIndex) {
                mainWindowController.initDataBase(dataList);
            }
        });
    }

    /**
     * Очистка коллекций данных и вспомогательной коллекции staticDataMap
     */
    public void clearDataTable() {
        if (streamData != null) streamData.clear();
        if (staticData != null) staticData.clear();
        staticDataMap.clear();
    }

    private void fillProtocolDataModel(StreamDataModel streamDataModel, ASdu aSdu, String date, InformationObject io) {
        streamDataModel.setStreamTimeProperty(date);
        streamDataModel.setStreamAsduProperty(aSdu.getCommonAddress());
        streamDataModel.setStreamCauseProperty(aSdu.getCauseOfTransmission().toString());
        streamDataModel.setStreamQualityProperty(io.getInformationElements()[0][0].getQuality());
        streamDataModel.setStreamTypeProperty(aSdu.getTypeIdentification().getId() + " (" +
                aSdu.getTypeIdentification().getDescription() + ")");
        if (aSdu.getTypeIdentification() == M_SP_TB_1 || aSdu.getTypeIdentification() == M_DP_TB_1
        ) {
            streamDataModel.setStreamTimeTagProperty(io.getInformationElements()[0][1].getValue());
        } else {
            streamDataModel.setStreamTimeTagProperty("");
        }
        if (aSdu.getTypeIdentification() == M_ME_NC_1 || aSdu.getTypeIdentification() == M_ME_TF_1) {
            streamDataModel.setStreamQualityProperty(io.getInformationElements()[0][1].getQuality());
        }
        if (aSdu.getTypeIdentification() == M_ME_TF_1) {
            streamDataModel.setStreamTimeTagProperty(io.getInformationElements()[0][2].getValue());
        }
        streamDataModel.setStreamValueProperty(io.getInformationElements()[0][0].getValue());
        streamDataModel.setStreamAddressProperty(io.getInformationObjectAddress());
    }

    public void createConnection(ClientConnectionBuilder clientConnectionBuilder) {
        final int MAX_ATTEMPTS = 3; // Максимальное количество попыток
        final int RETRY_DELAY_MS = 2000; // Задержка между попытками в миллисекундах
        int attempt = 1;

        // Закрытие предыдущего соединения, если оно существует
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
