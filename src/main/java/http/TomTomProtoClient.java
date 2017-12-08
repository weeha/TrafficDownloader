package http;

import java.text.MessageFormat;

public class TomTomProtoClient extends TomTomClient{

    public static final String LINK_PROTO = "";

    public TomTomProtoClient(){
        super();
    }

    @Override
    String getApiLink(){
        Object [] arguments = {
                key,
        };

        return MessageFormat.format(LINK_PROTO, arguments);
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + ".proto";
    }
}
