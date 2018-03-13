package dhbw.sa.kassensystemapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dhbw.sa.kassensystemapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckProduce extends Fragment {


    public CheckProduce() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_produce, container, false);
    }

}
