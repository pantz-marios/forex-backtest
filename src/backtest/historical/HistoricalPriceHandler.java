package backtest.historical;

import backtest.core.IEventQueueAdd;
import backtest.core.IPriceHandler;
import backtest.core.events.BarEvent;
import datatable.Column;
import datatable.DataTable;
import datatable.csv_reader.CsvReader;
import datatable.csv_reader.CsvReaderOptions;
import datatable.csv_reader.ICsvRowParser;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;



public class HistoricalPriceHandler implements IPriceHandler
{
    private String ticker;
    private String file;
    private DataTable data;
    private String datesColumn, openColumn, highColumn, lowColumn, closeColumn, volumeColumn;
    private int priceIndex;
    private boolean dataEnded;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    private static final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);



    public HistoricalPriceHandler(String file, String ticker)
    {
        this.file = file;
        this.ticker = ticker;
    }



    @Override
    public void init()
    {
        this.priceIndex = 0;
        this.dataEnded = false;
        data = loadData(file);
        datesColumn = "Datetime";
        openColumn = "Open";
        highColumn = "High";
        lowColumn = "Low";
        closeColumn = "Close";
        volumeColumn = "Volume";
    }

    @Override
    public void next(IEventQueueAdd eventQueue)
    {
        if(priceIndex >= data.getRowCount()-1)
        {
            dataEnded = true;
            return;
        }

        // create BarEvent and add it to EventQueue
        Date date = (Date) data.getValue(datesColumn, priceIndex);
        double open = (double) data.getValue(openColumn, priceIndex);
        double high = (double) data.getValue(highColumn, priceIndex);
        double low = (double) data.getValue(lowColumn, priceIndex);
        double close = (double) data.getValue(closeColumn, priceIndex);
        double volume = (double) data.getValue(volumeColumn, priceIndex);

        eventQueue.add(new BarEvent(ticker, date, open, high, low, close, volume));

        priceIndex++;
    }

    @Override
    public boolean isEnded()
    {
        return dataEnded;
    }



    private DataTable loadData(String filePath)
    {
        // create Table and fill it from csv file by using csv options, custom value parsing and cell factory
        final DataTable data = CsvReader.read(filePath, new CsvReaderOptions(null, true, null, getCustomRowParser()));

        // create another column for datetime
        Column<Date> datetimeCol = new Column<>("Datetime");
        for(int i=0 ; i<data.getRowCount() ; i++)
        {
            GregorianCalendar dateCal = new GregorianCalendar();
            GregorianCalendar timeCal = new GregorianCalendar();
            dateCal.setTime((Date) data.getValue("Date", i));
            timeCal.setTime((Date) data.getValue("Timestamp", i));
            GregorianCalendar dateTimeCal = new GregorianCalendar(dateCal.get(Calendar.YEAR), dateCal.get(Calendar.MONTH), dateCal.get(Calendar.DAY_OF_MONTH), timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE), timeCal.get(Calendar.SECOND));

            datetimeCol.addValue(dateTimeCal.getTime());
        }
        data.addColumn(datetimeCol);

        return data;
    }

    private static ICsvRowParser getCustomRowParser()
    {
        return new ICsvRowParser() {
            @Override
            public Object parse(int columnIndex, String value) {
                try
                {
                    switch (columnIndex)
                    {
                        case 0:
                            return dateFormat.parse(value);
                        case 1:
                            return timeFormat.parse(value);
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            return Double.parseDouble(value);
                    }

                }
                catch (ParseException e)
                {
                    return null;
                }

                return null;
            }
        };

    }

}