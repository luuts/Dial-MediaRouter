package com.holusbolus.dialmediaroute;

import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderService;
import android.util.Log;


public class DialMediaRouteProviderService extends MediaRouteProviderService {
    private static final String TAG = DialMediaRouteProviderService.class.getSimpleName();

    @Override
    public MediaRouteProvider onCreateMediaRouteProvider() {
        Log.d(TAG, "onCreateMediaRouteProvider");
        return new DialMediaRouteProvider(this);
    }
}
