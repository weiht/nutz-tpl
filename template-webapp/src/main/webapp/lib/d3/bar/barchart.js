(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module with d3 as a dependency.
    define(['d3', 'd3/plugins/tips'], factory);
  } else {
    // Browser global.
    d3.barchart = factory(root.d3, root.d3.tip);
  }
}(this, function (d3, tips) {

function barchart(parent, config) {
	var conf = config || {};
	var margin = conf.margin || {top: 40, right: 20, bottom: 30, left: 40};
	var canvasWidth = conf.canvasWidth || 960,
		canvasHeight = conf.canvasHeight || 500;
	var width = canvasWidth - margin.left - margin.right,
    	height = canvasHeight - margin.top - margin.bottom;
	var data = conf.data || [];
	var nameProp = conf.nameProperty || 'name',
		valueProp = conf.valueProperty || 'value';
	var valueAxisLabel = conf.valueAxisLabel || valueProp;

	var x = d3.scale.ordinal()
		.rangeRoundBands([0, width], .1);

	var y = d3.scale.linear()
	    .range([height, 0]);
	var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("bottom");

	var yAxis = d3.svg.axis()
	    .scale(y)
	    .orient("left");
	
	var tip = tips()
	  .attr('class', 'd3-tip d3BarChartTip')
	  .offset([-10, 0])
	  .html(function(d) {
	    return "<strong>" + valueAxisLabel + "：</strong> <span style='color:red'>" + d[valueProp] + "</span>";
	  });
	
	function component() {
        var svg = d3.select(parent).selectAll("svg").data([data]);
        var enter = svg.enter().append("svg")
		    .attr("width", width + margin.left + margin.right)
		    .attr("height", height + margin.top + margin.bottom)
		    .append("g")
		    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

		svg.call(tip);
		
		x.domain(data.map(function(d) { return d[nameProp]; }));
		y.domain([0, d3.max(data, function(d) { return d[valueProp]; })]);

		enter.selectAll(".bar")
			.data(data)
			.enter().append("rect")
			.attr("class", "bar")
			.attr("x", function(d) { return x(d[nameProp]); })
			.attr("width", x.rangeBand())
			.attr("y", function(d) { return y(d[valueProp]); })
			.attr("height", function(d) { return height - y(d[valueProp]); })
			.style('fill', function(d) { return d.color;})
			.on('click', clicked)
			.on('mouseover', barHovers)
			.on('mouseout', tip.hide);
		
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

		layout(svg);
	};
	
	function barHovers(d) {
		var showTip;
		if (typeof d.onhover == 'function') {
			showTip = d.onhover(d);
		}
		if (showTip !== false)
			tip.show(d);
	}
	
	function clicked(d) {
		if (typeof d.onclick == 'function') {
			d.onclick(d);
		}
		if (typeof conf.onclick == 'function') {
			conf.onclick(d);
		}
	}
	
	function layout(svg) {
		svg.selectAll('.y.axis')
			.call(yAxis);
		svg.selectAll('.bar')
			.attr("x", function(d) { return x(d[nameProp]); })
			.attr("width", x.rangeBand())
			.attr("y", function(d) { return y(d[valueProp]); })
			.attr("height", function(d) { return height - y(d[valueProp]); });
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

return barchart;

}));