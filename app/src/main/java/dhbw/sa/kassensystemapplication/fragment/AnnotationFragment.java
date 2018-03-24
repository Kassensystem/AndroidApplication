package dhbw.sa.kassensystemapplication.fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.OrderedItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnotationFragment extends Fragment {

    private Button confirmButton;
    private TextView priceOfItem;

    private Item item;
    private int sizeOfRelativeLayout = 0;
    private String orderedComment;
    private boolean checked;

    @SuppressLint("ValidFragment")
    public AnnotationFragment(Item item){
        this.item = item;
    }

    public AnnotationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_annotation, container, false);

        checked = false;

        confirmButton = v.findViewById(R.id.confirmComment);
        priceOfItem = v.findViewById(R.id.priceOfItem);

        if(item != null){
            priceOfItem.setText(String.valueOf((int) item.getRetailprice())+" €/pro Artikel");
            priceOfItem.setTextColor(Color.RED);
        }

        // declare the universal pixels
        final int pix = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 10,
                this.getResources().getDisplayMetrics());
        float posY = pix;
        float posYEditText = 0;
        int itemNr = 1;

        // declare the relative Layout. There the Nodes for the Order get added.
        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.rlCommentFragment);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        for (OrderedItem oritem: MainActivity.orderedItems){

            if (item.getItemID() == oritem.getItemID()){
                sizeOfRelativeLayout++;
            }

        }

        params.height = sizeOfRelativeLayout*5*pix;

        for (final OrderedItem orderedItem: MainActivity.orderedItems){

            if(item.getItemID()==orderedItem.getItemID()){

                final TextView itemName = new TextView(getActivity());
                final EditText comment = new EditText(getActivity());

                orderedComment = orderedItem.getComment();
                if (orderedComment == null){
                    orderedComment = "";
                }

                itemName.setLayoutParams(new LinearLayout.LayoutParams(9 * pix, 10 * pix));
                itemName.setText("Artikel "+itemNr+":");
                itemName.setX(pix/10);
                itemName.setY(posY+(pix/10));
                itemName.setPadding(pix, pix, pix, pix);
                relativeLayout.addView(itemName);

                comment.setLayoutParams(new RelativeLayout.LayoutParams(20* pix, 5*pix));
                comment.setText(orderedComment);
                comment.setX(10*pix);
                comment.setY(posYEditText);
                comment.setPadding(pix, pix, pix, pix);
                relativeLayout.addView(comment);

                comment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        orderedItem.setComment(comment.getText().toString());

                    }
                });

                posY = posY+ 4*pix;
                posYEditText = posYEditText + 4*pix;
                itemNr++;

            }

        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checked){
                    showItemSelectFragment();
                    checked = true;
                }
            }
        });

        return v;

    }

    private void showItemSelectFragment() {

        getActivity().setTitle("Bestellung aufgeben");
        ItemSelectFragment fragment = new ItemSelectFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();


    }

}
