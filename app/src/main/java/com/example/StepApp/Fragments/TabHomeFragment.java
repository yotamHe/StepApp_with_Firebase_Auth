package com.example.StepApp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.StepApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TabHomeFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "TabHStatusFragment";
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView textCounter;
    boolean activityRunning;
    private boolean isCntSensorPresent;
    //private Button buttonReturn;
    private TextView mainText;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private CircleImageView userPic;
    private TextView finishSession;
    private Integer stepCount = 0;
    private double km = stepCount * 0.000762;
    private double calories = stepCount * 0.04;
    private double MagnitudePrevious = 0;
    private TextView stepsToKm;
    private TextView stepsToKg;
    private FirebaseUser fUser;


    private String TAG2 = "step_sensor_data";

    public TabHomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);
        mainText = (TextView) view.findViewById(R.id.mainText);
        stepsToKm = (TextView) view.findViewById(R.id.stepsToKm);
        stepsToKg = (TextView) view.findViewById(R.id.stepsToKg);
        finishSession = (TextView) view.findViewById(R.id.finishSession);
        auth = FirebaseAuth.getInstance();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);
        textCounter = (TextView) view.findViewById(R.id.stepsCnt);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        finishSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> nMap = new HashMap<>();
                nMap.put("steps", stepCount);


                FirebaseDatabase.getInstance().getReference().child("StepAppDB").child("Users")
                        .child(fUser.getUid()).updateChildren(nMap);
                Toast.makeText(TabHomeFragment.this.getActivity(), "Session Completed, data has been updated", Toast.LENGTH_SHORT).show();

            }
        });

        databaseReference = FirebaseDatabase.getInstance()
                .getReference().child("StepAppDB").child("Users")
                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString().trim();
                mainText.setText("Hello " + name + " !!");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCntSensorPresent = true;
        } else {
            Toast.makeText(getActivity(), "Motion Sensor not Found on Device, steps will not be tracked", Toast.LENGTH_LONG).show();
            isCntSensorPresent = false;
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor == sensor) {
//            int sCount = (int) event.values[0];
//            textCounter.setText(String.valueOf(sCount));
//        }
        Log.d(TAG2, "event change");
        if (event != null) {
            Log.d(TAG2, "event not null");
            Log.d(TAG2, event.values.toString());
            Log.d(TAG2, "event value count " + event.values[0]);
            if (event.values[0] > 6) {
                stepCount++;
            }
            Integer divide = (stepCount%5000);
            textCounter.setText("Total Steps Taken: " + divide.toString());
            stepsToKm.setText("Total Distance Walked: " + new DecimalFormat("##.#").format(km) + " km");
            stepsToKg.setText("Total Calories Burned: " + new DecimalFormat("##.#").format(calories));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
