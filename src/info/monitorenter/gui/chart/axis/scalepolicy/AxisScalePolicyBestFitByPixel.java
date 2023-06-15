package info.monitorenter.gui.chart.axis.scalepolicy;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.LabeledValue;
import info.monitorenter.util.Range;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AxisScalePolicyBestFitByPixel implements IAxisScalePolicy {

  private static boolean isDivisibleBy(double isThis, double divisibleByThis) {
    return isThis % divisibleByThis == 0;
  }

  /**
   * Fast rounding function. To the nearest one-cent.
   * @param toBeRounded
   * @return Given value rounded to the nearest penny.
   */
  public static double nearestCent(double toBeRounded) {
    return (double)((int)( (toBeRounded + (toBeRounded < 0 ? -0.005 : 0.005)) * 100))/100;
  }

  public static double roundDown(double number, double roundingValue) {
    return Math.floor(number / roundingValue) * roundingValue;
  }

  public static double findLastValueBeforeExceeding(double target, double unit) {

    while (unit * 10 <= target) unit *= 10;

    return unit;

  }

  public static double getNearestRoundingFactor(double number) {
    double[] roundingFactors = { 0.05, 0.1, 0.25 };

    double smallestUnit = Double.MIN_VALUE;
    for(double roundingFactor : roundingFactors) {
      double candidate = findLastValueBeforeExceeding(number, roundingFactor);
      smallestUnit = Math.max(smallestUnit, candidate);
    }

    return smallestUnit;
  }

  /**
   * Returns the labels for this axis.
   * <p>
   * 
   * 
   * @return the labels for the axis.
   */
  protected List<LabeledValue> getLabels(final IAxis<?> axis) {

    final List<LabeledValue> toReturn = new LinkedList<>();

    final Range axisRange = axis.getRange();

    double
        min = axisRange.getMin(),
        max = axisRange.getMax(),
        visibleRange = max - min;

    Chart2D chart = axis.getAccessor().getChart();

    Font f = chart.getFont();
    FontMetrics fm = chart.getFontMetrics(f);

    int axisHeightInPx = chart.getYAxisHeight();
    int fontHeightInPx = fm.getHeight();
    int pixelHeightLimit = (int) ((double)axisHeightInPx * .5);

    int maxLabels = pixelHeightLimit / fontHeightInPx;

    double minDistanceBetweenLabels = visibleRange / maxLabels;

    double iterator = getNearestRoundingFactor(minDistanceBetweenLabels);

    //convert min to int as a cheap round-down to the nearest dollar
    for (double value = roundDown(min, iterator); value <= max; value+=iterator) {

      //sometimes value rounds down below min, in that case, skip it.
      if(value < min)
        continue;

      final LabeledValue label = new LabeledValue();
      label.setValue((value - min) / (visibleRange));
      label.setMajorTick(true);
      label.setLabel(axis.getFormatter().format(value));

      toReturn.add(label);

    }

    return toReturn;

  }

  /**
   * @see IAxisScalePolicy#getScaleValues(Graphics2D, IAxis)
   */
  public List<LabeledValue> getScaleValues(final Graphics2D g2d, final IAxis<?> axis) {
    return this.getLabels(axis);
  }

  /**
   * @see IAxisScalePolicy#initPaintIteration(IAxis)
   */
  public void initPaintIteration(IAxis<?> axis) {
    // nop
  }

}
