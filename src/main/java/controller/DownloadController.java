package controller;

import http.TomTomClient;
import http.TrafficClient;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.*;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian Noack on 09.11.2017.
 */
public class DownloadController implements Initializable {

    private JFileChooser chooser;
    private final DateFormat logFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat callFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Pattern pattern = Pattern.compile("([0-9][0-9]:[0-9][0-9])");
    private int callInterval = 60; // 60 seconds
    public static boolean downloadingStarted = false;

    @FXML
    Slider intervalSlider;
    @FXML
    TextField intervalField;
    @FXML
    TextField startTimeField;
    @FXML
    TextField endTimeField;
    @FXML
    TextArea outputArea;
    @FXML
    Button directoryButton;
    @FXML
    TextField directoryField;
    @FXML
    DatePicker datePicker;
    @FXML
    Button startButton;

    public void initialize(URL fxmlFileLocation, ResourceBundle resources){

        createLog();

        intervalField.setEditable(false);
        outputArea.setEditable(false);
        datePicker.setValue(LocalDate.now());
        System.out.println("Traffic Downloader started");

        intervalSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                intervalSlider.setValue(newValue.intValue());
                callInterval = newValue.intValue();
                intervalField.setText("" + newValue.intValue());
            }
        });

        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                chooser = new JFileChooser();
                chooser.setDialogTitle("select directory for traffic files");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getCurrentDirectory().getPath();
                    directoryField.setText(dir);
                    System.out.println("Download directory set to: " + dir);
                }
                else {
                    System.out.println("No Selection for download directory");
                }
            }
        });

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(!downloadingStarted) {
                    Matcher startMatcher = pattern.matcher(startTimeField.getText());
                    Matcher endMatcher = pattern.matcher(endTimeField.getText());
                    if (startMatcher.find() && endMatcher.find()) {
                        try {
                            Date startDate = callFormatter.parse(datePicker.getValue().toString() + " " + startTimeField.getText());
                            Date endDate = callFormatter.parse(datePicker.getValue().toString() + " " + endTimeField.getText());
                            System.out.println("Traffic downloading timer scheduled");
                            System.out.println("Start time: " + startDate);
                            System.out.println("End time: " + endDate);
                            System.out.println("Call interval: " + callInterval + " seconds");
                            startDownloading(startDate, endDate);
                        } catch (ParseException pe) {

                        }
                    } else {
                        System.out.println("[ERROR] Wrong start or end time!");
                    }
                    downloadingStarted = true;
                    startButton.setText("Cancel");
                    startButton.setStyle("-fx-background-color: #F44336");
                }else{
                    startButton.setText("Start");
                    startButton.setStyle("-fx-background-color: #4CAF50");
                    downloadingStarted = false;
                    System.out.println("Stopped downloading for next possible call interval");
                }

            }
        });

    }

    private void startDownloading(Date start, Date end){

        Timer dTimer = new Timer();
        TrafficClient tomTom = new TomTomClient();
        tomTom.setStopTime(end);
        dTimer.schedule(tomTom, start, callInterval*1000);
    }

    private void createLog(){
        PrintStream stream = new PrintStream(System.out){
            String timeStamp = "";
            @Override
            public void print(String text){
                timeStamp = logFormatter.format(Calendar.getInstance().getTime());
                outputArea.appendText("\n[" + timeStamp + "] " + text);
            }
        };
        System.setOut(stream);
    }

}
