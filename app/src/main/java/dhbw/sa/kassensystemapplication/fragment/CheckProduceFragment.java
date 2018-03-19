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
 * A simple {@link Fragment} subclass.
 */
public class CheckProduceFragment extends Fragment {

    public static String text = null;
    private Button confirmProduced;
    private TextView failurIfNoUnproducedItem;
    private int sizeOfRelativeLayout = 0;
    private String comment;

    public CheckProduceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_check_produce, container, false);

        confirmProduced = v.findViewById(R.id.confirmTheProduce);
        failurIfNoUnproducedItem = v.findViewById(R.id.failurIfNoUnproducedItem);

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10, this.getResources().getDisplayMetrics());
        float posY = pix;

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

                    // declaration of the TextView for the Items and the Buttons (+ and -)
                    final TextView commentTextView = new TextView(getActivity());
                    final CheckBox checkProduce = new CheckBox(getActivity());

                    // Params for the TextView quantityTextField
                    Table tableOfOrderedItem = findTableToOrder(orderedItem.getOrderID());

                    checkProduce.setText(name+"\n"+tableOfOrderedItem.getName());
                    checkProduce.setX(pix/10);
                    checkProduce.setY(posY);
                    rl.addView(checkProduce);

                    if(orderedItem.getComment() == null){
                        comment = "--";
                    } else {
                        comment = orderedItem.getComment();
                    }

                    commentTextView.setLayoutParams(new LinearLayout.LayoutParams(8 * pix, 10 * pix));
                    commentTextView.setX(13 * pix);
                    commentTextView.setY(posY);
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
                showToast("Ein oder mehrere Artikel können nicht richtig dargestellt werden.\n" +
                        "Bitte Überprüfen Sie an der Manager-Applikation Ihre Bestellungen.");
            }

        }

        confirmProduced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new UpdateOrder().execute();

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

    private class UpdateOrder extends AsyncTask<Void, Void, Void> {

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

    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
            this.text = null;
        }


    }

    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelectionFragment fragment = new TableSelectionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

}
