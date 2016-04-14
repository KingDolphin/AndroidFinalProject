package com.leonmontealegre.game.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class LoginActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "LoginActivity";

    private static final String USERNAME_JSON = "username";
    private static final String PASSWORD_JSON = "password";
    private static final String PASSWORD_LENGTH_JSON = "passwordLength";

    private static final String JSON_FILE_NAME = "save.json";

    private EditText passwordField;
    private EditText userNameField;

    private CheckBox rememberPasswordCheckBox;

    private JSONObject loginJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        Backendless.initApp(this, Options.APP_ID, Options.SECRET_KEY, Options.VERSION);

        userNameField = (EditText) findViewById(R.id.username_input_field);
        passwordField = (EditText) findViewById(R.id.password_input_field);

        Button loginButton = (Button) findViewById(R.id.login_button);

        TextView signUpButton = (TextView) findViewById(R.id.sign_up_button);
        TextView forgotPasswordButton = (TextView) findViewById(R.id.forgot_password_button);

        rememberPasswordCheckBox = (CheckBox) findViewById(R.id.remember_password_check_box);

        load();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(userNameField.getText().toString(), passwordField.getText().toString());
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause("email = '"+email+"'");
        Backendless.Persistence.of("").find

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "hi1");
                Backendless.UserService.restorePassword("leon", new LoadingCallback<Void>(LoginActivity.this, "Loading...") {
                    @Override
                    public void handleResponse(Void v) {
                        Log.d(TAG, "Temporary password has been emailed to the user");
                    }
                });
                Log.d(TAG, "hi2");
            }
        });
    }

    private void attemptLogin(final String username, final String password) {
        String encodedPassword;
        if (!loginJSON.optString(PASSWORD_JSON).isEmpty())
            encodedPassword = loginJSON.optString(PASSWORD_JSON);
        else
            encodedPassword = encodePassword(password);

        final String finalPassword = encodedPassword;
        try {
            loginJSON.put(USERNAME_JSON, username);

            loginJSON.put(PASSWORD_JSON,        rememberPasswordCheckBox.isChecked() ? finalPassword        : "");
            loginJSON.put(PASSWORD_LENGTH_JSON, rememberPasswordCheckBox.isChecked() ? password.length()    : 0);

            save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Backendless.UserService.login(username, finalPassword, new LoadingCallback<BackendlessUser>(this, "Logging in...") {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                super.handleResponse(backendlessUser);

                try {
                    loginJSON.put(USERNAME_JSON, username);

                    loginJSON.put(PASSWORD_JSON,        rememberPasswordCheckBox.isChecked() ? finalPassword        : "");
                    loginJSON.put(PASSWORD_LENGTH_JSON, rememberPasswordCheckBox.isChecked() ? password.length()    : 0);

                    save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "logged on");

//                Intent i = new Intent(LoginActivity.this, AndroidLauncher.class);
//                i.putExtra(Options.USER_EXTRA, backendlessUser);
//                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!rememberPasswordCheckBox.isChecked())
            passwordField.setText("");
    }

    private void save() {
        try {
            OutputStream out = this.openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            writer.write(loginJSON.toString());
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving file on: " + JSON_FILE_NAME, e);
        }
    }

    private void load() {
        try {
            InputStream in = this.openFileInput(JSON_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                jsonString.append(line);

            loginJSON = new JSONObject(jsonString.toString());

            reader.close();

            if (!loginJSON.optString(USERNAME_JSON).isEmpty()) {
                userNameField.setText(loginJSON.optString(USERNAME_JSON));
            }
            if (!loginJSON.optString(PASSWORD_JSON).isEmpty()) {
                rememberPasswordCheckBox.setChecked(true);
                String str = "";
                for (int i = 0; i < loginJSON.optInt(PASSWORD_LENGTH_JSON); i++)
                    str += " ";
                passwordField.setText(str);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading file from: " + JSON_FILE_NAME, e);
            loginJSON = new JSONObject();
        }
    }

    public static String encodePassword(String password) {
        String encodedPassword = "";
        long totalObfuscated = 0;
        for (int i = 0; i < password.length(); i++) {
            int asciiVal = (int)password.charAt(i);
            totalObfuscated += (((long)Math.pow(asciiVal, 3) * (long)Math.pow(password.length(), 2) * Integer.bitCount(asciiVal*password.length())));
            String obfuscatedVal = "" + totalObfuscated;
            while (obfuscatedVal.length() > 3) {
                int val = Integer.valueOf(obfuscatedVal.substring(0, 3)) + 0x0021;
                encodedPassword += (!Character.isWhitespace((char)val) ? (char)val : "");
                obfuscatedVal = obfuscatedVal.substring(3);
            }
            if (obfuscatedVal.length() > 0)
                encodedPassword += (char)Integer.valueOf(obfuscatedVal).intValue();
        }
        return encodedPassword;
    }

}