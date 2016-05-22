package org.dacapo.derby;

/**
 * 
 * @author Jing Pu
 * 
 * modified 4/7/2015
 *
 */
public class OrderItem4Sort
  implements Comparable<Object>
{
  private final int i;
  private final short q;
  private final short w;
  
  public int getI() {
	  return i;
  }

  public short getQ() {
	  return q;
  }
	
  public short getW() {
	  return w;
  }

  public OrderItem4Sort(int i, short q, short w)
  {
    this.i = i;
    this.q = q;
    this.w = w;
  }
  
  public int compareTo(Object o)
  {
    OrderItem4Sort oo = (OrderItem4Sort)o;
    if (this.w < oo.w) {
      return -1;
    }
    if (this.w > oo.w) {
      return 1;
    }
    if (this.i < oo.i) {
      return -1;
    }
    if (this.i > oo.i) {
      return 1;
    }
    return 0;
  }
}
