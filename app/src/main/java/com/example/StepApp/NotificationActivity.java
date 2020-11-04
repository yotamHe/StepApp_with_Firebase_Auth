package com.example.StepApp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.StepApp.Fragments.TabHomeFragment;
import com.example.StepApp.Fragments.TabSettingsFragment;
import com.example.StepApp.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    private Button enable;
    private Button disable;
    private MaterialEditText setTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        enable = (Button) findViewById(R.id.enable);
        disable = (Button) findViewById(R.id.disable);
        setTime = (MaterialEditText) findViewById(R.id.repeatingText);


        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Integer.parseInt(setTime.getText().toString().trim());
                String txtField = setTime.getText().toString().trim();


                Calendar calendar = Calendar.getInstance();

                Intent intent = new Intent(NotificationActivity.this, MyService.class);
                intent.setAction(MyService.ACTION_START_FOREGROUND_SERVICE);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext()
                        , 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar
                        .getTimeInMillis(), System
                        .currentTimeMillis() + (time * 1000), pendingIntent);

                startService(intent);

                /*
                Intent intent = new Intent(getApplicationContext(), Receiver.class);

*/

            }


        });
        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationActivity.this, MyService.class);
                intent.setAction(MyService.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });

    }
}