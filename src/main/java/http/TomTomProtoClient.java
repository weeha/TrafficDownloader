package http;

import java.text.MessageFormat;

public class TomTomProtoClient extends TomTomClient{

    public static final String LINK_PROTO = "https://traffic.tomtom.com/tsq/hdf-detailed/ITA-HDF_DETAILED-OPENLR/{0}/content.proto";

    public TomTomProtoClient(String key){
        super(key);
    }

    @Override
    String getApiLink(){
        Object [] arguments = {
                key,
        };

        return MessageFormat.format(LINK_PROTO, arguments);
    }

    public void run(){
        super.run();
    }

    @Override
    public String getFileEnding(){
        return ".proto";
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString();    }
}
