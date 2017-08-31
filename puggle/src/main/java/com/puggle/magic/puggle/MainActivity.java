package com.puggle.magic.puggle;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;


import com.puggle.magic.puggle.calendar.horizontal.HorizontalCalendar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeZone seoulTime = TimeZone.getTimeZone("Asia/Seoul");

        Calendar endDate = Calendar.getInstance(seoulTime);
        endDate.add(Calendar.MONTH, 1);

        Calendar startDate = Calendar.getInstance(seoulTime);
        startDate.add(Calendar.MONTH, -1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(findViewById(R.id.horizontalCalendar), R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(7)   // Number of Dates cells shown on screen (Recommended 5)
                .dayNameFormat("E")      // WeekDay text format
                .dayNumberFormat("d")    // Date format
                .dayNameLocale(Locale.KOREA)
                .dayNumberLocale(Locale.KOREA)
                .showDayName(true)      // Show or Hide dayName text
                .showMonthName(false)      // Show or Hide month text
                .textColor(Color.LTGRAY, Color.WHITE)    // Text color for none selected Dates, Text color for selected Date.
                .selectedDateBackground(Color.TRANSPARENT)  // Background color of the selected date cell.
                .selectorColor(Color.RED)   // Color of the selection indicator bar (default to colorAccent).
                .build();
    }

}
