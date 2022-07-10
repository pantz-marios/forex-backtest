package datatable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;



public class DataTable
{
    private LinkedHashMap<String, Column> columns;
    private List<ITableEventListener> tableEventListeners;



    public DataTable(Column... columns)
    {
        this.columns = getColumnsMap(columns);
        this.tableEventListeners = new LinkedList<>();
    }



    /**
     *
     * Get all the Table Columns.
     *
     */
    public LinkedHashMap<String, Column> getColumns()
    {
        return columns;
    }

    /**
     *
     * Get an array of Column types.
     *
     */
    public String[] getColumnTypes()
    {
        String[] types = new String[getColumnCount()];

        for(int colIndex=0 ; colIndex<getColumnCount() ; colIndex++)
            types[colIndex] = getValue(colIndex, 0).getClass().getSimpleName();

        return types;
    }

    /**
     *
     * Get Column at the given index.
     *
     */
    public <T> Column<T> getColumn(int index)
    {
        return columns.get(columns.keySet().toArray()[index]);
    }

    /**
     *
     * Get Column with the given name.
     *
     */
    public <T> Column<T> getColumn(String name)
    {
        return columns.get(name);
    }

    /**
     *
     * Add Column to Table.
     *
     */
    public void addColumn(Column column)
    {
        columns.put(column.getName(), column);

//        int columnIndex = getColumnIndex(column.getName());

        // notify event listeners
        for(ITableEventListener eventListener : tableEventListeners)
            eventListener.onColumnAdded(column);
    }

    /**
     *
     * Remove the given Column from the Table.
     *
     */
    public void removeColumn(String columnName)
    {
        int columnIndex = getColumnIndex(columnName);

        // remove column
        columns.remove(columnName);

        // notify event listeners
        for(ITableEventListener eventListener : tableEventListeners)
            eventListener.onColumnRemoved(columnIndex);
    }

    /**
     *
     * Remove the given Column from the Table.
     *
     */
    public void removeColumn(int columnIndex)
    {
        removeColumn(getColumn(columnIndex).getName());
    }



    /**
     *
     * Add value at the end of the given column.
     *
     */
    public <T> boolean addValue(String column, T value)
    {
        Column<T> col = columns.get(column);
        if(col == null)
            return false;

        return col.getValues().add(value);
    }

    /**
     *
     * Add value at the end of the given column.
     *
     */
    public <T> boolean addValue(int columnIndex, T value)
    {
        Column<T> col = getColumn(columnIndex);
        if(col == null)
            return false;

        return col.getValues().add(value);
    }

    /**
     *
     * Get value at the given column and row.
     *
     */
    public Object getValue(String column, int rowIndex)
    {
        Column col = columns.get(column);
        if(col == null)
            return null;

        return col.getValues().get(rowIndex);
    }

    /**
     *
     * Get value at the given column and row.
     *
     */
    public Object getValue(int columnIndex, int rowIndex)
    {
        Column col = getColumn(columnIndex);
        if(col == null)
            return null;

        return col.getValues().get(rowIndex);
    }

    /**
     *
     * Set the given value at the given column and row index.
     *
     */
    public void setValue(String column, int rowIndex, Object value)
    {
        columns.get(column).getValues().set(rowIndex, value);
    }

    /**
     *
     * Set the given value at the given column and row index.
     *
     */
    public void setValue(int columnIndex, int rowIndex, Object value)
    {
        String columnName = getColumn(columnIndex).getName();
        setValue(columnName, rowIndex, value);
    }



    /**
     *
     * Get the number of rows.
     *
     */
    public int getRowCount()
    {
        return  ((Column)((columns.values().toArray())[0])).getValues().size();
    }

    /**
     *
     * Get the number of columns.
     *
     */
    public int getColumnCount()
    {
        return  columns.values().toArray().length;
    }



    public void addTableEventListener(ITableEventListener tableEventListener)
    {
        this.tableEventListeners.add(tableEventListener);
    }

    public void removeTableEventListener(ITableEventListener tableEventListener)
    {
        this.tableEventListeners.remove(tableEventListener);
    }



    /**
     *
     * Get LinkedHashMap with key as the name of the Column and value the Column value from the given Columns array.
     *
     */
    private static LinkedHashMap<String, Column> getColumnsMap(Column[] columns)
    {
        LinkedHashMap<String, Column> columnsMap = new LinkedHashMap<>();

        for(Column c : columns)
            columnsMap.put(c.getName(), c);

        return columnsMap;
    }

    private int getColumnIndex(String columnName)
    {
        Object[] cols = columns.keySet().toArray();
        for(int c=0 ; c<cols.length ; c++)
            if(cols[c].equals(columnName))
                return c;

        return -1;
    }

}