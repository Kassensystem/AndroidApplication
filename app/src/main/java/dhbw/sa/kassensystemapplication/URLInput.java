package dhbw.sa.kassensystemapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class URLInput extends AppCompatActivity {

    private Button confirmURL;
    private EditText getTextURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlinput);

        confirmURL = (Button)findViewById(R.id.confirmURL);
        getTextURL = (EditText)findViewById(R.id.getTextURL);

        getTextURL.setGravity(Gravity.CENTER);
        getTextURL.setText(MainActivity.ip);

        confirmURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Save the Ip longer then the lifeTime
                SharedPreferences shared = getSharedPreferences("URLinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ip", getTextURL.getText().toString());
                editor.apply();

                //open up again the main Activity
                Intent intent = new Intent(URLInput.this, MainActivity.class);

                startActivity(intent);

            }
        });



    }
}
