package com.example.luis.adminpersonal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.luis.adminpersonal.Fragments.FragmentDashboard;
import com.example.luis.adminpersonal.Fragments.FragmentIncidencia;
import com.example.luis.adminpersonal.Fragments.FragmentScan;
import com.example.luis.adminpersonal.Utils.Utils;
import com.frosquivel.magicalcamera.MagicalCamera;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    DrawerLayout drawerLayout;
    TextView tvUsuario,tvEmail,tvEmpresa;
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;

    RequestQueue queue;

    CircularImageView avatar;
    ProgressDialog progressDialog;

    Context context;
    LocationManager locationManager;
    SharedPreferences prefs;
    String AVATAR,EMAIL, NOMBRE, APE_PAT,ID_USER,EMPRESA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("PreferenciasAdminPer", Context.MODE_PRIVATE);
        context=MainActivity.this;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Utils.setInitialSharedPreference(this, false);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View header=navigationView.getHeaderView(0);

        tvUsuario = (TextView)header.findViewById(R.id.ussuario);
        tvEmail = (TextView)header.findViewById(R.id.email);
        avatar = (CircularImageView)header.findViewById(R.id.avatar);
        tvEmpresa = (TextView)header.findViewById(R.id.empresa);
        EMAIL = prefs.getString("email", "por_defecto@email.com");
        ID_USER=prefs.getString("idUsuario", "0");
        NOMBRE=prefs.getString("nombre", "Default");
        APE_PAT=prefs.getString("apePat", "Default");
        AVATAR=prefs.getString("avatar", "Default");
        EMPRESA=prefs.getString("empresa", "Default");

        tvUsuario.setText(NOMBRE+" "+APE_PAT);
        tvEmail.setText(EMAIL);
        tvEmpresa.setText(EMPRESA);

        if(!AVATAR.equals("Default")){
            Glide.with(this)
                    .load(Base64.decode(AVATAR, Base64.DEFAULT))
                    .into(avatar);

        }


        //avatar.setBackgroundColor(Color.RED);

        queue = Volley.newRequestQueue(this);

        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        if(savedInstanceState == null) {
            setFragment(0);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //textView = (TextView) findViewById(R.id.textView);
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_inbox:
                                menuItem.setChecked(true);
                                //textView.setText(menuItem.getTitle());

                                setFragment(0);


                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_escanear:
                                    if(isActivePermission()){
                                        setFragment(1);
                                        menuItem.setChecked(true);
                                        drawerLayout.closeDrawer(GravityCompat.START);
                                        return true;
                                    }else{
                                        requestCodeQRCodePermissions();
                                        drawerLayout.closeDrawer(GravityCompat.START);
                                        return false;
                                    }

                            case R.id.item_navigation_drawer_incidencia:
                                menuItem.setChecked(true);

                                setFragment(2);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;

                            case R.id.item_navigation_drawer_logout:
                                Intent datos = new Intent(MainActivity.this, Login.class);
                                startActivity(datos);
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);

                                return true;


                        }
                        return true;
                    }
                });
    }
    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentDashboard fragmentDashboard = new FragmentDashboard();
                fragmentTransaction.replace(R.id.fragment, fragmentDashboard);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentScan fragmentScan = new FragmentScan();
                fragmentTransaction.replace(R.id.fragment, fragmentScan);
                fragmentTransaction.commit();

                break;
            case 2:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentIncidencia fragmentIncidencia = new FragmentIncidencia();
                fragmentTransaction.replace(R.id.fragment, fragmentIncidencia);
                fragmentTransaction.commit();
                break;



        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "Para poder utilizar la función de Escanear código debe otorgar permisos. Desea hacerlo ahora?", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    private boolean isActivePermission() {
        //this map is return in method onRequestPermissionsResult for view what permissions are actives
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                return  true;
            }else{

                return  false;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        return;

    }
}
