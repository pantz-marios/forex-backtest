package backtest.historical;

import backtest.core.*;
import backtest.core.broker.*;
import backtest.core.events.BarEvent;
import backtest.core.events.FillEvent;
import backtest.core.events.NotFillEvent;
import backtest.core.events.OrderEvent;
import utils.ConsoleColors;
import java.util.*;



public class BacktestExecutionHandler implements IExecutionHandler
{
    private Account account;
    private double lastPrice;
    private List<Position> closedPositions;
    private HashMap<String, Position> openPositions;
    private static final double MARGIN_REQUIREMENT = 0.1;   // 10%



    public BacktestExecutionHandler(String accountCurrency, double initialEquity)
    {
        this.account = new Account(accountCurrency, initialEquity);
        this.closedPositions = new LinkedList<>();
        this.openPositions = new HashMap<>();
    }



    @Override
    public void onBar(BarEvent event)
    {
        // do not do anything if there is not an open Position with the same ticker as the BarEvent's ticker
        if(!openPositions.containsKey(event.getTicker()))
            return;

        Position position = openPositions.get(event.getTicker());

        // calculate and set Position's Unrealized P/L
        double positionUnrealizedPL = calculatePositionUnrealizedPL(position, lastPrice);
        position.setUnrealizedPl(positionUnrealizedPL);

        // calculate and set Account's Unrealized P/L
        double accountUnrealizedPL = 0;
        for(Map.Entry<String, Position> openPosition : openPositions.entrySet())
            accountUnrealizedPL += openPosition.getValue().getUnrealizedPl();
        account.setUnrealizedPl(accountUnrealizedPL);

        // update the last price, this will be used later for orders' fill price
        this.lastPrice = event.getClose();
    }

