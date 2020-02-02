package com.fikretserkan.unibook.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fikretserkan.unibook.R;
import com.fikretserkan.unibook.Storage;
import com.fikretserkan.unibook.views.MainRecyclerView;

public class ProfileFragment extends Fragment {

    RecyclerView books_rv;
    LinearLayoutManager layout_manager;
    MainRecyclerView adapter;

    IntentFilter mIntentFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        books_rv = view.findViewById(R.id.my_books_rv);

        layout_manager = new LinearLayoutManager(getActivity());
        layout_manager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new MainRecyclerView(getActivity(), Storage.mybookList);

        books_rv.setLayoutManager(layout_manager);
        books_rv.setAdapter(adapter);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("UPDATE_RV");

        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);

        return view;
    }

    public BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            books_rv.setAdapter(adapter);
        }
    };
}