package dhbw.sa.kassensystemapplication.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;

import static dhbw.sa.kassensystemapplication.MainActivity.widthPixels;

/**
 * In dieser Klasse wird der Bezahlen-Bildschirm der Applikation erstellt.
 *
 * @author Daniel Schifano
 */
public class PayOrderFragment extends Fragment {

    /**
     * Nodes, in denen die Informationen für den Anwendern dargestellt werden, beziehungsweise die
     * sie verwenden können.
     */
    private CheckBox payAll;
    private CheckBox printerService;
    private TextView priceToPay;
    private Button payOrderButton;
    /**
     * Variablen, die zu "Berechnungen" innerhalb der Java-Klasse verwendet werden.
     */
    private int sizeOfRelativeLayout = 0;
    public static ArrayList<String> namesFromItems = new ArrayList<>();
    public static double storeOfSum = 0;
    public static String text = null;
    private double savePriceSeperat;
    private double savePriceFull;
    private boolean isAllPaid;
    private boolean checked;
    /**
     * Der Konstruktor, der zum aufrufen dieser Klasse benötigt wird.
     * Er benötigt keine Übergabe Parameter.
     * Damit wird der neue Bildschirm initalisiert und kann auf dem Smartphone angezeigt werden.
     */
    public PayOrderFragment() {
    }
    /**
     * Diese Methode wird aufgerufen wenn das Fragment erstellt wird.
     * Für den Prozess "Bestellung aufgeben" wird hier der letzte Bildschirm initialisiert.
     *
     * @param inflater Instantiiert ein XML-Layout in ein passendes View Objekt
     * @param container Erlaubt den Zugriff auf container Eigenschaften
     * @param savedInstanceState Gibt an in welchem Abschnitt des Lebenszyklus die App sich befindet.
     *                          Ob sie z.B. geschlossen wurde oder gestartet wurde.
     * @return View die dargestellt werden soll
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pay_order, container, false);
        checked = false;

        payAll = v.findViewById(R.id.payAllcheckBox);
        printerService = v.findViewById(R.id.printerServicecheckBox);
        priceToPay = v.findViewById(R.id.priceToPay);
        payOrderButton = v.findViewById(R.id.payOrderedItemsButton);

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10,
                this.getResources().getDisplayMetrics());
        float posY = pix;

        for(OrderedItem orderedItem: MainActivity.orderedItems){

            if(!orderedItem.isItemPaid()){

                for (Item item: MainActivity.allItems){

                    if(item.getItemID() == orderedItem.getItemID()){

                        storeOfSum = storeOfSum + (item.getRetailprice());
                        storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                        savePriceFull = storeOfSum;

                    }
                }

            }

        }

        priceToPay.setText(Double.toString(storeOfSum)+" €");
        priceToPay.setTextColor(Color.RED);
        priceToPay.setTextSize((float)(pix*1.2));

        RelativeLayout relativeLayout = v.findViewById(R.id.rlPayOrder);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        ScrollView scrollView = (ScrollView) v.findViewById(R.id.payAllScrollView);
        ViewGroup.LayoutParams paramsScrollView = scrollView.getLayoutParams();
        paramsScrollView.height = (MainActivity.heigthPixels)-(22*pix);


        /************************* Start der Forschleife für die Anzeige der Nodes***/

