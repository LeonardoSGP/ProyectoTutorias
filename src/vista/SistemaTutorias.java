package vista;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import modelo.Tutor;
import modelo.Tutorado;

public class SistemaTutorias extends javax.swing.JDialog {

    public SistemaTutorias(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mostrar1.setEnabled(false);
        mostrar2.setEnabled(false);
        setLocationRelativeTo(null);
        combousuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Selecciona un usuario", "Tutorado", "Tutor", "Coordinador"
        }));

        // Escuchador de cambios en el ComboBox
        combousuario.addActionListener(e -> ajustarCampos());

        // Estado inicial
        ajustarCampos();
          // Si además quieres terminar toda la app al cerrar:
    addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); 
            }
        });
    }

    private void ajustarCampos() {
        String usuario = (String) combousuario.getSelectedItem();

        switch (usuario) {
            case "Tutorado":
                passwordNC.setEnabled(true);
                passwordTarjeta.setEnabled(false);
                mostrar1.setEnabled(false);
                mostrar2.setEnabled(true);
                passwordTarjeta.setText("");
                break;
            case "Tutor":
                passwordTarjeta.setEnabled(true);
                passwordNC.setEnabled(false);
                mostrar2.setEnabled(false);
                mostrar1.setEnabled(true);
                passwordNC.setText("");
                break;
            case "Coordinador":
                passwordTarjeta.setEnabled(false);
                passwordNC.setEnabled(false);
                mostrar2.setEnabled(false);
                mostrar1.setEnabled(false);
                break;
            default:
                passwordTarjeta.setEnabled(false);
                passwordNC.setEnabled(false);
                passwordNC.setText("");
                passwordTarjeta.setText("");
                break;
        }
    }

    private Tutorado buscarTutoradoPorNC(String nc) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Tutorado> query = em.createQuery("SELECT t FROM Tutorado t WHERE t.nc = :nc", Tutorado.class);
            query.setParameter("nc", nc);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
            emf.close();
        }
    }

    private Tutor buscarTutorPorTarjeta(int numtar) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Tutor> query = em.createQuery("SELECT t FROM Tutor t WHERE t.numtar = :numtar", Tutor.class);
            query.setParameter("numtar", numtar); // ya es int, sin problema
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
            emf.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        combousuario = new javax.swing.JComboBox<>();
        aceptar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        labelCredencial = new javax.swing.JLabel();
        passwordTarjeta = new javax.swing.JPasswordField();
        passwordNC = new javax.swing.JPasswordField();
        mostrar1 = new javax.swing.JRadioButton();
        mostrar2 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setText("Sistema de Tutorias");

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Usuario:");

        combousuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tutorado", "Tutor", "Coordinador" }));

        aceptar.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        aceptar.setText("Aceptar ");
        aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Numero de Tarjeta:");

        labelCredencial.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        labelCredencial.setText("Numero de Control:");

        passwordTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordTarjetaActionPerformed(evt);
            }
        });

        mostrar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrar1ActionPerformed(evt);
            }
        });

        mostrar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrar2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addComponent(jLabel1)
                        .addGap(28, 28, 28)
                        .addComponent(combousuario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(labelCredencial))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(passwordTarjeta, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                                .addComponent(passwordNC)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mostrar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mostrar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(54, 54, 54))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel2)
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(combousuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mostrar1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(passwordTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelCredencial)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mostrar2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordNC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)))
                .addGap(35, 35, 35)
                .addComponent(aceptar)
                .addContainerGap(47, Short.MAX_VALUE))
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
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mostrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrar1ActionPerformed
        boolean showing = !mostrar1.isSelected();
        if (showing) {
            passwordTarjeta.setEchoChar('*');
        } else {
            passwordTarjeta.setEchoChar((char) 0);
        }
        showing = !showing;


    }//GEN-LAST:event_mostrar1ActionPerformed

    private void mostrar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrar2ActionPerformed
        boolean showing = !mostrar2.isSelected();
        if (showing) {
            passwordNC.setEchoChar('*');
        } else {
            passwordNC.setEchoChar((char) 0);
        }
        showing = !showing;
    }//GEN-LAST:event_mostrar2ActionPerformed

    private void passwordTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordTarjetaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordTarjetaActionPerformed

    private void aceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarActionPerformed
        String tipoUsuario = (String) combousuario.getSelectedItem();
        String input = "";
        // Validación: que no esté en blanco ni sea "Selecciona un usuario"
        if (tipoUsuario == null || tipoUsuario.equals("Selecciona un usuario")) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un tipo de usuario válido.");
            return;
        }

        if (tipoUsuario.equals("Tutorado")) {
            input = new String(passwordNC.getPassword()).trim();

            if (!input.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "El Número de Control debe tener exactamente 8 dígitos numéricos.");
                return;
            }

            Tutorado encontrado = buscarTutoradoPorNC(input);
            if (encontrado == null) {
                JOptionPane.showMessageDialog(this, "Número de Control no encontrado.");
                return;
            }
            // Aquí puedes abrir la vista ITutorado u otra lógica
            JOptionPane.showMessageDialog(this, "Bienvenido, " + encontrado.getNombre());

            // Abrir el menú principal con los datos del tutorado
            IMenu menu = new IMenu(null, true);
            menu.setUsuario("Tutorado", encontrado.getNombre(), encontrado.getNc());
            this.dispose();
            menu.setVisible(true);

        } else if (tipoUsuario.equals("Tutor")) {
            input = new String(passwordTarjeta.getPassword()).trim();

            if (!input.matches("\\d{8}")) {
                JOptionPane.showMessageDialog(this, "El Número de Tarjeta debe tener exactamente 8 dígitos numéricos.");
                return;
            }

            int numtar = Integer.parseInt(input); // CORRECCIÓN AQUÍ
            Tutor encontrado = buscarTutorPorTarjeta(numtar); // CORRECCIÓN AQUÍ

            if (encontrado == null) {
                JOptionPane.showMessageDialog(this, "Número de Tarjeta no encontrado.");
                return;
            }
            // Aquí puedes abrir la vista ITutorado u otra lógica
            JOptionPane.showMessageDialog(this, "Bienvenido, " + encontrado.getNombre());

            // Abrir el menú principal con los datos del tutor
            IMenu menu = new IMenu(null, true);
            menu.setUsuario("Tutor", encontrado); // Pasamos el objeto Tutor directamente
            this.dispose(); // Cerrar login
            menu.setVisible(true);

        } else if (tipoUsuario.equals("Coordinador")) {
            // Solicita la contraseña mediante un JOptionPane con campo de contraseña
            JPasswordField pwd = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(this, pwd, "Ingresa la contraseña de Coordinador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String contrasenaIngresada = new String(pwd.getPassword()).trim();
                if (!contrasenaIngresada.equals("Coordinador18")) {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta.");
                    return;
                }

                JOptionPane.showMessageDialog(this, "Bienvenido, Coordinador");

                // Abrir el menú principal con los datos del coordinador
                IMenu menu = new IMenu(null, true);
                menu.setUsuario("Coordinador", "Coordinador", null);

                this.dispose(); // Cerrar login
                menu.setVisible(true);
            } else {
                // Canceló el diálogo
                JOptionPane.showMessageDialog(this, "Acceso cancelado.");
            }
        }
    }//GEN-LAST:event_aceptarActionPerformed

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
            java.util.logging.Logger.getLogger(SistemaTutorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SistemaTutorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SistemaTutorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SistemaTutorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SistemaTutorias dialog = new SistemaTutorias(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton aceptar;
    private javax.swing.JComboBox<String> combousuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelCredencial;
    private javax.swing.JRadioButton mostrar1;
    private javax.swing.JRadioButton mostrar2;
    private javax.swing.JPasswordField passwordNC;
    private javax.swing.JPasswordField passwordTarjeta;
    // End of variables declaration//GEN-END:variables
}
