package http;

import controller.DownloadController;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by Florian Noack on 09.11.2017.
 */
public abstract class TrafficClient extends TimerTask{

    protected final String LINK = null;
    private Date stopTime;

    protected HttpGet request;
    private HttpResponse response;

    public TrafficClient(){
        RequestConfig config = RequestConfig.custom()
                .setCircularRedirectsAllowed(false)
                .setConnectionRequestTimeout(4000)
                .setConnectTimeout(4000)
                .setMaxRedirects(0)
                .setRedirectsEnabled(false)
                .setSocketTimeout(4000)
                .build();
        request = new HttpGet(getApiLink());
        request.setConfig(config);
    }

    abstract String getApiLink();

    public void run(){
        if(Calendar.getInstance().getTime().before(stopTime) && DownloadController.downloadingStarted) {
            System.out.println("Getting data from " + getApiLink());
        }else{
            System.out.println("Stopped calling traffic API from " + getApiLink());
        }
    }

    public void setStopTime(Date end){
        this.stopTime = end;
    }

    public HttpResponse getResponse(){
        return response;
    }
}
