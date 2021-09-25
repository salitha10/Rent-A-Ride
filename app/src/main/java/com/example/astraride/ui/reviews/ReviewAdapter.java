package com.example.astraride.ui.reviews;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.astraride.R;
import com.example.astraride.models.Review;
import com.example.astraride.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Viewholder> {

    private Context context;
    private ArrayList<Review> reviewModelArrayList;
    private ArrayList<User> userData;
    DatabaseReference dbfu;
    String curretUser;


    // Constructor
    public ReviewAdapter(Context context, ArrayList<Review> reviewModelArrayList) {
        this.context = context;
        this.reviewModelArrayList = reviewModelArrayList;
    }

    @NonNull
    @Override
    public ReviewAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card_view, parent, false);
        context = parent.getContext();
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout

        curretUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Review rev = reviewModelArrayList.get(position);
        holder.reviewRatingTV.setText("" + rev.getRating());
        holder.reviewCommentTV.setText(rev.getComments());
        //Get user data
        dbfu = FirebaseDatabase.getInstance().getReference().child("Users").child(rev.getReviewerId());

        dbfu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(context).load(user.getUserImage()).circleCrop()
                            .error(R.drawable.ic_launcher_foreground).into(holder.reviewImg);
                    holder.reviewNameTV.setText(user.getName());

                    //Add onclick listener
                    if (user.getUserId().equals(curretUser)) {
                        holder.card.setCardBackgroundColor(0xBFBABA);
                        holder.card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Start edit review
                                Intent intent = new Intent(context, EditReview.class);
                                intent.putExtra("reviewID", reviewModelArrayList.get(position).getReviewID());
                                intent.putExtra("rating", reviewModelArrayList.get(position).getRating());
                                intent.putExtra("itemID", reviewModelArrayList.get(position).getItemId());
                                intent.putExtra("comment", reviewModelArrayList.get(position).getComments());

                                context.startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });
    }



    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return reviewModelArrayList.size();
    }


    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView reviewImg;
        private TextView reviewNameTV, reviewRatingTV, reviewCommentTV;
        CardView card;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reviewImg = itemView.findViewById(R.id.idIVImage);
            reviewNameTV = itemView.findViewById(R.id.idTVName);
            reviewRatingTV = itemView.findViewById(R.id.idTVRating);
            reviewCommentTV = itemView.findViewById(R.id.idTVComment);
            card = itemView.findViewById(R.id.reviewCard);
        }
    }
}
