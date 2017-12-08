package controller;

import http.*;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Florian Noack on 09.11.2017.
 */
public class DownloadController implements Initializable {

    private JFileChooser chooser;
    private final DateFormat logFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat callFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Pattern pattern = Pattern.compile("([0-9][0-9]:[0-9][0-9])");
    private Pattern coordinatesPattern = Pattern.compile("([0-9]+.[0-9]*),([0-9]+.[0-9]*)");
    private int callInterval = 60; // 60 seconds
    private int radius = 50; // 50 km
    public static boolean downloadingStarted = false;
    public static final String CONFIG = "config.properties";
    public static final String TRAFFIC_DIR ="Traffic\\";
    private String downloadPath = null;
    public static Timer dTimer;

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
    @FXML
    ToggleButton tomTomButton;
    @FXML
    ToggleButton hereButton;
    @FXML
    ToggleButton inrixButton;
    @FXML
    TextField tomtomKey;
    @FXML
    TextField hereID;
    @FXML
    TextField hereCode;
    @FXML
    TextField inrixKey;
    @FXML
    TextField inrixVendor;
    @FXML
    TextField inrixConsumer;
    @FXML
    Slider radiusSlider;
    @FXML
    TextField radiusField;
    @FXML
    TextField coordinates;
    @FXML
    RadioButton hereXML;
    @FXML
    RadioButton hereJSON;
    @FXML
    RadioButton tomtomXML;
    @FXML
    RadioButton tomtomProto;
    @FXML
    RadioButton segmentInfo;

