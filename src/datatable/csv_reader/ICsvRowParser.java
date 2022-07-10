package datatable.csv_reader;



public interface ICsvRowParser
{
    Object parse(int columnIndex, String value);
}
