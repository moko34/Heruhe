package com.example.heruhe.ui.dashboard.posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.R;

import java.util.ArrayList;

public class PostsFragment extends Fragment implements PostMetaDataAdapter.OnPostClick{



    public static PostsFragment newInstance (ArrayList<PostMetaData> dataArrayList){
        PostsFragment fragment = new PostsFragment();
        fragment.metaDataArrayList=dataArrayList;

        return fragment;
    }


    private ArrayList<PostMetaData> metaDataArrayList;

    public PostsFragment (){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scrolling, container, false);

        if(view != null){
            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            PostMetaDataAdapter adapter = new PostMetaDataAdapter(this,metaDataArrayList,requireContext());
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("JSON",metaDataArrayList.size() + "ttt");


    }



    @Override
    public void onClick(PostMetaData postMetaData) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}