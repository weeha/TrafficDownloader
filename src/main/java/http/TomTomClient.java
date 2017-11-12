package http;

/**
 * Created by Florian Noack on 10.11.2017.
 */
public class TomTomClient extends TrafficClient{

    public static final String LINK = "TOMTOM";

    public TomTomClient(){
        super();
    }

    @Override
    final String getApiLink(){
        return LINK;
    }

    public void run(){
        super.run();
    }
}
