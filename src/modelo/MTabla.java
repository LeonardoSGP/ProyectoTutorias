package modelo;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class MTabla extends AbstractTableModel {

    private List<Tutor> tutores;
    private String encabezados[] = {"Num Tarj", "Nombre", "Carrera", "Dias"};

    public MTabla(List<Tutor> tutores) {
        this.tutores = tutores;
    }

    @Override
    public int getRowCount() {
        if (tutores != null) {
            return tutores.size();
        }
        return 0;
    }

    @Override
    public int getColumnCount() {
        return encabezados.length;
    }

    @Override
    public String getColumnName(int i) {
        return encabezados[i];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return tutores.get(rowIndex).getNumtar();
            case 1:
                return tutores.get(rowIndex).getNombre();
            case 2:
                return tutores.get(rowIndex).getCarrera();
            default:
                return tutores.get(rowIndex).getDias();
        }

    }

}
