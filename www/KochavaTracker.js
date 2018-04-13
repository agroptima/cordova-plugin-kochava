
/*
 * File Name  : KochavaTracker.js
 * Author     : Kochava
 * Description : This file initiates calls to the native library from the plugin
 */




               
               
function cordovaExecCommand(command)
{
	var args = Array.prototype.slice.call(arguments, 1);
	cordova.exec(function callback(data) { },
				 function errorHandler(err) { },
				 'KochavaTrackerPlugin',
				 command,
				 args
				 );
}

               
               
function cordovaExecCommandCallback(command, callback)
{
	var args = Array.prototype.slice.call(arguments, 2);
	cordova.exec(callback,
				 function errorHandler(err) { },
				 'KochavaTrackerPlugin',
				 command,
				 args
				 );
}

               

var KochavaTracker = {

	// General
	VERSION_STRING:"2.1.0",

               
               
	// Init Parameter MapObject Key Strings
	PARAM_ANDROID_APP_GUID_STRING_KEY:"androidAppGUIDString",
               
	PARAM_APP_LIMIT_AD_TRACKING_BOOL_KEY:"limitAdTracking",
               
	PARAM_IDENTITY_LINK_DICTIONARY_KEY:"identityLink",
               
	PARAM_IOS_APP_GUID_STRING_KEY:"iOSAppGUIDString",
               
	PARAM_LOG_LEVEL_ENUM_KEY:"logLevelEnum",
               
	PARAM_LOG_MULTILINE_BOOL_KEY:"logMultiLineBool",
               
	PARAM_RETRIEVE_ATTRIBUTION_BOOL_KEY:"retrieveAttribution",
               
               
               
	// KVLogLevelEnum Value Strings
	LOG_LEVEL_ENUM_NONE_VALUE:"none",
               
	LOG_LEVEL_ENUM_ERROR_VALUE:"error",
               
	LOG_LEVEL_ENUM_WARN_VALUE:"warn",
               
	LOG_LEVEL_ENUM_INFO_VALUE:"info",
               
	LOG_LEVEL_ENUM_DEBUG_VALUE:"debug",
               
	LOG_LEVEL_ENUM_TRACE_VALUE:"trace",
               
               
               
	// Events
	ATTRIBUTION_EVENT_TYPE:"attribution-notification",
               
               
               
	sendEventString:function(nameString, infoString) {
		cordovaExecCommand('sendEventString', nameString, infoString);
    	},
		
	sendEventMapObject:function(nameString, infoMapObject) {
        	cordovaExecCommand('sendEventMapObject', nameString, infoMapObject);
	},
   
	sendEventAppleAppStoreReceipt:function(nameString, mapObject, appStoreReceiptBase64EncodedString) {
		cordovaExecCommand('sendEventAppleAppStoreReceipt', nameString, mapObject, appStoreReceiptBase64EncodedString);
    	},
		
	sendEventGooglePlayReceipt:function(nameString, mapObject, receiptData, receiptDataSignature) {
        	cordovaExecCommand('sendEventGooglePlayReceipt', nameString, mapObject, receiptData, receiptDataSignature);
   	},
   
	sendDeepLink:function(openURLString, sourceApplicationString) {
		cordovaExecCommand('sendDeepLink', openURLString, sourceApplicationString);
	},
		
	setAppLimitAdTracking:function(appLimitAdTrackingBool) {
		cordovaExecCommand('setAppLimitAdTracking', appLimitAdTrackingBool);
	},
		
	sendIdentityLink:function(mapObject) {
		cordovaExecCommand('sendIdentityLink', mapObject);
	},
		
	getAttribution:function(callback) {
		cordovaExecCommandCallback('getAttribution', callback);
	},
		
	getDeviceId:function(callback) {
		cordovaExecCommandCallback('getDeviceId', callback);
    },
    
    getVersion:function(callback) {
		cordovaExecCommandCallback('getVersion', callback);
    },
    
    addPushToken:function(token) {
		cordovaExecCommand('addPushToken', token);
    },
    
    removePushToken:function(token) {
		cordovaExecCommand('removePushToken', token);
	},
	
	configure:function(parametersMapObject) {
        	parametersMapObject["versionExtension"] = "Cordova " + KochavaTracker.VERSION_STRING;
               
        	cordovaExecCommand('configure', parametersMapObject);
	},
               
};

               
               
module.exports = KochavaTracker;



