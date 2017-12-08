package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by fnoack on 09.11.2017.
 */
public class App extends Application{

    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/traffic_downloader.fxml"));
        primaryStage.setTitle("TUM BGU Traffic Downloader");
        primaryStage.setScene(new Scene(root, 952, 690));
        primaryStage.show();
    }

    public static void main(String args[]){
        launch(args);
    }
}
