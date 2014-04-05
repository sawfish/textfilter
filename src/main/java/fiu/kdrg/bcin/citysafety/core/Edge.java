package fiu.kdrg.bcin.citysafety.core;

import java.io.Serializable;

public class Edge implements Serializable{

  private static final long serialVersionUID = 1L;

  private double weight;
  private int source;
  private int target;
  private String city;


  public Edge(String city, int source, int target, double weight){
	  this.city = city;
	  this.source = source;
	  this.target = target;
	  this.weight = weight;
  }
  
  
  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public int getSource() {
    return source;
  }

  public void setSource(int source) {
    this.source = source;
  }

  public int getTarget() {
    return target;
  }

  public void setTarget(int target) {
    this.target = target;
  }
  
  
  @Override
  public int hashCode() {
          return this.genRealID().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
          if (this == obj)
                  return true;
          if (obj == null)
                  return false;
          if (getClass() != obj.getClass())
                  return false;
          Edge other = (Edge) obj;
          if (!(this.genRealID()).equals(other.genRealID())) {
                  return false;
          }
          return true;
  }
  
  
  public String genRealID(){
    return source + "_" + city + "_"+target;
  }
  
  
  public void addWeight(double a){
	  weight += a;
  }
  
  
  @Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("city: %s, source %d, target %d, weight %f.", city, source, target, weight);
	}
  

}
