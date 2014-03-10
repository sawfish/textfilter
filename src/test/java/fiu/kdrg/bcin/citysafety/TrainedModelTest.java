package fiu.kdrg.bcin.citysafety;

import static org.junit.Assert.*;

import org.jblas.DoubleMatrix;
import org.junit.Before;
import org.junit.Test;

import fiu.kdrg.bcin.citysafety.core.TrainedModel;
import fiu.kdrg.bcin.citysafety.util.MatrixUtil;

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
		assertTrue(model.loadCityInstances(cityOne).get(0).getSid() == 0);
		assertTrue(model.loadCityInstances(cityOne).get(1).getSid() == 1);
		assertTrue(model.loadCityInstances(cityTwo).get(0).getSid() == 0);
		assertTrue(model.loadCityInstances(cityTwo).get(1).getSid() == 1);
		DoubleMatrix dm = MatrixUtil.transformMapToMatrix(model.getCityDocsWeightedTopics(cityOne));
		System.out.println(dm.columns);
		System.out.println(dm.rows);
		
	}

}
