package backtest.core.broker;



public class Account
{
    private String currency;        // e.g. "USD"
    private double balance;         // cash
    private double usedMargin;      // the amount of money that is used to open a position(or trade)
    private double unrealizedPl;    // unrealized P/L
    private double realizedPl;      // realized P/L
//    private double marginLevel;       // not used



    public Account(String currency, double initialBalance)
    {
        this.currency = currency;
        this.balance = initialBalance;
        this.usedMargin = 0;
        this.unrealizedPl = 0;
        this.realizedPl = 0;
    }



    public String getCurrency()
    {
        return currency;
    }

    public double getEquity()
    {
        return balance + unrealizedPl;
    }

    public double getBalance()
    {
        return balance;
    }

    public double getUsedMargin()
    {
        return usedMargin;
    }

    public double getFreeMargin()
    {
        return getEquity() - usedMargin;
    }

    public double getUnrealizedPl()
    {
        return unrealizedPl;
    }

    public double getRealizedPl()
    {
        return realizedPl;
    }



    public void setBalance(double balance)
    {
        this.balance = balance;
    }

    public void setUsedMargin(double usedMargin)
    {
        this.usedMargin = usedMargin;
    }

    public void setUnrealizedPl(double unrealizedPl)
    {
        this.unrealizedPl = unrealizedPl;
    }

    public void setRealizedPl(double realizedPl)
    {
        this.realizedPl = realizedPl;
    }



    @Override
    public String toString()
    {
        return "Account   ,   currency=" + currency + "   ,   equity=" + getEquity() + "   ,   balance=" + balance + "   ,   usedMargin=" + usedMargin + "   ,   freeMargin=" + getFreeMargin() + "   ,   unrealizedPl=" + unrealizedPl + "   ,   realizedPl=" + realizedPl;
    }
}