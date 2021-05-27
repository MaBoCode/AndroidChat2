package com.example.app_template_java.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.app_template_java.R;
import com.example.app_template_java.databinding.FrgSplashscreenBinding;
import com.example.app_template_java.injects.base.BaseFragment;

import org.androidannotations.annotations.EFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class SplashScreenFragment extends BaseFragment {

    protected FrgSplashscreenBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FrgSplashscreenBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        new Handler().postDelayed(() -> Navigation.findNavController(binding.getRoot()).navigate(R.id.to_main_fragment), 300);
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
