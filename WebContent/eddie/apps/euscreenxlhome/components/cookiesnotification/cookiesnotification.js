var Cookiesnotification = function(options){
	var self = this;
	console.log("Cookiesnotification()");
	Component.apply(this, arguments);
	
	this.element = jQuery('#cookiesnotification');
	this.notification_box = this.element.find('#coockie_notification');
	this.cookie_okay_btn = this.element.find('#cookie_okay_button');

	var notificationCoockie = getCookie("cookieNotificationBox");

	console.log(notificationCoockie);
	if (notificationCoockie != "") {

    	this.notification_box.hide();
    
    }else {
    	this.notification_box.show();
    	
    }
	
	this.cookie_okay_btn.click(function(ev) {
		setCookie();
		self.notification_box.hide();
	});
	function setCookie() {
	    document.cookie = "cookieNotificationBox" + "=" + "true" + ";";
	}

	function getCookie(cookieName) {
	    var name = cookieName + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0; i<ca.length; i++) {
	        var c = ca[i];
	        
	        console.log(c);
	        
	        if(c.indexOf(name) > -1) {
	        	return "there is a cookie";
	        }
	    }
	    return "";
	}
};
Cookiesnotification.prototype = Object.create(Component.prototype);
