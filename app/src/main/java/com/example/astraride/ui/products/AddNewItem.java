package com.example.astraride.ui.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.number.IntegerWidth;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.astraride.R;
import com.example.astraride.models.Item;
import com.example.astraride.ui.profile.EditProfile;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNewItem extends AppCompatActivity {

    SliderView sliderView;
    SliderLayout sliderLayout;
    EditText title, brand, category, capacity, location, fee, details, color;
    Item item;
    Uri imageUri;
    ImageView image;
    Button save;
    DatabaseReference dbf;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);


        //Initialize variables
        image = findViewById(R.id.Item_image);
        save = findViewById(R.id.btnSave);
        title = findViewById(R.id.editTextTitle);
        brand = findViewById(R.id.editTextBrand);
        category = findViewById(R.id.editTextCategory);
        capacity = findViewById(R.id.editTextCapacity);
        color = findViewById(R.id.editTextBodyColor);
        location = findViewById(R.id.editTextTextLocation);
        fee = findViewById(R.id.editTextFee);
        details = findViewById(R.id.editTextDetails);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    public void captureImage(View view) {
        ImagePicker.with(AddNewItem.this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
            Glide.with(AddNewItem.this).load(imageUri).into(image);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    public void Save(View view) {

        ProgressDialog pd = new ProgressDialog(AddNewItem.this);
        pd.setMessage("Uploading...");
        pd.show();
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String time = Long.toString(System.currentTimeMillis());
        item = new Item();

        dbf = FirebaseDatabase.getInstance().getReference().child("Items");
        String id = dbf.push().getKey();

        //Get data
        item.setTitle(title.getText().toString().trim());
        item.setBrand(brand.getText().toString().trim());
        item.setCategory(category.getText().toString().trim());
        item.setCapacity(capacity.getText().toString().trim());
        item.setLocation(location.getText().toString().trim());
        item.setRentalFee(fee.getText().toString().trim());
        item.setColor(color.getText().toString().trim());
        item.setDetails(details.getText().toString().trim());
        item.setDatePosted(date);
        item.setPostedBy(currentUser.toString());
        item.setItemID(id);


        //Save to database
        Intent intent = new Intent(this, Inventory.class);

        if (imageUri != null) {
            StorageReference stref = FirebaseStorage.getInstance().getReference().child("Item_images")
                    .child(id);

            stref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    stref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            item.setItemImage(url);
                            Log.d("Image", url);

                            //Save all data in database
                            dbf.child(id).setValue(item);
                            pd.cancel();
                            Toast.makeText(AddNewItem.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            item.setItemImage("");
            dbf.child(id).setValue(item);
            pd.cancel();
            Toast.makeText(AddNewItem.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }
}