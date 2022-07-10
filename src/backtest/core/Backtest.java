package backtest.core;

import backtest.ILogger;
import backtest.core.events.*;
import backtest.core.broker.IExecutionHandler;
import java.util.LinkedList;
import java.util.Queue;



public class Backtest implements IEventQueueAdd
{
    private ILogger logger;
    private Queue<Event> eventQueue;
    private IPriceHandler priceHandler;
    private IExecutionHandler executionHandler;
    private Strategy strategy;
    private String ticker;                 // e.g. "EUR/USD"
    private boolean started, finished;
    private long startTime;
    private long elapsedTime;



    public Backtest(ILogger logger, IExecutionHandler executionHandler, IPriceHandler priceHandler, Strategy strategy, String ticker)
    {
        this.logger = logger;
        this.eventQueue = new LinkedList<>();
        this.executionHandler = executionHandler;
        this.priceHandler = priceHandler;
        this.strategy = strategy;
        this.ticker = ticker;
    }



    public Result run() throws Exception
    {
        // in order to run other backtest, a new instance of this class should be created
        if(started)
            throw new Exception("Tried to run multiple backtests with a single 'Backtest' class instance. For a 'Backtest' instance only one run() call is valid.");

        startTime = System.nanoTime();
        started = true;
        finished = false;
        priceHandler.init();
        strategy.init(executionHandler);


        while(!priceHandler.isEnded())
        {
            // get next TICK or BAR event and add it to EventQueue
            priceHandler.next(this);

            // process all events that have been generated in the last TICK or BAR event
            // if there are no more events, all events for the last TICK or Bar event have been processed
            while(!eventQueue.isEmpty())
            {
                // get Events from EventQueue
                Event event = eventQueue.remove();

                // log event(for debugging)
                if(logger != null)
                    logger.log(event.toString());

                // process event
                switch (event.getType())
                {
                    case TICK:
                        throw new Exception("'TICK' event is not supported.");
                    case BAR:
                        strategy.onBar(this, (BarEvent) event);
                        executionHandler.onBar((BarEvent) event);
                        break;
                    case FILL:
                        strategy.onFill((FillEvent) event);
                        break;
                    case NOT_FILL:
                        strategy.onNotFill((NotFillEvent) event);
                        break;
                    case ORDER:
                        executionHandler.executeOrder(this, (OrderEvent) event);
                        break;
                }
            }
        }

        elapsedTime = System.nanoTime() - startTime;
        elapsedTime /= 1000000;
        finished = true;

        return null;
    }

    @Override
    public void add(Event event)
    {
        if(event == null)
            return;

        eventQueue.add(event);
    }

    /**
     *
     * Get elapsed time in milliseconds. If the backtest has not been finished, return -1.
     *
     */
    public long getElapsedTime()
    {
        return finished ? elapsedTime : -1;
    }

}