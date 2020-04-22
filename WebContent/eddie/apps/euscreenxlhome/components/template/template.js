var Template = function () {
	$('#screen').css('visibility','hidden');
	
	/*home screen popup*/
	 $( "#dialog-new" ).dialog();
	 /*end of home screen popup*/
	 
    Component.apply(this, arguments);
    
};

Template.prototype = Object.create(Component.prototype);

var link = document.createElement('link');
link.setAttribute('rel', 'stylesheet');
link.setAttribute('type', 'text/css');
link.setAttribute('href', 'https://fonts.googleapis.com/css?family=Open+Sans:400,600,700&display=swap');
document.head.appendChild(link);
