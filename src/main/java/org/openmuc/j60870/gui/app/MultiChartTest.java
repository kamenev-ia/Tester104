package org.openmuc.j60870.gui.app;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.openmuc.j60870.gui.model.DataModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiChartTest {

    private ScheduledExecutorService scheduledExecutorService;
    private SimpleDateFormat simpleDateFormat;
    private XYChart.Series series;
    private Double value = 0.0;
    private ObservableList<XYChart.Series> seriesList;
    private ObservableList<XYChart.Data> data;
    ObservableList<DataModel> dataModels;
    public void startChart(Stage primaryStage, ObservableList<DataModel> dataModels)  {
        this.dataModels = dataModels;
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MultiChart.fxml"));
            primaryStage.setTitle("MultiChart");

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            xAxis.setAnimated(true);
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Value");
            yAxis.setAnimated(true);

            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

            LineChart<String, Number> multiChart = new LineChart<>(xAxis, yAxis);
            multiChart.setTitle("MultiChartLine");
            multiChart.setAnimated(true);

            seriesList = FXCollections.observableArrayList();

            for (DataModel dm :
                    dataModels) {
                data = FXCollections.observableArrayList();
                series = new XYChart.Series();
                series.setName(dm.getIoa() + "");
                seriesList.add(series);
                addMultiChartPoint();
            }

            Scene scene = new Scene(multiChart, 600, 600);
            for (XYChart.Series s :
                    seriesList) {
                multiChart.getData().add(s);
            }
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void realTimeChart() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(this::addMultiChartPoint), 0, 1, TimeUnit.SECONDS);
    }

    public void addMultiChartPoint() {
        Date now = new Date();
        data.add(new XYChart.Data(simpleDateFormat.format(now), Math.random()));
        for (XYChart.Series s :
                seriesList) {
            s.setData(data);
        }
    }
}
