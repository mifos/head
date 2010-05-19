// vim: ts=4:sw=4:nu:fdc=4:nospell
/*global Ext */
/**
 * @class Ext.ux.tree.RemoteTreePanel
 * @extends Ext.tree.TreePanel
 *
 * This class implements basic tree operations requesting a server to execute them
 * and upon the receipt of a response it updates the user interface. For example: 
 * Dragging a node to a new place causes request to be sent and if success is reported
 * the drag and drop operation is completed. UI is reverted to the state before D&D if
 * a failure is received (the error message is displayed in message box).
 *
 * The class tries to abstract from a specific server-side as much as possible to make it
 * easy to implement any data backend. All command (cmd) names and parameter names
 * are configurable.
 *
 * @author    Ing. Jozef Sakáloš
 * @copyright (c) 2008, by Ing. Jozef Sakáloš
 * @date      8. December 2008
 * @version   1.0
 * @revision  $Id: Ext.ux.tree.RemoteTreePanel.js 594 2009-02-25 10:31:29Z jozo $
 *
 * @license Ext.ux.tree.RemoteTreePanel.js is licensed under the terms of the Open Source
 * LGPL 3.0 license. Commercial use is permitted to the extent that the 
 * code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @forum     55102
 * @demo      http://remotetree.extjs.eu
 * @download  
 * <ul>
 * <li><a href="http://remotetree.extjs.eu/remotetree.tar.bz2">remotetree.tar.bz2</a></li>
 * <li><a href="http://remotetree.extjs.eu/remotetree.tar.gz">remotetree.tar.gz</a></li>
 * <li><a href="http://remotetree.extjs.eu/remotetree.zip">remotetree.zip</a></li>
 * </ul>
 *
 * @donate
 * <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_blank">
 * <input type="hidden" name="cmd" value="_s-xclick">
 * <input type="hidden" name="hosted_button_id" value="3430419">
 * <input type="image" src="https://www.paypal.com/en_US/i/btn/x-click-butcc-donate.gif" 
 * border="0" name="submit" alt="PayPal - The safer, easier way to pay online.">
 * <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
 * </form>
 */

Ext.ns('Ext.ux.tree');

/**
 * Creates new RemoteTreePanel
 * @constructor
 * @param {Object} config A config object
 */
