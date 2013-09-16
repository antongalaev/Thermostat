package com.galaev.thermostat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.galaev.thermostat.R;
import com.galaev.thermostat.ThermostatApp;
import com.galaev.thermostat.storage.TimeOfDay;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Activity class.
 * It is shown when application is launched.
 *
 * @author Anton Galaev
 */
public class MainActivity extends Activity {

    private ThermostatApp app;
    private TextView currentTemp;
    private SeekBar slider;
    private TextView dayTitle;
    private TextView timeTitle;
    private ImageView dayImage;
    private Button week;
    private int hour;
    private int minute;

    /**
     * Creation.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        app = (ThermostatApp) getApplication();
        app.setActivity(this);
        ImageView add = (ImageView) findViewById(R.id.addButton);
        ImageView sub = (ImageView) findViewById(R.id.subButton);
        week = (Button) findViewById(R.id.week);
        Button settings = (Button) findViewById(R.id.settings);
        currentTemp = (TextView) findViewById(R.id.temperatureCounter);
        slider = (SeekBar) findViewById(R.id.slider);
        dayTitle = (TextView) findViewById(R.id.dayTitle);
        timeTitle = (TextView) findViewById(R.id.timeTitle);
        dayImage = (ImageView) findViewById(R.id.dayImage);


        dayTitle.setText(app.getDay().toString());
        timeTitle.setText(app.getTime());
        setTimeOfDay(app.getTimeOfDay());
        slider.setMax(25);
        slider.setProgress((int) app.getCurrentTemperature() - 5);
        currentTemp.setText(String.format("%.1f \u2103", app.getCurrentTemperature()));


        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.isProgramEnabled()) {
                    Intent intent = new Intent(v.getContext(), WeekOverview.class);
                    startActivity(intent);
                } else {
                    Context context = getApplicationContext();
                    CharSequence text = "Week Program is Disabled";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Settings.class);
                startActivity(intent);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currTemp = app.getCurrentTemperature();
                if (currTemp < 30) {
                    app.setCurrentTemperature(currTemp + 0.1);
                }
            }
        });
        
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currTemp = app.getCurrentTemperature();
                if (currTemp > 5) {
                    app.setCurrentTemperature(currTemp - 0.1);
                }
            }
        });
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    app.setCurrentTemperature(progress + 5);
                }
            }
        });
        Timer timer = app.getTimer();
        if (! app.isTimerStarted()) {
            String[] time = app.getTime().split(":");
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
            app.setTimerStarted(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (MainActivity.class) {
                        minute += 5;
                        if (minute >= 60) {
                            ++ hour;
                            minute = 0;
                            if (hour == 24) {
                                hour = 0;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        app.theNextDay();
                                        if (week.isEnabled()) {
                                            app.setCurrentTemperature(app.getNightTemperature());
                                            app.setTimeOfDay(TimeOfDay.NIGHT);
                                        }
                                    }
                                });
                            }
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                app.setTime(String.format("%02d:%02d", hour, minute));
                            }
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * Update time.
     *
     * @param time
     */
    public void setTime(String time) {
        timeTitle.setText(time);
        String[] timeSplit = time.split(":");
        hour = Integer.parseInt(timeSplit[0]);
        minute = Integer.parseInt(timeSplit[1]);
        if (app.isProgramEnabled()) {
            app.syncProgram();
        }
    }

    /**
     * Update day.
     * @param day
     */
    public void setDay(String day) {
        dayTitle.setText(day);
    }

    public void setCurrentTemperature(double currTemp) {
        currentTemp.setText(String.format("%.1f \u2103", currTemp));
        slider.setProgress((int) currTemp - 5);
    }

    public void setProgramEnabled(boolean enabled) {
        week.setEnabled(enabled);
    }

    public void setTimeOfDay(TimeOfDay timeOfDay) {
        if (timeOfDay == TimeOfDay.DAY) {
             dayImage.setImageResource(R.drawable.sun);
        } else {
            dayImage.setImageResource(R.drawable.moon);
        }
    }
}