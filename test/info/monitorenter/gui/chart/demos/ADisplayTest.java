/*
 * ADisplayTest.java,  <enter purpose here>.
 * Copyright (c) 2007  Achim Westermann, Achim.Westermann@gmx.de
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  If you modify or optimize the code in a useful way please let me know.
 *  Achim.Westermann@gmx.de
 */
package info.monitorenter.gui.chart.demos;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxisLabelFormatter;
import info.monitorenter.gui.chart.IAxisTickPainter;
import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.util.Range;
import info.monitorenter.util.units.AUnit;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Basic junit test method hook that searches for files in this package with
 * name "textX.properties" (where "X" is a number that starts from 1) collects
 * the data in the files and shows a chart with a trace that contains this data
 * along with buttons for judging the display as right or wrong.
 * <p>
 * Note that the test files have to be named with ascending numbers to ensure
 * all are shown.
 * <p>
 * 
 * @author <a href="mailto:Achim.Westermann@gmx.de">Achim Westermann </a>
 * 
 * @version $Revision: 1.10 $
 * 
 */
public abstract class ADisplayTest extends TestCase {

  /** The frame currently visible. */
  protected JFrame m_frame;

  /**
   * Chart2D instance that failed, set by UI Threads to trigger failure on the
   * junit testcase Thread.
   * <p>
   */
  protected Chart2D m_failure = null;

  /**
   * Creates a test case with the name.
   * <p>
   * 
   * @param testName
   *          the name of the test case.
   */
  public ADisplayTest(final String testName) {
    super(testName);
  }

  /**
   * Template method that allows to configure the chart to show.
   * <p>
   * 
   * @param chart
   *          the chart that will be shown.
   */
  protected abstract void configure(StaticCollectorChart chart);

  /**
   * Template method that has to return a trace that may be configured.
   * <p>
   * 
   * @return a trace that may be configured.
   */
  protected abstract ITrace2D createTrace();

  /**
   * Internal helper that describes a Chart2D.
   * <p>
   * 
   * @param chart
   *          the instance to describe.
   * 
   * @return the description of the given instance.
   */
  private final String describe(final Chart2D chart) {
    StringBuffer result = new StringBuffer();
    result.append("Chart2D\n");
    result.append("-------\n");

    IAxisTickPainter labelPainter = chart.getAxisTickPainter();
    result.append("LabelPainter: ").append(labelPainter.getClass().getName()).append("\n");

    // X axis
    IAxis axis = chart.getAxisX();
    result.append("Axis x:\n");
    IRangePolicy rangePolicy = axis.getRangePolicy();
    result.append("  RangePolicy: " + rangePolicy.getClass().getName() + ":\n");
    Range range = rangePolicy.getRange();
    result.append("    min: " + range.getMin() + "\n");
    result.append("    max: " + range.getMax() + "\n");
    IAxisLabelFormatter labelFormatter = axis.getFormatter();
    result.append("  LabelFormatter: ").append(labelFormatter.getClass().getName()).append("\n");
    AUnit unit = labelFormatter.getUnit();
    result.append("  Unit: " + unit.getClass().getName()).append("\n");
    result.append("  Major tick spacing: ").append(axis.getMajorTickSpacing()).append("\n");
    result.append("  Minor tick spacing: ").append(axis.getMajorTickSpacing()).append("\n");

    // Y axis
    axis = chart.getAxisY();
    result.append("Axis y:\n");
    rangePolicy = axis.getRangePolicy();
    result.append("  RangePolicy: " + rangePolicy.getClass().getName() + ":\n");
    range = rangePolicy.getRange();
    result.append("    min: " + range.getMin() + "\n");
    result.append("    max: " + range.getMax() + "\n");
    labelFormatter = axis.getFormatter();
    result.append("  LabelFormatter: ").append(labelFormatter.getClass().getName()).append("\n");
    unit = labelFormatter.getUnit();
    result.append("  Unit: " + unit.getClass().getName()).append("\n");
    result.append("  Major tick spacing: ").append(axis.getMajorTickSpacing()).append("\n");
    result.append("  Minor tick spacing: ").append(axis.getMajorTickSpacing()).append("\n");

    // Traces
    result.append("  Traces:\n");
    Stroke stroke;
    for (ITrace2D trace : chart.getTraces()) {
      result.append("    ").append(trace.getClass().getName()).append(":\n");
      result.append("      amount of poijnts : ").append(trace.getSize()).append("\n");
      result.append("      x-range: [").append(trace.getMinX()).append(",").append(trace.getMaxX())
          .append("]\n");
      result.append("      y-range: [").append(trace.getMinY()).append(",").append(trace.getMaxY())
          .append("]\n");
      result.append("      Color: ").append(trace.getColor()).append("\n");
      result.append("      Label: ").append(trace.getLabel()).append("\n");
      result.append("      Visible: ").append(trace.isVisible()).append("\n");
      result.append("      Z-index: ").append(trace.getZIndex()).append("\n");
      result.append("      TracePainters: \n");
      for (ITracePainter< ? > tracePainter : trace.getTracePainters()) {
        result.append("        ").append(tracePainter.getClass().getName()).append("\n");
      }
      stroke = trace.getStroke();
      result.append("       Stroke: ").append(stroke.getClass().getName()).append("\n");
    }

    return result.toString();
  }