    @Override
    public void executeOrder(IEventQueueAdd eventQueue, OrderEvent event)
    {
        Date orderExecutionDate = new Date();


        // execute order
        if(openPositions.containsKey(event.getTicker()))         // there is an open Position for the given OrderEvent ticker
        {
            Position position = openPositions.get(event.getTicker());    // open Position for the OrderEvent's ticker
            OrderSide orderSide = event.getOrderSide();
            boolean positionOrderSidesEqual = (orderSide == OrderSide.BUY && position.getSide() == PositionSide.LONG) ||
                                              (orderSide == OrderSide.SELL && position.getSide() == PositionSide.SHORT);


            // update Position
            if(!positionOrderSidesEqual && position.getUnits() == event.getUnits())         // close Position
            {
                // create new Trade from OrderEvent and add it to Position's trades(this is required only for debugging purposes)
                Trade newTrade = new Trade(event.getTicker(), event.getUnits(), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG: PositionSide.SHORT, lastPrice, orderExecutionDate);
                newTrade.setCloseDate(orderExecutionDate);
                position.getTrades().add(newTrade);

                // calculate and set Position's Unrealized P/L and Realized P/L
                double positionUnrealizedPL = calculatePositionUnrealizedPL(position, lastPrice);
                position.setUnrealizedPl(0);
                position.setRealizedPl(position.getRealizedPl()+positionUnrealizedPL);

                // set Position's Trades' close dates and Position's close date
                for(Trade trade : position.getTrades())
                    trade.setCloseDate(orderExecutionDate);
                position.setCloseDate(orderExecutionDate);

                // update Account's Used Margin, Balance and Realized P/L
                account.setUsedMargin(account.getUsedMargin() - position.getUsedMargin());
                account.setBalance(account.getBalance() + positionUnrealizedPL);
                account.setRealizedPl(account.getRealizedPl() + positionUnrealizedPL);

                // move Position to from 'openPositions' map to 'closedPositions' list
                openPositions.remove(position.getTicker());
                closedPositions.add(position);

                printState(ConsoleColors.RED_BOLD, "Position Closed", event, position);
            }
            else if(!positionOrderSidesEqual && position.getUnits() < event.getUnits())     // reverse Position
            {
                // calculate Required Margin
                double requiredMargin = calculateRequiredMargin(event, MARGIN_REQUIREMENT);

                // calculate the difference of the Position's Used Margin with the Required Margin of the order
                double marginDiff = Math.abs(position.getUsedMargin() - requiredMargin);

                // calculate Used Margin and Free Margin that the Account would have if the Position was closed
                double accountUsedMarginWithoutPosition = account.getUsedMargin() - position.getUsedMargin();
                double accountFreeMarginWithoutPosition = account.getEquity() - accountUsedMarginWithoutPosition;

                // check if there is enough Free Margin to open new Position
                if(accountFreeMarginWithoutPosition < marginDiff)
                {
                    // add a NotFillEvent to events' queue and return
                    eventQueue.add(new NotFillEvent(event.getTicker(), orderExecutionDate, event.getOrderSide(), requiredMargin, account.getFreeMargin(), "Order could not be filled, not enough Free Margin."));
                    return;
                }

                // because reversing a Position is the same as closing the Position and reopen it with a unit difference, i should close the Position's Trades and
                // update Position's Realized P/L and Account's Balance, Realized P/L
                {
                    // calculate and set Position's Unrealized P/L and Realized P/L
                    double positionUnrealizedPL = calculatePositionUnrealizedPL(position, lastPrice);
                    position.setUnrealizedPl(0);
                    position.setRealizedPl(position.getRealizedPl() + positionUnrealizedPL);

                    // close Position's Trades
                    for (Trade trade : position.getTrades())
                        trade.setCloseDate(orderExecutionDate);

                    // update Account's Balance and Realized P/L
                    account.setBalance(account.getBalance() + positionUnrealizedPL);
                    account.setRealizedPl(account.getRealizedPl() + positionUnrealizedPL);
                }

                // create new Trade from OrderEvent(with the difference units) and add it to Position's trades
                Trade newTrade = new Trade(event.getTicker(), Math.abs(position.getUnits() - event.getUnits()), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG: PositionSide.SHORT, lastPrice, orderExecutionDate);
                position.getTrades().add(newTrade);

                // update Position's Side, Used Margin, Units and update date
                position.setSide(newTrade.getSide());
                position.setUsedMargin(marginDiff);
                position.setUnits(newTrade.getUnits());
                position.setUpdateDate(orderExecutionDate);

                // update Account's Used Margin (Account Used Margin = Account Used Margin - Position Used Margin Before Trade + Absolute Margin Difference of Position Used Margin and Order's Required Margin)
                account.setUsedMargin(accountUsedMarginWithoutPosition + marginDiff);


                printState(ConsoleColors.BLUE_BOLD, "Position Reversed", event, position);
            }
            else if(!positionOrderSidesEqual && position.getUnits() > event.getUnits())     // remove units from Position
            {
                // calculate Required Margin
                double requiredMargin = calculateRequiredMargin(event, MARGIN_REQUIREMENT);

                // no need to check for enough Free Margin, because the Position cannot be reversed here

                // create new Trade from OrderEvent and add it to Position's trades
                Trade newTrade = new Trade(event.getTicker(), event.getUnits(), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG: PositionSide.SHORT, lastPrice, orderExecutionDate);
                position.getTrades().add(newTrade);

                // update Position's Used Margin, Units and update date
                position.setUsedMargin(position.getUsedMargin() - requiredMargin);
                position.setUnits(position.getUnits() - newTrade.getUnits());
                position.setUpdateDate(orderExecutionDate);

                // update Account's Used Margin
                account.setUsedMargin(account.getUsedMargin() - requiredMargin);

                printState(ConsoleColors.YELLOW_BOLD, "Position Updated (removed units)", event, position);
            }
            else if(positionOrderSidesEqual)                                                // add units to Position
            {
                // calculate Required Margin
                double requiredMargin = calculateRequiredMargin(event, MARGIN_REQUIREMENT);

                // check if there is enough Free Margin to open new Position
                if(account.getFreeMargin() < requiredMargin)
                {
                    // add a NotFillEvent to events' queue and return
                    eventQueue.add(new NotFillEvent(event.getTicker(), orderExecutionDate, event.getOrderSide(), requiredMargin, account.getFreeMargin(), "Order could not be filled, not enough Free Margin."));
                    return;
                }

                // create new Trade from OrderEvent and add it to Position's trades
                Trade newTrade = new Trade(event.getTicker(), event.getUnits(), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG: PositionSide.SHORT, lastPrice, orderExecutionDate);
                position.getTrades().add(newTrade);

                // update Position's Used Margin, Units and update date
                position.setUsedMargin(position.getUsedMargin() + requiredMargin);
                position.setUnits(position.getUnits() + newTrade.getUnits());
                position.setUpdateDate(orderExecutionDate);

                // update Account's Used Margin
                account.setUsedMargin(account.getUsedMargin() + requiredMargin);

                printState(ConsoleColors.CYAN_BOLD, "Position Updated (added units)", event, position);
            }
        }
        else                                                     // there is NOT an open Position for the given OrderEvent ticker
        {
            // calculate Required Margin
            double requiredMargin = calculateRequiredMargin(event, MARGIN_REQUIREMENT);

            // check if there is enough Free Margin to open new Position
            if(account.getFreeMargin() < requiredMargin)
            {
                // add a NotFillEvent to events' queue and return
                eventQueue.add(new NotFillEvent(event.getTicker(), orderExecutionDate, event.getOrderSide(), requiredMargin, account.getFreeMargin(), "Order could not be filled, not enough Free Margin."));
                return;
            }

            // create new Trade from OrderEvent
            Trade newTrade = new Trade(event.getTicker(), event.getUnits(), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG: PositionSide.SHORT, lastPrice, orderExecutionDate);

            // create a new Position(with a new Trade), set Position's Used Margin and add it to 'openPositions' map
            Position position = new Position(event.getTicker(), event.getUnits(), event.getOrderSide() == OrderSide.BUY ? PositionSide.LONG : PositionSide.SHORT, orderExecutionDate);
            position.setUsedMargin(requiredMargin);
            position.getTrades().add(newTrade);
            openPositions.put(position.getTicker(), position);

            // update Account's Used Margin
            account.setUsedMargin(account.getUsedMargin() + requiredMargin);

            printState(ConsoleColors.GREEN_BOLD, "Position Opened", event, position);
        }


        // create and add a FillEvent to EventQueue
        int filledQuantity = event.getUnits();
        double filledPrice = lastPrice;
        double commission = 0;
        eventQueue.add(new FillEvent(event.getTicker(), orderExecutionDate, event.getOrderSide(), filledQuantity, filledPrice, commission));
    }



