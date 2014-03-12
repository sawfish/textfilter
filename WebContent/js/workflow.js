;
$(function() {
	statuses = [ "conf", "toRun", "fail", "succ", 'proc' ];

	jsPlumbInitialize();
	/*
	 * var actId; var actions = new Array(); for ( var i = 0; i < 0; i++) {
	 * actId = "1111111" + i; createAction1(actId, statuses[i % 5], i);
	 * actions.push(actId); }
	 * 
	 * var s; var t; for ( var j = 0; j < actions.length - 1; j++) { s =
	 * jsPlumb.addEndpoint(actions[j]); t = jsPlumb.addEndpoint(actions[j + 1]);
	 * jsPlumb.connect({ source : s, target : t, overlays : [ [ "Arrow", { width :
	 * 10, length : 15, location : 1 } ] ] }); } jsPlumb.draggable($(".state"));
	 */

});


function createAction(jobName, jobGroup) {
	$.get('ListAction?JobName=' + jobName + "&JobGroup=" + jobGroup, function(
			data) {
		createWorkflow(data);
	}, "json");

}

function createActionLog(schedSeq) {
	var url = 'ListActionLog?SchedSeq=' + schedSeq;
	$.get(url, function(data) {
		createWorkflowLog(data);
	}, "json");
}

