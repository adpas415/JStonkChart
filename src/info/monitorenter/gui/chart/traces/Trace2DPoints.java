package info.monitorenter.gui.chart.traces;

import info.monitorenter.gui.chart.*;
import info.monitorenter.gui.chart.pointpainters.APointPainter;
import info.monitorenter.gui.chart.traces.painters.TracePainterConfigurable;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

public class Trace2DPoints implements ITrace2D {

  /** Generated <code>serialVersionUID</code>. **/
  private static final long serialVersionUID = -2358495593310332400L;

  /**
   * The trace implementation that is being decorated with the
   * candlestick-feature.
   */
  private final ITrace2D m_delegate;

  /**
   * @see ITrace2D#addPoint(ITracePoint2D,
   *      ITrace2D)
   */
  public boolean addPoint(ITracePoint2D p, ITrace2D wrapperOfMe) {
    return this.m_delegate.addPoint(p, wrapperOfMe);
  }

  /**
   * Constructor taking the trace implementation to decorate with candle stick
   * painting.
   * <p>
   * 
   * @param delegateThatIsEnrichedByTagPainting
   *          impl that will deal with the basic trace functionality.
   *
   */
  public Trace2DPoints(final ITrace2D delegateThatIsEnrichedByTagPainting, APointPainter pointPainter) {
    assert (delegateThatIsEnrichedByTagPainting != null) : " Do not pass null";
    this.m_delegate = delegateThatIsEnrichedByTagPainting;
    /*
     * FIXME: Once method removeAllTracePainters() is available switch to that.
     */
    for (ITracePainter< ? > tracePainter : this.m_delegate.getTracePainters()) {
      this.m_delegate.removeTracePainter(tracePainter);
    }
    pointPainter.setTrace(m_delegate);
    setTracePainter(new TracePainterConfigurable<>(pointPainter));
    this.m_pointPainter = pointPainter;
  }

  /**
   * @param evt
   * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent evt) {
    this.m_delegate.propertyChange(evt);
  }

  /**
   * @see Comparable#compareTo(Object)
   */
  public int compareTo(ITrace2D o) {
    return this.m_delegate.compareTo(o);
  }

  /**
   * @param trace
   * @see ITrace2D#addComputingTrace(ITrace2D)
   */
  public void addComputingTrace(ITrace2D trace) {
    this.m_delegate.addComputingTrace(trace);
  }

  /**
   * @see ITrace2D#addErrorBarPolicy(IErrorBarPolicy)
   */
  public boolean addErrorBarPolicy(IErrorBarPolicy< ? > errorBarPolicy) {
    return this.m_delegate.addErrorBarPolicy(errorBarPolicy);
  }

  /**
   * @see ITrace2D#addPoint(double, double)
   */
  public boolean addPoint(double x, double y) {
    throw new UnsupportedOperationException(
        "Don't use this on a "
            + this.getClass().getName()
            + " instance as this implementation needs a special ITracePoint2D implementation. Use addPoint(ITracePoint2D) with the proper ITracePoint2DCandleStick implementation.");
  }

  /**
   * Reused candle stick point painter.
   */
  private IPointPainter< ? > m_pointPainter;

  /**
   * @see ITrace2D#addPoint(ITracePoint2D)
   */
  public boolean addPoint(ITracePoint2D p) {
    //p.removeAllAdditionalPointPainters();
    //p.addAdditionalPointPainter(this.m_pointPainter);
    boolean result = this.m_delegate.addPoint(p, this);
    return result;
  }

  /**
   * @see ITrace2D#addPointHighlighter(IPointPainter)
   */
  public boolean addPointHighlighter(IPointPainter< ? > highlighter) {
    return this.m_delegate.addPointHighlighter(highlighter);
  }

  /**
   * @see ITrace2D#addPropertyChangeListener(String,
   *      PropertyChangeListener)
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    this.m_delegate.addPropertyChangeListener(propertyName, listener);
  }

  /**
   * @see ITrace2D#addTracePainter(ITracePainter)
   */
  public boolean addTracePainter(ITracePainter< ? > painter) {
    return this.m_delegate.addTracePainter(painter);
  }

