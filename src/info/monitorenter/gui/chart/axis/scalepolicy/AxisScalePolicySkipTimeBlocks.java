package info.monitorenter.gui.chart.axis.scalepolicy;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.LabeledValue;
import info.monitorenter.gui.chart.axis.AxisLinearSkipTimeBlocks;
import info.monitorenter.gui.chart.axis.TimeBlock;
import info.monitorenter.util.Range;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;

public class AxisScalePolicySkipTimeBlocks extends AxisScalePolicyTransformation {

  public static final long YEAR = 31536000000L;
  public static final long MONTH = 2592000000L;
  public static final long DAY = 86400000L;
  public static final long HOUR = 3600000L;
  public static final long MINUTE = 60000L;
  public static final long SECOND = 1000L;

  SimpleDateFormat
    yearFormatter      = new SimpleDateFormat("yyyy"),
    monthFormatter     = new SimpleDateFormat("MM/dd"),
    hourMinuteFormat   = new SimpleDateFormat("HH:mm"),
    secondsFormat      = new SimpleDateFormat("HH:mm:ss"),
    millisecondsFormat = new SimpleDateFormat("HH:mm:ss.ssss");


  LabeledValue bakeLabel(String text, double value, boolean leftHand) {

    final LabeledValue label = new LabeledValue();

    label.setValue(value);
    label.setMajorTick(true);
    label.setLabel(text);
    label.setLeft(leftHand);

    return label;

  }

  private static boolean isDivisibleBy(double isThis, double divisibleByThis) {
    return isThis % divisibleByThis == 0;
  }

  public static double findLastValueBeforeExceeding(double target, double unit) {

    while (unit * 10 <= target) unit *= 10;

    return unit;

  }

  public static double getNearestRoundingFactor(double number) {
    double[] roundingFactors = { 100, 50, 25, 10, 5, 1, 0.5, 0.25, 0.1, 0.05 };
    double nearestRoundingFactor = roundingFactors[0];

    for (int i = 1; i < roundingFactors.length; i++) {
      if (roundingFactors[i] < number) {
        nearestRoundingFactor = roundingFactors[i];
      } else {
        break;
      }
    }

    return nearestRoundingFactor;
  }

