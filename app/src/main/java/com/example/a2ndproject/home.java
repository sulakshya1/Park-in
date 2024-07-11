package com.example.a2ndproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a2ndproject.databinding.ActivityHomeBinding;

import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class home extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;//yo1
    private static final int TIME_INTERVAL = 2000;//yo2

    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replacefragment(new HomeFragment());

        EdgeToEdge.enable(this);

        binding.bottomNavigationView.setOnItemSelectedListener(item->{
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                replacefragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.menu_nav) {
                replacefragment(new navigationFragment());
                return true;
            } else if (itemId == R.id.menu_settings) {
                replacefragment(new settingsFragment());
                return true;
            }
            return false;
        });
        //yo3 hamro double press back ko code
        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAndRemoveTask();
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(home.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                // Reset doubleBackToExitPressedOnce after TIME_INTERVAL milliseconds
                //yo chai ide le mageko mileko cha chune haina
                new Handler() {
                    public void postDelayed(Runnable runnable, int timeInterval) {
                    }

                    @Override
                    public void publish(LogRecord record) {

                    }

                    @Override
                    public void flush() {

                    }

                    @Override
                    public void close() throws SecurityException {

                    }
                }.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, TIME_INTERVAL);
            }
        });
    }
    public void replacefragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}