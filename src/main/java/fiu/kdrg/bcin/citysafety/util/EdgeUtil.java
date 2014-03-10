package fiu.kdrg.bcin.citysafety.util;

import java.util.List;

import fiu.kdrg.bcin.citysafety.core.Edge;

public class EdgeUtil {

	
	public static void printEdges(List<Edge> edges){
		for(Edge edge : edges){
			System.out.println(edge.toString());
		}
	}
	
}
