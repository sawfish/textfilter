package fiu.kdrg.bcin.citysafety.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fiu.kdrg.bcin.citysafety.core.ComparativeSummaryPair;
import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Disaster;
import fiu.kdrg.bcin.citysafety.core.Edge;
import fiu.kdrg.bcin.citysafety.core.Effect;
import fiu.kdrg.bcin.citysafety.core.Instance;
import fiu.kdrg.bcin.citysafety.db.DBConnection;
import fiu.kdrg.bcin.citysafety.servlet.helper.ModelCache;
import fiu.kdrg.bcin.citysafety.summary.Summarizer;

/**
 * Servlet implementation class GetSummaryServlet
 */
@WebServlet("/GetSummaryServlet")
public class GetSummaryServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public GetSummaryServlet() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    doPost(request, response);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    String cityOne = request.getParameter("cityOne");
    String cityTwo = request.getParameter("cityTwo");
    String dID = request.getParameter("dID");
    String eID = request.getParameter("eID");
    
    
    ComparativeSummaryPair csp = getSummary(cityOne, cityTwo, 
    		Integer.parseInt(dID), Integer.parseInt(eID));
    
//    Gson gson = new Gson();
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("Result", "OK");
//    jsonObj.add("disasters", gson.toJsonTree(disasters));
    jsonObj.addProperty("cityOne", cityOne);
    jsonObj.addProperty("cityTwo", cityTwo);
    jsonObj.addProperty("cityOneSummary", csp.getSummary1());
    jsonObj.addProperty("cityTwoSummary", csp.getSummary2());
    
//  System.out.println(jsonObj.toString());
    
    response.setContentType("application/json;charset=UTF-8");
    response.setHeader("pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    
    response.getWriter().write(jsonObj.toString());
    
    
  }
  
  
  
  
  private ComparativeSummaryPair getSummary(String cityOne,String cityTwo,int dID,int eID){
	  
	 String sql = "select * from summary where city1 = ? and city2 = ? and cid = ? and eid = ?";
	 
	 Connection conn = null;
	 PreparedStatement pstm = null;
	 
	 try {
		 conn = DBConnection.getConnection();
		 pstm = conn.prepareStatement(sql);
		 
		 pstm.setString(1, cityOne);
		 pstm.setString(2, cityTwo);
		 pstm.setInt(3, dID);
		 pstm.setInt(4, eID);
		 
		 ResultSet rs = pstm.executeQuery();
		 ComparativeSummaryPair csp = new ComparativeSummaryPair();
		 
		 if(rs.next()){
			csp.setCityOne(cityOne);
			csp.setCityTwo(cityTwo);
			csp.setCid(dID);
			csp.setEid(eID);
			csp.setSummary1(rs.getString("summary1"));
			csp.setSummary2(rs.getString("summary2"));
		 }
		 
		 return csp;
	} catch (Exception e) {
		// TODO: handle exception
	}
	 
	 return null;
	  
  }
  
  
  
  

}
