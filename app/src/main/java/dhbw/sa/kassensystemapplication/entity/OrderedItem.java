package dhbw.sa.kassensystemapplication.entity;

import com.fasterxml.jackson.annotation.JsonProperty;


public class OrderedItem {
    @JsonProperty private int orderedItemID;
    @JsonProperty private int orderID;
    @JsonProperty private int itemID;
    @JsonProperty private boolean itemPaid;
    @JsonProperty private boolean itemProduced;

    public OrderedItem() {}

    /**
     * Konstruktor zum Abrufen eines vollständigen OrderedItems aus der Datenbank.
     * @param orderedItemID
     * @param orderID
     * @param itemID
     * @param itemPaid
     * @param itemProduced
     */
    public OrderedItem(@JsonProperty("orderedItemID")int orderedItemID,
                       @JsonProperty("orderID") int orderID,
                       @JsonProperty("itemID") int itemID,
                       @JsonProperty("itemPaid") boolean itemPaid,
                       @JsonProperty("itemProduced") boolean itemProduced) {
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

    public void setItemIsPaid(){
        this.itemPaid = true;
    }

}