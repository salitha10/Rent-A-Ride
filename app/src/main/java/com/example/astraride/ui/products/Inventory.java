package com.example.astraride.ui.products;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.astraride.R;
import com.example.astraride.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Inventory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Inventory extends Fragment {

    DatabaseReference dbf;
    Item item;
    ArrayList<Item> itemList;

    public Inventory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Get data from database
        dbf = FirebaseDatabase.getInstance().getReference().child("Items");
        itemList = new ArrayList<>();


        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        //Setup recyclerview
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {

                    //Retrieve all data
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        Item item = new Item();
                        item.setItemID(dataSnapshot.child("itemID").getValue().toString());
                        item.setBrand(dataSnapshot.child("brand").getValue().toString());
                        item.setCategory(dataSnapshot.child("category").getValue().toString());
                        item.setColor(dataSnapshot.child("color").getValue().toString());
                        item.setCapacity(dataSnapshot.child("capacity").getValue().toString());
                        item.setDatePosted(dataSnapshot.child("datePosted").getValue().toString());
                        item.setDetails(dataSnapshot.child("details").getValue().toString());
                        item.setItemImage(dataSnapshot.child("itemImage").getValue().toString());
                        item.setLocation(dataSnapshot.child("location").getValue().toString());
                        item.setRentalFee(dataSnapshot.child("rentalFee").getValue().toString());
                        item.setTitle(dataSnapshot.child("title").getValue().toString());

                        itemList.add(item);
                        Log.d("title", item.getTitle());

                    }

                    ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(itemList);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.addNew);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), AddNewItem.class);
                startActivity(intent);
            }
        });

        return view;
    }

}