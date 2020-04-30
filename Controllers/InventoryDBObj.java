package Controllers;

import Entities.Inventory;
import NetoAPIConnection.InventoryAPICon;
import utility.Insert;
import utility.LogCases;
import utility.Logs;
import utility.Update;
import utility.database.DBConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.FileHandler;

public class InventoryDBObj {
    //constants for updating the database
    private static final String UPDATE_DAYS = "1";
    private static final String UPDATE_DAYS_QTY_AT_ZERO = "2";
    private static final String UPDATE_QTY = "3";

    //values associated with keys from the Neto API
    private String id;
    private String suppitemcode;
    private String dateadded;
    private String SKU;
    private String brand;
    private String model;
    private String primarysupplier;
    private String rcartridges;
    private String qtyavailable;
    private String _date;
    private String approved;

    //Convert date into dd-mm-yyyy format
    private SimpleDateFormat simpledate;
    private SimpleDateFormat newdateformat;
    private Date date;

    private Update update;
    private Insert insert;


    private ArrayList<Inventory> inventories;
    private InventoryManager inventorymanager;

    //objects return from Neto API
    private InventoryAPICon invcon;
    private JSONArray inventoryitems;


    public InventoryDBObj() {
        this.invcon = new InventoryAPICon();
        this.inventoryitems = invcon.getJsonArray();
        this.inventories = new ArrayList<>();
        this.inventorymanager = InventoryManager.getInstance();
    }

    /**
     * void update
     * Description: General update method to update the database with various fields
     */
    public void update() {
        //DATABASE FIELDS
        int dbid, dbqty, daysatzeroqty;
        ;

        String dbsku;
        //number days since a quantity has a zero or negative value
        int dayslapses = 0;
        //0 means quantity is positive and 1 means quantity is negative
        int status = 0;

        //API FIELDS
        String apisku, brand, model, psupplier, ptype, rcartridges, netodateadded, dateadd, dateupdate;
        int apiid, apiqty;

        String fetchsqldatastr = "SELECT * FROM inventory";

        DBConnection con = new DBConnection();
        Connection connection = con.getCon();
        PreparedStatement pstatementinsertion = null;
        PreparedStatement pstatementfetch = null;
        ResultSet rs = null;


        //date data added to the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime todaysdate = LocalDateTime.now();
        String todate = formatter.format(todaysdate);

        File file;
        String filename = "D:\\Liam\\Documents\\JavaProjects\\logs\\log.txt";
        Logs logger = new Logs();
        FileHandler fh = null;


        int invlistsize = 0;
        int rowaffected = 0;//rows getting updated

        try {
            file = new File(filename);
            fh = new FileHandler(file.getAbsolutePath(), true);
            createArrayInventory();
            pstatementfetch = connection.prepareStatement(fetchsqldatastr);
            rs = pstatementfetch.executeQuery();
            //invlistsize = inventories.size() - 1;
            //Inventory lastitem = inventories.get(invlistsize);

            while (rs.next()) {
                dbid = rs.getInt("idinventory");
                dbsku = rs.getString("sku");
                daysatzeroqty = rs.getInt("days_at_zero_qty");
                dbqty = rs.getInt("qty");
                brand = rs.getString("brand");
                dateadd = rs.getString("date_add");

                for (Inventory inventory : inventories) {
                    apiqty = Integer.parseInt(inventory.getQtyavailable());
                    //apiid = Integer.parseInt(inventory.getId());
                    apisku = inventory.getSku();
                    dateupdate = inventory.getDateupdated();
                    //logic which updates the quatity based on the qty from API
                    if ((apisku.equalsIgnoreCase(dbsku)) && (apiqty <= 0 && dbqty <= 0)) {
                        dayslapses = daysLapseSinceInventoryAdded(dateadd, dateupdate);
                        status = checkQuantityLevel(apiqty);
                        rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                    }
                    if ((apisku.equalsIgnoreCase(dbsku)) && (apiqty <= 0 && dbqty > 0)) {
                        dayslapses = daysLapseSinceInventoryAdded(dateadd, dateupdate);
                        status = checkQuantityLevel(apiqty);
                        rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                    }
                    if ((apisku.equalsIgnoreCase(dbsku)) && ((apiqty > 0 && dbqty > 0))) {
                        if (dbqty > apiqty || dbqty < apiqty) {
                            dayslapses = 0;
                            status = checkQuantityLevel(apiqty);
                            ;
                            rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                        }
                    }
                    if ((apisku.equalsIgnoreCase(dbsku)) && ((apiqty > 0 && (dbqty == apiqty)))) {

                        dayslapses = 0;
                        status = checkQuantityLevel(apiqty);
                        ;
                        rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                    }
                    if ((apisku.equalsIgnoreCase(dbsku)) && ((apiqty > 0 && dbqty < apiqty))) {

                        dayslapses = 0;
                        status = checkQuantityLevel(apiqty);
                        rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                    }

                }//end for
            }//end while

            if (rowaffected > 0) {
                logger.setup(fh);
                logger.logMessages(LogCases.LOG_MESSAGE, String.format("Rows updated " + rowaffected));
            }
        } catch (ParseException pex) {
            logger.setup(fh);
            logger.logMessages(LogCases.LOG_MESSAGE_EXCEPTION, pex.getMessage());
        } catch (SQLException sqlex) {
            logger.setup(fh);
            logger.logMessages(LogCases.LOG_MESSAGE_EXCEPTION, sqlex.getMessage());
        } catch (IOException e) {
            logger.setup(fh);
            logger.logMessages(LogCases.LOG_MESSAGE_EXCEPTION, e.getMessage());
            //logger.logErrors(e.getMessage());
            //e.printStackTrace();
        } finally {
            close(connection, rs, pstatementfetch);
        }

    }

