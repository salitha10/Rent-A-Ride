package com.example.astraride.ui.products;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.astraride.R;
import com.example.astraride.models.Item;

import java.util.ArrayList;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>{

    ArrayList<Item> itemList = new ArrayList<>();

    public ItemRecyclerViewAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.inventory_item_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final ArrayList myListData = listdata[position];
//        holder.itemImage.setImageURI(Uri.parse(itemList.get(position).getItemImage()));
        holder.itemtxt.setText("Hello");

//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(view.getContext(),"click on item: "+ position,Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public LinearLayout linearLayout;
        public TextView title, brand, category, capacity, location, fee, details, color, itemtxt;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage=itemView.findViewById(R.id.itemImg);
            itemtxt=itemView.findViewById(R.id.itemName);
            linearLayout=itemView.findViewById(R.id.layout_id);
        }
    }
}
