package org.openmuc.j60870.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.openmuc.j60870.ClientConnectionBuilder;
import org.openmuc.j60870.gui.app.Client104;
import org.openmuc.j60870.gui.app.SubstationDataBase;
import org.openmuc.j60870.gui.customUI.DifferentDisplayComboBox;
import org.openmuc.j60870.gui.model.*;
import org.openmuc.j60870.gui.utilities.*;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.internal.cli.CliConParameter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Map;

import static org.openmuc.j60870.gui.utilities.AppsLauncher.App.*;

public class MainWindowController {
    // Константы
    private static final String DARK_THEME = "/view/DarkThemeRoot.css";
    private static final String LIGHT_THEME = "/view/LightThemeRoot.css";
    private static final String ERROR_BACKGROUND_COLOR = "red";
    private static final String SUCCESS_BACKGROUND_COLOR = "#336600";
    private static final String MODBUS_RESOURCE_ROOT = "docs/modbus/";
    private static final String DOCUMENTS_RESOURCE_ROOT = "docs/iec/";

    // UI компоненты
    @FXML private AnchorPane mainPane, parameterPane;
    @FXML private TabPane parameterTabPane, dataTabPane;
    @FXML private Tab dataTab;
    @FXML private Button resizeButton, writeExcelButton, protocolButton, startButton;
    @FXML private TextField ipField, portField, asduField, t1Field, t2Field, t3Field;
    @FXML private TextField hourField, minuteField, secondField, tuAddressField, kField, wField, filterField;
    @FXML private Label dataBaseName, substationNumberLabel, substationAddressLabel;
    @FXML private ToolBar topBar;
    @FXML private VBox filterBox;
    @FXML private ComboBox<String> aSduLengthBox, ioaLengthBox;
    @FXML private ComboBox<DifferentDisplayComboBox.Item> interfacesComboBox;
    @FXML private CheckBox pcTimeBox, getToDataBaseCheckBox, filterTypeCheckBox;
    @FXML private DatePicker datePicker;
    @FXML private TableView<DataModel> dataBaseTable;
    @FXML private TableColumn<DataModel, String> dataNameParamColumn, dataValueColumn, dataQualityColumn, dataCheckColumn;
    @FXML private TableColumn<DataModel, Integer> dataIoaColumn;
    @FXML private TableView<ProtocolDataModel> protocolTable, staticTable;
    @FXML private TableColumn<ProtocolDataModel, String> protTimeColumn, protTypeColumn, protCauseColumn;
    @FXML private TableColumn<ProtocolDataModel, String> protValueColumn, protQualityColumn, protTimeTagColumn;
    @FXML private TableColumn<ProtocolDataModel, Integer> protAsduColumn, protAddressColumn;
    @FXML private TableColumn<ProtocolDataModel, Integer> staticAsduColumn, staticAddressColumn;
    @FXML private TableColumn<ProtocolDataModel, String> staticNameColumn, staticTypeColumn, staticCauseColumn;
    @FXML private TableColumn<ProtocolDataModel, String> staticValueColumn, staticQualityColumn, staticTimeColumn, staticClientTimeColumn;
    @FXML private RadioButton tsRadioButton, tiRadioButton, tuRadioButton;
    @FXML private MenuBar menuBar;
    @FXML private Menu fileMenu, toolsMenu, docsMenu;
    @FXML private MenuItem openBDMenuItem, darkTheme, lightTheme, aboutMenuItem, emulator104MenuItem, cmdMenuItem,
            openNetscan, openPutty;
    @FXML private TextFlow consoleTextFlow;
    @FXML private ScrollPane consolePane;

    // Модели и Сервисы
    private final DifferentDisplayComboBox differentDisplayComboBox = new DifferentDisplayComboBox();
    private final BooleanProperty isGetToChart = new SimpleBooleanProperty(false);
    private final ToggleGroup dataBaseListSelectGroup = new ToggleGroup();
    private final Client104 client104 = new Client104(this);
    private final UtilNet utilNet = new UtilNet();
    private final ConsolePrinter consolePrinter = new ConsolePrinter();
    private final Validator validator = new Validator();
    private final AppsLauncher appsLauncher = new AppsLauncher();
    private final FileManager fileManager = new FileManager();

