package main;

import backtest.core.IEventQueueAdd;
import backtest.core.OrderSide;
import backtest.core.OrderType;
import backtest.core.broker.IBroker;
import backtest.core.events.BarEvent;
import backtest.core.events.FillEvent;
import backtest.core.events.NotFillEvent;
import backtest.core.events.OrderEvent;
import backtest.core.Strategy;
import java.util.Random;



public class Strategy1 implements Strategy
{
    protected IBroker broker;
    private final double riskPercent = 0.02;

    private int priceIndex = 0;
    private Random random = new Random();



    @Override
    public void init(IBroker broker)
    {
        this.broker = broker;
    }

    @Override
    public void onBar(IEventQueueAdd eventQueue, BarEvent event)
    {
        // create and add an OrderEvent to EventQueue every 5 TICK or BAR events
        if((priceIndex+1) % 5 == 0)
        {
            // create and add an OrderEvent to EventQueue
            int units = (int) Math.floor(riskPercent * broker.getEquity());
            eventQueue.add(new OrderEvent(event.getTicker(), random.nextBoolean() ? OrderSide.BUY : OrderSide.SELL, OrderType.MARKET, units));
        }

        priceIndex++;
    }

    @Override
    public void onFill(FillEvent event)
    {
//        System.err.println("**********************   IMPLEMENT   TestPortfolio.onFill()    **********************");
    }

    @Override
    public void onNotFill(NotFillEvent event)
    {
//        System.err.println("**********************   IMPLEMENT   TestPortfolio.onNotFill()    **********************");
    }

}