    private double calculateRequiredMargin(OrderEvent event, double marginRequirement)
    {
        double notionalValue = 0;
        try
        {
            notionalValue = getTradeNotionalValue(event.getTicker(), account.getCurrency(), event.getUnits(), lastPrice);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        return notionalValue * marginRequirement;
    }

    private static double getTradeNotionalValue(String ticker, String accountCurrency, int units, double lastPrice) throws Exception
    {
        String baseCurrency = ticker.split("/")[0];
        String quoteCurrency = ticker.split("/")[1];

        if(accountCurrency.equals(baseCurrency))
        {
            return units;
        }
        else if(accountCurrency.equals(quoteCurrency))
        {
            return units * lastPrice;
        }

        throw new Exception("Account currency should be the base or the quote currency of the traded pair.");
    }

    private static double calculatePositionUnrealizedPL(Position position, double currentPrice)
    {
        double pl = 0;
        for(Trade trade : position.getTrades())
        {
            if(trade.isClosed())
                continue;

            pl += (trade.getSide() == PositionSide.LONG ? currentPrice - trade.getEntryPrice() : trade.getEntryPrice() - currentPrice) * trade.getUnits();
        }

        return pl;
    }



    private void printState(String color, String title, OrderEvent event, Position position)
    {
        System.out.print(color);
        System.out.println("***********************************  "+title+"  ***********************************");
        System.out.println(event);
        System.out.println(position);
        System.out.println(account);
        System.out.println("******************************************************************************************************");
        System.out.print(ConsoleColors.RESET);
    }



    @Override
    public double getEquity()
    {
        return account.getEquity();
    }

    @Override
    public String getAccountCurrency()
    {
        return account.getCurrency();
    }

}