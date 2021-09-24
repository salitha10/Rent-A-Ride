package com.example.astraride.ui.products;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.astraride.R;
import com.example.astraride.models.Item;
import com.example.astraride.ui.reviews.AddReview;
import com.example.astraride.ui.reviews.AllReviews;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    ArrayList<Item> itemList = new ArrayList<>();
    Context context;

    public ItemRecyclerViewAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.inventory_item_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Display values
        holder.itemName.setText(itemList.get(position).getTitle());
        holder.itemLocation.setText(itemList.get(position).getTitle());
        holder.itemPrice.setText("Rs." + itemList.get(position).getTitle());
        Glide.with(holder.itemImage.getContext()).load(itemList.get(position).getItemImage()).into(holder.itemImage);


        //Handle clicks
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewItem.class);
                intent.putExtra("itemID", itemList.get(position).getItemID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public CardView cardView;
        public TextView itemLocation, itemPrice, itemName;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image);
            itemLocation = itemView.findViewById(R.id.location);
            itemPrice = itemView.findViewById(R.id.price);
            itemName = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.ItemCard);
        }
    }
}
