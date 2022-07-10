package backtest.core;

import backtest.core.broker.IBroker;
import backtest.core.events.BarEvent;
import backtest.core.events.FillEvent;
import backtest.core.events.NotFillEvent;



public interface Strategy
{

    /**
     *
     * Perform initialization operations for the given IBroker.
     *
     */
    void init(IBroker broker);

    /**
     *
     * Handle the given BarEvent and create OrderEvents.
     *
     */
    void onBar(IEventQueueAdd eventQueue, BarEvent event);

    /**
     *
     * Handle the given FillEvent.
     *
     */
    void onFill(FillEvent event);

    /**
     *
     * Handle the given NotFillEvent.
     *
     */
    void onNotFill(NotFillEvent event);

}