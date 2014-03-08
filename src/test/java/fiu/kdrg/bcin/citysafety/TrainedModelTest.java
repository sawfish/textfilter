package fiu.kdrg.bcin.citysafety;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fiu.kdrg.bcin.citysafety.core.TrainedModel;

public class TrainedModelTest {

	
	TrainedModel model;
	String cityOne;
	String cityTwo;
	
	@Before
	public void setUp() throws Exception {
		
		cityOne = "miami";
		cityTwo = "chicago";
		model = new TrainedModel(cityOne, cityTwo);
	}

	@Test
	public void test() {
		
		model.loadCityInstances(cityOne);
		model.loadCityInstances(cityTwo);
		model.getCityDocsWeightedTopics(cityOne);
		model.getCityDocsWeightedTopics(cityTwo);
		assertTrue(model.getNumOfcityOneInst() == 772);
		assertTrue(model.getNumOfcityTwoInst() == 618);
		assertTrue(model.getTopicWeightedWords().size() == 10);
	}

}
