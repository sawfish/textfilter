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
	
	for(var i = 0; i < disasters.length; i++){
		var disaster = disasters[i];
		var dNode = $("<div class='d_node' id='disaster_" 
				+ disaster.id +"' style='top:" + (i*80 + 300) +"px;left:50px'></div>");
		$("#graph_panel").append(dNode);
	}
	
	for(var i = 0; i < effects.length; i++){
		var effect = effects[i];
		var eNode = $("<div class='e_node' id='effect_" 
				+ effect.id +"' style='top:" + i*80 +"px;left:600px'></div>");
		$("#graph_panel").append(eNode);
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

	var dynamicAnchors = [ [ 0.2, 0, 0, -1 ], [ 1, 0.2, 1, 0 ],
			[ 0.8, 1, 0, 1 ], [ 0, 0.8, -1, 0 ] ];

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

}
