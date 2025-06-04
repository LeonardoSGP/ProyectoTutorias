
package vista;

import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import modelo.Cita;
import modelo.DatosTablaTutorado;
import modelo.MTablaTutorado;
import modelo.Tutorado;
import modelo.Tutoria;

public class SeguimientoCoordinador extends javax.swing.JDialog {

    private String nc;

    public SeguimientoCoordinador(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

public SeguimientoCoordinador(java.awt.Frame parent, boolean modal, String nc) {
    super(parent, modal);
    this.nc = nc;
    initComponents();

    // Asigna el modelo con encabezados, aunque esté vacío
    tablaTutorado.setModel(new MTablaTutorado(new ArrayList<>()));

    // Luego carga los datos
    rellenar();
}

    private void rellenar() {
        EntityManager em = Persistence.createEntityManagerFactory("Tutoria3PU").createEntityManager();

        try {
            // Usar el número de control que se pasó
            if (nc == null || nc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Número de control no especificado");
                return;
            }

            Query query = em.createQuery("SELECT t FROM Tutorado t WHERE t.nc = :nc");
            query.setParameter("nc", nc);

            List<Tutorado> tutorados = query.getResultList();

            if (tutorados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tutorado no encontrado");
                return;
            }

            Tutorado tutoradoSeleccionado = tutorados.get(0);

            // Buscar las tutorías asociadas
            Query queryTutorias = em.createQuery("SELECT t FROM Tutoria t WHERE t.tutorado = :tutorado");
            queryTutorias.setParameter("tutorado", tutoradoSeleccionado);
            List<Tutoria> listaTutorias = queryTutorias.getResultList();

            ArrayList<DatosTablaTutorado> listaDatos = new ArrayList<>();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            for (Tutoria tutoria : listaTutorias) {
                Cita cita = tutoria.getCita();
                String fechaFormateada = formatoFecha.format(cita.getFecha());

                DatosTablaTutorado dato = new DatosTablaTutorado(
                        fechaFormateada, // Usa la fecha formateada
                        cita.getHora(),
                        cita.getEstado(),
                        tutoria.getAcciones()
                );
                listaDatos.add(dato);
            }

            MTablaTutorado modelo = new MTablaTutorado(listaDatos);
            tablaTutorado.setModel(modelo);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tutorías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ssss = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTutorado = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtNC = new javax.swing.JTextField();
        btnAceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tablaTutorado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Hora", "Asistencia", "Acciones"
            }
        ));
        jScrollPane1.setViewportView(tablaTutorado);

        jLabel1.setText("Tutorado");

        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ssssLayout = new javax.swing.GroupLayout(ssss);
        ssss.setLayout(ssssLayout);
        ssssLayout.setHorizontalGroup(
            ssssLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ssssLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNC, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAceptar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
        );
        ssssLayout.setVerticalGroup(
            ssssLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ssssLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(ssssLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAceptar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ssss, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ssss, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        // TODO add your handling code here:
        EntityManager em = Persistence.createEntityManagerFactory("Tutoria3PU").createEntityManager();

        try {
            // Obtener el número de control del tutorado desde lblNC
            String ncStr = txtNC.getText().trim();
            if (ncStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Número de control no especificado");
                return;
            }

            int numeroControl = Integer.parseInt(ncStr);

            // Buscar al tutorado por número de control
            String nc = txtNC.getText();
            Query query = em.createQuery("SELECT t FROM Tutorado t WHERE t.nc = :nc");
            query.setParameter("nc", nc);

            List<Tutorado> tutorados = query.getResultList();

            if (tutorados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tutorado no encontrado");
                return;
            }

            Tutorado tutoradoSeleccionado = tutorados.get(0);

            // Obtener todas las tutorías asociadas a este tutorado
            Query queryTutorias = em.createQuery("SELECT t FROM Tutoria t WHERE t.tutorado = :tutorado");
            queryTutorias.setParameter("tutorado", tutoradoSeleccionado);
            List<Tutoria> listaTutorias = queryTutorias.getResultList();

            // Preparar los datos para la tabla
            ArrayList<DatosTablaTutorado> listaDatos = new ArrayList<>();
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

            for (Tutoria tutoria : listaTutorias) {
                Cita cita = tutoria.getCita();
                String fechaFormateada = formatoFecha.format(cita.getFecha());

                DatosTablaTutorado dato = new DatosTablaTutorado(
                        fechaFormateada, // Usa la fecha formateada
                        cita.getHora(),
                        cita.getEstado(),
                        tutoria.getAcciones()
                );
                listaDatos.add(dato);
            }

            // Crear el modelo de tabla y asignarlo
            MTablaTutorado modelo = new MTablaTutorado(listaDatos);
            tablaTutorado.setModel(modelo);

            // (Opcional) Renderizar la columna de asistencia bonito
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tutorías: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }

    }//GEN-LAST:event_btnAceptarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SeguimientoCoordinador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeguimientoCoordinador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeguimientoCoordinador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeguimientoCoordinador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SeguimientoCoordinador dialog = new SeguimientoCoordinador(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel ssss;
    private javax.swing.JTable tablaTutorado;
    private javax.swing.JTextField txtNC;
    // End of variables declaration//GEN-END:variables
}