  /**
   * @see ITrace2D#containsTracePainter(ITracePainter)
   */
  public boolean containsTracePainter(ITracePainter< ? > painter) {
    return this.m_delegate.containsTracePainter(painter);
  }

  /**
   * @see ITrace2D#descendingIterator()
   */
  public Iterator<ITracePoint2D> descendingIterator() {
    return this.m_delegate.descendingIterator();
  }

  /**
   * @see ITrace2D#firePointChanged(ITracePoint2D, ITracePoint2D.STATE, Object, Object)
   */
  public void firePointChanged(final ITracePoint2D changed, final ITracePoint2D.STATE state, final Object oldValue, final Object newValue) {
    this.m_delegate.firePointChanged(changed, state, oldValue, newValue);
  }

  /**
   * @see ITrace2D#getColor()
   */
  public Color getColor() {
    return this.m_delegate.getColor();
  }

  /**
   * @see ITrace2D#getErrorBarPolicies()
   */
  public Set<IErrorBarPolicy< ? >> getErrorBarPolicies() {
    return this.m_delegate.getErrorBarPolicies();
  }

  /**
   * @see ITrace2D#getHasErrorBars()
   */
  public boolean getHasErrorBars() {
    return this.m_delegate.getHasErrorBars();
  }

  /**
   * @see ITrace2D#getLabel()
   */
  public String getLabel() {
    return this.m_delegate.getLabel();
  }

  /**
   * @see ITrace2D#getMaxSize()
   */
  public int getMaxSize() {
    return this.m_delegate.getMaxSize();
  }

  /**
   * @see ITrace2D#getMaxX()
   */
  public double getMaxX() {
    /*
     * This works as delegate asks point which asks the special painter.
     */
    return this.m_delegate.getMaxX();
  }

  /**
   * @see ITrace2D#getMaxY()
   */
  public double getMaxY() {
    /*
     * This works as delegate asks point which asks the special painter.
     */
    return this.m_delegate.getMaxY();
  }

  /**
   * @see ITrace2D#getMinX()
   */
  public double getMinX() {
    /*
     * This works as delegate asks point which asks the special painter.
     */
    return this.m_delegate.getMinX();
  }

  /**
   * @see ITrace2D#getMinY()
   */
  public double getMinY() {
    /*
     * This works as delegate asks point which asks the special painter.
     */
    return this.m_delegate.getMinY();
  }

  /**
   * @see ITrace2D#getName()
   */
  public String getName() {
    return this.m_delegate.getName();
  }

  /**
   * @see ITrace2D#getNearestPointEuclid(double,
   *      double)
   */
  public DistancePoint getNearestPointEuclid(double x, double y) {
    // FIXME: search for the nearest point to the centre of the candlestick
    // tracepoints.
    return this.m_delegate.getNearestPointEuclid(x, y);
  }

  /**
   * @see ITrace2D#getNearestPointManhattan(double,
   *      double)
   */
  public DistancePoint getNearestPointManhattan(double x, double y) {
    // FIXME: search for the nearest point to the centre of the candlestick
    // tracepoints.
    return this.m_delegate.getNearestPointManhattan(x, y);
  }

  @Override
  public DistancePoint getNearestPointX(double x) {
    return this.m_delegate.getNearestPointX(x);
  }

  @Override
  public double maxYVisible() {
    return this.m_delegate.maxYVisible();
  }

  @Override
  public double minYVisible() {
    return this.m_delegate.minYVisible();
  }

  /**
   * @see ITrace2D#getPhysicalUnits()
   */
  public String getPhysicalUnits() {
    return this.m_delegate.getPhysicalUnits();
  }

  /**
   * @see ITrace2D#getPhysicalUnitsX()
   */
  public String getPhysicalUnitsX() {
    return this.m_delegate.getPhysicalUnitsX();
  }

  /**
   * @see ITrace2D#getPhysicalUnitsY()
   */
  public String getPhysicalUnitsY() {
    return this.m_delegate.getPhysicalUnitsY();
  }

  /**
   * @see ITrace2D#getPointHighlighters()
   */
  public Set<IPointPainter< ? >> getPointHighlighters() {
    return this.m_delegate.getPointHighlighters();
  }

