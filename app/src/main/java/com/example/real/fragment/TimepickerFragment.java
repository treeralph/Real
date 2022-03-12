package com.example.real.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.example.real.Callback;
import com.example.real.R;

public class TimepickerFragment extends Fragment {

    private Callback callback;
    public TimepickerFragment(Callback callback) {
        this.callback = callback;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timepicker, container, false);
        callback.OnCallback(view);
        return view;
    }
}