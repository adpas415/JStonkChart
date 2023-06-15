package info.monitorenter.gui.chart.demos;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.axis.AxisLinearSkipTimeBlocks;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyBestFitByPixel;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicySkipTimeBlocks;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.labelformatters.formats.PriceFormat;
import info.monitorenter.gui.chart.labelformatters.formats.TradingVolumeFormat;
import info.monitorenter.gui.chart.pointpainters.PointPainterCandleStick;
import info.monitorenter.gui.chart.pointpainters.PointPainterSimpleStick;
import info.monitorenter.gui.chart.pointpainters.PointPainterTag;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.tracepoints.BackgroundColor;
import info.monitorenter.gui.chart.tracepoints.CandleStick;
import info.monitorenter.gui.chart.tracepoints.Tag;
import info.monitorenter.gui.chart.tracepoints.VolumeBar;
import info.monitorenter.gui.chart.traces.Trace2DPoints;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterBackgroundColor;
import info.monitorenter.util.Range;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;

public class StonkChartDemo extends JPanel {


    public static void main(String[] args) throws Exception {

        StonkChartDemo chart = new StonkChartDemo();

        // Make it visible:
        // Create a frame.
        JFrame frame = new JFrame("StonkChartDemo");
        // add the chart to the frame:
        frame.getContentPane().add(chart);
        frame.setSize(800, 600);
        // Enable the termination button [cross on the upper right edge]:
        frame.addWindowListener(new WindowAdapter() {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);

        chart.populateChart();

    }



    final Color
        green  =  Color.decode("#28A49C"),
        red    =  Color.decode("#F05851");

    final Chart2D
        price   =  new Chart2D(),
        volume  =  new Chart2D();

    final ITrace2D
        ohlc = new Trace2DPoints(new Trace2DSimple(), new PointPainterCandleStick()),
        vol  = new Trace2DPoints(new Trace2DSimple(), new PointPainterSimpleStick()),
        bg   = new Trace2DSimple(),
        tags = new Trace2DPoints(new Trace2DSimple(), new PointPainterTag());

    AxisLinearSkipTimeBlocks<AxisScalePolicySkipTimeBlocks> priceAxisX = new AxisLinearSkipTimeBlocks<>();

    {

        price.setAxisXBottom(priceAxisX, 0);

        bg.setTracePainter(new TracePainterBackgroundColor());
        price.addTrace(bg);

        price.addTrace(tags);

        price.addTrace(ohlc);
        ohlc.setName("OHLC");
        ohlc.setZIndex(1);

        volume.addTrace(vol);
        vol.setName("VOLUME");
        vol.setZIndex(1);

        decorateChart(price);
        decorateChart(volume);
        yAxisPrice(price);
        xAxisTime(price);

        yAxisVolume(volume);
        xAxisTime(volume);

        /*ChartStack chartStack = new ChartStack(Color.black, 6);
        chartStack.addChart(price);
        chartStack.addChart(volume);
        chartStack.refreshStackSync();*/

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(price);
        add(volume);

    }

    public void populateChart() throws Exception {

        List<Long> xCoords = new LinkedList<>();

        importCSV( "AAPL.csv", 30, row -> {

            String dateString = row.get("Date");

            long time = ZonedDateTime.of(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")), LocalTime.MIDNIGHT, ZoneId.of("America/New_York")).toInstant().toEpochMilli();

            double
                open   =  Double.valueOf(row.get("Open")),
                high   =  Double.valueOf(row.get("High")),
                low    =  Double.valueOf(row.get("Low")),
                close  =  Double.valueOf(row.get("Close"));

            long volume = Long.valueOf(row.get("Volume"));

            System.out.println(time + " > " +
                    "open: " + open + ", " +
                    "high: " + high + ", " +
                    "low: " + low + ", " +
                    "close: " + close + ", " +
                    "volume: " + volume + "");

            xCoords.add(time);


            paintCandle(time, open, high, low, close, volume);

        });

        Color
            red = new Color(255, 0, 0, 50),
            orange = new Color(255, 200, 0, 50),
            transparent = new Color(0,0,0,0);

        bg.setZIndex(-1);


        BackgroundColor
            regBg = new BackgroundColor(xCoords.get(3), 80, red),
            transparentBg = new BackgroundColor(xCoords.get(10), 80, transparent),
            orangeBg = new BackgroundColor(xCoords.get(17), 80, orange);

        bg.addPoint(regBg);
        bg.addPoint(transparentBg);
        bg.addPoint(orangeBg);

        long twelveHours = 1000*60*60*12;

        priceAxisX.setRangePolicy(new RangePolicyFixedViewport(new Range(xCoords.get(3), xCoords.get(xCoords.size()-3) - twelveHours)));
        volume.getAxisX().setRangePolicy(new RangePolicyFixedViewport(new Range(xCoords.get(3), xCoords.get(xCoords.size()-3) - twelveHours)));

        tags.addPoint(new Tag(xCoords.get(0), 75, 0, 0, null, 0, 0, "1", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(1), 76, 0, 0, null, 0, 0, "2", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(5), 77, 0, 0, null, 0, 0, "3", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(12), 78, 0, 0, null, 0, 0, "4", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(16), 79, 0, 0, null, 0, 0, "5", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(xCoords.size()-2), 80, 0, 0, null, 0, 0, "6", false, Color.WHITE));
        tags.addPoint(new Tag(xCoords.get(xCoords.size()-1), 81, 0, 0, null, 0, 0, "7", false, Color.WHITE));

        priceAxisX.addOmittedTimeBlock(xCoords.get(11), xCoords.get(16));

    }


    public void paintCandle(long time, double open, double high, double low, double close, long volume) {
        Color color = close > open ? green : red;
        ohlc.addPoint(new CandleStick(time, open, high, low, close, color));
        vol.addPoint(new VolumeBar(time, volume, color));
    }

    private static void decorateChart(Chart2D chart) {
        //chart.setFocusable(true);
        chart.setOpaque(true);
        chart.setPaintLabels(false);
        chart.setForeground(Color.white);
        chart.setBackground(Color.decode("#171B26"));
        chart.setGridColor(new Color(100,100,100,30));
        chart.setFont(new Font("Consolas",  0, 18));
        chart.setMinimumSize(new Dimension(100,75));
        chart.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        chart.setToolTipType(Chart2D.ToolTipType.VALUE_SNAP_TO_TRACEPOINTS);

        chart.getAxisX().setAxisTitle(new IAxis.AxisTitle(""));
        chart.getAxisY().setAxisTitle(new IAxis.AxisTitle(""));
    }

    private static void yAxisPrice(Chart2D chart) {

        IAxis yAxis = chart.getAxisY();

        //faded grid
        yAxis.setPaintGrid(true);

        yAxis.setFormatter(new LabelFormatterNumber(new PriceFormat(yAxis)));
        yAxis.setAxisScalePolicy(new AxisScalePolicyBestFitByPixel());

    }

    private static void yAxisVolume(Chart2D chart) {

        IAxis yAxis = chart.getAxisY();

        //faded grid
        yAxis.setPaintGrid(true);

        yAxis.setFormatter(new LabelFormatterNumber(new TradingVolumeFormat(yAxis)));
        yAxis.setAxisScalePolicy(new AxisScalePolicyBestFitByPixel());

    }

    private static void xAxisTime(Chart2D chart) {

        IAxis xAxis = chart.getAxisX();

        //faded grid
        xAxis.setPaintGrid(true);
        xAxis.setFormatter(new LabelFormatterDate(new SimpleDateFormat("MM/dd/yy")));//this format is ignored
        xAxis.setAxisScalePolicy(new AxisScalePolicySkipTimeBlocks());

    }

    private void importCSV(String fileName, int limit, Consumer<Map<String, String>> rowConsumer) throws Exception {

        String csv_pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", resourcesRootDir = "/";

        InputStream resourceAsStream = getClass().getResourceAsStream(resourcesRootDir+fileName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {

            LinkedList<String> headers = new LinkedList<>();

            int lineNum = 0;
            String line;
            while ((line = reader.readLine()) != null && (limit == -1 ? TRUE : limit-- > 0)) {

                try {

                    lineNum++;
                    String[] contents = line.split(csv_pattern);

                    //consume first row as headers
                    if(headers.isEmpty()) {

                        for(String header : contents)
                            headers.add(header);

                    } else {

                        if(contents.length != headers.size())
                            throw new Exception("Column Count Mismatch on Line #"+lineNum);

                        Map<String, String> row = new LinkedHashMap<>();
                        for(int column = 0; column < headers.size(); column++)
                            row.put(headers.get(column), contents[column]);

                        rowConsumer.accept(row);

                    }

                } catch (Exception ex) {
                    throw ex;
                }

            }

        } catch (Exception ex) {
            throw ex;
        }

    }


}
