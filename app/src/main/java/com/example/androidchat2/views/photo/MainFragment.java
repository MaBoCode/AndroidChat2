package com.example.androidchat2.views.photo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.androidchat2.core.photo.Photo;
import com.example.androidchat2.databinding.FrgMainBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.photo.utils.PhotoAdapter;
import com.example.androidchat2.views.utils.RecyclerViewUtils;

import org.androidannotations.annotations.EFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class MainFragment extends BaseFragment {

    protected FrgMainBinding binding;

    protected MainFragmentViewModel viewModel;

    protected PhotoAdapter photoAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgMainBinding.inflate(inflater, container, false);

        binding.lyShimmer.setShimmer(RecyclerViewUtils.getShimmer().build());

        setupUserAdapter();

        return binding.getRoot();
    }

    public void setupUserAdapter() {
        this.photoAdapter = new PhotoAdapter(new Photo.PhotoDiff());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        RecyclerViewUtils.setupAdapter(binding.userRecyclerView, layoutManager, photoAdapter);
        photoAdapter.addSkeletonItems(18);
    }

    @Override
    public void onStart() {
        super.onStart();

        viewModel.getPhotos();
    }

    @Override
    public void initViewModels() {
        viewModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        viewModel.photosLiveData.observe(getViewLifecycleOwner(), photos -> {
            photoAdapter.submitList(photos);
            new Handler(Looper.getMainLooper()).postDelayed(() -> binding.lyShimmer.hideShimmer(), 1000);
        });
        viewModel.loadingLiveData.observe(getViewLifecycleOwner(), loadingStatus -> {
            if (loadingStatus == BaseViewModel.LoadingStatus.LOADING) {
                binding.lyShimmer.startShimmer();
            } else {
                binding.lyShimmer.stopShimmer();
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        viewModel.photosLiveData.removeObservers(getViewLifecycleOwner());
        viewModel.loadingLiveData.removeObservers(getViewLifecycleOwner());
    }
}
