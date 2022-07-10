package backtest.core;

import backtest.core.events.Event;



public interface IEventQueueAdd
{

    /**
     *
     * Add the given event to the EventQueue.
     *
     */
    void add(Event event);

}
