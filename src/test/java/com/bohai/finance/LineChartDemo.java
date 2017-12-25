package com.bohai.finance;


import java.time.LocalTime;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.chart.NumberAxis.DefaultFormatter;

public class LineChartDemo extends Application {
    
    private LineChart<String, Number> chart;

    private Series<String, Number> hourDataSeries;

    private Series<String, Number> minuteDataSeries;

    private CategoryAxis xAxis;

    private Timeline animation;

    private double hours = 0;

    private double minutes = 0;

    private double timeInHours = 0;

    private double prevY = 10;

    private double y = 10;

    public LineChartDemo() {


    }

 

    public Parent createContent() {

        xAxis = new CategoryAxis();

        final NumberAxis yAxis = new NumberAxis(0, 100, 10);

        chart = new LineChart<>(xAxis, yAxis);

        // setup chart

        final String stockLineChartCss =

            getClass().getResource("StockLineChart.css").toExternalForm();

        chart.getStylesheets().add(stockLineChartCss);

        chart.setCreateSymbols(false);

        chart.setAnimated(false);

        chart.setLegendVisible(false);

        chart.setTitle("ACME Company Stock");

        xAxis.setLabel("Time");
        
        xAxis.setMaxWidth(30);
        
        yAxis.setLabel("Share Price");

        yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, "¥", null));

        // add starting data

        hourDataSeries = new Series<>();

        hourDataSeries.setName("Hourly Data");

        minuteDataSeries = new Series<>();

        minuteDataSeries.setName("Minute Data");

        // create some starting data

        hourDataSeries.getData().add(new Data<String, Number>("",

                                                              35));

        minuteDataSeries.getData().add(new Data<String, Number>("14:15",

                                                                15));
        minuteDataSeries.getData().add(new Data<String, Number>("14:16",
                
                16));
        minuteDataSeries.getData().add(new Data<String, Number>("14:17",
                
                17));
        minuteDataSeries.getData().add(new Data<String, Number>("14:18",
                
                16));

        chart.getData().add(minuteDataSeries);

        chart.getData().add(hourDataSeries);
        
        

        return chart;

    }

 

    @Override public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(new Scene(createContent()));

        primaryStage.show();
        
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                while (true) {
                    
                    minuteDataSeries.getData().add(new Data<String, Number>(LocalTime.now().toString(), Math.random() * 10));
                    
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
                
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

 

    /**

     * Java main for when running without JavaFX launcher

     */

    public static void main(String[] args) {

        launch(args);

    }
}
