/**
 * ## Example Code
 * 
 * App.DictCode.getName('table','column', '1'); //return table->column->name <br>
 * App.DictCode.getCode('table','column', 'name'); // return table->column->code <br>
 * App.DictCode.converter('table','column'); //return a function which will convert code to name <br>
 * App.DictCode.store('table','column'); // return a store which contains all data of table.column
*/


Ext.define('App.service.DictCodeService', {
	alternateClassName: 'App.DictCode',
	singleton:true,
    requires : ['Ext.data.reader.Json', 'Ext.data.Store', 'App.model.DbCode', 'Ext.data.proxy.Ajax', 'Ext.data.proxy.LocalStorage',
			'Ext.data.proxy.Rest',  'Ext.data.Request', 'Ext.data.Batch'],
	url : App.url('/rest/code/dict.json'),
	localStore : null,
	remoteStore : null,
	nameCache : {},
	mixins : {
		observable : 'Ext.util.Observable'
	},
	initStore : function() {
		var localStore = null;
		try {
			localStore = Ext.create('Ext.data.Store', {
				model : 'App.model.DbCode',
				autoLoad : false,
				proxy : {
					type : 'localstorage',
					id : 'app-ecode'
				}
			});
			//proxy will be instantiated in constructor of Ext.data.AbstractStore, will test the availability of localstorage.
		} catch (e) {
			localStore = null;
			App.log('failed to create localstorage, fallback to remote store.', e);
		}

		if (localStore) {
			var reload;
			try {
				localStore.load();
				var version = localStore.findRecord('column', '_store_version');
				reload = (version == null) || (version.get('code') != App.cfg.version);
			} catch (e) {
				// fix if data corrupted
				reload = true;
				App.log('unable to read from local store, try to clear local store', e);
			}
			if (reload) {
				App.log('not latest version, reload codes. latest version: ' + App.cfg.version);
				this.remoteStore = Ext.create('Ext.data.Store', {
					model : 'App.model.DbCode',
					proxy : {
						type : 'ajax',
						url : this.url,
						reader : {
							type : 'json'
						}
					},
					autoLoad : false
				});
//				var oldAsync = Ext.Ajax.async;
//				Ext.Ajax.async = false;
				this.remoteStore.load({
					scope : this,
					callback : function(records, operation, success) {
						if(!success){
							return;
						}
						localStore.getProxy().clear();
						localStore.load();
						App.log('load code records into local storage.');
						Ext.each(records, function(record, index) {
							delete record.data[record.idProperty];
							localStore.add(record.data);
						});
						localStore.add({
							column : '_store_version',
							code : App.cfg.version,
							name : App.cfg.version
						});
						localStore.sync();
					}
				});
//				Ext.Ajax.async = oldAsync;
			}
		} else {
			this.remoteStore = Ext.create('Ext.data.Store', {
				model : 'App.model.DbCode',
				proxy : {
					type : 'ajax',
					url : this.url,
					reader : {
						type : 'json'
					}
				},
				autoLoad : false
			});
			this.remoteStore.load();
		}
		this.localStore = localStore;
		this.codeStore = localStore || this.remoteStore;
	},
	findName:function(table, column, code, defaultValue){
		if (code == null || code == '') {
			return defaultValue === undefined ? '': defaultValue;
		}
		var store = this.codeStore;
		var index = store.findBy(function(model) {
			return model.get('table') == table && model.get('column') == column && model.get('code') == code;
		});
		if (index == -1) {
			var msg = Ext.String.format('unable to get magiccode definition, table:{0},column:{1},code:{2}', table, column,code);
			App.log(msg);
			App.log('records count in store:' + store.getCount() + '. store info:', store);
		} else {
			return store.getAt(index).get('name');
		}
	},
	getName : function(table, column, code, defaultValue) {
		var key = table + '.' + column + '.' + code;
		if (this.nameCache[key] === undefined) {
			var name = this.findName(table, column, code, defaultValue);
            this.nameCache[key] = name;
        }
        return this.nameCache[key];
	},
	getCode : function(table, column, name) {
		if (name == null || name == '') {
			return null;
		}
		var store = this.codeStore;
		var index = store.findBy(function(model) {
			return model.get('table') == table && model.get('column') == column && model.get('name') == name;
		});
		if (index == -1) {
			var msg = Ext.String.format('unable to get magiccode definition, table:{0},column:{1},name:{2}', table, column,name);
			App.log(msg);
			App.log('records count in store:' + store.getCount() + '. store info:', store);
		} else {
			return store.getAt(index).get('code');
		}
	},
	store : function(table, column) {
		var columnStore = Ext.create('Ext.data.Store', {
			model : 'App.model.DbCode',
			sorters : [{
				property : 'order',
				direction : 'ASC'
			}]
		});
		var items = this.codeStore.queryBy(function(m) {
			return m.get('table') == table && m.get('column') == column;
		});
		columnStore.add(items.getRange());
		columnStore.sort();
		return columnStore;
	},
	converter : function(table, column, defaultValue) {
		var service = this;
		var fn = function(code) {
			return service.getName(table, column, code, defaultValue);
		}
		return fn;
	},
	test : function() {
		App.log('test getname with code 1 in syscode: ', this.getName('SYSCODE', 1));
		App.log('test getcode with 个人  in account_type: ', this.getCode('ACCOUNT_TYPE', '个人'));
		App.log('test store in result_code,count: ', this.store('RESULT_CODE').getCount());
		App.log('test converter in SYSCODE with 1 ', this.converter('SYSCODE')('1'));
	},
	constructor : function() {
		this.initStore();
	}
});
