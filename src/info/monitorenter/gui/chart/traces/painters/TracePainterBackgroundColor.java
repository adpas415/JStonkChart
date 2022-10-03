package info.monitorenter.gui.chart.traces.painters;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.tracepoints.BackgroundColor;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class TracePainterBackgroundColor extends ATracePainter {

  /** Generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -3310431930065989648L;

  
  /** The list of x coordinates collected in one paint iteration. */

  private Map<Integer, BackgroundColor> m_bgPoints;
  
  /**
   * Defcon.
   * <p>
   */
  public TracePainterBackgroundColor() {    
    
  }
  
    @Override //todo: this might not ever be called for this class! purge if so.
    public double calculateMaxX(ITracePoint2D point) {
        if(point instanceof BackgroundColor && m_bgPoints != null) {
            //get the NEXT point, so we know where this BG ends

            boolean returnNext = false;
            for(BackgroundColor p : m_bgPoints.values()) {

                if(returnNext)
                    return p.getX();

                if(point == p)
                    returnNext = true;

            }

            //the given bg point isn't "finished", so it stretches as far as the data on the axis.
            return point.getListener().getRenderer().getAxisX().getMaxValue();

        }
        return point.getX();
    }

    @Override
    public double calculateMinX(ITracePoint2D point) {
        return point.getX();
    }

    @Override
    public double calculateMaxY(ITracePoint2D point) {
        return point.getY();
    }

    @Override
    public double calculateMinY(ITracePoint2D point) {
        return point.getY();
    }

    @Override
    public boolean isPixelTransformationNeededX() {
        return false;
    }

    @Override
    public boolean isPixelTransformationNeededY() {
        return false;
    }
    
  /**
   * @see ATracePainter#paintPoint(int,
   *      int, int, int, Graphics,
   *      ITracePoint2D)
   */
  @Override
  public void paintPoint(final int absoluteX, final int absoluteY, final int nextX, final int nextY, final Graphics g, final ITracePoint2D original) {
    if(original instanceof BackgroundColor) {
        this.m_bgPoints.put(nextX, (BackgroundColor) original);
    }
  }
  
  /**
   * @see info.monitorenter.gui.chart.ITracePainter#startPaintIteration(Graphics)
   */
  @Override
  public void startPaintIteration(final Graphics g2d) {
    super.startPaintIteration(g2d);
    if (this.m_bgPoints == null) {
      this.m_bgPoints = new LinkedHashMap<>();
    } else {
      this.m_bgPoints.clear();
    }
  }
  
  /**
   * @see info.monitorenter.gui.chart.ITracePainter#endPaintIteration(Graphics)
   */
  @Override
  public void endPaintIteration(final Graphics g2d) {
    super.endPaintIteration(g2d);
    if (g2d != null) {
        this.doDrawOperation(g2d);
    }
  }

    @Override
    public boolean isAdditionalSpaceRequiredX() {
        return false;
    }

    @Override
    public boolean isAdditionalSpaceRequiredY() {
        return false;
    }


    protected void doDrawOperation(final Graphics g) {

        LinkedList<Map.Entry<Integer, BackgroundColor>> colorsToPaint = new LinkedList<>(m_bgPoints.entrySet());

      while(!colorsToPaint.isEmpty()) {

          Map.Entry<Integer, BackgroundColor> xPxAndColor = colorsToPaint.pop();

          int absoluteX = xPxAndColor.getKey();
          BackgroundColor bgColor = xPxAndColor.getValue();

          ITrace2D trace = bgColor.getListener();
          if (trace == null) {
              throw new IllegalStateException("Given point is not attached to a trace yet. Cannot paint!");
          } else {
              Chart2D chart = trace.getRenderer();
              if (chart == null) {
                  throw new IllegalStateException("Given point is in a trace that is not attached to a chart yet. Cannot paint!");
              } else {

                  if(bgColor.isVisibleOnAxisX()) {

                      boolean isCurrentBackground = colorsToPaint.isEmpty();

                      int endAtPixelX = isCurrentBackground ? chart.getXChartEnd() : colorsToPaint.peek().getKey();
                      int startAtPixelX = Math.max(absoluteX, chart.getXChartStart());
                      int widthToPaint = isCurrentBackground ? chart.getXChartEnd() - startAtPixelX : endAtPixelX - startAtPixelX;

                      Color color = bgColor.getColor();

                      g.setColor(color);

                      g.fillRect(startAtPixelX, chart.getYChartEnd(), widthToPaint, chart.getYAxisHeight());

                  }

              }
          }
      }
      
  }
  
  
  
  /**
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final TracePainterBackgroundColor other = (TracePainterBackgroundColor) obj;
    if (this.m_bgPoints == null) {
        return other.m_bgPoints == null;
    } else return this.m_bgPoints.equals(other.m_bgPoints);
  }

  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.m_bgPoints == null) ? 0 : this.m_bgPoints.hashCode());
    return result;
  }


  @Override  
  public void discontinue(final Graphics g2d) {
      //intentionally left blank
  }
  
}
