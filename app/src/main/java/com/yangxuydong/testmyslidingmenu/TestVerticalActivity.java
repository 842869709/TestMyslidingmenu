package com.yangxuydong.testmyslidingmenu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.yangxuydong.myslidingmenu.MySlidingMenuVertical;

public class TestVerticalActivity extends AppCompatActivity {

    private MySlidingMenuVertical msmv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_v);

        initView();
    }

    private void initView() {
        msmv = findViewById(R.id.msmv);
        msmv.setOnStateChangeListening(new MySlidingMenuVertical.OnStateChangeListening() {
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
                msmv.setOpen();
            }
        });


    }
}