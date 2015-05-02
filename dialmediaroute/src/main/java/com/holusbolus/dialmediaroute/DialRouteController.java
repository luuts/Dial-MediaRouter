package com.holusbolus.dialmediaroute;

import android.support.v7.media.MediaRouteProvider;
import android.util.Log;

/**
 * Created by Stuart on 01/05/15.
 */
public class DialRouteController extends MediaRouteProvider.RouteController {
    private static final String TAG = DialRouteController.class.getSimpleName();
    private String routeId;

    public DialRouteController(String routeId) {

        this.routeId = routeId;
    }

    @Override
    public void onRelease() {
        Log.d(TAG,"OnRelase " + routeId);
    }

    @Override
    public void onSelect() {
        Log.d(TAG,"OnSelect " + routeId);

    }

    @Override
    public void onUnselect() {
        Log.d(TAG,"OnUnselect " + routeId);

    }

    @Override
    public void onUnselect(int reason) {
        Log.d(TAG,"OnUnselect " + reason + " " + routeId);
    }
}
