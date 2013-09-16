package com.galaev.thermostat.activities;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.galaev.thermostat.R;
import com.galaev.thermostat.ThermostatApp;
import com.galaev.thermostat.storage.Day;

/**
 * Settings page.
 */
public class Settings extends Activity {

    private ThermostatApp app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        app = (ThermostatApp) getApplication();
        Button timeButton = (Button) findViewById(R.id.timeButton);
        Button weekButton = (Button) findViewById(R.id.weekButton);
        Button dayButton = (Button) findViewById(R.id.dayButton);
        Button nightButton = (Button) findViewById(R.id.nightButton);
        ToggleButton programToggle = (ToggleButton) findViewById(R.id.programToggle);
        programToggle.setChecked(app.isProgramEnabled());

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DayPickerFragment();
                newFragment.show(getFragmentManager(), "dayPicker");
            }
        });
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TemperaturePickerFragment(R.string.day_temperature);
                newFragment.show(getFragmentManager(), "temperaturePicker");
            }
        });
        nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TemperaturePickerFragment(R.string.night_temperature);
                newFragment.show(getFragmentManager(), "temperaturePicker");
            }
        });
        programToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.setProgramEnabled(isChecked);
            }
        });
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String[] time = app.getTime().split(":");
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, Integer.parseInt(time[0]),
                    Integer.parseInt(time[1]), true);
        }

        public void onTimeSet(TimePicker view, int hour, int minute) {
            synchronized (MainActivity.class) {
                app.resetScheduledSwitch();
                app.setTime(String.format("%02d:%02d", hour, minute));
            }
        }
    }

    public class DayPickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.set_day)
                    .setItems(R.array.days, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            synchronized (MainActivity.class) {
                                app.setDay(Day.values()[which]);
                            }
                        }
                    });
            return builder.create();
        }

    }

    public class TemperaturePickerFragment extends DialogFragment {

        private int id;
        private double counter;

        public TemperaturePickerFragment(int id) {
            this.id = id;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialog = inflater.inflate(R.layout.dialog_temparature, null);
            ImageView add = (ImageView)dialog.findViewById(R.id.addButton);
            ImageView sub = (ImageView)dialog.findViewById(R.id.subButton);
            final TextView tempCounter = (TextView)dialog.findViewById(R.id.temperatureCounter);
            final SeekBar slider = (SeekBar)dialog.findViewById(R.id.slider);

            if (id == R.string.day_temperature) {
                counter = app.getDayTemperature();
            } else {
                counter = app.getNightTemperature();
            }

            tempCounter.setText(String.format("%.1f \u2103", counter));
            slider.setMax(25);
            slider.setProgress((int) counter - 5);


            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (counter < 30) {
                        counter += 0.1;
                    }
                    tempCounter.setText(String.format("%.1f \u2103", counter));
                    slider.setProgress((int) counter - 5);
                }
            });

            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (counter > 5) {
                        counter -= 0.1;
                    }
                    tempCounter.setText(String.format("%.1f \u2103", counter));
                    slider.setProgress((int) counter - 5);
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
                        counter = progress + 5;
                        tempCounter.setText(String.format("%.1f \u2103", counter));
                    }
                }
            });

            builder.setTitle(id)
                    .setView(dialog)
                    .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (id == R.string.day_temperature) {
                                app.setDayTemperature(counter);
                            } else {
                                app.setNightTemperature(counter);
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TemperaturePickerFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }

    }
}