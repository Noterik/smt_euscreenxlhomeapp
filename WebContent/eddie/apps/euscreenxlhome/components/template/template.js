var Template = function () {
	$('#screen').css('visibility','hidden');
    Component.apply(this, arguments);
    
};

Template.prototype = Object.create(Component.prototype);
