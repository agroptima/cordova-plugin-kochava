package com.kochava.sdk;

import android.content.Context;
import android.util.Log;
import android.content.SharedPreferences;
import android.util.Base64;

import com.kochava.base.AttributionListener;
import com.kochava.base.ConsentStatusChangeListener;
import com.kochava.base.Tracker;
import com.kochava.base.Tracker.IdentityLink;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

public class KochavaTrackerPlugin extends CordovaPlugin implements AttributionListener, ConsentStatusChangeListener {
    /**
     * Log TAG
     */
    private static final String LOGTAG = "KO/TR/CO/";
    /**
     * Application context.
     */
    private Context context;
    /**
     * Attribution listener.
     *
     * This is set or not based on the input parameters during configuration.
     */
    @Override
    public final void onAttributionReceived(final String attribution) {
        webView.loadUrl("javascript:window.attributionNotification.notificationCallback('"+Base64.encodeToString(attribution.getBytes(), Base64.DEFAULT)+"');");
    }

    /**
    * Consent Status Change Listener.
    */
    @Override
    public final void onConsentStatusChange() {
        webView.loadUrl("javascript:window.consentStatusChangeNotification.notificationCallback('{}');");
    }

    /**
     * Initialize the Plugin.
     * @param cordova Cordova interface interface.
     * @param webView Cordova webview interface.
     */
    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        if(context == null && cordova != null && cordova.getActivity() != null) {
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
     * Coerces the input Object into a Boolean if possible. If not null is returned.
     *
     * @param object Input Object.
     * @return Resulting Boolean or null if unable to coerce.
     */
    private static Boolean optBoolean(final Object object) {
        if (object instanceof Boolean) {
            return (Boolean) object;
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

    /**
     * Converts a json object that contains identity link information into an identity link object.
     *
     * @param identityLinkJson Input json object.
     * @return Identity link object or null on invalid input.
     */
    private static IdentityLink optIdentityLink(final JSONObject identityLinkJson) {
        if(identityLinkJson == null || identityLinkJson.length() == 0) {
            return null;
        }

        final IdentityLink identityLink = new IdentityLink();
        final Iterator<String> keys = identityLinkJson.keys();

        while(keys.hasNext()) {
            final String key = keys.next();
            final Object value = identityLinkJson.opt(key);
            if(value instanceof String) {
                identityLink.add(key, (String) value);
            }
        }

        return identityLink;
    }

    /**
     * Performs an action within the sdk as defined by the action parameter.
     *
     * @param action to perform.
     * @param args is a list of arguments to pass to the specific action.
     * @param callbackContext to pass to results of the action.
     * @return True if completed and false if unable to complete.
     * @throws JSONException if something went wrong with the arguments.
     */
    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action == null || action.length() == 0 || args == null) {
            return false;
        }
        Log.i(LOGTAG,"action is: " + action + " and there are " + args.length() + " args");

        //Configure the Tracker.
        if (action.equals("configure") && args.length() == 1) {
            return configure(optJsonObject(args.opt(0)));
        }

        //Send an event with string.
        if (action.equals("sendEventString") && args.length() == 2) {
            return sendEventString(args.optString(0, null), args.optString(1, null));
        }

        //Send an event with map object.
        if (action.equals("sendEventMapObject") && args.length() == 2) {
            return sendEventMapObject(args.optString(0, null), optJsonObject(args.opt(1)));
        }

        //(iOS Only) Send an event with apple store receipt.
        if (action.equals("sendEventAppleAppStoreReceipt")) {
            Log.i(LOGTAG,"sendEventAppleAppStoreReceipt doesn't apply on this OS");
            return false;
        }

        //(Android Only) Send an event with google store receipt.
        if (action.equals("sendEventGooglePlayReceipt") && args.length() == 4) {
            return sendEventGooglePlayReceipt(args.optString(0, null), optJsonObject(args.opt(1)), args.optString(2, null), args.optString(3, null));
        }

        //Sets an identity link.
        if (action.equals("setIdentityLink") && args.length() == 1) {
            return setIdentityLink(optJsonObject(args.opt(0)));
        }

        //Sends a deep link event.
        if (action.equals("sendDeepLink") && (args.length() == 1 || args.length() == 2)) {
            return sendDeepLink(args.optString(0, null));
        }

        //Sets app limit ad tracking.
        if(action.equals("setAppLimitAdTracking") && args.length() == 1)
        {
            return setAppLimitAdTracking(optBoolean(args.opt(0)));
        }

        //Retrieves attribution.
        if(action.equals("getAttribution")) {
            callbackContext.success(getAttribution());
            return true;
        }

        //Retrieves the device id.
        if(action.equals("getDeviceId")) {
            callbackContext.success(getDeviceId());
            return true;
        }

        //Retrieves the sdk version.
        if(action.equals("getVersion")) {
            callbackContext.success(getVersion());
            return true;
        }

        //Adds a push token.
        if(action.equals("addPushToken") && args.length() == 1) {
            return addPushToken(args.optString(0, null));
        }

        //Removes a push token.
        if(action.equals("removePushToken") && args.length() == 1) {
            return removePushToken(args.optString(0, null));
        }

        //Sets consent granted.
        if(action.equals("setConsentGranted") && args.length() == 1) {
            return setConsentGranted(optBoolean(args.opt(0)));
        }

        //Clears the shouldPrompt boolean.
        if(action.equals("setConsentPrompted")) {
            return setConsentPrompted();
        }

        //Returns the consent status (required, granted, partners, shouldPrompt, etc)
        if(action.equals("getConsentStatus")) {
            callbackContext.success(getConsentStatus());
            return true;
        }

        Log.i(LOGTAG,"Invalid Action");
        return false;
    }

    /**
     * Configures the Tracker given a series of input configuration items. This being successful does not indicate the Tracker was successfully configured.
     * Most of the validation of input parameters occurs within the native.
     *
     * @param input json object of parameters.
     * @return False if the input was null. Otherwise true.
     */
    private boolean configure(final JSONObject input) {
        if(input == null || input.length() == 0) {
            return false;
        }

        //Check if we are unconfiguring the tracker.
        if(input.has("INTERNAL_UNCONFIGURE")) {
            return unConfigureTracker();
        }

        //Check if we are resetting the tracker.
        if(input.has("INTERNAL_RESET")) {
            return resetTracker();
        }

        //Create the configuration object and pass it our context.
        final Tracker.Configuration configuration = new Tracker.Configuration(context);

        //Check for the App Guid.
        final String appGuid = input.optString("androidAppGUIDString", null);
        if (appGuid != null) {
            configuration.setAppGuid(appGuid);
        }
        input.remove("androidAppGUIDString");
        input.remove("iOSAppGUIDString");
        input.remove("windowsAppGUIDString");

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
            configuration.setAttributionListener(this);
        }
        input.remove("retrieveAttribution");

        //Check for app limit ad tracking.
        final Boolean appLimitAdTracking = optBoolean(input.opt("limitAdTracking"));
        if(appLimitAdTracking != null) {
            configuration.setAppLimitAdTracking(appLimitAdTracking);
        }
        input.remove("limitAdTracking");

        //Check if using the consent management system.
        final Boolean intelligentConsentManagement = optBoolean(input.opt("consentIntelligentManagement"));
        if(intelligentConsentManagement != null) {
            configuration.setIntelligentConsentManagement(intelligentConsentManagement);
            if(intelligentConsentManagement) {
                configuration.setConsentStatusChangeListener(this);
            }
        }
        input.remove("consentIntelligentManagement");

        //Check for Identity Link.
        final JSONObject identityLinkJson = optJsonObject(input.opt("identityLink"));
        final IdentityLink identityLink = optIdentityLink(identityLinkJson);
        if(identityLink != null) {
            configuration.setIdentityLink(identityLink);
        }
        input.remove("identityLink");

        //Check for Version Extension and Wrapper Build Date.
        final String ext = input.optString("versionExtension", null);
        final String buildDate = input.optString("wrapperBuildDateString", null);
        Tracker.ext(ext, buildDate);
        input.remove("versionExtension");
        input.remove("wrapperBuildDateString");

        //Apply the hostname override.
        final SharedPreferences.Editor ov = context.getSharedPreferences("koov", Context.MODE_PRIVATE).edit();
        final String urlInit = input.optString("initNetTransactionURLString", null);
        input.remove("initNetTransactionURLString");
        if(urlInit != null) {
            ov.putString("url_init", urlInit);
        }
        final String urlPushTokenAdd = input.optString("remoteNotificationsDeviceTokenAddNetTransactionURLString", null);
        input.remove("remoteNotificationsDeviceTokenAddNetTransactionURLString");
        if(urlPushTokenAdd != null) {
            ov.putString("url_push_token_add", urlPushTokenAdd);        
        }
        final String urlPushTokenRemove = input.optString("remoteNotificationsDeviceTokenRemoveNetTransactionURLString", null);
        input.remove("remoteNotificationsDeviceTokenRemoveNetTransactionURLString");
        if(urlPushTokenRemove != null) {
            ov.putString("url_push_token_remove", urlPushTokenRemove);           
        }
        final String urlGetAttribution = input.optString("getAttributionNetTransactionURLString", null);
        input.remove("getAttributionNetTransactionURLString");
        if(urlGetAttribution != null) {
            ov.putString("url_get_attribution", urlGetAttribution); 
        }
        final String urlInitial = input.optString("initialNetTransactionURLString", null);
        input.remove("initialNetTransactionURLString");
        if(urlInitial != null) {
            ov.putString("url_initial", urlInitial); 
        }
        final String urlUpdate = input.optString("updateNetTransactionURLString", null);
        input.remove("updateNetTransactionURLString");
        if(urlUpdate != null) {
            ov.putString("url_update", urlUpdate); 
        }
        final String urlIdentityLink = input.optString("identityLinkNetTransactionURLString", null);
        input.remove("identityLinkNetTransactionURLString");
        if(urlIdentityLink != null) {
            ov.putString("url_identity_link", urlIdentityLink); 
        }
        final String urlEvent = input.optString("arrayNetTransactionURLString", null);
        input.remove("arrayNetTransactionURLString");
        if(urlEvent != null) {
            ov.putString("url_event", urlEvent); 
        }
        ov.apply();

        //Remove iOS specific overrides.
        input.remove("deepLinkNetTransactionURLString");
        input.remove("eventNetTransactionURLString");
        input.remove("sessionNetTransactionURLString");

        //Add all remaining items as custom parameters.
        configuration.addCustom(input);

        //Configure the tracker.
        Tracker.configure(configuration);

        return true;
    }

