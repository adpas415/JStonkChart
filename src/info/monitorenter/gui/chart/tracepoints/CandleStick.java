package info.monitorenter.gui.chart.tracepoints;

import java.awt.*;

public class CandleStick extends TracePoint2D {

  /**
   * Constructor with every argument needed.
   * <p>
   * See <a target="_blank"
   * href="http://en.wikipedia.org/wiki/Candlestick_chart"
   * >http://en.wikipedia.org/wiki/Candlestick_chart</a>
   * <p>
   * 
   * @param xValue
   *          the x coordinate.
   * 
   * @param open
   *          the start y value.
   * 
   * @param close
   *          the end y value.
   * 
   * @param high
   *          the high y value.
   * 
   * @param low
   *          the low y value.
   */

  public CandleStick(final double xValue, final double open, final double high, final double low, final double close, final Color color) {
      super(xValue, open);
      this.m_end = close;
      this.m_high = high;
      this.m_low = low;
      this.m_color = color;
      this.m_color_faded = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);
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

  Color m_color_faded;
  public Color getColorFaded() {
      return m_color_faded;
  }
  
  Color m_color;
  public Color getColor() {
    return this.m_color;
  }
  /**
   * Returns the low y value.
   * <p>
   * 
   * @return the low y value.
   */
  public double getLow() {
    return this.m_low;
  }

  @Override
  public double getYMax() {
      return getHigh();
  }
  
  @Override
  public double getYMin() {
      return getLow();
  }
  
  /**
   * Returns the end y value.
   * <p>
   * 
   * @return the end y value.
   */
  public double getEnd() {
    return this.m_end;
  }

  /**
   * Returns the start y value.
   * <p>
   * 
   * Note: this is the reused inherited {@link #getY()}.
   * <p>
   * 
   * @return the start y value.
   */
  public double getStart() {
    return this.getY();
  }

  /** The low y value. **/
  private double m_low;

  /** The end y value. **/
  private double m_end;
 
}
