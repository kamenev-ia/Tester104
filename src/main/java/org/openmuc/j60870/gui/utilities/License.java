package org.openmuc.j60870.gui.utilities;

import java.time.LocalDate;

public class License {
    private boolean isLicenseActive() {
        return LocalDate.now().getYear() <= 2027;
    }

    public boolean isLicenseStatus() {
        isLicenseActive();
        return isLicenseActive();
    }
}
