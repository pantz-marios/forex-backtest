package backtest.core.broker;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;



/**
 *
 * A Position is the total of all open Trades for a single instrument(ticker).
 * A Position can be updated when the price changes(profit update), or one or more Trades are created, or the Position closes
 * which will result to close all it's Trades.
 *
 */
public class Position
{
    private String ticker;
    private int units;
    private PositionSide side;
    private List<Trade> trades;         // all Trades of this Position, Trades can only be closed/updated once the Position closes
    private Date openDate;              // the Date the position opened
    private Date closeDate;             // the Date the position closed
    private Date updateDate;            // the Date the position updated last time
    private double unrealizedPl;        // unrealized P/L
    private double realizedPl;          // realized P/L
    private double usedMargin;



    public Position(String ticker, int units, PositionSide side, Date openDate)
    {
        this.ticker = ticker;
        this.units = units;
        this.side = side;
        this.trades = new LinkedList<>();
        this.openDate = openDate;
        this.closeDate = null;
        this.updateDate = null;
        this.unrealizedPl = 0;
        this.realizedPl = 0;
        this.usedMargin = 0;
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

    public List<Trade> getTrades()
    {
        return trades;
    }

    public Date getOpenDate()
    {
        return openDate;
    }

    public Date getCloseDate()
    {
        return closeDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public double getUnrealizedPl()
    {
        return unrealizedPl;
    }

    public double getRealizedPl()
    {
        return realizedPl;
    }

    public double getUsedMargin()
    {
        return usedMargin;
    }


    public void setUnits(int units)
    {
        this.units = units;
    }

    public void setSide(PositionSide side)
    {
        this.side = side;
    }

    public void setCloseDate(Date closeDate)
    {
        this.closeDate = closeDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public void setUnrealizedPl(double unrealizedPl)
    {
        this.unrealizedPl = unrealizedPl;
    }

    public void setRealizedPl(double realizedPl)
    {
        this.realizedPl = realizedPl;
    }

    public void setUsedMargin(double usedMargin)
    {
        this.usedMargin = usedMargin;
    }



    @Override
    public String toString()
    {
        return "Position   ,   ticker=" + ticker + "   ,   units=" + units + "   ,   side=" + side + "   ,   trades=" + trades.size() + "   ,   openDate=" + openDate + "   ,   closeDate=" + closeDate + "   ,   updateDate=" + updateDate + "   ,   unrealizedPl=" + unrealizedPl + "   ,   realizedPl=" + realizedPl + "   ,   usedMargin=" + usedMargin;
    }
}