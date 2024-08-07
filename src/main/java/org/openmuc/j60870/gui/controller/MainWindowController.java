package org.openmuc.j60870.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.openmuc.j60870.*;
import org.openmuc.j60870.gui.app.Client104;
import org.openmuc.j60870.gui.app.ExcelConverter;
import org.openmuc.j60870.gui.app.MultiChartTest;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.ProtocolDataModel;
import org.openmuc.j60870.gui.model.SubstationParamModel;
import org.openmuc.j60870.gui.utilities.*;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.internal.cli.*;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.Calendar;

public class MainWindowController {
    @FXML
    AnchorPane mainPane, parameterPane;
    @FXML
    TabPane parameterTabPane;
    @FXML
    Button resizeButton;
    @FXML
    private TextField ipField, portField, asduField, t1Field, t2Field, t3Field, hourField, minuteField, secondField, tuAddressField, kField, wField, myIpField, filterField;
    @FXML
    private Label dataBaseName, substationNumberLabel, substationAddressLabel;
    @FXML
    private ToolBar topBar;
    @FXML
    private VBox filterBox;
    @FXML
    private TabPane dataTabPane;
    @FXML
    public Tab dataTab;
    @FXML
    private ComboBox<String> asduLengthBox, ioaLengthBox;
    @FXML
    private Button writeExcelButton, protocolButton, startButton;
    @FXML
    private CheckBox pcTimeBox, getToDataBaseCheckBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<DataModel> dataBaseTable;
    @FXML
    private TableColumn<DataModel, String> dataNameParamColumn, dataValueColumn, dataQualityColumn, dataCheckColumn;
    @FXML
    private TableColumn<DataModel, Integer> dataIoaColumn;
    @FXML
    public TableView<ProtocolDataModel> protocolTable, staticTable;
    @FXML
    private TableColumn<ProtocolDataModel, String> protTimeColumn, protTypeColumn, protCauseColumn, protValueColumn, protQualityColumn, protTimeTagColumn;
    @FXML
    private TableColumn<ProtocolDataModel, Integer> protAsduColumn, protAddressColumn;
    @FXML
    private TableColumn<ProtocolDataModel, Integer> staticAsduColumn, staticAddressColumn;
    @FXML
    private TableColumn<ProtocolDataModel, String> staticNameColumn, staticTypeColumn, staticCauseColumn, staticValueColumn, staticQualityColumn, staticTimeColumn, staticClientTimeColumn;
    @FXML
    public RadioButton tsRadioButton, tiRadioButton, tuRadioButton;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileMenu, toolsMenu, docsMenu;
    @FXML
    private MenuItem openBDMenuItem, darkTheme, lightTheme, aboutMenuItem;
    @FXML
    private TextFlow consoleTextFlow;
    @FXML
    private ScrollPane consolePane;
    @FXML
    private ImageView imageView;


    public int indexListDataBase;
    public ExcelConverter excelConverter;
    public boolean isGetToChart;
    public ObservableList<ObservableList<DataModel>> dataBaseList = FXCollections.observableArrayList();
    public String defaultStyleIpField;

    public final ToggleGroup dataBaseListSelectGroup = new ToggleGroup();

    private Integer analogAddressForChart;
    private LineChartController lineChartController;
    private boolean selectCmd;
    private boolean stateOnCmd;
    private Integer commonAddressParam;

    private final ObservableList<String> asduLengthList = FXCollections.observableArrayList("1 Byte", "2 Bytes");
    private final ObservableList<String> ioaLengthList = FXCollections.observableArrayList("1 Byte", "2 Bytes", "3 Bytes");
    private final Client104 client104 = new Client104(this);
    private final UtilNet utilNet = new UtilNet();
    private final ConsolePrinter consolePrinter = new ConsolePrinter();
    private final Validator validator = new Validator();
    private final String errorBackgroundColor = "red";
    private final String successBackgroundColor = "#336600";

    private String currentTheme;
    private static final String DARK_THEME = "/view/DarkThemeRoot.css";
    private static final String LIGHT_THEME = "/view/LightThemeRoot.css";


