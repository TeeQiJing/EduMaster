package com.practical.edumasters.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;  // Import Fragment class

import android.os.Bundle;
import android.util.Log;

import com.practical.edumasters.R;
import com.practical.edumasters.databinding.ActivityMainBinding;
import com.practical.edumasters.fragments.ChatFragment;
import com.practical.edumasters.fragments.CommunityFragment;
import com.practical.edumasters.fragments.LeaderboardFragment;
import com.practical.edumasters.fragments.LearnFragment;
import com.practical.edumasters.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    // Declare binding variable with the correct type
    private ActivityMainBinding binding;

    private ProfileFragment profileFragment;
    private LearnFragment learnFragment;
    private CommunityFragment communityFragment;
    private LeaderboardFragment leaderboardFragment;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize fragments
        profileFragment = new ProfileFragment();
        learnFragment = new LearnFragment();
        communityFragment = new CommunityFragment();
        leaderboardFragment = new LeaderboardFragment();
        chatFragment = new ChatFragment();

        // Load the default fragment
        loadFragment(learnFragment);

        // Handle BottomNavigation item clicks
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d("Navigation", "Selected item ID: " + itemId); // Add this line to debug
            if (itemId == R.id.nav_learn) {
                loadFragment(learnFragment);
                return true;
            } else if (itemId == R.id.nav_community) {
                loadFragment(communityFragment);
                return true;
            } else if (itemId == R.id.nav_leaderboard) {
                loadFragment(leaderboardFragment);
                return true;
            } else if (itemId == R.id.nav_chat) {
                loadFragment(chatFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(profileFragment);
                return true;
            }
            return false;
        });

    }

    // Method to load fragments dynamically
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
