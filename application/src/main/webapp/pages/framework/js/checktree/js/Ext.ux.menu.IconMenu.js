// vim: ts=4:sw=4:nu:fdc=4:nospell
/*global Ext */

/**
 * @class   Ext.ux.menu.IconMenu
 * @extends Ext.util.Observable
 *
 * <p>This <b>plugin</b> adds user defined menu to Window/Panel icon and 
 * adds dbl click close action to the icon itself.</p>
 * <p>
 * Default menu contains:<ul class="list">
 * <li>Restore</li>
 * <li>Minimize</li>
 * <li>Maximize</li>
 * <li>Close</li>
 * </ul>
 * items. 
 * Also, you can close the window by dblclicking the icon by default.</p>
 * <p>
 * You can add your own custom items to the default menu, switch default menu off or 
 * use your own menu.</p>
 *
 * <p><i>Usage:</i></p>
 * <pre>
 * var win = new Ext.Window({
 * &nbsp;   plugins:[{new Ext.ux.menu.IconMenu({})]
 * });
 * </pre>
 *
 * @author    Ing. Jozef Sakáloš
 * @copyright (c) 2008, Ing. Jozef Sakáloš
 * @version   1.0
 * @date      19. March 2008
 * @revision  $Id: Ext.ux.menu.IconMenu.js 614 2009-03-07 22:14:11Z jozo $
 * @license   
 * Ext.ux.menu.IconMenu is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 *
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @forum     30677
 * @demo      http://iconmenu.extjs.eu
 * @download  
 * <ul>
 * <li><a href="http://iconmenu.extjs.eu/iconmenu-1.0.tar.bz2">iconmenu-1.0.tar.bz2</a></li>
 * <li><a href="http://iconmenu.extjs.eu/iconmenu-1.0.tar.gz">iconmenu-1.0.tar.gz</a></li>
 * <li><a href="http://iconmenu.extjs.eu/iconmenu-1.0.zip">iconmenu-1.0.zip</a></li>
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

Ext.ns('Ext.ux.menu');

/**
 * Creates new IconMenu plugin
 * @constructor
 * @param {Object} config The configuration object
 */
Ext.ux.menu.IconMenu = function(config) {
	// apply config
	Ext.apply(this, config);

	// call parent
	Ext.ux.menu.IconMenu.superclass.constructor.apply(this, arguments);

}; // eo constructor

Ext.extend(Ext.ux.menu.IconMenu, Ext.util.Observable, {

	// configuration options
	// {{{
	/**
	 * @cfg {String} closeText Localizable text used for Close menu item. Defaults to 'Close'.
	 */
	 closeText:'Close'

	/**
	 * @cfg {Boolean} createDefault Set this to false to prevent the plugin to create default menu.
	 * <p>The default menu consists of:<p>
	 * <ul class="list">
	 * <li>Restore</li>
	 * <li>Minimize</li>
	 * <li>Maximize</li>
	 * <li>-separator-</li>
	 * <li>Close</li>
	 * </ul>
	 * items.
	 */
	,createDefault:true

	/**
	 * @cfg {Boolean} dblClickClose Set this to false to disable icon dblclick close action. Defaults to true.
	 */
	,dblClickClose:true

	/**
	 * @cfg {Array} customItems Array of menu items configs. Defaults to undefined.
	 * <p>For example:<p>
	 * <pre>
	 * new Ext.ux.menu.IconMenu:({
	 *&nbsp;	customItems:['separator'
	 *&nbsp;	,{
	 *&nbsp;		 text:'This is <b>zoom</b> item'
	 *&nbsp;		,iconCls:'icon-zoom'
	 *&nbsp;		,handler:function() {console.info(arguments)}
	 *&nbsp;	},{
	 *&nbsp;		 text:'This is <b>print</b> item'
	 *&nbsp;		,iconCls:'icon-print'
	 *&nbsp;		,handler:function() {console.info(arguments)}
	 *&nbsp;	}]
	 * });
	 * </pre>
	 */

	/**
	 * @cfg {Array} defaultItems Array of default items to create. 
	 * If you add items you need to add texts and handlers too. Defaults to
	 * ['restore', 'minimize', 'maximize', 'separator', 'close'].
	 */
	,defaultItems:['restore', 'minimize', 'maximize', 'separator', 'close']

	/**
	 * @cfg {String} iconCls Optional but recommended. Icon Class to use. If it is not set the iconCls of 
	 * the parent Panel/Window is used.
	 */

	/**
	 * @cfg {String} maximizeText Localizable text used for Maximize menu item. Defaults to 'Maximize'.
	 */
	,maximizeText:'Maximize'
	
	/**
	 * @cfg {Ext.menu.Menu} menu Optional. Menu to show on click. Defaults to undefined.
	 * <p>Default items created by the plugin if <b>defaultItems</b> is set to true are appended,
	 * after separator, to this menu.
	 */

	/**
	 * @cfg {String} minimizeText Localizable text used for Minimize menu item. Defaults to 'Minimize'.
	 */
	,minimizeText:'Minimize'

	/**
	 * @cfg {String} qtip Optional. Tooltip to display when mouse cursor hovers over the icon.
	 */

	/**
	 * @cfg {String} restoreText Localizable text used for Restore menu item. Defaults to 'Restore'.
	 */
	,restoreText:'Restore'

	/**
	 * @cfg {String} style Style to use for icon div.
	 */
	,style:'width:16px;height:16px;left:0;top:4px;position:absolute;cursor:pointer'

	/**
	 * @cfg {String} tooltip Optional. Synonym for qtip.
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
	 * Returns menu item identified by <b>cmd</b>. The routine iterates through menu items
	 * and look for menu item property <b>cmd</b>. First menu item with matching cmd is returned.
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
			if(img.hasClass('x-panel-inline-icon')) {
				img.setStyle({width:'2px'});
			}
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
//		Ext.util.CSS.getRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon', true);

		// remove all padding (to get rid of !important)
//		if(Ext.isIE) {
//			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon','padding','');
//			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon','padding','5px 0 4px 0');
//
//			Ext.util.CSS.updateRule('.x-window-tl .x-panel-icon','padding','');
//			Ext.util.CSS.updateRule('.x-window-tl .x-panel-icon','padding','5px 0 4px 0');
//		}
//		else {
//			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon','padding','');
//			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon','padding-left','20px!important');
//			Ext.util.CSS.updateRule('.x-panel-tl .x-panel-icon, .x-window-tl .x-panel-icon','padding','5px 0 4px 0');
//		}

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

Ext.reg('iconmenu', Ext.ux.menu.IconMenu);

// eof
