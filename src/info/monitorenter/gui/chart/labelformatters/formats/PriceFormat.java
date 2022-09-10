package info.monitorenter.gui.chart.labelformatters.formats;

import info.monitorenter.gui.chart.IAxis;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

public class PriceFormat extends NumberFormat {


    final DecimalFormat
            dollars = new DecimalFormat("'$'0.00"),
            hundreds = new DecimalFormat("'$'0"),
            thousands = new DecimalFormat("'$'0.00'k'"),
            millions = new DecimalFormat("'$'0.00'm'");


    final private IAxis axis;
    public PriceFormat(IAxis axis) {
        this.axis = axis;
    }

    private StringBuffer format(Number n, StringBuffer sb, FieldPosition fp) {

        double d = n.doubleValue();

        double range = axis.getRange().getExtent();

        if (range >= 5000000)
            return millions.format(d / 1000000, sb, fp);
        else if (range >= 5000)
            return thousands.format(d / 1000, sb, fp);
        else if (range >= 5)
            return hundreds.format(d, sb, fp);
        else
            return dollars.format(d, sb, fp);

    }

    @Override
    public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {

        return format((Number) d, sb, fp);

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
