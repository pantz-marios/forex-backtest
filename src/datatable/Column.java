package datatable;

import java.util.ArrayList;
import java.util.Arrays;



public class Column<T>
{
    private String name;
    private ArrayList<T> values;



    public Column(String name, T... values)
    {
        this.name = name;
        this.values = new ArrayList<>(Arrays.asList(values));
    }



    public String getName()
    {
        return name;
    }

    public ArrayList<T> getValues()
    {
        return values;
    }

    public void addValue(T value)
    {
        values.add(value);
    }

}