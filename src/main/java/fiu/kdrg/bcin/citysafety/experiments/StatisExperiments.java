package fiu.kdrg.bcin.citysafety.experiments;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Edge;
import fiu.kdrg.bcin.citysafety.core.Effect;
import fiu.kdrg.bcin.citysafety.util.MathUtil;

public class StatisExperiments {

        private Logger logger = LoggerFactory.getLogger(StatisExperiments.class);
	private ComparisonBrain brain;
	
	public StatisExperiments(String cityOne, String cityTwo) {
		brain = new ComparisonBrain(cityOne, cityTwo);
	}
	
	
	public double[] disasterDist(String city){
		
		int n = brain.getNumDisasters();
		double[] dist = new double[n];
		double sum = 0;
		double[] cnts = new double[n];
		
		for(int i = 0; i < n; i++){
			cnts[i] = brain.queryInstances(city, i, -1).size();
			sum += cnts[i];
		}
		for(int i = 0; i < n; i++){
			dist[i] = cnts[i] / sum;
		}
		
		return dist;
	}
	
	
	
	public double[] effectDist(String city){
		
		int n = brain.getNumTopics();
		double[] dist = new double[n];
		double sum = 0;
		double[] cnts = new double[n];
		
		brain.setTypeOfEdges(ComparisonBrain.UNNORMALIZED);
		for(int i = 0; i < n; i ++){
			for(Edge e : brain.queryEdgesByEffect(city, i)){
				cnts[i] += e.getWeight();
			}
			sum += cnts[i];
		}
		for(int i = 0; i < n; i ++){
			dist[i] = cnts[i] / sum;
		}
		
		return dist;
	}
	
	
	// we can simply get his from edge value
	public double[][] distOfEffectGivenD(String city){
		
		int n = brain.getNumDisasters();
		int m = brain.getNumTopics();
		double[][] dist = new double[n][m];
		
		for(int d = 0; d < n; d ++){
			for(Edge e : brain.queryEdges(city, d)){
				dist[d][e.getTarget()] = e.getWeight();
			}
		}
		
		return dist;
	}
	
	
	
	public double[][] distOfDisasterGivenE(String city){
		
		//Here effect first
		int n = brain.getNumTopics();
		int m = brain.getNumDisasters();
		double[][] dist = new double[n][m];
		
		for(int ef = 0; ef < n; ef ++){
			for(Edge e : brain.queryEdgesByEffect(city, ef)){
				dist[ef][e.getSource()] = e.getWeight();
			}
		}
		
		return dist;
	}
	
	
	
	/**
	 * this function will show us all statistic and textual result we want.
	 * get maximal P_{c_1}(d),P_{c_2}(d),P_{c_1}(e),P_{c_2}(e)
	 * get closest P(E|d) and P(D|e) using Kullback–Leibler divergence 
	 */
	public void runExperiment(){
	
	  String cityOne = brain.getCityOne();
	  String cityTwo = brain.getCityTwo();
	  String[] disasters = brain.getDisaster();
	  brain.seteSize(10);
	  List<Effect> effects = brain.queryAllEffect();
	  
	  double[] dDistOne = disasterDist(cityOne);
	  double[] eDistOne = effectDist(cityOne);
	  double[] dDistTwo = disasterDist(cityTwo);
	  double[] eDistTwo = disasterDist(cityTwo);
	  
	  logger.info("table 1");
	  System.out.println(String.format("most likely disaster is %s for city %s",disasters[MathUtil.maxIndex(dDistOne)],cityOne));
	  System.out.println(String.format("most likely disaster is %s for city %s",disasters[MathUtil.maxIndex(dDistTwo)],cityTwo));
	  System.out.println(String.format("most likely effect is %s for city %s", effects.get(MathUtil.maxIndex(eDistOne)), cityOne));
	  System.out.println(String.format("most likely effect is %s for city %s", effects.get(MathUtil.maxIndex(eDistTwo)), cityTwo));
	  
	  
	  double[][] eGivenDOne = distOfEffectGivenD(cityOne);
	  double[][] eGivenDTwo = distOfEffectGivenD(cityTwo);
	  double[][] dGivenEOne = distOfDisasterGivenE(cityOne);
	  double[][] dGivenETwo = distOfDisasterGivenE(cityTwo);
	  
	  double[] klValueOfD = klDivergences(eGivenDOne, eGivenDTwo);
	  double[] klValueOfE = klDivergences(dGivenEOne, dGivenETwo);
	  
	  
	  System.out.println(String.format("most similar disaster is %s", disasters[MathUtil.minIndex(klValueOfD)]));
	  System.out.println(String.format("most similar effect is %s", effects.get(MathUtil.minIndex(klValueOfE))));
	  
	  
	  logger.info("table 2");
	  for(int i = 0; i < disasters.length; i++ ){
	    
	    System.out.println(String.format("most likely effect is %s for city %s on disaster %s", 
	                  effects.get(MathUtil.maxIndex(eGivenDOne[i])), cityOne, disasters[i]));
	    System.out.println(String.format("most likely effect is %s for city %s on disaster %s", 
                          effects.get(MathUtil.maxIndex(eGivenDTwo[i])), cityTwo, disasters[i]));
	    
	  }
	  
	}
	
	
	
	/**
	 * calculate a list of klDivergence values for a list probability distribution
	 * watch out the dimension of one and two should be exactly same.
	 */
	private double[] klDivergences(double[][] one, double[][] two){
	  
	  double[] klValue = new double[one.length]; 
	  for(int i = 0; i < one.length; i++){
	    klValue[i] = MathUtil.klDivergence(one[i], two[i]);
	  }
	  
	  return klValue;
	}
	
	
	
	public static void main(String[] args) {
		
		int pairNum = 6;
		String[] cityOnes = {"miami","miami","miami","chicago","chicago","los+angeles"};
		String[] cityTwos = {"chicago","los+angeles","philadelphia","los+angeles","philadelphia","philadelphia"};
		
		for(int i = 0; i < pairNum; i++){
			
			String cityOne = cityOnes[i];
			String cityTwo = cityTwos[i];
			StatisExperiments se = new StatisExperiments(cityOne, cityTwo);
			se.runExperiment();
			
		}
		
		
	}
	
	
	
}
