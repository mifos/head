// vim: ts=4:sw=4:nu:fdc=4:nospell
/**
 * Ext.ux.IconMenu Plugin 
 *
 * Adds user defined menu to Window/Panel icon and adds dbl click close action
 *
 * @author    Ing. Jozef Sakáloš
 * @date      19. March 2008
 * @version   $Id: Ext.ux.IconMenu.js 174 2008-04-12 11:38:25Z jozo $
 *
 * @license Ext.ux.IconMenu is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * License details: http://www.gnu.org/licenses/lgpl.html
 */

/*global Ext */

// still only in svn
if(!Ext.isArray) {
	Ext.isArray = function(v){
		return v && typeof v.pop === 'function';
	};
}

/**
 * @class Ext.ux.IconMenu
 * @extends Ext.util.Observable
 *
 * @constructor
 * Creates new IconMenu
 * @param {Object} config The configuration object
 */
Ext.ux.IconMenu = function(config) {
	// apply config
	Ext.apply(this, config);

	// call parent
	Ext.ux.IconMenu.superclass.constructor.apply(this, arguments);

}; // eo constructor

Ext.extend(Ext.ux.IconMenu, Ext.util.Observable, {

	// configuration options
	// {{{
	/**
	 * @cfg {String} closeText Text for Close item
	 */
	 closeText:'Close'

	/**
	 * @cfg {Boolean} createDefault false to not create default menu
	 */
	,createDefault:true

	/**
	 * @cfg {Boolean} dblClickClose false to disable dblclick close action (defaults to true)
	 */
	,dblClickClose:true

	/**
	 * @cfg {Array} customItems Array of menu items configs.
	 * For example:
	 * new Ext.ux.IconMenu:({
	 *  customItems:['separator'
	 *	,{
	 *		 text:'This is <b>zoom</b> item'
	 *		,iconCls:'icon-zoom'
	 *		,handler:function() {console.info(arguments)}
	 *	},{
	 *		 text:'This is <b>print</b> item'
	 *		,iconCls:'icon-print'
	 *		,handler:function() {console.info(arguments)}
	 *	}]
	 */

	/**
	 * @cfg {Array} defaultItems Default items to create. If you add items you need to add texts and handlers too.
	 */
	,defaultItems:['restore', 'minimize', 'maximize', 'separator', 'close']

	/**
	 * @cfg {String} iconCls Optional but recommended. Icon Class to use. If not set iconCls of Panel/Window is used
	 */

	/**
	 * @cfg {String} maximizeText Text for Maximize item
	 */
	,maximizeText:'Maximize'
	
	/**
	 * @cfg {Ext.menu.Menu} menu Optional. Menu to show on click.
	 */

	/**
	 * @cfg {String} minimizeText Text for Minimize item
	 */
	,minimizeText:'Minimize'

	/**
	 * @cfg {String} qtip Optional. Tooltip to display when mouse cursor hovers over icon
	 */

	/**
	 * @cfg {String} restoreText Text for Restor item
	 */
	,restoreText:'Restore'

	/**
	 * @cfg {String} style Style to use for icon div
	 */
	,style:'width:16px;height:16px;left:0;top:4px;position:absolute;cursor:pointer'

	/**
	 * @cfg {String} tooltip Optional. Synonym for qtip
	 */

	// }}}

	// methods
	// {{{
	/** 
	 * Closes Panel/Window
	 * @private
	 */
	,closeHandler:function() {
		if(this.panel.closable) {
			var action = this.panel.closeAction;
			if('hide' !== action) {
				this.onDestroy();
			}
			this.panel[action]();
		}
	} // eo function closeHandler
	// }}}
	// {{{
	/**
	 * Returns menu item identified by cmd
	 * @param {String} cmd 
	 * @return {Ext.menu.Item}
	 */
	,getItemByCmd:function(cmd) {
		if(!this.menu || !cmd) {
			return null;
		}
		var item = this.menu.items.find(function(i) {
			return cmd === i.cmd;
		});
		return item;

	} // eo function getItemByCmd
	// }}}
	// {{{
	/**
	 * Hides menu
	 */
	,hideMenu:function() {
		if(this.menu) {
			this.menu.hide();
		}
	} // eo function hideMenu
	// }}}
	// {{{
	/**
	 * Main init function
	 * @private
	 * @param {Ext.Panel/Ext.Window} panel Panel/Window this plugin is in
	 */
	,init:function(panel) {
		this.panel = panel;
		this.iconCls = this.iconCls || panel.iconCls;
		panel.on({
			 scope:this
			,render:this.onRender
			,hide:this.hideMenu
			,destroy:this.onDestroy
		});
		// create default menu
		var i, cfg, item;
		var menuId = 'im-' + panel.id;

		// create default menu
		if(this.createDefault && Ext.isArray(this.defaultItems) && this.defaultItems.length) {
			if(this.menu) {
				this.menu.add('-');
			}
			else {
				this.menu = new Ext.menu.Menu({id:menuId});
			}
			for(i = 0; i < this.defaultItems.length; i++) {
				this.addItem(this.defaultItems[i]);
			}
		}

		// create custom menu
		if(Ext.isArray(this.customItems)) {
			if(!this.menu) {
				this.menu = new Ext.menu.Menu({id:menuId});
			}
			for(i = 0; i < this.customItems.length; i++) {
				this.addItem(this.customItems[i]);
			}
		}

		// install handlers
		if(this.menu) {
			this.menu.id = this.menu.id || menuId;
			this.menu = Ext.menu.MenuMgr.get(this.menu);
			this.menu.on("show", this.onMenuShow, this);
			this.menu.on("hide", this.onMenuHide, this);
			this.menu.parentPanel = this.panel;
		}
	} // eo function init
	// }}}
	// {{{
	/**
	 * Adds item to menu
	 * @private
	 * @param {String/Object} item String for default items config object for custom items
	 */
	,addItem:function(item) {
		if('separator' === item || '-' === item) {
			this.menu.add('-');
			return;
		}
		var cfg;
		if('string' === typeof item) {
			cfg = {
				 text:this[item + 'Text']
				,cmd:item
				,iconCls:'x-im-icon x-tool x-tool-' + item
				,scope:this
				,handler:this[item + 'Handler']
			};
		}
		else {
			cfg = item;
		}
		this.menu.add(cfg);
	} // eo function addItem
	// }}}
	// {{{
	/**
	 * Maximizes Panel/Window
	 * @private
	 */
	,maximizeHandler:function() {
		this.hideMenu();
		if(this.panel.maximizable) {
			this.panel.maximize();
		}
	} // eo function maximizePanel
	// }}}
	// {{{
	/**
	 * Minimizes Panel/Window
	 * @private
	 */
	,minimizeHandler:function() {
		this.hideMenu();
		if(this.panel.minimizable) {
			this.panel.minimize();
		}
	} // eo function minimizePanel
	// }}}
	// {{{
	/**
	 * Destroys underlying menu
	 * @private
	 */
	,onDestroy:function() {
		if(this.menu) {
			var el = this.menu.getEl();
			if(el.shadow && el.shadow.el) {
				el.shadow.el.remove();
			}
			this.menu.destroy();
			el.remove();
			this.menu = null;
		}
	} // eo function onDestroy
	// }}}
	// {{{
	/**
	 * hide menu event handler
	 * @private
	 */
    ,onMenuHide:function(e){
        this.ignoreNextClick = this.restoreClick.defer(350, this);
    } // eo function onMenuHide
	// }}}
	// {{{
	/**
	 * show menu event handler
	 * @private
	 */
    ,onMenuShow:function(e){
        this.ignoreNextClick = 0;
    } // eo function onMenuShow
	// }}}
	// {{{
	/**
	 * Creates new div for icon and installs event handlers
	 * @private
	 */
	,onRender:function() {

		var hd = this.panel.header;
		if(!hd) {
			return;
		}

		Ext.util.CSS.createStyleSheet('.x-im-icon{float:none!important;margin-left:0!important}');

		// adjust panel header
		hd.addClass('x-panel-icon');
		hd.applyStyles({position:'relative'});

		// create icon div
		this.icon = hd.insertFirst({
			 tag:'div'
			,id:Ext.id()
			,style:this.style
			,cls:this.iconCls
			,qtip:this.qtip || this.tooltip || ''
		}, 'first');

		// panels in layouts create img for icon so align ourselves to it
		var img = hd.down('img');
		if(img) {
			this.icon.alignTo(img, 'tl-tl');
			img.removeClass(this.panel.iconCls || this.iconCls);
			img.set({src:Ext.BLANK_IMAGE_URL});
		}

		// install icon event handlers
		this.icon.on({
			 scope:this
			,dblclick:function() {
				if(this.dblClickClose) {
					this.closeHandler();
				}
			}
			,click:{scope:this, delay:200, fn:function(e, t) {
				if(this.menu && !this.menu.isVisible() && !this.ignoreNextClick) {
					this.showMenu();
				}
			}}
		});

		// override panel's setIconClass method
		this.panel.setIconClass = this.setIconClass.createDelegate(this);

		// we need to refresh cache
		Ext.util.CSS.getRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon', true);

		// remove all padding (to get rid of !important)
		if(Ext.isIE) {
			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon','padding','');
			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon','padding','5px 0 4px 0');

			Ext.util.CSS.updateRule('.x-window-tl .x-panel-icon','padding','');
			Ext.util.CSS.updateRule('.x-window-tl .x-panel-icon','padding','5px 0 4px 0');
		}
		else {
			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon','padding','');
			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon','padding','5px 0 4px 0');
		}

		// set new padding
		var padding = this.icon.getWidth() + 4 + 'px' + ' !important';
		Ext.util.CSS.createStyleSheet('.x-im-header{padding-left:'+ padding + ';}');
		hd.addClass('x-im-header');

	} // eo function onRender
	// }}}
	// {{{
	/**
	 * @private
	 */
    ,restoreClick:function(){
        this.ignoreNextClick = 0;
    } // eo function restoreClick
	// }}}
	// {{{
	/**
	 * Restores Panel/Window
	 * @private
	 */
	,restoreHandler:function() {
		this.hideMenu();
		this.panel.restore();
	} // eo function restorePanel
	// }}}
	// {{{
	/**
	 * Replaces Panel/Window setIconClass method
	 * @private
	 */
	,setIconClass:function(iconCls) {
		this.icon.replaceClass(this.iconCls, iconCls);
	} // eo function setIconClass
	// }}}
	// {{{
	/**
	 * Shows menu
	 */
	,showMenu:function() {
		var item;
		if(this.menu) {
			try {
				// enable/disable close
				item = this.getItemByCmd('close');
				if(item) {
					item.setDisabled(!this.panel.closable);
				}

				// enable/disable maximize
				item = this.getItemByCmd('maximize');
				if(item) {
					item.setDisabled(!this.panel.maximizable || this.panel.maximized);
				}

				// enable/disable minimize
				item = this.getItemByCmd('minimize');
				if(item) {
					item.setDisabled(!this.panel.minimizable || this.panel.minimized);
				}

				// enable/disable restore
				item = this.getItemByCmd('restore');
				if(item) {
					item.setDisabled(!(this.panel.minimized || this.panel.maximized));
				}

				// show menu
				this.menu.show(this.icon, 'tl-bl?');
			} catch(e){}
		}
	}
	// }}}

}); // eo extend

Ext.reg('iconmenu', Ext.ux.IconMenu);

// eof
