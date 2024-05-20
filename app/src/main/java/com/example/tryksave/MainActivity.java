package com.example.tryksave;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fragmentManager;
    Toolbar toolbars;
    FloatingActionButton fabb;


    TextView userFullName , userEmail;

    Intent intentSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        intentSignIn = new Intent(this, SignIn.class);
        fabb = findViewById(R.id.fab);
        toolbars = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        View headerView = navigationView.getHeaderView(0);
        TextView userFullName = headerView.findViewById(R.id.TextViewNavUserFullName);
        TextView userEmail = headerView.findViewById(R.id.TextViewNavUserEmail);
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).collection("userDetails")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Access each document's data
                                    String name = document.getString("name");
                                    String email = document.getString("email");
                                    // Use the data as needed
                                    userEmail.setText(email);
                                    userFullName.setText(name);
                                    Log.d(TAG, name + " " + email);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                startActivity(intentSignIn);
                            }
                        }
                    });
        }



        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbars, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackground(null);

        fabb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFragment(new FragmentHome());
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.home){
                    openFragment(new HomeFragment());
                }else {
                    openFragment(new RecordsFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new HomeFragment());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.settings) {
            openFragment(new SettingsFragment());
        } else if (itemId == R.id.notifs) {
            openFragment(new NotificationsFragment());
        } else if (itemId == R.id.about) {
            openFragment(new AboutUsFragment());
        } else if (itemId == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(intentSignIn);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}