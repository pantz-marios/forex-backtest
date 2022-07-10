package datatable.csv_reader;



public class CsvReaderOptions
{
    private String columnDelimeter;
    private boolean firstLineAsColumnNames;
    private boolean ignoreFirstLine;
    private ICsvRowParser rowParser;

    private static final String DEFAULT_COLUMN_DELIMETER = ",";
    private static final boolean DEFAULT_FIRST_LINE_AS_COLUMN_NAMES = false;
    private static final boolean DEFAULT_IGNORE_FIRST_LINE = false;



    public CsvReaderOptions(String columnDelimeter, Boolean firstLineAsColumnNames, Boolean ignoreFirstLine, ICsvRowParser rowParser)
    {
        this.columnDelimeter = columnDelimeter != null ? columnDelimeter : DEFAULT_COLUMN_DELIMETER;
        this.firstLineAsColumnNames = firstLineAsColumnNames != null ? firstLineAsColumnNames : DEFAULT_FIRST_LINE_AS_COLUMN_NAMES;
        this.ignoreFirstLine = ignoreFirstLine != null ? ignoreFirstLine : DEFAULT_IGNORE_FIRST_LINE;
        this.rowParser = rowParser;
    }

    public CsvReaderOptions(String columnDelimeter, Boolean firstLineAsColumnNames, Boolean ignoreFirstLine)
    {
        this(columnDelimeter, firstLineAsColumnNames, ignoreFirstLine, null);
    }



    public String getColumnDelimeter()
    {
        return columnDelimeter;
    }

    public boolean firstLineAsColumnNames()
    {
        return firstLineAsColumnNames;
    }

    public boolean ignoreFirstLine()
    {
        return ignoreFirstLine;
    }

    public ICsvRowParser getRowParser()
    {
        return rowParser;
    }

}