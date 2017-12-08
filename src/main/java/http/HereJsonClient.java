package http;

import java.text.MessageFormat;

public class HereJsonClient extends HereClient {

    public HereJsonClient(String appCode){
        super(appCode);
    }

    public HereJsonClient(String latitude, String longitude, int radius, String key, String appCode) {
        super(latitude, longitude, radius, key, appCode);
    }

    String getFileEnding(){
        return ".json";
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

        return MessageFormat.format(LINK_RADIUS_SHD_JSON, arguments);
    }
}
