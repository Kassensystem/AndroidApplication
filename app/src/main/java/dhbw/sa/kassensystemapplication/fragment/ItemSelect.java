package dhbw.sa.kassensystemapplication.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import org.joda.time.DateTime;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;
import static dhbw.sa.kassensystemapplication.MainActivity.orderedItems;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;

import static dhbw.sa.kassensystemapplication.MainActivity.allItems;
import static dhbw.sa.kassensystemapplication.MainActivity.url;
import static dhbw.sa.kassensystemapplication.MainActivity.widthPixels;

/**
 * In dieser Klasse wird der Bildschirm initialisiert, auf dem die Bedienung auswählen kann, welche Artikel einer Bestellung hinzugefügt werden kann.
 * Ebenfalls werden in dieser Klasse die Buttons "Bezahlen" und "Bestellen" initialisiert. Durch diese wird die Kommunikation mit dem Server gestartet.
 * @author Daniel Schifano
 */
public class ItemSelect extends Fragment {

    // Nodes from the layout
    private TextView sum;
    private Button orderBtn;
    private Button paidBtn;

    /**
     * Speichert die Fehlermeldung des Servers.
     */
    public static String text = null;
    /**
     * Gibt an, wie lang der String maximal sein darf, bevor eine neue Zeile angefangen werden muss.
     */
    private int lengthOfStringTillSplit1 = 18;
    /**
     * Gibt an, wie lang der String maximal sein darf, bevor eine dritte Zeile angefangen werden muss.
     */
    private int lengthOfStringTillSplit2 = 2*lengthOfStringTillSplit1;

    // variables
    private double storeOfSum;
    private int sizeOfRelativeLayout;
    private boolean isOrderPaid;

