package JSONStringBuilder;

import org.json.simple.JSONObject;
import ConstantsandEnums.Type;

import java.util.ArrayList;
import java.util.List;


public class JSonBuilder {
    Type type;

    private String jsonobj;
    private String paymentsjsonobj;
    private String orderjsonobj;


    public JSonBuilder(Type type){
                this.type = type;
                setTypeOfAPI();
    }

    public void setTypeOfAPI(){

        switch (type){
            case ORDER:
                OrdersJSONObject();
                break;
            case PAYMENT:
                PaymentsJSONObject();
                break;
            case INVENTORY:
                InventoryJSONObject();
                break;
            default:
                System.out.println("Wrong Input");
                break;

        }
    }

    private void InventoryJSONObject(){
        JSONObject jsonString;
        JSONObject filters;
        List<String> selectors;

        //String return formted in JSON object
        jsonString = new JSONObject();

        //filters used based on API filters
        filters = new JSONObject();

        //list of API selectors
        selectors = new ArrayList<>();
        selectors.add("ID");
        selectors.add("SKU");
        selectors.add("IsActive");
        selectors.add("AvailableSellQuantity");
        selectors.add("DateAdded");
        selectors.add("DateUpdated");
        selectors.add("Brand");
        selectors.add("Model");
        selectors.add("PrimarySupplier");
        selectors.add("SupplierItemCode");
        selectors.add("Approved");
        selectors.add("Misc12");
        selectors.add("Misc03");
        selectors.add("Categories");

        //filters
        filters.put("Approved","True");
        filters.put("IsActive", "True");
        filters.put("OutputSelector", selectors);

        jsonString.put("Filter",filters);
        jsonobj = jsonString.toJSONString();
        //System.out.println(jsonobj);//("Line 15 return value " + jsonString.put("Filter","").toString());
    }

    private void PaymentsJSONObject(){
        JSONObject jsonString;
        JSONObject filters;
        List<String> selectors;
        List<String>orderline;

        //String return formted in JSON object
        jsonString = new JSONObject();

        //filters used based on API filters
        filters = new JSONObject();

        //list of API selectors
        selectors = new ArrayList<>();
        selectors.add("ID");
        selectors.add("AmountPaid");
        selectors.add("DatePaid");
        //selectors.add("DatePaidLocal");
        selectors.add("PaymentMethod");
        selectors.add("ProcessBy");
        selectors.add("AccountName");

        //filters
        filters.put("DatePaidFrom","2017-01-01 00:00:00");
        filters.put("OutputSelector", selectors);

        jsonString.put("Filter",filters);
        paymentsjsonobj = jsonString.toJSONString();
    }


    private void OrdersJSONObject(){
        JSONObject jsonString;
        JSONObject filters;
        List<String> selectors;
        List<String> orderstatus;
        List<String> compstatus;

        //String return formted in JSON object
        jsonString = new JSONObject();

        //filters used based on API filters
        filters = new JSONObject();
        orderstatus = new ArrayList<>();
        orderstatus.add("New");
        orderstatus.add("Pick");
        orderstatus.add("Pack");
        orderstatus.add("On Hold");
//        orderstatus.add("Pending Pickup");
//        orderstatus.add("Pending Dispatch");
        orderstatus.add("Dispatched");

        //adding order status
        filters.put("OrderStatus",orderstatus);

        //filter by date from
        filters.put("DateCompletedFrom", "2018-08-01");
        filters.put("DateCompletedTo","2018-09-19");
       // filters.put("")

        //completed status
        //compstatus =  new ArrayList<>();
        //compstatus.add("Approved");

        //adding complete status
        //filters.put("CompleteStatus", compstatus);



        //list of API selectors
        selectors = new ArrayList<>();
        selectors.add("OrderID");
        selectors.add("Username");
        selectors.add("Email");
        selectors.add("ShipAddress");
        selectors.add("BillAddress");
        selectors.add("PurchaseOrderNumber");
        selectors.add("GrandTotal");
        selectors.add("ShippingTotal");
        selectors.add("OrderType");
        selectors.add("OrderStatus");
        selectors.add("DateInvoiced");
        selectors.add("DatePaid");
        selectors.add("DateCompleted");
        //selectors.add("OrderLine");
        //selectors.add("OrderLine.ProductName");
        selectors.add("OrderPayment");
        //selectors.add("OrderPayment.PaymentType");

        //filters

        filters.put("OutputSelector", selectors);

        jsonString.put("Filter",filters);
        orderjsonobj = jsonString.toJSONString();
    }

    public String getInvetoryJsonString() {
        //System.out.println("Line 19 return value " + jsonobj);
        return jsonobj;
    }
    public String getPayJsonString() {
        //System.out.println("Line 19 return value " + paymentsjsonobj);
        return paymentsjsonobj;
    }

    public String getOrderjsonobj(){
        return orderjsonobj;
    }
}
