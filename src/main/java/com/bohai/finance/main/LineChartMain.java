package com.bohai.finance.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LineChartMain extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
	    Parent root = FXMLLoader.load(getClass()
                .getResource("/com/bohai/finance/view/LineChart.fxml"));

        primaryStage.setTitle("Tick");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
		
		Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                
            }
        });
		
		thread.setDaemon(true);
		thread.start();
	}
}
