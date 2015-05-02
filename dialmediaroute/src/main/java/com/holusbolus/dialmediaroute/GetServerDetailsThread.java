package com.holusbolus.dialmediaroute;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.entertailion.android.dial.BroadcastAdvertisement;
import com.entertailion.android.dial.DialServer;
import com.entertailion.android.dial.HttpRequestHelper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Stuart on 02/05/15.
 */
class GetServerDetailsThread extends Thread {
    private static final String HEADER_APPLICATION_URL = "Application-URL";
    public static final int DIAL_SERVER_FOUND = 249;
    private static final String TAG = GetServerDetailsThread.class.getSimpleName();
    private final BroadcastAdvertisement advert;
    private final Handler mainHandler;


    public GetServerDetailsThread(BroadcastAdvertisement advert, Handler mainHandler) {
        this.advert = advert;
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {
        HttpResponse response = new HttpRequestHelper().sendHttpGet(advert.getLocation());
        if (response != null) {
            String appsUrl = null;
            Header header = response.getLastHeader(HEADER_APPLICATION_URL);
            if (header != null) {
                appsUrl = header.getValue();
                Log.d(TAG, "appsUrl=" + appsUrl);
            }
            String friendlyName = null;
            String manufacturer = null;
            String modelName = null;
            String uuid = null;
            try {
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(reader);
                int eventType = parser.getEventType();
                String lastTagName = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String tagName = parser.getName();
                            lastTagName = tagName;
                            break;
                        case XmlPullParser.TEXT:
                            if (lastTagName != null) {
                                if ("friendlyName".equals(lastTagName)) {
                                    friendlyName = parser.getText();
                                } else if ("UDN".equals(lastTagName)) {
                                    uuid = parser.getText();
                                } else if ("manufacturer".equals(lastTagName)) {
                                    manufacturer = parser.getText();
                                } else if ("modelName".equals(lastTagName)) {
                                    modelName = parser.getText();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            tagName = parser.getName();
                            lastTagName = null;
                            break;
                    }
                    eventType = parser.next();
                }
                inputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "parse device description", e);
            }
            Log.d(TAG, "friendlyName=" + friendlyName);
            final DialServer dialServer = new DialServer(advert.getLocation(), advert.getIpAddress(), advert.getPort(), appsUrl, friendlyName, uuid, manufacturer, modelName);
            Message message = mainHandler.obtainMessage(DIAL_SERVER_FOUND, dialServer);
            mainHandler.sendMessage(message);
        }
    }
}
