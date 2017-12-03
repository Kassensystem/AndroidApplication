package dhbw.sa.kassensystemapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.params.HttpParams;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dhbw.sa.kassensystemapplication.entity.Item;
import dhbw.sa.kassensystemapplication.entity.Order;
import dhbw.sa.kassensystemapplication.entity.Table;
import dhbw.sa.kassensystemapplication.fragment.TableSelection;
import dhbw.sa.kassensystemapplication.fragment.URL_Einstellen;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //information which will/are communicate/d with the Server
    public static ArrayList<Table> allTables = new ArrayList<>();
    public static ArrayList<Item> allItems = new ArrayList<>();
    public static ArrayList<Order> allOrders = new ArrayList<>();
    public static ArrayList<Integer> orderItemIDs = new ArrayList<>();
    public static Table selectedTable;
    public static int selectedOrderID = -1;

    // The variables to get the connection with the server
    public static String ip = null;
    public static String url = null;

    // variables
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public void showToast(String handoverText){

        Toast.makeText(this, handoverText, Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_URL) {

            setTitle("URL Einstellen");
            URL_Einstellen fragment = new URL_Einstellen();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"fragment1");
            fragmentTransaction.commit();

        } else if (id == R.id.nav_Login){

           showToast("Hier wird dann der Login Bildschirm nächstes Semester eingebaut!");

        }else if (id == R.id.nav_CR) {

            showToast("Marvin Mai\nDaniel Schifano");

        } else if (id == R.id.nav_Order){

            setTitle("Bestellung aufgeben");
            TableSelection fragment = new TableSelection();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame,fragment,"fragment1");
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
