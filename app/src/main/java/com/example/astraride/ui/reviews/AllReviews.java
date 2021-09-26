package com.example.astraride.ui.reviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.astraride.R;
import com.example.astraride.models.Review;
import com.example.astraride.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllReviews extends AppCompatActivity {

    FloatingActionButton btn;
    ProgressBar pb;
    DatabaseReference dbf, dbfu;
    RecyclerView rec;
    String itemID;
    ArrayList<Review> rvList;
    ArrayList<User> userList;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reviews);

        //Ge data from intent
        Intent i = getIntent();
        itemID = i.getStringExtra("ItemID");

        Log.d("IItm", itemID);

        rvList = new ArrayList<Review>();

        dbf = FirebaseDatabase.getInstance().getReference().child("Reviews").child(itemID);
        rec = findViewById(R.id.allReviewsRecycle);
        pb = findViewById(R.id.progressBar);

        //Add to recycleview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rec.setLayoutManager(linearLayoutManager);
        rec.setHasFixedSize(true);


        //Get data
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {

                    for (DataSnapshot snap : snapshot.getChildren()) {

                            Review rv = snap.getValue(Review.class);
                            rvList.add(rv);
                            Log.e("rev", rv.getReviewID());

                    }

                    ReviewAdapter ra = new ReviewAdapter(getApplicationContext(), rvList);
                    rec.setAdapter(ra);
                    ra.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                }
                else{
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No reviews to show", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

        Log.d("rv", Integer.toString(rvList.size()));

        btn = (FloatingActionButton) findViewById(R.id.btnAddReview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AllReviews.this, AddReview.class);
                intent.putExtra("itemId",itemID);
                startActivity(intent);

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