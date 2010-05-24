Ext.ns('Ext.ux.tree');

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

var office_hierarchy=null;

Ext.onReady(function() {
    	
	var page = new WebPage({
        centerContent:'center-content'
	});

	var officeTree = new Ext.ux.tree.CheckTreePanel({
		 renderTo:'officeTree'
		,id:'officeHierarchy'
		,height:'auto'
		,width:'50%'
		,border:false
		,header:false
		,trackMouseOver:false
		,autoScroll:true
		,rootVisible:false
		,deepestOnly:false
        ,bubbleCheck:'none'
        ,cascadeCheck:'all'
		,root:{
			 nodeType:'async'
			,id:'root'
			,text:'root'
			,expanded:true
			,uiProvider:false
		}
		,loader:{
			 url:'holidayAction.do'
			,baseParams:{
	           method:'officeHierarchy'
			}
		}
		,listeners: {
			load: function() {
				var ids = document.getElementById('selectedOfficeIds').value;
				if(ids && ids!=''){
					this.expandAll();
					this.setValue(ids);
				}
				document.getElementById('selectedOfficeIds').value='';
        	}
		}
	});
	
	office_hierarchy=officeTree;
});