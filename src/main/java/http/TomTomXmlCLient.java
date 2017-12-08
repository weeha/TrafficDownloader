package http;

import java.text.MessageFormat;

public class TomTomXmlCLient extends TomTomClient{

    private static final String LINK_XML = "";

    public TomTomXmlCLient(){
        super();
    }

    @Override
    String getApiLink(){
        Object [] arguments = {
                key,
        };

        return MessageFormat.format(LINK_XML, arguments);
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + ".xml";
    }
}
