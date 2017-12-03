package dhbw.sa.kassensystemapplication.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;

public class TableSelection extends Fragment {

    // Nodes
    private Button confirmTV;
    private Spinner spinnerTV;
    public String text;

    // Variables
    private String tableName = null;

    public TableSelection() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the Layout and set it for use
        View v = inflater.inflate(R.layout.fragment_table_selection, container, false);

        // Get all the information form the Server. Also refresh the information every Order
        new GetAllTables().execute();
        new GetAllItems().execute();
        new GetAllOrders().execute();

        // Initialize the Nodes
        confirmTV = v.findViewById(R.id.confirmTV);
        spinnerTV = v.findViewById(R.id.spinnerTV);

        // Find the chosen Table
        spinnerTV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object object = adapterView.getItemAtPosition(i);
                tableName = object.toString();

                isOrderPaidAtTheSelectedTable(tableName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Open the new Fragment
        confirmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the next Fragment
                if (tableName == null) {

                    showToast("Bitte überprügen Sie die IP Adresse. Es kann keine Verbindung hergestellt werden");

                } else if (tableName != "Bitte wählen:") {

                    ItemSelect fragment = new ItemSelect();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "fragment1");
                    fragmentTransaction.commit();

                }  else {

                    showToast("Bitte wählen Sie einen Tisch aus");

                }

            }
        });


        return v;

    }

    private class GetAllTables extends AsyncTask<Void,Void,ArrayList<Table>> {

        @Override
        protected ArrayList<Table> doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            try {

                ResponseEntity<ArrayList<Table>> responseEntity =
                        restTemplate.exchange
                                (MainActivity.url + "/tables", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Table>>() {
                                        });
                ArrayList<Table> tables = responseEntity.getBody();

                MainActivity.allTables = tables;

                text = null;
                return tables;

            } catch (HttpClientErrorException e){
                text = getMessage(e.getResponseBodyAsString());
                 return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgenaut werden";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten";
                return null;
            }
        }
        @Override
        protected void onPostExecute( ArrayList<Table> tables) {
            super.onPostExecute(tables);
            if(text != null){
                showToast(text);
            } else {

                for (int j = 0; j < MainActivity.allTables.size(); j++) {

                    if (MainActivity.allTables.get(j).isAvailable() == false) {

                        MainActivity.allTables.remove(MainActivity.allTables.get(j));

                    }
                    if (MainActivity.allTables.get(j).isAvailable() == false) {

                        MainActivity.allTables.remove(MainActivity.allTables.get(j));

                    }
                    if (MainActivity.allTables.get(j).isAvailable() == false) {

                        MainActivity.allTables.remove(MainActivity.allTables.get(j));

                    }

                }
                String[] textOfSpinner = new String[MainActivity.allTables.size() + 1];
                if (tables != null) {

                    textOfSpinner[0] = "Bitte wählen:";
                    for (int i = 0; i < tables.size(); i++) {

                        textOfSpinner[i + 1] = tables.get(i).getName();

                    }

                }

                spinnerTV = (Spinner) getActivity().findViewById(R.id.spinnerTV);
                confirmTV = (Button) getActivity().findViewById(R.id.confirmTV);


                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, textOfSpinner);
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
                                ( MainActivity.url + "/items/", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Item>>() {});

                MainActivity.allItems = responseEntity.getBody();
                text = null;

                return MainActivity.allItems;

            } catch (HttpClientErrorException e){
                text = getMessage(e.getResponseBodyAsString());
                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgenaut werden";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten";
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);

            if(text != null){
                showToast(text);
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
                                (MainActivity.url + "/orders", HttpMethod.GET,
                                        null, new ParameterizedTypeReference<ArrayList<Order>>() {});
                ArrayList<Order> orders = responseEntity.getBody();
                text = null;

                return orders;

            } catch (HttpClientErrorException e){
                text = getMessage(e.getResponseBodyAsString());
                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgenaut werden";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten";
                return null;
            }
        }

        protected void onPostExecute( ArrayList<Order> orders) {
            super.onPostExecute(orders);

            if(text != null){
                showToast(text);
            }

            if (orders != null) {
                MainActivity.allOrders = orders;
            }

            /*
         *  TODO Überprüfen, ob mit diesem Tisch eine nicht-bezahlte Order existiert
         *  TODO Wenn ja, den Inhalt dieser Order darstellen (entsprechend die dargestellten Inhalte anpassen)
         */
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

    private void isOrderPaidAtTheSelectedTable(String tableName){

        // Request if there is a table selected
        if(tableName != "Bitte wählen:") {

            // adjustment of all Tables and the selected Table
            for (Table t : MainActivity.allTables) {

                // get the selected Table
                if (t.getName() == tableName) {

                    MainActivity.selectedTable = t;

                    // is there a bill with the selected Table, which is not paid
                    for (Order o : MainActivity.allOrders) {

                        // save the orderItems
                        if (!o.isPaid() && t.getTableID() == o.getTable()) {

                            MainActivity.orderItemIDs = Order.splitItemIDString(o.getItems());
                            MainActivity.selectedOrderID = o.getOrderID();

                        }

                    }

                }

            }
        }
    }

    private void showToast(String text){

        //Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
        System.out.println(text);
    }

}
