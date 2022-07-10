package datatable.csv_reader;

import datatable.Column;
import datatable.DataTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;



public class CsvReader
{

    /**
     *
     * Read the given csv file and create a new Table from it's data.
     *
     */
    public static DataTable read(String csvFilePath)
    {
        return read(csvFilePath, new CsvReaderOptions(null, null, null));
    }

    /**
     *
     * Read the given csv file and create a new Table from it's data.
     *
     */
    public static DataTable read(String filePath, CsvReaderOptions options)
    {
        DataTable table = new DataTable();
        final String columnDelimeter = options.getColumnDelimeter();


        try
        {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            List<String> lines = new LinkedList<>();
            String headerLine = null;


            // ignore first line
            if(options.ignoreFirstLine())
                br.readLine();

            // use first line as columns' names
            if(options.firstLineAsColumnNames())
            {
                headerLine = br.readLine();
            }

            // read all lines
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);


            // find the maximum number of columns
            int maxCols = 0;
            for(String l : lines)
            {
                int lineColsCount = l.split(columnDelimeter).length;
                if(lineColsCount > maxCols)
                    maxCols = lineColsCount;
            }


            // if 'firstLineAsColumnNames' option is set, get column names from the first line
            String[] headerNames = null;
            if(headerLine != null)
                headerNames = headerLine.split(columnDelimeter);


            // create Table Columns
            for(int i=0 ; i<maxCols ; i++)
            {
                String columnName = headerNames == null ? i+"" : headerNames[i];
                Column c = new Column(columnName);
                table.addColumn(c);
            }


            // fill Columns with data
            for(String l : lines)
            {
                String[] values = l.split(columnDelimeter);

                for(int colIndex=0 ; colIndex<maxCols ; colIndex++)
                {
                    if(colIndex < maxCols)
                    {
                        Object value;

                        if(options.getRowParser() == null)
                            value = values[colIndex];
                        else
                            value = options.getRowParser().parse(colIndex, values[colIndex]);

                        table.getColumn(colIndex).addValue(value);
                    }
                    else
                        table.getColumn(colIndex).addValue(null);
                }
            }
            br.close();
        }
        catch (Exception ex)
        {
            System.out.println("Couldn't read from the given file.");
        }

        return table;
    }

}