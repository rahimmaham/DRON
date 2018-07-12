package com.example.nimra.dron;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class admin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mfirebaseDatabase;
    private TextView name;
    private TextView uid;
    private TextView phoneNumber;
    private DatabaseReference mDatabase;
    private String userID;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        firebaseAuth = FirebaseAuth.getInstance();
        mfirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mfirebaseDatabase.getReference();


        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, login.class));
        }

        FirebaseUser user  = firebaseAuth.getCurrentUser();
        assert user != null;
        userID = user.getUid();


        img= (ImageView)findViewById(R.id.img);
        name=(TextView)findViewById(R.id.name);
        uid=(TextView)findViewById(R.id.uid);
        phoneNumber=(TextView)findViewById(R.id.phoneNumber);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    adminInfo uInfo = new adminInfo();
                    uInfo.setName(ds.child(userID).getValue(adminInfo.class).getName());
                    uInfo.setUid(ds.child(userID).getValue(adminInfo.class).getUid());
                    uInfo.setPhoneNumber(ds.child(userID).getValue(adminInfo.class).getPhoneNumber());
                    uInfo.setImg(ds.child(userID).getValue(adminInfo.class).getImg());
                    name.setText("Name: " + uInfo.getName());
                    uid.setText("User ID: "+uInfo.getUid());
                    phoneNumber.setText("Phone: "+uInfo.getPhoneNumber());
                    Picasso.with(admin.this).load(uInfo.getImg().toString()).into(img);





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(admin.this,gallery.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, login.class));

        } else if (id == R.id.nav_exit) {
            finishAffinity();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
