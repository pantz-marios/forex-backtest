package backtest.core.broker;

import backtest.core.IEventQueueAdd;
import backtest.core.events.BarEvent;
import backtest.core.events.OrderEvent;



public interface IExecutionHandler extends IBroker
{

    /**
     *
     * Update state on BAR event.
     *
     */
    void onBar(BarEvent event);

    /**
     *
     * Execute order for the given OrderEvent.
     *
     */
    void executeOrder(IEventQueueAdd eventQueue, OrderEvent event);

}