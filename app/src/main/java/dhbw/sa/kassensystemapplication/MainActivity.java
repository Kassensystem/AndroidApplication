package dhbw.sa.kassensystemapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;


    public static String ip = null;
    public static String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared = getSharedPreferences("URLinfo", Context.MODE_PRIVATE);
        ip = shared.getString("ip","");

        url = ip+":8080/api";

        // Creat the Navigation Draw
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open ,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //End of Navigation Draw

        try {
            new GetAllItems().execute();
            new GetAllTables().execute();
            new GetAllOrders().execute();
        } catch (Exception e) {
            Toast.makeText(this, "Verbindung fehlgeschlagen.\n Bitte überprügen Sie ihre URL", Toast.LENGTH_LONG).show();
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


            }catch (HttpClientErrorException e){
                String message = getMessage(e.getResponseBodyAsString());
                showToast(message);
                return null;
            } catch(ResourceAccessException e) {
                showToast("Keine Verbindung möglich! Bitte die URL überprüfen.");
                return null;
            }
        }
        @Override
        protected void onPostExecute( ArrayList<Table> tables) {
            super.onPostExecute(tables);
            if (tables != null) {
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

            }catch (HttpClientErrorException e){
                String message = getMessage(e.getResponseBodyAsString());
                showToast(message);
                return null;
            }catch(ResourceAccessException e) {
                showToast("Keine Verbindung möglich! Bitte die URL überprüfen.");
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

            }catch (HttpClientErrorException e){
                String message = getMessage(e.getResponseBodyAsString());
                showToast(message);
                return null;
            }catch(ResourceAccessException e) {
                showToast("Keine Verbindung möglich! Bitte die URL überprüfen.");
                return null;
            }
        }

        protected void onPostExecute( ArrayList<Order> orders) {
            super.onPostExecute(orders);

            if (orders != null) {
                //allOrders = orders;
            }
            //allOrders.add(new Order(1,"2;3;2;",1,2.50, DateTime.now(),false));

            /*
         *  TODO Überprüfen, ob mit diesem Tisch eine nicht-bezahlte Order existiert
         *  TODO Wenn ja, den Inhalt dieser Order darstellen (entsprechend die dargestellten Inhalte anpassen)
         */
        }
    }

    public void showToast(String handoverText){
        //Toast.makeText(this, handoverText, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_URL) {

            Intent intent = new Intent(MainActivity.this, URLInput.class);

            startActivity(intent);

        } else if (id == R.id.nav_Login){

            //TODO: LOGIN View einbauen
            showToast("Hier muss dann die LOGIN Activity noch erscheinen!");

        }else if (id == R.id.nav_CR) {

            showToast("Marvin Mai\nDaniel Schifano");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
