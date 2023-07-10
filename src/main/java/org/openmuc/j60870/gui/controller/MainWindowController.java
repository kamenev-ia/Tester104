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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.openmuc.j60870.*;
import org.openmuc.j60870.gui.app.Client104;
import org.openmuc.j60870.gui.app.ExcelConverter;
import org.openmuc.j60870.gui.model.DataModel;
import org.openmuc.j60870.gui.model.ProtocolDataModel;
import org.openmuc.j60870.gui.model.SubstationParamModel;
import org.openmuc.j60870.gui.utilities.ConsolePrinter;
import org.openmuc.j60870.gui.utilities.UtilNet;
import org.openmuc.j60870.gui.utilities.Validator;
import org.openmuc.j60870.ie.IeTime56;
import org.openmuc.j60870.internal.cli.*;

import java.io.IOException;
import java.net.*;
import java.time.LocalDate;
import java.util.Calendar;

public class MainWindowController {
    @FXML
    AnchorPane mainPane;
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
    private Button writeExcelButton, protocolButton;
    @FXML
    private CheckBox pcTimeBox, getToDataBaseCheckBox, isInspectMode;
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
    private MenuItem openBDMenuItem, darkTheme, lightTheme;
    @FXML
    private TextFlow textFlow;
    @FXML
    private ScrollPane consolePane;
    @FXML
    private ImageView imageView;

    public int indexListDataBase;
    public ExcelConverter excelConverter;
    public boolean isGetToChart;
    public ObservableList<DataModel> dataBaseData = FXCollections.observableArrayList();
    public ObservableList<ObservableList> dataBaseList = FXCollections.observableArrayList();

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

    public MainWindowController() {
    }

