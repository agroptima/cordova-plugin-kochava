
(function(cordova) {
 
	function AttributionNotification() {}

// Event spawned when a notification is received while the application is active
AttributionNotification.prototype.notificationCallback = function(notification) {

    var ev = document.createEvent('HTMLEvents');
    ev.notification = notification;
    ev.initEvent(KochavaTracker.ATTRIBUTION_EVENT_TYPE, true, true, arguments);
    document.dispatchEvent(ev);
};

cordova.addConstructor(function() {

                       console.log('RECEIVED NOTIFICATION! KochavaTracker.ATTRIBUTION_EVENT_TYPE! ' );
                       if(!window) window = {};
                       window.attributionNotification = new AttributionNotification();
                       });

})(window.cordova || window.Cordova || window.PhoneGap);

