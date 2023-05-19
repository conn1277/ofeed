package ofeed.model;

import javax.swing.table.AbstractTableModel;

public class TableModelTags extends AbstractTableModel {
        private String[] columnNames;
        private Object[][] data;

        public TableModelTags(int paths, int tags){
            data = new Object[tags][paths];
            columnNames = new String[tags];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
                return true;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setColumnNames(String[] newColumnNames) {
            columnNames = newColumnNames;
        }
        public Object[][] getData(){
            return data;
        }
        public void setData(Object[][] newData) {
            data = newData;
        }
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
            }
        }