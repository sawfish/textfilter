/*
 * Drag and Drop
 */

var testVar;


$(function() {

	jsPlumbInit();
	initConnections("miami", "chicago");

});



function initConnections(cityOne, cityTwo) {

	$.get("InitComparisonServlet", {
		cityOne : cityOne,
		cityTwo : cityTwo
	}, function(data) {
		console.log(data);
		testVar = data;
		createBipartiteGraph(data);
	}, "json");
	
}



function createBipartiteGraph(data){
	
	var disasters = data.disasters;
	var effects = data.effects;
	var edges = data.edges;
	
	for(var i = 0; i < disasters.length; i++){
		var disaster = disasters[i];
		var dNode = $("<div class='node d_node' id='disaster_" 
				+ disaster.id +"' style='top:" + (i*80 + 300) +"px;left:50px'></div>");
		$("#graph_panel").append(dNode);
	}
	
	for(var i = 0; i < effects.length; i++){
		var effect = effects[i];
		var eNode = $("<div class='node e_node' id='effect_" 
				+ effect.id +"' style='top:" + i*80 +"px;left:600px'></div>");
		$("#graph_panel").append(eNode);
	}
	
	
	var cityOneEdges = edges[data.cityOne];
	var cityTwoEdges = edges[data.cityTwo];
	
	for(var i = 0; i < cityOneEdges.length; i++){
		
		var edge = cityOneEdges[i];
		var d = jsPlumb.addEndpoint("disaster_" + edge.source, {anchor:jsPlumb.dAnchor}, jsPlumb.cityOneEndpoint);
		var t = jsPlumb.addEndpoint("effect_" + edge.target, {anchor:jsPlumb.eAnchor}, jsPlumb.cityTwoEndpoint);
		jsPlumb.connect({
			source : d,
			target : t,
			overlays : [ [ "Arrow", {
				width : 10,
				length : 15,
				location : 1
			} ] ]
		});
		
	}
	
	
	
	var source = $("<div class='d_node' id='source' style='left:100px;top:50px'></div>");
	var target = $("<div class='d_node' id='target' style='left:400px;top:50px'></div>");

	$("#graph_panel").append(source);
	$("#graph_panel").append(target);

	s = jsPlumb.addEndpoint("source", {}, {
		isSource : true,
		isTarget : true,
		maxConnections : -1
	});

	t = jsPlumb.addEndpoint("target", {}, {
		isSource : true,
		isTarget : true,
		maxConnections : -1
	});

	jsPlumb.connect({
		source : s,
		target : t,
		overlays : [ [ "Arrow", {
			width : 10,
			length : 15,
			location : 1
		} ] ]
	});

	jsPlumb.draggable($(".d_node"));
	
}



function jsPlumbInit() {

	
	var dynamicAnchors = [ [ 0.2, 0, 0, -1 ], 
	                       [ 1, 0.2, 1, 0 ],
	                       [ 0.8, 1, 0, 1 ], 
	                       [ 0, 0.8, -1, 0 ] ];

	jsPlumb.Defaults.Container = $("#graph_panel");
	jsPlumb.importDefaults({
		PaintStyle : {
			lineWidth : 0,
			strokeStyle : "orange",
			outlineColor : "orange",
			outlineWidth : 0
		},
		Connector : [ "StateMachine", {
			curviness : 20
		} ],
		Endpoint : [ "Dot", {
			radius : 3
		} ],
		EndpointStyle : {
			fillStyle : "orange"
		},
		Anchor : dynamicAnchors,
		// [ "TopCenter", "RightMiddle", "BottomCenter", "LeftMiddle" ]
		ConnectionsDetachable : false
	});
	
	
	
	var colorCityOne = "#316b31";
	jsPlumb.cityOneEndpoint = {
			endpoint: ["Dot", {
	            radius: 3
	        }],
	        paintStyle: {
	            fillStyle: colorCityOne
	        },
	        connectorStyle: {
	            strokeStyle: colorCityOne,
	            lineWidth: 3
	        },
	        connector: "Straight",
	        maxConnections: -1,
	        isSource: true,
	        scope: "green",
	        isTarget: true
	};


	var colorCityTwo = "rgba(229,219,61,0.5)";
	jsPlumb.cityTwoEndpoint = {
			endpoint: ["Dot", {
	            radius: 17
	        }],
	        anchor: "BottomLeft",
	        paintStyle: {
	            fillStyle: colorCityTwo,
	            opacity: 0.5
	        },
	        connectorStyle: {
	            strokeStyle: colorCityTwo,
	            lineWidth: 4
	        },
	        connector: "Straight",
	        maxConnections: -1,
	        isSource: true,
	        scope: 'yellow',
	        isTarget: true
	};

	
	// 10 position for dAnchor, this should be customized according to number of disasters and topics
	jsPlumb.dAnchor = [[1,0,0,-1],[1,0.1,0,-1],[1,0.2,0,-1],[1,0.3,0,-1],[1,0.4,0,-1],
	                [1,0.6,0,1],[1,0.7,0,1],[1,0.8,0,1],[1,0.9,0,1],[1,1,0,1]];

	// 3 position for eAnchor
	jsPlumb.eAnchor = [[0,0.2,1,0],[0,0.5,0,0],[0,0.8,-1,0]];
	
}