function createWorkflow(data) {
	$('#workflowPanel').empty();

	if (data.Result != 'OK')
		return;
	var idd;
	var jobGroup;
	var jobName;
	var actionSeq;
	var command;
	var parameter;
	var actDesc;
	var action;
	var temp = "0px;";
	var actions = new Array();
	var pactions = new Array();
	for ( var i = 0; i < data.TotalRecordCount; i++) {
		jobGroup = data.Records[i].JobGroup;
		jobName = data.Records[i].JobName;
		var x = jobGroup.split(" ");
		var y = jobName.split(" ");
		idd = "act_" + x.join("_") + "_" + y.join("_");

		actionSeq = data.Records[i].ActionSeq;
		command = data.Records[i].Command;
		parameter = data.Records[i].Parameter;
		actDesc = data.Records[i].ActDesc;
		parentActSeqs = data.Records[i].ParentActSeqs;
		controlFlowType = data.Records[i].ControlFlowType;
		var actionName = command.split("/");
		actionName = actionName[actionName.length - 1].split(" ")[0];
		action = $("<div class='state'><b>" + actionSeq + "</b>.<br/>"
				+ actionName + "</div>");
		if (i != 0 && i % 2 == 0)
			temp = "100px;";
		else if (i != 0 && i % 2 != 0)
			temp = "-100px;";
		var style = "left:" + temp + " top:" + i * 20 + "px;";
		action.attr({
			'status' : 'conf',
			// 'id' : "act_"+jobGroup+"_"+jobName+"_" + actionSeq,
			'id' : idd + actionSeq,
			'style' : style
		});
		$('#workflowPanel').append(action);
		var popup = createJobPopup(jobName, jobGroup, actionSeq, command,
				parameter, actDesc, parentActSeqs, controlFlowType);
		action.append(popup);
		// actions.push("act_" + jobGroup + "_" + jobName + "_" + actionSeq);
		actions.push(idd + actionSeq);
		pactions.push(parentActSeqs);
		action.hover(function() {
			$(".popup", this).css({
				top : 50,
				left : 50
			}).show();
		}, function() {
			$(".popup", this).hide();
		});
	}

	var s;
	var t;
	for ( var j = 0; j < actions.length; j++) {
		var parentActs = pactions[j].split(",");
		for ( var k = 0; k < parentActs.length; k++) {
			if (parentActs[k] == "-1")
				continue;
			s = jsPlumb.addEndpoint(idd + parentActs[k], {}, {
				isSource : true,
				isTarget : true,
				maxConnections : -1
			});
			t = jsPlumb.addEndpoint(actions[j], {}, {
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
		}
	}
	jsPlumb.draggable($(".state"));
}

function createJobPopup(jobName, jobGrp, actionSeq, cmd, param, desc,
		parentActSeqs, controlFlowType) {
	var tab = $("<table class='gridtable'></table>");
	var trActSeq = $("<tr><td>Action ID" + "</td><td>" + actionSeq
			+ "</td></tr>");
	tab.append(trActSeq);
	var trPActSeq = $("<tr><td>parents" + "</td><td>" + parentActSeqs
			+ "</td></tr>");
	tab.append(trPActSeq);
	var trCFT = $("<tr><td>Node Type" + "</td><td>" + controlFlowType
			+ "</td></tr>");
	tab.append(trCFT);
	var trJN = $("<tr><td>Job Name" + "</td><td>" + jobName + "</td></tr>");
	tab.append(trJN);
	var trJG = $("<tr><td>Job Group" + "</td><td>" + jobGrp + "</td></tr>");
	tab.append(trJG);
	var trCMD = $("<tr><td>Command" + "</td><td>" + cmd + "</td></tr>");
	tab.append(trCMD);
	var trParam = $("<tr><td>Parameter" + "</td><td>" + param + "</td></tr>");
	tab.append(trParam);
	var trDesc = $("<tr><td>Description" + "</td><td>" + desc + "</td></tr>");
	tab.append(trDesc);

	var popup = $("<div class='popup'>detail</div>");

	popup.attr({
		"id" : "popup_" + actionSeq
	});

	popup.append(tab);
	return popup;
}

function createJobLogPopup(prgName, prgParam, actionSeq, actStatus, host,
		start, end, info, logId, pLogId) {
	var tab = $("<table class='gridtable'></table>");
	var trLogId = $("<tr><td>LogId" + "</td><td>" + logId + "</td></tr>");
	tab.append(trLogId);
	var trPLogId = $("<tr><td>PLogId" + "</td><td>" + pLogId + "</td></tr>");
	tab.append(trPLogId);
	var trActSeq = $("<tr><td>Action ID" + "</td><td>" + actionSeq
			+ "</td></tr>");
	tab.append(trActSeq);
	var trStatus = $("<tr><td>Status" + "</td><td>" + actStatus + "</td></tr>");
	tab.append(trStatus);
	var trJN = $("<tr><td>Program" + "</td><td>" + prgName + "</td></tr>");
	tab.append(trJN);
	var trParam = $("<tr><td>Parameter" + "</td><td>" + prgParam + "</td></tr>");
	tab.append(trParam);
	var trHost = $("<tr><td>Host" + "</td><td>" + host + "</td></tr>");
	tab.append(trHost);
	var trStart = $("<tr><td>Start" + "</td><td>" + start + "</td></tr>");
	tab.append(trStart);

	var trEnd = $("<tr><td>End" + "</td><td>" + end + "</td></tr>");
	tab.append(trEnd);

	var trDesc = $("<tr><td>Output" + "</td><td>" + info + "</td></tr>");
	tab.append(trDesc);

	var popup = $("<div class='popup'>detail</div>");

	popup.attr({
		"id" : "popup_" + actionSeq
	});

	popup.append(tab);
	return popup;
}

function createWorkflowLog(data) {
	$('#workflowPanel').empty();

	if (data.Result != 'OK')
		return;
	var actionSeq;
	var prgName;
	var prgParam;
	var host;
	var start;
	var end;
	var actStatus;
	var info;
	var temp = "0px;";
	var actions = new Array();
	var pactions = new Array();
	for ( var i = 0; i < data.TotalRecordCount; i++) {
		logId = data.Records[i].LogId;
		pLogId = data.Records[i].PLogId;
		actionSeq = data.Records[i].ActionSeq;
		prgName = data.Records[i].PrgName;
		prgParam = data.Records[i].PrgParam;
		host = data.Records[i].Host;
		start = data.Records[i].Start;
		end = data.Records[i].End;
		actStatus = data.Records[i].ActStatus;
		info = data.Records[i].Info;
		var actionName = prgName.split("/");
		actionName = actionName[actionName.length - 1].split(" ")[0];
		action = $("<div class='state'><b>" + actionSeq + "</b>.<br/>"
				+ actionName + "</div>");
		if (i != 0 && i % 2 == 0)
			temp = "100px;";
		else if (i != 0 && i % 2 != 0)
			temp = "-100px;";
		var style = "left:" + temp + " top:" + i * 20 + "px;";
		var status;
		switch (actStatus) {
		case -1:
			status = 'toRun';
			break;
		case 0:
			status = "succ";
			break;
		case 1:
			status = "proc";
			break;
		case 2:
			status = "fail";
			break;
		default:
			status = "conf";
		}
		action.attr({
			'status' : status,
			'id' : "act_" + logId,
			'style' : style
		});
		$('#workflowPanel').append(action);
		actions.push("act_" + logId);
		pactions.push(pLogId);
		var popup = createJobLogPopup(prgName, prgParam, actionSeq, actStatus,
				host, start, end, info, logId, pLogId);
		action.append(popup);
		action.hover(function() {
			$(".popup", this).css({
				top : 50,
				left : 50
			}).show();
		}, function() {
			$(".popup", this).hide();
		});
	}

	var s;
	var t;
	for ( var j = 0; j < actions.length; j++) {
		var parentActs = pactions[j].split(",");
		for ( var k = 0; k < parentActs.length; k++) {
			if (parentActs[k] == "-1")
				continue;
			s = jsPlumb.addEndpoint('act_' + parentActs[k], {}, {
				isSource : true,
				isTarget : true,
				maxConnections : -1
			});
			t = jsPlumb.addEndpoint(actions[j], {}, {
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
		}
	}
	jsPlumb.draggable($(".state"));
}

function createAction1(actionId, status, index) {
	var action = $("<div class='state'>" + actionId + "</div>");
	var temp = "0px;";
	if (index != 0 && index % 2 == 0)
		temp = "100px;";
	else if (index != 0 && index % 2 != 0)
		temp = "-100px;";
	var style = "left:" + temp + "top:" + index * 20 + "px;";
	action.attr({
		'status' : status,
		'id' : actionId,
		'style' : style
	});
	$('#workflowPanel').append(action);
}

function jsPlumbInitialize() {
	var dynamicAnchors = [ [ 0.2, 0, 0, -1 ], [ 1, 0.2, 1, 0 ],
			[ 0.8, 1, 0, 1 ], [ 0, 0.8, -1, 0 ] ];

	jsPlumb.Defaults.Container = $("#workflowPanel");
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