package org.openmuc.j60870.gui.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.WindowEvent;
import org.openmuc.j60870.gui.model.ProtocolDataModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LineChartController {
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    private int maxPoints = 20;
    private XYChart.Series<String, Double> series;
    private MainWindowController mvc;
    private Double value = 0.0;
    private ScheduledExecutorService scheduledExecutorService;
    private SimpleDateFormat simpleDateFormat;
    private boolean isRealTimeChart;

    @FXML
    private LineChart<String, Double> lineChart;
    @FXML
    private Label apertureLabel, lastValueLabel, maxPointLabel;
    @FXML
    private Slider maxPointSlider;

    public void setTitle(Integer title) {
        series.setName("ТИ " + title);
    }

    public void setIsRealTimeChart(boolean isRealTimeChart) {
        this.isRealTimeChart = isRealTimeChart;
    }

    public void setMvc(MainWindowController mvc) {
        this.mvc = mvc;
    }
    private final EventHandler<WindowEvent> closeEventHandler = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if (isRealTimeChart) {
                scheduledExecutorService.shutdownNow();
            }
        }
    };
    public EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }

    @FXML
    private void startButtonPressed() {
        mvc.isGetToChart = true;
    }
    @FXML
    private void stopButtonPressed() {
        mvc.isGetToChart = false;
        setLastValue("0.0");
    }

    public void setAperture(Double aperture) {
        aperture = Math.abs(aperture);
        apertureLabel.setText(String.format("%.3f", aperture));
    }

    public void setLastValue(String lastValue) {
        this.value = Double.parseDouble(lastValue);
        if (!isRealTimeChart) {
            addLineChartPoint(this.value);
        }
        lastValueLabel.setText(lastValue);
    }

    private void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    @FXML
    private void initialize() {
        xAxis.setLabel("Time/ms");
        xAxis.setAnimated(true);
        yAxis.setLabel("Value");
        yAxis.setAnimated(true);
        lineChart.setAnimated(true);
        series = new XYChart.Series<>();
        lineChart.getData().add(series);

        maxPointLabel.setText("20");
        maxPointSlider.setMax(100);
        maxPointSlider.setMin(5);
        maxPointSlider.setValue(20);
        maxPointSlider.setShowTickMarks(true);
        maxPointSlider.setSnapToTicks(false);
        maxPointSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            maxPointLabel.setText(newValue.intValue() + "");
            setMaxPoints(newValue.intValue());
        });
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    public void realTimeChart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(this::addLineChartPoint), 0, 1, TimeUnit.SECONDS);
    }

    public void addLineChartPoint() {
        Date now = new Date();
        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), value));
        if (series.getData().size() > maxPoints)
            series.getData().remove(0, series.getData().size() - maxPoints - 1);
    }

    public void addLineChartPoint(Double value) {
        Date now = new Date();
        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), value));
        if (series.getData().size() > maxPoints)
            series.getData().remove(0, series.getData().size() - maxPoints - 1);
    }

    public void addLineChartPoint(ProtocolDataModel protocolDataModel, Integer analogAddressForChart) {
        if (protocolDataModel.getProtAddress().equals(analogAddressForChart)) {
            Double newValue = Double.parseDouble(protocolDataModel.getProtValue());
            Double aperture = newValue - value;
            value = newValue;
            Platform.runLater(()->{
                setAperture(aperture);
                setLastValue(protocolDataModel.getProtValue());
            });
        }
    }
}