    // Переменные
    private String currentTheme;
    private String defaultStyleIpField;
    private ObservableList<ObservableList<DataModel>> dataBaseList = FXCollections.observableArrayList();
    private Integer analogAddressForChart;
    private LineChartController lineChartController;
    private boolean selectCmd;
    private boolean stateOnCmd;
    private Integer commonAddressParam;
    private int indexListDataBase;
    private SubstationDataBase substationDataBase;

    // Коллекции данных
    private final ObservableList<String> aSduLengthList = FXCollections.observableArrayList("1 Byte", "2 Bytes");
    private final ObservableList<String> ioaLengthList = FXCollections.observableArrayList("1 Byte", "2 Bytes", "3 Bytes");

    /* Методы инициализации */
    @FXML
    private void initialize() {
        setupInitialState();
        setupEventHandlers();
        setupTableColumns();
        setupComboBoxes();
        setupTooltips();
        setupContextMenus();
        setupMenu();
    }

    private void setupInitialState() {
        currentTheme = DARK_THEME;
        commonAddressParam = 1;
        defaultStyleIpField = ipField.getStyle();
        datePicker.setValue(LocalDate.now());
        setCurrentTime();
    }

    private void setCurrentTime() {
        hourField.setText(String.valueOf(Calendar.HOUR));
        minuteField.setText(String.valueOf(Calendar.MINUTE));
        secondField.setText(String.valueOf(Calendar.SECOND));
    }

    /* Настройка обработчиков событий */
    private void setupEventHandlers() {
        setupIpFieldValidation();
        setupPortFieldValidation();
        setupConsoleAutoScroll();
        setupDataBaseListSelection();
        setupMenuActions();
        setupDragAndDrop();
        setupFilterTypeToggle();
        setupResizeButton();
    }

