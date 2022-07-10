package datatable;



public interface ITableEventListener
{
    void onColumnAdded(Column column);
    void onColumnRemoved(int columnIndex);
}
