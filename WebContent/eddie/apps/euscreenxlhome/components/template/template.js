var Template = function () {
	$('#screen').css('visibility','hidden');
	
	/*home screen popup*/
	 $( "#dialog-new" ).dialog();
	 /*end of home screen popup*/
	 
    Component.apply(this, arguments);
    
};

Template.prototype = Object.create(Component.prototype);