    public void initialize(URL fxmlFileLocation, ResourceBundle resources){

        createLog();
        loadConfig();
        tomTomButton.setSelected(true);
        intervalField.setEditable(false);
        radiusField.setEditable(false);
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

        radiusSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                radiusSlider.setValue(newValue.intValue());
                radius = newValue.intValue();
                radiusField.setText("" + newValue.intValue() + "m");
                if(newValue.intValue() > 100){
                    segmentInfo.setSelected(false);
                    segmentInfo.setDisable(true);
                }else{
                    if (segmentInfo.isDisabled()){
                        segmentInfo.setDisable(false);
                    }
                }
            }
        });

        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                chooser = new JFileChooser();
                chooser.setDialogTitle("select directory for traffic files");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String dirs = chooser.getSelectedFile().getPath();
                    directoryField.setText(dirs);
                    downloadPath = dirs;
                    System.out.println("Download directory set to: " + dirs);
                    storeConfig("dir", dirs);
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
                            pe.printStackTrace();
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
        coordinates.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("coordinates", newValue);
            }
        });
        hereID.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("hereID", newValue);
            }
        });
        hereCode.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("hereCode", newValue);
            }
        });
        tomtomKey.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("tomtomKey", newValue);
            }
        });
        inrixKey.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("inrixKey", newValue);
            }
        });
        inrixVendor.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("inrixVendor", newValue);
            }
        });
        inrixConsumer.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                storeConfig("inrixConsumer", newValue);
            }
        });
    }

    public static void setInrixToken(String token){
        //inrixKey.setText(token);
    }

    private void startDownloading(Date start, Date end){

        Matcher coords = coordinatesPattern.matcher(coordinates.getText());
        if(coords.find()) {
            String lat = coords.group(1);
            String lon = coords.group(2);

            dTimer = new Timer();
            if (tomTomButton.isSelected()) {
                if(tomtomXML.isSelected()){
                    TrafficClient tomTom = new TomTomXmlCLient(tomtomKey.getText());
                    tomTom.setStopTime(end);
                    tomTom.setDownloadDir(downloadPath);
                    dTimer.schedule(tomTom, start, callInterval * 1000);
                }if(tomtomProto.isSelected()){
                    TrafficClient tomTom = new TomTomProtoClient(tomtomKey.getText());
                    tomTom.setStopTime(end);
                    tomTom.setDownloadDir(downloadPath);
                    dTimer.schedule(tomTom, start, callInterval * 1000);
                }
            }
            if (hereButton.isSelected()) {
                if(hereXML.isSelected()) {
                    HereClient xmlHere = new HereXmlClient(lat, lon, radius, hereID.getText(), hereCode.getText());
                    //here.setRadius(radius);
                    xmlHere.setStopTime(end);
                    xmlHere.setDownloadDir(downloadPath);
                    dTimer.schedule(xmlHere, start, callInterval * 1000);
                }if(hereJSON.isSelected()){
                    HereClient jsonHere = new HereJsonClient(lat, lon, radius, hereID.getText(), hereCode.getText());
                    //jsonHere.setRadius(radius);
                    jsonHere.setStopTime(end);
                    jsonHere.setDownloadDir(downloadPath);
                    dTimer.schedule(jsonHere, start, callInterval * 1000);
                }
            }
            if (inrixButton.isSelected()) {
                InrixTokenClient tClient = null;
                TrafficClient inrix = new InrixClient(lat, lon, radius, inrixKey.getText());
                ((InrixClient) inrix).setTokenClient(tClient);
                inrix.setStopTime(end);
                //inrix.setRadius(radius);
                inrix.setDownloadDir(downloadPath);
                dTimer.schedule(inrix, start, callInterval * 1000);
                if(segmentInfo.isSelected()){
                    if(!inrixVendor.getText().equals("") && !inrixConsumer.getText().equals("")){
                        tClient = new InrixTokenClient(inrixVendor.getText(), inrixConsumer.getText());
                        tClient.setStopTime(end);
                        Date tokenTimerStart = start;
                        tokenTimerStart.setMinutes(start.getMinutes() - 1);
                        dTimer.schedule(tClient, tokenTimerStart, 30000);
                    }
                    XdSegmentsClient xdSegments = new XdSegmentsClient(lat, lon, radius, inrixKey.getText());
                    Date singleCallDate = start;
                    xdSegments.setTokenClient(tClient);
                    singleCallDate.setMinutes(start.getMinutes() + (callInterval/60));
                    xdSegments.setStopTime(singleCallDate);
                    xdSegments.setDownloadDir(downloadPath);
                    xdSegments.schedule(start);
                }
            }
        }
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
        PrintStream err = new PrintStream(System.err){
            String timeStamp = "";
            @Override
            public void print(String text){
                timeStamp = logFormatter.format(Calendar.getInstance().getTime());
                outputArea.appendText("\n[" + timeStamp + "] [ERROR] " + text);
            }
        };
        System.setOut(stream);
        //System.setErr(err);
    }

    private void loadConfig(){
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(this.CONFIG);
        try{
            prop.load(in);
            in.close();
            if(prop != null){
                tomtomKey.setText(prop.getProperty("tomtomKey"));
                hereID.setText(prop.getProperty("hereID"));
                hereCode.setText(prop.getProperty("hereCode"));
                inrixKey.setText(prop.getProperty("inrixKey"));
                inrixVendor.setText(prop.getProperty("inrixVendor"));
                inrixConsumer.setText(prop.getProperty("inrixConsumer"));
                coordinates.setText(prop.getProperty("coordinates"));
                directoryField.setText(prop.getProperty("dir"));
                downloadPath = prop.getProperty("dir");
            }
        }catch(IOException ie){
            System.err.println("Error while loading config file");
        }catch(NullPointerException ne){
            System.err.println("No config file found for loading API data");
        }
    }

    public static void stopTimer(){
        dTimer.cancel();
        dTimer.purge();
    }

    public void storeConfig(String key, String value){
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(this.CONFIG);
            Properties prop = new Properties();
            prop.load(in);
            prop.setProperty(key, value);
            FileOutputStream out = new FileOutputStream(getClass().getClassLoader().getResource(this.CONFIG).getPath());
            prop.store(out, null);
            out.close();
        }catch(IOException ie){
            System.err.println("Could not save properties");
        }
    }

}
