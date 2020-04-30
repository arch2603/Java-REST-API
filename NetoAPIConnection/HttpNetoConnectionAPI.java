package NetoAPIConnection;

import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpNetoConnectionAPI {

    //API Credentials
    private final String URLSTRING = "https://www.ausjetinks.com.au/do/WS/NetoAPI";
    private final String KEY = "HCcwfrDMzI3JUn16uB7JBvBeAcRZYhy2";
    private final String USER_NAME =  "ASUA";
    private URL url;
    private JSONObject jsonObject;


    private HttpURLConnection connection = null;
    private String inputLine;
    private StringBuffer response = new StringBuffer();

    public HttpNetoConnectionAPI(){
        createConnection(this.URLSTRING, KEY, USER_NAME);
    }
    private JSONObject createConnection(String strurl, String key, String uname){
        try{

            this.url = new URL(strurl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("NETOAPI_ACTION","GetItem");
            connection.setRequestProperty("NETOAPI_USERNAME", USER_NAME);
            connection.setRequestProperty("NETOAPI_KEY",KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoOutput(true);

            Map params = new LinkedHashMap();
            StringBuilder builder = new StringBuilder();



            if(connection.getResponseCode() == 200)
            {
                System.out.println("connected...");
                StringBuilder postdata = new StringBuilder();
                postdata.append("Item");
                byte[] postDataBytes = postdata.toString().getBytes("UTF-8");
                connection.getOutputStream().write(postDataBytes);
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                while((inputLine = input.readLine())!= null){
                    response.append(inputLine);
                }
                input.close();
            }

            //jsonObject = new JSONObject(response.toString());
            System.out.println(response.toString());

        }catch(IOException io){
            System.out.println(io.getMessage());
        }
        return null;
    }

}
