
/*
 * File Name  : KochavaTracker.js
 * Author     : Kochava
 * Description : This file initiates calls to the native library from the plugin
 */

function cordovaExecCommand(command) {
    var args = Array.prototype.slice.call(arguments, 1);
    cordova.exec(function callback(data) { },
        function errorHandler(err) { },
        'KochavaTrackerPlugin',
        command,
        args
    );
}

function cordovaExecCommandCallback(command, callback) {
    var args = Array.prototype.slice.call(arguments, 2);
    cordova.exec(callback,
        function errorHandler(err) { },
        'KochavaTrackerPlugin',
        command,
        args
    );
}

var KochavaTracker = {

    // Init Parameter MapObject Key Strings
    PARAM_ANDROID_APP_GUID_STRING_KEY: "androidAppGUIDString",
    PARAM_IOS_APP_GUID_STRING_KEY: "iOSAppGUIDString",
    PARAM_APP_LIMIT_AD_TRACKING_BOOL_KEY: "limitAdTracking",
    PARAM_IDENTITY_LINK_MAP_OBJECT_KEY: "identityLink",
    PARAM_IDENTITY_LINK_DICTIONARY_KEY: "identityLink", //Deprecated key
    PARAM_LOG_LEVEL_ENUM_KEY: "logLevelEnum",
    PARAM_RETRIEVE_ATTRIBUTION_BOOL_KEY: "retrieveAttribution",
    PARAM_INTELLIGENT_CONSENT_MANAGEMENT_BOOL_KEY: "consentIntelligentManagement",

    // KVLogLevelEnum Value Strings
    LOG_LEVEL_ENUM_NONE_VALUE: "none",
    LOG_LEVEL_ENUM_ERROR_VALUE: "error",
    LOG_LEVEL_ENUM_WARN_VALUE: "warn",
    LOG_LEVEL_ENUM_INFO_VALUE: "info",
    LOG_LEVEL_ENUM_DEBUG_VALUE: "debug",
    LOG_LEVEL_ENUM_TRACE_VALUE: "trace",

    // Consent Status Key Strings
    CONSENT_STATUS_DESCRIPTION_STRING_KEY: "description",
    CONSENT_STATUS_REQUIRED_BOOL_KEY: "required",
    CONSENT_STATUS_GRANTED_BOOL_KEY: "granted",
    CONSENT_STATUS_SHOULD_PROMPT_BOOL_KEY: "should_prompt",
    CONSENT_STATUS_RESPONSE_TIME_LONG_KEY: "response_time",
    CONSENT_STATUS_PARTNERS_KEY: "partners",
    CONSENT_STATUS_PARTNER_NAME_STRING_KEY: "name",

    // Events
    ATTRIBUTION_EVENT_TYPE: "attribution-notification",
    CONSENT_STATUS_CHANGE_EVENT_TYPE: "consent-status-change-notification",

    // Functions
    sendEventString: function (nameString, infoString) {
        cordovaExecCommand('sendEventString', nameString, infoString);
    },

    sendEventMapObject: function (nameString, infoMapObject) {
        cordovaExecCommand('sendEventMapObject', nameString, infoMapObject);
    },

    sendEventAppleAppStoreReceipt: function (nameString, infoMapObject, appStoreReceiptBase64EncodedString) {
        cordovaExecCommand('sendEventAppleAppStoreReceipt', nameString, infoMapObject, appStoreReceiptBase64EncodedString);
    },

    sendEventGooglePlayReceipt: function (nameString, infoMapObject, receiptData, receiptDataSignature) {
        cordovaExecCommand('sendEventGooglePlayReceipt', nameString, infoMapObject, receiptData, receiptDataSignature);
    },

    sendDeepLink: function (openURLString, sourceApplicationString) {
        cordovaExecCommand('sendDeepLink', openURLString, sourceApplicationString);
    },

    setAppLimitAdTracking: function (appLimitAdTrackingBool) {
        cordovaExecCommand('setAppLimitAdTracking', appLimitAdTrackingBool);
    },

    // Deprecated
    sendIdentityLink: function (mapObject) {
        cordovaExecCommand('setIdentityLink', mapObject);
    },

    setIdentityLink: function (mapObject) {
        cordovaExecCommand('setIdentityLink', mapObject);
    },

    getAttribution: function (callback) {
        cordovaExecCommandCallback('getAttribution', callback);
    },

    getDeviceId: function (callback) {
        cordovaExecCommandCallback('getDeviceId', callback);
    },

    getVersion: function (callback) {
        cordovaExecCommandCallback('getVersion', callback);
    },

    addPushToken: function (tokenString) {
        cordovaExecCommand('addPushToken', tokenString);
    },

    removePushToken: function (tokenString) {
        cordovaExecCommand('removePushToken', tokenString);
    },

    setConsentGranted: function (grantedBool) {
        cordovaExecCommand('setConsentGranted', grantedBool);
    },

    setConsentPrompted: function () {
        cordovaExecCommand('setConsentPrompted');
    },

    getConsentStatus: function (callback) {
        cordovaExecCommandCallback('getConsentStatus', callback);
    },

    configure: function (parametersMapObject) {
        parametersMapObject["versionExtension"] = "Cordova 2.2.0";
        parametersMapObject["wrapperBuildDateString"] = "2018-05-03T17:03:31Z";
        cordovaExecCommand('configure', parametersMapObject);
    }

};

module.exports = KochavaTracker;