    /**
     * Sends an event with a generic string for event data. If eventData is null the name alone will be sent.
     *
     * @param eventName used to name the event.
     * @param eventData generic info string of event data.
     * @return True.
     */
    private boolean sendEventString(final String eventName, final String eventData) {
        if(eventData == null) {
            Tracker.sendEvent(eventName, "");
        } else {
            Tracker.sendEvent(eventName, eventData);
        }
        return true;
    }

    /**
     * Sends an event with a map object. If eventData is null the name alone will be sent.
     *
     * @param eventName used to name the event.
     * @param eventData json object map of event data.
     * @return True.
     */
    private boolean sendEventMapObject(final String eventName, final JSONObject eventData) {
        if(eventData == null) {
            Tracker.sendEvent(eventName, "");
        } else {
            Tracker.sendEvent(eventName, eventData.toString());
        }
        return true;
    }

    /**
     * Sends an event with a google play receipt.
     *
     * @param eventName used to name the event.
     * @param eventData map of extra event data. May be null.
     * @param receiptJson from the play services in app billing library.
     * @param receiptSignature from the play services in ap billing library.
     * @return True.
     */
    private boolean sendEventGooglePlayReceipt(final String eventName, final JSONObject eventData, final String receiptJson, final String receiptSignature) {
        final Tracker.Event event = new Tracker.Event(eventName);
        event.setGooglePlayReceipt(receiptJson, receiptSignature);
        event.addCustom(eventData);
        Tracker.sendEvent(event);
        return true;
    }

