package info.monitorenter.gui.chart.axis.scalepolicy;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisScalePolicy;
import info.monitorenter.gui.chart.LabeledValue;
import info.monitorenter.util.Range;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AxisScalePolicyPrice implements IAxisScalePolicy {

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

    int pixelWidth = chart.getYAxisHeight(),
        pixelsConsumed = 0;

    Set<LabeledValue>
      tenCents        =  new LinkedHashSet<>(),
      fiftyCents      =  new LinkedHashSet<>(),
      dollars         =  new LinkedHashSet<>(),
      fiveDollars     =  new LinkedHashSet<>(),
      tenDollars      =  new LinkedHashSet<>(),
      fiftyDollars    =  new LinkedHashSet<>(),
      hundredDollars  =  new LinkedHashSet<>();


    double iterator = 1;

    if(max - min < 10) {
      iterator = 0.10;
    }

    int tenCentLength = 0,
        fiftyCentLength = 0,
        dollarsLength = 0,
        fivesLength = 0,
        tensLength = 0,
        fiftiesLength = 0,
        hundredsLength = 0;

    int iter = 0, iterLimit = 9999; //failsafe
    //convert min to int as a cheap round-down to the nearest dollar
    for (double value = (int)min; value <= max; value=nearestCent(value+iterator)) {

      if(iter++ > iterLimit)
        break;

      //sometimes value rounds down below min, in that case, skip it.
      if(value < min)
        continue;

      final LabeledValue label = new LabeledValue();
      label.setValue((value - min) / (visibleRange));
      label.setMajorTick(true);

      label.setLabel(axis.getFormatter().format(value));

      int fontHeightInPx = fm.getHeight();

      if(value == 0) {

        hundredDollars.add(label);
        hundredsLength+=fontHeightInPx;

      } else {

        if (isDivisibleBy(value, 50)) {
          fiftyDollars.add(label);
          fiftiesLength+=fontHeightInPx;
        } else if (isDivisibleBy(value, 10)) {
          tenDollars.add(label);
          tensLength+=fontHeightInPx;
        } else if (isDivisibleBy(value, 5)) {
          fiveDollars.add(label);
          fivesLength+=fontHeightInPx;
        } else if (isDivisibleBy(value, 1)){
          dollars.add(label);
          dollarsLength+=fontHeightInPx;
        } else if (isDivisibleBy(value, 0.50)) {
          fiftyCents.add(label);
          fiftyCentLength+=fontHeightInPx;
        } else {
          tenCents.add(label);
          tenCentLength+=fontHeightInPx;
        }

      }

    }

    int pixelHeightLimit = (int) ((double)pixelWidth * .6);

    if(pixelsConsumed + hundredsLength < pixelHeightLimit) {
      toReturn.addAll(hundredDollars);
      pixelsConsumed += hundredsLength;

      if(pixelsConsumed + fiftiesLength < pixelHeightLimit) {
        toReturn.addAll(fiftyDollars);
        pixelsConsumed += fiftiesLength;

        if(pixelsConsumed + tensLength < pixelHeightLimit) {
          toReturn.addAll(tenDollars);
          pixelsConsumed += tensLength;

          if(pixelsConsumed + fivesLength < pixelHeightLimit) {
            toReturn.addAll(fiveDollars);
            pixelsConsumed += fivesLength;

            if(pixelsConsumed + dollarsLength < pixelHeightLimit) {
              toReturn.addAll(dollars);
              pixelsConsumed += dollarsLength;


              if(pixelsConsumed + fiftyCentLength < pixelHeightLimit) {
                toReturn.addAll(fiftyCents);
                pixelsConsumed += fiftyCentLength;

                if(pixelsConsumed + tenCentLength < pixelHeightLimit)
                  toReturn.addAll(tenCents);

              }


            }

          }

        }

      }

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