    // Constructor
    public ItemSelect() {

    }
    /**
     * Diese Methode wird aufgerufen, wenn das Fragment erstellt wird.
     * Dabei werden alle Artikel (mit ihren Informationen Preis und Menge) die in der Datenbank als verfügbar hinterlegt sind dargestellt.
     * Zusätzlich werden die Befehle zur Übermittlung der Bestellung an den Server hier gestartet.
     *
     * @param inflater Instantiiert ein XML-Layout in ein passendes View Objekt
     * @param container Erlaubt den Zugriff auf container Eigenschaften
     * @param savedInstanceState Gibt an in welchem Abschnitt des Lebenszyklus die App sich befindet. Ob sie z.B. geschlossen wurde oder gestartet wurde.
     * @return View die dargestellt werden soll
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // declare the View of the Fragment
        View v = inflater.inflate(R.layout.fragment_item_select, container, false);

        // initialize the Nodes from the layout
        orderBtn = v.findViewById(R.id.orderBtn);
        paidBtn = v.findViewById(R.id.paidBtn);
        sum = v.findViewById(R.id.sumIV);
        sum.setText("0.0 €");

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        float posY = pix;

        // declare the relative Layout. There the Nodes for the Order get added.
        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.rl);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        ScrollView scrollView = (ScrollView) v.findViewById(R.id.scrollView2);
        ViewGroup.LayoutParams paramsScrollView = scrollView.getLayoutParams();
        paramsScrollView.height = (MainActivity.heigthPixels)-(22*pix);

        //sort the Items which are not available. So the Scroll View is not to long.
        for (Item item: MainActivity.allItems){

            if(item.isAvailable())
                sizeOfRelativeLayout++;

        }
        if(allItems.size() == 0){
            showToast("Es befinden sich keine Artikel in der Datenbank.");
        }

        params.height = sizeOfRelativeLayout*5*pix+8;

        // Change the Color of the TextView Sum
        sum.setTextColor(Color.RED);
        sum.setTextSize((float) (pix*1.5));



        // initialise the Nodes: TextView and the Buttons
        for(int i = 0; i< MainActivity.allItems.size(); i++){

            if(MainActivity.allItems.get(i).isAvailable()) {

                String name = MainActivity.allItems.get(i).getName();
                if(name.length() > lengthOfStringTillSplit1){
                    name = name.substring(0,lengthOfStringTillSplit1) + "-\n" + name.substring(lengthOfStringTillSplit1);
                    if (name.length() > lengthOfStringTillSplit2){
                        name = name.substring(0,lengthOfStringTillSplit1) + "-\n" + name.substring(lengthOfStringTillSplit1,lengthOfStringTillSplit2) +"-\n" + name.substring(lengthOfStringTillSplit1);
                    }
                }
                int inventory = allItems.get(i).getQuantity();
                if(inventory>= 1000){
                    inventory = 999;
                }

                // Set the Parameter for the TextViews Name, inventory and Quantity

                // declaration of the TextView for the Items and the Buttons (+ and -)
                final TextView nameTextView = new TextView(getActivity());
                final TextView quantityTextField = new TextView(getActivity());
                final TextView inventoryTextView = new TextView(getActivity());
                Button plus = new Button(getActivity());
                Button minus = new Button(getActivity());

                // Params for the TextView quantityTextField
                quantityTextField.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                quantityTextField.setId(i);
                quantityTextField.setText("0");
                quantityTextField.setX(pix/10);
                quantityTextField.setY(posY);
                quantityTextField.setPadding(pix, pix, pix, pix);
                relativeLayout.addView(quantityTextField);

                // Params for the TextView txt
                nameTextView.setLayoutParams(new LinearLayout.LayoutParams(30 * pix, 10 * pix));
                nameTextView.setText(name);
                nameTextView.setId(-i);
                nameTextView.setX(pix*2);
                nameTextView.setY(posY);
                nameTextView.setPadding(pix, pix, pix, pix);
                relativeLayout.addView(nameTextView);



                // Params for the TextView inventory
                inventoryTextView.setLayoutParams(new LinearLayout.LayoutParams(30*pix,10*pix));
                inventoryTextView.setText(Integer.toString(inventory));
                inventoryTextView.setX(widthPixels-15*pix-pix/2);
                inventoryTextView.setY(posY);
                inventoryTextView.setPadding(pix,pix,pix,pix);
                relativeLayout.addView(inventoryTextView);

                // To start the update-Order with already chosen items
                int startUpdate = 0;

                for (OrderedItem orderedItem: orderedItems){

                    if(orderedItem.getItemID() == MainActivity.allItems.get(i).getItemID()){

                        int number = Integer.parseInt((String)quantityTextField.getText());
                        number++;
                        startUpdate++;

                        int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());
                        numberOfInventory--;
                        inventoryTextView.setText(Integer.toString(numberOfInventory));

                        storeOfSum = storeOfSum + MainActivity.allItems.get(i).getRetailprice();
                        storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                        quantityTextField.setText(Integer.toString(number));
                        sum.setText(Double.toString(storeOfSum) + " €");

                    }

                }

                // Set the Parameter for the Buttons plus and minus
                //Params for the Button: -
                minus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                minus.setText("-");
                minus.setX(MainActivity.widthPixels-8*pix);
                minus.setPadding(pix, pix, pix, pix);
                minus.setY(posY);
                relativeLayout.addView(minus);


                // Params for the Button: +
                plus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                plus.setText("+");
                plus.setX(widthPixels - 12*pix);
                plus.setPadding(pix, pix, pix, pix);
                plus.setY(posY);
                relativeLayout.addView(plus);

                //The Y-position need to crow with the quantityTextField of Items!
                posY = posY + 5 * pix;

                //Listener for the Button: + (The selected item will be added to the order)
                plus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(Integer.parseInt((String)inventoryTextView.getText())>0) {

                            // Update the quantity TextView
                            int selectedQuantity = Integer.parseInt((String)quantityTextField.getText());
                            selectedQuantity++;
                            if(selectedQuantity >= 1000){
                                selectedQuantity = 999;
                            }

                            // Update the inventory TextView
                            int numberOfInventory = Integer.parseInt((String) inventoryTextView.getText());
                            numberOfInventory--;
                            if (numberOfInventory == 998){
                                numberOfInventory = 999;
                            }

                            // Calculate the Sum
                            String itemName = (String) nameTextView.getText();
                            if(itemName.length() >lengthOfStringTillSplit1){
                                itemName = itemName.substring(0,lengthOfStringTillSplit1) + itemName.substring(lengthOfStringTillSplit1 +2);
                                if (itemName.length() > 40){
                                    itemName = itemName.substring(0,lengthOfStringTillSplit2)+itemName.substring(lengthOfStringTillSplit2+2);
                                }
                            }
                            double result = UpdateSum(true,itemName);

                            // Set the updated quantity and inventory
                            quantityTextField.setText(Integer.toString(selectedQuantity));
                            inventoryTextView.setText(Integer.toString(numberOfInventory));

                            // Set the updated Sum
                            sum.setText(Double.toString(result) + " €");

                        }
                    }

                });

                //Listener for the Button: - (The selected item will be remove from the order)
                final int finalStartUpdate = startUpdate;
                minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //Get the quantity and the inventory
                        int selectedQuantity = Integer.parseInt((String) quantityTextField.getText());
                        int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());

                        // request if there is an Item in the Order
                        if (selectedQuantity > 0) {


                            if(finalStartUpdate < selectedQuantity) {
                                selectedQuantity--;
                                numberOfInventory++;
                                if (numberOfInventory >= 1000) {
                                    numberOfInventory = 999;
                                }

                                // Update the sumTextView and set the TextView Sum
                                String itemName = (String) nameTextView.getText();
                                if (itemName.length() > lengthOfStringTillSplit1) {
                                    itemName = itemName.substring(0, lengthOfStringTillSplit1) + itemName.substring(lengthOfStringTillSplit1 + 2);
                                    if (itemName.length() > 40) {
                                        itemName = itemName.substring(0, lengthOfStringTillSplit2) + itemName.substring(lengthOfStringTillSplit2 + 2);
                                    }
                                }
                                double result = UpdateSum(false, itemName);
                                sum.setText(Double.toString(result) + " €");
                            }

                        }

                        // Set the TextViews quantity and inventory
                        inventoryTextView.setText(Integer.toString(numberOfInventory));
                        quantityTextField.setText(Integer.toString(selectedQuantity));

                    }

                });
            }
        }

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOrderPaid = false;
                new UpdateOrder().execute();


            }
        });

        paidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOrderPaid = true;
                new UpdateOrder().execute();

            }
        });

        return v;
    }

    //update the sumTextView in the Fragment

    /**
     * In dieser Methode wird der Gesamtpreis der Bestellung errechnet.
     *
     * @param isAdd Wurde ein Artikle hinzugefügt oder abgezogen.
     * @param itemName Welcher Artikel wurde hinzugefügt/abgezogen.
     * @return Den Gesamtpreis der Bestellung.
     */
    private double UpdateSum(boolean isAdd, String itemName){


        double result = 0;
        int getItem = -1;

        //summation
        if (isAdd){

                // Search in all items, to get the retailprice
                for (int i = 0; i < MainActivity.allItems.size(); i++) {

                    // Search if one item from all items got the same name
                    if (MainActivity.allItems.get(i).getName().equalsIgnoreCase(itemName)) {

                        storeOfSum = storeOfSum + (MainActivity.allItems.get(i).getRetailprice());
                        storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                        // add the Item to the Order
                        orderedItems.add(new OrderedItem(MainActivity.selectedOrderID,MainActivity.allItems.get(i).getItemID()));

                        break;
                    }

                }

                result = storeOfSum;

        //subtract
        } else {

            // Search in all items, to get the retailprice
            for (int i = 0; i < MainActivity.allItems.size(); i++) {

                // Search if one item from all items got the same name
                if (MainActivity.allItems.get(i).getName().equals(itemName)) {

                    storeOfSum = storeOfSum - (MainActivity.allItems.get(i).getRetailprice());
                    storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                    for (int j = 0; j<=MainActivity.orderedItems.size();j++){

                        if(MainActivity.orderedItems.get(j).getItemID() == MainActivity.allItems.get(i).getItemID()){

                            getItem = j;
                            break;

                        }

                    }
                }

            }

            MainActivity.orderedItems.remove(getItem);

            result = storeOfSum;

        }




        return result;

    }

