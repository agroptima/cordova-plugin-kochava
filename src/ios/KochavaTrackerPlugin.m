//
//  KochavaTrackerPlugin.m
//  KochavaTracker (PhoneGap)
//
//  Copyright (c) 2013 - 2017 Kochava, Inc. All rights reserved.
//
//  Description : This is the plugin class implementation file.
//

#pragma mark - IMPORT

#import "KochavaTrackerPlugin.h"

#pragma mark - CONST

NSString *const KVA_PARAM_IOS_APP_GUID_STRING_KEY = @"iOSAppGUIDString";

#pragma mark - IMPLEMENTATION

@implementation KochavaTrackerPlugin
    
#pragma mark - GENERAL

+ (void)invalidateKochava
{
    [KochavaTracker.shared performSelector:@selector(invalidate)];
}

+ (void)removeKochavaUserDefaults
{
    NSArray *keyArray = NSUserDefaults.standardUserDefaults.dictionaryRepresentation.allKeys;
    for (id key in keyArray)
    {
        NSRange kochavaPrefixRange = [key rangeOfString:@"com.kochava"];
        if ( kochavaPrefixRange.location != NSNotFound )
        {
            [NSUserDefaults.standardUserDefaults removeObjectForKey:key];
        }
    }
}
    
- (void)evaluateWindowAttributionNotificationCallbackWithParameterString:(nonnull NSString *)parameterString
{
    // javaScriptString
    NSString *javaScriptString = [NSString stringWithFormat:@"window.attributionNotification.notificationCallback('%@');",parameterString];
    
    // webView
    if ([self.webView isKindOfClass:[UIWebView class]])
    {
        UIWebView *webView = (UIWebView*)self.webView;
        
        dispatch_async(dispatch_get_main_queue(), ^{
            
            [webView stringByEvaluatingJavaScriptFromString:javaScriptString];
            
        });
    }
}

- (void)sendEventString:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // nameString
    NSString *nameString = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // infoString
    NSString *infoString = [invokedUrlCommand.arguments objectAtIndex:1];
    
    // KochavaTracker.shared
    [KochavaTracker.shared sendEventWithNameString:nameString infoString:infoString];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendEventWithInfoString did succeed"];

    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}
    
- (void)sendEventMapObject:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // nameString
    NSString *nameString = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // infoDictionary
    NSDictionary *infoDictionary = [invokedUrlCommand.arguments objectAtIndex:1];
    
    // KochavaTracker.shared
    [KochavaTracker.shared sendEventWithNameString:nameString infoDictionary:infoDictionary];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendEventWithInfoDictionary did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)sendEventAppleAppStoreReceipt:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // nameString
    NSString *nameString = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // infoDictionary
    NSDictionary *infoDictionary = [invokedUrlCommand.arguments objectAtIndex:1];
    
    // appStoreReceiptBase64EncodedString
    NSString *appStoreReceiptBase64EncodedString = [invokedUrlCommand.arguments objectAtIndex:2];
    
    // event
    KochavaEvent *event = [KochavaEvent eventWithEventTypeEnum:KochavaEventTypeEnumCustom];
    event.customEventNameString = nameString;
    event.infoDictionary = infoDictionary;
    event.appStoreReceiptBase64EncodedString = appStoreReceiptBase64EncodedString;
    
    // KochavaTracker.shared
    [KochavaTracker.shared sendEvent:event];
     
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendEventWithAppleAppStoreReceipt did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}
    
- (void)sendEventGooglePlayReceipt:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    NSLog(@"KOCHAVA - sendEventWithGooglePlayReceiptButton does not apply to this OS");
}
    
