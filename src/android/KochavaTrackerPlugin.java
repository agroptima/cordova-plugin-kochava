package com.kochava.sdk;

import android.content.Context;
import android.util.Log;

import com.kochava.base.AttributionListener;
import com.kochava.base.Tracker;
import com.kochava.base.Tracker.IdentityLink;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class KochavaTrackerPlugin extends CordovaPlugin {
    /**
     * TAG
     */
    private static final String LOGTAG = "KO/CO/";
    /**
     * Application context.
     */
    private Context context;
    /**
     * Attribution listener that is passed to the Kochava SDK.
     */
    private final AttributionListener attributionListener = new AttributionListener() {
        @Override
        public final void onAttributionReceived(final String attribution) {
            webView.loadUrl("javascript:window.attributionNotification.notificationCallback('"+attribution+"');");
        }
    };

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        if(context == null) {
            context = cordova.getActivity().getApplicationContext();
        }
        Log.i(LOGTAG,"Plugin Initialization");
    }

    /**
     * Coerces the input Object into a JSONObject if possible. If not null is returned.
     * <p>
     * No version exists with default value support.
     *
     * @param object Input Object
     * @return Resulting JSONObject or null if unable to coerce.
     */
    private static JSONObject optJsonObject(final Object object) {
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        if (object instanceof String) {
            try {
                return new JSONObject((String) object);
            }
            catch (final Throwable ignore) {
                //Intentially ignored.
            }
        }
        return null;
    }

    /**
     * Coerces a String or Integer into the proper log level. Returning the given default value if unable.
     *
     * @param object       Input Object
     * @param defaultValue Default Value.
     * @return Resulting LogLevel or defaultValue if unable to coerce.
     */
    private static int optLogLevel(final Object object, final int defaultValue) {
        if (object instanceof Integer) {
            final int logLevel = (Integer) object;
            if (logLevel >= Tracker.LOG_LEVEL_NONE && logLevel <= Tracker.LOG_LEVEL_TRACE) {
                return logLevel;
            }
        }
        else if (object instanceof String) {
            final String logLevel = (String) object;
            if ("NONE".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_NONE;
            }
            else if ("ERROR".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_ERROR;
            }
            else if ("WARN".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_WARN;
            }
            else if ("INFO".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_INFO;
            }
            else if ("DEBUG".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_DEBUG;
            }
            else if ("TRACE".equalsIgnoreCase(logLevel)) {
                return Tracker.LOG_LEVEL_TRACE;
            }
        }
        return defaultValue;
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action == null || args == null) {
            return false;
        }
        Log.i(LOGTAG,"action is: " + action + " and there are " + args.length() + " args");

        if (action.equals("configure")) {
            return configure(optJsonObject(args.opt(0)));
        }
        if(!Tracker.isConfigured()) {
            Log.i(LOGTAG,"Kochava library has not been initialized yet");
            return false;
        }
        if (action.equals("sendEventString")) {
            return sendEventString(args.optString(0), args.optString(1));
        }
        if (action.equals("sendEventMapObject")) {
            return sendEventMapObject(args.optString(0), optJsonObject(args.opt(1)));
        }
        if (action.equals("sendEventAppleAppStoreReceipt")) {
            Log.i(LOGTAG,"sendEventAppleAppStoreReceipt doesn't apply on this OS");
            return false;
        }
        if (action.equals("sendEventGooglePlayReceipt")) {
            return sendEventGooglePlayReceipt(args.optString(0), optJsonObject(args.opt(1)), args.optString(2), args.optString(3));
        }
        if (action.equals("sendIdentityLink")) {
            return sendIdentityLink(optJsonObject(args.opt(0)));
        }
        if (action.equals("sendDeepLink")) {
            return sendDeepLink(args.optString(0));
        }
        if(action.equals("setAppLimitAdTracking"))
        {
            return setAppLimitAdTracking(args.optBoolean(0));
        }
        if(action.equals("getAttribution")) {
            callbackContext.success(getAttribution());
            return true;
        }
        if(action.equals("getDeviceId")) {
            callbackContext.success(getDeviceId());
            return true;
        }
        if(action.equals("getVersion")) {
            callbackContext.success(getVersion());
            return true;
        }
        if(action.equals("addPushToken")) {
            return addPushToken(args.optString(0));
        }
        if(action.equals("removePushToken")) {
            return removePushToken(args.optString(0));
        }
        Log.i(LOGTAG,"Invalid action");
        return false;
    }


    private boolean configure(JSONObject input) {
        if(input == null) {
            return false;
        }

        //Create the configuration object and pass it our context.
        Tracker.Configuration configuration = new Tracker.Configuration(context);

        //Check for the App Guid.
        final String appGuid = input.optString("androidAppGUIDString", null);
        if (appGuid != null) {
            configuration.setAppGuid(appGuid);
        }
        input.remove("androidAppGUIDString");

        //Check for the partner name.
        final String partnerName = input.optString("partner_name", null);
        if (partnerName != null) {
            configuration.setPartnerName(partnerName);
        }
        input.remove("partner_name");

        //Check for the log level. Do not set if invalid.
        final int logLevel = optLogLevel(input.opt("logLevelEnum"), -1);
        if (logLevel != -1) {
            configuration.setLogLevel(logLevel);
        }
        input.remove("logLevelEnum");

        //Check for request attribution.
        if (input.optBoolean("retrieveAttribution", false)) {
            configuration.setAttributionListener(attributionListener);
        }
        input.remove("retrieveAttribution");

        //Check for app limit ad tracking.
        if (input.has("limitAdTracking")) {
            final Boolean appLimitAdTracking = input.optBoolean("limitAdTracking", false);
            configuration.setAppLimitAdTracking(appLimitAdTracking);
        }
        input.remove("limitAdTracking");

        //Check for Identity Link.
        try {
            final IdentityLink identityLink = new IdentityLink();
            final JSONObject identityLinkJson = optJsonObject(input.opt("identityLink"));
            final Iterator<String> identityLinkIterator = identityLinkJson.keys();
            while (identityLinkIterator.hasNext()) {
                final String key = identityLinkIterator.next();
                final String value = identityLinkJson.optString(key);
                if (value != null) {
                    identityLink.add(key, value);
                }
            }
            configuration.setIdentityLink(identityLink);
        }
        catch (Throwable t) {
            Log.w(LOGTAG + "configure", t);
        }
        input.remove("identityLink");


        //Check for Version Extension.
        final String ext = input.optString("versionExtension", null);
        Tracker.ext(ext);
        input.remove("versionExtension");

        //Remove iOS items from the json object.
        input.remove("iOSAppGUIDString");
        input.remove("trackerHostname");

        //Add all remaining items as custom parameters.
        configuration.addCustom(input);

        //Configure the tracker.
        Tracker.configure(configuration);

        return true;
    }

    private boolean sendEventString(String eventName, String eventData) {
        if(eventName == null || eventData == null) {
            return false;
        }
        Tracker.sendEvent(eventName, eventData);
        return true;
    }

    private boolean sendEventMapObject(String eventName, JSONObject eventData) {
        if(eventName == null || eventData == null) {
            return false;
        }
        Tracker.sendEvent(eventName, eventData.toString());
        return true;
    }

    private boolean sendEventGooglePlayReceipt(String eventName, JSONObject eventData, String receiptJson, String receiptSignature) {
        if(eventName == null || eventData == null || receiptJson == null || receiptSignature == null) {
            return false;
        }

        Tracker.Event event = new Tracker.Event(eventName);
        event.setGooglePlayReceipt(receiptJson, receiptSignature);
        if(eventData != null) {
            event.addCustom(eventData);
        }
        Tracker.sendEvent(event);
        return true;
    }

    private boolean sendIdentityLink(JSONObject identityLinkJson) {
        if(identityLinkJson == null) {
            return false;
        }
        Tracker.IdentityLink identityLink = new Tracker.IdentityLink();
        Iterator<String> keys = identityLinkJson.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = identityLinkJson.opt(key);
            if(value instanceof String) {
                identityLink.add(key, (String) value);
            }
        }
        Tracker.setIdentityLink(identityLink);
        return true;
    }

    private boolean sendDeepLink(String uri) {
        if(uri == null) {
            return false;
        }
        Tracker.sendEventDeepLink(uri);
        return true;
    }

    private boolean setAppLimitAdTracking(boolean appLimitAdTracking) {
        Tracker.setAppLimitAdTracking(appLimitAdTracking);
        return true;
    }

    private String getAttribution() {
        return Tracker.getAttribution();
    }

    private String getDeviceId() {
        return Tracker.getDeviceId();
    }

    private String getVersion() {
        return Tracker.getVersion();
    }

    private boolean addPushToken(String token) {
        if(token == null) {
            return false;
        }
        Tracker.addPushToken(token);
        return true;
    }

    private boolean removePushToken(String token) {
        if(token == null) {
            return false;
        }
        Tracker.removePushToken(token);
        return true;
    }

}
