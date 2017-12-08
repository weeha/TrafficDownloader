package http;

import java.text.MessageFormat;

public class TomTomXmlCLient extends TomTomClient{

    private static final String LINK_XML = "https://traffic.tomtom.com/tsq/hdf/ITA-HDF-DET-OPENLR/{0}/content.xml";

    public TomTomXmlCLient(String key){
        super(key);
    }

    @Override
    String getApiLink(){
        Object [] arguments = {
                key,
        };

        return MessageFormat.format(LINK_XML, arguments);
    }

    String getFileEnding(){
        return ".xml";
    }

    public void run(){
        super.run();
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString();
    }
}
