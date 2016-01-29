(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module with d3 as a dependency.
    define(['d3'], factory)
  } else if (typeof module === 'object' && module.exports) {
    // CommonJS
    module.exports = function(d3) {
      d3.tip = factory(d3)
      return d3.tip
    }
  } else {
    // Browser global.
    root.d3.tip = factory(root.d3)
  }
}(this, function (d3) {

function linechart(parent, config) {
	var conf = config || {};
	var margin = conf.margin || {top: 20, right: 20, bottom: 30, left: 50};
	var canvasWidth = conf.canvasWidth || 960,
		canvasHeight = conf.canvasHeight || 500;
	var width = canvasWidth - margin.left - margin.right,
    	height = canvasHeight - margin.top - margin.bottom;
	var data = conf.data || [];
	var timeProp = conf.timeProperty || 'time',
		valueProp = conf.valueProperty || 'value';
	var valueAxisLabel = conf.valueAxisLabel || valueProp;

	var x = d3.time.scale()
		.range([0, width]);
	var y = d3.scale.linear()
	    .range([height, 0]);
	var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("bottom");
	var yAxis = d3.svg.axis()
	    .scale(y)
	    .orient("left");

	var line = d3.svg.line()
	    .x(function(d) { return x(d[timeProp]); })
	    .y(function(d) { return y(d[valueProp]); });
	
	function component() {
		var svg = d3.select(parent || 'body').selectAll("svg").data([data]);
		var enter = svg.enter().append("svg")
			.attr("width", width + margin.left + margin.right)
			.attr("height", height + margin.top + margin.bottom)
			.append("g")
			.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		x.domain(d3.extent(data, function(d) { return d[timeProp]; }));
		y.domain(d3.extent(data, function(d) { return d[valueProp]; }));

		enter.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + height + ")")
			.call(xAxis);

		enter.append("g")
			.attr("class", "y axis")
			.call(yAxis)
			.append("text")
			.attr("transform", "rotate(-90)")
			.attr("y", 6)
			.attr("dy", ".71em")
			.style("text-anchor", "end")
			.text(valueAxisLabel);

		enter.append("path")
			.attr("class", "line");
		
		layout(svg);
	}
	
	function layout(svg) {
		svg.selectAll('.y.axis')
			.call(yAxis);
		svg.selectAll('path.line')
			.datum(data)
			.attr("d", line);
	}
	
	component.render = function() {
		component();
		return component;
	}
	
	component.data = function(v) {
		if (!arguments.length) return data;
		data = v;
		return component;
	}
	
	return component;
}

return linechart;

}));