package fiu.kdrg.bcin.citysafety.summary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fiu.kdrg.bcin.citysafety.core.ComparativeSummaryPair;
import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Instance;
import fiu.kdrg.bcin.citysafety.db.DBConnection;
import fiu.kdrg.bcin.citysafety.util.Constants;

public class CityPairSummarizer extends Summarizer {

	public static String SUMMARY_SQL = "insert into summary (city1,city2,cid,eid,summary1,summary2) values (?,?,?,?,?,?)";
	
	
	private Logger logger = LoggerFactory.getLogger(CityPairSummarizer.class);
	
	private String cityOne;
	private String cityTwo;
	private ComparisonBrain model;
	
	public CityPairSummarizer(String cityOne, String cityTwo) {
		this.cityOne = cityOne;
		this.cityTwo = cityTwo;
		this.model = new ComparisonBrain(cityOne, cityTwo);
	}
	
	
	/**
	 * generate summarization according to causal node and effect node selected.
	 * if no selection, its corresponding id is -1. Thus 4 * 11 = 44 pairs of summarization
	 * will be generated for each pair of cities. It will be stored in database for
	 * latter use.
	 */
	public void emitSummariesToDB(){
		
		List<ComparativeSummaryPair> summaries = new ArrayList<ComparativeSummaryPair>();
		List<String> oneS = new ArrayList<String>();
		for(int d = -1; d < model.getNumDisasters(); d ++){
			
			for(int e = -1; e < model.getNumTopics(); e ++){
				
				List<Instance> cityOneInsts = model.queryInstances(cityOne, d, e);
				List<Instance> cityTwoInsts = model.queryInstances(cityTwo, d, e);
				oneS = this.summarize(cityOneInsts, cityTwoInsts);
				
				ComparativeSummaryPair csp = new ComparativeSummaryPair();
				csp.setCityOne(cityOne);
				csp.setCityTwo(cityTwo);
				csp.setCid(d);
				csp.setEid(e);
				csp.setSummary1(oneS.get(0));
				csp.setSummary2(oneS.get(1));
				summaries.add(csp);
				
				logger.info(String.format("summarization done for disaster %d and effect %d", d,e));
				
				
			}
			
		}
		
		System.out.println(summaries.size());
		populateSummaries(summaries);
		
	}
	
	
	
	private void populateSummaries(List<ComparativeSummaryPair> summaries){
		
		Connection conn = null;
		
		try {
			
			conn = DBConnection.getConnection();
			conn.setAutoCommit(false);
			PreparedStatement pstm = conn.prepareStatement(SUMMARY_SQL);
			for(ComparativeSummaryPair csp: summaries){
				
				pstm.setString(1, csp.getCityOne());
				pstm.setString(2, csp.getCityTwo());
				pstm.setInt(3, csp.getCid());
				pstm.setInt(4, csp.getEid());
				pstm.setString(5, csp.getSummary1());
				pstm.setString(6, csp.getSummary2());
				
				pstm.addBatch();
				
			}
			
			pstm.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			conn.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		
		
		int pairNum = Constants.cityOnes.length;
		for(int i = 0; i < pairNum; i++){
			
			String cityOne = Constants.cityOnes[i];
			String cityTwo = Constants.cityTwos[i];
			
			CityPairSummarizer summarizer = new CityPairSummarizer(cityOne, cityTwo);
			summarizer.emitSummariesToDB();
			
		}
		
	}
	
	
	
}
