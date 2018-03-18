package dhbw.sa.kassensystemapplication.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;
import dhbw.sa.kassensystemapplication.entity.Table;


/**
 * In dieser Klasse wird der Startbildschirm der Applikation erstellt.
 * Dieser wird ebenfalls aufgerufen, wenn angefangen wird eine Bestellung aufzugeben.
 * @author Daniel Schifano
 */


public class TableSelectionFragment extends Fragment {

    // Nodes
    private Button confirmTV;
    private Spinner spinnerTV;
    /**
     * Speichert die Fehlermeldung des Servers.
     */
    public String text;

    // Variables
    private String tableName = null;

    public TableSelectionFragment() {

    }

    /**
     *
     * Diese Methode wird aufgerufen wenn das Fragment erstellt wird. Dabei wird der Befehl gegeben, dass alle Artikel, Tische und Bestellungen vom Server angefordert.
     * Für "Bestellung aufgeben" wird hier der Startbildschirm initialisiert.
     *
     * @param inflater Instantiiert ein XML-Layout in ein passendes View Objekt
     * @param container Erlaubt den Zugriff auf container Eigenschaften
     * @param savedInstanceState Gibt an in welchem Abschnitt des Lebenszyklus die App sich befindet. Ob sie z.B. geschlossen wurde oder gestartet wurde.
     * @return View die dargestellt werden soll
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity.allunproducedItems.clear();
        MainActivity.orderedItems.clear();
        PayOrderFragment.namesFromItems.clear();

        // Get the Layout and set it for use
        View v = inflater.inflate(R.layout.fragment_table_selection, container, false);

        // Get all the information form the Server. Also refresh the information every Order
        new GetAllItems().execute();
        new GetAllOrders().execute();
        new GetAllUnproducedItems().execute();
        new GetAllTables().execute();



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

                //TODO: Hier wenn nichts ausgewählt ist soll

            }
        });

        // Open the new Fragment
        confirmTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the next Fragment
                if (tableName == null) {

                    showToast("Bitte überprüfen Sie die IP-Adresse. Es kann keine Verbindung hergestellt werden");

                } else if (tableName != "") {

                    if(isOrderPaidAtTheSelectedTable(tableName)){
                        MainActivity.orderIsPaid = true;
                        new CreatNewOrder().execute();
                    } else {
                        MainActivity.orderIsPaid = false;
                        new GetOrderedItems().execute();
                    }

                }  else {

                    showToast("Bitte wählen Sie einen Tisch aus");

                }

            }
        });

        return v;

    }
    /**
     * private
     * Diese Klasse wird dafür verwendet, alle Tische die derzeit in der Datenbank als verfügbar angelegt sind in die Applikation zu laden.
     */
    private class GetAllTables extends AsyncTask<Void,Void,ArrayList<Table>> {

        /**
         * Diese Methode wird dafür verwendet alle Tische der Datenbank zu erhalten. Die Tische werden in der MainActivity gepseichert.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet werden sollen.
         * @return Wenn die Kommunikation ohne Fehler gelungen ist, werden alle Tische die in der Datenbank hinterlegt sind zurückgegeben. Falls ein Fehler auftritt wird null zurückgegeben.
         */
        @Override
        protected ArrayList<Table> doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            try {

                ResponseEntity<ArrayList<Table>> responseEntity =
                        restTemplate.exchange
                                (MainActivity.url + "/tables", HttpMethod.GET,
                                        Entity.getEntity(null), new ParameterizedTypeReference<ArrayList<Table>>() {
                                        });
                ArrayList<Table> tables = responseEntity.getBody();
                System.out.println(responseEntity.getStatusCode());

                MainActivity.allTables = tables;

                text = null;
                for(int i = 0;i<tables.size();i++)
                System.out.println("Table ID: "+tables.get(i).getTableID() + " Table Name: "+ tables.get(i).getName()+" is the Table Avialable: "+tables.get(i).isAvailable());
                return tables;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();

                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }

