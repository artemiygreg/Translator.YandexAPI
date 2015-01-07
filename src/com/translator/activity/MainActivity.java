package com.translator.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.example.R;
import com.translator.classes.App;

public class MainActivity extends Activity  {
    private App app;
    private EditText request;
    public TextView tv_result;
    private Button btn_translate, btn_retranslate;
    private CheckBox ru_en, en_ru, usualTranslation, groupedTranslation;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        app = new App(this);
        request = (EditText) findViewById(R.id.request);
        tv_result = (TextView) findViewById(R.id.tv_result);
        btn_translate = (Button) findViewById(R.id.btn_translate);
        btn_retranslate = (Button) findViewById(R.id.btn_retranslate);
        ru_en = (CheckBox) findViewById(R.id.ru_en);
        en_ru = (CheckBox) findViewById(R.id.en_ru);
        usualTranslation = (CheckBox) findViewById(R.id.usualTranslation);
        groupedTranslation = (CheckBox) findViewById(R.id.groupedTranslation);
        usualTranslation.setChecked(true);
        ru_en.setChecked(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(app.getStringFromResources(R.string.textTranslating));

        ru_en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    en_ru.setChecked(false);
                    ru_en.setChecked(true);
                }
                else {
                    en_ru.setChecked(true);
                    ru_en.setChecked(false);
                }
            }
        });
        en_ru.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ru_en.setChecked(false);
                    en_ru.setChecked(true);
                }
                else {
                    if(groupedTranslation.isChecked()) {
                        en_ru.setChecked(true);
                    }
                    else {
                        ru_en.setChecked(true);
                        en_ru.setChecked(false);
                    }
                }
            }
        });
        usualTranslation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    groupedTranslation.setChecked(false);
                    usualTranslation.setChecked(true);
                    ru_en.setEnabled(true);
                }
                else {
                    ru_en.setEnabled(true);
                    groupedTranslation.setChecked(true);
                    usualTranslation.setChecked(false);
                }
            }
        });
        groupedTranslation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    usualTranslation.setChecked(false);
                    groupedTranslation.setChecked(true);
                    en_ru.setChecked(true);
                    ru_en.setEnabled(false);
                }
                else {
                    usualTranslation.setChecked(true);
                    groupedTranslation.setChecked(false);
                }
            }
        });

        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(request.getText().toString().equals(app.getStringFromResources(R.string.emptyString))) {
                        app.Alert(app.getStringFromResources(R.string.error), app.getStringFromResources(R.string.messageForTranslate));
                    }
                    else {
                        if(usualTranslation.isChecked()) {
                            tv_result.setText(app.getStringFromResources(R.string.emptyString));
                            app.server.translate_text(request.getText().toString(), tv_result, ((ru_en.isChecked())?(app.getStringFromResources(R.string.ruToEng)):(app.getStringFromResources(R.string.engToRu))), progressDialog);
                        }
                        else {
                            tv_result.setText(app.getStringFromResources(R.string.emptyString));
                            app.server.groupedTranslation(request.getText().toString(), tv_result, progressDialog);
                        }
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        btn_retranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv_result.getText().toString().equals(app.getStringFromResources(R.string.emptyString))){
                    app.Alert(app.getStringFromResources(R.string.error), app.getStringFromResources(R.string.notText));
                }
                else {
                    if(app.server.lang_translate.equals(app.getStringFromResources(R.string.ruToEng))) {
                        request.setText(tv_result.getText().toString());
                        ru_en.setChecked(false);
                        en_ru.setChecked(true);
                        app.server.translate_text(tv_result.getText().toString(), tv_result, app.getStringFromResources(R.string.engToRu), progressDialog);
                    }
                    else {
                        request.setText(tv_result.getText().toString());
                        ru_en.setChecked(true);
                        en_ru.setChecked(false);
                        app.server.translate_text(tv_result.getText().toString(), tv_result, app.getStringFromResources(R.string.ruToEng), progressDialog);
                    }
                }
            }
        });
    }
}