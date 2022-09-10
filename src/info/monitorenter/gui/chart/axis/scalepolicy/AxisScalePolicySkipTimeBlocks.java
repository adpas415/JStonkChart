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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AxisScalePolicySkipTimeBlocks extends AxisScalePolicyTransformation {

  SimpleDateFormat
    dayFormatter    = new SimpleDateFormat("MM/dd"),
    hourMinuteFormat = new SimpleDateFormat("HH:mm");


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

  final long
          oneSecond         = 1000L,
          oneMinute         = oneSecond * 60,
          oneHour           = oneMinute * 60,
          twentyHours       = oneHour   * 20;
  /**
   * Returns the labels for this axis.
   * <p>
   * 
   * 
   * @return the labels for the axis.
   */
  protected List<LabeledValue> getLabels(final IAxis<?> axis) {

    AxisLinearSkipTimeBlocks timeSkippingAxis = null;

    if(axis instanceof AxisLinearSkipTimeBlocks)
      timeSkippingAxis = (AxisLinearSkipTimeBlocks) axis;

    final List<LabeledValue> toReturn = new LinkedList<LabeledValue>();

    final Range axisRange = axis.getRange();

    double
        min = axisRange.getMin(),
        max = axisRange.getMax(),
        visibleRange = max - min;

    if(timeSkippingAxis != null) {
      min = timeSkippingAxis.transform(min);
      max = timeSkippingAxis.transform(max);
      visibleRange = max - min;
    }

    if(visibleRange < oneMinute)
      return toReturn;

    final boolean iterateIntraday = visibleRange < twentyHours;

    Chart2D chart = axis.getAccessor().getChart();

    Font f = chart.getFont();
    FontMetrics fm = chart.getFontMetrics(f);

    int pixelWidth = chart.getXAxisWidth(),
        pixelsConsumed = 0;


    Set<TimeBlock> timeBlocks = null;

    if(timeSkippingAxis != null)
      timeBlocks = timeSkippingAxis.getSkipTimeBlocks();

    //if this axis involves time-skips, we'll label those first.
    if(timeSkippingAxis != null) {

      //indicate date at 0,0 if we're in an intraday timescale. Width = 0 because not in the official axis space.
      if(iterateIntraday)
        toReturn.add(bakeLabel(dayFormatter.format(axisRange.getMin()), 0, true));

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
          toReturn.add(bakeLabel(lbl, (timeSkippingAxis.transform(tB.endAt+1) - min) / (visibleRange), false));

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

          toReturn.add(bakeLabel(timeJumpLabel, (timeSkippingAxis.transform(tB.endAt+1) - min) / (visibleRange), false));

          pixelsConsumed += fm.stringWidth(timeJumpLabel);

        }

      }

    }

    if(iterateIntraday) {

      Set<LabeledValue>
        minutes         =  new LinkedHashSet<>(),
        fiveMinutes     =  new LinkedHashSet<>(),
        tenMinutes      =  new LinkedHashSet<>(),
        thirtyMinutes   =  new LinkedHashSet<>(),
        hours           =  new LinkedHashSet<>();

      ZonedDateTime dateIterator  = Instant.ofEpochMilli((long)min).atZone(ZoneId.of("America/New_York"));

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

        if(offsetValue < min || offsetValue > max) {
          dateIterator = dateIterator.plusMinutes(1);
          continue;
        }

        final LabeledValue label = new LabeledValue();
        label.setValue((offsetValue - min) / (visibleRange));
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

      int pixelWidthLimit = (int) ((double)pixelWidth * .6);

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

    return toReturn;

  }

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
