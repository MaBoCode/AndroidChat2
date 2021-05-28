package com.example.androidchat2.views;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.example.androidchat2.databinding.ActMainBinding;
import com.example.androidchat2.injects.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EActivity
public class MainActivity extends BaseActivity {

    protected ActMainBinding binding;

    protected MainActivityViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }

    @Override
    public void subscribeObservers() {

    }

    @Override
    public void unsubscribeObservers() {

    }
}