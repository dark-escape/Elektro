package com.stonecode.elektro;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private EditText etUID, etPass;
    private Button btLogin;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_sign_in, container, false);
        etUID= (EditText) root.findViewById(R.id.et_uid);
        etPass= (EditText) root.findViewById(R.id.et_pass);
        int uid=PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(SignUpFragment.SAVED_UID,0);

        if (uid!=0) {
            etUID.setText(String.valueOf(uid));
        }
        btLogin= (Button) root.findViewById(R.id.bt_login);

        if (!etUID.getText().toString().isEmpty()) {
            etPass.requestFocus();
        }
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass, uid;
                uid = etUID.getText().toString();
                pass = etPass.getText().toString();
                if (uid.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getContext(), "None of the fields can be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ServerRequests(getContext()).login(Integer.parseInt(uid), pass, new ServerRequests.RequestCallback() {
                    @Override
                    public void response(Object data) {
                        try {
                            Toast.makeText(getContext(), ((JSONObject)data).getString("msg"), Toast.LENGTH_SHORT).show();
                            if (!((JSONObject)data).getBoolean("error")) {
                                startActivity(new Intent(getContext(),MainActivity.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        return root;
    }

}