- (void)sendDeepLink:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // deepLinkURLString
    NSString *deepLinkURLString = (NSString *)[invokedUrlCommand.arguments objectAtIndex:0];
    
    // deeplinkURL
    NSURL *deeplinkURL = [NSURL URLWithString:[deepLinkURLString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    // The following line would work, but may not be entirely equivalent.  The best solution will be to offer a urlString (in addition to url) on the standard parameters.
    // NSURL *deeplinkURL = [NSURL URLWithString:[deepLinkURLString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLHostAllowedCharacterSet]];
    
    // sourceApplicationString
    NSString *sourceApplicationString = (NSString *)[invokedUrlCommand.arguments objectAtIndex:1];
    
    // KochavaTracker.shared
    [KochavaTracker.shared sendDeepLinkWithOpenURL:deeplinkURL sourceApplicationString:sourceApplicationString];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendDeepLink did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}
    
- (void)setAppLimitAdTracking:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // appLimitAdTrackingBool
    BOOL appLimitAdTrackingBool = [[invokedUrlCommand.arguments objectAtIndex:0] boolValue];

    // KochavaTracker.shared
    [KochavaTracker.shared setAppLimitAdTrackingBool:appLimitAdTrackingBool];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"setAppLimitAdTrackingBool did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}
    
- (void)setIdentityLink:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // dictionaryObject
    id dictionaryObject = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // dictionary
    NSDictionary *dictionary = nil;
    
    if ( [dictionaryObject isKindOfClass:[NSDictionary class]] )
    {
        dictionary = (NSDictionary *)dictionaryObject;
    }

    // resultMessageString and resultCommandStatus
    NSString *resultMessageString = nil;
    
    CDVCommandStatus resultCommandStatus = CDVCommandStatus_NO_RESULT;
    
    if (dictionary != nil)
    {
        // KochavaTracker.shared
        [KochavaTracker.shared sendIdentityLinkWithDictionary:dictionary];

        // resultMessageString
        resultMessageString = @"setIdentityLinkWithDictionary did succeed";
        
        // resultCommandStatus
        resultCommandStatus = CDVCommandStatus_OK;
    }
    else
    {
        // resultMessageString
        resultMessageString = @"setIdentityLinkWithDictionary not processed";
        
        // resultCommandStatus
        resultCommandStatus = CDVCommandStatus_ERROR;
    }

    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:resultCommandStatus messageAsString:resultMessageString];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)getAttribution:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // attributionDictionary
    NSDictionary *attributionDictionary = KochavaTracker.shared.attributionDictionary;
    
    // resultMessageString
    NSString *resultMessageString = nil;
    
    if (attributionDictionary == nil)
    {
        resultMessageString = @"";
    }
    else
    {
        // attributionDictionaryJSONData and error
        NSError *error = nil;
        NSData *attributionDictionaryJSONData = [NSJSONSerialization dataWithJSONObject:attributionDictionary options:0 error:&error];
        
        // attributionDictionaryJSONString
        NSString *attributionDictionaryJSONString = nil;
        
        if (attributionDictionaryJSONData != nil)
        {
            attributionDictionaryJSONString = [[NSString alloc] initWithData:attributionDictionaryJSONData encoding:NSUTF8StringEncoding];
        }
        
        // ... resultMessageString
        if (attributionDictionaryJSONString != nil)
        {
            resultMessageString = attributionDictionaryJSONString;
        }
        else
        {
            resultMessageString = @"{ \"messageString\": \"the information cannot be serialized into a json string\" }";
        }
    }
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:resultMessageString];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}
    
- (void)getDeviceId:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // deviceIdString
    NSString *deviceIdString = KochavaTracker.shared.deviceIdString;
    if(deviceIdString == nil)
    {
        deviceIdString = @"";
    }
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:deviceIdString];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)getVersion:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // sdkVersionString
    NSString *sdkVersionString = KochavaTracker.shared.sdkVersionString;
    if(sdkVersionString == nil)
    {
        sdkVersionString = @"";
    }
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:sdkVersionString];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)addPushToken:(nullable CDVInvokedUrlCommand *)invokedUrlCommand
{
    // remoteNotificationsDeviceTokenString
    NSString *remoteNotificationsDeviceTokenString = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // remoteNotificationsDeviceToken
    NSData *remoteNotificationsDeviceToken = [self.class dataWithHexString:remoteNotificationsDeviceTokenString];
    
    // KochavaTracker.shared
    [KochavaTracker.shared addRemoteNotificationsDeviceToken:remoteNotificationsDeviceToken];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendEventWithInfoString did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)removePushToken:(nullable CDVInvokedUrlCommand *)invokedUrlCommand
{
    // remoteNotificationsDeviceTokenString
    NSString *remoteNotificationsDeviceTokenString = [invokedUrlCommand.arguments objectAtIndex:0];
    
    // remoteNotificationsDeviceToken
    NSData *remoteNotificationsDeviceToken = [self.class dataWithHexString:remoteNotificationsDeviceTokenString];
    
    // KochavaTracker.shared
    [KochavaTracker.shared removeRemoteNotificationsDeviceToken:remoteNotificationsDeviceToken];
    
    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"sendEventWithInfoString did succeed"];
    
    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)setConsentGranted:(nullable CDVInvokedUrlCommand *)invokedUrlCommand
{
    id consentGrantedObject = [invokedUrlCommand.arguments objectAtIndex:0];
    
    NSNumber *consentGranted = nil;
    
    if ( [consentGrantedObject isKindOfClass:[NSNumber class]] )
    {
        consentGranted = (NSNumber *)consentGrantedObject;
    }

    if(consentGranted != nil)
    {
        [KochavaTracker.shared.consent didPromptWithDidGrantBoolNumber:consentGranted];
    }

    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"setConsentGranted did succeed"];

    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)setConsentPrompted:(nullable CDVInvokedUrlCommand *)invokedUrlCommand
{
    [KochavaTracker.shared.consent willPrompt];

    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"setConsentPrompted did succeed"];

    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

