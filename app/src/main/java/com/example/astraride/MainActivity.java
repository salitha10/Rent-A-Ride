package com.example.astraride;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.astraride.models.User;
import com.example.astraride.ui.profile.EditProfile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    DatabaseReference dbf;
    String name = "Name", email = "email@email.com", imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Gert user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        dbf = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        //Set navigation headers
        View headerView = navigationView.getHeaderView(0);
        ImageView drawerImage = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.profile_name);
        TextView drawerAccount = (TextView) headerView.findViewById(R.id.profile_email);

        //Get data from database
        dbf.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    //Display data
                    name = snapshot.child("name").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    imgUri = snapshot.child("userImage").getValue().toString();

                    //Set values
                    Glide.with(MainActivity.this).load(imgUri).circleCrop().into(drawerImage);
                    drawerUsername.setText(name);
                    drawerAccount.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DBError", error.getMessage());
            }
        });


        drawerUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfile.class);
                intent.putExtra("userID", user.getUid());
                startActivity(intent);
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_Inventory, R.id.nav_orders)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}