  public static double getNearestRoundingFactor(double number, double[] roundingFactors) {
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

    if(axis instanceof AxisLinearSkipTimeBlocks timeSkippingAxis) {

      final Range axisRange = axis.getRange();

      double
          minVisibleX = timeSkippingAxis.transform(axisRange.getMin()),
          maxVisibleX = timeSkippingAxis.transform(axisRange.getMax()),
          visibleRange = maxVisibleX - minVisibleX;

      Chart2D chart = axis.getAccessor().getChart();
      FontMetrics fm = chart.getFontMetrics(chart.getFont());

      int xAxisWidthInPx = chart.getXAxisWidth();
      int longestLabelWidthInPx = fm.stringWidth("HH:mm:ss.ssss");

      double idealDistanceBetweenLabels = visibleRange / 5;

      final List<LabeledValue> toReturn = new LinkedList<>();

      /*
        encode each timescale into a record containing
        (labelPattern, minVisibleRange, roundingFactors)

        then, find the minVisibleRange nearest to visibleRange
       */

      /*

        if we know the length of hte longest formatter
        and we want to fit at least FIVE major ticks on there
        so, we get 1/5th of the visible range and find the next
        lowest time-scale

        10-25-50 milliseconds
        1-5-10-20-30 minutes
        1-6-12 hours

        from YEARS to MILLISECONDS.

        smallest to largest, first that fits.
       */

      return toReturn;

    }

    return Collections.emptyList();

  }


/*
      final boolean iterateIntraday = visibleRange < twentyHours;

      //indicate date at 0,0 if we're in an intraday timescale. Width = 0 because not in the official axis space.
      if(iterateIntraday)
        toReturn.add(bakeLabel(dayFormatter.format(axisRange.getMin()), 0, true));

      Set<TimeBlock> timeBlocks = timeSkippingAxis.getSkipTimeBlocks();
      for(TimeBlock tB : timeBlocks) {

        //skip any time-jumps that are not within visible range
        if(tB.endAt < axisRange.getMin() || tB.startAt > axisRange.getMax())
          continue;

        ZonedDateTime
                skipStartsAt  =  Instant.ofEpochMilli((long)tB.startAt).atZone(ZoneId.of("America/New_York")),
                skipEndsAt    =  Instant.ofEpochMilli((long)tB.endAt)  .atZone(ZoneId.of("America/New_York"));

        long
                startAt_epochDay = skipStartsAt.toLocalDate().toEpochDay(),
                endAt_epochDay = skipEndsAt.toLocalDate().toEpochDay();

        if(endAt_epochDay != startAt_epochDay) {

          String lbl = dayFormatter.format(skipEndsAt.toInstant().toEpochMilli());

          //if we've jumped past midnight, indicate the date change
          toReturn.add(bakeLabel(lbl, (timeSkippingAxis.transform(tB.endAt+1) - minVisibleX) / (visibleRange), false));

          pixelsConsumed += fm.stringWidth(lbl);

        } else {

          // otherwise, just indicate how much time we've skipped.

          String timeJumpLabel = "+";

          long
                  timeJump = (long) (tB.endAt - tB.startAt),
                  minutes = timeJump/60000;

          int hours = (int) (minutes/60);

          //format as +99min until we hit 100, then switch to +1h40m
          if(minutes > 99)
            timeJumpLabel += hours + "h";
          else hours = 0;

          timeJumpLabel += (minutes - (hours*60)) + "m";

          toReturn.add(bakeLabel(timeJumpLabel, (timeSkippingAxis.transform(tB.endAt+1) - minVisibleX) / (visibleRange), false));

          pixelsConsumed += fm.stringWidth(timeJumpLabel);

        }

      }

      if(iterateIntraday) {

        Set<LabeledValue>
                minutes         =  new LinkedHashSet<>(),
                fiveMinutes     =  new LinkedHashSet<>(),
                tenMinutes      =  new LinkedHashSet<>(),
                thirtyMinutes   =  new LinkedHashSet<>(),
                hours           =  new LinkedHashSet<>();

        ZonedDateTime dateIterator  = Instant.ofEpochMilli((long)minVisibleX).atZone(ZoneId.of("America/New_York"));

        dateIterator = dateIterator.withSecond(0);
        dateIterator = dateIterator.withMinute(0);
        dateIterator = dateIterator.withHour(0);

        int minutesLength = 0,
                fiveMinutesLength = 0,
                tenMinutesLength = 0,
                thirtyMinutesLength = 0,
                hoursLength = 0;

        for (double value = dateIterator.toInstant().toEpochMilli(); value <= axisRange.getMax(); value = dateIterator.toInstant().toEpochMilli()) {

          if(timeBlocks != null) {
            boolean skip = false;
            for(TimeBlock tB : timeBlocks) {
              if(tB.startAt <= value && value <= tB.endAt) {
                skip = true;
                break;
              }
            }
            if(skip) {
              dateIterator = dateIterator.plusMinutes(1);
              continue;
            }

          }

          double offsetValue = value;
          if(timeSkippingAxis!= null)
            offsetValue = timeSkippingAxis.transform(value);

          if(offsetValue < minVisibleX || offsetValue > maxVisibleX) {
            dateIterator = dateIterator.plusMinutes(1);
            continue;
          }

          final LabeledValue label = new LabeledValue();
          label.setValue((offsetValue - minVisibleX) / (visibleRange));
          label.setMajorTick(true);

          String labelText = hourMinuteFormat.format(value);
          label.setLabel(labelText);
          int labelWidthInPx = fm.stringWidth(labelText);

          dateIterator = dateIterator.plusMinutes(1);

          int minute = dateIterator.getMinute() - 1;

          if(minute == 0) {

            hours.add(label);
            hoursLength+=labelWidthInPx;

          } else {

            if (isDivisibleBy(minute, 30)) {
              thirtyMinutes.add(label);
              thirtyMinutesLength+=labelWidthInPx;
            } else if (isDivisibleBy(minute, 10)) {
              tenMinutes.add(label);
              tenMinutesLength+=labelWidthInPx;
            } else if (isDivisibleBy(minute, 5)) {
              fiveMinutes.add(label);
              fiveMinutesLength+=labelWidthInPx;
            } else {
              minutes.add(label);
              minutesLength+=labelWidthInPx;
            }

          }

        }

        int pixelWidthLimit = (int) ((double)xAxisWidthInPx * .6);

        if(pixelsConsumed + hoursLength < pixelWidthLimit) {
          toReturn.addAll(hours);
          pixelsConsumed += hoursLength;

          if(pixelsConsumed + thirtyMinutesLength < pixelWidthLimit) {
            toReturn.addAll(thirtyMinutes);
            pixelsConsumed += thirtyMinutesLength;

            if(pixelsConsumed + tenMinutesLength < pixelWidthLimit) {
              toReturn.addAll(tenMinutes);
              pixelsConsumed += tenMinutesLength;

              if(pixelsConsumed + fiveMinutesLength < pixelWidthLimit) {
                toReturn.addAll(fiveMinutes);
                pixelsConsumed += fiveMinutesLength;

                if(pixelsConsumed + minutesLength < pixelWidthLimit) {
                  toReturn.addAll(minutes);
                }

              }

            }

          }

        }

      }
*/
  /**
   * @see info.monitorenter.gui.chart.IAxisScalePolicy#getScaleValues(Graphics2D, IAxis)
   */
  public List<LabeledValue> getScaleValues(final Graphics2D g2d, final IAxis<?> axis) {
    return this.getLabels(axis);
  }

  /**
   * @see info.monitorenter.gui.chart.IAxisScalePolicy#initPaintIteration(IAxis)
   */
  public void initPaintIteration(IAxis<?> axis) {
    // nop
  }

}
