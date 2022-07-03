package com.example.heruhe.ui.search;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.heruhe.MainActivity;
import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentSearchBinding;

import com.example.heruhe.ui.dashboard.UserDashboardFragment;
import com.example.heruhe.ui.search.placeholder.PlaceholderContent;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A fragment representing a list of Items.
 */
public class SearchItemFragment extends Fragment {

    private FragmentSearchBinding binding;
    private final QueryFragment queryFragment = QueryFragment.newInstance(this::navigateToDashboard);
    private MenuItem searchMenuItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchItemFragment newInstance(int columnCount) {

        return new SearchItemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding= null;
       // setHasOptionsMenu(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu,menu);

        searchMenuItem = menu.findItem(R.id.home_search_menu);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //if(!s.equals(""))
                searchParseUser(s);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentSearchBinding.inflate(inflater,container,false);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(binding.searchFragmentContainer.getId(),queryFragment);
        transaction.commit();


        return binding.getRoot();
    }


    /**
     * Searches the back4app server for users with username that contain @param keyword
     * @param keyword search keyword
     */
    private void searchParseUser(String keyword){
        String acceptedKeyword = keyword.trim().toLowerCase();
        ArrayList<PlaceholderContent.UserSearchItem> matchedUsers=new ArrayList<>();
        if(!acceptedKeyword.equals("")){
        ParseUser.getQuery().whereContains(MainActivity.USERNAME_KEY,acceptedKeyword).findInBackground((objects, e) -> {
            if(e == null){
                if(binding != null){
                if(objects.size() > 0){
                    for (ParseUser user:objects){
                        ParseFile profileImage = user.getParseFile(UserDashboardFragment.PROFILE_IMAGE_KEY);
                        if(!Objects.equals(user.getUsername(), ParseUser.getCurrentUser().getUsername()))
                        matchedUsers.add(new PlaceholderContent.UserSearchItem(user.getUsername(),
                                profileImage != null ? profileImage.getUrl():null,
                                user.getUpdatedAt()));
                    }
                  }
                    PlaceholderContent.setUserSearchItems(matchedUsers);
                    queryFragment.updateAdapter();
                }
            }else {
                e.printStackTrace();
            }
          });
        }
        else {

             queryFragment.showNoPostPage();
        }

    }

    /**
     * Navigates the user to the user dashboard fragment
     * @param username the username of the user
     */
    public void navigateToDashboard(String username){
        searchMenuItem.setVisible(false);
        if(binding != null){
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(binding.searchFragmentContainer.getId(),UserDashboardFragment.newInstance(username));
            transaction.commit();
        }
    }
}