    /**
     * Sets an identity link.
     *
     * @param identityLinkJson identity link json object.
     * @return True.
     */
    private boolean setIdentityLink(final JSONObject identityLinkJson) {
        final IdentityLink identityLink = optIdentityLink(identityLinkJson);
        Tracker.setIdentityLink(identityLink);
        return true;
    }

    /**
     * Sends a deeplink event.
     *
     * @param uri to pass with event.
     * @return True.
     */
    private boolean sendDeepLink(final String uri) {
        Tracker.sendEventDeepLink(uri);
        return true;
    }

    /**
     * Sets app limit ad tracking.
     *
     * @param appLimitAdTracking True to enable, False to disable. Default == false.
     * @return True if appLimitAdTracking was non null. False if appLimitAdTracking was null.
     */
    private boolean setAppLimitAdTracking(final Boolean appLimitAdTracking) {
        if(appLimitAdTracking == null) {
            return false;
        }
        Tracker.setAppLimitAdTracking(appLimitAdTracking);
        return true;
    }

    /**
     * @return The saved attribution or an empty string if it does not exist or the tracker is not configured.
     */
    private String getAttribution() {
        return Tracker.getAttribution();
    }

    /**
     * @return The device id or an empty string if the tracker is not configured.
     */
    private String getDeviceId() {
        return Tracker.getDeviceId();
    }

