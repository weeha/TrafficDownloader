package http;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.util.EntityUtils;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;

public class XdSegmentsClient extends InrixClient {

    private final Timer timer;

    public XdSegmentsClient(){
        super();
        timer = new Timer();
    }

    public XdSegmentsClient(String latitude, String longitude, int radius, String key){
        super(latitude, longitude, radius, key);
        timer = new Timer();
    }

    public void schedule(Date start){
        timer.schedule(this, start);
    }

    @Override
    final String getApiLink() {
        if(tokenClient != null)
            key = tokenClient.getKey();
        Object[] arguments = {
                key,
                latitude,
                longitude,
                getRadius()
        };

        //return MessageFormat.format(LINK_RADIUS_SEGMENT_INFO, arguments);
        return "http://api.inrix.com/traffic/inrix.ashx?action=getreferenceschemaingeography&Geoid=40&token=F-Ju40efyS1xppCDtZvedvVDprWtssGnx7u1TSTWjns%7C&LOCRefMethod=OPENLR";
    }

    public void run(){
        if(request == null){
            generateRequest();
        }

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

        System.out.println("Stopped calling traffic API from " + getApiLink());
        timer.cancel();
        timer.purge();

    }

    protected String generateFileString(){
        return super.generateFileString() + "_XD_INFO";
    }
}
