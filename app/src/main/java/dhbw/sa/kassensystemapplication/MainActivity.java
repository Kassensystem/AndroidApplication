package dhbw.sa.kassensystemapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.entity.Category;
import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;
import dhbw.sa.kassensystemapplication.fragment.TableSelection;
import dhbw.sa.kassensystemapplication.fragment.adjustUrl;

/**
 * Diese Klasse dient als Container (Hintergrund) für alle anderen Klassen.
 * Zusätzlich werden in dieser Klasse alle Informationen die von der Datenbank empfangen werden, gespeichert.
 * @author Daniel Schifano
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //information which will/are communicate/d with the Server
    /**
     * Liste die alle Tische der Datenbank beinhaltet.
     */
    public static ArrayList<Table> allTables = new ArrayList<>();
    /**
     * Liste die alle Artikel der Datenbank beinhaltet.
     */
    public static ArrayList<Item> allItems = new ArrayList<>();
    /**
     * Liste die alle Bestellungen der Datenbank beinhaltet.
     */
    public static ArrayList<Order> allOrders = new ArrayList<>();
    /**
     * Liste die alle "Kategorien" der Datenbank beinhaltet.
     */
    public static ArrayList<Category> allCategories = new ArrayList<>();
    /**
     * Liste die alle ArtikelIDs einer Bestellung der Datenbank beinhaltet.
     */
    public static ArrayList<Integer> orderItemIDs = new ArrayList<>();
    /**
     * Der Tisch an dem die Bestellung stattfindet.
     */
    public static Table selectedTable;
    /**
     * Zwischenspeicher der ausgewählten BestellungsID.
     */
    public static int selectedOrderID = -1;

    // The variables to get the connection with the server
    /**
     * Speichert die IP-Adresse des Servers.
     */
    public static String ip = null;
    /**
     * Speichert die URL des Servers.
     */
    public static String url = null;
    /**
     * Der Hintergrund für alle weiteren Klassen wird hier gespeichert
     */
    public static Context context;

    // variables
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    public static int widthPixels = 0;
    public static int heigthPixels = 0;


    /**
     * Diese Methode wird aufgerufen wenn die App gestartet wird. Dabei wird das Layout(Hintergrund) für alle weiteren Klassen initialisiert.
     * @param savedInstanceState Gibt an in welchem Abschnitt des Lebenszyklus die App sich befindet. Ob sie z.B. geschlossen wurde oder gestartet wurde.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set the Activity (the complete App) to Portrait
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this.getApplicationContext();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthPixels = metrics.widthPixels;
        heigthPixels = metrics.heightPixels;

        //Load the URL. If there is non, set URL standard
        if (!loadURL()){

            url = "http://192.168.178.25:8080/api";

        }

        // Creat the Navigation Draw
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open ,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Declare the Fragment which will be shown by the start of the app
        setTitle("Bestellung aufgeben");
        TableSelection fragment = new TableSelection();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment,"fragment1");
        fragmentTransaction.commit();

    }

    /**
     * Methode, die den übergebenen Text auf dem Smartphone darstellt.
     * @param handoverText Der Text welcher dargestellt werden soll.
     */
    public void showToast(String handoverText){

        Toast.makeText(this, handoverText, Toast.LENGTH_LONG).show();

    }

    /**
     * Mithilfe dieser Methode wird der Button initialisiert.
     *
     * @param item Der Button des Navigation Drawer.
     * @return true, wenn die Methode richtig abgearbeitet werden kann.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Mit dieser Funktion werden die verschiedenen Klassen (Fragments) die im Navigation-Drawer auswählbar sind aufgerufen.
     * @param item Das item das in dem Navigation-Drawer ausgewählt/angeklickt wurde
     * @return true, wenn die Methode ohne Fehler abgearbeitet werden konnte
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // which item is chosen
        if (id == R.id.nav_URL) {

            setTitle("URL Einstellen");
            adjustUrl fragment = new adjustUrl();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_Login){

           showToast("Hier wird dann der Login Bildschirm nächstes Semester eingebaut!");

        }else if (id == R.id.nav_CR) {

            showToast("Marvin Mai\nDaniel Schifano");

        } else if (id == R.id.nav_Order){

            setTitle("Bestellung aufgeben");
            TableSelection fragment = new TableSelection();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * In dieser Methode werden die IP-Adresse und die URL geladen.
     * Hierfür wird in der Klasse AdjustUrl die IP-Adresse und die URL über den Lebenszyklus der Applikation gespeichert
     * @return true, wenn bereits ein URL gespeichert wurde. False wenn noch kein URL gespeichert wurde
     */
    public boolean loadURL(){

        // Get the ip from the "store" of the app
        SharedPreferences shared = getPreferences(0);

        // If there is nothing saved before, the return is false
        ip = shared.getString("ip","");
        if(ip == null){
            return false;

        // If there is an ip saved before, the return is true
        } else {
            url = "http://" + ip + ":8080/api";
            return true;
        }
    }

}
 /*
  * TODO: Schauen ob mab das hinbekommt, eine Meldung zu geben ob der Server gerade geschlossen wurde
  * TODO: SO wie der Moosi gemeint hat, die Anzahl der Artikel darstellen
  * TODO: Schauen was für Tests es gibt, die ich machen kann.....vorallem mit den Meilensteinen
  */