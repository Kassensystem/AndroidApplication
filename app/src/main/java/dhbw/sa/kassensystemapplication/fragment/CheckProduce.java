package dhbw.sa.kassensystemapplication.fragment;


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

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;
import dhbw.sa.kassensystemapplication.entity.Table;

import static dhbw.sa.kassensystemapplication.MainActivity.url;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckProduce extends Fragment {

    public static String text = null;
    private Button confirmProduced;
    private int sizeOfRelativeLayout = 0;
    private String comment;

    public CheckProduce() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_check_produce, container, false);

        confirmProduced = v.findViewById(R.id.confirmTheProduce);

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
                    comment = "";
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

        }

        confirmProduced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new UpdateOrder().execute();
                showTableFragment();

            }
        });
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
            }catch (Exception e){
                text = "undefinierter Fehler";
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showToast(text);

        }


    }

    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_LONG).show();
            this.text = null;
        }


    }

    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelection fragment = new TableSelection();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

}
