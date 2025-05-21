package org.openmuc.j60870.gui.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final BooleanProperty chartActive = new SimpleBooleanProperty(false);
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
        chartActive.bindBidirectional(mvc.isGetToChartProperty());
    }
    private final EventHandler<WindowEvent> closeEventHandler = event -> {
        stopRealTimeChart();
    };

    private void stopRealTimeChart(){
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdownNow();
            try {
                if (!scheduledExecutorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Не удалось корректно завершить выполнение задач");
                }
            } catch (InterruptedException e) {
                System.err.println("Ожидание завершения задач прервано: " + e.getMessage());
            }
            scheduledExecutorService = null;
        }
    }
    public EventHandler<WindowEvent> getCloseEventHandler() {
        return closeEventHandler;
    }

    @FXML
    private void startButtonPressed() {
        setChartActive(true);
    }
    @FXML
    private void stopButtonPressed() {
        setChartActive(false);
        stopRealTimeChart();
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
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
        scheduledExecutorService.scheduleAtFixedRate(() ->
                Platform.runLater(() -> {
                    if (isChartActive()) {
                        addLineChartPoint();
                    }
                }), 0, 1, TimeUnit.SECONDS);
    }

    public void addLineChartPoint() {
        Date now = new Date();
        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), value));
        int extraPoints = series.getData().size() - maxPoints;
        if (extraPoints > 0)
            series.getData().remove(0, extraPoints);
    }

    public void addLineChartPoint(Double value) {
        Date now = new Date();
        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(now), value));
        int extraPoints = series.getData().size() - maxPoints;
        if (extraPoints > 0)
            series.getData().remove(0, extraPoints);
    }

    public void addLineChartPoint(ProtocolDataModel protocolDataModel, Integer analogAddressForChart) {
        if (protocolDataModel.getProtAddress().equals(analogAddressForChart)) {
            String protValueStr = protocolDataModel.getProtValue();
            double newValue;
            try {
                if (protValueStr == null || protValueStr.trim().isEmpty()) {
                    throw new NumberFormatException("Полученое значение пустое или равно null");
                }
                newValue = Double.parseDouble(protValueStr);
            } catch (NumberFormatException e) {
                System.err.println("Ошибка преобразования полученного значения: " + protValueStr + ". " + e.getMessage());
                return;
            }
            double aperture = newValue - value;
            value = newValue;
            Platform.runLater(()->{
                setAperture(aperture);
                setLastValue(protocolDataModel.getProtValue());
            });
        }
    }

    public BooleanProperty chartActiveProperty() {
        return chartActive;
    }
    public boolean isChartActive() {
        return chartActive.get();
    }

    public void setChartActive(boolean value) {
        chartActive.set(value);
    }

}
