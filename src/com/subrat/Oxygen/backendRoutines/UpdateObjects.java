package com.subrat.Oxygen.backendRoutines;

import android.os.AsyncTask;
import com.subrat.Oxygen.customviews.OxygenView;
import com.subrat.Oxygen.objects.*;
import com.subrat.Oxygen.objects.Object;

/**
 * Created by subrat.panda on 08/05/15.
 */
public class UpdateObjects extends AsyncTask<OxygenView, Void, Void> {
    private OxygenView oxygenView;

    @Override
    protected Void doInBackground(OxygenView... params) {
        updateAllObjects();
        oxygenView = params[0];
        System.out.println(oxygenView);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        oxygenView.invalidate();
    }

    private void updateAllObjects() {
        try {
            for (com.subrat.Oxygen.objects.Object object1 : Object.getObjectList()) {
                for (Object object2 : Object.getObjectList()) {
                    if (object2.getObjectId() > object1.getObjectId()) {
                        object1.checkCollision(object2);
                    }
                }
            }
        } catch (Exception e) {
        }

        for (Object object : Object.getObjectList()) {
            object.updatePosition();
        }
    }
}
