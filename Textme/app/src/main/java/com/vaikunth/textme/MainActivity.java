package com.vaikunth.textme;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

//import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
   private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAdapter mTabAdapter;

    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolBarMain);
        setSupportActionBar(mToolbar);
        mAuth=FirebaseAuth.getInstance();
        mTabLayout=findViewById(R.id.tabLayout);
        mViewPager=findViewById(R.id.viewPager);
        mTabAdapter=new TabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//
        getMenuInflater().inflate(R.menu.options_menu,menu);
//        return true;
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       // super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.logOut)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
           // finish();
        }
//        if(item.getItemId()==R.id.main_settings_option)
//        {
//            mAuth.signOut();
//            Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
//            startActivity(intent);
//            finish();
//        }
//        if(item.getItemId()==R.id.main_find_friends_option)
//        {
//            mAuth.signOut();
//            Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
//            startActivity(intent);
//            finish();
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,Object> map=new HashMap<>();
        map.put("Online", ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String,Object> map=new HashMap<>();
        map.put("Offline", ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
    }

//    @Override
//    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
//        super.onCreatePanelMenu(featureId, menu);
//        getMenuInflater().inflate(R.menu.options_menu,menu);
//        return true;
//    }
//    onOp
}