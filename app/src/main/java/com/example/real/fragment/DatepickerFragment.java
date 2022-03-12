package com.example.real.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.adapter.ViewPagerAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class DatepickerFragment extends Fragment{

    private Callback callback;

    public DatepickerFragment(Callback callback) {
        // Required empty public constructor
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datepicker, container, false);
        callback.OnCallback(view);
        return view;
    }
}