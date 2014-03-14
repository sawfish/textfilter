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
import fiu.kdrg.bcin.citysafety.servlet.helper.ModelCache;

/**
 * Servlet implementation class InitComparisonServlet
 */
@WebServlet("/InitComparisonServlet")
public class InitComparisonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InitComparisonServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String cityOne = request.getParameter("cityOne");
		String cityTwo = request.getParameter("cityTwo");
		
		ComparisonBrain brain = ModelCache.query(cityOne, cityTwo);
		List<Disaster> disasters = brain.queryAllDisaster();
		List<Effect> effects = brain.queryAllEffect();
		Map<String,List<Edge>> edges = brain.queryAllEdges();
		
		Gson gson = new Gson();
		JsonObject jsonObj = new JsonObject();
		jsonObj.addProperty("Result", "OK");
		jsonObj.add("disasters", gson.toJsonTree(disasters));
		jsonObj.add("effects", gson.toJsonTree(effects));
		jsonObj.add("edges", gson.toJsonTree(edges));
		
//		System.out.println(jsonObj.toString());
		
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		
		response.getWriter().write(jsonObj.toString());
		
	}

}
