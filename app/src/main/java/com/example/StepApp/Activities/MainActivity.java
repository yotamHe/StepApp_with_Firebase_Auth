package com.example.StepApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.StepApp.Models.User;
import com.example.StepApp.R;
import com.example.StepApp.SensorsAndAdapters.StepDetector;
import com.example.StepApp.SensorsAndAdapters.StepListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private int numSteps;
    private TextView TvSteps;
    private Button BtnStart;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    User user;

    // valid password pattern
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$^&+=])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$");

    private EditText textInputEmail;
    private EditText textInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        textInputEmail = findViewById(R.id.emailInput);
        textInputPassword = findViewById(R.id.editTextTextPassword);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        pd = new ProgressDialog(this);

        BtnStart = (Button) findViewById(R.id.loginButton);


        /*
        * what happens when the user clicks on the login button:
        * if valid - continues to home screen
        * else, error messages will appear
        * */
        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailInput = textInputEmail.getText().toString().trim();
                String passwordInput = textInputPassword.getText().toString().trim();

                if (emailInput.isEmpty() && passwordInput.isEmpty()) {
                    textInputEmail.setError("Field can't be empty");
                    textInputPassword.setError("Field can't be empty");
                } else if (emailInput.isEmpty() && !PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    textInputEmail.setError("Field can't be empty");
                    textInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");
                } else if (passwordInput.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    textInputPassword.setError("Field can't be empty");
                    textInputEmail.setError("Please enter a valid email address");
                } else if (emailInput.isEmpty() && PASSWORD_PATTERN.matcher(passwordInput).matches())
                    textInputEmail.setError("Field can't be empty");

                else if (passwordInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
                    textInputPassword.setError("Field can't be empty");
                else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && !PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    textInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");
                    textInputEmail.setError("Please enter a valid email address");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && PASSWORD_PATTERN.matcher(passwordInput).matches())
                    textInputEmail.setError("Please enter a valid email address");
                else if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && !PASSWORD_PATTERN.matcher(passwordInput).matches())
                    textInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");

                else {
                    textInputPassword.setError(null);
                    textInputEmail.setError(null);
                    loginUser(emailInput, passwordInput);
                }
            }
        });
    }

    //Exit app function
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finishAffinity();
                        System.exit(0);
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    // --  move to registration screen
    public void regScreen(View view) {
        Intent intent = new Intent(this, regActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void loginUser(String email, String password) {

        pd.setMessage("Please Wait..");
        pd.show();


        //authentication with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("test", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(MainActivity.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("test", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Something Went Wrong...",
                            Toast.LENGTH_SHORT).show();
                }

                pd.dismiss();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNS) {
        numSteps++;

    }

}
