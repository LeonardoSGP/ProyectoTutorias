package vista;

import control.AdmDatos;
import control.TutorJpaController;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.MTabla;
import modelo.Tutor;

public class ITutor extends javax.swing.JDialog {

    private TutorJpaController cTutor;
    private List<Tutor> tutores;
    private MTabla mt;
    private AdmDatos admDatos = new AdmDatos();
    private Tutor tutor;
    private boolean modoEditar = false;
    private int indiceSeleccionado = -1;
    private ITutorado itutorado;
    private IMenu menu;
    // <<< NUEVO: Método para inyectar ITutorado

    public void setITutorado(ITutorado itutorado) {
        this.itutorado = itutorado;
    }

    public void setMenu(IMenu menu) {
        this.menu = menu;
    }

    public ITutor(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
        cTutor = new TutorJpaController(admDatos.getEmf());
        tutores = cTutor.findTutorEntities();
        mt = new MTabla(tutores);
        tablaTutores.setModel(mt);
        tablaTutores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaTutores.getSelectedRow();
                if (fila != -1) {
                    Tutor seleccionado = tutores.get(fila);
                    numTarS.setText(String.valueOf(seleccionado.getNumtar()));
                    nombreTutor.setText(seleccionado.getNombre());
                    comboCarrera.setSelectedItem(seleccionado.getCarrera());
                    txtdias.setText(seleccionado.getDias());

                    numTarS.setEnabled(false); // Evitar editar la llave primaria
                    agregar.setText("Modificar registro");
                    modoEditar = true;
                    indiceSeleccionado = fila;
                }
            }
        });

        comboCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            " Selecciona carrera ", "Sistemas", "Electrónica", "Industrial", "Gestión Empresarial", "Civil", "Electrica", "Administracion", "Mecanica"
        }));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTutores = new javax.swing.JTable();
        agregar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nombreTutor = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        comboCarrera = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtdias = new javax.swing.JTextField();
        numTarS = new javax.swing.JTextField();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tablaTutores.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaTutores);

        agregar.setText("Agregar Registro");
        agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial Black", 1, 14)); // NOI18N
        jLabel1.setText("TUTORES");

        jLabel2.setText("No. Tarjeta");

        jLabel3.setText("Nombre");

        nombreTutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreTutorActionPerformed(evt);
            }
        });
        nombreTutor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nombreTutorKeyTyped(evt);
            }
        });

        jLabel4.setText("Carrera");

        comboCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboCarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCarreraActionPerformed(evt);
            }
        });

        jLabel5.setText("Dias");

        txtdias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdiasActionPerformed(evt);
            }
        });
        txtdias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtdiasKeyTyped(evt);
            }
        });

        numTarS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numTarSKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 704, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(numTarS)
                            .addComponent(nombreTutor, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .addComponent(comboCarrera, 0, 176, Short.MAX_VALUE)
                            .addComponent(txtdias))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79))))
            .addGroup(layout.createSequentialGroup()
                .addGap(312, 312, 312)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(76, 76, 76)
                        .addComponent(agregar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(numTarS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(nombreTutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtdias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(comboCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarActionPerformed
        String numTarjetaTexto = numTarS.getText().trim();

        if (numTarjetaTexto.length() != 8) {
            JOptionPane.showMessageDialog(this, "El número de tarjeta debe tener exactamente 8 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numTarjeta = 0;
        try {
            for (char c : numTarjetaTexto.toCharArray()) {
                if (!Character.isDigit(c)) {
                    JOptionPane.showMessageDialog(this, "El número de tarjeta solo debe contener dígitos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            numTarjeta = Integer.parseInt(numTarjetaTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número de tarjeta válido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = nombreTutor.getText().trim();
        String carreraSeleccionada = (String) comboCarrera.getSelectedItem();
        String dias = txtdias.getText().trim();
        // Validar que solo se ingresen días del 1 al 5
        try {
            int dia = Integer.parseInt(dias);
            if (dia < 1 || dia > 5) {
                JOptionPane.showMessageDialog(this, "Solo se permiten días del 1 al 5.", "Error en días", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo 'días' solo debe contener números del 1 al 5.", "Error en días", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nombre.isEmpty() || carreraSeleccionada == null || carreraSeleccionada.equals(" Selecciona carrera ") || dias.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor completa todos los campos antes de guardar.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modoEditar) {
            // Modificar tutor existente
            Tutor tutorModificar = tutores.get(indiceSeleccionado);
            tutorModificar.setNombre(nombre);
            tutorModificar.setCarrera(carreraSeleccionada);
            tutorModificar.setDias(dias);

            try {
                cTutor.edit(tutorModificar);
                JOptionPane.showMessageDialog(this, "Tutor modificado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar el tutor.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            modoEditar = false;
            agregar.setText("Agregar registro");
            numTarS.setEnabled(true);
            // >>> ACTUALIZA COMBO EN ITUTORADO
            if (itutorado != null) {
                itutorado.actualizarTutores();
            }
            if (menu != null) {
                menu.getVistaCitaCoordinador().actualizarTutores();
                menu.getVistaTutoria().actualizarTutoresYCitas();
            }

        } else {
            // Validar tarjeta repetida solo si agregas
            for (Tutor t : cTutor.findTutorEntities()) {
                if (t.getNumtar() == numTarjeta) {
                    JOptionPane.showMessageDialog(this, "El número de tarjeta ya existe. Ingresa uno diferente.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            tutor = new Tutor();
            tutor.setNumtar(numTarjeta);
            tutor.setCarrera(carreraSeleccionada);
            tutor.setDias(dias);
            tutor.setNombre(nombre);

            cTutor.create(tutor);
            JOptionPane.showMessageDialog(this, "Tutor guardado exitosamente.");
        }
        // >>> ACTUALIZA COMBO EN ITUTORADO
        if (itutorado != null) {
            itutorado.actualizarTutores();
        }
        if (menu != null) {
            menu.getVistaCitaCoordinador().actualizarTutores();
            menu.getVistaTutoria().actualizarTutoresYCitas();
        }

        // Recargar tabla y limpiar campos
        tutores = cTutor.findTutorEntities();
        mt = new MTabla(tutores);
        tablaTutores.setModel(mt);

        numTarS.setText("");
        nombreTutor.setText("");
        comboCarrera.setSelectedIndex(0);
        txtdias.setText("");
        // <<< LLAMADA PARA ACTUALIZAR ITutorado
        if (itutorado != null) {
            itutorado.actualizarTutores();
        }
        if (menu != null) {
            menu.getVistaCitaCoordinador().actualizarTutores();
            menu.getVistaTutoria().actualizarTutoresYCitas();
        }


    }//GEN-LAST:event_agregarActionPerformed

    private void txtdiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdiasActionPerformed

    }//GEN-LAST:event_txtdiasActionPerformed

    private void txtdiasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdiasKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_txtdiasKeyTyped

    private void nombreTutorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nombreTutorKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isLetter(c) && c != ' ') {
            evt.consume();
        }
    }//GEN-LAST:event_nombreTutorKeyTyped

    private void comboCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCarreraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboCarreraActionPerformed

    private void nombreTutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreTutorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreTutorActionPerformed

    private void numTarSKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numTarSKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_numTarSKeyTyped

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
            java.util.logging.Logger.getLogger(ITutor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ITutor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ITutor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ITutor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                ITutor dialog = new ITutor(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton agregar;
    private javax.swing.JComboBox<String> comboCarrera;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField nombreTutor;
    private javax.swing.JTextField numTarS;
    private javax.swing.JTable tablaTutores;
    private javax.swing.JTextField txtdias;
    // End of variables declaration//GEN-END:variables
}
