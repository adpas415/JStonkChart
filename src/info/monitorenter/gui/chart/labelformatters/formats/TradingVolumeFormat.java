package info.monitorenter.gui.chart.labelformatters.formats;

import info.monitorenter.gui.chart.IAxis;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class TradingVolumeFormat extends NumberFormat {


    final DecimalFormat
        hundreds   =  new DecimalFormat("##0"),
        thousands  =  new DecimalFormat("##0'k'"),
        lowThousands  =  new DecimalFormat("##0.#'k'"),
        millions   =  new DecimalFormat("##0.#'m'");

    final private IAxis yAxis;
    public TradingVolumeFormat(IAxis yAxis) {
        this.yAxis = yAxis;
    }

    @Override
    public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {

        return format((Number) d, sb, fp);

    }

    private StringBuffer format(Number n, StringBuffer sb, FieldPosition fp) {

        double maxVisibleValue = yAxis.getMax();
        double d = n.longValue();

        if (d >= 1000000)
            return millions.format(n.doubleValue() / 1000000, sb, fp);
        else if (d >= 1000)
            if(maxVisibleValue >= 3000)
                return thousands.format(n.doubleValue() / 1000, sb, fp);
            else
                return lowThousands.format(n.doubleValue() / 1000, sb, fp);
        else
            return hundreds.format(n, sb, fp);

    }

    @Override
    public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {

        return format((Number) l, sb, fp);

    }

    @Override
    public Number parse(String string, ParsePosition pp) {
        return parse(string);
    }

    @Override
    public Number parse(String string) {

        try {
            string = string.replace("$", "");

            if (string.contains("m"))
                return Double.valueOf(string.replace("m", "")) * 1000000;
            else if (string.contains("k"))
                return Double.valueOf(string.replace("k", "")) * 1000;

            return Double.valueOf(string);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;

    }

}
