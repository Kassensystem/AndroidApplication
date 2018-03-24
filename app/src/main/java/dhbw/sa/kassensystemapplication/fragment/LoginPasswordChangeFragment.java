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

/**
 * In dieser Klasse wird der Login-Passwort-ändern-Bildschirm der Applikation erstellt.
 *
 * @author Daniel Schifano
 */
public class LoginPasswordChangeFragment extends Fragment {

    /**
     * Nodes, in denen die Informationen für den Anwendern dargestellt werden, beziehungsweise die
     * sie verwenden können.
     */
    private Button confirmPasswordChange;
    private EditText loginName;
    private EditText oldPassword;
    private EditText newPassword;
    private EditText repeatNewPassword;
    private TextView wrongPasswort;
    /**
     * Variablen, die zu "Berechnungen" innerhalb der Java-Klasse verwendet werden.
     */
    private String getNewpassword;
    private String sendNewPassword;
    private boolean agreedNewPassword = false;
    private Boolean response = false;
    private static String text = null;
    private boolean checked;
    /**
     * Der Konstruktor, der zum aufrufen dieser Klasse benötigt wird.
     * Er benötigt keine Übergabe Parameter.
     * Damit wird der neue Bildschirm initalisiert und kann auf dem Smartphone angezeigt werden.
     */
    public LoginPasswordChangeFragment() {
        // Required empty public constructor
    }
    /**
     * Diese Methode wird aufgerufen wenn das Fragment erstellt wird. Dabei werden alle Nodes
     * initialisiert.
     * Nach erfolgreichem ändern des Passworts wird das Passwort in der MainActivity gespeichert.
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
        //Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login_password_change, container, false);
        checked = false;

        confirmPasswordChange = v.findViewById(R.id.confirmPasswordChange);
        loginName = v.findViewById(R.id.loginNamePasswordChange);
        oldPassword = v.findViewById(R.id.oldPassword);
        newPassword = v.findViewById(R.id.newPassword);
        repeatNewPassword = v.findViewById(R.id.repeatNewPassword);
        wrongPasswort = v.findViewById(R.id.wrongPassword);

        //Überprüfen ob die Passwörter übereinstimmen
        repeatNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                getNewpassword = newPassword.getText().toString();
                String getRepeatNewPassword = repeatNewPassword.getText().toString();
                if(!getNewpassword.equals(getRepeatNewPassword)){
                    wrongPasswort.setText("Die Passwörter stimmen nicht überein!");
                    wrongPasswort.setTextColor(Color.RED);
                    agreedNewPassword = false;
                } else {
                    wrongPasswort.setText("");
                    agreedNewPassword = true;
                }

            }
        });

        confirmPasswordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checked) {
                    if(agreedNewPassword){

                        MainActivity.loginName = loginName.getText().toString();
                        MainActivity.loginPasswordHash = String.valueOf(oldPassword.getText().toString().hashCode());
                        sendNewPassword = String.valueOf(newPassword.getText().toString().hashCode());

                        oldPassword.setText("");
                        newPassword.setText("");
                        repeatNewPassword.setText("");

                        new ChangeLoginPassword().execute();

                    }else {
                        showToast("Überprüfen Sie Ihre Eingabe.");
                    }
                }

            }
        });

        return v;
    }

    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param text Der Text welcher dargestellt werden soll.
     */
    private void showToast(String text){

        if(text != null){
            Toast.makeText(MainActivity.context, text, Toast.LENGTH_SHORT).show();
        }


    }
    /**
     * Mithilfe dieser Methode wird die Java-Klasse TableSelectionFragment aufgerufen und die
     * Java-Klasse LoginPasswordChangeFragment wird nicht mehr dargestellt.
     */
    private void showTableFragment(){

        getActivity().setTitle("Bestellung aufgeben");
        TableSelectionFragment fragment = new TableSelectionFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
    /**
     * Diese Klasse wird dafür verwendet, das Passwort zu ändern. Dabei wird mit
     * dem Schlüsselwort "changeLoginPasswort" der Server kontaktiert. Dabei wird das neue Passwort
     * übergeben.
     */
    private class ChangeLoginPassword extends AsyncTask<Void, Void, Void> {
        /**
         * Mit dieser Methode wird das Passwort an den Server/Datenbank übertragen.
         *
         * @param params welche Datentypen die Informationen haben, die im Hintergrund bearbeitet
         *               werden sollen.
         * @return gibt null zurück, da Informationen lediglich an den Server geschickt werden.
         */
        @Override
        protected Void doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();

            try {
                response = false;
                ResponseEntity<Boolean> responseEntity = restTemplate.exchange
                        (MainActivity.url + "/changeLoginPassword", HttpMethod.PUT,
                                Entity.getEntity(sendNewPassword),Boolean.class);
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
         * @param aVoid wird hier nicht benötigt
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (response){
                showTableFragment();
                MainActivity.loginPasswordHash = sendNewPassword;
                SharedPreferences shared = getActivity().getPreferences(0);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("passwordhash", sendNewPassword);
                editor.apply();
            } else {
                showToast(text);
            }


        }


    }


}
