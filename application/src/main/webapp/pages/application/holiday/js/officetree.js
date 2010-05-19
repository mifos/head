Ext.ns('Example', 'WebPage');

    Ext.onReady(function() {
    Ext.QuickTips.init();
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
			checkchange: function() {
				selectedOfficeIds.setValue(this.getValue());
        	}
		}
	});

	var selectedOfficeIds = new Ext.form.TextField({
		 renderTo:'officeTree'
		,id:'selectedOfficeIds'
		,name:'selectedOfficeIds'
		,hidden:true	
		,style:'margin-top:2px'
	});
});