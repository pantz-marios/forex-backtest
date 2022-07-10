package main;

import backtest.ILogger;
import backtest.core.Backtest;
import backtest.core.Result;
import backtest.core.Strategy;
import backtest.historical.BacktestExecutionHandler;
import backtest.historical.HistoricalPriceHandler;
import java.nio.file.Paths;



/**
 *
 * TODO:   - Create tests to test BacktestExecutionHandler.onBar()  and   BacktestExecutionHandler.executeOrder().
 *         - The IBroker should return a HashMap of valid Margin Requirements per ticker(e.g HashMap<String, Double>) and the
 *           BacktestExecutionHandler should use that for getting the Margin Requirement.
 *         - Implement the computation of Backtest results.
 *         - Implement Monte Carlo simulation.
 *         - Add commisions in pips.
 *         - Add Stop Loss and Take Profit order types.
 *         - Add multi-currency support.
 *         - I should check whether a Position should be closed because of low Free Margin (losing Position).
 *
 */



public class Main
{
    public static void main(String[] args) throws Exception
    {
        // equity, account currency and backtested ticker
        double equity = 100000;
        String accountCurrency = "USD";
        String ticker = "EUR/USD";

        // load historical data
        final String fileName = "financial_data.csv";
        final String filePath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()+ "test_resources/"+fileName).toFile().getAbsolutePath();
        HistoricalPriceHandler priceHandler = new HistoricalPriceHandler(filePath, ticker);

        // create  IExecutionHandler and IStrategy
        BacktestExecutionHandler executionHandler = new BacktestExecutionHandler(accountCurrency, equity);
        Strategy strategy = new Strategy1();

        // ILogger to log events to console
        ILogger printLogger = msg -> System.out.println(msg);

        // create and run backtest
        Backtest backtest = new Backtest(printLogger, executionHandler, priceHandler, strategy, ticker);
        Result backtestResult = backtest.run();

        System.out.println("Backtest finished in  "+backtest.getElapsedTime()+" ms.");
    }
}