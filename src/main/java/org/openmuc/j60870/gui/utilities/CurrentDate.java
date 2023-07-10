package org.openmuc.j60870.gui.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDate {

    public String getCurrentDate(String formatForDate) {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat(formatForDate);
        return formatForDateNow.format(new Date());
    }
}
