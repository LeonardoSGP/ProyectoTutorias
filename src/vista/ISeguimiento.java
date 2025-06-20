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

public class ISeguimiento extends javax.swing.JDialog {

    private String nc;

    public ISeguimiento(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.nc = nc;
        initComponents();

        // Asignar modelo vacío para que se vean los encabezados al abrir la vista
        tablaTutorado.setModel(new MTablaTutorado(new ArrayList<>()));

        rellenar(); // Cargar los datos reales si los hay
    }

    public ISeguimiento(java.awt.Frame parent, boolean modal, String nc) {
        super(parent, modal);
        this.nc = nc;
        initComponents();
        rellenar(); // Carga el seguimiento al abrir
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tablaTutorado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaTutorado);

        javax.swing.GroupLayout ssssLayout = new javax.swing.GroupLayout(ssss);
        ssss.setLayout(ssssLayout);
        ssssLayout.setHorizontalGroup(
            ssssLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
        );
        ssssLayout.setVerticalGroup(
            ssssLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ssssLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(ISeguimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ISeguimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ISeguimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ISeguimiento.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                ISeguimiento dialog = new ISeguimiento(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel ssss;
    private javax.swing.JTable tablaTutorado;
    // End of variables declaration//GEN-END:variables
}
