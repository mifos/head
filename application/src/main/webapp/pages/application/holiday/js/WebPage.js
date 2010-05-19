Ext.ns('WebPage');
 
WebPage = function(config) {
	Ext.apply(this, config, {
		 autoRender:true
		,ctCreate:{tag:'div', id:'ct-wrap', cn:[{tag:'div', id:'ct'}]}
	});

	if(this.autoRender) {
		this.render();
	}
};

Ext.override(WebPage, {
    render:function() {
		var content = Ext.get('center-content');
		var dh = Ext.DomHelper;

		this.wrap = dh.insertAfter(content, this.ctCreate, true);
		this.ct = Ext.get('ct');

		this.center = dh.append(this.ct, {tag:'div', id:'center'}, true);

		if(this.centerContent) {
			this.center.appendChild(this.centerContent);
			this.centerContent = Ext.get(this.centerContent).removeClass('x-hidden');
		}
	}
});