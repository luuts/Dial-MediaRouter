package com.holusbolus.dialmediarouter;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.holusbolus.dialmediaroute.DialMediaRouteProvider;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaRouteSelector mSelector;
    private MediaRouter mMediaRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaRouter = MediaRouter.getInstance(this);

        mSelector = new MediaRouteSelector.Builder()
                .addControlCategory(DialMediaRouteProvider.CATEGORY + "/" + "uk.co.bbc.iPlayer")
                .build();
    }

    @Override
    public void onStart() {
        mMediaRouter.addCallback(mSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
        super.onStart();
    }

    @Override
    public void onStop() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider) MenuItemCompat.getActionProvider(
                        mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mSelector);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {

                @Override
                public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRouteSelected: route=" + route);

                    if (route.supportsControlCategory(
                            MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)){
                        // remote playback device
                        updateRemotePlayer(route);
                    } else {
                        // secondary output device
                        updatePresentation(route);
                    }
                }

                @Override
                public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRouteUnselected: route=" + route);

                    if (route.supportsControlCategory(
                            MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)){
                        // remote playback device
                        updateRemotePlayer(route);
                    } else {
                        // secondary output device
                        updatePresentation(route);
                    }
                }

                @Override
                public void onRoutePresentationDisplayChanged(
                        MediaRouter router, MediaRouter.RouteInfo route) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged: route=" + route);

                    if (route.supportsControlCategory(
                            MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)){
                        // remote playback device
                        updateRemotePlayer(route);
                    } else {
                        // secondary output device
                        updatePresentation(route);
                    }
                }
            };

    private void updatePresentation(MediaRouter.RouteInfo route) {

    }

    private void updateRemotePlayer(MediaRouter.RouteInfo route) {
    }
}
