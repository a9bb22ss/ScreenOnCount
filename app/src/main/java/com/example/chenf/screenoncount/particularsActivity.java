package com.example.chenf.screenoncount;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ParticularsActivity extends Activity {

    private TextView start_time;
    private TextView end_time;
    private TextView duration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particulars);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        duration = (TextView) findViewById(R.id.duration);
        Intent intent = getIntent();
        Duration this_duration = (Duration) intent.getSerializableExtra("duration");
        start_time.setText(this_duration.getStart_time());
        end_time.setText(this_duration.getEnd_time());
        duration.setText(this_duration.getDuration());
    }
}