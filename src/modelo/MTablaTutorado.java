package modelo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MTablaTutorado extends AbstractTableModel {

    private final String[] columnas = {"Fecha", "Hora", "Asistencia", "Acciones"};
    private final List<DatosTablaTutorado> datos;

    public MTablaTutorado(List<DatosTablaTutorado> datos) {
        this.datos = datos;
    }

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DatosTablaTutorado dato = datos.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return dato.getFecha();
            case 1:
                return dato.getHora();
            case 2:
                // Determinar asistencia según si tiene acciones
                return (dato.getAcciones() != null && !dato.getAcciones().trim().isEmpty()) ? "Asistió" : "No asistió";
            case 3:
                return dato.getAcciones();
            default:
                return null;
        }
    }
}