- (void)getConsentStatus:(nullable CDVInvokedUrlCommand *)invokedUrlCommand
{
    NSString *consentStatusString = nil;
    
    NSObject *consentAsForContextObject = [KochavaTracker.shared.consent asForContextObjectWithContext:KVAContext.sdkWrapper];

    if (consentAsForContextObject != nil)
    {
        NSError *error = nil;
        NSData *consentStatusJSONData = [NSJSONSerialization dataWithJSONObject:consentAsForContextObject options:0 error:&error];
        
        if (consentStatusJSONData != nil)
        {
            consentStatusString = [[NSString alloc] initWithData:consentStatusJSONData encoding:NSUTF8StringEncoding];
        }
    }
    
    if(consentStatusString == nil)
    {
        consentStatusString = @"{}";
    }

    // pluginResult
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:consentStatusString];

    // self.commandDelegate
    [self.commandDelegate sendPluginResult:pluginResult callbackId:invokedUrlCommand.callbackId];
}

#pragma mark - LIFECYCLE
    
- (void)configure:(CDVInvokedUrlCommand *)invokedUrlCommand
{
    // Discussion:  Configures the shared tracker.

    // receivedParametersDictionaryObject
    id receivedParametersDictionaryObject = nil;
    
    if (invokedUrlCommand.arguments.count > 0)
    {
        receivedParametersDictionaryObject = [invokedUrlCommand.arguments objectAtIndex:0];
    }
    
    // receivedParametersDictionary
    NSDictionary *receivedParametersDictionary = nil;
    
    if ((receivedParametersDictionaryObject != nil) && ([receivedParametersDictionaryObject isKindOfClass:[NSDictionary class]]))
    {
        receivedParametersDictionary = (NSDictionary *)receivedParametersDictionaryObject;
    }
    
    // VALIDATION (RETURN)
    if (receivedParametersDictionary == nil)
    {
        NSLog(@"KochavaTrackerPlugin.configure parameter 0 is not an NSDictionary.  iOS native cannot initialize.");
        return;
    }

    // Check for the existence of the hidden unconfigure key.
    if ([receivedParametersDictionary objectForKey:@"INTERNAL_UNCONFIGURE"]) {
        NSLog(@"KochavaTrackerPlugin.configure UnConfigure.");
        [KochavaTrackerPlugin invalidateKochava];
        return;
    }

    // Check for the existence of the hidden reset key.
    if ([receivedParametersDictionary objectForKey:@"INTERNAL_RESET"]) {
        NSLog(@"KochavaTrackerPlugin.configure Reset.");
        [KochavaTrackerPlugin removeKochavaUserDefaults];
        return;
    }

    NSLog(@"KochavaTrackerPlugin.configure.");
    
    // PARSE SPECIFIC PARAMETERS FROM RECEIVEDPARAMETERSDICTIONARY
    // appGUIDStringObject
    id appGUIDStringObject = receivedParametersDictionary[KVA_PARAM_IOS_APP_GUID_STRING_KEY];
    
    // CONFIGURE TRACKER
    // trackerParametersDictionary
    NSMutableDictionary *trackerParametersDictionary = receivedParametersDictionary.mutableCopy;

    // ... kKVAParamAppGUIDStringKey
    if (appGUIDStringObject != nil)
    {
        trackerParametersDictionary[kKVAParamAppGUIDStringKey] = appGUIDStringObject;
        
        trackerParametersDictionary[KVA_PARAM_IOS_APP_GUID_STRING_KEY] = nil;
    }

    // kochavaTracker
    // this cannot be run in background or will crash trying to collect user agent
    [KochavaTracker.shared configureWithParametersDictionary:trackerParametersDictionary delegate:self];

    //Check if intelligent consent management is on and apply as necessary.
    BOOL intelligentManagementBool = [[receivedParametersDictionary objectForKey:@"consentIntelligentManagement"] boolValue];
    if(intelligentManagementBool) {
        KochavaTracker.shared.consent.didUpdateBlock = ^(KVAConsent * _Nonnull consent)
        {
            // javaScriptString
            NSString *javaScriptString = [NSString stringWithFormat:@"window.consentStatusChangeNotification.notificationCallback('');"];
            
            // webView
            if ([self.webView isKindOfClass:[UIWebView class]])
            {
                UIWebView *webView = (UIWebView*)self.webView;
                
                dispatch_async(dispatch_get_main_queue(), ^{
                    
                    [webView stringByEvaluatingJavaScriptFromString:javaScriptString];
                    
                });
            }
        };
    }
    
}
    
