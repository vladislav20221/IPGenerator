package ipgenerator;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.table.AbstractTableModel;

public class DataModel extends AbstractTableModel {
    // Названия стобцов таблици
    private CopyOnWriteArrayList<String> columnNames = new CopyOnWriteArrayList<>();
    // Данные полученные с базы данных
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Object>> data = new CopyOnWriteArrayList<>();
    // Разрешает редактирование ячеек таблицы
    private boolean editable = false;
    // Конструктор по умолчанию
    public DataModel () {}
    /**
     * Определяет можно ли редактировать ячейки таблицы или нет.
     * @param editable 
     */
    public void setEditable ( boolean editable ) {this.editable = editable;}    
//--------------------- Реализация собственного интрфейса ---------------------- 
//--------------- Переопределённые методы класса AbstractTableModel ------------
    @Override// Возвращает имени столбца
    public String getColumnName ( int column ) {return getColumnNames().get( column );}    
    public void addColumnName ( Object value ) {         
        getColumnNames().add( value.toString() );
        fireTableStructureChanged();
    }
    @Override// Устанавливает можно редактировать данные или нет. false нельзя, true можно
    public boolean isCellEditable ( int row, int column ) {return editable;}
    // МЕтоды обезательные для реализации
    @Override// Количесвто строк
    public int getRowCount() {return getData().size();}
    @Override// Количество столбцов
    public int getColumnCount() {return getColumnNames().size();}
    @Override// Возвращает значение ячейки
    public Object getValueAt( int row, int column ) {
        if ( getData().isEmpty() ) return null;
        if ( row < 0 || column < 0 ) return null;
        return (getData().get(row)).get(column);
    }
    public void clearRowAll () { data.clear(); }
    
    public void addRow ( Object [] value ) {
        int columncount = value.length;
        CopyOnWriteArrayList<Object> row = new CopyOnWriteArrayList<>();
        for ( int i = 0; i<columncount; i++ ) {
            row.add( value[i] );
        }
        getData().add( row );        
    }
    public void update () { 
        if ( getData().isEmpty() ) return;
        fireTableStructureChanged();
    }
    
    @Override// Метод установки значения данных
    public void setValueAt( Object value, int row, int column ) {this.getData().get(row).set( column, value );}
//-------------------- Геттеры сетеры класса -----------------------------------
    public CopyOnWriteArrayList<String> getColumnNames() { return columnNames; }
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Object>> getData() { return data; }
    
    public boolean isEditable() { return editable; }
}