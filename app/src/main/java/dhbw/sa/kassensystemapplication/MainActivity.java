package dhbw.sa.kassensystemapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;

public class MainActivity extends AppCompatActivity {

    //private String[] textOfSpinner;
    private Spinner spinnerTV;
    private Button confirmTV;

    private ArrayList<Table> allTables = new ArrayList<>();
    public static ArrayList<Item> allItems = new ArrayList<>();
    private ArrayList<Order> allOrders = new ArrayList<>();
    private Table table;
    private int tableID = 0;
    public static ArrayList<Integer> itemsOfOrder = new ArrayList<>();
    private String itemsOfOrderAsString;
    private String tableName;


    public static String url = "http://192.168.178.22:8080/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            new GetAllItems().execute();
            new GetAllTables().execute();
            new GetAllOrders().execute();
        } catch (Exception e) {
            Toast.makeText(this, "Verbindung fehlgeschlagen.\n Bitte überprügen Sie ihren URL", Toast.LENGTH_LONG).show();
        }


        spinnerTV = (Spinner)findViewById(R.id.spinnerTV);

        confirmTV = (Button)findViewById(R.id.confirmTV);
        confirmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tableName == "Bitte wählen:"){

                    showToast("Bitte wählen Sie einen Tisch bevor Sie fortfahren");

                } else {

                    Intent intent = new Intent(MainActivity.this, ItemSelection.class);

                    intent.putExtra("tableID", String.valueOf(tableID));

                    startActivity(intent);

                }

            }
        });

        spinnerTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                tableName = item.toString();
                if (tableName != "Bitte wählen:") {
                    for (Table t : allTables) {
                        if (t.getName() == tableName) {
                            table = t;
                            System.out.println("TISCH AUSGEWÄHLT: " + t.getName()); //debug

                            for (Order o : allOrders) {

                                System.out.println(o.getTable());

                                if (!o.isPaid() && t.getTableID() == o.getTable()) {

                                    ItemSelection.updatableOrderID = o.getOrderID();
                                    itemsOfOrder = Order.splitItemIDString(o.getItems());
                                    System.out.println("-----------------------------------------------");

                                    for (int j = 0; j < itemsOfOrder.size(); j++) {
                                        System.out.println(itemsOfOrder.get(j));
                                    }

                                }
                            }
                        }
                    }
                    tableID = table.getTableID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private class GetAllTables extends AsyncTask<Void,Void,ArrayList<Table>> {

        @Override
        protected ArrayList<Table> doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                ResponseEntity<ArrayList<Table>> responseEntity =
                        restTemplate.exchange
                                (url + "/tables", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Table>>() {});

                ArrayList<Table> tables = responseEntity.getBody();

                allTables = tables;

                return tables;


            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute( ArrayList<Table> tables) {
            super.onPostExecute(tables);
            String [] textOfSpinner = new String[tables.size()+1];
            textOfSpinner[0] = "Bitte wählen:";
            for(int i=0; i < tables.size(); i++){

                if(tables.get(i).isAvailable()) {
                    textOfSpinner[i+1] = tables.get(i).getName();
                }

            }

            spinnerTV = (Spinner) findViewById(R.id.spinnerTV);
            confirmTV = (Button) findViewById(R.id.confirmTV);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, textOfSpinner);
            spinnerTV.setAdapter(spinnerAdapter);

        }
    }

    private class GetAllItems extends AsyncTask<Void, Void, ArrayList<Item>>{

        @Override
        protected ArrayList<Item> doInBackground(Void... params){

            RestTemplate restTemplate = new RestTemplate();


            try {
                ResponseEntity<ArrayList<Item>> responseEntity =
                        restTemplate.exchange
                                ( url + "/items/", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Item>>() {});

                allItems = responseEntity.getBody();


                return allItems;

            }catch (Exception e) {
                return null;
            }

        }

    }

    private class GetAllOrders extends AsyncTask<Void,Void,ArrayList<Order>> {

        @Override
        protected ArrayList<Order> doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity<ArrayList<Order>> responseEntity =
                        restTemplate.exchange
                                (url + "/orders", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Order>>() {});
                ArrayList<Order> orders = responseEntity.getBody();

                return orders;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute( ArrayList<Order> orders) {
            super.onPostExecute(orders);

            allOrders = orders;
            //allOrders.add(new Order(1,"2;3;2;",1,2.50, DateTime.now(),false));

            /*
         *  TODO Überprüfen, ob mit diesem Tisch eine nicht-bezahlte Order existiert
         *  TODO Wenn ja, den Inhalt dieser Order darstellen (entsprechend die dargestellten Inhalte anpassen)
         */
        }
    }

    public void showToast(String handoverText){

        Toast.makeText(this, handoverText, Toast.LENGTH_LONG).show();

    }


}
