package org.openmuc.j60870.gui.emulator104;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import org.openmuc.j60870.*;
import org.openmuc.j60870.gui.model.ServerModel;
import org.openmuc.j60870.ie.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASduSender {

    /**
     * Сервер IEC104
     * {@code volatile} гарантирует корректное обновление состояния в многопоточной среде
     */
    private volatile Server server;
    /**
     * Объект для синхронизцаии доступа к таймеру периодической отправки
     */
    private final Object timelineLock = new Object();
    /**
     * Таймер для автоматической отправки aSdu с заданным интервалом
     */
    private Timeline periodicSenderTimeline;
    /**
     * Список активных клиентских подключений
     */
    private final List<Connection> connections = new ArrayList<>();
    /**
     * Свойство отражающее состояние сервера (онлайн/офлайн)
     */
    private final BooleanProperty isServerOnline = new SimpleBooleanProperty(false);
    private ObservableList<ServerModel> dataList;

    public ASduSender(ObservableList<ServerModel> dataList) {
        this.dataList = dataList;
    }

    public void startPeriodicSender(ObservableList<ServerModel> serverModels, double intervalSeconds) {
        stopSending();
        synchronized (timelineLock) {
            periodicSenderTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(intervalSeconds),
                            event -> sendSelectedASdu(serverModels)
                    ));
            periodicSenderTimeline.setCycleCount(Timeline.INDEFINITE);
            periodicSenderTimeline.play();
        }
    }

    public void stopSending() {
        synchronized (timelineLock) {
            if (periodicSenderTimeline != null) {
                periodicSenderTimeline.stop();
            }
        }
    }
    public void startServer(int port) throws IOException{
        server = Server.builder()
                .setPort(port)
                .build();
        server.start(new ServerEventListener() {
            @Override
            public void connectionIndication(Connection connection) {
                connection.setConnectionListener(new ConnectionEventListener() {
                    private boolean selected = false;
                    @Override
                    public void newASdu(ASdu aSdu) {
                        try {
                            switch (aSdu.getTypeIdentification()) {
                                // interrogation command
                                case C_IC_NA_1:
                                    connection.sendConfirmation(aSdu);
                                    System.out.println("Got interrogation command (100). Will send scaled measured values.");
                                    sendSelectedASdu(dataList);
                                    break;
                                case C_SC_NA_1:
                                    InformationObject informationObject = aSdu.getInformationObjects()[0];
                                    IeSingleCommand singleCommand = (IeSingleCommand) informationObject
                                            .getInformationElements()[0][0];

                                    if (informationObject.getInformationObjectAddress() != 5000) {
                                        break;
                                    }
                                    if (singleCommand.isSelect()) {
                                        System.out.println("Got single command (45) with select true. Select command.");
                                        selected = true;
                                    }
                                    else if (!singleCommand.isSelect() && selected) {
                                        System.out.println("Got single command (45) with select false. Execute selected command.");
                                        selected = false;
                                    }
                                    else {
                                        System.out.println("Got single command (45) with select false. But no command is selected, no execution.");
                                    }
                                    break;
                                case C_CS_NA_1:
                                    IeTime56 ieTime56 = new IeTime56(System.currentTimeMillis());
                                    System.out.println("Got Clock synchronization command (103). Send current time: \n" + ieTime56);
                                    connection.synchronizeClocks(aSdu.getCommonAddress(), ieTime56);
                                    break;
                                default:
                                    System.out.println("Got unknown request: " + aSdu + ". Will not confirm it.\n");
                            }

                        } catch (EOFException e) {
                            System.out.println("Will quit listening for commands on connection  because socket was closed.");
                        } catch (IOException e) {
                            System.out.println("Will quit listening for commands on connection because of error: \"" +
                                    e.getMessage() + "\".");
                        }

                    }
                    @Override
                    public void connectionClosed(IOException cause) {
                        System.out.println("Отключился клиент: " + connection);
                        connections.remove(connection);
                    }
                });
                System.out.println("Подключился клиент: " + connection);
                synchronized (connections) {
                    connections.add(connection);
                }
            }

            @Override
            public void serverStoppedListeningIndication(IOException e) {
                isServerOnline.set(false);
                System.err.println("Сервер перестал прослушивать входящие подключения " + e.getMessage());
            }

            @Override
            public void connectionAttemptFailed(IOException e) {
                System.err.println("Неудачная попытка подключения " + e.getMessage());
            }
        });
        isServerOnline.set(true);
        System.out.println("Сервер запущен на порту " + port);
    }

    private void sendASdu(ServerModel serverModel) {
        ASdu aSdu = ASduFactory.createASdu(serverModel.getaSduType(), serverModel.getCauseOfTransmission(),
                serverModel.getCommonAddress(), serverModel.getIoa(), serverModel.getValue(), serverModel.getQuality());
        synchronized (connections) {
            connections.removeIf(Connection::isClosed);
            for (Connection connection:
                    connections) {
                try {
                    connection.send(aSdu);
                    System.out.println("Отправлено ASdu: " + aSdu);
                } catch (IOException e) {
                    System.err.println("Ошибка отправки ASdu клиенту " + connection + ": " + e.getMessage());
                }
            }
        }
    }

    public void sendSelectedASdu(ObservableList<ServerModel> serverModels) {
        if (serverModels.isEmpty()) {
            System.out.println("Список aSdu для отправки пуст");
            return;
        }
        List<ServerModel> modelsCopyForSending = new ArrayList<>(serverModels);
        for (ServerModel serverModel :
                modelsCopyForSending) {
            sendASdu(serverModel);
        }
    }

    public void stopServer() {
        stopSending();  // Сначала останавливаем таймер

        if (server != null) {
            server.stop();
            isServerOnline.set(false);
            System.out.println("Сервер остановлен.");
        }

        synchronized (connections) {
            for (Connection connection : connections) {
                connection.close();
            }
            connections.clear();
        }
    }

    public boolean isIsServerOnline() {
        return isServerOnline.get();
    }

    public BooleanProperty isServerOnlineProperty() {
        return isServerOnline;
    }

    public void setIsServerOnline(boolean isServerOnline) {
        this.isServerOnline.set(isServerOnline);
    }
}
