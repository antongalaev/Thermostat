package com.galaev.thermostat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.galaev.thermostat.R;

/**
 * Week representation.
 */
public class WeekOverview extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_overview);
        Button[] days = new Button[7];
        days[0] = (Button) findViewById(R.id.monday);
        days[1] = (Button) findViewById(R.id.tuesday);
        days[2] = (Button) findViewById(R.id.wednesday);
        days[3] = (Button) findViewById(R.id.thursday);
        days[4] = (Button) findViewById(R.id.friday);
        days[5] = (Button) findViewById(R.id.saturday);
        days[6] = (Button) findViewById(R.id.sunday);

        for (int i = 0; i < days.length; ++ i) {
            final int dayNumber = i;
            days[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DaySwitches.class);
                    intent.putExtra("DAY", dayNumber);
                    startActivity(intent);
                }
            });
        }

	}
}
