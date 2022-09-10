/*
 *  AxisLog10.java of project jchart2d, Axis implementation with log  base 10 
 *  display.
 *  Copyright (c) 2007 - 2011 Achim Westermann, created on 20:33:13.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 *
 */
package info.monitorenter.gui.chart.axis;

import info.monitorenter.gui.chart.IAxisLabelFormatter;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicySkipTimeBlocks;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyTransformation;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterSimple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An {@link AAxis} with log base 10 scaled
 * display of values.
 * <p>
 * <h2>Caution</h2>
 * This will not work with negative values (Double.NaN is computed for log of
 * negative values).
 * <p>
 * This will even not work with values < 1.0 as the log transformation turns
 * negative for values < 1.0 and becomes {@link Double#NEGATIVE_INFINITY} with
 * lim -> 0.0 with more and more turns to a 100 % CPU load.
 * <p>
 * 
 * @param <T>
 *          Used to enforce that this instance only accepts
 *          {@link AxisScalePolicyTransformation} and subtypes.
 *          
 * @author Pieter-Jan Busschaert (contributor)
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann</a>
 * 
 * 
 * @version $Revision: 1.15 $
 */
public class AxisLinearSkipTimeBlocks<T extends AxisScalePolicyTransformation> extends AAxisTransformation<T> {

  /** Generated <code>serialVersionUID</code>. */
  private static final long serialVersionUID = -1783172443489534956L;

  @SuppressWarnings("unchecked")
  public AxisLinearSkipTimeBlocks() {
    /*
     * I consider this necessary cast to T as a bug (compare with declaration if T in class header):
     */
    this(new LabelFormatterSimple(), (T)new AxisScalePolicySkipTimeBlocks());
  }

  /**
   * Constructor that uses the given label formatter for formatting labels.
   * <p>
   *
   * @param formatter
   *          needed for formatting labels of this axis. Prefer using simple
   *          implementations like {@link LabelFormatterSimple}, a log axis is
   *          complicated enough to understand.
   *
   * @param scalePolicy
   *          controls the ticks/labels and their distance.
   */
  public AxisLinearSkipTimeBlocks(final IAxisLabelFormatter formatter, final T scalePolicy) {
    super(formatter, scalePolicy);
  }

  Set<TimeBlock> timeSkips = new HashSet<>();

  public void addOmittedTimeBlock(double startAt, double endAt) {
    timeSkips.add(new TimeBlock(startAt, endAt));
  }

  public Set<TimeBlock> getSkipTimeBlocks() {
    return timeSkips;
  }

  public void clearSkipTimeBlocks() {
    timeSkips.clear();
  }

  public double computeOffset(double valueToTransform) {

    double toReturn = 0;

    Set<TimeBlock> timeBlocks = getSkipTimeBlocks();

    Set<TimeBlock> blocksInRange = timeBlocks.stream().filter(timeBlock -> timeBlock.startAt > getMinValue() && timeBlock.endAt < valueToTransform).sorted(Comparator.comparingDouble(o -> o.startAt)).collect(Collectors.toCollection(LinkedHashSet::new));

    TimeBlock inProgress = null;

    for(TimeBlock otb : blocksInRange) {
      if(inProgress == null) {
        inProgress = new TimeBlock(otb.startAt, otb.endAt);
      } else if(otb.startAt < inProgress.endAt && otb.endAt > inProgress.startAt) {
        inProgress.startAt = Math.min(inProgress.startAt, otb.startAt);
        inProgress.endAt = Math.max(inProgress.startAt, otb.endAt);
      } else if(inProgress.startAt < otb.endAt && inProgress.endAt > otb.startAt) {
        inProgress.startAt = Math.min(inProgress.startAt, otb.startAt);
        inProgress.endAt = Math.max(inProgress.startAt, otb.endAt);
      } else {
        toReturn += inProgress.width();
        inProgress = new TimeBlock(otb.startAt, otb.endAt);
      }
    }

    if(inProgress != null)
      toReturn += inProgress.width();

    //round down if we happen to have moved directly into an omitted time block
    for(TimeBlock tB : timeBlocks) {
      if(tB.startAt < toReturn && toReturn < tB.endAt) {
        return tB.startAt;
      }
    }

    return toReturn;

  }

  /**
   * Performs {@link Math#log10(double)} with a check for reaching infinity.
   * <p>
   * 
   * The argument should not be negative, so only normalized values (no chart
   * values but their scaled values or pixel values) should be given here.
   * <p>
   * 
   * If the argument is close to zero, the result of log would be
   * {@link Double#POSITIVE_INFINITY} which is transformed to
   * {@link Double#MAX_VALUE}.
   * <p>
   * 
   * @param in
   *          the value to compute the log base 10 of.
   * 
   * @return log base 10 for the given value.
   */
  @Override
  public double transform(final double in) {

    double
      toTransform = in;

    if (this.m_accessor == null) {
      throw new IllegalStateException("Connect this axis (" + this.getAxisTitle().getTitle()
          + ") to a chart first before doing this operation.");
    }

    return toTransform - computeOffset(in);

  }

  /**
   * @see AAxisTransformation#untransform(double)
   */
  @Override
  public double untransform(final double in) {

    double
      toTransform = in;

    if (this.m_accessor == null) {
      throw new IllegalStateException("Connect this axis (" + this.getAxisTitle().getTitle()
              + ") to a chart first before doing this operation.");
    }

    return toTransform + computeOffset(in);

  }

  public LinkedList<TimeBlock> computeVisibleBlocks() {

    //todo: these need to be computed and sorted only when we're adding new time blocks to skip
    LinkedList<TimeBlock> skipTimeBlocks = getSkipTimeBlocks().stream()
      .sorted(Comparator.comparingDouble(o -> o.startAt))
      .collect(Collectors.toCollection(LinkedList::new));

    double
      minValue = getMinValue(),
      maxValue = getMaxValue();

    LinkedList<TimeBlock> visibleBlocks = new LinkedList<>();

    TimeBlock previousOmission = null;
    for(TimeBlock toSkip : skipTimeBlocks) {

      if(visibleBlocks.isEmpty())
        visibleBlocks.add(new TimeBlock(minValue, toSkip.startAt));
      else
        visibleBlocks.add(new TimeBlock(previousOmission.endAt, toSkip.startAt));

      previousOmission = toSkip;

    }

    if(visibleBlocks.isEmpty())
      visibleBlocks.add(new TimeBlock(minValue, maxValue));
    else
      visibleBlocks.add(new TimeBlock(previousOmission.endAt, maxValue));

    return visibleBlocks;

  }

  @Override
  public double translatePxToValue(int mouseClickLocationInPx) {

    List<TimeBlock> visibleTimeBlocksOnAxis = computeVisibleBlocks();

    double // min & max values visible on the x-axis
        axisMinTime = getMin(),
        axisMaxTime = getMax();

    // remove any timeBlock not intersecting with our visible axis range.
    visibleTimeBlocksOnAxis.removeIf(tB -> tB.endAt < axisMinTime || axisMaxTime < tB.startAt );

    // add up the total units along our axis, mindful of the potentially clipped edges
    double timeUnitsOnAxisX = visibleTimeBlocksOnAxis.stream().mapToDouble(tB -> Math.min(axisMaxTime, tB.endAt) - Math.max(tB.startAt, axisMinTime)).sum();

    // check if we've got empty space (outside explicitly declared time blocks)
    TimeBlock
      firstBlock = visibleTimeBlocksOnAxis.get(0),
      lastBlock = visibleTimeBlocksOnAxis.get(visibleTimeBlocksOnAxis.size()-1);
    double
      emptyTimeSpace_left = Math.max(firstBlock.startAt - axisMinTime, 0d),
      emptyTimeSpace_right = Math.max(axisMaxTime - lastBlock.endAt, 0d);

    timeUnitsOnAxisX += (emptyTimeSpace_left + emptyTimeSpace_right);

    int // figure how much pixel space we have on our axis
      xChartStartInPx = getAccessor().m_chart.getXChartStart(),
      xChartEndInPx   = getAccessor().m_chart.getXChartEnd(),
      pixelsAcrossVisibleAxis = xChartEndInPx - xChartStartInPx;

    double timeUnitsPerPixel = timeUnitsOnAxisX / pixelsAcrossVisibleAxis;

    int emptySpaceLeftInPx = emptyTimeSpace_left == 0d ? 0 : (int)(emptyTimeSpace_left / timeUnitsPerPixel);

    int startOfTimeBlockInPixelSpace = xChartStartInPx + emptySpaceLeftInPx;
    for(TimeBlock tB : visibleTimeBlocksOnAxis) {

      double blockLengthInTime = Math.min(axisMaxTime, tB.endAt) - Math.max(tB.startAt, axisMinTime);
      int blockLengthInPixels = (int) (blockLengthInTime / timeUnitsPerPixel);

      if(     mouseClickLocationInPx >= startOfTimeBlockInPixelSpace
          &&  mouseClickLocationInPx <= startOfTimeBlockInPixelSpace + blockLengthInPixels) {

        //check
        int pixelsIntoBlock = mouseClickLocationInPx - startOfTimeBlockInPixelSpace;

        double
          timeIntoBlock = pixelsIntoBlock * (blockLengthInTime / blockLengthInPixels),
          mouseAtValue = Math.max(tB.startAt, axisMinTime) + timeIntoBlock;

        return mouseAtValue;

      }

      startOfTimeBlockInPixelSpace += blockLengthInPixels;

    }

    return Double.NaN;

  }

}
