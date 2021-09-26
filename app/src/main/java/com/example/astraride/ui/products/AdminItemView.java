package com.example.astraride.ui.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.astraride.R;
import com.example.astraride.models.Item;
import com.example.astraride.ui.orders.Checkout;
import com.example.astraride.ui.reviews.AllReviews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminItemView extends AppCompatActivity {
    TextView brand, color, title, capacity, rental, location, category, details, review;
    Button edit;
    ProgressBar pb;
    ImageView img;
    String itemId, price;
    DatabaseReference dbf;
    String currentUser;
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_view);

        //Get item id
        Intent intent = getIntent();
        itemId = intent.getStringExtra("item");

        //Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Initialize
        brand = (TextView) findViewById(R.id.ItemViewBrand);
        color = (TextView) findViewById(R.id.ItemViewColor);
        title = (TextView) findViewById(R.id.ItemViewTitle);
        capacity = (TextView) findViewById(R.id.ItemViewCapacity);
        location = (TextView) findViewById(R.id.ItemViewLocation);
        rental = (TextView) findViewById(R.id.ItemViewPrice);
        category = (TextView) findViewById(R.id.ItemViewCategory);
        details = (TextView) findViewById(R.id.ItemViewDetails);
        review = (TextView) findViewById(R.id.ItemViewReviews);
        img = (ImageView) findViewById(R.id.imageViewItem);
        edit = (Button)findViewById(R.id.btnEdit);
        pb = findViewById(R.id.adminItmPB);


        //Goto review page
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminItemView.this, AllReviews.class);
                intent.putExtra("ItemID",itemId);
                startActivity(intent);
            }
        });

        //Go to checkout page
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminItemView.this, EditItem.class);
                intent.putExtra("item",item);
                startActivity(intent);
            }
        });

        //Load data from database
        dbf = FirebaseDatabase.getInstance().getReference().child("Items").child(itemId);

        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){

                    //Get object
                    item = snapshot.getValue(Item.class);

                    brand.setText(snapshot.child("brand").getValue().toString());
                    category.setText(snapshot.child("category").getValue().toString());
                    color.setText(snapshot.child("color").getValue().toString());
                    capacity.setText(snapshot.child("capacity").getValue().toString());
                    details.setText(snapshot.child("details").getValue().toString());
                    location.setText(snapshot.child("location").getValue().toString());
                    price = snapshot.child("rentalFee").getValue().toString();
                    rental.setText("Rs." + price + " per hour");
                    title.setText(snapshot.child("title").getValue().toString());
                    Glide.with(AdminItemView.this).load(snapshot.child("itemImage").getValue().toString()).error(R.drawable.ic_launcher_foreground).into(img);
                    pb.setVisibility(View.GONE);
                }
                else{
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}