    public MainWindowController() {
    }

    @FXML
    private void initialize() {
        currentTheme = DARK_THEME;
        isGetToChart = false;
        asduLengthBox.setItems(asduLengthList);
        asduLengthBox.setValue("2 Byte");
        ioaLengthBox.setItems(ioaLengthList);
        ioaLengthBox.setValue("3 Bytes");
        commonAddressParam = 1;
        protocolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataBaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        staticTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        dataBaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        defaultStyleIpField = ipField.getStyle();
        ipField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) {
                if (!validator.validateIpAddress(ipField.getText())) {
                    ipField.setStyle("-fx-background-color: " + errorBackgroundColor);
                    printConsoleErrorMessage("Некорректное значение в поле IP адреса");
                }
            } else {
                ipFieldResetStyle();
            }
        });
        String stylePortField = portField.getStyle();
        portField.focusedProperty().addListener((ov, oldV, newV) ->{
            if (!newV) {
                if (!validator.validatePort(portField.getText())) {
                    portField.setStyle("-fx-background-color: " + errorBackgroundColor);
                    printConsoleErrorMessage("Некорректное значение в поле порта");
                }
            } else {
                portField.setStyle(stylePortField);
            }
        });

        consoleTextFlow.getChildren().addListener(
                (ListChangeListener<? super Node>) ((change) -> {
                    consoleTextFlow.requestLayout();
                    consolePane.requestLayout();
                    consolePane.setVvalue(consolePane.getVmax());
                })
        );

        //Всплывающие подсказки
        t1Field.setTooltip(new Tooltip("Тайм-аут при посылке или \nтестировании APDU (по умол. 15с)"));
        t2Field.setTooltip(new Tooltip("Тайм-аут для подтверждения в случае \nотсутствия сообщения с данными (по умол. 10с)"));
        t3Field.setTooltip(new Tooltip("Тайм-аут для посылки блоков тестирования \nв случае долгого простоя (по умол. 20с)"));
        kField.setTooltip(new Tooltip("Максимальная разность между переменной состояния \nпередачи и номером последнего подтвержденного APDU (по умол. 12)"));
        wField.setTooltip(new Tooltip("Последнее подтверждение после приема \nw APDU формата I (по умол. 8)"));

        //Установка текущей даты и времени в поля выборы даты/времени
        datePicker.setValue(LocalDate.now());
        hourField.setText(String.valueOf(Calendar.HOUR));
        minuteField.setText(String.valueOf(Calendar.MINUTE));
        secondField.setText(String.valueOf(Calendar.SECOND));

        //Переключатели листов банка данных
        tsRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tsRadioButton.setDisable(true);
        tsRadioButton.setSelected(true);
        tiRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tiRadioButton.setDisable(true);
        tuRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tuRadioButton.setDisable(true);
        changeDataBaseList();

        //Определение IP клиента, установка значения в поле IP адреса
        try {
            myIpField.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        dataCheckColumn.setStyle("-fx-alignment: CENTER;");
        openBDMenuItem.setOnAction(event -> openExcelFile());
        aboutMenuItem.setOnAction(event -> openAboutWindow());

        //Смена темы оформления
        darkTheme.setOnAction(event -> switchTheme(DARK_THEME));
        lightTheme.setOnAction(event -> switchTheme(LIGHT_THEME));

        dataTabPane.setOnDragOver(event -> {
            if (event.getGestureSource() != dataTabPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        dataTabPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                openExcelFile(db.getFiles().get(0));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        resizeButton.setOnAction(event -> resizePane());
    }

    private void resizePane(){
        if (parameterTabPane.getSide() == Side.TOP) {
            parameterTabPane.setSide(Side.LEFT);
            parameterPane.setMaxWidth(30);
            resizeButton.setText(">");
        } else {
            resizeButton.setText("<");
            parameterTabPane.setSide(Side.TOP);
            parameterPane.setMaxWidth(300);
        }
    }

    private TextFieldTableCell<ProtocolDataModel, String> setValueColumnStyle() {
        TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            cell.setStyle("-fx-background-color: transparent");
            if (newItem != null) {
                setTSCellColor(newItem, cell);
            }
        });
        return cell;
    }

    private TextFieldTableCell<ProtocolDataModel, String> setAnimatedValueColumnStyle() {
        TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            cell.setStyle("-fx-background-color: transparent");
            if (newItem != null) {
                cell.getStyleClass().add("animated-gradient");
                ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();
                KeyValue kv1 = new KeyValue(baseColor, Color.rgb(200, 40, 0));
                KeyValue kv2 = new KeyValue(baseColor, Color.rgb(40, 200, 0));
                KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                KeyFrame kf2 = new KeyFrame(Duration.millis(500), kv2);
                Timeline timeline = new Timeline(kf1, kf2);
                baseColor.addListener((ob, oldColor, newColor) -> cell.setStyle(String.format("-gradient-base: #%02x%02x%02x; ",
                        (int)(newColor.getRed()*255),
                        (int)(newColor.getGreen()*255),
                        (int)(newColor.getBlue()*255))));
                timeline.setAutoReverse(true);
                timeline.setCycleCount(5);
                timeline.play();
                timeline.setOnFinished(e -> setTSCellColor(newItem, cell));
            }
        });
        return cell;
    }

    private void setTSCellColor(String newItem, TextFieldTableCell<ProtocolDataModel, String> cell) {
        switch (newItem) {
            case "ON":
                cell.setStyle("-fx-background-color: rgba(200, 40, 0, 0.6) ;");
                break;
            case "OFF":
                cell.setStyle("-fx-background-color: rgba(40, 200, 0, 0.6) ;");
                break;
            case "INDETERMINATE_OR_INTERMEDIATE":
            case "INDETERMINATE":
                cell.setStyle("-fx-background-color: rgba(200, 200, 0, 0.6) ;");
                break;
            default:
                cell.setStyle("-fx-background-color: transparent");
        }
    }

    private TextFieldTableCell<ProtocolDataModel, String> setQualityColumnStyle() {
        TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
            cell.setStyle("-fx-background-color: transparent");
            if (newItem != null) {
                switch (newItem) {
                    case "BL (блокировка) ":
                    case "SB (замещение) ":
                    case "NT (неактуальное) ":
                    case "IV (недействительное) ":
                        cell.setStyle("-fx-background-color: rgba(0, 100, 200, 0.6) ;");
                        break;
                    default:
                        cell.setStyle("-fx-background-color: transparent");
                        break;
                }
            }
        });
        return cell;
    }

    public void initialize(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            protTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            protTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            protCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            protAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            protAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            protValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            protValueColumn.setCellFactory(tc-> setValueColumnStyle());
            protQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            protQualityColumn.setCellFactory(tc->setQualityColumnStyle());
            protTimeTagColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());

            FilteredList<ProtocolDataModel> filteredList = new FilteredList<>(dataModelObservableList, p -> true);
            filterField.textProperty().addListener(((ObservableValue<? extends String> observable, String s, String t1) -> filteredList.setPredicate(protocolDataModel -> {
                if (t1 == null || t1.isEmpty()){
                    return true;
                }
                String ioa = String.valueOf(protocolDataModel.getProtAddress());
                return ioa.contains(t1);
            })));
            SortedList<ProtocolDataModel> sortedDataList = new SortedList<>(filteredList);
            sortedDataList.comparatorProperty().bind(protocolTable.comparatorProperty());

            protocolTable.setItems(sortedDataList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    public void initStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            staticAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            staticNameColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            staticCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            staticValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticValueColumn.setCellFactory(tc->setValueColumnStyle());
            staticQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            staticQualityColumn.setCellFactory(tc->setQualityColumnStyle());
            staticTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
            staticClientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    public void initSingleStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            staticAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            staticNameColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            staticCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            staticValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticValueColumn.setCellFactory(tc->setAnimatedValueColumnStyle());
            staticQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            staticQualityColumn.setCellFactory(tc->setQualityColumnStyle());
            staticTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
            staticClientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    public void initDataBase(ObservableList<DataModel> dataModelObservableList) {
        try {
            dataNameParamColumn.setCellValueFactory(cellData -> cellData.getValue().nameOfParamProperty());
            dataIoaColumn.setCellValueFactory(cellData -> cellData.getValue().ioaProperty().asObject());
            dataValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
            dataQualityColumn.setCellValueFactory(cellData -> cellData.getValue().qualityProperty());
            dataCheckColumn.setCellValueFactory(
                    new PropertyValueFactory<>("check")
            );
            dataBaseTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearDataTable() {
        client104.clearDataTable();
        protValueColumn.setCellFactory(tc ->{
            TextFieldTableCell cell = new TextFieldTableCell();
            cell.setStyle("-fx-background-color: transparent");
            return cell;
        });
        protQualityColumn.setCellFactory(tc->{
            TextFieldTableCell cell = new TextFieldTableCell();
            cell.setStyle("-fx-background-color: transparent");
            return cell;
        });
    }

    @FXML
    private void startButtonClicked() {
        if (client104.isConnectionExist()) {
            client104.closeConnection();
        }
        String ipServer = ipField.getText();
        if (!ipServer.equals("")) {
            if (!portField.getText().equals("")) {
                int portServer = Integer.parseInt(portField.getText());
                printConsoleInfoMessage("Подключение к " + ipServer + ":" + portServer);
                if (isHostAvailable(ipServer, portServer)) {
                    startClient(ipServer, portServer);
                } else {
                    printConsoleErrorMessage("Указанный сервер не доступпен");
                }
            } else {
                printConsoleErrorMessage("Не указан порт");
            }
        } else {
            printConsoleErrorMessage("Не указан IP адрес");
        }
    }

    private static boolean isHostAvailable(String ip, int port){
        try (Socket s = new Socket(ip, port)) {
            s.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    private void stopButtonClicked() {
        if (client104.isConnectionOpen()) {
            closeConnection();
        }
    }

    @FXML
    public void openExcelFile() {
        excelConverter = new ExcelConverter(this);
        dataBaseList = excelConverter.openFile();
        initializeExcelFile(dataBaseList);
    }

    private void openExcelFile(File file) {
        excelConverter = new ExcelConverter(this);
        dataBaseList = excelConverter.openFile(file);
        initializeExcelFile(dataBaseList);
    }

    private void initializeExcelFile(ObservableList<ObservableList<DataModel>> dataBaseList) {
        if (dataBaseList != null) {
            this.dataBaseName.setText("Открыт файл: " + excelConverter.getDataBaseName());
            tsRadioButton.setDisable(false);
            tiRadioButton.setDisable(false);
            tuRadioButton.setDisable(false);
            getToDataBaseCheckBox.setDisable(false);
            writeExcelButton.setDisable(false);
            protocolButton.setDisable(false);
            initDataBase(dataBaseList.get(0));
            dataBaseTable.setItems(dataBaseList.get(0));
        } else {
            this.dataBaseName.setText("Невозможно открыть файл");
        }
    }

    @FXML
    public void writeExcel() {
        excelConverter.fillOutputBDFile(dataBaseList);
    }

    @FXML
    public void giButtonClicked() {
        client104.sendGI(commonAddressParam);
        printConsoleInfoMessage("Отправка команды общего опроса станции.");
    }

    @FXML
    public void clockSyncButton() {
        IeTime56 timeToSync;
        if (pcTimeBox.isSelected()) {
            timeToSync = new IeTime56(System.currentTimeMillis());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getValue().getYear(), datePicker.getValue().getMonthValue() - 1, datePicker.getValue().getDayOfMonth(),
                    Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText()), Integer.parseInt(secondField.getText()));
            timeToSync = new IeTime56(calendar.getTimeInMillis());
        }
        client104.sendClockSynchronization(commonAddressParam, timeToSync);
        printConsoleInfoMessage("Отправка команды синхронизации времени");
    }

    @FXML
    public void selOnButton() {
       selectCmd = true;
       stateOnCmd = true;
       cmdButtonClicked();
    }

    @FXML
    public void selOffButton() {
        selectCmd = true;
        stateOnCmd = false;
        cmdButtonClicked();
    }
    @FXML
    public void exeOnButton() {
        selectCmd = false;
        stateOnCmd = true;
        cmdButtonClicked();
    }
    @FXML
    public void exeOffButton() {
        selectCmd = false;
        stateOnCmd = false;
        cmdButtonClicked();
    }

    public boolean isGetToDataBase() {
        return getToDataBaseCheckBox.isSelected();
    }

    public void cmdButtonClicked() {
        try {
            client104.sendTuCommand(commonAddressParam, Integer.parseInt(tuAddressField.getText()), selectCmd, stateOnCmd);
        } catch (NumberFormatException numberFormatException) {
            printConsoleErrorMessage("Попытка отправки команды ТУ без обязательного параметра Адрес_ТУ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        printConsoleInfoMessage("Соединение остановлено");
        ipField.setStyle("-fx-background-color: derive(#373e43, 35%)");
        client104.closeConnection();
    }

    public void setIpFieldStyle(boolean connectionStatus) {
        if (connectionStatus) {
            ipField.setStyle("-fx-background-color: " + successBackgroundColor);
        } else {
            ipField.setStyle("-fx-background-color: derive(#373e43, 35%)");
        }
    }

    public void startClient(String ip, int port) {
        ClientConnectionBuilder clientConnectionBuilder = null;
        commonAddressParam = Integer.parseInt(asduField.getText());

        CliConParameter cliConParameter = new CliConParameter();
        cliConParameter.setAsduLength(Integer.parseInt(asduLengthBox.getValue().substring(0, 1)));
        cliConParameter.setIoaLength(Integer.parseInt(ioaLengthBox.getValue().substring(0, 1)));
        cliConParameter.setT1(Integer.parseInt(t1Field.getText()) * 1000);
        cliConParameter.setT2(Integer.parseInt(t2Field.getText()) * 1000);
        cliConParameter.setT3(Integer.parseInt(t3Field.getText()) * 1000);
        cliConParameter.setkParam(Integer.parseInt(kField.getText()));
        cliConParameter.setwParam(Integer.parseInt(wField.getText()));
        cliConParameter.setPort(port);

        try {
            InetAddress address = InetAddress.getByName(ip);
            clientConnectionBuilder = client104.createClientConnectionBuilder(address, cliConParameter);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (clientConnectionBuilder != null) {
            client104.createConnection(clientConnectionBuilder);
            if (client104.isConnectionOpen()) {
                printConsoleSuccessMessage("Открыто соединение с " + ip + ":" + port);
                ipField.setStyle("-fx-background-color: " + successBackgroundColor);
            } else {
                printConsoleErrorMessage("Невозможно подключиться к " + ip + ":" + port);
            }
        }
    }

    public void ipFieldResetStyle() {
        ipField.setStyle(defaultStyleIpField);
    }

    //Вывод информационного сообщения в консоль
    public void printConsoleInfoMessage(String... strings) {
        printMessage(Message.MessageType.INFO, strings);
    }

    //Вывод сообщения об ошибке в консоль
    public void printConsoleErrorMessage(String... strings) {
        printMessage(Message.MessageType.ERROR, strings);
    }

    //Вывод сообщения об успешной операции в консоль
    public void printConsoleSuccessMessage(String... strings) {
        printMessage(Message.MessageType.SUCCESS, strings);
    }

    public void printMessage(Message.MessageType type, String... strings) {
        ObservableList<? extends Node> list;
        for (String string : strings) {
            list = consoleTextFlow.getChildren();
            switch (type) {
                case ERROR:
                    consolePrinter.printErrorMessage(list, string);
                    break;
                case INFO:
                    consolePrinter.printInfoMessage(list, string);
                    break;
                case SUCCESS:
                    consolePrinter.printSuccessMessage(list, string);
                    break;
            }
        }
    }

    public void setSubstationParamFromBD(SubstationParamModel substationParamModel) {
        ipField.setText(substationParamModel.getIpParam());
        portField.setText(substationParamModel.getPortParam());
        substationNumberLabel.setText(substationParamModel.getSubstationTypeParam() + " " + substationParamModel.getSubstationNumberParam());
        substationAddressLabel.setText(substationParamModel.getSubstationAddressParam());
    }

    @FXML
    private void pingIpAddress() {
        utilNet.pingIp(ipField.getText());
    }

    @FXML
    private void changeLocalIp() {
        utilNet.changeLocalIp(myIpField.getText());
    }

    //Смена листов БД в зависимости от состояния переключателя ТС - ТИ
    private void changeDataBaseList() {
        dataBaseListSelectGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (dataBaseListSelectGroup.getSelectedToggle().equals(tsRadioButton)) {
                indexListDataBase = 0;
                initDataBase(dataBaseList.get(0));
                dataBaseTable.setContextMenu(null);
            } else if (dataBaseListSelectGroup.getSelectedToggle().equals(tiRadioButton)) {
                initDataBase(dataBaseList.get(1));
                indexListDataBase = 1;
                dataBaseTable.setContextMenu(tiContextMenu(dataBaseTable));
            } else if (dataBaseListSelectGroup.getSelectedToggle().equals(tuRadioButton)){
                initDataBase(dataBaseList.get(2));
                indexListDataBase = 2;
                dataBaseTable.setContextMenu(tuContextMenu());
            }
        });
    }

    //Обработка свайпа влево. Смена листов БД (ТС <-> ТИ)
    @FXML
    private void swipeListLeft() {
        if (indexListDataBase < 2) {
            indexListDataBase = indexListDataBase + 1;
            initDataBase(dataBaseList.get(indexListDataBase));
            setSelectedToggle(indexListDataBase);
        }
    }

    //Обработка свайпа вправо. Смена листов БД (ТС <-> ТИ)
    @FXML
    private void swipeListRight() {
        if (indexListDataBase > 0) {
            indexListDataBase = indexListDataBase - 1;
            initDataBase(dataBaseList.get(indexListDataBase));
            setSelectedToggle(indexListDataBase);
        }
    }

    private void setSelectedToggle(int indexListDataBase) {
        if (indexListDataBase == 0) {
            dataBaseListSelectGroup.selectToggle(tsRadioButton);
        } else dataBaseListSelectGroup.selectToggle(tiRadioButton);
    }

    @FXML
    private void openProtocolWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ProtocolWindow.fxml"));
            AnchorPane protocolPane = loader.load();
            Stage protocolStage = new Stage();
            Scene scene = new Scene(protocolPane);
            protocolStage.setScene(scene);
            scene.getStylesheets().add(mainPane.getScene().getStylesheets().get(0));
            protocolStage.initModality(Modality.APPLICATION_MODAL);
            ProtocolWindowController controller = loader.getController();
            controller.setCurrentDataBase(excelConverter.file.getAbsolutePath());
            protocolStage.setResizable(true);
            protocolStage.setTitle("Протокол");
            protocolStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ContextMenu tiContextMenu(TableView tableView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem openLineChartMenuItem = new MenuItem("Открыть график");
        MenuItem openRealTimeLineChartMenuItem = new MenuItem("Открыть RealTime график");
        openLineChartMenuItem.setOnAction(event -> {
            this.isGetToChart = true;
            startLineChart(tableView);
            if (lineChartController != null) {
                Platform.runLater(()->lineChartController.addLineChartPoint());
            }
        });
        openRealTimeLineChartMenuItem.setOnAction(event -> {
            this.isGetToChart = true;
            startLineChart(tableView);
            if (lineChartController != null) {
                lineChartController.setIsRealTimeChart(true);
                Platform.runLater(()->lineChartController.realTimeChart());
            }
        });
        MenuItem openMultiChart = new MenuItem("MultiChart");
        openMultiChart.setOnAction(event -> {
            startMultiChart();
            if (multiChartTest != null) {
                Platform.runLater(() -> multiChartTest.realTimeChart());
            }
        });
        contextMenu.getItems().addAll(openLineChartMenuItem, openRealTimeLineChartMenuItem, openMultiChart);
        return contextMenu;
    }

    //Тестовый блок для MultiChart
    public MultiChartTest multiChartTest;
    private void startMultiChart() {
        ObservableList<DataModel> dataModelList = dataBaseTable.getSelectionModel().getSelectedItems();
        multiChartTest = new MultiChartTest();
        Stage stage = new Stage();
        multiChartTest.startChart(stage, dataModelList);
    }
    //Конец тестового блока для MultiChart

    private ContextMenu tuContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem onMenuItem = new MenuItem("Включить");
        MenuItem offMenuItem = new MenuItem("Отключить");
        onMenuItem.setOnAction(event -> {
            client104.sendTuCommand(commonAddressParam, dataBaseTable.getSelectionModel().getSelectedItem().getIoa(), true, true);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client104.sendTuCommand(commonAddressParam, dataBaseTable.getSelectionModel().getSelectedItem().getIoa(), false, true);
        });
        offMenuItem.setOnAction(event -> {
            client104.sendTuCommand(commonAddressParam, dataBaseTable.getSelectionModel().getSelectedItem().getIoa(), true, false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            client104.sendTuCommand(commonAddressParam, dataBaseTable.getSelectionModel().getSelectedItem().getIoa(), false, false);
        });
        contextMenu.getItems().addAll(onMenuItem, offMenuItem);
        return contextMenu;
    }

    private final Double oldValue = 0.0;

    private void startLineChart(TableView tableView) {
        if (tableView.equals(protocolTable)) {
            if (protocolTable.getSelectionModel().getSelectedItem().getProtType().equals("36 (Measured value, short floating point number with time tag CP56Time2a)") || protocolTable.getSelectionModel().getSelectedItem().getProtType().equals("13 (Measured value, short floating point number)")) {
                analogAddressForChart = protocolTable.getSelectionModel().getSelectedItem().getProtAddress();
                openLineChartWindow();
            } else {
                printConsoleInfoMessage("Просмотр графика доступен только для аналоговых значений");
            }
        } else {
            analogAddressForChart = dataBaseTable.getSelectionModel().getSelectedItem().getIoa();
            openLineChartWindow();
        }
    }

    private void openLineChartWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/LineChart.fxml"));
            AnchorPane baseAppPane = loader.load();
            Stage lineChartStage = new Stage();
            Scene scene = new Scene(baseAppPane);
            lineChartStage.setScene(scene);
            lineChartStage.setResizable(true);
            lineChartController = loader.getController();
            lineChartController.setTitle(analogAddressForChart);
            lineChartController.setMvc(this);
            scene.getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
            lineChartStage.show();
            lineChartStage.setOnCloseRequest(lineChartController.getCloseEventHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLineChartPoint(ProtocolDataModel protocolDataModel) {
        lineChartController.addLineChartPoint(protocolDataModel, analogAddressForChart);
    }

    private void switchTheme(String styleSheet) {
        currentTheme = styleSheet;
        mainPane.getScene().getStylesheets().clear();
        mainPane.getScene().setUserAgentStylesheet(null);
        mainPane.getScene().getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
    }

    //Скачивание образца банка данных
    @FXML
    private void downloadDataBaseTemplate() {
        FileManager fileManager = new FileManager();
        fileManager.downloadExcelFileTo("/dataBase/DB.xlsm");
    }

    @FXML
    private void openAboutWindow() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/AboutWindow.fxml"));
            AnchorPane aboutPane = loader.load();
            Stage aboutStage = new Stage();
            Scene scene = new Scene(aboutPane);
            aboutStage.setScene(scene);
            aboutStage.setResizable(false);
            aboutStage.setTitle("О программе");
            aboutStage.setMaximized(false);
            aboutStage.setAlwaysOnTop(true);
            aboutStage.getScene().getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
            aboutStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Message{
    enum MessageType{
        ERROR, INFO, SUCCESS;
    }
}