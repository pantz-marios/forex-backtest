package backtest.core.events;

import java.util.Date;



public class BarEvent extends Event
{
    private String ticker;
    private Date date;
    private double open, high, low, close, volume;



    public BarEvent(String ticker, Date date, double open, double high, double low, double close, double volume)
    {
        super(EventType.BAR);
        this.ticker = ticker;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }



    public String getTicker()
    {
        return ticker;
    }

    public Date getDate()
    {
        return date;
    }

    public double getOpen()
    {
        return open;
    }

    public double getHigh()
    {
        return high;
    }

    public double getLow()
    {
        return low;
    }

    public double getClose()
    {
        return close;
    }

    public double getVolume()
    {
        return volume;
    }



    @Override
    public String toString()
    {
        return "BarEvent   ,   ticker=" + ticker + "   ,   date=" + date + "   ,   open=" + open + "   ,   high=" + high + "   ,   low=" + low + "   ,   close=" + close + "   ,   volume=" + volume;
    }
}