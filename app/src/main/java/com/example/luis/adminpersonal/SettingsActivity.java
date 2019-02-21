package com.example.luis.adminpersonal;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.luis.adminpersonal.Utils.Utils;
import com.rey.material.widget.EditText;
import com.rey.material.widget.Slider;

public class SettingsActivity extends AppCompatActivity {


    private EditText txtTitleSelectPictureName;
    private EditText txtDirectoryName;
    private EditText txtPhotoName;
    private Switch switchAutoIncrement;
    private Slider sliderQuality;
    private RadioGroup radioTypeExtension;
    private RadioButton radioExtensionPNG;
    private RadioButton radioExtensionJPEG;
    private Context context;

    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.tittle_settings));

        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);



        txtTitleSelectPictureName = (EditText) findViewById(R.id.txtTitleSelectPictureName);
        txtDirectoryName = (EditText) findViewById(R.id.txtDirectoryName);
        txtPhotoName = (EditText) findViewById(R.id.txtPhotoName);
        switchAutoIncrement = (Switch) findViewById(R.id.switchAutoIncrement);
        sliderQuality = (Slider) findViewById(R.id.sliderQuality);
        radioTypeExtension = (RadioGroup) findViewById(R.id.radioTypeExtension);
        radioExtensionPNG = (RadioButton) findViewById(R.id.radioExtensionPNG);
        radioExtensionJPEG = (RadioButton) findViewById(R.id.radioExtensionJPEG);
        valuesSharedPreference();
        listeners();
    }
    private void valuesSharedPreference(){
        txtDirectoryName.setText(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_DIRECTORY_NAME));
        txtPhotoName.setText(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_PHOTO_NAME));
        txtTitleSelectPictureName.setText(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_SELECTED_PICTURE));
        sliderQuality.setValue(
                Float.parseFloat(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_QUALITY_PICTURE)),true);

        if(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_FORMAT).equals(Utils.C_PNG)){
            radioExtensionPNG.setChecked(true);
        }else if(Utils.getSharedPreference(context, Utils.C_PREFERENCE_MC_FORMAT).equals(Utils.C_JPG)){
            radioExtensionJPEG.setChecked(true);
        }
        switchAutoIncrement.setChecked(Boolean.parseBoolean(Utils.getSharedPreference(context,Utils.C_PREFERENCE_MC_AUTO_IC_NAME)));
    }
    private void listeners() {
        txtDirectoryName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_DIRECTORY_NAME, txtDirectoryName.getText().toString());
            }
        });

        txtPhotoName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_PHOTO_NAME, txtPhotoName.getText().toString());
            }
        });

        txtTitleSelectPictureName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_SELECTED_PICTURE, txtTitleSelectPictureName.getText().toString());
            }
        });


        sliderQuality.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_QUALITY_PICTURE, String.valueOf(newValue));
            }
        });

        switchAutoIncrement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_AUTO_IC_NAME, String.valueOf(isChecked));
            }
        });

        radioExtensionPNG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_FORMAT, Utils.C_PNG);
            }
        });

        radioExtensionJPEG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setSharedPreference(context, Utils.C_PREFERENCE_MC_FORMAT, Utils.C_JPG);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
