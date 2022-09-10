package info.monitorenter.gui.chart.axis;

public class TimeBlock {

    public double startAt, endAt;

    public TimeBlock(double startAt, double endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public double width() {
        return Math.max(startAt, endAt) - Math.min(startAt, endAt);
    }

}
