import Controllers.InventoryDBObj;

public class Main{

    public static void main(String args[])
    {
        start();
    }

    private static void start()
    {
        InventoryDBObj dbObj = new InventoryDBObj();
        dbObj.insert();
        //dbObj.update();

    }

}