#pragma mark - DELEGATE CALLBACKS
#pragma mark KochavaTrackerDelegate
     
- (void)tracker:(nonnull KochavaTracker *)tracker didRetrieveAttributionDictionary:(nonnull NSDictionary *)attributionDictionary
{
        // attributionDictionaryJSONData and error
    NSError *error = nil;
    
    NSData *attributionDictionaryJSONData = [NSJSONSerialization dataWithJSONObject:attributionDictionary options:0 error:&error];
    
    if (error != nil)
    {
        NSLog(@"error: %@", error);
    }
    
    // attributionDictionaryJSONString
    NSString *attributionDictionaryJSONString = nil;
    
    if (attributionDictionaryJSONData != nil)
    {
        attributionDictionaryJSONString = [[NSString alloc] initWithData:attributionDictionaryJSONData encoding:NSUTF8StringEncoding];
    }
    
    // resolvedAttributionDictionaryJSONString
    NSString *resolvedAttributionDictionaryJSONString = nil;
    
    if (attributionDictionaryJSONString != nil)
    {
        resolvedAttributionDictionaryJSONString = attributionDictionaryJSONString;
    }
    else
    {
        resolvedAttributionDictionaryJSONString = @"{ \"messageString\": \"the information cannot be serialized into a json string\" }";
    }
    // resolvedAttributionDictionaryJSONStringData
    NSData *resolvedAttributionDictionaryJSONStringData = [resolvedAttributionDictionaryJSONString dataUsingEncoding:NSUTF8StringEncoding];
    
    // resolvedAttributionDictionaryJSONStringDataBase64EncodedString
    NSString *resolvedAttributionDictionaryJSONStringDataBase64EncodedString = nil;
    
    if (resolvedAttributionDictionaryJSONStringData != nil)
    {
        resolvedAttributionDictionaryJSONStringDataBase64EncodedString = [resolvedAttributionDictionaryJSONStringData base64EncodedStringWithOptions:0];
    }
    
    // evaluateWindowAttributionNotificationCallbackWithParameterString
    if (resolvedAttributionDictionaryJSONStringDataBase64EncodedString != nil)
    {
        [self evaluateWindowAttributionNotificationCallbackWithParameterString:resolvedAttributionDictionaryJSONStringDataBase64EncodedString];
    }
}

#pragma mark - CLASS METHODS

+(id)dataWithHexString:(NSString *)hex
{
    // Discussion:  Said to be 'Not efficent'.  This is being employed to take the output of an NSData description (which is a hex string, such as is the case with a push notification token) and turn it back into an NSData.  Hex strings should have an even number of digits, and string should be all hex digits.
    
    char buf[3];
    buf[2] = '\0';
    unsigned char *bytes = malloc(hex.length/2);
    unsigned char *bp = bytes;
    for (CFIndex i = 0; i < hex.length; i += 2) {
        buf[0] = [hex characterAtIndex:i];
        buf[1] = [hex characterAtIndex:i+1];
        char *b2 = NULL;
        *bp++ = strtol(buf, &b2, 16);
    }
    
    return [NSData dataWithBytesNoCopy:bytes length:hex.length/2 freeWhenDone:YES];
}

@end
