package modelo; 

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

public class AsistenciaRenderer extends JCheckBox implements TableCellRenderer {

    public AsistenciaRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // Si es la última fila, NO mostrar checkbox
        if (row >= ((MTablaCita) table.getModel()).getDatos().size()) {
            return new JLabel(""); // celda vacía
        }

        // Si no, comportamiento normal del checkbox
        setSelected(Boolean.TRUE.equals(value));
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }
}
