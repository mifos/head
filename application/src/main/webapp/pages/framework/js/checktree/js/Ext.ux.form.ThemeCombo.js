// vim: ts=4:sw=4:nu:fdc=4:nospell
/*global Ext */
/**
 * @class Ext.ux.form.ThemeCombo
 * @extends Ext.form.ComboBox
 *
 * Combo pre-configured for themes selection. Keeps state if a provider is present.
 * 
 * @author      Ing. Jozef Sakáloš 
 * @copyright   (c) 2008, by Ing. Jozef Sakáloš
 * @version     1.1
 * @date        30. January 2008
 * @revision    $Id: Ext.ux.form.ThemeCombo.js 589 2009-02-21 23:30:18Z jozo $
 *
 * @license Ext.ux.form.ThemeCombo is licensed under the terms of
 * the Open Source LGPL 3.0 license.  Commercial use is permitted to the extent
 * that the code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 * 
 * <p>License details: <a href="http://www.gnu.org/licenses/lgpl.html"
 * target="_blank">http://www.gnu.org/licenses/lgpl.html</a></p>
 *
 * @demo     http://extjs.eu
 * @forum    25564
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

Ext.ns('Ext.ux.form');

/**
 * Creates new ThemeCombo
 * @constructor
 * @param {Object} config A config object
 */
Ext.ux.form.ThemeCombo = Ext.extend(Ext.form.ComboBox, {
	// configurables
	 themeBlueText: 'Ext Blue Theme'
	,themeGrayText: 'Gray Theme'
	,themeBlackText: 'Black Theme'
	,themeOliveText: 'Olive Theme'
	,themePurpleText: 'Purple Theme'
	,themeDarkGrayText: 'Dark Gray Theme'
	,themeSlateText: 'Slate Theme'
	,themeVistaText: 'Vista Theme'
	,themePeppermintText: 'Peppermint Theme'
	,themePinkText: 'Pink Theme'
	,themeChocolateText: 'Chocolate Theme'
	,themeGreenText: 'Green Theme'
	,themeIndigoText: 'Indigo Theme'
	,themeMidnightText: 'Midnight Theme'
	,themeSilverCherryText: 'Silver Cherry Theme'
	,themeSlicknessText: 'Slickness Theme'
	,themeVar:'theme'
	,selectThemeText: 'Select Theme'
	,themeGrayExtndText:'Gray-Extended Theme'
	,lazyRender:true
	,lazyInit:true
	,cssPath:'../ext/resources/css/' // mind the trailing slash

	// {{{
	,initComponent:function() {

		var config = {
			store: new Ext.data.SimpleStore({
				fields: ['themeFile', {name:'themeName', type:'string'}]
				,data: [
					 ['xtheme-default.css', this.themeBlueText]
					,['xtheme-gray.css', this.themeGrayText]
					,['xtheme-darkgray.css', this.themeDarkGrayText]
					,['xtheme-black.css', this.themeBlackText]
					,['xtheme-olive.css', this.themeOliveText]
					,['xtheme-purple.css', this.themePurpleText]
					,['xtheme-slate.css', this.themeSlateText]
					,['xtheme-peppermint.css', this.themePeppermintText]
					,['xtheme-chocolate.css', this.themeChocolateText]
					,['xtheme-green.css', this.themeGreenText]
					,['xtheme-indigo.css', this.themeIndigoText]
					,['xtheme-midnight.css', this.themeMidnightText]
					,['xtheme-silverCherry.css', this.themeSilverCherryText]
					,['xtheme-slickness.css', this.themeSlicknessText]
					,['xtheme-gray-extend.css', this.themeGrayExtndText]
//					,['xtheme-pink.css', this.themePinkText]
				]
			})
			,valueField: 'themeFile'
			,displayField: 'themeName'
			,triggerAction:'all'
			,mode: 'local'
			,forceSelection:true
			,editable:false
			,fieldLabel: this.selectThemeText
		}; // eo config object

		// apply config
        Ext.apply(this, Ext.apply(this.initialConfig, config));

		this.store.sort('themeName');

		// call parent
		Ext.ux.form.ThemeCombo.superclass.initComponent.apply(this, arguments);

		if(false !== this.stateful && Ext.state.Manager.getProvider()) {
			this.setValue(Ext.state.Manager.get(this.themeVar) || 'xtheme-default.css');
		}
		else {
			this.setValue('xtheme-default.css');
		}

	} // end of function initComponent
	// }}}
	// {{{
	,setValue:function(val) {
		Ext.ux.form.ThemeCombo.superclass.setValue.apply(this, arguments);

		// set theme
		Ext.util.CSS.swapStyleSheet(this.themeVar, this.cssPath + val);

		if(false !== this.stateful && Ext.state.Manager.getProvider()) {
			Ext.state.Manager.set(this.themeVar, val);
		}
	} // eo function setValue
	// }}}

}); // end of extend

// register xtype
Ext.reg('themecombo', Ext.ux.form.ThemeCombo);

// eof