        for(OrderedItem orderedItem: MainActivity.orderedItems) {

            if (!orderedItem.isItemPaid()) {
                int sumOfItemIDsInOrder = 0;
                int itemID = orderedItem.getItemID();
                String name = null;

                //Get the right sum of orderedItems:
                for(OrderedItem item: MainActivity.orderedItems){
                    if(itemID == item.getItemID()){

                        if (!item.isItemPaid()) {
                            sumOfItemIDsInOrder++;
                        }

                    }
                }

                //Get the name of the Item:
                for (Item item : MainActivity.allItems) {

                    if (itemID == item.getItemID()) {
                        name = item.getName();
                        break;
                    }

                }

                if (!isItemAlreadySelected(name)) {

                    namesFromItems.add(name);

                    final TextView nameTextView = new TextView(getActivity());
                    final TextView quantityTextField = new TextView(getActivity());
                    final TextView inventoryTextView = new TextView(getActivity());
                    Button plus = new Button(getActivity());
                    Button minus = new Button(getActivity());

                    // Params for the TextView txt
                    nameTextView.setLayoutParams(new LinearLayout.LayoutParams(30 * pix, 10 * pix));
                    nameTextView.setText(name);
                    nameTextView.setX(pix*2);
                    nameTextView.setY(posY);
                    nameTextView.setPadding(pix, pix, pix, pix);
                    relativeLayout.addView(nameTextView);

                    // Params for the TextView inventory
                    inventoryTextView.setLayoutParams(new LinearLayout.LayoutParams(30*pix,10*pix));
                    inventoryTextView.setText(Integer.toString(sumOfItemIDsInOrder));
                    inventoryTextView.setX(widthPixels-15*pix-pix/2);
                    inventoryTextView.setY(posY);
                    inventoryTextView.setPadding(pix,pix,pix,pix);
                    relativeLayout.addView(inventoryTextView);

                    // Params for the TextView quantityTextField
                    quantityTextField.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                    quantityTextField.setText("0");
                    quantityTextField.setX(pix/10);
                    quantityTextField.setY(posY);
                    quantityTextField.setPadding(pix, pix, pix, pix);
                    relativeLayout.addView(quantityTextField);

                    // Set the Parameter for the Buttons plus and minus
                    // Params for the Button: +
                    plus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                    plus.setText("+");
                    plus.setX(widthPixels - 10*pix);
                    plus.setPadding(pix, pix, pix, pix);
                    plus.setY(posY);
                    relativeLayout.addView(plus);

                    //Params for the Button: -
                    minus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                    minus.setText("-");
                    minus.setX(MainActivity.widthPixels-6*pix);
                    minus.setPadding(pix, pix, pix, pix);
                    minus.setY(posY);
                    relativeLayout.addView(minus);

                    //The Y-position need to crow with the quantityTextField of Items!
                    posY = posY + 5 * pix;

                    /************************* PLus Und Minus Botton **********/

                    plus.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (payAll.isChecked()) {
                                if(Integer.parseInt((String)inventoryTextView.getText())>0) {

                                    // Update the quantity TextView
                                    int selectedQuantity = Integer.parseInt((String)quantityTextField.getText());
                                    selectedQuantity++;

                                    // Update the inventory TextView
                                    int numberOfInventory = Integer.parseInt((String) inventoryTextView.getText());
                                    numberOfInventory--;

                                    // Calculate the Sum
                                    double result = updateSum(true,(String)nameTextView.getText());

                                    // Set the updated quantity and inventory
                                    quantityTextField.setText(Integer.toString(selectedQuantity));
                                    inventoryTextView.setText(Integer.toString(numberOfInventory));

                                    // Set the updated Sum
                                    priceToPay.setText(Double.toString(result) + " €");
                                    if(payAll.isChecked()){

                                        savePriceSeperat = result;

                                    }

                                }
                            }
                        }

                    });

                    //Listener for the Button: - (The selected item will be remove from the order)
                    minus.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (payAll.isChecked()) {
                                //Get the quantity and the inventory
                                int selectedQuantity = Integer.parseInt((String) quantityTextField.getText());
                                int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());

                                // request if there is an Item in the Order
                                if (selectedQuantity > 0) {

                                    selectedQuantity--;
                                    numberOfInventory++;

                                    // Update the sumTextView and set the TextView Sum
                                    double result = updateSum(false, (String)nameTextView.getText());
                                    priceToPay.setText(Double.toString(result) + " €");

                                    if(payAll.isChecked()){
                                        savePriceSeperat = result;
                                    }

                                }

