package com.puggle.magic.puggle;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.puggle.magic.puggle.calendar.horizontal.HorizontalCalendar;
import com.puggle.magic.puggle.calendar.horizontal.HorizontalCalendarView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        TimeZone seoulTime = TimeZone.getTimeZone("Asia/Seoul");

        Calendar endDate = Calendar.getInstance(seoulTime);
        endDate.add(Calendar.MONTH, 1);

        Calendar startDate = Calendar.getInstance(seoulTime);
        startDate.add(Calendar.MONTH, -1);

        HorizontalCalendar calendarView = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(7)   // Number of Dates cells shown on screen (Recommended 5)
                .dayNameFormat("E")	  // WeekDay text format
                .dayNumberFormat("d")    // Date format
                .dayNameLocale(Locale.KOREA)
                .dayNumberLocale(Locale.KOREA)
                .showDayName(true)	  // Show or Hide dayName text
                .showMonthName(false)	  // Show or Hide month text
                .textColor(Color.LTGRAY, Color.WHITE)    // Text color for none selected Dates, Text color for selected Date.
                .selectedDateBackground(Color.TRANSPARENT)  // Background color of the selected date cell.
                .selectorColor(Color.RED)   // Color of the selection indicator bar (default to colorAccent).
                .build();
    }
}
