package info.monitorenter.gui.chart.tracepoints;

import java.awt.*;

public class VolumeBar extends TracePoint2D {

  public VolumeBar(final double xValue, final double heightY, final Color color) {
      this(xValue, 0, heightY);
      this.m_color = color;
  }
    
  public VolumeBar(final double xValue, final double startY, final double endY) {
    super(xValue, startY);
    this.m_color = Color.white;
    this.m_high = endY;
  }

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -135311007801611830L;

  /** The high y value. **/
  private double m_high;

  /**
   * Returns the high y value.
   * <p>
   * 
   * @return the high y value.
   */
  public double getHigh() {
    return this.m_high;
  }
  
  Color m_color;
  public Color getColor() {
    return this.m_color;
  }

  @Override
  public double getYMax() {
      return getHigh();
  }
  
  @Override
  public double getYMin() {
      return Math.min(y, getHigh());
  }
  
  
}
