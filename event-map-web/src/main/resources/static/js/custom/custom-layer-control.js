L.Control.CustomLayers = L.Control.Layers.extend({
	
	initialize: function (baseLayers, overlays, options) {
		L.Util.setOptions(this, {collapsed : false});//TODO:collapse false silinecek default true
		
		this._layerControlInputs = [];
		this._layers = [];
		this._lastZIndex = 0;
		this._handlingClick = false;
		
		for (var i in baseLayers) {
			this._addLayer(baseLayers[i], i);
		}
		
		for (i in overlays) {
			this._addLayer(overlays[i], overlays[i].name, true);
		}
	},
	_initLayout: function () {
		var className = 'leaflet-control-layers',
			container = this._container = L.DomUtil.create('div', className),
			collapsed = this.options.collapsed;
		
		// makes this work on IE touch devices by stopping it from firing a mouseout event when the touch is released
		container.setAttribute('aria-haspopup', true);
		
		L.DomEvent.disableClickPropagation(container);
		L.DomEvent.disableScrollPropagation(container);
		
		var section = this._section = L.DomUtil.create('section', className + '-list');
		
		if (collapsed) {
			this._map.on('click', this.collapse, this);
			
			if (!L.Browser.android) {
				L.DomEvent.on(container, {
					mouseenter: this.expand,
					mouseleave: this.collapse
				}, this);
			}
		}
		
		var link = this._layersLink = L.DomUtil.create('a', className + '-toggle', container);
		link.href = '#';
		link.title = 'Layers';
		
		if (L.Browser.touch) {
			L.DomEvent.on(link, 'click', stop);
			L.DomEvent.on(link, 'click', this.expand, this);
		} else {
			L.DomEvent.on(link, 'focus', this.expand, this);
		}
		
		if (!collapsed) {
			this.expand();
		}
		
		this._baseLayersList = L.DomUtil.create('div', className + '-base', section);
		this._separator = L.DomUtil.create('div', className + '-separator', section);
		this._overlaysList = L.DomUtil.create('div', className + '-overlays', section);
		
		container.appendChild(section);
	},
	_update: function () {
		if (!this._container) { return this; }
		
		L.DomUtil.empty(this._baseLayersList);
		L.DomUtil.empty(this._overlaysList);
		
		this._layerControlInputs = [];
		var baseLayersPresent, overlaysPresent, i, obj, baseLayersCount = 0;
		
		for (i = 0; i < this._layers.length; i++) {
			obj = this._layers[i];
			if (obj.layer.isGroupName) {
				
				this._addGroupItem(obj);
				
			} else {
				
				this._addItem(obj);
			}
			
			overlaysPresent = overlaysPresent || obj.overlay;
			baseLayersPresent = baseLayersPresent || !obj.overlay;
			baseLayersCount += !obj.overlay ? 1 : 0;
		}
		
		// Hide base layers section if there's only one layer.
		if (this.options.hideSingleBase) {
			baseLayersPresent = baseLayersPresent && baseLayersCount > 1;
			this._baseLayersList.style.display = baseLayersPresent ? '' : 'none';
		}
		
		this._separator.style.display = overlaysPresent && baseLayersPresent ? '' : 'none';
		
		return this;
	},
	_addGroupItem: function(obj) {//TODO: daha compact yapılabilir gibi bakılacak.
		
		let groupContainer = document.createElement('div');
		groupContainer.className = 'leaflet-control-layers-group';
		// groupContainer.id = 'leaflet-control-layers-group-' + obj.group.id;
		
		let groupName = document.createElement('span');
		groupName.className = 'leaflet-control-layers-group-name';
		groupName.innerHTML = obj.name;
		groupContainer.appendChild(groupName);
		
		this._overlaysList.appendChild(groupContainer);
		
		this._checkDisabledLayers();
		return groupContainer;
	},
	_addItem: function (obj) {
		var label = document.createElement('label'),
			checked = this._map.hasLayer(obj.layer),
			input;
		
		if (obj.overlay) {
			input = document.createElement('input');
			input.type = 'checkbox';
			input.className = 'leaflet-control-layers-selector';
			input.defaultChecked = checked;
		} else {
			input = this._createRadioElement('leaflet-base-layers_' + L.Util.stamp(this), checked);
		}
		
		this._layerControlInputs.push(input);
		input.layerId = L.Util.stamp(obj.layer);
		
		L.DomEvent.on(input, 'click', this._onInputClick, this);
		
		var name = document.createElement('span');
		name.innerHTML = ' ' + obj.name;
		
		// Helps from preventing layer control flicker when checkboxes are disabled
		// https://github.com/Leaflet/Leaflet/issues/2771
		var holder = document.createElement('div');
		if (obj.overlay) {
			holder.className = "leaflet-control-layers-grouped-checkbox-holder";
		}
		
		label.appendChild(holder);
		holder.appendChild(input);
		holder.appendChild(name);
		
		var container = obj.overlay ? this._overlaysList : this._baseLayersList;
		container.appendChild(label);
		
		this._checkDisabledLayers();
		return label;
	},
	
});

L.control.customLayers = function (baseLayers, overlays, options) {
	return new L.Control.CustomLayers(baseLayers, overlays, options);
};