    public void updateItemTracking() {

    }

    public void insert() {

        //String fetcsqlstr = "SELECT idinventory, sku FROM inventory;";

        //DATABASE FIELDS
        int dbid, daysatzeroqty;
        int status = 0;
        String dbsku;

        //API FIELDS
        String apisku, brand, model, psupplier, ptype, rcartridges, netodateadded, dateadd, dateupdate;
        int apiid, apiqty;

        DBConnection con = new DBConnection();
        Connection connection = con.getCon();
        PreparedStatement pstatementinsertion = null;
        PreparedStatement pstatementfetch = null;
        ResultSet rs = null;

        //date data added to the database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime todaysdate = LocalDateTime.now();
        String todate = formatter.format(todaysdate);
        //System.out.println(formatter.format(todaysdate));

        //try {

        //pstatementfetch = connection.prepareStatement(fetcsqlstr);
        //pstatementfetch.setInt(1, apiid);//setting id to the api id
        //pstatementfetch.setString(2, apisku);//setting sku to the api
        //rs = pstatementfetch.executeQuery();//executing query
        createArrayInventory();
        for (Inventory inventory : inventories) {
            apiqty = Integer.parseInt(inventory.getQtyavailable());
            //apiid = Integer.parseInt(inventory.getId());
            apisku = inventory.getSku();
            brand = inventory.getBrand();
            model = inventory.getModel();
            psupplier = inventory.getPrimarysupplier();
            ptype = inventory.getProducttype();
            rcartridges = inventory.getRcartridges();
            netodateadded = inventory.getDate();
            dateupdate = inventory.getDateupdated();

            //while (rs.next())
            //{
            //dbid = rs.getInt("idinventory");
            //dbsku = rs.getString("sku");
            status = checkQuantityLevel(apiqty);
            if (!checkIfSKUExists(connection, apisku)) {
                try {
                    dateadd = todaysDate();
                    daysatzeroqty = daysLapseSinceInventoryAdded(todate, dateupdate);
                    System.out.println("Line 251... debugging");
                    insertData(Insert.INSERTNEWDATA, connection, apisku, daysatzeroqty, apiqty, brand, model, psupplier, ptype, rcartridges, netodateadded, dateadd, status);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            //}
            System.out.println("Line 259... debugging");
        }

        //} catch (SQLException e) {
        // e.printStackTrace();
        // } finally {

        close(connection, pstatementinsertion);
        System.out.println("Line 267... debugging");
        //}
    }

    private boolean checkIfSKUExists(Connection dbcon, String sku) {
        int count = 0;//check for records already added
        String sql = "SELECT COUNT(*) FROM inventory " + " WHERE sku = ?";
        ResultSet rs = null;
        try {
            PreparedStatement ps = dbcon.prepareStatement(sql);
            ps.setString(1, sku);
            rs = ps.executeQuery();

            if(rs.next()){
                count = rs.getInt(1);
                return count > 0;
            }
            ;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count <= 0;
    }

    public void insertDataDB() {
        DBConnection con = new DBConnection();
        Connection connection = con.getCon();
        PreparedStatement preparedStatement = null;

        /**/
        String sqlinv = "INSERT INTO inventory(idinventory, sku, days_at_zero_qty, qty, brand, model, primary_supplier, product_type, related_cart,  date_added) "
                + " VALUES(?,?,?,?,?,?,?,?,?,?) ";

        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat d = new SimpleDateFormat("dd-MM-yyyy");
        long millis = System.currentTimeMillis();
        date = new Date(millis);
        Date f_date_now;
        int result;

        try {

            preparedStatement = connection.prepareStatement(sqlinv, Statement.RETURN_GENERATED_KEYS);
            for (Inventory inventory : inventories) {
                preparedStatement.setInt(1, Integer.parseInt(inventory.getId()));
                preparedStatement.setString(2, inventory.getSku());
                preparedStatement.setInt(3, inventory.getNumberofdays());
                preparedStatement.setInt(4, Integer.parseInt(inventory.getQtyavailable()));
                preparedStatement.setString(5, inventory.getBrand());
                preparedStatement.setString(6, inventory.getModel());
                preparedStatement.setString(7, inventory.getPrimarysupplier());
                preparedStatement.setString(8, inventory.getProducttype());
                preparedStatement.setString(9, inventory.getRcartridges());
                preparedStatement.setString(10, inventory.getDate());
                result = preparedStatement.executeUpdate();
                if (result == 1) {
                    System.out.println("Records have been entered successfully");
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(connection, preparedStatement);
        }
    }

    private void daysAdded() {
        Logs logger = new Logs();
        FileHandler fh = null;
        DBConnection con = new DBConnection();
        Connection connection = con.getCon();
        PreparedStatement preparedStatement = null;
        ArrayList<Integer> ids = new ArrayList<>();
        File file;

        String inputday = "26/09/2018";
        String filename = "D:\\Liam\\Documents\\JavaProjects\\logs\\log.txt";

        //--------------------SQL Statements---------------------------------------------//
        String sql = "UPDATE inventory SET date_add = ? "
                + " WHERE idinventory = ? ";

        String sql2 = "SELECT idinventory FROM inventory";

        //------------------------------------------------------------------------------//

        int rows = 0;
        int result = 0;
        ResultSet rs = null;

        //#sql
        try {
            //Statement statement = connection.createStatement();
            try {
                preparedStatement = connection.prepareStatement(sql2);
                rs = preparedStatement.executeQuery();
                //ResultSet rs = statement.executeQuery(sql2);
                while (rs.next()) {
                    //rows++;
                    ids.add(new Integer(rs.getInt(1)));

                }

            } finally {
                close(rs);
            }

            try {
                preparedStatement = connection.prepareStatement(sql);
                for (Integer row : ids) {
                    preparedStatement.setString(1, inputday);
                    preparedStatement.setInt(2, Integer.parseInt(row.toString()));
                    preparedStatement.executeUpdate();
                }

                file = new File(filename);
                fh = new FileHandler(file.getAbsolutePath());
                logger.setup(fh);
                logger.logSuccess("Update completed successfully");
                //System.out.println("Number of Rows: " + rows);

            } catch (FileNotFoundException fe) {

            } catch (IOException io) {

            } finally {
                close(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SecurityException se) {
            se.printStackTrace();
        }

    }

    private String todaysDate() {
        String todaysdate;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date currentdate = new Date();

        todaysdate = formatter.format(currentdate);

        return todaysdate;
    }

    public void insertData(Insert insert, Connection connection, String sku, int dayatzeroqty, int qty, String brand, String model, String psupplier, String product_type, String rcartridges, String netodateadded, String dateadd, int status) {

        PreparedStatement pstatementinsertion = null;
        ResultSet rs = null;

        int result, recordsupdate = 0;
        switch (insert) {
            case INSERTNEWDATA:
                /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
                String insertsqlstr = "INSERT INTO inventory(sku, days_at_zero_qty, qty, brand, model, primary_supplier, product_type, related_cart,  neto_date_added, date_add, status) " +
                        " VALUES(?,?,?,?,?,?,?,?,?,?,?) ";
                //--------------------------------------------------------END SQL STATEMENT---------------------------------------------------------------------------------------------------//

                try {
                    pstatementinsertion = connection.prepareStatement(insertsqlstr, Statement.RETURN_GENERATED_KEYS);
                    for (Inventory inventory : inventories) {
                        //pstatementinsertion.setInt(1, id);
                        pstatementinsertion.setString(1, sku);
                        pstatementinsertion.setInt(2, dayatzeroqty);
                        pstatementinsertion.setInt(3, qty);
                        pstatementinsertion.setString(4, brand);
                        pstatementinsertion.setString(5, model);
                        pstatementinsertion.setString(6, psupplier);
                        pstatementinsertion.setString(7, product_type);
                        pstatementinsertion.setString(8, rcartridges);
                        pstatementinsertion.setString(9, netodateadded);
                        pstatementinsertion.setString(10, dateadd);
                        pstatementinsertion.setInt(11, status);
                        result = pstatementinsertion.executeUpdate();
                        if (result == 1) {
                            recordsupdate++;
                        }
                    }
                    updateLogFile("Successfully updated " + recordsupdate + " records");

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void updateLogFile(String msg) {
        File file;
        String filename = "D:\\Liam\\Documents\\JavaProjects\\logs\\log.txt";
        Logs logger = new Logs();
        FileHandler fh = null;

        file = new File(filename);
        try {
            fh = new FileHandler(file.getAbsolutePath(), true);
            logger.setup(fh);
            logger.logSuccess(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void UpdateDaysLapses() throws IOException {

        int dbid;
        String dbsku;
        int daysatzeroqty;
        int dayslapse = 0;
        int dbqty;
        String brand;
        String dateadd;
        int status = 0;

        DBConnection con = new DBConnection();
        Connection connection = con.getCon();
        Statement statement = null;
        ResultSet resultSet = null;

        File file;
        String filename = "D:\\Liam\\Documents\\JavaProjects\\logs\\log.txt";
        Logs logger = new Logs();
        FileHandler fh = null;


        int invlistsize = 0;
        int rowaffected = 0;//rows getting updated
        file = new File(filename);
        fh = new FileHandler(file.getAbsolutePath(), true);

        try {
            createArrayInventory();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM inventory");
            invlistsize = inventories.size() - 1;
            Inventory lastitem = inventories.get(invlistsize);
            while (resultSet.next()) {
                dbid = resultSet.getInt("idinventory");
                dbsku = resultSet.getString("sku");
                daysatzeroqty = resultSet.getInt("days_at_zero_qty");
                dbqty = resultSet.getInt("qty");
                brand = resultSet.getString("brand");
                dateadd = resultSet.getString("date_add");

                for (Inventory inventory : inventories) {
                    int apiqty = Integer.parseInt(inventory.getQtyavailable());
                    int apiid = Integer.parseInt(inventory.getId());
                    String apisku = inventory.getSku();
                    String dupdate = inventory.getDateupdated();

                    if (apiid == dbid && (apiqty <= 0 && dbqty <= 0)) {
                        int dayslapses = daysLapseSinceInventoryAdded(dateadd, dupdate);
                        rowaffected = updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS, connection);

                    }
                    if (apiid == dbid && (apiqty <= 0 && dbqty > 0)) {
                        int dayslapses = daysLapseSinceInventoryAdded(dateadd, dupdate);
                        rowaffected += updateDaysAtZeroQuantity(dbsku, apiqty, dayslapses, status, UPDATE_DAYS_QTY_AT_ZERO, connection);
                    }
                }//end for
            }//end while

            if (rowaffected == 1) {
                logger.setup(fh);
                logger.logSuccess(String.format("Rows updated " + rowaffected));
            }
        } catch (ParseException pex) {
            logger.setup(fh);
            logger.logErrors(pex.getMessage());
        } catch (SQLException sqlex) {
            logger.setup(fh);
            logger.logErrors(sqlex.getMessage());
        } finally {
            close(connection, resultSet, statement);
        }


    }

    private int checkQuantityLevel(int quantity) {
        /*
            Seven level of status:
            0 - zero or negative quantity
            1 - quantity greater than 0 and less than or equal 10
            2 - quantity greater than 10 and less than or equal 20
            3 - quantity greater than 20 and less than or equal 40
            4 - quantity greater than 40 and less than or equal 60
            5 - quantity greater than 60 and less than or equal 80
            6 - quantity greater than 80 and less than or equal 100
            7 - quantity greater than 100
        */
        int status = 0;

        if (quantity <= 0) {
            return status;

        } else if (quantity > 0 && quantity <= 10) {
            status = 1;
            return status;

        } else if (quantity > 10 && quantity <= 20) {
            status = 2;
            return status;

        } else if (quantity > 20 && quantity <= 40) {
            status = 3;
            return status;

        } else if (quantity > 40 && quantity <= 60) {
            status = 4;
            return status;

        } else if (quantity > 60 && quantity <= 80) {
            status = 5;
            return status;

        } else if (quantity > 80 && quantity <= 100) {

            status = 6;
            return status;
        } else {
            status = 7;
            return status;
        }

    }

    private int updateDaysAtZeroQuantity(String sku, int qty, int dayslapse, int status, String methodupdate, Connection connection) {
        PreparedStatement preparedStatement;
        int rowaffected = 0;

        switch (methodupdate) {
            case InventoryDBObj.UPDATE_DAYS:
                String update = "UPDATE inventory "
                        + " SET days_at_zero_qty = ? "
                        + " WHERE sku = ? ";

                try {

                    preparedStatement = connection.prepareStatement(update);
                    preparedStatement.setInt(1, dayslapse);
                    //preparedStatement.setInt(2, id);
                    preparedStatement.setString(2, sku);
                    rowaffected = preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case InventoryDBObj.UPDATE_DAYS_QTY_AT_ZERO:
                String updatedaysqty = "UPDATE inventory "
                        + " SET days_at_zero_qty = ?, qty = ?, status = ?"
                        + " WHERE idinventory sku = ? ";

                try {

                    preparedStatement = connection.prepareStatement(updatedaysqty);
                    preparedStatement.setInt(1, dayslapse);
                    preparedStatement.setInt(2, qty);
                    preparedStatement.setInt(3, status);
                    //preparedStatement.setInt(4, id);
                    preparedStatement.setString(4, sku);
                    rowaffected = preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }


        return rowaffected;
    }

    private int daysLapseSinceInventoryAdded(String invdayadded, String dupdated) throws ParseException {

        int dayslapses;

        String newfdatestr = invdayadded.replaceAll("/", "-");

        DateFormat dateupdated = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tempdate = new SimpleDateFormat("dd-MM-yyyy");


        Date reformatedate = tempdate.parse(newfdatestr);
        Date reformateupdateday = dateupdated.parse(dupdated);
        String dateadddb = new SimpleDateFormat("yyyy-MM-dd").format(reformatedate);//date from DB reformatted
        String dayupdate = new SimpleDateFormat("yyyy-MM-dd").format(reformateupdateday);


        LocalDate localtodayDate = LocalDate.parse(dayupdate);
        LocalDate dateadd = LocalDate.parse(dateadddb);
        long days = Math.abs(ChronoUnit.DAYS.between(localtodayDate, dateadd));//TimeUnit.DAYS.convert(timelapse, TimeUnit.MILLISECONDS);
        dayslapses = (int) days;

        return dayslapses;
    }


    private void createArrayInventory() {
        //Inventory Fields
        String id;
        String suppitemcode;
        String dateadded;
        String dateupdate;
        String datetupdate;
        String sku;
        String brand;
        String model;
        String primarysupplier;

        String rcartridges;
        String qtyavailable;
        String approved;


        JSONArray categories;
        String prodtype;

        //data return from API(JSON Array)
        Iterator jsondata = inventoryitems.iterator();
        int catid;

        while (jsondata.hasNext()) {

            org.json.simple.JSONObject value = (JSONObject) jsondata.next();
            id = (String) value.get("ID");
            dateadded = (String) value.get("DateAdded");
            sku = (String) value.get("SKU");
            qtyavailable = (String) value.get("AvailableSellQuantity");
            brand = (String) value.get("Brand");
            model = (String) value.get("Model");
            primarysupplier = (String) value.get("PrimarySupplier");
            suppitemcode = (String) value.get("SupplierItemCode");
            approved = (String) value.get("Approved");
            rcartridges = (String) value.get("Misc12");
            prodtype = (String) value.get("Misc03");
            categories = (JSONArray) value.get("Categories");
            dateupdate = (String) value.get("DateUpdated");
            //datetupdate = (String) value.get("DateUpdatedTo");
            //String id, String dateadded, String sku, String qty, String brand, String model, String primarysupplier, String relatedcartridges
            inventorymanager.setInventory(id, dateadded, sku, qtyavailable, brand, model, suppitemcode, primarysupplier, rcartridges, approved, categories,
                    prodtype, dateupdate);
            inventories.add(inventorymanager.getInventory());
        }
        //System.out.println("line 94 INVOUT:  ...."+categories);
    }

    private void close(Connection connection, ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection connection, PreparedStatement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
