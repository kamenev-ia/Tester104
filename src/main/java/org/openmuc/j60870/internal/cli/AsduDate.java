package org.openmuc.j60870.internal.cli;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AsduDate {
    public String getAsduDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
        return format.format(date);
    }
}
