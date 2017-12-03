package dhbw.sa.kassensystemapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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


public class URL_Einstellen extends Fragment {

    private Button confirmURL;
    private EditText editTextURL;

    public URL_Einstellen() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the Layout and set it for use
        View v = inflater.inflate(R.layout.fragment_url__einstellen, container, false);

        // Initialize Button and EditText
        editTextURL = v.findViewById(R.id.editTextURL);
        editTextURL.setGravity(Gravity.CENTER);
        confirmURL = v.findViewById(R.id.confirmURL);

        //Set the loaded URL in the EditText View
        editTextURL.setText(MainActivity.ip);

        //Save the ip and update the URL. (Open the TableSelection Fragment)
        confirmURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get the ip from the EditText View
                String ipToSafe = editTextURL.getText().toString();

                //Save the ip longer than the lifeTime of the app
                SharedPreferences shared = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ip",ipToSafe);
                editor.apply();

                //Set the updated ip
                MainActivity.ip = ipToSafe;
                MainActivity.url = "http://"+ipToSafe+":8080/api";
                System.out.println(ipToSafe);

                //Set the Fragment: TableSelection
                TableSelection fragment = new TableSelection();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment,"fragment1");
                fragmentTransaction.commit();
            }
        });


        return v;

    }

}