                                // Set the TextViews quantity and inventory
                                inventoryTextView.setText(Integer.toString(numberOfInventory));
                                quantityTextField.setText(Integer.toString(selectedQuantity));
                            }

                        }

                    });


                }
            }

        }

        //declare the length of the relativLayout
        for(String name: namesFromItems){

            sizeOfRelativeLayout++;

        }
        params.height = sizeOfRelativeLayout*5*pix;

        payOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checked) {
                    if (!payAll.isChecked()) {

                        for (OrderedItem item: MainActivity.orderedItems){

                            item.setItemIsPaid(true);

                        }
                    }

                    if(printerService.isChecked()){
                        new PrintSalesCheck().execute();
                    }
                    new UpdateOrder().execute();
                    checked = true;
                }

            }
        });

        //Auf Zugriff auf CheckBox warten
        payAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!payAll.isChecked()){

                    storeOfSum = savePriceFull;
                    priceToPay.setText(storeOfSum+" €");


                } else {
                    storeOfSum = savePriceSeperat;
                    priceToPay.setText(storeOfSum+" €");
                }

            }
        });

        return v;
    }
    /**
     * Diese Methode dient zur Abfrage ob ein Artikel aus der Datenbank bereits auf dem Bildschirm
     * dargestellt wird.
     * Dazu wird der Artikelname (als String) übergeben. Anschließend wird die ArrayList
     * "namesFromItems" durchlaufen und überprüft ob der Übergebene String sich in der Liste
     * befindet.
     * Die Methode gibt "true" zurück, wenn sich der Name in der Liste befindet. Ansonsten wird
     * "false" zurückgegeben.
     *
     * Die Funktion wird verwendet, damit jeder Artikel nur einmal auf dem Bildschirm dargestellt
     * wird.
     *
     * @param selectedName Der Artikelname, der überprüft werden soll.
     * @return true, wenn der Artikelname in der Liste ist.
     *         false, wenn der Artikelname nicht in der List ist.
     */
    private static boolean isItemAlreadySelected(String selectedName){

        for(String name: namesFromItems){
            if(name.equals(selectedName)){
                return true;
            }
        }

        return false;
    }
    /**
     * Mit dieser Methode wird zum einen der Gesamtpreis einer Bestellung berechnet. Zum anderen
     * werden die neu-Bestellten Artikel in der ArrayList OrderedItems (der MainActivity)
     * gespeichert beziehungsweise wieder davon entfernt.
     * Diese Liste wird nach Abschluss des Prozesses "Bestellung aufgeben" an die Datenbank
     * (und somit die Küche) weitergeleitet.
     *
     * Diese Methode wird bei jedem betätigen eines Plus- oder Minus-Buttons aufgerufen.
     *
     * @param isAdd Boolean, ob der gewählte Artikel zu der ArrayList hinzugefügt werden soll, oder
     *              ob dieser entfernt werden soll.
     * @param itemName String, gibt den Artikelname an. Anhand dem Namen wird ein Artikel
     *                 hinzugefügt oder gelöscht.
     * @return Double, den Gesamtpreis einer Bestellung.
     */
    private double updateSum(boolean isAdd, String itemName){


        for(Item item: MainActivity.allItems){

            if(item.getName().equals(itemName)){

                if(isAdd){

                    for (OrderedItem orderedItem: MainActivity.orderedItems){

                        if (orderedItem.getItemID() == item.getItemID()){

                            if (!orderedItem.isItemPaid()) {
                                storeOfSum = storeOfSum + (item.getRetailprice());
                                storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));
                                //MainActivity.orderedItems.remove(orderedItem);
                                orderedItem.setItemIsPaid(true);
                                //MainActivity.orderedItems.add(orderedItem);
                                return storeOfSum;
                            }
                        }

                    }
                } else {


                    for (OrderedItem orderedItem: MainActivity.orderedItems){

                        if (orderedItem.getItemID() == item.getItemID() && orderedItem.isItemPaid()){

                            storeOfSum = storeOfSum - (item.getRetailprice());
                            storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));
                            //MainActivity.orderedItems.remove(orderedItem);
                            orderedItem.setItemIsPaid(false);
                            //MainActivity.orderedItems.add(orderedItem);
                            return storeOfSum;
                        }

                    }

                }

            }

        }

        return storeOfSum;

    }
    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param text Der Text welcher dargestellt werden soll.
     */
    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
            this.text = null;
        }


    }
    /**
     * Mithilfe dieser Methode wird die Java-Klasse TableSelectionFragment aufgerufen und die
     * Java-Klasse PayOrderFragment wird nicht mehr dargestellt.
     */
    private void showTableFragment(){

        TableSelectionFragment fragment = new TableSelectionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
    /**
     * Mithilfe dieser Methode wird die Java-Klasse ItemSelectFragment aufgerufen und die
     * Java-Klasse PayOrderFragment wird nicht mehr dargestellt.
     */
    private void showItemSelectFragment(){

        ItemSelectFragment fragment = new ItemSelectFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
    /**
     * Diese Klasse wird dafür verwendet, eine bereits bestehende Bestellung, die mithilfe der
     * Applikation upgedated wurde, an den Server weiterzuleiten.
     */
    private class UpdateOrder extends AsyncTask<Void, Void, Void> {
        /**
         * Mit dieser Methode wird eine bereits bestehende Bestellung die mithilfe der Applikation
         * upgedated wurde an den Server übermittelt.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet
         *               werden sollen.
         * @return gibt null zurück, da Informationen lediglich an den Server geschickt werden.
         */
        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {

                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/orderedItem", HttpMethod.PUT,
                                Entity.getEntity(MainActivity.orderedItems),Integer.class );

                for(OrderedItem item: MainActivity.orderedItems){
                    if(!item.isItemPaid()){
                        isAllPaid = false;
                        break;
                    } else {
                        isAllPaid = true;
                    }
                }

                namesFromItems.clear();
                storeOfSum = 0;
                text = null;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();
                System.out.println("\n"+text+"\n--------------------------");
                namesFromItems.clear();
                return null;
            }catch (Exception e){
                text = "undefinierter Fehler";
                namesFromItems.clear();
                e.printStackTrace();
            }

            return null;
        }
        /**
         * Falls bei der Übertragung der Bestellung zum Server ein Fehler auftritt, wird mithilfe
         * der ShowToast-Methode dieser Fehler dargestellt.
         * @param aVoid wird hier nicht benötigt
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (text == null){

                if(!isAllPaid){
                    showItemSelectFragment();
                } else {
                    showTableFragment();
                }

            } else {
                showToast(text);
            }

        }

    }
    /**
     * Diese Klasse wird dafür verwendet, einen Kundenbeleg als Rechnung anzufordern. Dabei wird mit
     * dem Schlüsselwort "printOrder" der Server kontaktiert. Daraufhin wird der Kundenbeleg
     * ausgedruckt.
     */
    private class PrintSalesCheck extends AsyncTask<Void, Void, Void> {
        /**
         * Mit dieser Methode wird der Kundenbeleg angefordert.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet
         *               werden sollen.
         * @return gibt null zurück, da Informationen lediglich an den Server geschickt werden.
         */
        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                //Den Bon Ausdrucken
                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/printOrder/"+MainActivity.selectedOrderID, HttpMethod.POST,
                                Entity.getEntity(null),Integer.class);

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();
            }catch (Exception e){
                text = "undefinierter Fehler";
                e.printStackTrace();
            }

            return null;
        }
        /**
         * Falls bei der Übertragung zum Server ein Fehler auftritt, wird mithilfe
         * der ShowToast-Methode dieser Fehler dargestellt.
         * @param aVoid wird hier nicht benötigt
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showToast(text);

        }


    }

}

