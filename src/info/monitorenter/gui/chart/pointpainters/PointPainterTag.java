package info.monitorenter.gui.chart.pointpainters;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.tracepoints.Tag;

import java.awt.*;

/**
 * Renders points in form of a disc with configurable diameter.
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 * 
 * 
 * @version $Revision: 1.22 $
 */
public class PointPainterTag extends APointPainter<PointPainterTag> {

  /** Generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -6317473632026920774L;

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMaxX(ITracePoint2D)
   */
  @Override
  public double calculateMaxX(final ITracePoint2D point) {      
    if(point.getClass().equals(Tag.class) && point.getListener() != null && !((Tag)point).getAdjust() && point.getListener().getRenderer().getGraphics() != null) {
        
        String longestLine = "";
        for(String line : ((Tag)point).getText().split("\n"))
            if(line.length() > longestLine.length())
                longestLine = line;
        IAxis xAxis = point.getListener().getRenderer().getAxisX();
                    
        return xAxis.translatePxToValue( xAxis.translateValueToPx(point.getX()) + ((Tag)point).getTextX() + point.getListener().getRenderer().getGraphics().getFontMetrics().stringWidth(longestLine));
        
    }
    return point.getX();
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMaxY(ITracePoint2D)
   */
  @Override
  public double calculateMaxY(final ITracePoint2D point) {
    /*
    if(point.getClass().equals(Tag.class) && ((Tag)point).getTag() != null) {
        IAxis yAxis = point.getListener().getAxisY();
        return yAxis.translatePxToValue( yAxis.translateValueToPx(point.getY()) + ((BufferedImage) ((Tag)point).getTag()).getHeight());
    }*/
    return point.getY();
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMinX(ITracePoint2D)
   */
  @Override
  public double calculateMinX(final ITracePoint2D point) {
      return point.getX();
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMinY(ITracePoint2D)
   */
  public double calculateMinY(final ITracePoint2D point) {
      
    return point.getY();
  }

    @Override
    public boolean isAdditionalSpaceRequiredX() {
        return false;
    }

    @Override
    public boolean isAdditionalSpaceRequiredY() {
        return false;
    }

    /**
   * @see APointPainter#equals(Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
      return this.getClass() == obj.getClass();
  }

  /**
   * Returns the diameter of the discs to paint in pixel.
   * <p>
   * 
   * @return the diameter of the discs to paint in pixel.
   */

  /**
   * @see APointPainter#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    return result;
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#isPixelTransformationNeededX()
   */
  @Override
  public boolean isPixelTransformationNeededX() {
   return false;
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#isPixelTransformationNeededX()
   */
  @Override
  public boolean isPixelTransformationNeededY() {
   return false;
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#paintPoint(int, int, int,
   *      int, Graphics, ITracePoint2D)
   */
    @Override
  public void paintPoint(final int absoluteX, final int absoluteY, final int nextX, final int nextY, final Graphics g, final ITracePoint2D original) {
    if(original.getClass().equals(Tag.class)) {

        Tag tag = (Tag) original;
        String str = tag.getText();
        Color c = tag.getColor();
        Image m_Tag = tag.getTag();

        if(m_Tag != null) {
            g.drawImage(m_Tag, nextX + tag.getTagX(), nextY + tag.getTagY(), null);
        }      
        if(str != null && !str.isEmpty()) {
            g.setColor(c);

            int x = nextX + (tag.getAdjust() ? tag.getTextX() - g.getFontMetrics().stringWidth(!str.contains("\n") ? str : str.substring(0, str.indexOf("\n"))) : tag.getTextX());
            int y = nextY + tag.getTextY() - g.getFontMetrics().getHeight();

            for (String line : str.split("\n")) {
                g.drawString(line, x, y += g.getFontMetrics().getHeight());
            }
        }
    
        
    }
  }

  /**
   * Sets the diameter of the discs to paint in pixel.
   * <p>
   * 
   * @param discSize
   *          the diameter of the discs to paint in pixel.
   */

}
