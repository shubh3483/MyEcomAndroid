package com.example.myecom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.example.myecom.databinding.ActivityMainBinding;
import com.example.myecom.databinding.DialogVariantPickerBinding;
import com.example.myecom.databinding.DialogWeightPickerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        DialogWeightPickerBinding weightPickerBinding = DialogWeightPickerBinding.inflate(getLayoutInflater());
        DialogVariantPickerBinding variantPickerBinding = DialogVariantPickerBinding.inflate(getLayoutInflater());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new MaterialAlertDialogBuilder(MainActivity.this, R.style.CustomDialogTheme)
                        .setView(weightPickerBinding.getRoot())
                        .show();
            }
        }, 5000);

        weightPickerBinding.btnSave.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(MainActivity.this, R.style.CustomDialogTheme)
                    .setView(variantPickerBinding.getRoot())
                    .show();
        });
    }
}