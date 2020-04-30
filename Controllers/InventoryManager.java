package Controllers;

import Entities.Inventory;
import org.json.simple.JSONArray;

public class InventoryManager {
    private static InventoryManager instance = new InventoryManager();
    private static Inventory inventory;

    private InventoryManager(){}

    public static InventoryManager getInstance(){

        return instance;
    }

    public void setInventory(String id, String dateadded, String sku, String qty, String brand, String model, String suppitemcode, String primarysupplier, String relatedcartridges, String approved ){
        inventory = new Inventory();
        inventory.setId(id);
        inventory.setDateadded(dateadded);
        inventory.setSku(sku);
        inventory.setQtyavailable(qty);
        inventory.setBrand(brand);
        inventory.setModel(model);
        inventory.setSuppitemcode(suppitemcode);
        inventory.setPrimarysupplier(primarysupplier);
        inventory.setRcartridges(relatedcartridges);
        inventory.setApproved(approved);
    }

    public void setInventory(String id, String dateadded, String sku, String qty, String brand, String model, String suppitemcode, String primarysupplier, String relatedcartridges, String approved, JSONArray categories, String prodtype, String dfupdate){
        inventory = new Inventory();
        inventory.setId(id);
        inventory.setDateadded(dateadded);
        inventory.setSku(sku);
        inventory.setQtyavailable(qty);
        inventory.setBrand(brand);
        inventory.setModel(model);
        inventory.setSuppitemcode(suppitemcode);
        inventory.setPrimarysupplier(primarysupplier);
        inventory.setRcartridges(relatedcartridges);
        inventory.setApproved(approved);
        inventory.setCategories(categories);
        inventory.setProducttype(prodtype);
        inventory.setDateupdated(dfupdate);
    }

    public Inventory getInventory(){
        return inventory;
    }



}
