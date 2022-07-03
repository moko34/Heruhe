package com.example.heruhe.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.heruhe.databinding.FragmentQueryBinding;

import com.example.heruhe.ui.search.placeholder.PlaceholderContent;


import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QueryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueryFragment extends Fragment {

    public interface OnSearchItemSelected{
        void onClick(String username);
    }

    private OnSearchItemSelected onSearchItemSelected;
    private ArrayList<PlaceholderContent.UserSearchItem> matchedItems;
    private FragmentQueryBinding binding;


    public QueryFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QueryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QueryFragment newInstance(OnSearchItemSelected selected) {
        QueryFragment fragment = new QueryFragment();
        fragment.onSearchItemSelected=selected;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         binding = FragmentQueryBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment

         matchedItems = new ArrayList<>(PlaceholderContent.ITEMS);


        if(matchedItems.size() > 0){
            binding.searchNoPost.setVisibility(View.GONE);
            binding.list.setVisibility(View.VISIBLE);
            binding.list.setAdapter(new MySearchItemRecyclerViewAdapter(PlaceholderContent.ITEMS,requireContext(),
                    QueryFragment.this::navigateToUserDashboard));
        }else {
            binding.list.setVisibility(View.GONE);
            binding.searchNoPost.setVisibility(View.VISIBLE);
         }
        return binding.getRoot();
    }


    /**
     * Updates the recycler view adapter with the current users matching the searched
     * username
     */
    public void updateAdapter(){

        if(binding != null ){
            matchedItems = new ArrayList<>(PlaceholderContent.ITEMS);
            if(matchedItems.size() > 0){
                binding.searchNoPost.setVisibility(View.GONE);
                binding.list.setVisibility(View.VISIBLE);
                if(getContext() != null)
                binding.list.setAdapter(new MySearchItemRecyclerViewAdapter(PlaceholderContent.ITEMS,requireContext(),
                        QueryFragment.this::navigateToUserDashboard));
            }else {
                showNoPostPage();
            }
        }
    }

    /**
     * Updates the UI with a result of an empty keyword
     */
    public void showNoPostPage(){
        if(binding != null) {
            binding.list.setVisibility(View.GONE);
            binding.searchNoPost.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Navigates the user to the user dashboard fragment
     * @param username
     */
    private void navigateToUserDashboard(String username){
        onSearchItemSelected.onClick(username);
       // navigateToDashboard(username);
    }

}