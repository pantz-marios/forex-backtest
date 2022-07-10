package backtest.core.events;

import backtest.core.OrderSide;
import backtest.core.OrderType;



public class OrderEvent extends Event
{
    private String ticker;
    private OrderSide orderSide;
    private OrderType orderType;
    private int units;



    public OrderEvent(String ticker, OrderSide orderSide, OrderType orderType, int units)
    {
        super(EventType.ORDER);
        this.ticker = ticker;
        this.orderType = orderType;
        this.orderSide = orderSide;
        this.units = units;
    }



    public String getTicker()
    {
        return ticker;
    }

    public OrderSide getOrderSide()
    {
        return orderSide;
    }

    public OrderType getOrderType()
    {
        return orderType;
    }

    public int getUnits()
    {
        return units;
    }



    @Override
    public String toString()
    {
        return "OrderEvent   ,   ticker=" + ticker + "   ,   orderSide=" + orderSide + "   ,   orderType=" + orderType + "   ,   units=" + units;
    }
}