                e.printStackTrace();
                 return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten Table";
                e.printStackTrace();
                return null;
            }
        }

        /**
         * In dieser Methode werden alle verfügbaren Tische die in der Datenbank hinterlegt sind in das Dropdown Menü gespeichert
         * Falls ein Fehler in der Übermittlung der Daten vom Server auftritt, wird dieser mit Hilfe der Methode showToast dargestellt.
         *
         * @param tables Alle Tische die von der Datenbank übermittelt wurden.
         */
        @Override
        protected void onPostExecute( ArrayList<Table> tables) {
            super.onPostExecute(tables);
            if(text != null){
                showToast(text);
                text = null;
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

                    textOfSpinner[0] = "";
                    for (int i = 0; i < tables.size(); i++) {

                        textOfSpinner[i + 1] = tables.get(i).getName();

                    }
                    if(textOfSpinner.length<2){
                        showToast("Es befinden sich keine Daten in der Datenbank.");
                    }
                }

                spinnerTV = (Spinner) getActivity().findViewById(R.id.spinnerTV);
                confirmTV = (Button) getActivity().findViewById(R.id.confirmTV);


                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, textOfSpinner);
                spinnerTV.setAdapter(spinnerAdapter);
            }
        }

    }
    /**
     * Diese Klasse wird dafür verwendet, alle Artikel die derzeit in der Datenbank als verfügbar angelegt sind in die Applikation zu laden.
     */
    private class GetAllItems extends AsyncTask<Void, Void, ArrayList<Item>>{

        /**
         * Diese Methode wird dafür verwendet alle Artikel der Datenbank zu erhalten. Die Artikel werden in der MainActivity gepseichert.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet werden sollen.
         * @return Wenn die Kommunikation ohne Fehler gelungen ist, werden alle Artikel die in der Datenbank hinterlegt sind zurückgegeben. Falls ein Fehler auftritt wird null zurückgegeben.
         */
        @Override
        protected ArrayList<Item> doInBackground(Void... params){

            RestTemplate restTemplate = new RestTemplate();

            try {
                ResponseEntity<ArrayList<Item>> responseEntity =
                        restTemplate.exchange
                                (MainActivity.url + "/items", HttpMethod.GET,
                                        Entity.getEntity(null), new ParameterizedTypeReference<ArrayList<Item>>() {
                                        });

                MainActivity.allItems = responseEntity.getBody();
                text = null;

                return MainActivity.allItems;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();

                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }

                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten Item";
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Falls bei der Kommunikation mit dem Server ein Fehler auftritt, wird mithilfe der ShowToast-Methode dieser Fehler dargestellt.
         *
         * @param items Wird hier nicht benötigt.
         */
        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            super.onPostExecute(items);

            if(text != null){
                showToast(text);
                text = null;
            }

        }
    }
    /**
     * Diese Klasse wird dafür verwendet, alle Bestellungen die derzeit in der Datenbank angelegt sind in die Applikation zu laden.
     */
    private class GetAllOrders extends AsyncTask<Void,Void,ArrayList<Order>> {

        /**
         * Diese Methode wird dafür verwendet alle Bestellungen der Datenbank zu erhalten. Die Bestellungen werden in der MainActivity gepseichert.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet werden sollen.
         * @return null, alle Informationen die vom Server übermittelt wurden werden in der MainActivity gespeichert.
         */
        @Override
        protected ArrayList<Order> doInBackground(Void... params) {
            try {
                RestTemplate restTemplate = new RestTemplate();


                ResponseEntity<ArrayList<Order>> responseEntity =
                        restTemplate.exchange
                                (MainActivity.url + "/orders", HttpMethod.GET,
                                        Entity.getEntity(null), new ParameterizedTypeReference<ArrayList<Order>>() {
                                        });
                MainActivity.allOrders = responseEntity.getBody();

                text = null;
                return null;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }
                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten Order";
                e.printStackTrace();
                return null;
            }
        }

        /**
         *  Falls bei der Kommunikation mit dem Server ein Fehler auftritt, wird mithilfe der ShowToast-Methode dieser Fehler dargestellt.
         *  Speichert die Bestellungen in der MainActivity.
         * @param orders alle Bestellungen die in der Datenbank hinterlegt sind
         */
        protected void onPostExecute( ArrayList<Order> orders) {
            super.onPostExecute(orders);

            if(text != null){
                showToast(text);
                text = null;
            }

            if (orders != null) {
                MainActivity.allOrders = orders;
            }

        }
    }

    private class CreatNewOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();


            try {

                Order order = new Order(MainActivity.selectedTable.getTableID());

                ResponseEntity<Integer> responseEntity = restTemplate.exchange
                    (MainActivity.url + "/order", HttpMethod.POST,
                        Entity.getEntity(order),Integer.class );

                Integer orderId = responseEntity.getBody();
                MainActivity.selectedOrderID = orderId;


            } catch (HttpClientErrorException e){

                text = e.getResponseBodyAsString();
                e.printStackTrace();
                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }
                return null;
            }catch (Exception e){

                text = "undefinierter Fehler";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (text == null){
                showItemFragment();
            } else {
                showToast(text);
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
                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }
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

            if (orderedItems != null) {
                MainActivity.orderedItems = orderedItems;
            }

            if(text != null){
                showToast(text);
                text = null;
            } else {
                showItemFragment();
            }


        }
    }

    private class GetAllUnproducedItems extends AsyncTask<Void,Void,ArrayList<OrderedItem>> {

        @Override
        protected ArrayList<OrderedItem> doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            try {

                ResponseEntity<ArrayList<OrderedItem>> responseEntity =
                        restTemplate.exchange
                                (MainActivity.url + "/unproducedOrderedItems", HttpMethod.GET,
                                        Entity.getEntity(null), new ParameterizedTypeReference<ArrayList<OrderedItem>>() {
                                        });

                ArrayList<OrderedItem> orderedItems = responseEntity.getBody();
                MainActivity.allunproducedItems = orderedItems;

                text = null;
                return orderedItems;

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                if(text.indexOf("Login")!= -1){
                    text = "Der Login ist Fehlgeschlagen.\nBitte melden Sie sich mit Ihren " +
                            "Zugangsdaten an";
                }
                return null;
            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.";
                return null;
            }
            catch (Exception e){
                text ="Ein Unbekannter Fehler ist aufgetreten Table";
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute( ArrayList<OrderedItem> orderedItems) {
            super.onPostExecute(orderedItems);

            if(text != null){
                showToast(text);
                text = null;
            }


        }

    }

    /**
     * Überprüft ob eine noch nicht bezahlte Rechnung an dem übergebenen Tisch existiert.
     * Falls eine nicht bezahlte Bestellung existiert, werden die Artikel der Bestellung in der MainActivity gespeichert.
     * @param tableName Übergibt den Tisch-Name der überprüft werden soll.
     */
    private boolean isOrderPaidAtTheSelectedTable(String tableName){

        // Request if there is a table selected
        if(tableName != "") {

            // adjustment of all Tables and the selected Table
            for (Table t : MainActivity.allTables) {

                // get the selected Table
                if (t.getName() == tableName) {

                    MainActivity.selectedTable = t;

                    // is there a bill with the selected Table, which is not paid
                    for (Order o : MainActivity.allOrders) {

                        // save the orderItems
                        if (!o.isPaid() && t.getTableID() == o.getTable()) {

                            MainActivity.selectedOrderID = o.getOrderID();
                            return false;

                        }

                    }

                }

            }

        }
        return true;
    }
    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param text Der Text welcher dargestellt werden soll.
     */
    private void showToast(String text){
        if(text != null) {
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
            System.out.println(text);
        }
    }

    private void showItemFragment(){
        ItemSelectFragment fragment = new ItemSelectFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "fragment1");
        fragmentTransaction.commit();
    }




}
