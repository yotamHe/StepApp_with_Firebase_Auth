package com.example.StepApp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.StepApp.R;
import com.example.StepApp.SensorsAndAdapters.HelpDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class regActivity extends AppCompatActivity {

    private final String TAG = "myDebug";

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

    private MaterialEditText regInputEmail;
    private MaterialEditText regInputPassword;

    private TextView helpBtn;
    private TextView completeRegBtn;
    private MaterialEditText fullname;


    private DatabaseReference rRootRef;
    private FirebaseAuth auth;
    private FirebaseDatabase mDB;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        regInputEmail = findViewById(R.id.regEmailInput);
        regInputPassword = findViewById(R.id.regPassInput);
        completeRegBtn = findViewById(R.id.completeRegBtn);
        fullname = findViewById(R.id.fullname);
        helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        auth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        rRootRef = mDB.getReference().child("StepAppDB").child("Users");
        pd = new ProgressDialog(this);


        /*
         * what happens when the user clicks on the register button:
         * if valid - continues to login screen
         * else, error messages will appear
         * */
        completeRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = regInputEmail.getText().toString().trim();
                String passwordInput = regInputPassword.getText().toString().trim();
                String fullName = Objects.requireNonNull(fullname.getText()).toString().trim();
                if (emailInput.isEmpty() && passwordInput.isEmpty() && fullName.isEmpty()) {
                    regInputEmail.setError("Field can't be empty");
                    regInputPassword.setError("Field can't be empty");
                    fullname.setError("Field can't be empty");

                } else if (emailInput.isEmpty() && !PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    regInputEmail.setError("Field can't be empty");
                    regInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");

                } else if (passwordInput.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    regInputPassword.setError("Field can't be empty");
                    regInputEmail.setError("Please enter a valid email address");

                } else if (emailInput.isEmpty() && PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    regInputEmail.setError("Field can't be empty");

                } else if (passwordInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
                    regInputPassword.setError("Field can't be empty");

                else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && !PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    regInputEmail.setError("Please enter a valid email address");
                    regInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");

                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && PASSWORD_PATTERN.matcher(passwordInput).matches()) {
                    regInputEmail.setError("Please enter a valid email address");

                } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
                    regInputPassword.setError("Password must have at least one uppercase letter, one lowercase letter, a number and one special character");

                else {
                    regInputEmail.setError(null);
                    regInputPassword.setError(null);
                    regUser(emailInput, passwordInput);
                }
            }
        });
    }

    //opens a help dialog
    public void openDialog() {
        HelpDialog HelpDialog = new HelpDialog();
        HelpDialog.show(getSupportFragmentManager(), "dialog");
    }

    //returns to login screen
    public void backToLogin(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    //shows a loader until registration is done
    public void regUser(String Email, String Password) {
        Log.d("result", "result");
        pd.setMessage("Please Wait..");
        pd.show();

        //authentication with Firebase
        auth.createUserWithEmailAndPassword(regInputEmail.getText().toString().trim(), regInputPassword.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "onSuccess: ");
                HashMap<String, Object> users = new HashMap<>();
                users.put("name", fullname.getText().toString().trim());
                users.put("email", regInputEmail.getText().toString().trim());
                users.put("password", regInputPassword.getText().toString().trim());
                users.put("Imageurl", "default");
                users.put("birthDate", "null");
                users.put("Height", "null");
                users.put("Weight", "null");
                users.put("steps", "0");
                rRootRef.child(auth.getCurrentUser().getUid())
                        .setValue(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: reg");
                                Toast.makeText(regActivity.this, "Registration Succeeded", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(regActivity.this, MainActivity.class);
                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: reg" + e.getMessage());
                        Toast.makeText(regActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });

    }
}
