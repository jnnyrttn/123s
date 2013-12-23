package dev.apteki;

import android.location.Location;

/**
 * Created by pawel on 23.12.13.
 */
public interface LocationTaskListener {
    public void onLocationComplete(Location location);
}
