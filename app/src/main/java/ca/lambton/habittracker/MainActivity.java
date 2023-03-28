package ca.lambton.habittracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import ca.lambton.habittracker.databinding.ActivityMainBinding;
import ca.lambton.habittracker.habit.model.User;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.view.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfiguration;
    private HabitViewModel habitViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_my_habits, R.id.menu_logout, R.id.nav_community)
                .setOpenableLayout(drawerLayout)
                .build();

        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(getApplication())).get(HabitViewModel.class);

        User user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {

            habitViewModel.getUserByEmail(user.getEmail()).observe(this, userResult -> {
                if (userResult != null) {
                    if (!user.getEmail().equalsIgnoreCase(userResult.getEmail())) {
                        habitViewModel.saveUser(user);
                    }
                } else {
                    habitViewModel.saveUser(user);
                }
            });
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0);
        TextView loggedName = headerView.findViewById(R.id.name_logged_label);
        TextView loggedEmail = headerView.findViewById(R.id.email_logged_label);
        ImageView profileImage = headerView.findViewById(R.id.profile_image);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            loggedName.setText(firebaseUser.getDisplayName());
            loggedEmail.setText(firebaseUser.getEmail());

            if (firebaseUser.getPhotoUrl() != null) {
                Picasso.get().load(firebaseUser.getPhotoUrl()).fit().into(profileImage);
            }
        }

        navigationView.getMenu().findItem(R.id.menu_logout).setOnMenuItemClickListener(menuItem -> {
            this.signOut();
            return true;
        });


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(getApplication())).get(HabitViewModel.class);


        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public HabitViewModel habitViewModel() {
        return this.habitViewModel;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        // Start your login activity here
        Intent loginActivity = new Intent(this, LoginFragment.class);
        startActivity(loginActivity);
    }

}