    private void setupIpFieldValidation() {
        ipField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV && !validator.validateIpAddress(ipField.getText())) {
                ipField.setStyle("-fx-background-color: " + ERROR_BACKGROUND_COLOR);
                printConsoleErrorMessage("Некорректное значение в поле IP адреса");
            } else {
                ipFieldResetStyle();
            }
        });
    }

    private void setupPortFieldValidation() {
        portField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV && !validator.validatePort(portField.getText())) {
                portField.setStyle("-fx-background-color: " + ERROR_BACKGROUND_COLOR);
                printConsoleErrorMessage("Некорректное значение в поле порта");
            } else {
                portField.setStyle(defaultStyleIpField);
            }
        });
    }

    private void setupConsoleAutoScroll() {
        consoleTextFlow.getChildren().addListener((ListChangeListener<? super Node>) change -> {
            consoleTextFlow.requestLayout();
            consolePane.requestLayout();
            consolePane.setVvalue(consolePane.getVmax());
        });
    }

    private void setupDataBaseListSelection() {
        tsRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tsRadioButton.setDisable(true);
        tsRadioButton.setSelected(true);
        tiRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tiRadioButton.setDisable(true);
        tuRadioButton.setToggleGroup(dataBaseListSelectGroup);
        tuRadioButton.setDisable(true);

        dataBaseListSelectGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tsRadioButton) {
                handleTsRadioButtonSelection();
            } else if (newValue == tiRadioButton) {
                handleTiRadioButtonSelection();
            } else if (newValue == tuRadioButton) {
                handleTuRadioButtonSelection();
            }
        });
    }

    private void setupMenu() {
        Map<String, Map<String, String>> modbusResourceStructure = fileManager.getResourceStructure(MODBUS_RESOURCE_ROOT);
        Map<String, Map<String, String>> iecResourceStructure = fileManager.getResourceStructure(DOCUMENTS_RESOURCE_ROOT);
        Menu manualsMenu = new Menu("Справочники");
        Menu modbusMenu = new Menu("Карты Modbus");
        Menu iecMenu = new Menu("ГОСТы");
        manualsMenu.getItems().add(modbusMenu);
        manualsMenu.getItems().add(iecMenu);
        buildMenuFromStructure(iecMenu, iecResourceStructure);
        buildMenuFromStructure(modbusMenu, modbusResourceStructure);
        menuBar.getMenus().add(manualsMenu);
    }

    private void setupMenuActions() {
        openBDMenuItem.setOnAction(event -> openExcelFile());
        aboutMenuItem.setOnAction(event -> openAboutWindow());
        emulator104MenuItem.setOnAction(event -> openServer());
        cmdMenuItem.setOnAction(event -> openCmdWindow());
        darkTheme.setOnAction(event -> switchTheme(DARK_THEME));
        lightTheme.setOnAction(event -> switchTheme(LIGHT_THEME));
        openNetscan.setOnAction(event -> openApps(NETSCAN));
        openPutty.setOnAction(event -> openApps(PUTTY));
    }

    private void setupDragAndDrop() {
        dataTabPane.setOnDragOver(event -> {
            if (event.getGestureSource() != dataTabPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        dataTabPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                openExcelFile(db.getFiles().get(0));
            }
            event.setDropCompleted(db.hasFiles());
            event.consume();
        });
    }

    private void setupFilterTypeToggle() {
        filterTypeCheckBox.selectedProperty().addListener(event -> filterTypeCheckBox.setText(filterTypeCheckBox.isSelected() ? "Содержит" : "Равно"));
    }

    private void setupResizeButton() {
        resizeButton.setOnAction(event -> resizePane());
    }

    /* Конфигурация таблиц */
    private void setupTableColumns() {
        configureProtocolTableColumns();
        configureStaticTableColumns();
        configureDataBaseTableColumns();

        protocolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataBaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        staticTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataBaseTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dataCheckColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void configureProtocolTableColumns() {
        protTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
        protTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
        protCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
        protAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
        protAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
        protValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
        protValueColumn.setCellFactory(tc -> setValueColumnStyle());
        protQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
        protQualityColumn.setCellFactory(tc -> setQualityColumnStyle());
        protTimeTagColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
    }

    private void configureStaticTableColumns() {
        staticAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
        staticAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
        staticNameColumn.setVisible(false);
        staticTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
        staticCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
        staticValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
        staticValueColumn.setCellFactory(tc -> setValueColumnStyle());
        staticValueColumn.setCellFactory(tc -> setAnimatedValueColumnStyle());
        staticQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
        staticQualityColumn.setCellFactory(tc -> setQualityColumnStyle());
        staticTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
        staticClientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
    }

    private void configureDataBaseTableColumns() {
        dataNameParamColumn.setCellValueFactory(cellData -> cellData.getValue().nameOfParamProperty());
        dataIoaColumn.setCellValueFactory(cellData -> cellData.getValue().ioaProperty().asObject());
        dataValueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        dataQualityColumn.setCellValueFactory(cellData -> cellData.getValue().qualityProperty());
        dataCheckColumn.setCellValueFactory(new PropertyValueFactory<>("check"));
    }

    /* Настройка UI компонентов */
    private void setupComboBoxes() {
        aSduLengthBox.setItems(aSduLengthList);
        aSduLengthBox.setValue("2 Byte");
        ioaLengthBox.setItems(ioaLengthList);
        ioaLengthBox.setValue("3 Bytes");
        setInterfacesComboBoxText();
    }

    private void setInterfacesComboBoxText() {
        differentDisplayComboBox.createComboBox(interfacesComboBox, utilNet.getInterfacesIpItemsForComboBox());
    }

    private void setupTooltips() {
        t1Field.setTooltip(new Tooltip("Тайм-аут при посылке или \nтестировании APDU (по умол. 15с)"));
        t2Field.setTooltip(new Tooltip("Тайм-аут для подтверждения в случае \nотсутствия сообщения с данными (по умол. 10с)"));
        t3Field.setTooltip(new Tooltip("Тайм-аут для посылки блоков тестирования \nв случае долгого простоя (по умол. 20с)"));
        kField.setTooltip(new Tooltip("Максимальная разность между переменной состояния \nпередачи и номером последнего подтвержденного APDU (по умол. 12)"));
        wField.setTooltip(new Tooltip("Последнее подтверждение после приема \nw APDU формата I (по умол. 8)"));
    }

    private void setupContextMenus() {
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    /* Обработчики выбора листов БД */
    private void handleTsRadioButtonSelection() {
        indexListDataBase = 0;
        initDataBase(dataBaseList.get(0));
        dataBaseTable.setContextMenu(null);
    }

    private void handleTiRadioButtonSelection() {
        indexListDataBase = 1;
        initDataBase(dataBaseList.get(1));
        dataBaseTable.setContextMenu(tiContextMenu(dataBaseTable));
    }

    private void handleTuRadioButtonSelection() {
        indexListDataBase = 2;
        initDataBase(dataBaseList.get(2));
        dataBaseTable.setContextMenu(tuContextMenu());
    }

    /* Создание контекстных меню */
    private ContextMenu tiContextMenu(TableView tableView) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem openLineChartMenuItem = new MenuItem("Открыть график");
        MenuItem openRealTimeLineChartMenuItem = new MenuItem("Открыть RealTime график");

        openLineChartMenuItem.setOnAction(event -> {
            setIsGetToChart(true);
            startLineChart(tableView);
            if (lineChartController != null) {
                Platform.runLater(() -> lineChartController.addLineChartPoint());
            }
        });

        openRealTimeLineChartMenuItem.setOnAction(event -> {
            setIsGetToChart(true);
            startLineChart(tableView);
            if (lineChartController != null) {
                lineChartController.setIsRealTimeChart(true);
                Platform.runLater(() -> lineChartController.realTimeChart());
            }
        });

        contextMenu.getItems().addAll(openLineChartMenuItem, openRealTimeLineChartMenuItem);
        return contextMenu;
    }

    private ContextMenu tuContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem onMenuItem = new MenuItem("Включить");
        MenuItem offMenuItem = new MenuItem("Отключить");

        onMenuItem.setOnAction(event -> sendTuCommandWithDelay(true));

        offMenuItem.setOnAction(event -> sendTuCommandWithDelay(false));

        contextMenu.getItems().addAll(onMenuItem, offMenuItem);
        return contextMenu;
    }

    private void sendTuCommandWithDelay(boolean state) {
        DataModel selectedItem = dataBaseTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            client104.sendTuCommand(commonAddressParam, selectedItem.getIoa(), true, state);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            client104.sendTuCommand(commonAddressParam, selectedItem.getIoa(), false, state);
        }
    }

    /* Управление соединением */
    @FXML
    private void startButtonClicked() {
        new Thread(this::startButtonClickedAction).start();
    }

    private void startButtonClickedAction() {
        if (client104.isConnectionExist()) {
            client104.closeConnection();
        }

        String ipServer = ipField.getText();
        if (ipServer.isEmpty()) {
            printConsoleErrorMessage("Не указан IP адрес");
            return;
        }

        if (portField.getText().isEmpty()) {
            printConsoleErrorMessage("Не указан порт");
            return;
        }

        int portServer = Integer.parseInt(portField.getText());
        printConsoleInfoMessage("Подключение к " + ipServer + ":" + portServer);

        if (isHostAvailable(ipServer, portServer)) {
            startClient(ipServer, portServer);
        } else {
            printConsoleErrorMessage("Указанный сервер не доступен");
        }
    }

    @FXML
    private void stopButtonClicked() {
        if (client104.isConnectionOpen()) {
            closeConnection();
        }
    }

    private static boolean isHostAvailable(String ip, int port) {
        try (Socket ignored = new Socket(ip, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void startClient(String ip, int port) {
        commonAddressParam = Integer.parseInt(asduField.getText());
        ClientConnectionBuilder clientConnectionBuilder = createClientConnectionBuilder(ip, port);

        if (clientConnectionBuilder != null) {
            client104.createConnection(clientConnectionBuilder);
            if (client104.isConnectionOpen()) {
                handleSuccessfulConnection(ip, port);
            } else {
                printConsoleErrorMessage("Невозможно подключиться к " + ip + ":" + port);
            }
        }
    }

    private ClientConnectionBuilder createClientConnectionBuilder(String ip, int port) {
        CliConParameter cliConParameter = new CliConParameter();
        cliConParameter.setAsduLength(Integer.parseInt(aSduLengthBox.getValue().substring(0, 1)));
        cliConParameter.setIoaLength(Integer.parseInt(ioaLengthBox.getValue().substring(0, 1)));
        cliConParameter.setT1(Integer.parseInt(t1Field.getText()) * 1000);
        cliConParameter.setT2(Integer.parseInt(t2Field.getText()) * 1000);
        cliConParameter.setT3(Integer.parseInt(t3Field.getText()) * 1000);
        cliConParameter.setkParam(Integer.parseInt(kField.getText()));
        cliConParameter.setwParam(Integer.parseInt(wField.getText()));
        cliConParameter.setPort(port);

        try {
            InetAddress address = InetAddress.getByName(ip);
            return client104.createClientConnectionBuilder(address, cliConParameter);
        } catch (UnknownHostException e) {
            printConsoleErrorMessage("Ошибка при определении IP адреса: " + e.getMessage());
            return null;
        }
    }

    private void handleSuccessfulConnection(String ip, int port) {
        printConsoleSuccessMessage("Открыто соединение с " + ip + ":" + port);
        ipField.setStyle("-fx-background-color: " + SUCCESS_BACKGROUND_COLOR);
    }

    public void closeConnection() {
        printConsoleInfoMessage("Соединение остановлено");
        ipField.setStyle("-fx-background-color: derive(#373e43, 35%)");
        client104.closeConnection();
    }

    /* Меотды для команд */
    @FXML
    public void giButtonClicked() {
        client104.sendGI(commonAddressParam);
        printConsoleInfoMessage("Отправка команды общего опроса станции.");
    }

    @FXML
    public void clockSyncButton() {
        IeTime56 timeToSync = pcTimeBox.isSelected() ?
                new IeTime56(System.currentTimeMillis()) :
                createCustomTime();

        client104.sendClockSynchronization(commonAddressParam, timeToSync);
        printConsoleInfoMessage("Отправка команды синхронизации времени");
    }

    private IeTime56 createCustomTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                datePicker.getValue().getYear(),
                datePicker.getValue().getMonthValue() - 1,
                datePicker.getValue().getDayOfMonth(),
                Integer.parseInt(hourField.getText()),
                Integer.parseInt(minuteField.getText()),
                Integer.parseInt(secondField.getText())
        );
        return new IeTime56(calendar.getTimeInMillis());
    }

    @FXML
    public void selOnButton() {
        prepareCommand(true, true);
    }

    @FXML
    public void selOffButton() {
        prepareCommand(true, false);
    }

    @FXML
    public void exeOnButton() {
        prepareCommand(false, true);
    }

    @FXML
    public void exeOffButton() {
        prepareCommand(false, false);
    }

    private void prepareCommand(boolean select, boolean state) {
        selectCmd = select;
        stateOnCmd = state;
        cmdButtonClicked();
    }

    public void cmdButtonClicked() {
        try {
            client104.sendTuCommand(
                    commonAddressParam,
                    Integer.parseInt(tuAddressField.getText()),
                    selectCmd,
                    stateOnCmd
            );
        } catch (NumberFormatException e) {
            printConsoleErrorMessage("Попытка отправки команды ТУ без обязательного параметра Адрес_ТУ");
        } catch (Exception e) {
            printConsoleErrorMessage("Ошибка при отправке команды");
        }
    }

    /* Действия с БД */
    @FXML
    public void openExcelFile() {
        substationDataBase = new SubstationDataBase(this);
        dataBaseList = substationDataBase.openFile();
        initializeExcelFile(dataBaseList);
    }

    private void openExcelFile(File file) {
        substationDataBase = new SubstationDataBase(this);
        dataBaseList = substationDataBase.openFile(file);
        initializeExcelFile(dataBaseList);
    }

    private void initializeExcelFile(ObservableList<ObservableList<DataModel>> dataBaseList) {
        if (dataBaseList != null) {
            updateUIForSuccessfulFileOpen();
            initDataBase(dataBaseList.get(0));
            dataBaseTable.setItems(dataBaseList.get(0));
        } else {
            dataBaseName.setText("Невозможно открыть файл");
        }
    }

    private void updateUIForSuccessfulFileOpen() {
        dataBaseName.setText("Открыт файл: " + substationDataBase.getDataBaseName());
        tsRadioButton.setDisable(false);
        tiRadioButton.setDisable(false);
        tuRadioButton.setDisable(false);
        getToDataBaseCheckBox.setDisable(false);
        writeExcelButton.setDisable(false);
        protocolButton.setDisable(false);
    }

    @FXML
    public void writeExcel() {
        substationDataBase.saveDataToFile(dataBaseList);
    }

    /* Вспомогательные методы графического интерфейса */
    private void resizePane() {
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

    public void ipFieldResetStyle() {
        ipField.setStyle(defaultStyleIpField);
    }

    public void setIpFieldStyle(boolean connectionStatus) {
        System.out.println("connectionStatus is: " + connectionStatus);
        ipField.setStyle(connectionStatus ?
                "-fx-background-color: " + SUCCESS_BACKGROUND_COLOR :
                "-fx-background-color: derive(#373e43, 35%)"
        );
    }

    /* Методы вывода сообщений в консоль приложения */
    public void printConsoleInfoMessage(String... strings) {
        printMessage(Message.MessageType.INFO, strings);
    }

    public void printConsoleErrorMessage(String... strings) {
        printMessage(Message.MessageType.ERROR, strings);
    }

    public void printConsoleSuccessMessage(String... strings) {
        printMessage(Message.MessageType.SUCCESS, strings);
    }

    public void printMessage(Message.MessageType type, String... strings) {
        ObservableList<? extends Node> list = consoleTextFlow.getChildren();
        for (String string : strings) {
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

    /* Стили ячеек таблицы */
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
            if ((oldItem == null && newItem == null) || (oldItem != null && oldItem.equals(newItem))) {
                return;
            }

            ProtocolDataModel rowData = cell.getTableRow() != null ? (ProtocolDataModel) cell.getTableRow().getItem() : null;
            if (rowData == null || !rowData.isNewlyAdded()) {
                return;
            }
            cell.setStyle("-fx-background-color: transparent");
            animateCell(cell, newItem);
        });
        return cell;
    }

    private void animateCell(TextFieldTableCell<ProtocolDataModel, String> cell, String newItem) {
        if (newItem != null) {
            cell.getStyleClass().add("animated-gradient");
            ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(baseColor, Color.rgb(200, 40, 0))),
                    new KeyFrame(Duration.millis(500), new KeyValue(baseColor, Color.rgb(40, 200, 0)))
            );

            baseColor.addListener((ob, oldColor, newColor) ->
                    cell.setStyle(String.format("-gradient-base: #%02x%02x%02x; ",
                            (int)(newColor.getRed()*255),
                            (int)(newColor.getGreen()*255),
                            (int)(newColor.getBlue()*255)))
            );

            timeline.setAutoReverse(true);
            timeline.setCycleCount(5);
            timeline.play();
            timeline.setOnFinished(e -> setTSCellColor(newItem, cell));
        }
    }

    private void setTSCellColor(String newItem, TextFieldTableCell<ProtocolDataModel, String> cell) {
        String style;
        switch (newItem) {
            case "ON":
                style = "-fx-background-color: rgba(200, 40, 0, 0.6) ;";
                break;
            case "OFF":
                style = "-fx-background-color: rgba(40, 200, 0, 0.6) ;";
                break;
            case "INDETERMINATE_OR_INTERMEDIATE":
            case "INDETERMINATE":
                style = "-fx-background-color: rgba(200, 200, 0, 0.6) ;";
                break;
            default:
                style = "-fx-background-color: transparent";
                break;
        }
        cell.setStyle(style);
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

    /* Управление окнами */
    private void openAboutWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AboutWindow.fxml"));
            AnchorPane aboutPane = loader.load();
            Stage aboutStage = createStage(aboutPane, "О программе", false);
            aboutStage.show();
        } catch (IOException e) {
            printConsoleErrorMessage("Ошибка при открытии окна 'О программе'");
        }
    }

    private void openServer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ServerWindow.fxml"));
            AnchorPane serverPane = loader.load();
            ServerWindowController controller = loader.getController();
            Stage serverStage = createStage(serverPane, "Сервер", false);
            controller.setStage(serverStage);
            serverStage.show();
        } catch (IOException e) {
            printConsoleErrorMessage("Ошибка при открытии эмулятора");
        }
    }

    private void openCmdWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CmdWindow.fxml"));
            AnchorPane cmdPane = loader.load();
            Stage cmdStage = createStage(cmdPane, "Командная строка", true);
            cmdStage.show();
        } catch (IOException e) {
            printConsoleErrorMessage("Ошибка при открытии командной строки");
        }
    }

    private void openApps(AppsLauncher.App app) {
        appsLauncher.launchApp(app.getResourcePath());
    }

    private Stage createStage(AnchorPane pane, String title, boolean resizable) {
        Stage stage = new Stage();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setResizable(resizable);
        stage.setTitle(title);
        scene.getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
        return stage;
    }

    @FXML
    private void openProtocolWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProtocolWindow.fxml"));
            AnchorPane protocolPane = loader.load();
            Stage protocolStage = createStage(protocolPane, "Протокол", true);
            protocolStage.initModality(Modality.APPLICATION_MODAL);
            protocolStage.showAndWait();
        } catch (IOException e) {
            printConsoleErrorMessage("Ошибка при открытии протокола: " + e.getMessage());
        }
    }

    /* Методы для графиков */
    private void startLineChart(TableView tableView) {
        if (tableView.equals(protocolTable)) {
            handleProtocolTableChart();
        } else {
            handleDataBaseTableChart();
        }
    }

    private void handleProtocolTableChart() {
        ProtocolDataModel selectedItem = protocolTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null && isAnalogValue(selectedItem.getProtType())) {
            analogAddressForChart = selectedItem.getProtAddress();
            openLineChartWindow();
        } else {
            printConsoleInfoMessage("Просмотр графика доступен только для аналоговых значений");
        }
    }

    private boolean isAnalogValue(String type) {
        return type.equals("36 (Measured value, short floating point number with time tag CP56Time2a)") ||
                type.equals("13 (Measured value, short floating point number)");
    }

    private void handleDataBaseTableChart() {
        DataModel selectedItem = dataBaseTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            analogAddressForChart = selectedItem.getIoa();
            openLineChartWindow();
        }
    }

    private void openLineChartWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LineChart.fxml"));
            AnchorPane baseAppPane = loader.load();
            Stage lineChartStage = createStage(baseAppPane, "График", true);
            lineChartController = loader.getController();
            lineChartController.setTitle(analogAddressForChart);
            lineChartController.setMvc(this);
            lineChartStage.setOnCloseRequest(lineChartController.getCloseEventHandler());
            lineChartStage.show();
        } catch (IOException e) {
            printConsoleErrorMessage("Ошибка при открытии графика: " + e.getMessage());
        }
    }

    public void addLineChartPoint(ProtocolDataModel protocolDataModel) {
        if (lineChartController != null) {
            lineChartController.addLineChartPoint(protocolDataModel, analogAddressForChart);
        }
    }

    /* Управление темами оформления приложения */
    private void switchTheme(String styleSheet) {
        currentTheme = styleSheet;
        mainPane.getScene().getStylesheets().clear();
        mainPane.getScene().setUserAgentStylesheet(null);
        mainPane.getScene().getStylesheets().add(getClass().getResource(currentTheme).toExternalForm());
    }

    /* Геттеры, Сеттеры */
    public BooleanProperty isGetToChartProperty() {
        return isGetToChart;
    }

    public boolean isGetToChart() {
        return isGetToChart.get();
    }

    public void setIsGetToChart(boolean value) {
        isGetToChart.set(value);
    }

    public boolean isGetToDataBase() {
        return getToDataBaseCheckBox.isSelected();
    }

    /* Вспомогательные методы */
    @FXML
    private void downloadDataBaseTemplate() {
        FileManager fileManager = new FileManager();
        fileManager.downloadExcelFileTo("/dataBase/DB.xlsm", writeExcelButton.getScene().getWindow());
    }

    @FXML
    private void clearDataTable() {
        staticTable.setItems(FXCollections.observableArrayList());
        protocolTable.setItems(FXCollections.observableArrayList());
        client104.clearDataTable();
    }

    private TextFieldTableCell<ProtocolDataModel, String> createTransparentCell() {
        TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
        cell.setStyle("-fx-background-color: transparent");
        return cell;
    }

    @FXML
    private void pingIpAddress() {
        utilNet.pingIp(ipField.getText());
    }

    @FXML
    private void changeLocalIp() {
        utilNet.changeLocalIp(interfacesComboBox.getAccessibleText());
    }

    public void setSubstationParamFromBD(SubstationParamModel substationParamModel) {
        ipField.setText(substationParamModel.getIpParam());
        portField.setText(String.valueOf(substationParamModel.getPortParam()));
        substationNumberLabel.setText(substationParamModel.getSubstationTypeParam() + " " +
                substationParamModel.getSubstationNumberParam());
        substationAddressLabel.setText(substationParamModel.getSubstationAddressParam());
    }

    @FXML
    private void swipeListLeft() {
        if (indexListDataBase < 2) {
            indexListDataBase++;
            initDataBase(dataBaseList.get(indexListDataBase));
            setSelectedToggle(indexListDataBase);
        }
    }

    @FXML
    private void swipeListRight() {
        if (indexListDataBase > 0) {
            indexListDataBase--;
            initDataBase(dataBaseList.get(indexListDataBase));
            setSelectedToggle(indexListDataBase);
        }
    }

    private void setSelectedToggle(int index) {
        Toggle toggle;
        switch (index) {
            case 0:
                toggle = tsRadioButton;
                break;
            case 1:
                toggle = tiRadioButton;
                break;
            case 2:
                toggle = tuRadioButton;
                break;
            default:
                toggle = null;
                break;
        }
        if (toggle != null) {
            dataBaseListSelectGroup.selectToggle(toggle);
        }
    }

    private void buildMenuFromStructure(Menu parentMenu, Map<String, Map<String, String>> structure) {
        structure.forEach((folder, files) -> {
            if (folder.isEmpty()) {
                // Файлы в корне
                files.forEach((file, path) -> {
                    MenuItem item = new MenuItem(file);
                    item.setOnAction(e -> fileManager.handleResourceSelection(path));
                    parentMenu.getItems().add(item);
                });
            } else {
                // Подпапки
                Menu subMenu = new Menu(folder);
                files.forEach((file, path) -> {
                    MenuItem item = new MenuItem(file);
                    item.setOnAction(e -> fileManager.handleResourceSelection(path));
                    subMenu.getItems().add(item);
                });
                parentMenu.getItems().add(subMenu);
            }
        });
    }

    /* Методы инициализации данных */
    public void initDataBase(ObservableList<DataModel> dataModelObservableList) {
        try {
            dataBaseTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            printConsoleErrorMessage("Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }

    public void initialize(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            FilteredList<ProtocolDataModel> filteredList = new FilteredList<>(dataModelObservableList, p -> true);
            setupFiltering(filteredList);

            SortedList<ProtocolDataModel> sortedDataList = new SortedList<>(filteredList);
            sortedDataList.comparatorProperty().bind(protocolTable.comparatorProperty());
            protocolTable.setItems(sortedDataList);
        } catch (NullPointerException e) {
            printConsoleErrorMessage("Ошибка при инициализации протокола: " + e.getMessage());
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    private void setupFiltering(FilteredList<ProtocolDataModel> filteredList) {
        filterTypeCheckBox.selectedProperty().addListener(event ->
                filteredList.setPredicate(this::setIOAFilter));

        filterField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(this::setIOAFilter));

        filteredList.setPredicate(this::setIOAFilter);
    }

    private boolean setIOAFilter(ProtocolDataModel protocolDataModel) {
        String filterText = filterField.getText();
        if (filterText == null || filterText.isEmpty()) {
            return true;
        }
        String ioa = String.valueOf(protocolDataModel.getProtAddress());
        return filterTypeCheckBox.isSelected() ?
                ioa.contains(filterText) :
                ioa.equals(filterText);
    }

    public void initStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            printConsoleErrorMessage("Ошибка при инициализации статических данных: " + e.getMessage());
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    public void initSingleStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            printConsoleErrorMessage("Ошибка при инициализации единичных статических данных: " + e.getMessage());
        }
        protocolTable.setContextMenu(tiContextMenu(protocolTable));
    }

    public Tab getDataTab() {
        return dataTab;
    }

    public ObservableList<ObservableList<DataModel>> getDataBaseList() {
        return dataBaseList;
    }

    public int getIndexListDataBase() {
        return indexListDataBase;
    }

    /* Inner Classes */
    private static class Message {
        enum MessageType {
            ERROR, INFO, SUCCESS
        }
    }
}