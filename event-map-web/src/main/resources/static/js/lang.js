Lang = function () {
	this.props = undefined;
};

Lang.prototype.load = function (props) {
	
	this.props = props
};

Lang.prototype.get = function (key) {
	
	if (typeof this.props === "undefined") {
		return "??{0}??".f(key);
	}
	
	if(this.props[key]) {
		return this.props[key];
	}
	else {
		return "??{0}??".f(key);
	}
};

let lang = new Lang();
