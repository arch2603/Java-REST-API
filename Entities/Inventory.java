package Entities;

import org.json.simple.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Inventory {

    //fields from the neto api also in the database
    private String id;
    private String suppitemcode;
    private String dateadded;
    private String dateupdated;
    private String datetupdated;
    private String sku;
    private String brand;
    private String model;
    private String primarysupplier;

    private String rcartridges;
    private String qtyavailable;
    private String _date;
    private int numberofdays;

    private String approved;
    private String producttype;
    private JSONArray categories;

    //Convert date into dd-mm-yyyy format
    private SimpleDateFormat simpledate;
    private SimpleDateFormat newdateformat;
    private Date date;

    public Inventory() {

        simpledate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        newdateformat = new SimpleDateFormat("dd-MM-yyyy");

    }

    /* ----------------------------------------SETTERS------------------------------------------- */
    public void setApproved(String approved) {
        this.approved = approved;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSuppitemcode(String suppitemcode) {
        this.suppitemcode = suppitemcode;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateadded;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPrimarysupplier(String primarysupplier) {
        this.primarysupplier = primarysupplier;
    }

    public void setRcartridges(String rcartridges) {
        this.rcartridges = rcartridges;
    }

    public void setQtyavailable(String qtyavailable) {
        this.qtyavailable = qtyavailable;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public void setCategories(JSONArray categories) {
        this.categories = categories;
    }

    public void setDateupdated(String dateupdated) {
        this.dateupdated = dateupdated;
    }

    public void setDatetupdated(String datetupdated) {
        this.datetupdated = datetupdated;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    private int checkNegativeStock(String qty) {
        int noofdays = 0;
        int quantity = Integer.parseInt(qtyavailable);
        if (quantity < 0) {
            noofdays += 1;
        }
        return noofdays;
    }

    /*---------------------------------------- GETTERS -----------------------------------------------*/
    public String getId() {
        return id;
    }

    public String getSuppitemcode() {
        return suppitemcode;
    }

    public String getDateadded() {
        return dateadded;
    }

    public String getSku() {
        return sku;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getPrimarysupplier() {
        return primarysupplier;
    }

    public String getRcartridges() {
        return rcartridges;
    }

    public String getQtyavailable() {
        return qtyavailable;
    }

    public String getApproved() {
        return approved;
    }

    public String getDate() {
        try {
            date = simpledate.parse(dateadded);
            _date = newdateformat.format(date);

        } catch (ParseException pex) {
            System.out.println(pex.getMessage());
        }
        return _date;
    }

    public int getNumberofdays() {
        int value = checkNegativeStock(qtyavailable);
        this.numberofdays = value;
        return numberofdays;
    }

    public String getDatetupdated() {
        return datetupdated;
    }

    public String getProducttype() {
        return producttype;
    }

    public String getDateupdated() {
        return dateupdated;
    }

    public JSONArray getCategories() {
        return categories;
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


}
