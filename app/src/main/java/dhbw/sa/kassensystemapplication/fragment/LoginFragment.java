package dhbw.sa.kassensystemapplication.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import dhbw.sa.kassensystemapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginNameEditText;
    private EditText loginPassword;
    private Button loginButton;

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

        loginNameEditText.isClickable();
        loginNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loginNameEditText.getText().equals("Login-Name")){
                    loginNameEditText.setText("");
                }
            }
        });

        return v;
    }

}