Ext.ux.tree.RemoteTreePanel = Ext.extend(Ext.tree.TreePanel, {
 
	// {{{
	// config options
	// localizable texts
	 appendText:'Append'
	,collapseAllText:'Collapse All'
	,collapseText:'Collapse'
	,contextMenu:true
	,deleteText:'Delete'
	,errorText:'Error'
	,expandAllText:'Expand All'
	,expandText:'Expand'
	,insertText:'Insert'
	,newText:'New'
	,reallyWantText:'Do you really want to'
	,reloadText:'Reload'
	,renameText:'Rename'

	// other options
	/**
	 * @cfg {Object} actions Public interface to methods of tree operations. Actions are created internally
	 * and then are available for user space program. Actions provided from outside at instatiation time
	 * are honored.
	 */

	/**
	 * @cfg {Boolean} allowLeafAppend When dragging a node over a leaf the node cannot be appended.
	 * If this config option is true then the leaf dragged over is turned into node allowing to append
	 * dragged node to it. Defaults to true.
	 */
	,allowLeafAppend:true

	/**
	 * @cfg {String} appendIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,appendIconCls:'icon-arrow-down'

	/**
	 * @cfg {Boolean} border Draw border around panel if true. (Defaults to false)
	 */
    ,border:false

	/**
	 * @cfg {Object} cmdNames Names of commands sent to the server
	 */
	,cmdNames:{
		 moveNode:'moveTreeNode'
		,renameNode:'renameTreeNode'
		,removeNode:'removeTreeNode'
		,appendChild:'appendTreeChild'
		,insertChild:'insertTreeChild'
	}

	/**
	 * @cfg {String} collapseAllIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,collapseAllIconCls:'icon-collapse'

	/**
	 * @cfg {String} collapseIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,collapseIconCls:'icon-collapse'

	/**
	 * @cfg {String} deleteIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,deleteIconCls:'icon-cross'

	/**
	 * @cfg {Boolean} editable Set it to false to switch tree to read-only mode. Defaults to true.
	 */
	,editable:true

	/**
	 * @cfg {Object} editorConfig Configuration for Ext.tree.TreeEditor
	 */
	,editorConfig:{
		 cancelOnEsc:true
		,completeOnEnter:true
	}

	/**
	 * @cfg {Object} editorFieldConfig Configuration for tree editor field
	 */
	,editorFieldConfig:{
		 allowBlank:false
		,selectOnFocus:true
	}

	/**
	 * @cfg {Boolean} enableDD Enable drag and drop operations. Defaults to true.
	 */
	,enableDD:true

	/**
	 * @cfg {String} expandAllIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,expandAllIconCls:'icon-expand'

	/**
	 * @cfg {String} expandIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,expandIconCls:'icon-expand'

	/**
	 * @cfg {String} insertIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,insertIconCls:'icon-arrow-right'

	/**
	 * @cfg {String} layout Default layout used for the panel. Defaults to 'fit'.
	 */
	,layout:'fit'

	/**
	 * @cfg {Object} paramNames Names of parameters sent to server in requests.
	 */
	,paramNames:{
		 cmd:'cmd'
		,id:'id'
		,target:'target'
		,point:'point'
		,text:'text'
		,newText:'newText'
		,oldText:'oldText'
	}

	/**
	 * @cfg {String} reloadIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,reloadIconCls:'icon-refresh'

	/**
	 * @cfg {String} renameIconCls Icon class to use for {Ext.Action} iconCls. This icon is then used
	 * for the action user interface (context menu, button, etc.)
	 */
	,renameIconCls:'icon-pencil'
	// }}}
    // {{{
    ,initComponent:function() {

        // {{{
        // hard coded config (cannot be changed from outside)
        var config = {};

		// todo: add other keys and put them to context menu
		if(!this.keys) {
			config.keys = (function() {
				var keys = [];
				if(true === this.editable) {
					keys.push({
						 key:Ext.EventObject.DELETE
						,scope:this
						,stopEvent:true
						,handler:this.onKeyDelete
					});

					keys.push({
						 key:Ext.EventObject.F2
						,scope:this
						,stopEvent:true
						,handler:this.onKeyEdit
					});
				}
				return keys;
			}.call(this));
		}
 
        // apply config
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        // }}}
		// {{{
        // call parent
        Ext.ux.tree.RemoteTreePanel.superclass.initComponent.apply(this, arguments);
		// }}}
		// {{{
		// make sure that all nodes are created
		if(true === this.loader.preloadChildren) {
			this.loader.on({load:function(loader, node) {
				node.cascade(function(n) {
					loader.doPreload(n);
				});
			}});
		}
		// }}}
		// {{{
		// create tree editor
		if(true === this.editable && !this.editor) {
			this.editor = new Ext.tree.TreeEditor(this, this.editorFieldConfig, this.editorConfig);
			this.editor.on({
				 complete:{scope:this, fn:this.onEditComplete}
				,beforestartedit:{scope:this, fn:function(){ return this.editable; }}
			});
		}
		// }}}
		// {{{
		// remember selected node
		if(true === this.editable) {
			this.getSelectionModel().on({
				selectionchange:{scope:this, fn:function(selModel, node) {
					this.selectedNode = node;
				}
			}});
		}
		// }}}
		// {{{
		// create actions
		if(true === this.editable && !this.actions) {
			this.actions = {
				 reloadTree:new Ext.Action({
					 text:this.reloadText
					,iconCls:this.reloadIconCls
					,scope:this
					,handler:function() {this.root.reload();}
				})
				,expandNode:new Ext.Action({
					 text:this.expandText
					,iconCls:this.expandIconCls
					,scope:this
					,handler:this.onExpandNode
				})
				,expandAll:new Ext.Action({
					 text:this.expandAllText
					,iconCls:this.expandAllIconCls
					,scope:this
					,handler:this.onExpandAll
				})
				,collapseNode:new Ext.Action({
					 text:this.collapseText
					,iconCls:this.collapseIconCls
					,scope:this
					,handler:this.onCollapseNode
				})
				,collapseAll:new Ext.Action({
					 text:this.collapseAllText
					,iconCls:this.collapseAllIconCls
					,scope:this
					,handler:this.onCollapseAll
				})
				,renameNode:new Ext.Action({
					 text:this.renameText
					,iconCls:this.renameIconCls
					,scope:this
					,handler:this.onRenameNode
				})
				,removeNode:new Ext.Action({
					 text:this.deleteText
					,iconCls:this.deleteIconCls
					,scope:this
					,handler:this.onRemoveNode
				})
				,appendChild:new Ext.Action({
					 text:this.appendText
					,iconCls:this.appendIconCls
					,scope:this
					,handler:this.onAppendChild
				})
				,insertChild:new Ext.Action({
					 text:this.insertText
					,iconCls:this.insertIconCls
					,scope:this
					,handler:this.onInsertChild
				})
			};
		}
		// }}}
		// {{{
		// create context menu
		if(true === this.editable && true === this.contextMenu) {
			this.contextMenu = new Ext.menu.Menu([
				 new Ext.menu.TextItem({text:'', style:'font-weight:bold;margin:0px 4px 0px 27px;line-height:18px'})
				,'-'
				,this.actions.reloadTree
				,this.actions.expandAll
				,this.actions.collapseAll
				,'-'
				,this.actions.expandNode
				,this.actions.collapseNode
				,'-'
				,this.actions.renameNode
				,'-'
				,this.actions.appendChild
				,this.actions.insertChild
				,'-'
				,this.actions.removeNode
			]);
		}

		// install event handlers on contextMenu
		if(this.contextMenu) {
			this.on({contextmenu:{scope:this, fn:this.onContextMenu, stopEvent:true}});
			this.contextMenu.on({
				hide:{scope:this, fn:function() {
					this.actionNode = null;
				}}
				,show:{scope:this, fn:function() {
					var node = this.actionNode;
					var text = Ext.util.Format.ellipsis(node ? node.text : '', 12);
					this.contextMenu.items.item(0).el.update(text);
					this.contextMenu.el.shadow.hide();
					this.contextMenu.el.shadow.show(this.contextMenu.el);
				}}
			});
		}
		// }}}
		// {{{
		// setup D&D
		if(true === this.enableDD) {
			this.on({
				 beforenodedrop:{scope:this, fn:this.onBeforeNodeDrop}
				,nodedrop:{scope:this, fn:this.onNodeDrop}
				,nodedragover:{scope:this, fn:this.onNodeDragOver}
				,startdrag:{scope:this, fn:this.onStartDrag}
			});

		}
		// }}}
		// {{{
		// add events
		this.addEvents(
			/**
			 * Fires before request is sent to the server, return false to cancel the event.
			 *
			 * @event beforeremoverequest
			 * @param {Ext.ux.tree.RemoteTreePanel} tree This tree panel
			 * @param {Object} options Options as passed to Ajax.request
			 */
			 'beforeremoverequest'

			/**
			 * Fires before request is sent to the server, return false to cancel the event.
			 *
			 * @event beforerenamerequest
			 * @param {Ext.ux.tree.RemoteTreePanel} tree This tree panel
			 * @param {Object} options Options as passed to Ajax.request
			 */
			,'beforerenamerequest'

			/**
			 * Fires before request is sent to the server, return false to cancel the event.
			 *
			 * @event beforeappendrequest
			 * @param {Ext.ux.tree.RemoteTreePanel} tree This tree panel
			 * @param {Object} options Options as passed to Ajax.request
			 */
			,'beforeappendrequest'

			/**
			 * Fires before request is sent to the server, return false to cancel the event.
			 *
			 * @event beforeinsertrequest
			 * @param {Ext.ux.tree.RemoteTreePanel} tree This tree panel
			 * @param {Object} options Options as passed to Ajax.request
			 */
			,'beforeinsertrequest'

			/**
			 * Fires before request is sent to the server, return false to cancel the event.
			 *
			 * @event beforeremoverequest
			 * @param {Ext.ux.tree.RemoteTreePanel} tree This tree panel
			 * @param {Object} options Options as passed to Ajax.request
			 */
			,'beforemoverequest'

		);
		// }}}

    } // eo function initComponent
    // }}}
	// {{{
	/**
	 * initEvents override
	 *
	 * @private
	 */
	,initEvents:function() {
		Ext.ux.tree.RemoteTreePanel.superclass.initEvents.apply(this, arguments);
		if(true === this.enableDD) {
			// prevent dragging if the tree is not editable
			this.dragZone.onBeforeDrag = function(data, e) {
				var n = data.node;
				return n && n.draggable && !n.disabled && this.tree.editable;
			}; // eo function onBeforeDrag
		}
	} // eo function initEvents
	// }}}
	// {{{
	/**
	 * Server request (action) callback function)
	 * @param {Object} options Options used for request
	 * @param {Boolean} success
	 * @param {Object} response
	 */
	,actionCallback:function(options, success, response) {

		// remove loading indicator
		if(options.node) {
			options.node.getUI().afterLoad();
		}

		// {{{
		// failure handling
		if(true !== success) {
			this.showError(response.responseText);
			return;
		}
		var o;
		try {
			o = Ext.decode(response.responseText);
		}
		catch(ex) {
			this.showError(response.responseText);
			return;
		}
		if(true !== o.success) {
			this.showError(o.error || o.errors);
			switch(options.action) {
				case 'appendChild':
				case 'insertChild':
					options.node.parentNode.removeChild(options.node);
				break;

				default:
				break;
			}
			return;
		}
		if(!options.action) {
			this.showError('Developer error: no options.action');
		}
		// }}}
		//{{{
		// success handling - synchronize ui with server action
		switch(options.action) {
			case 'renameNode':
				options.node.setText(options.params.newText);
			break;

			case 'removeNode':
				options.node.parentNode.removeChild(options.node);
			break;

			case 'moveNode':
				if('append' === options.e.point) {
					options.e.target.expand();
				}
				this.dropZone.completeDrop(options.e);
			break;

			case 'appendChild':
			case 'insertChild':
				// change id of the appended/inserted node
				this.unregisterNode(options.node);
				options.node.id = o.id;
				Ext.fly(options.node.getUI().elNode).set({'ext:tree-node-id':o.id});
				this.registerNode(options.node);
				options.node.select();
			break;
		}
		//}}}

	} // eo function actionCallback
	// }}}
	// {{{
	/**
	 * Returne combined object of baseParams and params
	 * @private
	 * @param {Object} params params to combine with baseParams
	 */
	,applyBaseParams:function(params) {
		var o = Ext.apply({}, this.baseParams || this.loader.baseParams || {});
		Ext.apply(o, params || {});
		return o;
	}
	// }}}
	// {{{
	/**
	 * Requests server to append the child node. Child node has already been appended
	 * at client but it is removed if server append fails
	 * 
	 * @param {Ext.tree.TreeNode} childNode node to append
	 * @param {Boolean} insert Do not apppend but insert flag
	 */
	,appendChild:function(childNode, insert) {

		var params = this.applyBaseParams();
		params[this.paramNames.cmd] = true === insert ? this.cmdNames.insertChild : this.cmdNames.appendChild;
		params[this.paramNames.id] = childNode.parentNode.id;
		params[this.paramNames.text] = childNode.text;

		var o = Ext.apply(this.getOptions(), {
			 action:true === insert ? 'insertChild' : 'appendChild'
			,node:childNode
			,params:params
		});

		if(false !== this.fireEvent('before' + (insert ? 'insert' : 'append') + 'request', this, o)) {

			// set loading indicator
			childNode.getUI().beforeLoad();
			Ext.Ajax.request(o);
		}
	} // eo function appendChild
	// }}}
	// {{{
	/**
	 * Returns options for server request
	 * @return {Object} options for request
	 * @private
	 */
	,getOptions:function() {
		return {
			 url:this.loader.url || this.loader.dataUrl || this.url || this.dataUrl
			,method:this.loader.method || this.method || 'POST'
			,scope:this
			,callback:this.actionCallback
		};
	} // eo function getOptions
	// }}}
	// {{{
	/**
	 * appendChild action handler
	 * @param {Boolean} insert Do not append but insert flag
	 * @private
	 */
	,onAppendChild:function(insert) {
		this.actionNode = this.actionNode || this.selectedNode;
		if(!this.actionNode) {
			return;
		}
		var node = this.actionNode;
		var child;
		node.leaf = false;
		node.expand(false, false, function(n) {
			if(true === insert) {
				child = n.insertBefore(this.loader.createNode({text:this.newText, loaded:true}), n.firstChild);
			}
			else {
				child = n.appendChild(this.loader.createNode({text:this.newText, loaded:true}));
			}
		}.createDelegate(this));

		this.editor.creatingNode = true;
		if(true === insert) {
			this.editor.on({complete:{scope:this, single:true, fn:this.onInsertEditComplete}});
		}
		else {
			this.editor.on({complete:{scope:this, single:true, fn:this.onAppendEditCompete}});
		}

		this.editor.triggerEdit(child);
		this.actionNode = null;

	} // eo function onAppendChild
	// }}}
	// {{{
	/**
	 * Editing complete event handler
	 *
	 * @param {Ext.tree.TreeEditor} editor
	 * @param {String} newText 
	 * @param {String} oldText
	 * @private
	 */
	,onAppendEditCompete:function(editor, newText, oldText) {
		this.appendChild(editor.editNode);
	} // onAppendEditCompete
	// }}}
	// {{{
	/**
	 * Before node drop event handler. Cancels node drop at client but initiates
	 * request to server. Drop is completed if the server returns success
	 *
	 * @param {Object} e DD object
	 * @private
	 */
	,onBeforeNodeDrop:function(e) {

		this.moveNode(e);
		e.dropStatus = true;
		return false;

	} // eo function onBeforeNodeDrop
	// }}}
	// {{{
	/**
	 * contextmenu event handler. Shows context menu
	 *
	 * @param {Ext.tree.TreeNode} node right-clicked node
	 * @param {Ext.EventObject} e event
	 */
	,onContextMenu:function(node, e) {
		var menu = this.contextMenu;

		// no node under click - use root node
		if(node.browserEvent) {
			this.getSelectionModel().clearSelections();
			menu.showAt(node.getXY());
			this.actionNode = this.getRootNode();
			node.stopEvent();
		}
		// a node under click
		else {
			node.select();
			this.actionNode = node;
			var alignEl = node.getUI().getEl();
			var xy = menu.getEl().getAlignToXY(alignEl, 'tl-tl', [0, 18]);
			menu.showAt([e.getXY()[0], xy[1]]);
			e.stopEvent();
		}

		var actions = this.actions;
		var disable = true !== this.editable || !this.actionNode;
		actions.appendChild.setDisabled(disable);
		actions.renameNode.setDisabled(disable);
		actions.removeNode.setDisabled(disable);
		actions.insertChild.setDisabled(disable);

	} // eo function onContextMenu
	// }}}
	// {{{
	/**
	 * Event handler of editor complete event
	 * Calls rename node but returns false as ui is updated later on success
	 *
	 * @param {Ext.tree.TreeEditor} editor
	 * @param {String} newText
	 * @param {String} oldText
	 * @return {Boolean} false - to cancel immediate editing/renaming
	 */
	,onEditComplete:function(editor, newText, oldText) {
		if(editor.creatingNode) {
			editor.creatingNode = false;
			return;
		}

		this.renameNode(editor.editNode, newText);
		return false;
	} // eo function onEditComplete
	// }}}
	// {{{
	/**
	 * expandAll action handler
	 *
	 * @private
	 */
	,onExpandAll:function() {
		this.getRootNode().expand(true, false);
	} // eo function onExpandAll
	// }}}
	// {{{
	/**
	 * expandNode action handler
	 *
	 * @private
	 */
	,onExpandNode:function() {
		(this.actionNode || this.selectedNode || this.getRootNode()).expand(true, false);
	} // eo function onExpandNode
	// }}}
	// {{{
	/**
	 * collapseAll action handler
	 *
	 * @private
	 */
	,onCollapseAll:function() {
		this.getRootNode().collapse(true, false);
	} // eo function onCollapseAll
	// }}}
	// {{{
	/**
	 * collapseNode action handler
	 *
	 * @private
	 */
	,onCollapseNode:function() {
		(this.actionNode || this.selectedNode || this.getRootNode()).collapse(true, false);
	} // eo function onCollapseNode
	// }}}
	// {{{
	/**
	 * insertNode editing completed event handler
	 *
	 * @param {Ext.tree.TreeEditor} editor
	 * @param {String} newText
	 * @param {String} oldText
	 * @private
	 */
	,onInsertEditComplete:function(editor, newText, oldText) {
		this.appendChild(editor.editNode, true);
	} // eo onInsertEditComplete
	// }}}
	// {{{
	/**
	 * insertChild action handler
	 *
	 * @private
	 */
	,onInsertChild:function() {
		this.onAppendChild(true);
	} // onInsertChild
	// }}}
	 // {{{
	/**
	 * Delete key event handler. Calls delete action if a node is selected
	 *
	 * @param {Number} key
	 * @param {Ext.EventObject} e 
	 */
	,onKeyDelete:function(key, e) {
		this.actionNode = this.getSelectionModel().getSelectedNode();
		this.actions.removeNode.execute();
	} // eo onKeyDelete
	// }}}
	 // {{{
	/**
	 * Edit key (F2) event handler. Triggers editing.
	 *
	 * @param {Number} key
	 * @param {Ext.EventObject} e 
	 */
	,onKeyEdit:function(key, e) {
		var node = this.getSelectionModel().getSelectedNode();
		if(node && true === this.editable) {
			this.actionNode = node;
			this.onRenameNode();
		}
	} // eo onKeyEdit
	// }}}
	// {{{
	/**
	 * nodedragover event handler. Resets leaf flag if appendig to leafs is allowed
	 *
	 * @param {Object} e DD object
	 * @private
	 */
	,onNodeDragOver:function(e) {
		if(true === this.allowLeafAppend) {
			e.target.leaf = false;
		}
	} // eo function onNodeDragOver
	// }}}
	// {{{
	/**
	 * nodedrop event handler
	 *
	 * @param {Object} e DD object
	 * @private
	 */
	,onNodeDrop:function(e) {
	} // eo function onNodeDrop
	// }}}
	// {{{
	/**
	 * onRender override
	 *
	 * @private
	 */
	,onRender:function() {
		Ext.ux.tree.RemoteTreePanel.superclass.onRender.apply(this, arguments);
		if(false === this.rootVisible && this.contextMenu) {
			this.el.on({contextmenu:{scope:this, fn:this.onContextMenu, stopEvent:true}});
		}
	} // eo function onRender
	// }}}
	// {{{
	/**
	 * removeNode action handler
	 *
	 * @private
	 */
	,onRemoveNode:function() {
		this.actionNode = this.actionNode || this.selectedNode;
		if(!this.actionNode) {
			return;
		}
		var node = this.actionNode;
		this.removeNode(node);
		this.actionNode = null;
	} // eo function onRemoveNode
	// }}}
	// {{{
	/**
	 * renameNode action handler
	 *
	 * @private
	 */
	,onRenameNode:function() {
		this.actionNode = this.actionNode || this.selectedNode;
		if(!this.actionNode) {
			return;
		}
		var node = this.actionNode;
		this.editor.triggerEdit(node, 10);
		this.actionNode = null;
	} // eo function onRenameNode
	// }}}
	// {{{
	/**
	 * Adds a custom class to drag ghost - default icons are always used otherwise
	 *
	 * @private
	 */
	,onStartDrag:function() {
		this.dragZone.proxy.ghost.addClass(this.cls || this.initialConfig.cls || '');
	} // eo function onStartDrag
	// }}}
	// {{{
	/**
	 * Requests server to move node. Node move has been initiated at client but
	 * has been cancelled. The move is completed if the server returns succes.
	 *
	 * @param {Object} e DD object
	 * @private
	 */
	,moveNode:function(e) {

		var params = this.applyBaseParams();
		params[this.paramNames.cmd] = this.cmdNames.moveNode;
		params[this.paramNames.id] = e.dropNode.id;
		params[this.paramNames.target] = e.target.id;
		params[this.paramNames.point] = e.point;

		var o = Ext.apply(this.getOptions(), {
			 action:'moveNode'
			,e:e
			,node:e.dropNode
			,params:params
		});

		if(false !== this.fireEvent('beforemoverequest', this, o)) {
			// set loading indicator
			e.dropNode.getUI().beforeLoad();
			Ext.Ajax.request(o);
		}

	} // eo function moveNode
	// }}}
	// {{{
	/**
	 * Sends request to server to remove the node. Node is removed from UI
	 * if the server returns success.
	 *
	 * @param {Ext.tree.TreeNode} node to remove
	 * @private
	 */
	,removeNode:function(node) {
		if(0 === node.getDepth()) {
			return;
		}
		Ext.Msg.show({
			 title:this.deleteText
			,msg:this.reallyWantText + ' ' + this.deleteText.toLowerCase() + ': <b>' + node.text + '</b>?'
			,icon:Ext.Msg.QUESTION
			,buttons:Ext.Msg.YESNO
			,scope:this
			,fn:function(response) {
				if('yes' !== response) {
					return;
				}

				var params = this.applyBaseParams();
				params[this.paramNames.cmd] = this.cmdNames.removeNode;
				params[this.paramNames.id] = node.id;

				var o = Ext.apply(this.getOptions(), {
					 action:'removeNode'
					,node:node
					,params:params
				});

				if(false !== this.fireEvent('beforeremoverequest', this, o)) {
					// set loading indicator
					node.getUI().beforeLoad();
					Ext.Ajax.request(o);
				}
			}
		});
	} // eo function removeNode
	// }}}
	// {{{
	/**
	 * Sends request to server to rename the node
	 *
	 * @param {Ext.tree.TreeNode} node Node to rename
	 * @param {String} newText New name for the node
	 * @private
	 */
	,renameNode:function(node, newText) {

		var params = this.applyBaseParams();
		params[this.paramNames.cmd] = this.cmdNames.renameNode;
		params[this.paramNames.id] = node.id;
		params[this.paramNames.newText] = newText;
		params[this.paramNames.oldText] = node.text || '';

		var o = Ext.apply(this.getOptions(), {
			 action:'renameNode'
			,node:node
			,params:params
		});

		if(false !== this.fireEvent('beforerenamerequest', this, o)) {
			// set loading indicator
			node.getUI().beforeLoad();
			Ext.Ajax.request(o);
		}

	} // eo function renameNode
	// }}}
	// {{{
	/**
	 * Shows error
	 *
	 * @param {String} msg Error message to display
	 * @param {String} title Title of the error dialog. Defaults to 'Error'
	 */
	,showError:function(msg, title) {
		Ext.Msg.show({
			 title:title || this.errorText
			,msg:msg
			,icon:Ext.Msg.ERROR
			,buttons:Ext.Msg.OK
		});
	} // eo function showError
	// }}}

}); // eo extend
 
// register xtype
Ext.reg('remotetreepanel', Ext.ux.tree.RemoteTreePanel); 
 
// eof
