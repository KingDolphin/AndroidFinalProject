package com.leonmontealegre.game.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import static com.leonmontealegre.game.android.LoginActivity.encodePassword;

public class SignUpActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = "SignUpActivity";

    private EditText userNameField;
    private EditText passwordField;
    private EditText retypePasswordField;
    private EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

//        if (getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userNameField = (EditText)findViewById(R.id.username_input_field);
        passwordField = (EditText)findViewById(R.id.password_input_field);
        retypePasswordField = (EditText)findViewById(R.id.retype_password_input_field);
        emailField = (EditText)findViewById(R.id.email_input_field);

        Button signUpButton = (Button)findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(userNameField.getText().toString(), passwordField.getText().toString(),
                        retypePasswordField.getText().toString(), emailField.getText().toString());
            }
        });
    }

    private void signUp(String username, String password, String passwordRetyped, String email) {
        if (!password.equals(passwordRetyped)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT);
        } else if (username.length() < Options.MIN_USERNAME_LENGTH) {
            Toast.makeText(this, "Username too short! Must be at least " + Options.MIN_USERNAME_LENGTH + " characters!", Toast.LENGTH_SHORT).show();
        } else if (username.length() > Options.MAX_USERNAME_LENGTH) {
            Toast.makeText(this, "Username too long! Cannot be more than " + Options.MAX_USERNAME_LENGTH + " characters!", Toast.LENGTH_SHORT).show();
        } else if (password.length() < Options.MIN_PASSWORD_LENGTH) {
            Toast.makeText(this, "Password too short! Must be at least " + Options.MIN_PASSWORD_LENGTH + " characters!", Toast.LENGTH_SHORT).show();
        } else {
            BackendlessUser user = new BackendlessUser();
            user.setProperty(Options.USERNAME_KEY, username);
            user.setEmail(email);
            user.setPassword(encodePassword(password));

            Backendless.UserService.register(user, new LoadingCallback<BackendlessUser>(this, "Registering...") {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {
                    super.handleResponse(backendlessUser);
                    Toast.makeText(SignUpActivity.this, backendlessUser.getEmail() + " successfully registered!", Toast.LENGTH_SHORT).show();
                    SignUpActivity.this.finish();
                }
            });
        }
    }

}