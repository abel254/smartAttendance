package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.smartattendance.Authentication.AdminLogin;
import com.google.android.material.navigation.NavigationView;

public class AdminMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nave_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeAdminFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);

            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeAdminFragment()).addToBackStack("tag").commit();
                break;
            case R.id.nav_scan:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ScanFragment()).addToBackStack("tag").commit();
                break;
            case R.id.nav_current:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CurrentFragment()).commit();
                break;
            case R.id.nav_reports:

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ReportsFragment()).commit();
                break;
            case R.id.nav_allstudents:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RegisteredStudentsFragment()).commit();
                break;
            case R.id.nav_qrcodes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new StudentQrCodesFragment()).commit();
                break;
            case R.id.nav_logout:
                startActivity(new Intent(getApplicationContext(), PreLogin.class));
                finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
/*
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        super.onBackPressed();

    }

 */

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(AdminMain.this, permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(AdminMain.this, new String[]{permission},
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
