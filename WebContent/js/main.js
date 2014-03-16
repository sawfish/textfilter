/*
 * Drag and Drop
 */

//global variable
var testVar;
var dataMap = {};

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
		dataMap[cityOne+"_"+cityTwo] = data;
		createBipartiteGraph(data);
	}, "json");
	
}



function createBipartiteGraph(data){
	
	var disasters = data.disasters;
	var effects = data.effects;
	var edges = data.edges;
	
	for(var i = 0; i < disasters.length; i++){
		var disaster = disasters[i];
		var dNode = $("<div class='node d_node' id='disaster_" + 
						disaster.id +"' style='top:" + (i*250 + 200) +"px;left:50px'>" +
						"<div class='d_content'>"+ disaster.text +"</div></div>");
		$("#graph_panel").append(dNode);
	}
	
	for(var i = 0; i < effects.length; i++){
		var effect = effects[i];
		var eNode = $("<div class='node e_node' id='effect_" 
				+ effect.id +"' style='top:" + i*5 +"px;left:400px'></div>");
		$("#graph_panel").append(eNode);
		//generate word cloud
		$("#effect_" + effect.id).jQCloud(effect.words);
	}
//	$("#jqcloud").jQCloud(effects[0].words);
	
	
	var cityOne = data.cityOne;
	var cityTwo = data.cityTwo;
	var cityOneEdges = edges[cityOne];
	var cityTwoEdges = edges[cityTwo];
	
	for(var i = 0; i < cityOneEdges.length; i++){
		
		var edge = cityOneEdges[i];
		var d = jsPlumb.addEndpoint("disaster_" + edge.source, {anchor:jsPlumb.dAnchor}, jsPlumb.cityOneEndpoint);
		var t = jsPlumb.addEndpoint("effect_" + edge.target, {anchor:jsPlumb.eAnchor}, jsPlumb.cityOneEndpoint);
		jsPlumb.connect({
			source : d,
			target : t,
			overlays : [ [ "Arrow", {
				width : 15,
				length : 15,
				location : 0.8
			} ],
			["Label",{
				location: 0.8,
				label: edge.weight.toFixed(2),
				cssClass:"wLabel " + cityOne + "_wLabel"
			}]]
		});
		
	}
	
	
	for(var i = 0; i < cityTwoEdges.length; i++){
		
		var edge = cityTwoEdges[i];
		var d = jsPlumb.addEndpoint("disaster_" + edge.source, {anchor:jsPlumb.dAnchor}, jsPlumb.cityTwoEndpoint);
		var t = jsPlumb.addEndpoint("effect_" + edge.target, {anchor:jsPlumb.eAnchor}, jsPlumb.cityTwoEndpoint);
		jsPlumb.connect({
			source : d,
			target : t,
			overlays : [ [ "Arrow", {
				width : 10,
				length : 15,
				location : 1
			} ],
			["Label",{
				location: 1,
				label: edge.weight.toFixed(2),
				cssClass:"wLabel " + cityTwo + "_wLabel"
			}]]
		});
		
	}
	
//	jsPlumb.draggable($(".d_node"));
//	jsPlumb.draggable($(".e_node"));

	
	//add event listner to node
	$(".d_node").click(function(){
		jsPlumb.select({source:$(this).attr("id")}).each(function(con){
			if(con.isHover()){
				con.setHover(false);
			}else{
				con.setHover(true);
				var dID = con.sourceId;
				visualizeSummaries(cityOne,cityTwo,dID.substring(dID.lastIndexOf("_")+1),"");
			}
		});
	});
	
	
	$(".e_node").click(function(){
		var id = $(this).attr("id");
		jsPlumb.select().each(function(con){
			if(con.isHover()){
				if(con.targetId != id){
					con.setHover(false);
				}else{
					var dID = con.sourceId;
					var eID = con.targetId;
					visualizeSummaries(cityOne,cityTwo,
							dID.substring(dID.lastIndexOf("_")+1),eID.substring(eID.lastIndexOf("_")+1));
				}
			}
		});
	});
	
}




function visualizeSummaries(cityOne,cityTwo,dID,eID){
	$.get("GetSummaryServlet",{
		cityOne: cityOne,
		cityTwo: cityTwo,
		dID:dID,
		eID:eID
	},function(data){
		$("#summary_panel .cityOne").text(data.cityOneSummary);
		$("#summary_panel .cityTwo").text(data.cityTwoSummary);
	},"json");
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
	
	
	
	var colorCityOne = "#2FCC22";
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
	        connectorHoverStyle: {
	        	lineWidth: 4,
	        	strokeStyle:"#11F0BA",
	        	outlineWidth: 2,
	        	outlineColor: "white"
	        },
	        connector: "Straight",
	        maxConnections: -1,
	        isSource: true,
	        scope: "cityOne",
	        isTarget: true
	};
	
	var colorCityTwo = "#6498f3";
	jsPlumb.cityTwoEndpoint = {
			endpoint: ["Dot", {
	            radius: 3
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
	        connectorHoverStyle: {
	        	lineWidth: 4,
	        	strokeStyle:"#0d27e7",
	        	outlineWidth: 2,
	        	outlineColor: "white"
	        },
	        connector: "Straight",
	        maxConnections: -1,
	        isSource: true,
	        scope: 'cityTwo',
	        isTarget: true
	};
	
	
	// 10 position for dAnchor, this should be customized according to number of disasters and topics
	jsPlumb.dAnchor = [[1,0,0,-1],[1,0.1,0,-1],[1,0.2,0,-1],[1,0.3,0,-1],[1,0.4,0,-1],
	                [1,0.6,0,1],[1,0.7,0,1],[1,0.8,0,1],[1,0.9,0,1],[1,1,0,1]];

	// 3 position for eAnchor
	jsPlumb.eAnchor = [[0,0.2,1,0],[0,0.5,0,0],[0,0.8,-1,0]];
	
}
