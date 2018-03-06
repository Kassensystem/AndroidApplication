package dhbw.sa.kassensystemapplication.entity;

public class OrderedItem {
    private int orderedItemID;
    private int orderID;
    private int itemID;
    private boolean itemPaid;
    private boolean itemProduced;

    /**
     * Konstruktor zum Abrufen eines vollständigen OrderedItems aus der Datenbank.
     * @param orderedItemID
     * @param orderID
     * @param itemID
     * @param itemPaid
     * @param itemProduced
     */
    public OrderedItem(int orderedItemID, int orderID, int itemID, boolean itemPaid, boolean itemProduced) {
        this.orderedItemID = orderedItemID;
        this.orderID = orderID;
        this.itemID = itemID;
        this.itemPaid = itemPaid;
        this.itemProduced = itemProduced;
    }

    /**
     * Konstruktor zum Hinzufügen eines neuen OrderedItems in die Datenbank.
     * @param orderID
     * @param itemID
     */
    public OrderedItem(int orderID, int itemID) {
        this.orderID = orderID;
        this.itemID = itemID;
        this.itemPaid = false;
        this.itemProduced = false;
    }

    public int getOrderedItemID() {
        return orderedItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getItemID() {
        return itemID;
    }

    public boolean isItemPaid() {
        return itemPaid;
    }

    public boolean isItemProduced()
    {
        return itemProduced;
    }

    public void itemIsPaid() {
        this.itemPaid = true;
    }

    public void itemIsProduced()
    {
        this.itemProduced = true;
    }

}