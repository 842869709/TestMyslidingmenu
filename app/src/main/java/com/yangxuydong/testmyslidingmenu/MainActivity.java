package com.yangxuydong.testmyslidingmenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yangxuydong.myslidingmenu.MySlidingMenu;

public class MainActivity extends AppCompatActivity {

    private MySlidingMenu msm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        msm = findViewById(R.id.msm);
        msm.setRightDown(true);
        msm.setOnStateChangeListening(new MySlidingMenu.OnStateChangeListening() {
            @Override
            public void OnStateChange(boolean isExpand) {
                Log.i("test",isExpand?"打开":"关闭");
            }

            @Override
            public void OnScroll(int precent) {
                Log.i("test","precent="+precent);
            }
        });

        findViewById(R.id.bt_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msm.setOpen();
            }
        });

    }
}