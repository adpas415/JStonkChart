package info.monitorenter.gui.chart.pointpainters;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.AxisLinearSkipTimeBlocks;
import info.monitorenter.gui.chart.axis.TimeBlock;
import info.monitorenter.gui.chart.tracepoints.VolumeBar;
import info.monitorenter.gui.chart.traces.ATrace2D;

import java.awt.*;
import java.util.LinkedList;
import java.util.Set;

public class PointPainterSimpleStick extends APointPainter<PointPainterSimpleStick> {

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -6708238540093878572L;

  /** The width of the candlestick. */

  /**
   * Constructor taking the width.
   * <p>
   *
   **/
  public PointPainterSimpleStick() {
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMaxX(ITracePoint2D)
   */
  @Override
  public double calculateMaxX(final ITracePoint2D point) {
   return point.getX();
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMaxY(ITracePoint2D)
   */
  @Override
  public double calculateMaxY(final ITracePoint2D point) {
    return point.getY(); 
  }

  /**
   * 
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMinX(ITracePoint2D)
   */
  @Override
  public double calculateMinX(final ITracePoint2D point) {
    return point.getX();
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#calculateMinY(ITracePoint2D)
   */
  @Override
  public double calculateMinY(final ITracePoint2D point) {
    return point.getY();    
  }

  /**
   * @see info.monitorenter.gui.chart.IPointPainter#endPaintIteration(Graphics)
   */
  @Override
  public void endPaintIteration(Graphics g2d) {
    //nop
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
   * @see Object#equals(Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return getClass() == obj.getClass();
  }
  /**
   * @see Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
  public void paintPoint(int absoluteX, int absoluteY, int nextX, int nextY, Graphics g, ITracePoint2D original) {
    /*
     * absoluteX corresponds to getX(), absoluteY to getStart(). All other
     * coords have to be transformed to px.
     */
    double x = nextX;
    /*
     * Get the corresponding chart for coordinate translation:
     */
    ITrace2D trace = original.getListener();
    if (trace == null) {
      throw new IllegalStateException("Given point is not attached to a trace yet. Cannot paint!");
    } else {
      Chart2D chart = trace.getRenderer();
      if (chart == null) {
        throw new IllegalStateException("Given point is in a trace that is not attached to a chart yet. Cannot paint!");
      } else {
        /*
         * Normalize y
         */
        if(original.getClass() == VolumeBar.class) {
            VolumeBar volumeBar = (VolumeBar) original;
            double traceMaxY = chart.getAxisY().getMax();
            double traceMinY = chart.getAxisY().getMin();
            double scalerY = traceMaxY - traceMinY;
            double highYNormalized = (volumeBar.getHigh() - traceMinY) / scalerY;
            double lowYNormalized = (volumeBar.y - traceMinY) / scalerY;
            /*
             * Transform to px
             */
            double yChartStartPx = chart.getYChartStart();
            double yChartEndPx = chart.getYChartEnd();
            double rangeYPx = yChartStartPx - yChartEndPx;
            double highYPx = yChartStartPx - (int) Math.round(highYNormalized * rangeYPx);
            double lowYPx = yChartStartPx - (int) Math.round(lowYNormalized * rangeYPx);


            Color candleStickColor = volumeBar.getColor();

            //color the candlestick
            g.setColor(candleStickColor);

            // color the wick transparent only-if we're painting a single-pixel-wide bar.
            if(barWidth == 0) {

                //draw the wick
                g.drawLine((int)x, (int)highYPx, (int)x, (int)lowYPx);

            } else {

                int finalX = (int)x;

                //draw the wick
                g.drawLine(finalX, (int)highYPx, finalX, (int)lowYPx);

                int finalBarWidth = barWidth + 1 + barWidth;

                //draw the body
                g.fillRect(
                    finalX-barWidth,
                    (int)Math.min(highYPx, lowYPx),
                    finalBarWidth,
                    1 + (int) Math.abs(highYPx - lowYPx)
                );

            }

        }
      }
    }
  }

    int barWidth = 0;

    /**
     * @see info.monitorenter.gui.chart.IPointPainter#startPaintIteration(Graphics)
     */
    @Override
    public void startPaintIteration(Graphics g2d) {

        Chart2D chart = trace2D.getRenderer();

        IAxis axis = chart.getAxisX();

        LinkedList<TimeBlock> timeBlocksOnAxis = new LinkedList<>();

        double
            min = axis.getMin(),
            max = axis.getMax(),
            axisRange = max-min;

        if(axis instanceof AxisLinearSkipTimeBlocks) {

            timeBlocksOnAxis = ((AxisLinearSkipTimeBlocks) axis).computeVisibleBlocks();

            Set<TimeBlock> skipTimeBlocks = ((AxisLinearSkipTimeBlocks) axis).getSkipTimeBlocks();

            double axisWidthReduction = skipTimeBlocks.stream().mapToDouble(tB -> min < tB.startAt && tB.endAt < max ? tB.width() : 0).sum();

            axisRange -= axisWidthReduction;

        } else timeBlocksOnAxis.add(new TimeBlock(min, max));

        //remove any timeBlock not intersecting with our visible axis range.
        timeBlocksOnAxis.removeIf(tB -> tB.endAt < min || max < tB.startAt );

        //add up the total units along our axis, mindful of the edges
        double
                timeUnitsVisible_xAxis = timeBlocksOnAxis.stream().mapToDouble(tB -> Math.min(max, tB.endAt) - Math.max(tB.startAt, min)).sum(),
                minDistanceBetweenPoints_inTime = ((ATrace2D)trace2D).x_minDistanceBetweenPoints,
                xRangePx = chart.getXAxisWidth(),
                //convert minimum distance between points from time to pixel space
                minDistanceBetweenPoints_inPixels =  ((minDistanceBetweenPoints_inTime / Math.max(timeUnitsVisible_xAxis, axisRange)) * xRangePx);

        //trim some fat
        minDistanceBetweenPoints_inPixels -= 3;

        //half the minimum distance
        minDistanceBetweenPoints_inPixels /= 2d;

        //round down to the nearest even number
        int roundedBarWidth = (int) ((minDistanceBetweenPoints_inPixels/2d)*2d);

        barWidth =  Math.max(roundedBarWidth, 0);

    }

}
