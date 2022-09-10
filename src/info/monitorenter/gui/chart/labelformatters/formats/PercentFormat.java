package info.monitorenter.gui.chart.labelformatters.formats;

import java.text.*;

public class PercentFormat extends NumberFormat {


    final DecimalFormat percent = new DecimalFormat("#%");

    @Override
    public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {
        return percent.format(d, sb, fp);
    }

    @Override
    public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {
        return percent.format(l, sb, fp);
    }

    @Override
    public Number parse(String string, ParsePosition pp) {
        return parse(string);
    }

    @Override
    public Number parse(String string) {

        try {
            return percent.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }

}
