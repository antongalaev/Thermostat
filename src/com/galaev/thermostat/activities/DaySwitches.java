package com.galaev.thermostat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.galaev.thermostat.R;
import com.galaev.thermostat.ThermostatApp;
import com.galaev.thermostat.storage.Day;
import com.galaev.thermostat.storage.DayTimetable;
import com.galaev.thermostat.storage.TemperatureSwitch;
import com.galaev.thermostat.storage.TimeOfDay;

/**
 * Switches list activity.
 *
 */
public class DaySwitches extends Activity {

    private Day day;
    private DayTimetable timetable;
    private int switchCounter;
    private Button contextTarget;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_switches);
        ThermostatApp app = (ThermostatApp) getApplication();
        ImageButton addButton = (ImageButton) findViewById(R.id.addSwitchButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SwitchPickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        day = Day.values()[getIntent().getIntExtra("DAY", 2)];
        timetable = app.getTimetable(day);
        setTitle(day + " Switches");
        inflateSwitches();
    }

    private void inflateSwitches() {
        switchCounter = 0;
        ViewGroup switchList = (ViewGroup) DaySwitches.this.findViewById(R.id.switchList);
        if (switchList.getChildCount() != 1) {
            switchList.removeViews(0, switchList.getChildCount() - 1);
        }
        for (TemperatureSwitch s : timetable.toArray())  {
            inflateSwitch(s);
        }
    }

    private void inflateSwitch(final TemperatureSwitch ts) {
        String time = ts.getTime();
        TimeOfDay timeOfDay = ts.getTimeOfDay();
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup insertPoint = (ViewGroup) DaySwitches.this.findViewById(R.id.switchList);
        View switchView = inflater.inflate(R.layout.switch_view, null);
        insertPoint.addView(switchView, switchCounter);
        Button switchButton = (Button) switchView.findViewById(R.id.switchButton);
        StringBuilder sb = new StringBuilder();
        Drawable drawable;
        if (timeOfDay.equals(TimeOfDay.DAY)) {
            sb.append("Day - ");
            drawable = getResources().getDrawable(R.drawable.sun);
            switchButton.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else {
            sb.append("Night - ");
            drawable = getResources().getDrawable(R.drawable.moon);

        }
        drawable.setBounds(10, 0, 60, 50);
        switchButton.setCompoundDrawables(drawable, null, null, null);
        sb.append(time);
        switchButton.setText(sb.toString());
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RemoveDialogFragment(ts);
                newFragment.show(getFragmentManager(), "removeSwitch");
            }
        });
        registerForContextMenu(switchButton);
        ++ switchCounter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        contextTarget = (Button) v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cm_edit:
                editSwitch(contextTarget);
                return true;
            case R.id.cm_remove:
                deleteSwitch(contextTarget);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void deleteSwitch(View target) {
        Button btn = (Button) target;
        String[] text = btn.getText().toString().split(" ");
        for (TemperatureSwitch s : timetable.toArray())  {
            if (text[text.length - 1].equals(s.getTime())) {
                DialogFragment newFragment = new RemoveDialogFragment(s);
                newFragment.show(getFragmentManager(), "removeSwitch");
                break;
            }
        }
    }

    private void editSwitch(View target) {
        Button btn = (Button) target;
        String[] text = btn.getText().toString().split(" ");
        for (TemperatureSwitch s : timetable.toArray())  {
            if (text[text.length - 1].equals(s.getTime())) {
                DialogFragment newFragment = new SwitchPickerFragment(s);
                newFragment.show(getFragmentManager(), "timePicker");
                break;
            }
        }
    }


    public class RemoveDialogFragment extends DialogFragment {

        private TemperatureSwitch ts;

        public RemoveDialogFragment(TemperatureSwitch temperatureSwitch) {
            ts = temperatureSwitch;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.remove_title)
                    .setMessage(R.string.remove_message)
                    .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                             timetable.removeSwitch(ts);
                             inflateSwitches();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RemoveDialogFragment.this.getDialog().cancel();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public class SwitchPickerFragment extends DialogFragment {

        private TimePicker timePicker;
        private Switch typeSwitch;
        private TemperatureSwitch editable;

        public SwitchPickerFragment(TemperatureSwitch s) {
            super();
            editable = s;
        }

        public SwitchPickerFragment() {
            super();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialog = inflater.inflate(R.layout.dialog_switch, null);
            timePicker = (TimePicker) dialog.findViewById(R.id.switchTimePicker);
            typeSwitch = (Switch) dialog.findViewById(R.id.typeSwitcher);
            timePicker.setIs24HourView(true);
            builder.setTitle(R.string.new_switch);
            if (editable != null) {
                String[] timeSplit = editable.getTime().split(":");
                timePicker.setCurrentHour(Integer.parseInt(timeSplit[0]));
                timePicker.setCurrentMinute(Integer.parseInt(timeSplit[1]));
                typeSwitch.setChecked(editable.getTimeOfDay() == TimeOfDay.DAY);
                builder.setTitle(R.string.edit_switch);
            }
            builder.setView(dialog)
                    .setPositiveButton(editable == null ? R.string.create : R.string.edit,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            TimeOfDay timeOfDay;
                            if (typeSwitch.isChecked()) {
                                timeOfDay = TimeOfDay.DAY;
                            } else {
                                timeOfDay = TimeOfDay.NIGHT;
                            }
                            if (timetable.numberOf(timeOfDay) == 5 && editable == null) {
                                Context context = getApplicationContext();
                                CharSequence text = "Only 5 Switches of Each Type Please";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                return;
                            }
                            String time = String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                            // Create switch in storage
                            TemperatureSwitch tSwitch = new TemperatureSwitch();
                            tSwitch.setTimeOfDay(timeOfDay);
                            tSwitch.setTime(time);
                            tSwitch.setDay(day);
                            if (editable != null) {
                                timetable.removeSwitch(editable);
                            }
                            if (timetable.addSwitch(tSwitch)) {
                                inflateSwitches();
                            } else {
                                Context context = getApplicationContext();
                                CharSequence text = "Two Switches For One Moment Are Not Allowed";
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SwitchPickerFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}