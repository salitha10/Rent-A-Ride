package com.example.astraride.ui.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.astraride.MainActivity;
import com.example.astraride.R;
import com.example.astraride.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    //Declare variables
    EditText name, email, phoneNo, password, address, confirm_password;
    Button btnSignUp;
    DatabaseReference dbf;
    User user;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize ui components
        name = findViewById(R.id.editTextTextName);
        email = findViewById(R.id.editTextTextEmail);
        phoneNo = findViewById(R.id.editTextTextPhone);
        address = findViewById(R.id.editTextTextAddress);
        password = findViewById(R.id.editTextPassword);
        confirm_password = findViewById(R.id.editTextConfirmPassword);
        btnSignUp = findViewById(R.id.btn_signup);

        user = new User();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Save data in database
    public void Save(View view) {
        boolean check = true;

        String passwordInput = password.getText().toString();
        String confirmPasswordInput = confirm_password.getText().toString();

        try {
            //Save in Users table
            dbf = FirebaseDatabase.getInstance().getReference().child("Users");

            //Data validation
            if (TextUtils.isEmpty(name.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Name Text Field is empty", Toast.LENGTH_SHORT).show();
                name.requestFocus();
            } else if (TextUtils.isEmpty(email.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Email Text Field is empty", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (!validateEmail(email.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Email Invalid", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (TextUtils.isEmpty(phoneNo.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Phone No. Text Field is empty", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (phoneNo.getText().toString().trim().length() != 10) {
                check = false;
                Toast.makeText(getApplicationContext(), "Invalid Phone No.", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (TextUtils.isEmpty(address.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Address Text Field is empty", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (TextUtils.isEmpty(password.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Password Text Field is empty", Toast.LENGTH_SHORT).show();
                password.requestFocus();
            } else if (password.getText().toString().length() < 6) {
                check = false;
                Toast.makeText(getApplicationContext(), "Password Should contain at least 6 characters", Toast.LENGTH_SHORT).show();
                password.requestFocus();
            } else if (TextUtils.isEmpty(confirm_password.getText().toString())) {
                check = false;
                Toast.makeText(getApplicationContext(), "Re-enter Password", Toast.LENGTH_SHORT).show();
                confirm_password.requestFocus();
            } else if (!(passwordInput.equals(confirmPasswordInput))) {
                check = false;
                Toast.makeText(getApplicationContext(), "Password mis-match", Toast.LENGTH_SHORT).show();
                confirm_password.requestFocus();
            } else {

                //Validation passed
                if (check == true) {

                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                        user.setUserId(currentUser.getUid());
                                        Log.d("id", currentUser.getUid());
                                        user.setUserImage("https://firebasestorage.googleapis.com/v0/b/astra-ride.appspot.com/o/blank-profile-picture-973460_640.png?alt=media&token=693087a2-f9e9-46d9-885c-a0e33608c8cc");
                                        user.setName(name.getText().toString().trim());
                                        user.setEmail(email.getText().toString().trim());
                                        user.setPhoneNo(phoneNo.getText().toString().trim());
                                        user.setUserType("customer");
                                        user.setAddress(address.getText().toString().trim());
                                        user.setPassword(password.getText().toString().trim());

                                        //Go to main activity
                                        dbf.child(currentUser.getUid()).setValue(user);

                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);

                                        Toast.makeText(SignUp.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    //Validate email
    private boolean validateEmail(String emailstr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailstr);
        return matcher.find();
    }
}