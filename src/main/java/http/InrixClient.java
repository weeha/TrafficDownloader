package http;

import java.text.MessageFormat;

public class InrixClient extends TrafficClient {

    public static final String LINK_RADIUS_TMC_XD_XML = "http://eu.api.inrix.com/Traffic/Inrix.ashx?Action=GetSegmentSpeedInRadius&Token={0}&Center={1}%7C{2}&Radius={3}&RoadSegmentType=XDS,TMC&Units=1&Resolution=0&Coverage=4&SegmentOutputFields=All";
    public static final String LINK_RADIUS_SEGMENT_INFO = "http://eu.api.inrix.com/Traffic/Inrix.ashx?Action=GetSegmentsInRadius&Token={0}&Center={1}%7C{2}&Radius={3}&RoadSegmentType=TMC,XDS&SegmentOutputFields=All";
    protected InrixTokenClient tokenClient = null;

    public InrixClient(){
        super();
    }

    public InrixClient(String latitude, String longitude, int radius, String key){
        super(latitude, longitude, radius, key);
    }

    public void setTokenClient(InrixTokenClient client){
        tokenClient = client;
    }

    String getFileEnding(){
        return ".xml";
    }

    @Override
    String getApiLink(){
        if(tokenClient != null)
            key = tokenClient.getKey();
        Object [] arguments = {
                key,
                latitude,
                longitude,
                getRadius()
        };

        return MessageFormat.format(LINK_RADIUS_TMC_XD_XML, arguments);
    }

    public void run(){
        super.run();
    }

    public final String getAPIName(){
        return "INRIX";
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + "_INRIX";
    }
}
