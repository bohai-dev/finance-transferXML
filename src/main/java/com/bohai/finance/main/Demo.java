package com.bohai.finance.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Demo extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
	    
	 // Read file fxml and draw interface.
        Parent root = FXMLLoader.load(getClass()
                .getResource("/com/bohai/finance/view/Demo.fxml"));

        primaryStage.setTitle("Demo");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
		
	}

	public static void main(String[] args) {
		launch(args);
	}
}
