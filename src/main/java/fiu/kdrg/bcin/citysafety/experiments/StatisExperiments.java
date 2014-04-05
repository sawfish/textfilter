package fiu.kdrg.bcin.citysafety.experiments;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Edge;

public class StatisExperiments {

	private ComparisonBrain brain;
	
	public StatisExperiments(String cityOne, String cityTwo) {
		brain = new ComparisonBrain(cityOne, cityTwo);
	}
	
	
	public double[] disasterDist(String city){
		
		int n = brain.getNumDisasters();
		double[] dist = new double[n];
		double sum = 0;
		double[] cnts = new double[n];
		
		for(int i = 0; i < n; n++){
			cnts[i] = brain.queryInstances(city, i, -1).size();
			sum += cnts[i];
		}
		for(int i = 0; i < n; n++){
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
		for(int i = 0; i < n; n++){
			for(Edge e : brain.queryEdgesByEffect(city, i)){
				cnts[i] += e.getWeight();
			}
			sum += cnts[i];
		}
		for(int i = 0; i < n; n ++){
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
		
		
		
	}
	
	
	public static void main(String[] args) {
		
		String cityOne = "miami";
		String cityTwo = "miami";
		StatisExperiments se = new StatisExperiments(cityOne, cityTwo);
		se.runExperiment();
		
	}
	
	
	
}
