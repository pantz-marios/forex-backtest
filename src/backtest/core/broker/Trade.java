package backtest.core.broker;

import java.util.Date;



/**
 *
 * A Trade is created when an Order is executed. A Trade cannot be updated or closed, except
 * when a Position closes which will automatically close all it's Trades.
 *
 */
public class Trade
{
    private String ticker;
    private int units;
    private PositionSide side;
    private double entryPrice;          // the price the Trade has been executed/created
    private Date openDate;              // the Date the trade opened
    private Date closeDate;             // the Date the trade closed



    public Trade(String ticker, int units, PositionSide side, double entryPrice, Date openDate)
    {
        this.ticker = ticker;
        this.units = units;
        this.side = side;
        this.entryPrice = entryPrice;
        this.openDate = openDate;
    }



    public boolean isClosed()
    {
        return closeDate == null;
    }

    public String getTicker()
    {
        return ticker;
    }

    public int getUnits()
    {
        return units;
    }

    public PositionSide getSide()
    {
        return side;
    }

    public double getEntryPrice()
    {
        return entryPrice;
    }

    public Date getOpenDate()
    {
        return openDate;
    }

    public Date getCloseDate()
    {
        return closeDate;
    }


    public void setCloseDate(Date closeDate)
    {
        this.closeDate = closeDate;
    }



    @Override
    public String toString()
    {
        return "Trade   ,   ticker=" + ticker + "   ,   units=" + units + "   ,   side=" + side + "   ,   entryPrice=" + entryPrice + "   ,   openDate=" + openDate + "   ,   closeDate=" + closeDate;
    }
}