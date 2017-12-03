package dhbw.sa.kassensystemapplication.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.ItemSelection;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;

import static dhbw.sa.kassensystemapplication.MainActivity.url;

public class ItemSelect extends Fragment {

    // Nodes from the layout
    private TextView sum;
    private Button orderBtn;
    private Button paidBtn;

    // variables
    private double storeOfSum;
    private int sizeOfRelativeLayout;


    // Constructor
    public ItemSelect() {

    }

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
        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl);
        ViewGroup.LayoutParams params = rl.getLayoutParams();

        //sort the Items which are not available. So the Scroll View is not to long.
        for (Item item: MainActivity.allItems){

            if(item.isAvailable())
                sizeOfRelativeLayout++;

        }

        params.height = sizeOfRelativeLayout*5*pix;

        // Change the Color of the TextView Sum
        sum.setTextColor(Color.RED);
        sum.setTextSize(pix*2);

        // initialise the Nodes: TextView and the Buttons
        for(int i = 0; i< MainActivity.allItems.size(); i++){

            if(MainActivity.allItems.get(i).isAvailable()) {

                String name = MainActivity.allItems.get(i).getName();

                // Set the Parameter for the TextViews Name, inventory and Quantity

                // declaration of the TextView for the Items and the Buttons (+ and -)
                final TextView nameTextView = new TextView(getActivity());
                final TextView quantityTextField = new TextView(getActivity());
                final TextView inventoryTextView = new TextView(getActivity());
                Button plus = new Button(getActivity());
                Button minus = new Button(getActivity());

                // Params for the TextView txt
                nameTextView.setLayoutParams(new LinearLayout.LayoutParams(30 * pix, 10 * pix));
                nameTextView.setText(name);
                nameTextView.setId(-i);
                nameTextView.setX(pix / 10);
                nameTextView.setY(posY);
                nameTextView.setPadding(pix, pix, pix, pix);
                rl.addView(nameTextView);

                // Params for the TextView inventory
                inventoryTextView.setLayoutParams(new LinearLayout.LayoutParams(30*pix,10*pix));
                inventoryTextView.setText(Integer.toString(MainActivity.allItems.get(i).getQuantity()));
                inventoryTextView.setX(15 * pix);
                inventoryTextView.setY(posY);
                inventoryTextView.setPadding(pix,pix,pix,pix);
                rl.addView(inventoryTextView);

                // Params for the TextView quantityTextField
                quantityTextField.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                quantityTextField.setId(i);
                quantityTextField.setText("0");
                quantityTextField.setX(18 * pix);
                quantityTextField.setY(posY);
                quantityTextField.setPadding(pix, pix, pix, pix);
                rl.addView(quantityTextField);

                // To start the update-Order with already chosen items
                for (Integer itemID: MainActivity.orderItemIDs){

                    if(itemID == MainActivity.allItems.get(i).getItemID()){

                        int number = Integer.parseInt((String)quantityTextField.getText());
                        number++;

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
                // Params for the Button: +
                plus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                plus.setText("+");
                plus.setX(21 * pix);
                plus.setPadding(pix, pix, pix, pix);
                plus.setY(posY);
                rl.addView(plus);

                //Params for the Button: -
                minus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                minus.setText("-");
                minus.setX(25 * pix);
                minus.setPadding(pix, pix, pix, pix);
                minus.setY(posY);
                rl.addView(minus);

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

                            // Update the inventory TextView
                            int numberOfInventory = Integer.parseInt((String) inventoryTextView.getText());
                            numberOfInventory--;

                            // Calculate the Sum
                            double result = UpdateSum(true,(String)nameTextView.getText());

                            // Set the updated quantity and inventory
                            quantityTextField.setText(Integer.toString(selectedQuantity));
                            inventoryTextView.setText(Integer.toString(numberOfInventory));

                            // Set the updated Sum
                            sum.setText(Double.toString(result) + " €");

                        }
                    }

                });

                //Listener for the Button: - (The selected item will be remove from the order)
                minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //Get the quantity and the inventory
                        int selectedQuantity = Integer.parseInt((String) quantityTextField.getText());
                        int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());

                        // request if there is an Item in the Order
                        if (selectedQuantity > 0) {

                            selectedQuantity--;
                            numberOfInventory++;

                            // Update the sumTextView and set the TextView Sum
                            double result = UpdateSum(false, (String)nameTextView.getText());
                            sum.setText(Double.toString(result) + " €");
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
                new CreatNewOrder().execute();
            }
        });

        return v;
    }

    //update the sumTextView in the Fragment
    private double UpdateSum(boolean isAdd, String itemName){

        double result = 0;

        //summation
        if (isAdd){

                // Search in all items, to get the retailprice
                for (int i = 0; i < MainActivity.allItems.size(); i++) {

                    // Search if one item from all items got the same name
                    if (MainActivity.allItems.get(i).getName().equalsIgnoreCase(itemName)) {

                        storeOfSum = storeOfSum + (MainActivity.allItems.get(i).getRetailprice());
                        storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                        // add the Item to the Order
                        MainActivity.orderItemIDs.add(MainActivity.allItems.get(i).getItemID());

                        break;
                    }

                }

                result = storeOfSum;

        //subtract
        } else {

            // Search in all items, to get the retailprice
            for (int i = 0; i < MainActivity.allItems.size(); i++) {

                // Search if one item from all items got the same name
                if (MainActivity.allItems.get(i).getName().equalsIgnoreCase(itemName)) {

                    storeOfSum = storeOfSum - (MainActivity.allItems.get(i).getRetailprice());
                    storeOfSum = (double) ((int) storeOfSum + (Math.round(Math.pow(10, 3) * (storeOfSum - (int) storeOfSum))) / (Math.pow(10, 3)));

                    // Search in the Order for the item
                    for(Integer integer: MainActivity.orderItemIDs){

                        // if there is the Item, it will be delete from this Order
                        if(integer == MainActivity.allItems.get(i).getItemID()){

                            MainActivity.orderItemIDs.remove(integer);
                            break;

                        }

                    }

                    break;

                }

            }

            result = storeOfSum;

        }




        return result;

    }

    private static class CreatNewOrder extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {

        RestTemplate restTemplate = new RestTemplate();

        try {
            String itemIDs = Order.joinIntIDsIntoString(MainActivity.orderItemIDs);
            System.out.println(itemIDs);
            int tableID = MainActivity.selectedTable.getTableID();
            System.out.println(tableID);
            double price = 2.59;
            System.out.println(price);

            Order order = new Order("1;2;2;3;", 1, 1.5, false);

            //Order übertragen
            // TODO Das Datum kann nicht übertragen werden. Wird momentan auf controllerseite erzeugt.
            restTemplate.postForLocation(url + "/order/", order, Order.class);

        } catch (HttpClientErrorException e){
            String message = getMessage(e.getResponseBodyAsString());
            System.out.println("-----------------------------------------\n"+message);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

    /**
     * Extrahiert die Fehlermessage aus dem Body der JSON-Rückmeldung
     * @param body body der Rückmeldung des RestApiContorllers in JSON
     * @return extrahierte Fehlermessage des RestApiControllers
     */
    private static String getMessage(String body) {
        int lastindex = body.lastIndexOf("message");
        char [] charArray = body.toCharArray();
        int index = lastindex + 10;
        char newChar = charArray[index];
        StringBuilder message = new StringBuilder();
        while(!String.valueOf(newChar).equals("\"")) {
            message.append(newChar);
            newChar = charArray[++index];
        }
        return message.toString();
    }

}