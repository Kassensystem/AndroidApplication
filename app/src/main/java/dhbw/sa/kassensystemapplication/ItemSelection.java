package dhbw.sa.kassensystemapplication;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.joda.time.DateTime;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;

import static dhbw.sa.kassensystemapplication.MainActivity.allItems;
import static dhbw.sa.kassensystemapplication.MainActivity.url;

/**
 * TODO Shared Preferences für Speichern von Daten
 *
 * @author Dani Schifano
 * @author Marvin Mai
 */
public class ItemSelection extends AppCompatActivity {

    Display mdisp;
    Point mdispSize;
    private TextView sum;
    private Button orderbtn;
    private Button paidbtn;
    private double speicher;
    private ArrayList<Item> orderItems = new ArrayList<>();
    public static double price = 0;
    public static int tableID = 0;
    public static int updatableOrderID = 0;
    public boolean orderIsPaid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_selection);



        //get table from main
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            tableID = Integer.parseInt(extras.getString("tableID"));


        mdisp = getWindowManager().getDefaultDisplay();
        mdispSize = new Point();
        mdisp.getSize(mdispSize);
        int maxX = mdispSize.x;
        int maxY = mdispSize.y;

        sum = (TextView) findViewById(R.id.sumTV);
        sum.setText("0.0 €");
        orderbtn = (Button) findViewById(R.id.orderBtn);
        paidbtn = (Button) findViewById(R.id.paidBtn);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);

        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        float posY = pix;

        ViewGroup.LayoutParams params = rl.getLayoutParams();
        params.height = MainActivity.allItems.size()*5*pix;

        for(int i =0; i<MainActivity.allItems.size();i++){

            if(MainActivity.allItems.get(i).isAvailable()) {

                String name = MainActivity.allItems.get(i).getName();

                // declaration of the TextView for the Items and the Buttons (+ and -)
                final TextView txt = new TextView(this);
                final TextView quantityTextField = new TextView(this);
                final TextView inventoryTextView = new TextView(this);
                Button plus = new Button(this);
                Button minus = new Button(this);

                // Params for the TextView txt
                txt.setLayoutParams(new LinearLayout.LayoutParams(30 * pix, 10 * pix));
                txt.setText(name);
                txt.setId(-i);
                txt.setX(pix / 10);
                txt.setY(posY);
                txt.setPadding(pix, pix, pix, pix);
                rl.addView(txt);

                inventoryTextView.setLayoutParams(new LinearLayout.LayoutParams(30*pix,10*pix));
                inventoryTextView.setText(Integer.toString(MainActivity.allItems.get(i).getQuantity()));
                inventoryTextView.setX(maxX - (18 * pix));
                inventoryTextView.setY(posY);
                inventoryTextView.setPadding(pix,pix,pix,pix);
                rl.addView(inventoryTextView);

                // Params for the TextView quantityTextField
                quantityTextField.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                quantityTextField.setId(i);
                quantityTextField.setText("0");
                quantityTextField.setX(maxX - (15 * pix));
                quantityTextField.setY(posY);
                quantityTextField.setPadding(pix, pix, pix, pix);
                rl.addView(quantityTextField);

                for (Integer itemID: MainActivity.itemsOfOrder){

                    if(itemID == MainActivity.allItems.get(i).getItemID()){

                        int number = Integer.parseInt((String)quantityTextField.getText());
                        number++;

                        int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());
                        numberOfInventory--;
                        inventoryTextView.setText(Integer.toString(numberOfInventory));

                        speicher = speicher + MainActivity.allItems.get(i).getRetailprice();
                        speicher = (double) ((int) speicher + (Math.round(Math.pow(10, 3) * (speicher - (int) speicher))) / (Math.pow(10, 3)));

                        quantityTextField.setText(Integer.toString(number));
                        sum.setText(Double.toString(speicher) + " €");


                    }

                }

                // Params for the Button: +
                plus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                plus.setText("+");
                plus.setX(maxX - 11 * pix);
                plus.setPadding(pix, pix, pix, pix);
                plus.setY(posY);
                rl.addView(plus);

                //Params for the Button: -
                minus.setLayoutParams(new LinearLayout.LayoutParams(4 * pix, 4 * pix));
                minus.setText("-");
                minus.setX(maxX - 7 * pix);
                minus.setPadding(pix, pix, pix, pix);
                minus.setY(posY);
                rl.addView(minus);

                //The Y-position need to crow with the quantityTextField of Items!
                posY = posY + 5 * pix;

                //Listener for the Button: +
                plus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if(Integer.parseInt((String)inventoryTextView.getText())>0) {

                            String selectedQuantity = (String) quantityTextField.getText();

                            int number = Integer.parseInt(selectedQuantity);
                            number++;

                            int numberOfInventory = Integer.parseInt((String) inventoryTextView.getText());
                            numberOfInventory--;

                            inventoryTextView.setText(Integer.toString(numberOfInventory));

                            // Search for the right Item, to get the Retailprice
                            for (int i = 0; i < MainActivity.allItems.size(); i++) {

                                if (MainActivity.allItems.get(i).getName().equalsIgnoreCase((String) txt.getText())) { //TODO: Nicht name sondern ID abfragen

                                    speicher = speicher + (MainActivity.allItems.get(i).getRetailprice());
                                    speicher = (double) ((int) speicher + (Math.round(Math.pow(10, 3) * (speicher - (int) speicher))) / (Math.pow(10, 3)));

                                    orderItems.add(MainActivity.allItems.get(i)); //Todo: auch bei minus button

                                    break;
                                }

                            }

                            selectedQuantity = Integer.toString(number);

                            quantityTextField.setText(selectedQuantity);
                            sum.setText(Double.toString(speicher) + " €");
                            //price = Double.parseDouble(sum.getText().toString());
                            price = speicher;
                        }
                    }

                });

                //Listener for the Button: -
                minus.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String numberAsString = (String) quantityTextField.getText();

                        int number = Integer.parseInt(numberAsString);
                        int numberOfInventory = Integer.parseInt((String)inventoryTextView.getText());

                        if (number > 0) {

                            number--;
                            numberOfInventory++;

                            inventoryTextView.setText(Integer.toString(numberOfInventory));

                            // Search for the right Item, to get the Retailprice
                            for (int i = 0; i < MainActivity.allItems.size(); i++) {

                                if (MainActivity.allItems.get(i).getName().equalsIgnoreCase((String) txt.getText())) { //TODO: Nicht name sondern ID abfragen

                                    speicher = speicher - (MainActivity.allItems.get(i).getRetailprice());
                                    //speicher = Math.round(speicher*100)/100.0;
                                    speicher = (double) ((int) speicher + (Math.round(Math.pow(10, 3) * (speicher - (int) speicher))) / (Math.pow(10, 3)));
                                    break;

                                }

                            }
                        }

                        numberAsString = Integer.toString(number);

                        quantityTextField.setText(numberAsString);
                        sum.setText(Double.toString(speicher) + " €");

                    }

                });
            }
        }



        sum.setTextColor(Color.RED);
        sum.setTextSize(pix*2);

        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            // TODO create oder update
            orderIsPaid = false;
            if(updatableOrderID == 0)
                new CreatNewOrder().execute();
            else
                new UpdateOrder().execute();

                showMainActivity();

            }
        });

        paidbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            orderIsPaid = true;
            // TODO create order update
                if(updatableOrderID == 0)
                    new CreatNewOrder().execute();
                else
                    new UpdateOrder().execute();

                showMainActivity();
            }
        });
    }

    private void showMainActivity() {
        Intent intent = new Intent(ItemSelection.this, MainActivity.class);

        startActivity(intent);
    }

    private void refreshDisplayedItems(Order order) {

        ArrayList<Integer> itemIDs = Order.splitItemIDString(order.getItems());
        for(Integer i: itemIDs) {
            // TODO aktualisieren der ausgewählten Items der order
            for(int var= 0; var < allItems.size(); var++ ){
                Item item = allItems.get(var);
                if(item.getItemID()==i){
                    TextView quantityTextView = (TextView) findViewById(var);
                    String numberAsString;
                    numberAsString = (String) quantityTextView.getText();
                    int number = Integer.parseInt(numberAsString);
                    number++;

                    // Search for the right Item, to get the Retailprice
                    for (int var2 = 0; var2 < MainActivity.allItems.size(); var2++) {

                        if (MainActivity.allItems.get(var2).getName().equalsIgnoreCase((String) quantityTextView.getText())) { //TODO: Nicht name sondern ID abfragen

                            speicher = speicher + (MainActivity.allItems.get(var2).getRetailprice());
                            speicher = (double) ((int) speicher + (Math.round(Math.pow(10, 3) * (speicher - (int) speicher))) / (Math.pow(10, 3)));

                            orderItems.add(MainActivity.allItems.get(var2)); //Todo: auch bei minus button

                            break;
                        }

                    }

                }
            }
        }

    }

    private class CreatNewOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                String itemIDs = Order.joinIDsIntoString(orderItems);
                System.out.println(itemIDs);
                int tableID = ItemSelection.tableID;
                System.out.println(tableID);
                double price = ItemSelection.price;
                System.out.println(price);
                DateTime date = DateTime.now();
                System.out.println(date.toString());
                Order order = new Order(itemIDs, tableID, price, orderIsPaid);

                //Order übertragen
                // TODO Das Datum kann nicht übertragen werden. Wird momentan auf controllerseite erzeugt.
                restTemplate.postForLocation(url + "/order/", order, Order.class);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }

    private class UpdateOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                String itemIDs = Order.joinIDsIntoString(orderItems);
                System.out.println(itemIDs);
                int tableID = ItemSelection.tableID;
                System.out.println(tableID);
                double price = ItemSelection.price;
                System.out.println(price);
                DateTime date = DateTime.now();
                System.out.println(date.toString());
                System.out.println("---------------------------------\n" + orderIsPaid);
                Order order = new Order(updatableOrderID, itemIDs, tableID, price, null, orderIsPaid);

                //Order übertragen
                // TODO Das Datum kann nicht übertragen werden. Wird momentan auf controllerseite erzeugt.
                restTemplate.put(url + "/order/" + order.getOrderID(), order);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }

}
