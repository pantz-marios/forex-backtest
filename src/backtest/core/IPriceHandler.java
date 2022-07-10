package backtest.core;



public interface IPriceHandler
{

    void init();

    /**
     *
     * Add next BAR or TICK event to the EventsQueue.
     *
     */
    void next(IEventQueueAdd eventQueue);

    /**
     *
     * Return whether there are no more prices. For live data, this should not return true if the there are no data
     * right now, instead it should return true if the connection is closed.
     *
     */
    boolean isEnded();
}