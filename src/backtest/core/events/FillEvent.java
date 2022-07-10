package backtest.core.events;

import backtest.core.OrderSide;
import java.util.Date;



public class FillEvent extends Event
{
    private String ticker;
    private Date fillDate;
    private OrderSide orderSide;
    private int filledQuantity;
    private double filledPrice;
    private double commission;



    public FillEvent(String ticker, Date fillDate, OrderSide orderSide, int filledQuantity, double filledPrice, double commission)
    {
        super(EventType.FILL);
        this.ticker = ticker;
        this.fillDate = fillDate;
        this.orderSide = orderSide;
        this.filledQuantity = filledQuantity;
        this.filledPrice = filledPrice;
        this.commission = commission;
    }



    public String getTicker()
    {
        return ticker;
    }

    public Date getFillDate()
    {
        return fillDate;
    }

    public OrderSide getOrderSide()
    {
        return orderSide;
    }

    public int getFilledQuantity()
    {
        return filledQuantity;
    }

    public double getFilledPrice()
    {
        return filledPrice;
    }

    public double getCommission()
    {
        return commission;
    }



    @Override
    public String toString()
    {
        return "FillEvent   ,   ticker=" + ticker + "   ,   fillDate=" + fillDate + "   ,   orderSide=" + orderSide + "   ,   filledQuantity=" + filledQuantity + "   ,   filledPrice=" + filledPrice + "   ,   commission=" + commission;
    }
}