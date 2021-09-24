package com.example.astraride.ui.products;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.denzcoskun.imageslider.models.SlideModel;

import com.example.astraride.R;
import com.example.astraride.ui.profile.EditProfile;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewProduct#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewProduct extends Fragment {

    Context context;

    public AddNewProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = inflater.getContext();
        return inflater.inflate(R.layout.fragment_add_new_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        sliderView = getView().findViewById(R.id.imageSlider);
//
//        int[] images = {
//                R.drawable.city_girl_rafiki,
//                R.drawable.ic_menu_camera,
//                R.drawable.city_girl_rafiki,
//                R.drawable.ic_menu_camera};
//        SliderAdapter sliderAdapter = new SliderAdapter(images);
//
//        sliderView.setSliderAdapter(sliderAdapter);
//        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
//        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
//        sliderView.startAutoCycle();


    }
}