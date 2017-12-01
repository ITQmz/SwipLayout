package com.qmz.swiplayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class SecondActivity extends Activity {
    private SwipLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        layout = (SwipLayout) LayoutInflater.from(this).inflate(
                R.layout.base, null);
        layout.attachToActivity(this);
    }
}
