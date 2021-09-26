package com.example.astraride.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astraride.MainActivity;
import com.example.astraride.R;
import com.example.astraride.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class Login extends AppCompatActivity {

    EditText email, password;
    TextView forgetpassword;
    Button login, signup;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbf;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize ui components
        email = findViewById(R.id.textedit_email);
        password = findViewById(R.id.textedit_password);
        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.btn_signup);
        forgetpassword = findViewById(R.id.forgot_pass);

        firebaseAuth = FirebaseAuth.getInstance();

        //Forgot password
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Dialog bix to get input
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog  = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Forgot Password?");
                passwordResetDialog.setMessage("Enter your Email to reset the password");
                passwordResetDialog.setView(resetMail);

                //Send email to reset password
                passwordResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this,"Reset Email Link Sent",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this,"Error: "+ e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                });

                //Cancel button
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //redirect to login page
                    }
                });

                //Show dialog box
                passwordResetDialog.create().show();
            }
        });

    }

    //Go to sign up page
    public void SignUp(View view){
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }


    //Login
    public void Login(View view) {

        boolean check = true;

        try {

            //Validate fields
            if (TextUtils.isEmpty(email.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Email Text Field is empty", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(password.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Password Text Field is empty", Toast.LENGTH_SHORT).show();
            } else {

                if (check == true) {

                    // firebase authentication implementation
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        //Go to homepage after login
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);

                                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    }

                                    //Login fails
                                    else{
                                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}