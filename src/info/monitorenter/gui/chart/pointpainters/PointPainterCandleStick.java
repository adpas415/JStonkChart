package info.monitorenter.gui.chart.pointpainters;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.AxisLinearSkipTimeBlocks;
import info.monitorenter.gui.chart.axis.TimeBlock;
import info.monitorenter.gui.chart.tracepoints.CandleStick;
import info.monitorenter.gui.chart.traces.ATrace2D;

import java.awt.*;
import java.util.LinkedList;
import java.util.Set;

/**
 * A special point painter that will only be useable to render instances of
 * {@link CandleStick}.
 * <p>
 * 
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 */
public class PointPainterCandleStick extends APointPainter<PointPainterCandleStick> {

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -6708238540093878572L;

  /** The width of the candlestick. */

  /**
   * Constructor taking the width.
   * <p>
   *
   **/
  public PointPainterCandleStick() {
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

    } else timeBlocksOnAxis.add(new TimeBlock(axis.getMinValue(), axis.getMaxValue()));

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
        if(original.getClass() == CandleStick.class) {

            CandleStick candleStick = (CandleStick) original;

            double
                yAxis_maxVisibleValue = chart.getAxisY().getMax(),
                yAxis_minVisibleValue = chart.getAxisY().getMin(),
                yAxis_visibleRange = yAxis_maxVisibleValue - yAxis_minVisibleValue,
                startYNormalized  =  (candleStick.getStart() - yAxis_minVisibleValue) / yAxis_visibleRange,
                endYNormalized    =  (candleStick.getEnd()   - yAxis_minVisibleValue) / yAxis_visibleRange,
                highYNormalized   =  (candleStick.getHigh()  - yAxis_minVisibleValue) / yAxis_visibleRange,
                lowYNormalized    =  (candleStick.getLow()   - yAxis_minVisibleValue) / yAxis_visibleRange,
            /*
             * Transform to px
             */
                yChartStartPx = chart.getYChartStart(),
                yChartEndPx = chart.getYChartEnd(),
                rangeYPx = yChartStartPx - yChartEndPx,

                startYPx  =  yChartStartPx - (int) Math.round(startYNormalized * rangeYPx),
                endYPx    =  yChartStartPx - (int) Math.round(endYNormalized   * rangeYPx),
                highYPx   =  yChartStartPx - (int) Math.round(highYNormalized  * rangeYPx),
                lowYPx    =  yChartStartPx - (int) Math.round(lowYNormalized   * rangeYPx);

            /*
             * Compute Bar Width
             */

          double
              xValue = candleStick.getX(),
              xAxis_maxVisibleValue = chart.getAxisX().getMax(),
              xAxis_minVisibleValue = chart.getAxisX().getMin(),
              xAxis_visibleRange = xAxis_maxVisibleValue - xAxis_minVisibleValue,
              xNormalized = (xValue - xAxis_minVisibleValue) /  xAxis_visibleRange,
              xChartStartPx = chart.getXChartStart(),
              xChartEndPx = chart.getXChartEnd(),
              rangeXPx = xChartEndPx - xChartStartPx,
              xAsDouble = xChartStartPx + (int) Math.round(xNormalized * rangeXPx);

          int x = (int)xAsDouble;
          //Finally, paint the bar:

          Color candleStickColor = candleStick.getColor();

          // color the wick transparent only-if we're painting a single-pixel-wide bar.
          if(barWidth == 0) {

            //color the wick
            g.setColor(candleStick.getColorFaded());

            //draw the wick
            g.drawLine(x, (int)highYPx, x, (int)lowYPx);

            //color the body
            g.setColor(candleStickColor);

            //draw the body
            g.drawLine(x, (int)startYPx, x, (int)endYPx);

          } else {

            //color the candlestick
            g.setColor(candleStickColor);

            //draw the wick
            g.drawLine(x, (int)highYPx, x, (int)lowYPx);

            int finalBarWidth = barWidth + 1 + barWidth;

            int yAxisWestBoundary = trace2D.getRenderer().getAxisY().getPixelXRight();

            int barLeftPixel = Math.max(x-barWidth, yAxisWestBoundary);

            int offset = Math.abs((x-barWidth) - barLeftPixel);

            //draw the body
            g.fillRect(
              barLeftPixel,
              (int)Math.min(startYPx, endYPx),
              finalBarWidth - offset,
              1 + (int) Math.abs(startYPx - endYPx)
            );

          }

        }
      }
    }
  }

}
