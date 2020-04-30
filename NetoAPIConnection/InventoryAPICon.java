package NetoAPIConnection;

import okhttp3.*;
import JSONStringBuilder.JSonBuilder;
import ConstantsandEnums.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class InventoryAPICon extends APIConnection {

    private JSONArray jsonArray;
    private JSONArray jsonarray;

    public InventoryAPICon(){
        super();
        connection = new OkHttpClient().newBuilder()
        .connectTimeout(10000, TimeUnit.SECONDS)
        .writeTimeout(1000, TimeUnit.SECONDS)
        .readTimeout(5000, TimeUnit.SECONDS)
        .build();
        mediaType = MediaType.parse("application/json");
        ConnectionInventoryAPI();

    }

    private void ConnectionInventoryAPI(){
        try {
            jbuilder = new JSonBuilder(Type.INVENTORY);//intiating the to building a JSON object for keys and values from NETO system
            sbody = jbuilder.getInvetoryJsonString();//ing the JSON object for key and value pairs from JSonBuilder class
            //System.out.println("Line 22 InvAPICon... "+sbody);
            body = RequestBody.create(mediaType, sbody);
            //initilaising header parameters for verfication by API
            request = new Request.Builder()
                    .url(getUrl())
                    .post(body)
                    .addHeader("NETOAPI_ACTION","GetItem")
                    .addHeader("NETOAPI_KEY",getKEY())
                    .addHeader("NETOAPI_USERNAME", getUserName())
                    .addHeader("Accept", "application/json")
                    .addHeader("Postman-Token", "fd1eb95a-8dd7-46ec-8640-ef8041b34595")
                    .build();
            response = connection.newCall(request).execute();
            jsonArray = getJSONArray(response);
            //System.out.println("Line 45 InvAPICon: " + response.toString());

        }catch (IOException io){
            System.out.println(io.getMessage());
        }finally {
            response.close();
        }
    }

    /*
        JSONArray getJSONArray(Response response)
        Process OKHTTp response using JSON.simple package
     */
    private JSONArray getJSONArray(Response res) {
        JSONParser parser = new JSONParser();
        //Fetching the data from the data API and convert to JSON object
        JSONObject jsonobject = null;
        JSONArray jsonarr = null;
        //Iterator jsondata;
        try {
            //getting the response from the NETO API and the data and process it
            jsonobject = (JSONObject) parser.parse(res.body().string().trim());

            //Accessing the 1st Key in the list to gain access to the rest of the JSON object
            jsonarr = (JSONArray) jsonobject.get("Item");
            //System.out.println(jarray);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(jarray);
        return jsonarr;
    }

   /* private JSONArray GetJArrayAPIResponse(Response response) {

        String jsonString;
        JSONObject jsonObject;
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper objectMapper = new ObjectMapper();



        try {
            jsonString =  response.body().string().trim();

            jsonObject = mapper.readValue(jsonString, JSONObject.class);

            //System.out.println(jsonObject.toJSONString());
            //jsonarray = (JSONArray) jsonObject.get("Item");

            JsonNode rootNode = objectMapper.readTree(jsonString);

            JsonNode idnode = rootNode.path("Item");

            System.out.println("Item Index: "+idnode.asInt());

            JsonNode catnode = idnode.path("Categories");
            JsonNode catname = rootNode.path("CategoryName");

            System.out.println("Cat Name "+catname.toString());

            Iterator<JsonNode> catelements = catnode.elements();

            while(catelements.hasNext())
            {
                JsonNode cate = catelements.next();
                System.out.println("Categories: \n"+cate.toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonarray;
    }*/

    public JSONArray getJsonArray() {
        return jsonArray;
    }

}
