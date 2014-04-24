/*
 * Drag and Drop
 */

//global variable
var testVar;
var dataMap = {};

$(function() {

	jsPlumbInit();
	
	$(".cityPairMenu li").click(function(){
		var strArr = $(this).text().split("VS");
		var cityOne = strArr[0].trim().replace(" ","+");
		var cityTwo = strArr[1].trim().replace(" ","+");
		$(".infoDisplay").text($(this).text());
		initConnections(cityOne,cityTwo);
	});
	initConnections("miami", "chicago");

});


function progress(percent, element) {
    var progressBarWidth = percent * element.width() / 100;
    // With labels:
    element.find('div').animate({
        width: progressBarWidth
    }, 100).html(percent + "%&nbsp;");

    // Without labels:
    //element.find('div').animate({ width: progressBarWidth }, 500);
}

$(document).ready(function () {
    $('.progressBar').each(function () {
        //alert('Hello');
        var bar = $(this);
//        var max = $(this).attr('id');
//        max = max.substring(3);

//        progress(max, bar);
    });
});



function initConnections(cityOne, cityTwo) {

	//refresh main part of the web page
	refreshMainFramePage();
	
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


function refreshMainFramePage(){
	
	$("#graph_panel").empty();
	$(".summary").empty();
	
}


//<div class="progressBar progressBarCityOne" id="max22">
//<div></div>
//</div>
//<div class="progressBar progressBarCityTwo" id="max92">
//<div></div>
//</div>
function createBar(w1,w2){

	var dBar = $("<div class='progressBar progressBarCityOne' style='width:" + w1 + "'><div></div></div>" + 
			"<div class='progressBar progressBarCityTwo' style='width:" + w2 + "'><div></div></div>");
	return dBar;
}


function createBipartiteGraph(data){
	
	var disasters = data.disasters;
	var effects = data.effects;
	var edges = data.edges;
	
	for(var i = 0; i < disasters.length; i++){
		var disaster = disasters[i];
		var dNode = $("<div class='node d_node' id='disaster_" + 
						disaster.id +"' style='top:" + (i*250 + 100) +"px;left:50px'>" +
						"<div class='d_bar'></div><div class='d_content'>"+ disaster.text +"</div></div>");
		$("#graph_panel").append(dNode);
	}
	
	for(var i = 0; i < effects.length; i++){
		var effect = effects[i];
		var eNode = $("<div class='node e_node' id='effect_" 
				+ effect.id +"' style='top:" + i*5 +"px;left:500px'></div>");
		$("#graph_panel").append(eNode);
		//generate word cloud
		$("#effect_" + effect.id).jQCloud(effect.words);
	}
//	$("#jqcloud").jQCloud(effects[0].words);
	
	
	var cityOne = data.cityOne;
	var cityTwo = data.cityTwo;
	var cityOneEdges = edges[cityOne];
	var cityTwoEdges = edges[cityTwo];
	
	var dmap = {};
	
	for(var i = 0; i < cityOneEdges.length; i++){
		
		var edge = cityOneEdges[i];
		console.log(edge);
		var tmp = edge.weight + 1;
		var lineW = Math.log(tmp)/Math.log(2) + 1;
		console.log(tmp);
		console.log(lineW);
		jsPlumb.cityOneEndpoint.connectorStyle.lineWidth = lineW.toFixed(0) * 2;
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
				location: 0.7,
				label: edge.weight.toFixed(2),
				cssClass:"wLabel " + cityOne + "_wLabel"
			}]]
		});
		
	}
	
	
	for(var i = 0; i < cityTwoEdges.length; i++){
		
		var edge = cityTwoEdges[i];
		console.log(edge);
		var tmp = edge.weight + 1;
		var lineW = Math.log(tmp)/Math.log(2) + 1;
		console.log(tmp);
		console.log(lineW);
		jsPlumb.cityTwoEndpoint.connectorStyle.lineWidth = lineW.toFixed(0) * 2;
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
				location: 0.9,
				label: edge.weight.toFixed(2),
				cssClass:"wLabel " + cityTwo + "_wLabel"
			}]]
		});
		
	}
	
//	jsPlumb.draggable($(".d_node"));
//	jsPlumb.draggable($(".e_node"));

	visualizeSummaries(cityOne,cityTwo,"-1","-1");
	//add event listner to node
	$(".d_node").click(function(){
		jsPlumb.select({source:$(this).attr("id")}).each(function(con){
			if(con.isHover()){
				con.setHover(false);
			}else{
				con.setHover(true);
				var dID = con.sourceId;
				visualizeSummaries(cityOne,cityTwo,dID.substring(dID.lastIndexOf("_")+1),"-1");
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
		$(".cityOneSummary").text(data.cityOneSummary);
		$(".cityTwoSummary").text(data.cityTwoSummary);
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
	            radius: 4
	        }],
	        paintStyle: {
	            fillStyle: colorCityOne
	        },
	        connectorStyle: {
	            strokeStyle: colorCityOne,
	            lineWidth: 3
	        },
	        connectorHoverStyle: {
//	        	lineWidth: 4,
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
			endpoint: ["Rectangle", {
	            radius: 1
	        }],
	        anchor: "BottomLeft",
	        paintStyle: {
	            fillStyle: colorCityTwo,
	            opacity: 0.5
	        },
	        connectorStyle: {
	            strokeStyle: colorCityTwo,
	            lineWidth: 3
	        },
	        connectorHoverStyle: {
//	        	lineWidth: 4,
	        	strokeStyle:"#0d27e7",
	        	outlineWidth: 2,
	        	outlineColor: "white"
	        },
//	        connector: ["StateMachine",{
//	        	curviness: 20
//	        }],
	        connector:"Straight",
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
