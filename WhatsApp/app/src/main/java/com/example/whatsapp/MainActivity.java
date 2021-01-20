package com.example.whatsapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.whatsapp.Adapter.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText phonetxt,codetxt;
    private Toolbar main_tool ;
    private ViewPager mviewpager;
    private TabLayout mtablayout;
    private SectionPagerAdapter msectionpageradapter;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        main_tool=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(main_tool);
        getSupportActionBar().setTitle("WahtsApp");


//esraa
    mviewpager=(ViewPager)findViewById(R.id.main_tabpager);
    mtablayout=(TabLayout)findViewById(R.id.main_tablayout);
    msectionpageradapter=new SectionPagerAdapter(getSupportFragmentManager());
    mviewpager.setAdapter(msectionpageradapter);
    mtablayout.setupWithViewPager(mviewpager);



////
}

    @Override
    public void onStart() {
        super.onStart();

    }

    public void SendToStartActivity()
    {

        Intent start=new Intent(MainActivity.this,StartActivity.class);
        startActivity(start);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.main_logout_btn:
                FirebaseAuth.getInstance().signOut();
                 SendToStartActivity();
                return true;

            case R.id.main_setting_btn:
                Intent SettingIntent=new Intent(MainActivity.this,SetUp.class);
                startActivity(SettingIntent);
                return true;
            case R.id.Creating_Groub_Btn:
                Intent GroupIntent=new Intent(MainActivity.this,CreateGroupActivity.class);
                startActivity(GroupIntent);
                return true;
            case R.id.main_all_btn:
                Intent FriendsIntent = new Intent(MainActivity.this , FriendsActivity.class);
                startActivity(FriendsIntent);

                return true;

        }
        return false;

    }


}