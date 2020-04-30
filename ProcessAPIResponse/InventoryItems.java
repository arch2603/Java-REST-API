package ProcessAPIResponse;

import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

public class InventoryItems{
    private JSONArray jarray;
    private JSONArray jsonarray;

    public JSONArray ProcessAPIResponse(Response res) {
        JSONParser parser = new JSONParser();
        //Fetching the data from the data API and convert to JSON object
        JSONObject jsonobject = null;
        //Iterator jsondata;
        try {
            //getting the response from the NETO API and the data and process it
            jsonobject = (JSONObject) parser.parse(res.body().string().trim());

            //Accessing the 1st Key in the list to gain access to the rest of the JSON object
            jarray = (JSONArray) jsonobject.get("Item");
            //System.out.println(jarray);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(jarray);
        return jarray;
    }

}
