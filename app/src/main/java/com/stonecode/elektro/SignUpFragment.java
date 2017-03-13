package com.stonecode.elektro;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private EditText etName, etAge, etEmail, etPass;
    private Button btRegister;

    public static final String SAVED_UID="uid";

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        etName = (EditText) root.findViewById(R.id.et_name);
        etAge = (EditText) root.findViewById(R.id.et_age);
        etEmail = (EditText) root.findViewById(R.id.et_email);
        etPass = (EditText) root.findViewById(R.id.et_pass);
        btRegister = (Button) root.findViewById(R.id.register);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, email, pass, age;
                name = etName.getText().toString();
                age = etAge.getText().toString();
                pass = etPass.getText().toString();
                email = etEmail.getText().toString();
                if (name.isEmpty() || age.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getContext(), "None of the fields can be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ServerRequests(getContext()).newUser(name, Integer.parseInt(age), email, pass, new ServerRequests.RequestCallback() {
                    @Override
                    public void response(Object data) {
                        JSONObject object= (JSONObject) data;
                        try {
                            Toast.makeText(getContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                            if (object.getBoolean("error")) {
                                return;
                            }
                            saveUID(object.getInt("u_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });



        return root;
    }

    private void saveUID(int u_id) {
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getContext());

        SharedPreferences.Editor et=pref.edit();

        et.putInt(SAVED_UID,u_id);
        et.apply();
        Toast.makeText(getContext(), "Your UID is: "+u_id, Toast.LENGTH_SHORT).show();

    }

}
