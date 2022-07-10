package backtest.core.events;

import backtest.core.OrderSide;
import java.util.Date;



public class NotFillEvent extends Event
{
    private String ticker;
    private Date date;
    private OrderSide orderSide;
    private double requiredMargin;
    private double freeMargin;
    private String msg;



    public NotFillEvent(String ticker, Date date, OrderSide orderSide, double requiredMargin, double freeMargin, String msg)
    {
        super(EventType.NOT_FILL);
        this.ticker = ticker;
        this.date = date;
        this.orderSide = orderSide;
        this.requiredMargin = requiredMargin;
        this.freeMargin = freeMargin;
        this.msg = msg;
    }



    public String getTicker()
    {
        return ticker;
    }

    public Date getDate()
    {
        return date;
    }

    public OrderSide getOrderSide()
    {
        return orderSide;
    }

    public double getRequiredMargin()
    {
        return requiredMargin;
    }

    public double getFreeMargin()
    {
        return freeMargin;
    }

    public String getMsg()
    {
        return msg;
    }



    @Override
    public String toString()
    {
        return "NotFillEvent   ,   ticker=" + ticker + "   ,   date=" + date + "   ,   orderSide=" + orderSide + "   ,   requiredMargin=" + requiredMargin + "   ,   freeMargin=" + freeMargin + "   ,   msg=" + msg;
    }
}