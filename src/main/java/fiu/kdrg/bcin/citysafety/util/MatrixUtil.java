package fiu.kdrg.bcin.citysafety.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jblas.DoubleMatrix;

public class MatrixUtil {

  
  public static DoubleMatrix transformMapToMatrix(Map<Integer,Map<Integer,Double>> mapMatrix){
    
    List<Integer> sortedKeys = new ArrayList<Integer>(mapMatrix.keySet());
    Collections.sort(sortedKeys);
    double[][] dm = new double[sortedKeys.get(sortedKeys.size() - 1) + 1][];
    
    for(Map.Entry<Integer, Map<Integer,Double>> entryOne : mapMatrix.entrySet()){
      
      int key = entryOne.getKey();
      Map<Integer,Double> value = entryOne.getValue();
      sortedKeys = new ArrayList<Integer>(value.keySet());
      Collections.sort(sortedKeys);
      dm[key] = new double[sortedKeys.get(sortedKeys.size() - 1) + 1];
      
      for(Map.Entry<Integer, Double> entryTwo : value.entrySet()){
        dm[key][entryTwo.getKey()] = entryTwo.getValue();
      }
      
    }
    
    return (new DoubleMatrix(dm));
  }
  
  
}
