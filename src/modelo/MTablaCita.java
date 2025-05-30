package modelo;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

public class MTablaCita extends AbstractTableModel {

    private ArrayList<DatosTablaCitas> datosCitas;
    String encabezados[] = {"Tutorado", "Asistencia", "Accion"};
    Class clasesC[] = {String.class, Boolean.class, String.class};

    public MTablaCita(ArrayList mtc) {
        datosCitas = mtc;
    }

    @Override
    public boolean isCellEditable(int r, int c) {
        // Evita que la última fila sea editable
        if (r >= datosCitas.size()) {
            return false;
        }

        if (c == 1) {
            return true; // Asistencia siempre editable
        }
        if (c == 2) {
            return datosCitas.get(r).getAsistencia(); // Solo si asistencia es true
        }
        return false; // Tutorado no editable
    }

    @Override
    public String getColumnName(int c) {
        return encabezados[c];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return clasesC[columnIndex];
    }

    @Override
    public int getRowCount() {
        if (datosCitas.isEmpty()) {
            return 0;  // No mostrar ni una fila si está vacío
        }
        return datosCitas.size() + 1;  // Mostrar la fila adicional solo si hay datos
    }

    @Override
    public int getColumnCount() {
        return encabezados.length;
    }

    @Override
    public Object getValueAt(int r, int c) {
        if (r < datosCitas.size()) {
            switch (c) {
                case 0:
                    return datosCitas.get(r).getT().getNombre();
                case 1:
                    return datosCitas.get(r).getAsistencia();
                case 2:
                    return datosCitas.get(r).getAccion();
                default:
                    return null;

            }
        } else if (c == 0) {
            return "Asistencia: " + asistencia();
        }
        return null;

    }

    public int asistencia() {
        int nta = 0;
        for (int a = 0; a < datosCitas.size(); a++) {
            if (datosCitas.get(a).getAsistencia()) {
                nta++;
            }
        }
        return nta;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DatosTablaCitas datos = datosCitas.get(rowIndex);

        switch (columnIndex) {
            case 1: // Asistencia
                boolean nuevaAsistencia = (Boolean) aValue;
                datos.setAsistencia(nuevaAsistencia);

                if (!nuevaAsistencia) {
                    // Si se desmarca, limpiar acción
                    datos.setAccion("");
                    fireTableCellUpdated(rowIndex, 2); // Refresca celda de acción
                }

                fireTableCellUpdated(rowIndex, 1); // Refresca celda de asistencia
                break;

            case 2: // Acción
                // Solo permitir si la asistencia está marcada
                if (datos.getAsistencia()) {
                    datos.setAccion((String) aValue);
                    fireTableCellUpdated(rowIndex, 2);
                }
                break;
        }
        // Refrescar la última fila para que el conteo se actualice inmediatamente
        fireTableRowsUpdated(datosCitas.size(), datosCitas.size());
    }

    public ArrayList<DatosTablaCitas> getDatos() {
        return datosCitas;
    }

}
