package dhbw.sa.kassensystemapplication.fragment;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginNameEditText;
    private EditText loginPassword;
    private Button loginButton;
    private static String text = null;
    private static Boolean response = false;

    String password;
    String encryptedString;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        loginNameEditText = v.findViewById(R.id.loginName);
        loginPassword = v.findViewById(R.id.loginPassword);
        loginButton = v.findViewById(R.id.loginButton);

        loginNameEditText.setText(MainActivity.loginName);
        loginPassword.setText(MainActivity.loginPasswordHash);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = loginPassword.getText().toString();
                if(!MainActivity.loginPasswordHash.equals(password)) {
                    encryptedString = String.valueOf(password.hashCode());
                } else {
                    encryptedString = password;
                }

                MainActivity.loginPasswordHash = encryptedString;
                MainActivity.loginName = loginNameEditText.getText().toString();

                SharedPreferences shared = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("loginname",loginNameEditText.getText().toString());
                editor.putString("passwordhash", encryptedString);
                editor.apply();

                new loginCheck().execute();

            }
        });

        return v;
    }
    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelection fragment = new TableSelection();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }

    private class loginCheck extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                ResponseEntity<Boolean> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/login", HttpMethod.GET,
                                Entity.getEntity(null),Boolean.class);
                text = null;
                response = responseEntity.getBody();

            } catch (HttpClientErrorException e){
                text = "Der Name oder das Passwort wurden falsch eingegeben.\n" +
                        "Bitte überprüfen Sie Ihre Logindaten.";
                e.printStackTrace();
            }catch (Exception e){
                text = "undefinierter Fehler";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response){
                showTableFragment();
            } else {
                showToast(text);
            }


        }


    }

    private void showToast(String text){
        if(text != null) {
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_LONG).show();
            System.out.println(text);
        }
    }



}
