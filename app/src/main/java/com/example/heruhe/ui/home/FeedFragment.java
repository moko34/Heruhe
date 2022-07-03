package com.example.heruhe.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heruhe.AuthActivity;
import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentFeedBinding;
import com.example.heruhe.ui.dashboard.posts.Post;
import com.example.heruhe.utils.HelperMethods;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private HomeFragmentAdapter adapter;
    private FragmentFeedBinding binding;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance() {

        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentFeedBinding.inflate(inflater,container,false);


        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.bottomMargin = 100;
        binding.homeFragmentConstraint.setLayoutParams(layoutParams);
        binding.homeLoading.setVisibility(View.VISIBLE);
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<ParseUser> following = new ArrayList<>(Objects.requireNonNull(currentUser.getList(AuthActivity.FOLLOWING)));
        Log.i("COUGH","followers" + following.size());
        if(following.size()>0){
            HelperMethods.fetchPosts(following, posts -> {
                if(binding != null) {
                    binding.homeLoading.setVisibility(View.GONE);
                    adapter = new HomeFragmentAdapter(posts, requireContext(), requireActivity());
                    binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.homeRecyclerView.setAdapter(adapter);
                }
            });
        }
        else {
            binding.homeLoading.setText(getString(R.string.follow_client));
        }


        return binding.getRoot();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}