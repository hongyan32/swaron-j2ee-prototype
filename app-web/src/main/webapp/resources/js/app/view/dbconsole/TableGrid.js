Ext.define('App.view.dbconsole.TableGrid', {
	requires : [
	    'App.service.CodeService',
	    'Ext.grid.plugin.RowEditing',
	    'Ext.ux.grid.HeaderFilters',
	    'Ext.grid.column.Date',
	    'Ext.data.Store',
	    'Ext.ux.CheckColumn',
	    'Ext.form.field.ComboBox',
	    'Ext.form.field.Date',
	    'Ext.form.field.Checkbox',
	    'Ext.toolbar.Paging'
	],
	extend : 'Ext.grid.Panel',
	alias : 'widget.tablegrid',
	title : 'DataSource Info Grid',
	frame : false,
	selModel : {
		selType : 'rowmodel'
	},
	buildStore:function(){
		var storeName = App.lazy.GridConfig.storeName;
		var store = Ext.create(storeName);
		this.store = store;
	},
	initComponent : function() {
		var headerFilter = Ext.create('Ext.ux.grid.HeaderFilters');
		var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
			errorSummary : false
		});
		this.plugins = [rowEditing, headerFilter];
		
		this.buildStore();
		this.columns = App.lazy.GridConfig.getGridColumns();
		this.callParent(arguments);
	}
});
