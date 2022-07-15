package com.example.newdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ImageButton changeLicenseDate;
    private Global globalVars;
    private TextView[] dates;
    private TextView[][] timers;
    private boolean timerWorks;
    private CountDownTimer[] cTimers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globalVars = new Global();
        dates = new TextView[3];
        dates[0] = findViewById(R.id.dayNightDateTextView);
        dates[1] = findViewById(R.id.nightOnlyDateTextView);
        dates[2] = findViewById(R.id.newDriverDateTextView);

        timers = new TextView[3][4];
        timers[0][0] = findViewById(R.id.dayNightDaysTextView);
        timers[0][1] = findViewById(R.id.dayNightHoursTextView);
        timers[0][2] = findViewById(R.id.dayNightMinutesTextView);
        timers[0][3] = findViewById(R.id.dayNightSecondsTextView);
        timers[1][0] = findViewById(R.id.nightOnlyDaysTextView);
        timers[1][1] = findViewById(R.id.nightOnlyHoursTextView);
        timers[1][2] = findViewById(R.id.nightOnlyMinutesTextView);
        timers[1][3] = findViewById(R.id.nightOnlySecondsTextView);
        timers[2][0] = findViewById(R.id.newDriverDaysTextView);
        timers[2][1] = findViewById(R.id.newDriverHoursTextView);
        timers[2][2] = findViewById(R.id.newDriverMinutesTextView);
        timers[2][3] = findViewById(R.id.newDriverSecondsTextView);

        cTimers = new CountDownTimer[3];

        changeLicenseDate = findViewById(R.id.changeLicenseDateBtn);
        changeLicenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        load_data();

        initialAtStart();
    }

    private void showDatePickerDialog() {
        DatePickerDialog dpd = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dpd.show();
    }

    private void save_data() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(globalVars);
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit().putString("myObject", json).apply();
    }

    private void load_data() {
        String res = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getString("myObject", "-1");
        if (!res.equals("-1")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            globalVars = gson.fromJson(res, Global.class);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        globalVars.setLicenseDate(year, month, dayOfMonth);
        save_data();
        for (int i = 0; i < 3; i++) {
            if (cTimers[i] != null)
                cTimers[i].cancel();
            else break;
        }
        initialAtStart();

    }

    private void initialAtStart() {
        if (globalVars.getLicenseDate() != null) {
            Calendar dayNightDate = globalVars.getDayNightDate();
            Calendar nightOnlyDate = globalVars.getNightOnlyDate();
            Calendar newDriverDate = globalVars.getNewDriverDate();
            dates[0].setText(reformatDate(dayNightDate));
            dates[1].setText(reformatDate(nightOnlyDate));
            dates[2].setText(reformatDate(newDriverDate));
            startTimer(dayNightDate, timers[0], 0);
            startTimer(nightOnlyDate, timers[1], 1);
            startTimer(newDriverDate, timers[2], 2);
        }
    }

    private String reformatDate(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        month++;
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) sb.append('0');
        sb.append(dayOfMonth).append(" / ");
        if (month < 10) sb.append('0');
        sb.append(month).append(" / ").append(year);
        return sb.toString();
    }

    private void startTimer(Calendar c, TextView[] times, int timerIndex) {
        Calendar now = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        long millis = c.getTimeInMillis() - now.getTimeInMillis();
        timerWorks = true;
        cTimers[timerIndex] = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTextViews(millisUntilFinished, times);
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void updateTextViews(long millisUntilFinished, TextView[] times) {
        int days = (int) ((millisUntilFinished / (1000 * 60 * 60 * 24)));
        int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
        int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
        int seconds = (int) ((millisUntilFinished / 1000) % 60);
        times[0].setText(String.valueOf(days));
        times[1].setText(String.valueOf(hours));
        times[2].setText(String.valueOf(minutes));
        times[3].setText(String.valueOf(seconds));
    }
}