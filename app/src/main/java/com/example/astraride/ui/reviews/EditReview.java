package com.example.astraride.ui.reviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.astraride.R;
import com.example.astraride.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditReview extends AppCompatActivity {


    //Declare variables
    RatingBar rating;
    EditText comments;
    ImageView thumbnail;
    TextView item;
    DatabaseReference dbf;
    String currentUser;
    Button done, delete;
    String itemID, reviewID, comment;
    float ratingVal;
    Review review;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        review = new Review();

        //Get values from intent
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemID");
        reviewID = intent.getStringExtra("reviewID");
        comment = intent.getStringExtra("comment");
        ratingVal = intent.getFloatExtra("rating", 0);


        //Initialize components
        comments = (EditText) findViewById(R.id.editTextProductComments);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        thumbnail = (ImageView) findViewById(R.id.orderImage);
        item = (TextView) findViewById(R.id.orderName);
        done = (Button) findViewById(R.id.button);
        delete = (Button) findViewById(R.id.button2);

        //Display data
        rating.setRating(ratingVal);
        comments.setText(comment);


        //Load data from database
        dbf = FirebaseDatabase.getInstance().getReference().child("Items").child(itemID);
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    Glide.with(EditReview.this).load(snapshot.child("itemImage").getValue().toString()).into(thumbnail);
                    item.setText(snapshot.child("title").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

        //Delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Confirm delete
                AlertDialog.Builder alert = new AlertDialog.Builder(EditReview.this);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        try {
                            dbf = FirebaseDatabase.getInstance().getReference().child("Reviews").child(reviewID);
                            dbf.removeValue(); //Delete

                            //Pass item ID
                            Intent intent = new Intent(EditReview.this, AllReviews.class);
                            intent.putExtra("ItemID", itemID);
                            startActivity(intent);

                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    //Save review
    public void saveReview(View view) {

        try {
            //Save vales in model
            review.setRating(rating.getRating());
            review.setComments(comments.getText().toString());
            review.setItemId(itemID);
            review.setReviewerId(currentUser);

            //Save to db

            dbf = FirebaseDatabase.getInstance().getReference().child("Reviews");
            String id = dbf.push().getKey(); //Get key from database
            review.setReviewID(id);
            dbf.child(id).setValue(review); //send model to database
            Toast.makeText(getApplicationContext(), "Review Added", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditReview.this, AllReviews.class);
            intent.putExtra("ItemID", itemID);
            startActivity(intent);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

}