package dhbw.sa.kassensystemapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public static ArrayList<Order> allOrders = new ArrayList<>();
    private Table table;
    private int tableID = 0;

    public static String ip = "http://192.168.178.22:8080/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new GetAllItems().execute();
        new GetAllTables().execute();

        spinnerTV = (Spinner)findViewById(R.id.spinnerTV);

        confirmTV = (Button)findViewById(R.id.confirmTV);
        confirmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ItemSelection.class);

                intent.putExtra("tableID", String.valueOf(tableID));

                startActivity(intent);

            }
        });

        spinnerTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                String tableName = item.toString();
                for(Table t: allTables) {
                    if(t.getName() == tableName) {
                        table = t;
                        System.out.println("TISCH AUSGEWÄHLT: " + t.getName()); //debug
                    }
                }

                tableID = table.getTableID();
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
                                (ip + "/tables", HttpMethod.GET,
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
            String [] textOfSpinner = new String[tables.size()];
            for(int i=0; i < tables.size(); i++){
                textOfSpinner[i] = tables.get(i).getName();
            }

            spinnerTV = (Spinner) findViewById(R.id.spinnerTV);
            confirmTV = (Button) findViewById(R.id.confirmTV);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, textOfSpinner);
            spinnerTV.setAdapter(spinnerAdapter);

        }
    }

    private class GetAllItems extends AsyncTask<Void, Void, ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                ResponseEntity<ArrayList<Item>> responseEntity =
                        restTemplate.exchange
                                ( ip + "/items/", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Item>>() {});

                allItems = responseEntity.getBody();

                return allItems;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
