package http;

import controller.DownloadController;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * Created by Florian Noack on 10.11.2017.
 */
public abstract class TomTomClient extends TrafficClient{

    public static final String LINK = "TOMTOM";

    public TomTomClient(String key){
        super();
        this.key = key;
    }

    @Override
    protected void generateRequest(){
        request = new HttpGet(getApiLink());
        //request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        request.setConfig(config);
    }

    String getFileEnding(){
        return null;
    }

    public void run(){
        if(request == null){
            generateRequest();
        }
        if(Calendar.getInstance().getTime().before(stopTime) && DownloadController.downloadingStarted) {
            System.out.println("Getting data from " + getApiLink());
            try {
                response = client.execute(request);
                HttpEntity entity = response.getEntity();
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

    public final String getAPIName(){
        return "TomTom";
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + "_TOMTOM";
    }
}
