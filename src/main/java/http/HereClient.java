package http;


public abstract class HereClient extends TrafficClient {

    protected static final String LINK_RADIUS_TMC_JSON = "https://traffic.cit.api.here.com/traffic/6.1/flow.json?prox=45.4501%2C10.9915%2C966&app_id=F3Q9S8pJT9hWvpescpo3&app_code=uRHvC5LtHiO7K-QBIVRXLA";
    protected static final String LINK_RADIUS_TMC_XML = "https://traffic.cit.api.here.com/traffic/6.1/flow.xml?prox=45.4501%2C10.9915%2C966&app_id=F3Q9S8pJT9hWvpescpo3&app_code=uRHvC5LtHiO7K-QBIVRXLA";
    protected static final String LINK_RADIUS_SHD_JSON = "https://traffic.cit.api.here.com/traffic/6.1/flow.json?prox=45.4501%2C10.9915%2C966&app_id=F3Q9S8pJT9hWvpescpo3&app_code=uRHvC5LtHiO7K-QBIVRXLA&responseattributes=sh%2Cfc";
    protected static final String LINK_RADIUS_SHD_XML = "https://traffic.cit.api.here.com/traffic/6.1/flow.xml?prox={0}%2C{1}%2C{2}&app_id={3}&app_code={4}&responseattributes=sh%2Cfc";
    protected String appCode;
    protected boolean [] attributes = {true, true}; // fc , fc attributes from HERE API

    public HereClient(String appCode){
        super();
        this.appCode = appCode;
    }

    public HereClient(String latitude, String longitude, int radius, String key, String appCode){
        super(latitude, longitude, radius, key);
        this.appCode = appCode;
    }

    public String getAppCode(){
        return appCode;
    }

    public void run(){
        super.run();
    }

    public final String getAPIName(){
        return "HERE";
    }

    @Override
    protected String generateFileString(){
        return super.generateFileString() + "_HERE";
    }
}
