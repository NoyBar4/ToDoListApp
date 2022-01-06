package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class Opening extends AppCompatActivity {

    ImageView splashImg, circle, icon;
    LottieAnimationView lottieAnimationView;
    TextView title;

    private static final int NUM_PAGES = 2;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        splashImg = findViewById(R.id.img);
        circle = findViewById(R.id.circle);
        icon = findViewById(R.id.icon);
        title = findViewById(R.id.title);
        lottieAnimationView = findViewById(R.id.lottie);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        splashImg.animate().translationY(-2600).setDuration(1000).setStartDelay(4000);
        circle.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        icon.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        title.animate().translationY(2000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(4000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*
                Intent mainIntent = new Intent(Opening.this, Today.class);
                startActivity(mainIntent);
                finish();

                 */
                viewPager.setAdapter(pagerAdapter);
            }
            //timer in miliseconds, 1000ms = 1s//
        },5000);


    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                //Intro intro = new Intro();
                return new Intro();
                case 1:
                    //Intro1 intro1 = new Intro1();
                    return new Intro1();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}