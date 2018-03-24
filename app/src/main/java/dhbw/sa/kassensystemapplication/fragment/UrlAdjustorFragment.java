package dhbw.sa.kassensystemapplication.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;

/**
 * In dieser Klasse wird der Bildschirm zur IP-Adressen-Auswahl der Applikation erstellt.
 * Dieser wird ebenfalls aufgerufen, wenn angefangen wird eine Bestellung aufzugeben.
 * @author Daniel Schifano
 */
public class UrlAdjustorFragment extends Fragment {

    /**
     * Nodes, in denen die Informationen für den Anwendern dargestellt werden, beziehungsweise die
     * sie verwenden können.
     */
    private Button confirmURL;
    private EditText editTextURL;
    private TextView textView;

    /**
     * Der Konstruktor, der zum aufrufen dieser Klasse benötigt wird.
     * Er benötigt keine Übergabe Parameter.
     * Damit wird der neue Bildschirm initalisiert und kann auf dem Smartphone angezeigt werden.
     */
    public UrlAdjustorFragment() {

    }

    /**
     * Mit dieser Methode kann die IP-Adresse eingestellt werden.
     *
     * @param inflater Instantiiert ein XML-Layout in ein passendes View Objekt
     * @param container Erlaubt den Zugriff auf container Eigenschaften
     * @param savedInstanceState Gibt an in welchem Abschnitt des Lebenszyklus die App sich befindet. Ob sie z.B. geschlossen wurde oder gestartet wurde.
     * @return View die dargestellt werden soll
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the Layout and set it for use
        View v = inflater.inflate(R.layout.fragment_url__einstellen, container, false);

        // Initialize Button and EditText
        editTextURL = v.findViewById(R.id.editTextURL);
        editTextURL.setGravity(Gravity.CENTER);
        confirmURL = v.findViewById(R.id.confirmURL);
        textView = v.findViewById(R.id.textViewURL);

        //Set the loaded URL in the EditText View
        editTextURL.setText(MainActivity.ip);

        //Save the ip and update the URL. (Open the TableSelectionFragment Fragment)
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

                //Set the Fragment: TableSelectionFragment
                TableSelectionFragment fragment = new TableSelectionFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,fragment,"fragment1");
                fragmentTransaction.commit();
                getActivity().setTitle("Bestellung aufgeben");
            }
        });



        return v;

    }

}