  /**
   * @see ITrace2D#getPropertyChangeListeners(String)
   */
  public PropertyChangeListener[] getPropertyChangeListeners(String property) {
    return this.m_delegate.getPropertyChangeListeners(property);
  }

  /**
   * @see ITrace2D#getRenderer()
   */
  public Chart2D getRenderer() {
    return this.m_delegate.getRenderer();
  }

  /**
   * @see ITrace2D#getSize()
   */
  public int getSize() {
    return this.m_delegate.getSize();
  }

  /**
   * @see ITrace2D#getStroke()
   */
  public Stroke getStroke() {
    return this.m_delegate.getStroke();
  }

  /**
   * @see ITrace2D#getTracePainters()
   */
  public Set<ITracePainter< ? >> getTracePainters() {
    return this.m_delegate.getTracePainters();
  }

  /**
   * @see ITrace2D#getTracePointProvider()
   */
  public ITracePointProvider getTracePointProvider() {
    return this.m_delegate.getTracePointProvider();
  }

  /**
   * @see ITrace2D#getZIndex()
   */
  public Integer getZIndex() {
    return this.m_delegate.getZIndex();
  }

  @Override
  public void initPaintIteration() {

  }

  @Override
  public boolean isAdditionalSpaceRequired() {
    return false;
  }

  /**
   * @see ITrace2D#isEmpty()
   */
  public boolean isEmpty() {
    return this.m_delegate.isEmpty();
  }

  @Override
  public boolean isPixelTransformationRequired() {
    return false;
  }

  /**
   * @see ITrace2D#isVisible()
   */
  public boolean isVisible() {
    return this.m_delegate.isVisible();
  }

  /**
   * @see ITrace2D#iterator()
   */
  public Iterator<ITracePoint2D> iterator() {
    return this.m_delegate.iterator();
  }

  @Override
  public double maxXSearch() {
    return this.m_delegate.maxXSearch();
  }

  @Override
  public double maxYSearch() {
    return this.m_delegate.maxYSearch();
  }

  @Override
  public double minXSearch() {
    return this.m_delegate.minXSearch();
  }

  @Override
  public double minYSearch() {
    return this.m_delegate.minYSearch();
  }

  @Override
  public void onAdded2ChartBeforeFirstPaint() {

  }

  /**
   * @see ITrace2D#removeAllPointHighlighters()
   */
  public Set<IPointPainter< ? >> removeAllPointHighlighters() {
    return this.m_delegate.removeAllPointHighlighters();
  }

  /**
   * @see ITrace2D#removeAllPoints()
   */
  public void removeAllPoints() {
    this.m_delegate.removeAllPoints();
  }

  /**
   * @see ITrace2D#removeComputingTrace(ITrace2D)
   */
  public boolean removeComputingTrace(ITrace2D trace) {
    return this.m_delegate.removeComputingTrace(trace);
  }

  /**
   * @see ITrace2D#removeErrorBarPolicy(IErrorBarPolicy)
   */
  public boolean removeErrorBarPolicy(IErrorBarPolicy< ? > errorBarPolicy) {
    return this.m_delegate.removeErrorBarPolicy(errorBarPolicy);
  }

  /**
   * @see ITrace2D#removePoint(ITracePoint2D)
   */
  public boolean removePoint(ITracePoint2D point) {
    return this.m_delegate.removePoint(point);
  }

  /**
   * @see ITrace2D#removePointHighlighter(IPointPainter)
   */
  public boolean removePointHighlighter(IPointPainter< ? > highlighter) {
    return this.m_delegate.removePointHighlighter(highlighter);
  }

