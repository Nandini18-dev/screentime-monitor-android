package com.nandini.screentime.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nandini.screentime.R;
import com.nandini.screentime.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentFor(item.getItemId());
            if (fragment == null) return false;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private Fragment fragmentFor(int itemId) {
        if (itemId == R.id.nav_dashboard) {
            return new DashboardFragment();
        } else if (itemId == R.id.nav_apps) {
            return new AppListFragment();
        } else if (itemId == R.id.nav_history) {
            return new HistoryFragment();
        } else if (itemId == R.id.nav_settings) {
            return new SettingsFragment();
        }
        return null;
    }
}
