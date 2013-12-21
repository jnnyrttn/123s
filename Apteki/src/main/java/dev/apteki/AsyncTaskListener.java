package dev.apteki;

import org.json.JSONArray;

/**
 * Created by pawel on 13.12.13.
 */
public interface AsyncTaskListener {
    public void onTaskComplete(JSONArray result);
}
