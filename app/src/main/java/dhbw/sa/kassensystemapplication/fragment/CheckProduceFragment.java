package dhbw.sa.kassensystemapplication.fragment;


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
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;
import dhbw.sa.kassensystemapplication.entity.Table;

/**
 * In dieser Klasse wird der Bestellungsannahme-Bildschirm der Applikation erstellt.
 *
 * @author Daniel Schifano
 */
public class CheckProduceFragment extends Fragment {

    /**
     * Nodes, in denen die Informationen für den Anwendern dargestellt werden, beziehungsweise die
     * sie verwenden können.
     */
    private Button confirmProduced;
    private TextView failurIfNoUnproducedItem;
    /**
     * Variablen, die zu "Berechnungen" innerhalb der Java-Klasse verwendet werden.
     */
    public static String text = null;
    private int sizeOfRelativeLayout = 0;
    private String comment;
    boolean checked;
    /**
     * Gibt an, wie lang der String maximal sein darf, bevor eine neue Zeile angefangen werden muss.
     */
    private int lengthOfStringTillSplit1 = 17;
    /**
     * Gibt an, wie lang der String maximal sein darf, bevor eine dritte Zeile angefangen werden muss.
     */
    private int lengthOfStringTillSplit2 = 2*lengthOfStringTillSplit1;
    /**
     * Der Konstruktor, der zum aufrufen dieser Klasse benötigt wird.
     * Er benötigt keine Übergabe Parameter.
     * Damit wird der neue Bildschirm initalisiert und kann auf dem Smartphone angezeigt werden.
     */
    public CheckProduceFragment() {
        // Required empty public constructor
    }
    /**
     * Diese Methode wird aufgerufen wenn das Fragment erstellt wird. Dabei werden alle Nodes
     * initialisiert.
     * Wenn Artikel als "angenommen" markiert sind, dann werden Sie an den Server geschickt und in
     * der Datenbank gespeichert.
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
        View v = inflater.inflate(R.layout.fragment_check_produce, container, false);

        confirmProduced = v.findViewById(R.id.confirmTheProduce);
        failurIfNoUnproducedItem = v.findViewById(R.id.failurIfNoUnproducedItem);

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        float posY = pix;
        checked = false;

        // declare the relative Layout. There the Nodes for the Order get added.
        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rlCheckProduce);
        ViewGroup.LayoutParams params = rl.getLayoutParams();

            for(OrderedItem orderedItem: MainActivity.allunproducedItems){

                sizeOfRelativeLayout++;
            }

            params.height = sizeOfRelativeLayout*5*pix;

            for(final OrderedItem orderedItem: MainActivity.allunproducedItems) {

                try {
                    if (!orderedItem.isItemProduced()) {

                        String name = null;

                        //get the name from the orderedItem
                        for (Item item: MainActivity.allItems){
                            if (item.getItemID() == orderedItem.getItemID()){
                                name = item.getName();
                                break;
                            }
                        }
                        if(name.length() > lengthOfStringTillSplit1){
                            name = name.substring(0,lengthOfStringTillSplit1) + "-\n" + name.substring(lengthOfStringTillSplit1);
                            if (name.length() > lengthOfStringTillSplit2){
                                name = name.substring(0,lengthOfStringTillSplit1) + "-\n" + name.substring(lengthOfStringTillSplit1,lengthOfStringTillSplit2) +"-\n" + name.substring(lengthOfStringTillSplit1);
                            }
                        }

                        // declaration of the TextView for the Items and the Buttons (+ and -)
                        final TextView commentTextView = new TextView(getActivity());
                        final CheckBox checkProduce = new CheckBox(getActivity());

                        // Params for the TextView quantityTextField
                        Table tableOfOrderedItem = findTableToOrder(orderedItem.getOrderID());

                        checkProduce.setText(name+" ("+tableOfOrderedItem.getName()+")");
                        checkProduce.setX(pix/10);
                        checkProduce.setY(posY);
                        rl.addView(checkProduce);

                        if(orderedItem.getComment() == null){
                            comment = "--";
                        } else {
                            comment = orderedItem.getComment();
                        }


                        commentTextView.setLayoutParams(new LinearLayout.LayoutParams(15 * pix, 10 * pix));
                        commentTextView.setX((float) (14.5 * pix));
                        commentTextView.setY(posY-(pix/2));
                        commentTextView.setText(comment);
                        commentTextView.setPadding(pix, pix, pix, pix);
                        rl.addView(commentTextView);

                        posY = posY +5*pix;

                        checkProduce.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (checkProduce.isChecked()){

                                    orderedItem.setItemIsProduced(true);

                                } else {

                                    orderedItem.setItemIsProduced(false);

                                }

                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            confirmProduced.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!checked){
                        new UpdateOrder().execute();
                        checked = true;
                    }
                }
            });

            if(MainActivity.allunproducedItems.size() == 0){
                failurIfNoUnproducedItem.setTextColor(Color.RED);
                failurIfNoUnproducedItem.setText("Es sind keine Artikel\nabholbereit.");
            } else {
                failurIfNoUnproducedItem.setText("");
            }

        return v;
    }
    /**
     * Mit dieser Methode wird der Tisch zu einer Bestellung herausgesucht.
     * Diese Methode wird immer dann aufgerufen, wenn ein Artikel auf dem Bildschirm dargestellt
     * werden soll. Der Tischname befindet sich immer in Klammern hinter dem Artikelname.
     *
     * @param orderID Die Bestellung, von der der Tisch gesucht wird.
     * @return Table, wenn ein Tisch zu der orderID gefunden wurde.
     *         null, wenn kein Tisch zu der orderID gefunden wurde. Dies ist vorallem dann der Fall,
     *         wenn in der Datenbank die orderID nicht mehr vorhanden ist.
     */
    private Table findTableToOrder(int orderID) {

        System.out.println("----------\nOrder-ID: " + orderID);

        int tableID = 0;
        Order debugOrder = null;
        Table debugTable = null;

        for (Order order: MainActivity.allOrders){
            debugOrder = order;
            if(order.getOrderID() == orderID){
                tableID = order.getTable();

                System.out.println("\tTable-ID: " + tableID);
                break;
            }
        }

        for (Table table: MainActivity.allTables){
            if (table.getTableID() == tableID){
                debugTable = table;
                System.out.println("\tTable-Name: " + table.getName());
                return table;
            }

        }
        if(orderID == 52)
            System.out.println("stop");

        System.out.println("\tnix gefunden");
        return null;
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
     * Java-Klasse CheckProduceFragment wird nicht mehr dargestellt.
     */
    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelectionFragment fragment = new TableSelectionFragment();
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
                                Entity.getEntity(MainActivity.allunproducedItems),Integer.class );

                MainActivity.allunproducedItems.clear();

            } catch (HttpClientErrorException e){
                text = e.getResponseBodyAsString();
                e.printStackTrace();
                System.out.println("\n"+text+"\n--------------------------");
                return null;

            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.\nDie Artikel konnten nicht " +
                        "übertragen werden. Bitte verbinden Sie sich mit dem Netzwerk und " +
                        "versuchen Sie es erneut";
                return null;
            }catch (Exception e){
                text = "undefinierter Fehler";
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

            if(text == null){
                showTableFragment();
            } else {
                showToast(text);
            }

        }
    }
}
