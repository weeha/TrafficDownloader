package http;

import controller.DownloadController;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InrixTokenClient extends TrafficClient{

    public static final String LINK = "http://api.inrix.com/Traffic/Inrix.ashx?action=getsecuritytoken&VendorID={0}&ConsumerID={1}";
    private Pattern pattern = Pattern.compile("<AuthToken expiry=\"([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z)\">(.+?)<\\/AuthToken>");
    private String accessToken = null;
    private String consumerId;
    private String vendorId;
    private Date expiringDate = null;

    public InrixTokenClient(String vendor, String consumer){
        super();
        this.vendorId = vendor;
        this.consumerId = consumer;
    }

    public void setExperingDate(Date d){
        expiringDate = d;
    }

    public String getAccessToken(){
        return accessToken;
    }

    String getFileEnding(){
        return null;
    }

    String getAPIName(){
        return "INRIX Token Client";
    }

    public void run() {
        if (request == null) {
            generateRequest();
        }
        boolean expired = expiringDate == null ? true : Calendar.getInstance().getTime().after(expiringDate);
        if(Calendar.getInstance().getTime().before(stopTime) && expired) {
            System.out.println("INRIX access token expired...\n Accessing a new one");
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
                Matcher matcher = pattern.matcher(responseString);
                if(matcher.find()){
                    Instant instant = Instant.parse(matcher.group(1));
                    setExperingDate(Date.from(instant));
                    accessToken = matcher.group(2);
                    DownloadController.setInrixToken(accessToken);
                }
                System.out.println("Accessed new INRIX token: " + getAccessToken());
            }catch(java.net.SocketTimeoutException ste){
                System.err.println(getAPIName() + " socket timed out");
            }catch(IOException ie){
                System.err.println(getAPIName() + " could not execute request");
                ie.printStackTrace();
            }
        }
    }

    @Override
    String getApiLink(){
        Object [] arguments = {
                this.vendorId,
                this.consumerId
        };

        return MessageFormat.format(LINK, arguments);
    }

    public static void main(String [] args){

        //i.setRecordCreationTime("2017-04-04T04:20:00Z");
        Instant instant = Instant.parse("2017-12-07T18:59:00Z");
        System.out.println(Date.from(instant).getMinutes()-1);
    }

}
