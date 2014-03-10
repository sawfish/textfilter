package fiu.kdrg.bcin.citysafety.core;

public class Edge {

  int weight;
  int source;
  int target;

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
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
  
  
  private String genRealID(){
    return source+"_"+target;
  }
  

}