  /**
   * Internal helper that describes a StaticCollectorChart.
   * <p>
   * 
   * @param collectorchart
   *          the instance to describe.
   * 
   * @return the description of the given instance.
   */
  private final String describe(final StaticCollectorChart collectorchart) {
    return this.describe(collectorchart.getChart());
  }

  /**
   * Marks this test as failure.
   * <p>
   * 
   * @param wrong
   *          the chart that failed.
   *          <p>
   */
  public final void fail(final Chart2D wrong) {
    this.m_failure = wrong;

  }

  /**
   * Template method that returns the next chart to test or null if no further
   * chart is available.
   * <p>
   * 
   * @return the next chart to test or null if no further chart is available.
   * 
   * @throws IOException
   *           if sth. goes wrong.
   */
  protected abstract StaticCollectorChart getNextChart() throws IOException;

  /**
   * Internal helper that shows the chart in a frame.
   * <p>
   * 
   * @param chart
   *          the chart to display.
   * 
   * @throws InterruptedException
   *           if sleeping fails.
   */
  private final void show(final StaticCollectorChart chart) throws InterruptedException {

    this.m_frame = new JFrame(this.getClass().getName());
    Container content = this.m_frame.getContentPane();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.add(chart);

    // description of the chart:
    JTextArea description = new JTextArea();
    description.setFont(new Font("Courier", Font.PLAIN, 12));
    description.setEditable(false);
    JScrollPane scrollpane = new JScrollPane(description);
    scrollpane.setMaximumSize(new Dimension(600, 100));
    scrollpane.setPreferredSize(new Dimension(600, 100));
    content.add(scrollpane);

    // buttons for fail and ok:
    JButton fail = new JButton("fail");
    fail.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent ae) {
        ADisplayTest.this.fail(chart.getChart());
      }
    });
    JButton ok = new JButton("Ok");
    ok.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent ae) {
        ADisplayTest.this.m_frame.setVisible(false);
        ADisplayTest.this.m_frame.dispose();
      }
    });
    JPanel controls = new JPanel();
    controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
    controls.add(Box.createHorizontalGlue());
    controls.add(ok);
    controls.add(Box.createHorizontalGlue());
    controls.add(fail);
    controls.add(Box.createHorizontalGlue());
    content.add(controls);

    this.m_frame.addWindowListener(new WindowAdapter() {
      /**
       * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
       */
      @Override
      public void windowClosing(final WindowEvent e) {
        ADisplayTest.this.m_frame.setVisible(false);
        ADisplayTest.this.m_frame.dispose();
      }
    });
    this.m_frame.setSize(600, 600);
    this.m_frame.setVisible(true);
    Thread.sleep(1000);
    description.setText(this.describe(chart));
    while (this.m_frame.isVisible()) {
      try {
        Thread.sleep(1000);
        if (this.m_failure != null) {
          this.m_frame.setVisible(false);
          this.m_frame.dispose();
          this.m_frame = null;

          Assert.fail("Display Test judged bad:\n" + this.describe(this.m_failure));
        }
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
    this.m_frame.setVisible(false);
    this.m_frame.dispose();
    this.m_frame = null;

  }

  /**
   * Basic junit test method hook that searches for files in this package with
   * name "textX.properties" (where "X" is a number that starts from 1) collects
   * the data in the files and shows a chart with a trace that contains this
   * data along with buttons for judging the display as right or wrong.
   * <p>
   * 
   * @throws IOException
   *           if sth. goes wrong.
   * 
   * @throws InterruptedException
   *           if sleeping is interrupted.
   */
  public final void testDisplay() throws IOException, InterruptedException {
    StaticCollectorChart chart;
    boolean foundData = true;
    do {
      chart = this.getNextChart();
      if (chart == null) {
        foundData = false;
      } else {
        this.configure(chart);
        this.show(chart);
      }
    } while (foundData);
  }
}