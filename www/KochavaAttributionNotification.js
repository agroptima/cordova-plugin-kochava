
(function(cordova) {
 
    function AttributionNotification() {}
    function ConsentStatusChangeNotification() {}

// Event spawned when an attribution notification is received while the application is active
AttributionNotification.prototype.notificationCallback = function(notification) {
    console.log('Received Attribution Notification');
    var ev = document.createEvent('HTMLEvents');
    ev.notification = Base64.decode(notification);
    ev.initEvent(KochavaTracker.ATTRIBUTION_EVENT_TYPE, true, true, arguments);
    document.dispatchEvent(ev);
};

// Event spawned when a consent status notification is received while the application is active
ConsentStatusChangeNotification.prototype.notificationCallback = function(notification) {
    console.log('Received Consent Status Change Notification');
    var ev = document.createEvent('HTMLEvents');
    ev.initEvent(KochavaTracker.CONSENT_STATUS_CHANGE_EVENT_TYPE, true, true, arguments);
    document.dispatchEvent(ev);
};

// Setup handlers for the native notifications via an extra constructor.
cordova.addConstructor(function() {
        console.log('Setup Kochava Native Notifications');
        if(!window) window = {};
        window.attributionNotification = new AttributionNotification();
        window.consentStatusChangeNotification = new ConsentStatusChangeNotification();
    });
})(window.cordova || window.Cordova || window.PhoneGap);

/**
* Base64 encode / decode
* http://www.webtoolkit.info/
* https://www.coditty.com/code/utf-base64-encode-in-php-and-decode-in-javascript
**/
var Base64 = {

    // private property
    _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    // public method for decoding
    decode : function (input) {
      var output = "";
      var chr1, chr2, chr3;
      var enc1, enc2, enc3, enc4;
      var i = 0;

      input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

      while (i < input.length) {

        enc1 = this._keyStr.indexOf(input.charAt(i++));
        enc2 = this._keyStr.indexOf(input.charAt(i++));
        enc3 = this._keyStr.indexOf(input.charAt(i++));
        enc4 = this._keyStr.indexOf(input.charAt(i++));

        chr1 = (enc1 << 2) | (enc2 >> 4);
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
        chr3 = ((enc3 & 3) << 6) | enc4;

        output = output + String.fromCharCode(chr1);

        if (enc3 != 64) {
          output = output + String.fromCharCode(chr2);
        }
        if (enc4 != 64) {
          output = output + String.fromCharCode(chr3);
        }

      }

      output = Base64._utf8_decode(output);

      return output;

    },

    // private method for UTF-8 decoding
    _utf8_decode : function (utftext) {
      var string = "";
      var i = 0;
      var c = c1 = c2 = 0;

      while ( i < utftext.length ) {

        c = utftext.charCodeAt(i);

        if (c < 128) {
          string += String.fromCharCode(c);
          i++;
        }
        else if((c > 191) && (c < 224)) {
          c2 = utftext.charCodeAt(i+1);
          string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
          i += 2;
        }
        else {
          c2 = utftext.charCodeAt(i+1);
          c3 = utftext.charCodeAt(i+2);
          string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
          i += 3;
        }

      }

      return string;
    }

  }