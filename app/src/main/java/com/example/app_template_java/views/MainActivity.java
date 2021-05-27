package com.example.app_template_java.views;

import android.os.Bundle;

import com.example.app_template_java.databinding.ActMainBinding;
import com.example.app_template_java.injects.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EActivity
public class MainActivity extends BaseActivity {

    protected ActMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void subscribeObservers() {

    }

    @Override
    public void unsubscribeObservers() {

    }
}