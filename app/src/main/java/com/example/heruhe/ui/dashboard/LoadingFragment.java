package com.example.heruhe.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentLoadingBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoadingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoadingFragment extends Fragment {

    private FragmentLoadingBinding binding;


    public LoadingFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LoadingFragment newInstance() {


        return  new LoadingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Animation fade_animation = AnimationUtils.loadAnimation(requireContext(),R.anim.fade_load_anim);
        int childrenCount =  binding.infoList.getChildCount();

          for(int i = 0;i<childrenCount;i++){
              binding.infoList.getChildAt(i).startAnimation(fade_animation);
          }
          binding.profileImage.startAnimation(fade_animation);
          binding.imgCover.startAnimation(fade_animation);
          binding.line.startAnimation(fade_animation);
          binding.postFragmentContainer.startAnimation(fade_animation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.imgCover.clearAnimation();
        binding.postFragmentContainer.clearAnimation();
        binding.profileImage.clearAnimation();
        binding.line.clearAnimation();
        int childrenCount = binding.infoList.getChildCount();
        for(int i = 0;i<childrenCount;i++){
            binding.infoList.getChildAt(i).clearAnimation();
        }
        binding=null;
    }
}