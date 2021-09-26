package com.example.astraride.ui.other;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.astraride.R;

public class AboutUs extends Fragment {

    ImageView phone, web, mail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        //Init
        phone = view.findViewById(R.id.imgPhone);
        mail = view.findViewById(R.id.imgMail);
        web = view.findViewById(R.id.imgWeb);

        //Set click lstenetrs
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sliit.lk"));
                startActivity(browserIntent);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("mailto:astraride@sliit.lk"));
                    startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(getContext(), "No email client found!", Toast.LENGTH_SHORT);
                }
            }
        });

        return view;
    }
}