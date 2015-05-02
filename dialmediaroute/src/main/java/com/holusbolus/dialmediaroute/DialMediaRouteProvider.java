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

import java.net.InetAddress;
import java.net.UnknownHostException;
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

        try {
            InetAddress broadcastAddress = InetAddress.getByName("239.255.255.250");
            BroadcastDiscoveryClient broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, mainHandler);
            Thread broadcastClientThread = new Thread(broadcastClient);
            broadcastClientThread.start();

        } catch (RuntimeException e) {
            Log.e(TAG, "startBroadcast", e);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        worker.scheduleAtFixedRate(scanRunnable, 0, 60, TimeUnit.SECONDS);
//        worker.schedule(scanRunnable, 5, TimeUnit.SECONDS);
    }

    private void addRoute(DialServer dialServer) {
        IntentFilter filter = new IntentFilter();
        filter.addCategory(CATEGORY);
        filter.addAction(MediaControlIntent.ACTION_PLAY);
        filter.addAction(MediaControlIntent.ACTION_PAUSE);
        MediaRouteDescriptor descriptor = new MediaRouteDescriptor.Builder(dialServer.getUuid(), dialServer.getFriendlyName()).addControlFilter(filter).build();
        MediaRouteProviderDescriptor providerDescriptor = new MediaRouteProviderDescriptor.Builder().addRoute(descriptor).build();
        setDescriptor(providerDescriptor);
        Log.d(TAG, "Setting routes  " + providerDescriptor.toString());
    }

    @Override
    public void onDiscoveryRequestChanged(MediaRouteDiscoveryRequest request) {
        Log.d(TAG, "onDiscoverRequestChanged " + request.toString());
        super.onDiscoveryRequestChanged(request);
    }

    @Nullable
    @Override
    public RouteController onCreateRouteController(String routeId) {
        Log.d(TAG, "onCreateRouteController " + routeId);
        return new DialRouteController(routeId);
    }

}