    /**
     * @return The sdk version or an empty string if the tracker is not configured.
     */
    private String getVersion() {
        return Tracker.getVersion();
    }

    /**
     * Adds a push token.
     *
     * @param token to add.
     * @return True.
     */
    private boolean addPushToken(final String token) {
        Tracker.addPushToken(token);
        return true;
    }

    /**
     * Removes a push token.
     *
     * @param token to remove.
     * @return True.
     */
    private boolean removePushToken(final String token) {
        Tracker.removePushToken(token);
        return true;
    }

    /**
     * UnConfigures the Tracker. It is preferable for the SDK to be idle when this occurs.
     *
     * @return true
     */
    private boolean unConfigureTracker() {
        // unConfigure the tracker.
        try {
            final Method method = Tracker.class.getDeclaredMethod("unConfigure");
            method.setAccessible(true);
            method.invoke(null);
        }
        catch (final Exception e) {
            Log.w(LOGTAG, Log.getStackTraceString(e));
        }

        // Return that it was successful.
        return true;
    }

    /**
     * Resets the tracker saved state. The Tracker must be in the UnConfigured state to run.
     *
     * @return true
     */
    private boolean resetTracker() {
        // Delete the database.
        context.deleteDatabase("kodb");

        // Clear shared preferences
        final SharedPreferences sp = context.getSharedPreferences("kosp", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        final SharedPreferences ov = context.getSharedPreferences("koov", Context.MODE_PRIVATE);
        ov.edit().clear().apply();

        // Return that it was successful.
        return true;
    }

    /**
    * Sets consent to either granted or declined as specified by the user.
    */
    private boolean setConsentGranted(Boolean granted) {
        if(granted == null) {
            return false;
        }
        Tracker.setConsentGranted(granted);
        return true;
    }

    /**
    * Clears the consent should prompt boolean.
    */
    private boolean setConsentPrompted() {
        Tracker.clearConsentShouldPrompt();
        return true;
    }

    /**
    * serialized json object containing.
    *   description (String)
    *   required (boolean)
    *   granted (boolean)
    *   response_time (long)
    *   should_prompt (boolean)
    *   partners (json array)
    */
    private String getConsentStatus() {
        final JSONObject consentStatus = new JSONObject();
        try {
            consentStatus.put("description", Tracker.getConsentDescription());
            consentStatus.put("required", Tracker.isConsentRequired());
            consentStatus.put("granted", Tracker.isConsentGranted());
            consentStatus.put("response_time", Tracker.getConsentResponseTime());
            consentStatus.put("should_prompt", Tracker.isConsentShouldPrompt());
            consentStatus.put("partners", new JSONArray(Tracker.getConsentPartnersJson()));
        } catch(Throwable t) {
            t.printStackTrace();
        }
        return consentStatus.toString();
    }

}
