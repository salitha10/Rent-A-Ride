package com.example.astraride.ui.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.astraride.R;
import com.example.astraride.models.Order;
import com.example.astraride.models.Review;
import com.example.astraride.models.User;
import com.example.astraride.ui.reviews.AddReview;
import com.example.astraride.ui.reviews.AllReviews;
import com.example.astraride.ui.reviews.ReviewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyOrders extends Fragment {

    DatabaseReference dbf, dbfu;
    RecyclerView rec;
    ArrayList<Order> orderLst;
    String currentUser;
    Order od;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_orders, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        orderLst = new ArrayList<>();

        dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child("Goa7MGXYe9guQoE6m8cF0rXItq93");
        rec = view.findViewById(R.id.myOrdersRecycle);

        //Add to recycleview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rec.setLayoutManager(linearLayoutManager);
        rec.setHasFixedSize(true);

        //Get data
        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        //Save data in model
                        od = snap.getValue(Order.class);
                        orderLst.add(od);

                    }

                    Log.d("itmID", Integer.toString(orderLst.size()));
                    OrderAdapter orderAdapter = new OrderAdapter(getContext(), orderLst);
                    rec.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

        return view;
    }

}
