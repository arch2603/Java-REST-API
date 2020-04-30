package displaying;

import Controllers.InventoryManager;
import Entities.Inventory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Output {

    private InventoryManager inventorymanager = InventoryManager.getInstance();
    private List<Inventory> inventories = new ArrayList<>();
    private FileWriter fileWriter = null;

    //Inventory Fields
    private String id;
    private String suppitemcode;
    private String dateadded;
    private String SKU;
    private String brand;
    private String model;
    private String primarysupplier;

    private String rcartridges;
    private String qtyavailable;
    private String approved;

    private JSONArray categories;
    private String prodtype;

   public Output()
   {
   }

    public ArrayList<Inventory> createInventoryObject(JSONArray jsonarray) {

        Iterator jsondata = jsonarray.iterator();
        int catid;

        while (jsondata.hasNext()){

            JSONObject value = (JSONObject) jsondata.next();
            id = (String) value.get("ID");
            dateadded = (String) value.get("DateAdded");
            SKU = (String) value.get("SKU");
            qtyavailable = (String) value.get("AvailableSellQuantity");
            brand = (String) value.get("Brand");
            model = (String) value.get("Model");
            primarysupplier = (String) value.get("PrimarySupplier");
            suppitemcode = (String) value.get("SupplierItemCode");
            approved = (String) value.get("Approved");
            rcartridges = (String) value.get("Misc12");
            categories = (JSONArray)value.get("Categories");
            //String id, String dateadded, String sku, String qty, String brand, String model, String primarysupplier, String relatedcartridges
            //inventorymanager.setInventory(id,dateadded,SKU,qtyavailable,brand,model, suppitemcode, primarysupplier,rcartridges,approved, categories, prodtype);
            inventories.add(inventorymanager.getInventory());
        }

        return (ArrayList<Inventory>) inventories;
    }

    public ArrayList<Inventory> createInventoryObject(org.json.JSONArray jsonarray) {

        Iterator jsondata = jsonarray.iterator();
        int catid;

        while (jsondata.hasNext()){

            JSONObject value = (JSONObject) jsondata.next();
            id = (String) value.get("ID");
            dateadded = (String) value.get("DateAdded");
            SKU = (String) value.get("SKU");
            qtyavailable = (String) value.get("AvailableSellQuantity");
            brand = (String) value.get("Brand");
            model = (String) value.get("Model");
            primarysupplier = (String) value.get("PrimarySupplier");
            suppitemcode = (String) value.get("SupplierItemCode");
            approved = (String) value.get("Approved");
            rcartridges = (String) value.get("Misc12");
            categories = (JSONArray) value.get("Categories");

            //String id, String dateadded, String sku, String qty, String brand, String model, String primarysupplier, String relatedcartridges
//            inventorymanager.setInventory(id,dateadded,SKU,qtyavailable,brand,model, suppitemcode, primarysupplier,rcartridges,approved, categories, prodtype);
//            inventories.add(inventorymanager.getInventory());
        }

        return (ArrayList<Inventory>) inventories;
    }


    public void display(ArrayList<Inventory> inventories){

        for(Inventory inventory : inventories)
        {
            System.out.println("ID: "+ inventory.getId());
            System.out.println("SKU: "+ inventory.getSku());
            System.out.println("Date: "+ inventory.getDate());
            System.out.println("QTY: "+inventory.getQtyavailable());
            System.out.println("Brand: "+inventory.getBrand());
            System.out.println("Model: "+inventory.getModel());
            System.out.println("Supplier Item Code: "+ inventory.getSuppitemcode());
            System.out.println("Related Cartridges: "+inventory.getRcartridges());
            System.out.println("Categories: "+inventory.getCategories());
            System.out.println(" ");

        }
    }

}
