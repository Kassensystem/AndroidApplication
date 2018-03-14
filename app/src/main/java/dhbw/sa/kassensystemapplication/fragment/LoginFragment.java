package dhbw.sa.kassensystemapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dhbw.sa.kassensystemapplication.MainActivity;
import dhbw.sa.kassensystemapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginNameEditText;
    private EditText loginPassword;
    private Button loginButton;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                password = loginPassword.getText().toString();
                try {

                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    messageDigest.update(password.getBytes());
                    encryptedString = new String(messageDigest.digest());

                    MainActivity.loginPassword=encryptedString;
                    MainActivity.loginName = loginNameEditText.getText().toString();

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                showTableFragment();

            }
        });

        return v;
    }
    private void showTableFragment(){

        TableSelection fragment = new TableSelection();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();

    }
}
