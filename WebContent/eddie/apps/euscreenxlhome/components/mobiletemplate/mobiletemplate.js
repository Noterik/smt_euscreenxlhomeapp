var Mobiletemplate = function () {
    Page.apply(this, arguments);
    
    this.$navElement = jQuery('#navpanel');
    this.$navbarElement = jQuery('.navbar-header');
    this.$formElement = jQuery('#headerform');
    this.$overlayButtons = jQuery('button[data-overlay]');
};

Mobiletemplate.prototype = Object.create(Page.prototype);
Mobiletemplate.prototype.activateTooltips = function(){	
	//this.overlayButtons.tooltip();
};
Mobiletemplate.prototype.searchButton = jQuery("#searchbutton");
Mobiletemplate.prototype.menuButton = jQuery("#menubutton");
Mobiletemplate.prototype.events = {
	'click #searchbutton': function(event) {
        if(this.searchButton.hasClass("active")) {
            this.searchButton.removeClass("active"); // toggle style
            this.$navbarElement.removeClass('searchOpened');
            this.menuButton.show();
        } else {
            this.searchButton.addClass("active"); // toggle style
            this.$navbarElement.addClass('searchOpened');
            this.$formElement.find('input[type="text"]').focus();
            this.menuButton.hide();
        }
    }
}