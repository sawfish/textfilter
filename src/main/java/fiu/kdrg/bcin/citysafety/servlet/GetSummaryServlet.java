package fiu.kdrg.bcin.citysafety.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fiu.kdrg.bcin.citysafety.core.ComparisonBrain;
import fiu.kdrg.bcin.citysafety.core.Disaster;
import fiu.kdrg.bcin.citysafety.core.Edge;
import fiu.kdrg.bcin.citysafety.core.Effect;
import fiu.kdrg.bcin.citysafety.core.Instance;
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
    
    
    ComparisonBrain brain = ModelCache.query(cityOne, cityTwo);
    List<Instance> cityOneInsts = null;
    List<Instance> cityTwoInsts = null;
    
    if(dID.isEmpty() && eID.isEmpty()){
      cityOneInsts = brain.queryInstances(cityOne);
      cityTwoInsts = brain.queryInstances(cityTwo);
    }else if(dID.isEmpty() || eID.isEmpty()){
      
      //this has been implemented yet
      if(dID.isEmpty()){
        
      }
      
      if(eID.isEmpty()){
        cityOneInsts = brain.queryInstancesByDisaster(cityOne, Integer.parseInt(dID));
        cityTwoInsts = brain.queryInstancesByDisaster(cityTwo, Integer.parseInt(dID));
      }
      
    }else{
      
      cityOneInsts = brain.queryInstances(cityOne, Integer.parseInt(dID), Integer.parseInt(eID));
      cityTwoInsts = brain.queryInstances(cityTwo, Integer.parseInt(dID), Integer.parseInt(eID));
      
    }
    
    List<String> summaries = (new Summarizer()).summarize(cityOneInsts, cityTwoInsts);
    
//    Gson gson = new Gson();
    JsonObject jsonObj = new JsonObject();
    jsonObj.addProperty("Result", "OK");
//    jsonObj.add("disasters", gson.toJsonTree(disasters));
    jsonObj.addProperty("cityOne", cityOne);
    jsonObj.addProperty("cityTwo", cityTwo);
    jsonObj.addProperty("cityOneSummary", summaries.get(0));
    jsonObj.addProperty("cityTwoSummary", summaries.get(1));
    
//  System.out.println(jsonObj.toString());
    
    response.setContentType("application/json;charset=UTF-8");
    response.setHeader("pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    
    response.getWriter().write(jsonObj.toString());
    
    
  }

}