  /**
   * @see ITrace2D#removePropertyChangeListener(PropertyChangeListener)
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.m_delegate.removePropertyChangeListener(listener);
  }

  /**
   * @see ITrace2D#removePropertyChangeListener(String,
   *      PropertyChangeListener)
   */
  public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
    this.m_delegate.removePropertyChangeListener(property, listener);
  }

  /**
   * @see ITrace2D#removeTracePainter(ITracePainter)
   */
  public boolean removeTracePainter(ITracePainter< ? > painter) {
    return this.m_delegate.removeTracePainter(painter);
  }

  /**
   * @see ITrace2D#setColor(Color)
   */
  public void setColor(Color color) {
    this.m_delegate.setColor(color);
  }

  /**
   * @see ITrace2D#setErrorBarPolicy(IErrorBarPolicy)
   */
  public Set<IErrorBarPolicy< ? >> setErrorBarPolicy(IErrorBarPolicy< ? > errorBarPolicy) {
    return this.m_delegate.setErrorBarPolicy(errorBarPolicy);
  }

  /**
   * @see ITrace2D#setName(String)
   */
  public void setName(String name) {
    this.m_delegate.setName(name);
  }

  /**
   * @see ITrace2D#setPhysicalUnits(String,
   *      String)
   */
  public void setPhysicalUnits(String xunit, String yunit) {
    this.m_delegate.setPhysicalUnits(xunit, yunit);
  }

  /**
   * @see ITrace2D#setPointHighlighter(IPointPainter)
   */
  public Set<IPointPainter< ? >> setPointHighlighter(IPointPainter< ? > highlighter) {
    return this.m_delegate.setPointHighlighter(highlighter);
  }

  /**
   * @see ITrace2D#setRenderer(Chart2D)
   */
  public void setRenderer(Chart2D renderer) {
    this.m_delegate.setRenderer(renderer);
  }

  /**
   * @see ITrace2D#setStroke(Stroke)
   */
  public void setStroke(Stroke stroke) {
    this.m_delegate.setStroke(stroke);
  }

  /**
   * @see ITrace2D#setTracePainter(ITracePainter)
   */
  public Set<ITracePainter< ? >> setTracePainter(ITracePainter< ? > painter) {
    return this.m_delegate.setTracePainter(painter);
  }

  /**
   * @throws UnsupportedOperationException
   *           always.
   * 
   * @see ITrace2D#setTracePointProvider(ITracePointProvider)
   */
  public void setTracePointProvider(ITracePointProvider tracePointProvider) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Don't use this on a " + this.getClass().getName()
        + " instance as this implementation needs a special trace point provider implementation. ");
  }

  /**
   * @see ITrace2D#setVisible(boolean)
   */
  public void setVisible(boolean visible) {
    this.m_delegate.setVisible(visible);
  }

  /**
   * @see ITrace2D#setZIndex(Integer)
   */
  public void setZIndex(Integer zIndex) {
    this.m_delegate.setZIndex(zIndex);
  }

  /**
   * @see ITrace2D#showsErrorBars()
   */
  public boolean showsErrorBars() {
    return this.m_delegate.showsErrorBars();
  }

  /**
   * @see ITrace2D#showsNegativeXErrorBars()
   */
  public boolean showsNegativeXErrorBars() {
    return this.m_delegate.showsNegativeXErrorBars();
  }

  /**
   * @see ITrace2D#showsNegativeYErrorBars()
   */
  public boolean showsNegativeYErrorBars() {
    return this.m_delegate.showsNegativeYErrorBars();
  }

  /**
   * @see ITrace2D#showsPositiveXErrorBars()
   */
  public boolean showsPositiveXErrorBars() {
    return this.m_delegate.showsPositiveXErrorBars();
  }

  /**
   * @see ITrace2D#showsPositiveYErrorBars()
   */
  public boolean showsPositiveYErrorBars() {
    return this.m_delegate.showsPositiveYErrorBars();
  }

  @Override
  public boolean interpolateDiscontinuedPoints() {
    return false;
  }
/*
  @Override
  public DistancePoint getNearestPointX(double x) {
    return this.m_delegate.getNearestPointX(x);
  }
  @Override
  public ITracePoint2D firstVisibleXPoint() {
    return this.m_delegate.firstVisibleXPoint();
  }

  @Override
  public void setAxis(final IAxis< ? > xAxis, final IAxis< ? > yAxis) {
      this.m_delegate.setAxis(xAxis, yAxis);
  }

  @Override
  public IAxis getAxisX() {
      return m_delegate.getAxisX();
  }

  @Override
  public IAxis getAxisY() {
      return m_delegate.getAxisY();
  }
  */
}
