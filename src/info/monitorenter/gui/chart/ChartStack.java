package info.monitorenter.gui.chart;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChartStack extends JPanel {
        
    public Queue<Chart2D> charts = new ConcurrentLinkedQueue();
    final Color dividerColor;
    final int dividerThickness;

    public ChartStack() {
        this(Color.black, 6);
    }
    public ChartStack(Color dividerColor, int dividerThickness) {

        this.dividerColor = dividerColor;
        this.dividerThickness = dividerThickness;

        setLayout(new BorderLayout());
        setOpaque(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                super.componentResized(e);

                charts.forEach(s->s.setRequestedRepaint(true));

            }
        });

    }

    Map<Chart2D, Double> weightsByChart = new HashMap<>();
    public void addChart(Chart2D chart, double resizeWeight) {
        weightsByChart.put(chart, resizeWeight);
        charts.add(chart);
    }
    
    public void addChart(Chart2D chart) {
        charts.add(chart);
    }
        
    public void removeChart(Chart2D chart) {
        charts.remove(chart);
    }
    
    public void removeAllCharts() {
        charts.clear();
    }
    
    public JPanel getStackPanel() {        
        return this;
    }
    
    public void refreshStackSync() {
        
        Iterator<Chart2D> iter = charts.iterator();
        
        Chart2D firstChart = iter.next();
        
        firstChart.clearSynchronizations();
        firstChart.getAxisX().setVisible(true);
        
        Chart2D prevChart;
        
        if(iter.hasNext()) {
            prevChart = firstChart;
            while(iter.hasNext()) {
                Chart2D nextChart = iter.next();
                nextChart.clearSynchronizations();
                //-
                if(nextChart.isVisible()) {
                    firstChart.setSynchronizedXStartChart(nextChart);
                    firstChart.syncAxisX(nextChart);
                    //-
                    prevChart.getAxisX().setVisible(false);
                    nextChart.getAxisX().setVisible(true);
                    //-
                    prevChart = nextChart;
                }
            }
        }
        
        removeAll();
        
        iter = charts.iterator();        
        firstChart = iter.next();
                
        if(!iter.hasNext())
            add(firstChart);
        else {
            Component topPane = null; 
            while(iter.hasNext()) {
                
                Chart2D nextChart = iter.next();
                
                if(nextChart.isVisible()) {
                    topPane = new MinimalistSplitPane(JSplitPane.VERTICAL_SPLIT, dividerColor, dividerThickness, topPane == null ? firstChart : topPane, nextChart);
                } else if( topPane == null && !iter.hasNext() )
                    topPane = firstChart;

                if(weightsByChart.containsKey(nextChart) && topPane instanceof JSplitPane) {
                    ((JSplitPane) topPane).setResizeWeight(weightsByChart.get(nextChart));
                }

            }
            add(topPane);
        }
                
        revalidate();
        
        final Chart2D f = firstChart;
        new Thread() {
            public void run() {         
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                } finally {
                    f.updateZoomForSyncCharts();
                    if(getParent() != null)
                        getParent().repaint();
                }
            }
        }.start();
    }

    public static class MinimalistSplitPane extends JSplitPane {
        public MinimalistSplitPane() {
            this(Color.black, 6);
        }
        public MinimalistSplitPane(Color separatorColor, int dividerThickness) {
            setup(separatorColor, dividerThickness);
        }

        public MinimalistSplitPane(int orientation, Color dividerColor, int dividerThickness, Component component1, Component component2) {
            super(orientation, component1, component2);
            setup(dividerColor, dividerThickness);

        }

        private void setup(Color separatorColor, int dividerThickness) {

            setDividerSize(dividerThickness);
            setUI(new BasicSplitPaneUI() {
                @Override
                public BasicSplitPaneDivider createDefaultDivider() {
                    return new BasicSplitPaneDivider(this) {

                        @Override
                        public void setBorder(Border b) {}

                        @Override
                        public void paint(Graphics g) {
                            g.setColor(separatorColor);
                            g.fillRect(0, 0, getSize().width, getSize().height);
                            super.paint(g);
                        }

                    };
                }
            });
            setBorder(BorderFactory.createEmptyBorder());

        }

        public MinimalistSplitPane(int orientation, Color separatorColor, int dividerThickness) {
            super(orientation);
            setup(separatorColor, dividerThickness);
        }

    }

}
