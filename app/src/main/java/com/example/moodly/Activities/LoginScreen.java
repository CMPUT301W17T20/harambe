package com.example.moodly.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * This class is the login screen of the app.
 * Users can either login or sign up for an account on the app.
 * Users are stored in a master list of users that will be used
 * to add and confirm users as they use the app.
 */
public class LoginScreen extends AppCompatActivity {
    //final ArrayList<User> userList = new ArrayList<>();
    EditText userName;
    Button loginButton;
    Button signUpButton;
    Intent intent;
    UserController conn = UserController.getInstance();

    /**
     * Creates the ViewMoodList intent and calls upon setListeners()
     * @param savedInstanceState
     * @see #setListeners()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userName = (EditText) findViewById(R.id.userName);
        intent = new Intent(getApplicationContext(), ViewMoodList.class);
        setListeners();
    }

    /*
    protected void showInput(String dialogText){
        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View view = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        builder.setView(view);
        builder.setTitle(dialogText);
        // Taken from http://stacktips.com/tutorials/android/android-input-dialog-example 3/11/2017
        final EditText userInput = (EditText)view.findViewById(R.id.usernameText);
        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = userInput.getText().toString();
                User person = new User(username);
                userList.add(person);
                Intent intent = new Intent(LoginScreen.this,ViewMoodList.class);
                Toast.makeText(LoginScreen.this,"Login Successful",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        builder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }*/



    /**
     * Provides a short toast to current user logging in
     */
    private void hello(String name) {
        String greetings = "Hello, " + name;
        Toast.makeText(LoginScreen.this, greetings, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets listeners for login and sign up buttons
     *
     */
    protected void setListeners(){
        loginButton = (Button) findViewById(R.id.Login);
        signUpButton = (Button) findViewById(R.id.Register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setCurrentUser(userName.getText().toString());
                if (conn.getCurrentUser() != null) {
                    hello(userName.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginScreen.this, "This username does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

//         No sign up functionality yet
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setCurrentUser(userName.getText().toString());
                if (conn.getCurrentUser() == null) {
                    conn.createUser(userName.getText().toString());
                    hello(userName.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginScreen.this, "This username already exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}