package dhbw.sa.kassensystemapplication.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Model für einen Datensatz der Datenbanktabelle orders.
 *
 * @author Marvin Mai
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JsonProperty private int orderID;
    @JsonProperty private String itemIDs;
    @JsonProperty private int tableID;
    @JsonProperty private double price;
    @JsonProperty private org.joda.time.DateTime date;
    @JsonProperty private boolean paid;

    /*Constructors*/

    /**
     * Default-Contructor
     */
    public Order() {
    }

    /**
     * Konstruktor für eine vollständige Bestellung, die aus der MySQL-Datenbank gelesen wurde.
     * @param orderID ID der Bestellung aus der Datenbank.
     * @param itemIDs Artikel-IDs der Bestellung aus der Datenbank, not null.
     * @param tableID Tisch der Bestellung aus der Datenbank.
     * @param price Preis der Bestellung aus der Datenbank.
     * @param date Datum und Zeitpunkt der Bestellung aus der Datenbank, not null.
     * @param paid Bezahlstatus der Bestellung aus der Datenbank.
     */
    //@JsonCreator
    public Order(@JsonProperty("orderID") int orderID,
                  @JsonProperty("itemIDs") String itemIDs,
                  @JsonProperty("tableID") int tableID,
                  @JsonProperty("price") double price,
                  @JsonProperty("date") DateTime date,
                  @JsonProperty("paid") boolean paid) {

        if(itemIDs == null)
            throw new NullPointerException("Es müssen itemIDs übergeben werden!");

        if(date == null){}
            //throw new NullPointerException("Es muss ein Datum übergeben werden!");

        this.orderID = orderID;
        this.itemIDs = itemIDs;
        this.tableID = tableID;
        this.price = price;
        this.date = date;
        this.paid = paid;
    }

    /**
     * Konstruktor zum Erstellen einer neuen Bestellung,
     * die anschließend an die MySQL-Datenbank übertragen werden soll
     * @param itemIDs Artikel-IDs der neuen Bestellung.
     * @param tableID Tisch der neuen Bestellung, not null.
     * @param price Preis der neuen Bestellung.
     * @param date Datum der neuen Bestellung, not null.
     * @param paid Bezahlstatus der neuen Bestellung.
     */
    public Order(String itemIDs, int tableID, double price, DateTime date, boolean paid) {

        if(itemIDs == null)
            throw new NullPointerException("Es müssen itemIDs übergeben werden!");

        if(date == null){}
            //throw new NullPointerException("Es ein Datum übergeben werden!");

        this.itemIDs = itemIDs;
        this.tableID = tableID;
        this.price = price;
        this.date = date;
        this.paid = paid;
    }
    public Order(String itemIDs, int tableID, double price, boolean paid) {

        if(itemIDs == null)
            throw new NullPointerException("Es müssen itemIDs übergeben werden!");

        this.itemIDs = itemIDs;
        this.tableID = tableID;
        this.price = price;
        this.paid = paid;
    }


    /*Getter*/

    public int getOrderID() {
        return this.orderID;
    }

    public String getItems() {
        return this.itemIDs;
    }

    public int getTable() {
        return this.tableID;
    }

    public Table getTable(ArrayList<Table> allTables) {
        for(Table t: allTables) {
            if(t.getTableID() == this.tableID)
                return t;
        }
        return null;
    }

    public double getPrice() {
        return this.price;
    }

    public DateTime getDate() {
        return this.date;
    }

    public boolean isPaid() {
        return paid;
    }

    /*Setter*/

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setItemIDs(String itemIDs) {

        if(itemIDs == null)
            throw new NullPointerException("Eine Bestellung muss Artikel enthalten!");

        this.itemIDs = itemIDs;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDate(DateTime dateTime) {

        if(dateTime == null)
            throw new NullPointerException("Es muss ein Datum festgelegt werden!");

        this.date = dateTime;
    }

    public void setPaid() {
        this.paid = true;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    //region Ermittlung der Items

    /* Funktionen zum Erhalten der Items, die zu dieser Bestellung gehoeren.
       Zur Verwendung alle Items uebergeben, auch nicht verfuegbare.
     */

    public ArrayList<Item> getItems(ArrayList<Item> allItems) {

        if(allItems == null)
            throw new NullPointerException("Es müssen alle Artikel der Datenbank übergeben werden.");

        ArrayList<Item> items = new ArrayList<>();
        ArrayList<Integer> itemIDs = splitItemIDString(this.itemIDs);
        for(Integer itemID: itemIDs) {
            Item item = getItemByID(itemID, allItems);
            items.add(item);
        }
        return items;
    }

    public static ArrayList<Integer> splitItemIDString(String itemIDString) {

        if(itemIDString == null)
            throw new NullPointerException("Es wurde kein ID-String übergeben!");

        //Ermitteln der einzelnen IDs aus String
        ArrayList<Integer> itemIDList = new ArrayList<>();
        for(String itemID: itemIDString.split(";")) {
            itemIDList.add(Integer.parseInt(itemID));
        }
        return itemIDList;
    }

    private Item getItemByID(int itemID, ArrayList<Item> allItems) {

        if(allItems == null)
            throw new NullPointerException("Es müssen alle Artikel der Datenbank übergeben werden!");

        for(Item i: allItems) {
            if(i.getItemID() == itemID)
                return i;
        }
        return null;
    }

    //endregion

    public static String joinIDsIntoString(ArrayList<Item> items) {

        if(items == null)
            throw new NullPointerException("Es wurden keine Artikel übergeben!");

        StringBuilder IDString = new StringBuilder();
        for(Item i: items) {
            IDString.append(i.getItemID()).append(";");
        }
        return IDString.toString();
    }

    public static String joinIntIDsIntoString(ArrayList<Integer> itemIDs) {

        if(itemIDs == null)
            throw new NullPointerException("Es wurden keine Artikel-IDs übergeben!");

        StringBuilder IDString = new StringBuilder();
        for(Integer i: itemIDs) {
            IDString.append(i).append(";");
        }
        return IDString.toString();
    }

}
