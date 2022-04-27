package com.example.real.tool;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.example.real.Callback;
import com.example.real.MakeAuctionContentActivity;
import com.example.real.R;
import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.fragment.MapFragment;
import com.example.real.fragment.TimepickerFragment;
import com.naver.maps.map.renderer.MapRenderer;
import com.naver.maps.map.renderer.a.d;

import java.util.zip.Inflater;

public class LocationDialog extends Dialog {

    Callback callback;

    Context context;
    MapFragment mapFragment;

    public LocationDialog(@NonNull Context context, Callback callback) {
        super(context);

        this.context = context;
        this.callback = callback;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setContentView(R.layout.custom_dialog_location);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_location);

        mapFragment = new MapFragment(context, new Callback() {
            @Override
            public void OnCallback(Object object) {

            }
        }, new Callback() {
            @Override
            public void OnCallback(Object object) {
                View view = (View) object;
                CardView cardView = view.findViewById(R.id.mapFragmentCheckButton);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.OnCallback(mapFragment.getFocusAddress());
                    }
                });
            }
        });


        ViewPager viewPager = (ViewPager) findViewById(R.id.customDialogLocationViewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(((AppCompatActivity)context).getSupportFragmentManager());
        viewPagerAdapter.addItem(mapFragment);

        viewPager.setAdapter(viewPagerAdapter);
    }
}
