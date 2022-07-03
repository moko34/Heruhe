package com.example.heruhe.ui.search.placeholder;

import android.graphics.Bitmap;

import com.example.heruhe.utils.HelperMethods;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public  static  List<UserSearchItem> ITEMS = new ArrayList<>();

    public static void setUserSearchItems(ArrayList<UserSearchItem> searchItems){
        ITEMS=searchItems;
    }

    /**
     * A map of sample (placeholder) items, by ID.
     */
    //public static final Map<String, UserSearchItem> ITEM_MAP = new HashMap<>();

   // private static final int COUNT = ITEMS.size();




    /**
     * A placeholder item representing a piece of content.
     */
    public static class UserSearchItem {
        private final String username;
        private final String profileImageUrl;
        private final String[] lastUpdated;


        public UserSearchItem(String username, @Nullable String profileImageUrl, Date lastUpdated) {
            this.username = username;
            this.profileImageUrl = profileImageUrl;
            this.lastUpdated = HelperMethods.getDateString(lastUpdated);
        }

        public String getUsername() {
            return username;
        }

        public String getProfileImage() {
            return profileImageUrl;
        }

        public String[] getLastUpdated() {
            return lastUpdated;
        }
    }
}