package vista;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import modelo.Tutor;

public class IMenu extends javax.swing.JDialog {

    private ITutorado vistaTutorado;
    private ITutor vistaTutor;
    private ICita vistaCita;
    private ICitaCoordinador vistaCitaCoordinador;
    private ITutoria vistaTutoria;
    private int Seleccionada;
    private String tipoUsuario;
    private Tutor tutorActual;
    private String tutoradoNC;
    private String nombreUsuario;

    public IMenu(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);

        tabMenu.addChangeListener(e -> {
            int index = tabMenu.getSelectedIndex();
            if (index >= 0) {
                JPanel panel = (JPanel) tabMenu.getComponentAt(index);
                if (panel.getComponent(0) instanceof JPanel vistaPanel) {
                    var comp = vistaPanel.getComponent(0);

                    if (comp instanceof ITutorado tutorado) {
                        tutorado.actualizarTutores(); // Refresca tutorados
                    } else if (comp instanceof ICitaCoordinador cita) {
                        cita.actualizarTutores(); // Refresca citas si se modificó un tutor
                    } else if (comp instanceof ITutoria tutoria) {
                        tutoria.actualizarTutoresYCitas(); // Refresca tutores y citas
                    }
                }
            }
        });
            addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); 
            }
        });

    }

    private void initTabs() {
        tabMenu.removeAll();
        Seleccionada = 0;

        // Instancia las vistas
        vistaTutorado = new ITutorado(null, true);
        vistaTutor = new ITutor(null, true);
        vistaTutoria = new ITutoria(null, true);
        vistaCitaCoordinador = new ICitaCoordinador(null, true);

        // Conecta ITutor con ITutorado
        vistaTutor.setITutorado(vistaTutorado);
        vistaTutor.setMenu(this);

        // Agrega las vistas como pestañas
        tabMenu.addTab("Tutores", crearPanelVista(vistaTutor));
        tabMenu.addTab("Tutorados", crearPanelVista(vistaTutorado));
        tabMenu.addTab("Citas", crearPanelVista(vistaCitaCoordinador));
        tabMenu.addTab("Tutorías", crearPanelVista(vistaTutoria));
        tabMenu.addTab("Seguimiento", crearPanelVista(new SeguimientoCoordinador(null, true)));
    }

    private JPanel crearPanelVista(JDialog vista) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(vista.getContentPane(), BorderLayout.CENTER);
        return panel;
    }

    public void setUsuario(String tipo, Object datosUsuario) {
        this.tipoUsuario = tipo;
        lblUsuario.setText(tipo + ": " + (datosUsuario instanceof String ? datosUsuario : ((Tutor) datosUsuario).getNombre()));

        if (tipo.equals("Tutor")) {
            Tutor tutor = (Tutor) datosUsuario;
            tabMenu.removeAll();
            tabMenu.addTab("Citas", crearPanelVista(new ICita(null, true, tutor)));
            tabMenu.addTab("Tutorías", crearPanelVista(new ITutoria(null, true, tutor))); // Si ITutoria también lo necesita
        } else if (tipo.equals("Coordinador")) {
            initTabs();
        } else if (tipo.equals("Tutorado")) {
            initTabs();// Tu código existente para el tutorado
        }
    }

    public void setUsuario(String tipo, String nombre, String nc) {
        this.tipoUsuario = tipo;
        this.tutoradoNC = nc;
        lblUsuario.setText(nombre);
        tabMenu.removeAll();

        if (tipo.equals("Tutorado")) {
            this.tutoradoNC = nc;
            tabMenu.addTab("Seguimiento", crearPanelVista(new ISeguimiento(null, true, nc)));
        } else if (tipo.equals("Tutor")) {
            tabMenu.addTab("Citas", crearPanelVista(new ICita(null, true)));
            tabMenu.addTab("Tutorías", crearPanelVista(new ITutoria(null, true)));
        } else if (tipo.equals("Coordinador")) {
            initTabs();
        }
    }

    public void agregarPestana(String titulo, JDialog dialogo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(dialogo.getContentPane(), BorderLayout.CENTER);
        tabMenu.addTab(titulo, panel);
    }

    public ICitaCoordinador getVistaCitaCoordinador() {
        return vistaCitaCoordinador;
    }

    public ITutoria getVistaTutoria() {
        return vistaTutoria;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        tabMenu = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setText("Sistema Tutorias");

        tabMenu.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tabMenuMouseExited(evt);
            }
        });

        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPanel2MouseExited(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 765, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        tabMenu.addTab("Tutores", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 765, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        tabMenu.addTab("Tutorados", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 765, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        tabMenu.addTab("Cita", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 765, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );

        tabMenu.addTab("Tutoria", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 753, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 452, Short.MAX_VALUE)
        );

        tabMenu.addTab("Segumiento ", jPanel6);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setText("Usuario");

        jButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton1.setText("Cerrar Sesion");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(169, 169, 169)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(99, 99, 99))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(tabMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 849, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                            .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)))
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        dispose();
        SistemaTutorias st = new SistemaTutorias(new javax.swing.JFrame(), true);
        st.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jPanel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseExited

    }//GEN-LAST:event_jPanel2MouseExited

    private void tabMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabMenuMouseExited


    }//GEN-LAST:event_tabMenuMouseExited

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
            java.util.logging.Logger.getLogger(IMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IMenu dialog = new IMenu(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JTabbedPane tabMenu;
    // End of variables declaration//GEN-END:variables
}
