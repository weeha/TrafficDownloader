package http;

import java.text.MessageFormat;

/**
 * Created by Florian Noack on 10.11.2017.
 */
public abstract class TomTomClient extends TrafficClient{

    public static final String LINK = "TOMTOM";

    public TomTomClient(){
        super();
    }

    String getFileEnding(){
        return null;
    }

    public void run(){
        super.run();
    }

    public final String getAPIName(){
        return "TomTom";
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + "_TOMTOM";
    }
}
