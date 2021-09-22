package com.example.astraride.ui.profile;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.astraride.MainActivity;
import com.example.astraride.R;
import com.example.astraride.models.User;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {

    EditText name, email, phoneNo, password, address, confirm_password;
    Button btnUpdate, btnUpload, btnEdit, btnDelete;
    ImageView dp;
    DatabaseReference dbf;
    User user;
    Uri imageUri;
    boolean done = false;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Get Current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        phoneNo = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        btnUpdate = findViewById(R.id.btn_update);
        btnUpload = findViewById(R.id.btn_upload);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete_account);
        dp = findViewById(R.id.dp);


        //Loading screen
        ProgressDialog pd = new ProgressDialog(EditProfile.this);
        pd.setMessage("Loading...");
        pd.show();

        //Get userID from intent
        Intent intent = getIntent();
        String id = intent.getExtras().getString("userID");


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
                    user.setUserImage(snapshot.child("userImage").getValue().toString());
                    Glide.with(EditProfile.this).load(user.getUserImage()).circleCrop().into(dp);

                    user.setPassword(snapshot.child("password").getValue().toString());
                    user.setUserType(snapshot.child("userType").getValue().toString());

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

                boolean check = true;

                if (TextUtils.isEmpty(name.getText().toString())) {
                    check = false;
                    Toast.makeText(getApplicationContext(), "Name Text Field is empty", Toast.LENGTH_SHORT).show();
                    name.requestFocus();
                }else if (TextUtils.isEmpty(phoneNo.getText().toString())) {
                    check = false;
                    Toast.makeText(getApplicationContext(), "Phone No. Text Field is empty", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else if (phoneNo.getText().toString().trim().length() != 10) {
                    check = false;
                    Toast.makeText(getApplicationContext(), "Invalid Phone No.", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                }else if (TextUtils.isEmpty(address.getText().toString())) {
                    check = false;
                    Toast.makeText(getApplicationContext(), "Address Text Field is empty", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                } else {
                    if (check == true) {

                        pd.setMessage("Uploading...");
                        pd.show();


                        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.hasChildren()) {

                                    user.setName(name.getText().toString().trim());
                                    user.setEmail(email.getText().toString().trim());
                                    user.setPhoneNo(phoneNo.getText().toString().trim());
                                    user.setAddress(address.getText().toString().trim());

                                    //Upload image
                                    if (imageUri != null) {
                                        StorageReference stref = FirebaseStorage.getInstance().getReference().child("Profile_images")
                                                .child(id);

                                        stref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                stref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String url = uri.toString();
                                                        user.setUserImage(url);
                                                        Log.d("Image", url);

                                                        //Save all data in database
                                                        dbf.setValue(user);
                                                        pd.cancel();
                                                        reverseEdit();
                                                        Toast.makeText(EditProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        dbf.setValue(user);
                                        pd.cancel();
                                        reverseEdit();
                                        Toast.makeText(EditProfile.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("DBError", error.getMessage());
                            }
                        });
                    }
                }
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

        //requestCode ==  2404
        if(resultCode == Activity.RESULT_OK){
            imageUri = data.getData();
            Glide.with(EditProfile.this).load(imageUri).circleCrop().into(dp);

        }
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    //Get image extension
    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mp = MimeTypeMap.getSingleton();
        return mp.getExtensionFromMimeType(cr.getType(uri));
    }

    public void edit(View view){
        btnEdit.setVisibility(View.GONE);
        btnUpload.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.VISIBLE);

        name.setFocusableInTouchMode(true);
        address.setFocusableInTouchMode(true);
        phoneNo.setFocusableInTouchMode(true);
    }

    public void reverseEdit(){
        btnEdit.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);

        name.setFocusable(false);
        phoneNo.setFocusable(false);
        address.setFocusable(false);
    }

    //Delete account
    public void deleteAccount(View view){

        dbf.removeValue();
        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditProfile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Account can't be deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

}