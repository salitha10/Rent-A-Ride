package com.example.astraride.ui.products;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    DatabaseReference dbf;
    Item item;
    ArrayList<Item> itemList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Inventory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Inventory.
     */
    // TODO: Rename and change types and number of parameters
    public static Inventory newInstance(String param1, String param2) {
        Inventory fragment = new Inventory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //Get data from database
        dbf = FirebaseDatabase.getInstance().getReference().child("Items");
        itemList = new ArrayList<>();

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });


        View view = inflater.inflate(R.layout.fragment_inventory, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(itemList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}