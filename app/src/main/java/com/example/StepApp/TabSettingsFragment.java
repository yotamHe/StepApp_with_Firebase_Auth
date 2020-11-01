package com.example.StepApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.StepApp.R;

public class TabSettingsFragment extends Fragment {
    private static final String TAG = "TabSettingsFragment";


    private Button notifySettings;
    private Button editProfile;
    private Button logOut;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_settings, container, false);
        notifySettings = (Button) view.findViewById(R.id.notifySettings);
        editProfile = (Button) view.findViewById(R.id.editProfile);
        logOut= (Button) view.findViewById(R.id.logOut);


        notifySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TabSettingsFragment.this.getActivity(),NotificationActivity.class));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TabSettingsFragment.this.getActivity(), UserEditActivity.class);
                startActivity(intent);

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TabSettingsFragment.this.getActivity(),MainActivity.class));
            }
        });

        return view;
    }

}
