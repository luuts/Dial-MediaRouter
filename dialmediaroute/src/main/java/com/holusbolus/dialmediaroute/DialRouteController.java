package com.holusbolus.dialmediaroute;

import android.support.v7.media.MediaRouteProvider;
import android.util.Log;

import com.entertailion.android.dial.DialServer;

/**
 * Created by Stuart on 01/05/15.
 */
public class DialRouteController extends MediaRouteProvider.RouteController {
    private static final String TAG = DialRouteController.class.getSimpleName();
    private DialServer dialServer;

    public DialRouteController(DialServer dialServer) {

        this.dialServer = dialServer;
    }

    @Override
    public void onRelease() {
        Log.d(TAG,"OnRelase " + dialServer);
    }

    @Override
    public void onSelect() {
        Log.d(TAG,"OnSelect " + dialServer);

    }

    @Override
    public void onUnselect() {
        Log.d(TAG,"OnUnselect " + dialServer);

    }

    @Override
    public void onUnselect(int reason) {
        Log.d(TAG,"OnUnselect " + reason + " " + dialServer);
    }
}
