package com.holusbolus.dialmediaroute;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteDescriptor;
import android.support.v7.media.MediaRouteDiscoveryRequest;
import android.support.v7.media.MediaRouteProvider;
import android.support.v7.media.MediaRouteProviderDescriptor;
import android.util.Log;

import com.entertailion.android.dial.BroadcastAdvertisement;
import com.entertailion.android.dial.BroadcastDiscoveryClient;
import com.entertailion.android.dial.DialServer;
import com.entertailion.android.dial.TrackedDialServers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DialMediaRouteProvider extends MediaRouteProvider {
    private static final String TAG = DialMediaRouteProvider.class.getSimpleName();
    public static final String CATEGORY = DialMediaRouteProvider.class.getPackage().toString();
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private final Handler mainHandler;
//    private final Runnable scanRunnable;
    private Thread getServerDetailsThread;
    private final TrackedDialServers trackedDialServers;
    private String appName;
    private boolean shouldSearch;
    private BroadcastDiscoveryClient broadcastClient;


    public DialMediaRouteProvider(Context context) {
        super(context);
        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "Handle message " + msg);
                if (msg.what == BroadcastDiscoveryClient.BROADCAST_RESPONSE) {
                    final BroadcastAdvertisement advert = (BroadcastAdvertisement) msg.obj;
                    if (advert.getLocation() != null) {
                        getServerDetailsThread = new GetServerDetailsThread(advert, mainHandler);
                        getServerDetailsThread.start();
                    }
                    return true;
                }
                else if (msg.what == GetServerDetailsThread.DIAL_SERVER_FOUND) {
                    DialServer dialServer = (DialServer) msg.obj;
                    addRoute(dialServer);
                    return true;
                }

                return false;
            }
        };
        mainHandler = new Handler(context.getMainLooper(), callback);
//        scanRunnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "Scanning...");
//                try {
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//        };


//        worker.scheduleAtFixedRate(scanRunnable, 0, 60, TimeUnit.SECONDS);
//        worker.schedule(scanRunnable, 5, TimeUnit.SECONDS);
        trackedDialServers = new TrackedDialServers();
    }

    private void addRoute(DialServer dialServer) {
        trackedDialServers.add(dialServer);

        IntentFilter filter = new IntentFilter();
        filter.addCategory(category(appName));
        filter.addAction(MediaControlIntent.ACTION_PLAY);
        filter.addAction(MediaControlIntent.ACTION_PAUSE);

        Iterator<DialServer> iterator = trackedDialServers.iterator();
        ArrayList<MediaRouteDescriptor> mediaRoutes = new ArrayList<MediaRouteDescriptor>();
        while (iterator.hasNext()) {
            DialServer ds = iterator.next();
            MediaRouteDescriptor descriptor = new MediaRouteDescriptor.Builder(ds.getUuid(), ds.getFriendlyName()).addControlFilter(filter).build();
            mediaRoutes.add(descriptor);
        }

        MediaRouteProviderDescriptor providerDescriptor = new MediaRouteProviderDescriptor.Builder().addRoutes(mediaRoutes).build();
        setDescriptor(providerDescriptor);
        Log.d(TAG, "Setting routes  " + providerDescriptor.toString());
    }

    @Override
    public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest request) {
        Log.d(TAG, "onDiscoverRequestChanged " + (request != null ? request.toString() : "- no request"));
        //TODO: add several categories for each appName found
        boolean requestIsDial = false;
        if (request != null) {
            List<String> categories = request.getSelector().getControlCategories();
            for (String category : categories) {
                int indexOfCategory = category.indexOf(CATEGORY);
                if (indexOfCategory >= 0) {
                    requestIsDial = true;
                    int startIndex = indexOfCategory + CATEGORY.length() + 1;
                    appName = category.substring(startIndex);
                    break;
                }
            }
        }
        shouldSearch = requestIsDial;
        if (shouldSearch) {
            try {
                stopSearch();
                InetAddress broadcastAddress = InetAddress.getByName("239.255.255.250");
                broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, mainHandler);
                Thread broadcastClientThread = new Thread(broadcastClient);
                broadcastClientThread.start();

            } catch (RuntimeException e) {
                Log.e(TAG, "startBroadcast", e);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        else {
            stopSearch();
        }
        super.onDiscoveryRequestChanged(request);
    }

    private void stopSearch() {
        if (broadcastClient != null) {
            broadcastClient.stop();
            broadcastClient = null;
        }
    }

    @Nullable
    @Override
    public RouteController onCreateRouteController(String routeId) {
        Log.d(TAG, "onCreateRouteController " + routeId);
        DialServer dialServer = trackedDialServers.findDialServer(routeId);

        return new DialRouteController(dialServer);
    }

    public static String category(String appName) {
        return CATEGORY + "/"+appName;
    }

}
