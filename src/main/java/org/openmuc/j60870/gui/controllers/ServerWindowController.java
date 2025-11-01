package org.openmuc.j60870.gui.controllers;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import org.openmuc.j60870.gui.emulator104.ASduSender;
import org.openmuc.j60870.gui.customUI.CheckBoxPopupTableCell;
import org.openmuc.j60870.gui.models.ServerModel;
import org.openmuc.j60870.gui.utilities.Validator;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class ServerWindowController {
    //Константы
    private static final File SAVE_FILE = new File("senderData.json");
    private static final ServerModel DEFAULT_SERVER_MODEL = new ServerModel(30, 1, 1, 101, 0, 1);

    //UI компоненты
    @FXML private TableView<ServerModel> serverTable;
    @FXML private TableColumn<ServerModel, Integer> aSduTypeColumn, causeOfTransmissionColumn, commonAddressColumn, ioaColumn, qualityColumn, valueColumn;
    @FXML private Button addRowButton, connectButton, disconnectButton, sendSelectedButton, sendAllButton, periodicSendButton, periodicSendStopButton;
    @FXML private TextField portTextField, periodTextField;

    //Отслеживание статуса сервера
    private final BooleanProperty b = new SimpleBooleanProperty(false);

    private Validator validator = new Validator();

    //Переменные
    private ASduSender aSduSender;
    private ObservableList<ServerModel> dataList;

    //Настройка Gson для сохранения и загрузки данных таблицы сигналов
    private final Gson gson = new GsonBuilder()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getDeclaredType().getTypeName().contains("com.sun.javafx.binding.ExpressionHelper");
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz.getName().contains("com.sun.javafx.binding.ExpressionHelper");
                }
            })
            .create();

    /* Метод инициализации */
    @FXML
    private void initialize() {
        configureServerTable();
        configureServerTableColumns();
        loadDataFromFile();
        setButtonsActions();
        setupDefaultServerModel();
        serverTable.setItems(dataList);
    }


    private void configureServerTable() {
        serverTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        serverTable.setEditable(true);
    }

    private void setButtonsActions() {
        addRowButton.setOnAction(event -> addRow());
        connectButton.setOnAction(event -> connect());
        sendSelectedButton.setOnAction(event -> sendSelectedASdu());
        sendAllButton.setOnAction(event -> sendAllASdu());
        disconnectButton.setOnAction(event -> {
            try {
                disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        periodicSendButton.setOnAction(event -> startPeriodicSending());
        periodicSendStopButton.setOnAction(event -> stopPeriodicSending());
    }

    private void setupDefaultServerModel(){
        if (dataList == null) {
            dataList = FXCollections.observableArrayList();
            dataList.add(DEFAULT_SERVER_MODEL);
        }
    }

    private void addRow() {
        dataList.add(DEFAULT_SERVER_MODEL);
    }

    private void configureServerTableColumns() {
        ObservableList<Integer> aSduTypeList = FXCollections.observableArrayList(1, 30, 3, 31, 13, 36);
        aSduTypeColumn.setCellValueFactory(cellData -> cellData.getValue().aSduTypeProperty().asObject());
        aSduTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(aSduTypeList));
        causeOfTransmissionColumn.setCellValueFactory(cellData -> cellData.getValue().causeOfTransmissionProperty().asObject());
        causeOfTransmissionColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        commonAddressColumn.setCellValueFactory(cellData -> cellData.getValue().commonAddressProperty().asObject());
        commonAddressColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        ioaColumn.setCellValueFactory(cellData -> cellData.getValue().ioaProperty().asObject());
        ioaColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        qualityColumn.setCellValueFactory(cellData -> cellData.getValue().qualityProperty().asObject());
        qualityColumn.setCellFactory(param -> new CheckBoxPopupTableCell<>());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    private void connect(){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (validator.validatePort(portTextField.getText())) {
                    serverTable.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        if (event.getCode() == KeyCode.SPACE) {
                            sendSelectedASdu();
                            event.consume();
                        }
                    });
                    aSduSender = new ASduSender(serverTable.getItems());
                    b.bind(aSduSender.isServerOnlineProperty());
                    b.addListener(event -> setTextFieldBackgroundColor());
                    try {
                        aSduSender.startServer(Integer.parseInt(portTextField.getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setTextFieldBackgroundColor();
                } else {
                    System.out.println("Не корректный номер порта");
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void sendSelectedASdu() {
        aSduSender.sendSelectedASdu(serverTable.getSelectionModel().getSelectedItems());
    }

    private void sendAllASdu() {
        aSduSender.sendSelectedASdu(serverTable.getItems());
    }

    private void disconnect() throws IOException {
        if (aSduSender != null) {
            aSduSender.stopServer();
        }
    }

    private void saveDataToFile() {
        try (Writer writer = new FileWriter(SAVE_FILE)) {
            gson.toJson(dataList, writer);
            System.out.println("Данные сохранены в файл: " + SAVE_FILE.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromFile() {
        System.out.println("Loaded data from file: " + SAVE_FILE.getAbsolutePath());
        if (SAVE_FILE.exists()) {
            System.out.println("File was find");
            try (Reader reader = new FileReader(SAVE_FILE)) {
                Type listType = new TypeToken<List<ServerModel>>() {
                }.getType();
                List<ServerModel> loadedList = gson.fromJson(reader, listType);
                dataList = FXCollections.observableArrayList(loadedList);
                System.out.println("Данные загружены из файла");
            } catch (Exception e) {
                System.out.println("Ошибка при чтении файла");
                e.printStackTrace();
            }
        }
    }

    private void setTextFieldBackgroundColor() {
        if (b.getValue()) {
            portTextField.setStyle("-fx-background-color: rgba(33, 150, 0, 0.6)");
        } else {
            portTextField.setStyle("-fx-background-color: rgba(200, 40, 0, 0.6)");
        }
    }

    private void startPeriodicSending() {
        if (periodTextField.getText() != null) {
            aSduSender.startPeriodicSender(serverTable.getSelectionModel().getSelectedItems(), Integer.parseInt(periodTextField.getText()));
        }
    }

    private void stopPeriodicSending() {
        aSduSender.stopSending();
    }

    public void setStage(Stage serverStage) {
        serverStage.setOnCloseRequest(event -> {
            saveDataToFile();
            if (aSduSender != null) {
                try {
                    disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}