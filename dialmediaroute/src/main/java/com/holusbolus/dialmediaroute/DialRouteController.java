package com.holusbolus.dialmediaroute;

import android.content.Intent;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouter;
import android.util.Log;

import com.entertailion.android.dial.DialServer;


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
        new StartDialAppThread(dialServer).start();
    }

    @Override
    public void onUnselect() {
        Log.d(TAG,"OnUnselect " + dialServer);

    }

    @Override
    public void onSetVolume(int volume) {
        super.onSetVolume(volume);
    }

    @Override
    public void onUpdateVolume(int delta) {
        super.onUpdateVolume(delta);
    }

    @Override
    public boolean onControlRequest(Intent intent, MediaRouter.ControlRequestCallback callback) {
        return super.onControlRequest(intent, callback);
    }

    @Override
    public void onUnselect(int reason) {
        Log.d(TAG,"OnUnselect " + reason + " " + dialServer);
    }
}
