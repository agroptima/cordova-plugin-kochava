//
//  KochavaTrackerPlugin.h
//  KochavaTracker (PhoneGap)
//
//  Copyright (c) 2013 - 2017 Kochava, Inc. All rights reserved.
//
//  Description : This is the plugin class header file.
//

#pragma mark - IMPORT

#import <Cordova/CDV.h>
#import "KochavaTracker.h"

#pragma mark - INTERFACE

@interface KochavaTrackerPlugin : CDVPlugin <KochavaTrackerDelegate>
    
#pragma mark - METHODS
      
- (void)configure:(nonnull CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)sendEventString:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)sendEventMapObject:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)sendEventAppleAppStoreReceipt:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)sendEventGooglePlayReceipt:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)sendDeepLink:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)setAppLimitAdTracking:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)setIdentityLink:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)getAttribution:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)getDeviceId:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;
    
- (void)getVersion:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)addPushToken:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)removePushToken:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)setConsentGranted:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)setConsentPrompted:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

- (void)getConsentStatus:(nullable CDVInvokedUrlCommand *)invokedUrlCommand;

@end
