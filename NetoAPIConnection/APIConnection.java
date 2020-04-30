package NetoAPIConnection;

import okhttp3.*;
import JSONStringBuilder.JSonBuilder;
import ConstantsandEnums.Type;


public class APIConnection
{
    //API Credentials
    private static final String url = "https://www.ausjetinks.com.au/do/WS/NetoAPI";
    private static final String KEY = "HCcwfrDMzI3JUn16uB7JBvBeAcRZYhy2";
    private static final String USER_NAME =  "ASUA";

    protected RequestBody body;
    protected Request request;
    protected OkHttpClient connection;
    protected MediaType mediaType;
    protected JSonBuilder jbuilder;//intiating the to building a JSON object for keys and values from NETO system
    protected String sbody;//gett
    protected Response response;
    Type type;

    public APIConnection(){

    }


    public static String getUrl() {
        return url;
    }

    public static String getKEY() {
        return KEY;
    }

    public static String getUserName() {
        return USER_NAME;
    }

}
