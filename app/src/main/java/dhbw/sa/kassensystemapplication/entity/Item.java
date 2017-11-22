package dhbw.sa.kassensystemapplication.entity;


/**
 * Model für einen Datensatz der Datenbanktabelle items.
 *
 * @author Marvin Mai
 */
public class Item {
    private int itemID;
    private String name;
    private double retailprice;
    private int quantity;
    private boolean available;

    /**
     * Default-Constructor
     */
    public Item() {
        //default constructor
    }

    /**
     * Konstruktor für einen vollständigen Artikel, der aus der MySQL-Datenbank gelesen wurde.
     * @param itemID ID des Tisches aus der Datenbank.
     * @param name Name  des Tisches aus der Datenbank.
     * @param retailprice Verkaufspreis des Tisches aus der Datenbank.
     * @param quantity Anzahl  des Tisches aus der Datenbank, ermittelt aus Wareneingängen und Warenausgängen.
     * @param available Verfügbarkeit des Tisches aus der Datenbank.
     */
    public Item(int itemID, String name, double retailprice, int quantity, boolean available){

        if(name == null)
            throw new NullPointerException("Es muss ein Name übergeben werden!");

        this.itemID = itemID;
        this.name = name;
        this.retailprice = retailprice;
        this.quantity = quantity;
        this.available = available;
    }

    /**
     * Konstruktor zum Erstellen eines neuen Artikels, der anschließend an die MySQL-Datenbank
     * übertragen werden soll.
     * @param name Name des neuen Artikels.
     * @param retailprice Verkaufspreis des neuen Artikels.
     * @param quantity Anzahl des neuen Artikels, diese wird als neuer Wareneingang abgespeichert.
     * @param available Verfügbarkeit des neuen Artikels.
     */
    public Item(String name, double retailprice, int quantity, boolean available){

        if(name == null)
            throw new NullPointerException("Es muss ein Name übergeben werden!");

        this.name = name;
        this.retailprice = retailprice;
        this.quantity = quantity;
        this.available = available;
    }

    /**
     * Konstruktor zum Erstellen eines Artikel ohne eine Anzahl, der nicht an die Datenbank übertragen werden soll.
     * @param itemID ID des Artikels.
     * @param name Name des Artikels.
     * @param retailprice Verkaufspreis des Artikels.
     * @param available Verfügbarkeit des Artikels.
     */
    public Item(int itemID, String name, double retailprice, boolean available){

        if(name == null)
            throw new NullPointerException("Es muss ein Name übergeben werden!");

        this.itemID = itemID;
        this.name = name;
        this.retailprice = retailprice;
        this.available = available;
    }

    /*Getter*/

    public int getItemID() {
        return this.itemID;
    }

    public String getName() {
        return this.name;
    }

    public double getRetailprice() {
        return this.retailprice;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    /*Setter*/

    public void setAvailable(boolean available) {
        this.available = available;
    }

}
