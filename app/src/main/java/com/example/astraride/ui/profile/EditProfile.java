package com.example.astraride.ui.profile;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.astraride.R;
import com.example.astraride.models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {

    EditText name, email, phoneNo, password, address, confirm_password;
    Button btnUpdate, btnUpload;
    ImageView dp;
    DatabaseReference dbf;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        phoneNo = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpload = findViewById(R.id.btn_upload);
        dp = findViewById(R.id.dp);


        //Loading screen
        ProgressDialog pd = new ProgressDialog(EditProfile.this);
        pd.setMessage("Loading");
        pd.show();

        //Get userID from intent
//        Intent intent = getIntent();
//        String id = intent.getExtras().getString("userID");
        String id = "Qv33H6pzzYVGmuV2y8Lk00q8q073";

        dbf = FirebaseDatabase.getInstance().getReference("Users").child(id);
        Log.d("profileID", id);

        //Get data from database
        dbf.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                if (snapshot.hasChildren()){


                    //Initialize user object
                    user = new User();
                    user.setUserId(id);

                    //Display data
                    name.setText(snapshot.child("name").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());
                    phoneNo.setText(snapshot.child("phoneNo").getValue().toString());
                    address.setText(snapshot.child("address").getValue().toString());

                    user.setPassword(snapshot.child("password").getValue().toString());
//                    user.setUserType(snapshot.child("userType").getValue().toString());

                    //Disable editing
                    name.setFocusable(false);
                    email.setFocusable(false);
                    phoneNo.setFocusable(false);
                    address.setFocusable(false);

                    pd.cancel();

                }

                else{
                    Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DBError", error.getMessage());
            }
        });

        //Update database
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChildren()){

                            user.setName(name.getText().toString().trim());
                            user.setEmail(email.getText().toString().trim());
                            user.setPhoneNo(phoneNo.getText().toString().trim());
                            user.setAddress(address.getText().toString().trim());

                            //Save in database
                            dbf.setValue(user);
                            Toast.makeText(EditProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DBError", error.getMessage());
                    }
                });
            }
        });


    }

    public void captureImage(View view){
        ImagePicker.with(EditProfile.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.d("URI data", data.getStringExtra("dat").toString());
//        Toast.makeText(EditProfile.this,  requestCode, Toast.LENGTH_SHORT).show();

        if(requestCode ==  2404){

            Toast.makeText(EditProfile.this, data.getData().toString(), Toast.LENGTH_SHORT).show();

            Uri uri = data.getData();
            dp.setImageURI(uri);

        }

    }
}