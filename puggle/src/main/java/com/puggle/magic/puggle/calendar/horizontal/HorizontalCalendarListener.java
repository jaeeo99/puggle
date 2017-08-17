package com.puggle.magic.puggle.calendar.horizontal;

import java.util.Date;

/**
 * Created by jaeeo99 on 2017. 8. 8..
 */

public abstract class HorizontalCalendarListener {
    public abstract void onDateSelected(Date date, int position);

    public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy){}

    public boolean onDateLongClicked(Date date, int position){
        return false;
    }
}
