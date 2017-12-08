package http;

import java.text.MessageFormat;
import java.util.Arrays;

public class HereXmlClient extends HereClient{

    public HereXmlClient(String appCode){
        super(appCode);
    }

    public HereXmlClient(String latitude, String longitude, int radius, String key, String appCode) {
        super(latitude, longitude, radius, key, appCode);
    }

    String getFileEnding(){
        return ".xml";
    }

    @Override
    public String generateFileString(){
        return super.generateFileString();
    }

    @Override
    final String getApiLink(){

        Object[] arguments = {
                getLatitude(),
                getLongitude(),
                getRadius(),
                getKey(),
                getAppCode()
        };

        return MessageFormat.format(LINK_RADIUS_SHD_XML, arguments);
    }
}
