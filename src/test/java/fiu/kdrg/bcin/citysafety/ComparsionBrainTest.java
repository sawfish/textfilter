package fiu.kdrg.bcin.citysafety;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Edge;
import fiu.kdrg.bcin.citysafety.util.EdgeUtil;

public class ComparsionBrainTest {

	ComparisonBrain brain;
	String cityOne;
	String cityTwo;
	
	@Before
	public void setUp() throws Exception {
		
		cityOne = "miami";
		cityTwo = "chicago";
		brain = new ComparisonBrain(cityOne, cityTwo);
		
	}

	@Test
	public void test() {
		
		List<Edge> edges = brain.queryEdges(cityOne);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityTwo);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityOne, 0);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
		
		edges = brain.queryEdges(cityOne, 0, 0);
		System.out.println(edges.size());
		EdgeUtil.printEdges(edges);
	}

}
