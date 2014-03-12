/*
 * Drag and Drop
 */

$(function(){
	
	jsPlumb.Defaults.Container = $("body");
	
	var firstInstance = jsPlumb.getInstance();
	firstInstance.importDefaults({
		Connector : [ "Bezier", { curviness: 150 } ],
		Anchors : [ "TopCenter", "BottomCenter" ]
	});
	
	var e1 = jsPlumb.addEndpoint("node0");
	var t1 = jsPlumb.addEndpoint("node1");
	
	firstInstance.connect({
		source:e1,
		target:t1
	});
	
});
