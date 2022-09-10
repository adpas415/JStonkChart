package info.monitorenter.gui.chart.demos;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyPrice;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicySkipTimeBlocks;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterNumber;
import info.monitorenter.gui.chart.labelformatters.formats.PriceFormat;
import info.monitorenter.gui.chart.labelformatters.formats.TradingVolumeFormat;
import info.monitorenter.gui.chart.pointpainters.PointPainterCandleStick;
import info.monitorenter.gui.chart.pointpainters.PointPainterSimpleStick;
import info.monitorenter.gui.chart.tracepoints.CandleStick;
import info.monitorenter.gui.chart.tracepoints.SimpleStick;
import info.monitorenter.gui.chart.traces.Trace2DPoints;
import info.monitorenter.gui.chart.traces.Trace2DSimple;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class StonkChartDemo {

    final Color
        green  =  Color.decode("#28A49C"),
        red    =  Color.decode("#F05851");

    final Chart2D
        price   =  new Chart2D(),
        volume  =  new Chart2D();

    final ITrace2D
        ohlc = new Trace2DPoints(new Trace2DSimple(), new PointPainterCandleStick()),
        vol  = new Trace2DPoints(new Trace2DSimple(), new PointPainterSimpleStick());

    {

        decorate(price);
        decorate(volume);
        yAxisPrice(price);
        yAxisVolume(volume);
        xAxisTime(volume);
        setupToPaintCandles();

    }

    private void importCSV(String resourcePath, Consumer<Map<String, String>> rowConsumer) {

        String csv_pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {

            LinkedList<String> headers = new LinkedList<>();

            int lineNum = 0;
            String line;
            while ((line = reader.readLine()) != null) {

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
                    ex.printStackTrace();
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }



        //todo: import an actual Intraday 1-min bar dataset, perhaps two or three days and we'll demonstrate skipping time blocks.
        ZonedDateTime barTime = ZonedDateTime.of(LocalDate.of(1989, 7, 17), LocalTime.of(14, 30), ZoneId.of("America/New_York"));

        paintCandle(barTime.toInstant().toEpochMilli(), 1, 2, 0.50, 1.50, 500);
        barTime = barTime.plusMinutes(1);

    }

    private void populateChart() {

        String demoFile = "/info/monitorenter/gui/chart/demos/AAPL.csv";

        //Date,Low,Open,Volume,High,Close,Adjusted Close
        importCSV( demoFile, row -> {

            String dateString = row.get("Date");

            long time = ZonedDateTime.of(LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy")), LocalTime.MIDNIGHT, ZoneId.of("America/New_York")).toInstant().toEpochMilli();

            double
                open   =  Double.valueOf(row.get("Open")),
                high   =  Double.valueOf(row.get("High")),
                low    =  Double.valueOf(row.get("Low")),
                close  =  Double.valueOf(row.get("Close"));

            long volume = Long.valueOf(row.get("Volume"));

            Color color = close > high ? green : red;

            ohlc.addPoint(new CandleStick(time, open, high, low, close, color));
            vol.addPoint(new SimpleStick(time, 0, volume, color));

        });

    }


    public void paintCandle(long time, double open, double high, double low, double close, long volume) {
        Color color = close > high ? green : red;
        ohlc.addPoint(new CandleStick(time, open, high, low, close, color));
        vol.addPoint(new SimpleStick(time, 0, volume, color));
    }

    private static void decorate(Chart2D chart) {
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
    }

    private static void yAxisPrice(Chart2D chart) {

        IAxis yAxis = chart.getAxisY();

        //faded grid
        yAxis.setPaintGrid(true);

        yAxis.setFormatter(new LabelFormatterNumber(new PriceFormat(yAxis)));
        yAxis.setAxisScalePolicy(new AxisScalePolicyPrice());

    }

    private static void yAxisVolume(Chart2D chart) {

        IAxis yAxis = chart.getAxisY();

        //faded grid
        yAxis.setPaintGrid(true);

        yAxis.setFormatter(new LabelFormatterNumber(new TradingVolumeFormat(yAxis)));

    }

    private static void xAxisTime(Chart2D chart) {

        IAxis xAxis = chart.getAxisX();

        //faded grid
        xAxis.setPaintGrid(true);
        xAxis.setFormatter(new LabelFormatterDate(new SimpleDateFormat("MM/yy")));//this format is ignored
        xAxis.setAxisScalePolicy(new AxisScalePolicySkipTimeBlocks());

    }

    private void setupToPaintCandles() {

        ohlc.setName("OHLC");
        vol.setName("VOLUME");

        price.addTrace(ohlc);
        ohlc.setZIndex(1);

        volume.addTrace(vol);
        vol.setZIndex(1);

    }

}
