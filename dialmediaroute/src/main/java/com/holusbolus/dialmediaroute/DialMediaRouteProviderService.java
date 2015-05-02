package com.holusbolus.dialmediaroute;

import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderService;
import android.util.Log;

/**
 * Created by Stuart on 01/05/15.
 */
public class DialMediaRouteProviderService extends MediaRouteProviderService {
    private static final String TAG = MediaRouteProviderService.class.getSimpleName();

    @Override
    public MediaRouteProvider onCreateMediaRouteProvider() {
        Log.d(TAG, "onCreateMediaRouteProvider");
        return new DialMediaRouteProvider(this);
    }
}
