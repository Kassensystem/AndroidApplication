package dhbw.sa.kassensystemapplication.fragment;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import dhbw.sa.kassensystemapplication.Entity;
import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;

/**
 * In dieser Klasse wird der Login-Bildschirm der Applikation erstellt.
 *
 * @author Daniel Schifano
 */
public class LoginFragment extends Fragment {

    /**
     * Nodes, in denen die Informationen für den Anwendern dargestellt werden, beziehungsweise die
     * sie verwenden können.
     */
    private EditText loginNameEditText;
    private EditText loginPassword;
    private Button loginButton;
    private static String text = null;
    private static Boolean response;
    private boolean checked;
    /**
     * Variablen, die zu "Berechnungen" innerhalb der Java-Klasse verwendet werden.
     */
    String password;
    String encryptedString;
    /**
     * Der Konstruktor, der zum aufrufen dieser Klasse benötigt wird.
     * Er benötigt keine Übergabe Parameter.
     * Damit wird der neue Bildschirm initalisiert und kann auf dem Smartphone angezeigt werden.
     */
    public LoginFragment() {

    }
    /**
     * Diese Methode wird aufgerufen wenn das Fragment erstellt wird. Dabei werden alle Nodes
     * initialisiert.
     * Nach erfolgreichem Anmelden am Server wird das Passwort und der Login-Name in der
     * MainActivity gespeichert.
     * So muss es nicht bei jedem Start der App neu eingegeben werden.
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        response = false;
        checked = false;

        loginNameEditText = v.findViewById(R.id.loginName);
        loginPassword = v.findViewById(R.id.loginPassword);
        loginButton = v.findViewById(R.id.loginButton);

        loginNameEditText.setText(MainActivity.loginName);
        loginPassword.setText(MainActivity.loginPasswordHash);

        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checked = false;
            }
        });

        loginNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checked = false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!checked) {
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

                    new LoginCheck().execute();
                    checked = true;
                }

            }
        });


        return v;
    }
    /**
     * Mithilfe dieser Methode wird die Java-Klasse TableSelectionFragment aufgerufen und die
     * Java-Klasse LoginFragment wird nicht mehr dargestellt.
     */
    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelectionFragment fragment = new TableSelectionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param text Der Text welcher dargestellt werden soll.
     */
    private void showToast(String text){
        if(text != null) {
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_LONG).show();
            System.out.println(text);
        }
    }
    /**
     * Diese Klasse wird dafür verwendet, die Login-Daten der Bedienung zu überprüfen. Dabei wird
     * mit dem Schlüsselwort "login" der Server kontaktiert.
     * Dabei wird der Login-Name und das Login-Passwort übergeben. Wird vom Server eine bestätigung
     * gesendet, gilt die Bedienung als angemeldet.
     */
    private class LoginCheck extends AsyncTask<Void, Void, Void> {
        /**
         * Mit dieser Methode werden die Login-Daten an den Server/Datenbank übertragen.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet
         *               werden sollen.
         * @return gibt null zurück, da Informationen lediglich an den Server geschickt werden.
         */
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

            } catch (ResourceAccessException e) {
                text = "Es konnte keine Verbindung aufgebaut werden.";
                return null;
            }catch (Exception e){
                text = "undefinierter Fehler";
                e.printStackTrace();
            }
            return null;
        }
        /**
         * Falls bei der Übertragung zum Server ein Fehler auftritt, wird mithilfe
         * der ShowToast-Methode dieser Fehler dargestellt.
         *
         * Zu diesem Fehler zählt auch, wenn die Login-Daten nicht korrekt sind.
         *
         * @param aVoid wird hier nicht benötigt
         */
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






}