    /**
     * Diese Klasse wird dafür verwendet, eine bereits bestehende Bestellung, die mithilfe der Applikation upgedated wurde, an den Server weiterzuleiten.
     */

    private class UpdateOrder extends AsyncTask<Void, Void, Void> {

        /**
         * Mit dieser Methode wird eine bereits bestehende Bestellung die mithilfe der Applikation upgedated wurde an den Server übermittelt.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet werden sollen.
         * @return gibt null zurück, da Informationen lediglich an den Server geschickt werden.
         */
        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {

                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/orderedItem", HttpMethod.POST,
                                Entity.getEntity(MainActivity.orderedItems),Integer.class );

                if (!isOrderPaid) {
                    MainActivity.orderedItems.clear();
                }
                text = null;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();

            }catch (Exception e){
                text = "Die Verbindung zum Server ist unterbrochen worden!";
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Falls bei der Übertragung der Bestellung zum Server ein Fehler auftritt, wird mithilfe der ShowToast-Methode dieser Fehler dargestellt.
         * @param aVoid wird hier nicht benötigt
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showToast(text);

            if(text == null){

                if (isOrderPaid) {
                    new GetOrderedItems().execute();
                } else {
                    showTableFragment();
                }

            }

        }


    }

    private class GetOrderedItems extends AsyncTask<Void,Void,ArrayList<OrderedItem>> {

        @Override
        protected ArrayList<OrderedItem> doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();


            try {
                ResponseEntity<ArrayList<OrderedItem>> responseEntity =
                        restTemplate.exchange
                                ( MainActivity.url + "/orderedItems/"+MainActivity.selectedOrderID, HttpMethod.GET,
                                        Entity.getEntity(null), new ParameterizedTypeReference<ArrayList<OrderedItem>>() {});

                MainActivity.orderedItems = responseEntity.getBody();
                text = null;

                return MainActivity.orderedItems;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten";
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute( ArrayList<OrderedItem> orderedItems) {
            super.onPostExecute(orderedItems);

            if(text != null){
                showToast(text);
                text = null;
            }

            if (orderedItems != null) {
                MainActivity.orderedItems = orderedItems;
                showPayFragment();
            }

        }
    }

    /**
     * Mithilfe dieser Methode wird die Klasse TableSelection aufgerufen und die Klasse ItemSelect wird nicht mehr dargestellt.
     */
    private void showTableFragment(){

        TableSelection fragment = new TableSelection();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    private void showPayFragment(){

        PayOrder fragment = new PayOrder();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param text Der Text welcher dargestellt werden soll.
     */
    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_LONG).show();
            this.text = null;
        }


    }

}