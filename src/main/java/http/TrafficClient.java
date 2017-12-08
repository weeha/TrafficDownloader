package http;

import controller.DownloadController;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Florian Noack on 09.11.2017.
 */
public abstract class TrafficClient extends TimerTask{

    protected String fileEnding = null;
    protected Date stopTime;
    private final DateFormat logFormatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private String path = null;
    protected HttpGet request = null;
    protected HttpResponse response;
    protected org.apache.http.client.HttpClient client;
    protected String key;
    protected String latitude;
    protected String longitude;
    protected int radius;
    protected RequestConfig config = null;

    public TrafficClient(){
        config = RequestConfig.custom()
                .setCircularRedirectsAllowed(false)
                .setConnectionRequestTimeout(4000)
                .setConnectTimeout(4000)
                .setMaxRedirects(0)
                .setRedirectsEnabled(false)
                .setSocketTimeout(10000)
                .build();

        client = HttpClientBuilder.create().build();

    }

    protected void generateRequest(){
        request = new HttpGet(getApiLink());
        request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        request.setConfig(config);
    }

    public TrafficClient(String latitude, String longitude, int radius, String key){
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.key = key;
         config = RequestConfig.custom()
                .setCircularRedirectsAllowed(false)
                .setConnectionRequestTimeout(4000)
                .setConnectTimeout(4000)
                .setMaxRedirects(0)
                .setRedirectsEnabled(false)
                .setSocketTimeout(100000)
                .build();
        client = HttpClientBuilder.create().build();

    }

    public void setFileEnding(String ending){
        fileEnding = ending;
    }

    abstract String getFileEnding();

    public void setKey(String key){
        this.key = key;
    }

    public String getKey(){
        return key;
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    public String getRadius(){
        return String.valueOf(radius);
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLatitude(){
        return latitude;
    }

    abstract String getApiLink();
    abstract String getAPIName();

    public void run(){
        if(request == null){
            generateRequest();
        }
        if(Calendar.getInstance().getTime().before(stopTime) && DownloadController.downloadingStarted) {
            System.out.println("Getting data from " + getApiLink());
            try {
                response = client.execute(request);
                HttpEntity entity = response.getEntity();
                Header contentEncodingHeader = entity.getContentEncoding();
                if(contentEncodingHeader != null){
                    HeaderElement[] encodings =contentEncodingHeader.getElements();
                    for (int i = 0; i < encodings.length; i++) {
                        if (encodings[i].getName().equalsIgnoreCase("gzip")) {
                            entity = new GzipDecompressingEntity(entity);
                            break;
                        }
                    }
                }
                String responseString = EntityUtils.toString(entity, "ISO-8859-1");
                File file = new File(generateFileString() +  getFileEnding());
                FileUtils.writeStringToFile(file, responseString, "ISO-8859-1");
                System.out.println("Stored " + getAPIName() + " Data to: " + file.getPath());
            }catch(java.net.SocketTimeoutException ste){
                System.err.println(getAPIName() + " socket timed out");
            }catch(IOException ie){
                System.err.println(getAPIName() + " could not execute request");
                ie.printStackTrace();
            }
        }else{
            System.out.println("Stopped calling traffic API from " + getApiLink());
            DownloadController.stopTimer();
            return;
        }
    }

    public void setDownloadDir(String path){
        this.path = path;
    }

    public void setStopTime(Date end){
        this.stopTime = end;
    }

    public HttpResponse getResponse(){
        return response;
    }

    protected String generateFileString(){

        Calendar cal = Calendar.getInstance();
        DateFormat sdf = logFormatter;
        if(path != null)
            return path + "\\" + DownloadController.TRAFFIC_DIR + sdf.format(cal.getTime());
        return DownloadController.TRAFFIC_DIR + sdf.format(cal.getTime());
    }

    public static void main(String [] args){
        TrafficClient inrix = new XdSegmentsClient("45.441765" ,"10.983625", 101, "62Ykl*3Bujwi5sJEVS3DsoPKVLwS8YR*zvp-c9Gk*4A%7C");
        inrix.setDownloadDir("C:\\Users\\flori\\Documents\\Traffic\\");
        Timer dTimer = new Timer();
        Date start = new Date();
        start.setSeconds(start.getSeconds() + 20);
        Date end = new Date();
        end.setMinutes(end.getMinutes() + 5);
        inrix.setStopTime(end);
        DownloadController.downloadingStarted = true;
        dTimer.schedule(inrix, start);


    }


}
