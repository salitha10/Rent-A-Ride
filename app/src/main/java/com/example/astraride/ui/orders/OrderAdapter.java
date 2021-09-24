package com.example.astraride.ui.orders;

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
import com.example.astraride.models.Order;
import com.example.astraride.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.Viewholder>{

    private Context context;
    private ArrayList<Order> orderList;
    private ArrayList<User> userData;
    DatabaseReference dbfu, dbf;
    String curretUser;
    Order order;


    // Constructor
    public OrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public OrderAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_card, parent, false);
        context = parent.getContext();
        return new OrderAdapter.Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout

        //User loged in
        curretUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Order order = orderList.get(position);
        holder.orderNameTV.setText(order.getName());
        String date[] = order.getOrderDate().split(" "); //DB date contains time
        holder.orderDateTV.setText(date[0]);

        //Get user data
        dbfu = FirebaseDatabase.getInstance().getReference().child("Items").child(order.getItemID());

        dbfu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {

                    //Get item image
                    Glide.with(context).load(snapshot.child("itemImage").getValue()).into(holder.orderImg);

                    //Add onclick listener
                    holder.card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                //Start edit order
                                Intent intent = new Intent(context, EditOrder.class);
                                intent.putExtra("order", orderList.get(position));
                                context.startActivity(intent);
                        }
                    });

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
        return orderList.size();
    }


    // Initialize views
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView orderImg;
        private TextView orderNameTV, orderDateTV;
        CardView card;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            orderImg = itemView.findViewById(R.id.orderImage);
            orderNameTV = itemView.findViewById(R.id.orderName);
            orderDateTV = itemView.findViewById(R.id.orderDate);
            card = itemView.findViewById(R.id.orderCard);
        }
    }

}