    @FXML
    private void initialize() {
        isGetToChart = false;
        //Установка значений для полей длин адресов ASDU и объекта информации
        asduLengthBox.setItems(asduLengthList);
        asduLengthBox.setValue("2 Byte");
        ioaLengthBox.setItems(ioaLengthList);
        ioaLengthBox.setValue("3 Bytes");
        commonAddressParam = 1;
        protocolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataBaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        staticTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String styleIpField = ipField.getStyle();
        ipField.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) {
                if (!validator.validateIpAddress(ipField.getText())) {
                    ipField.setStyle("-fx-background-color: red");
                    printConsoleErrorMessage("Некорректное значение в поле IP адреса");
                }
            } else {
                ipField.setStyle(styleIpField);
            }
        });
        String stylePortField = portField.getStyle();
        portField.focusedProperty().addListener((ov, oldV, newV) ->{
            if (!newV) {
                if (!validator.validatePort(portField.getText())) {
                    portField.setStyle("-fx-background-color: red");
                    printConsoleErrorMessage("Некорректное значение в поле порта");
                }
            } else {
                portField.setStyle(stylePortField);
            }
        });

        textFlow.getChildren().addListener(
                (ListChangeListener<? super Node>) ((change) -> {
                    textFlow.requestLayout();
                    consolePane.requestLayout();
                    consolePane.setVvalue(1.0f);
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

        //Смена темы оформления
        darkTheme.setOnAction(event -> switchTheme("/view/DarkThemeRoot.css"));
        lightTheme.setOnAction(event -> switchTheme("/view/LightThemeRoot.css"));
    }

    public void initialize(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            protTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            protTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            protCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            protAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            protAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            protValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            protValueColumn.setCellFactory(tc->{
                TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
                cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                    cell.setStyle("-fx-background-color: transparent");
                    if (newItem != null) {
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
                });
                return cell;
            });
            protQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            protQualityColumn.setCellFactory(tc->{
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
            });
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
        addContextMenu();
    }

    public void initStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            staticAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            staticNameColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            staticCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            staticValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticValueColumn.setCellFactory(tc->{
                TextFieldTableCell<ProtocolDataModel, String> cell = new TextFieldTableCell<>();
                cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                    cell.setStyle("-fx-background-color: transparent");
                    if (newItem != null) {
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
                });
                return cell;
            });
            staticQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            staticQualityColumn.setCellFactory(tc->{
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
            });
            staticTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
            staticClientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        addContextMenu();
    }

    public void initSingleStaticData(ObservableList<ProtocolDataModel> dataModelObservableList) {
        try {
            staticAsduColumn.setCellValueFactory(cellData -> cellData.getValue().protAsduProperty().asObject());
            staticAddressColumn.setCellValueFactory(cellData -> cellData.getValue().protAddressProperty().asObject());
            staticNameColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticTypeColumn.setCellValueFactory(cellData -> cellData.getValue().protTypeProperty());
            staticCauseColumn.setCellValueFactory(cellData -> cellData.getValue().protCauseProperty());
            staticValueColumn.setCellValueFactory(cellData -> cellData.getValue().protValueProperty());
            staticValueColumn.setCellFactory(tc->{
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
                        timeline.setOnFinished(e -> {
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
                        });
                    }
                });
                return cell;
            });
            staticQualityColumn.setCellValueFactory(cellData -> cellData.getValue().protQualityProperty());
            staticQualityColumn.setCellFactory(tc->{
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
            });
            staticTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeTagProperty());
            staticClientTimeColumn.setCellValueFactory(cellData -> cellData.getValue().protTimeProperty());
            staticTable.setItems(dataModelObservableList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        addContextMenu();
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
                startClient(ipServer, portServer);
            } else {
                printConsoleErrorMessage("Не указан порт");
            }
        } else {
            printConsoleErrorMessage("Не указан IP адрес");
        }
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
        this.dataBaseName.setText("Открыт файл: " + excelConverter.getDataBaseName());
        if (dataBaseData != null) {
            tsRadioButton.setDisable(false);
            tiRadioButton.setDisable(false);
            getToDataBaseCheckBox.setDisable(false);
            writeExcelButton.setDisable(false);
            protocolButton.setDisable(false);
//            Режим приемки пока не реализован
//            isInspectMode.setDisable(false);
        }
        initDataBase(dataBaseList.get(0));
        dataBaseTable.setItems(dataBaseList.get(0));
    }

    @FXML
    public void writeExcel() {
        excelConverter.fillExcel(dataBaseList);
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
                ipField.setStyle("-fx-background-color: #336600");
            } else {
                printConsoleErrorMessage("Невозможно подключиться к " + ip + ":" + port);
            }
        }
    }

    //Вывод информационного сообщения в консоль
    public void printConsoleInfoMessage(String... strings) {
        for (String string : strings) {
            ObservableList<? extends Node> list = textFlow.getChildren();
            consolePrinter.printInfoMessage(list, string);
        }
    }

    //Вывод сообщения об ошибке в консоль
    public void printConsoleErrorMessage(String... strings) {
        for (String string : strings) {
            ObservableList<? extends Node> list = textFlow.getChildren();
            consolePrinter.printErrorMessage(list, string);
        }
    }

    //Вывод сообщения об успешной операции в консоль
    public void printConsoleSuccessMessage(String... strings) {
        for (String string : strings) {
            ObservableList<? extends Node> list = textFlow.getChildren();
            consolePrinter.printSuccessMessage(list, string);
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
            } else if (dataBaseListSelectGroup.getSelectedToggle().equals(tiRadioButton)) {
                initDataBase(dataBaseList.get(1));
                indexListDataBase = 1;
            } else {
                initDataBase(dataBaseList.get(1));
                indexListDataBase = 2;
            }
        });
    }

    //Обработка свайпа влево. Смена листов БД (ТС <-> ТИ)
    @FXML
    private void swipeListLeft() {
        if (indexListDataBase < 1) {
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

    private void addContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem openLineChartMenuItem = new MenuItem("Открыть график");
        MenuItem openRealTimeLineChartMenuItem = new MenuItem("Открыть RealTime график");
        openLineChartMenuItem.setOnAction(event -> {
            this.isGetToChart = true;
            startLineChart();
            if (lineChartController != null) {
                Platform.runLater(()->lineChartController.addLineChartPoint());
            }
        });
        openRealTimeLineChartMenuItem.setOnAction(event -> {
            this.isGetToChart = true;
            startLineChart();
            if (lineChartController != null) {
                lineChartController.setIsRealTimeChart(true);
                Platform.runLater(()->lineChartController.realTimeChart());
            }
        });
        contextMenu.getItems().addAll(openLineChartMenuItem, openRealTimeLineChartMenuItem);
        protocolTable.setContextMenu(contextMenu);
    }

    private Double oldValue = 0.0;

    private void startLineChart() {
        if (protocolTable.getSelectionModel().getSelectedItem().getProtType().equals("36 (Measured value, short floating point number with time tag CP56Time2a)") || protocolTable.getSelectionModel().getSelectedItem().getProtType().equals("13 (Measured value, short floating point number)")) {
            analogAddressForChart = protocolTable.getSelectionModel().getSelectedItem().getProtAddress();
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
                scene.getStylesheets().add(mainPane.getScene().getStylesheets().get(0));
                lineChartStage.show();
                lineChartStage.setOnCloseRequest(lineChartController.getCloseEventHandler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            printConsoleInfoMessage("Просмотр графика доступен только для аналоговых значений");
        }
    }

    public void addLineChartPoint(ProtocolDataModel protocolDataModel) {
        if (protocolDataModel.getProtAddress().equals(analogAddressForChart)) {
            Double newValue = Double.parseDouble(protocolDataModel.getProtValue());
            Double aperture = newValue - oldValue;
            oldValue = newValue;
            Platform.runLater(()->{
                lineChartController.setAperture(aperture);
                lineChartController.setLastValue(protocolDataModel.getProtValue());
            });
        }
    }

    private void switchTheme(String styleSheet) {
        mainPane.getScene().getStylesheets().clear();
        mainPane.getScene().setUserAgentStylesheet(null);
        mainPane.getScene().getStylesheets().add(getClass().getResource(styleSheet).toExternalForm());
    }
}
