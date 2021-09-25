package com.example.astraride.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.astraride.R;
import com.example.astraride.models.Item;
import com.example.astraride.ui.products.AddNewItem;
import com.example.astraride.ui.products.ItemRecyclerViewAdapter;
import com.example.astraride.ui.products.SliderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    DatabaseReference dbf, dbfs;
    Item item;
    ArrayList<Item> itemList;
    SliderView sliderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setMessage("Loading...");
        pd.show();


        //Get data from database
        dbf = FirebaseDatabase.getInstance().getReference().child("Items");
        itemList = new ArrayList<>();


        //Slider
        sliderView = view.findViewById(R.id.slider);

        List<String> urls = new ArrayList<>();
        dbfs = FirebaseDatabase.getInstance().getReference().child("promos");
        dbfs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot sp: snapshot.getChildren()){
                    urls.add(sp.child("url").getValue().toString());
                }

                SliderAdapter sliderAdapter = new SliderAdapter(urls, view.getContext());
                Log.d("Count", Integer.toString(sliderAdapter.getCount()));
                sliderView.setSliderAdapter(sliderAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();


        //Setup recyclerview
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.homeRecycler);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2); //Use grid Laypur manager
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);



        dbf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {

                    //Retrieve all data
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        item = ds.getValue(Item.class);
                        itemList.add(item);

                    }

                    HomeViewAdapter adapter = new HomeViewAdapter(itemList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DBError", error.getMessage());
            }
        });

        pd.dismiss();
        return view;
    }

}

