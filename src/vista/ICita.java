package vista;

import control.TutorJpaController;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.SpinnerListModel;
import modelo.Tutor;
import control.CitaJpaController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Cita;

public class ICita extends javax.swing.JDialog {

    private List<Tutor> tutores;
    private TutorJpaController cTutor;
    private CitaJpaController cCita;
    private Tutor tutorActual;

    public ICita(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
        String[] hours = {
            "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM",
            "11:00 AM", "12:00 PM", "13:00 PM", "14:00 PM",
            "15:00 PM", "16:00 PM", "17:00 PM", "18:00 PM", "19:00 PM"
        };

        SpinnerListModel hourModel = new SpinnerListModel(hours);
        spinnerhora.setModel(hourModel);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        cTutor = new TutorJpaController(emf);
        cCita = new CitaJpaController(emf);
        jdatefecha.setDateFormatString("dd/MM/yyyy");
// No permitir fechas pasadas
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        jdatefecha.setMinSelectableDate(hoy.getTime());
// Cargar y ordenar tutores por ID
        tutores = cTutor.findTutorEntities();
        tutores.sort((t1, t2) -> t1.getIdpersona().compareTo(t2.getIdpersona()));

// Limpiar y llenar combo
        comboTutor.removeAllItems();
        comboTutor.addItem("Selecciona un tutor");

        for (Tutor t : tutores) {
            comboTutor.addItem(t.getNombre());
        }
        // Agregar los estados al comboEstado
        comboestado.removeAllItems(); // Limpiar opciones previas
        comboestado.addItem("Selecciona un estado");
        comboestado.addItem("Pendiente");
        comboestado.addItem("Confirmada");
        comboestado.addItem("Cancelada");
        comboestado.addItem("Finalizada");

        // Bloquear todos los componentes excepto el combo de tutor y botón de aceptar
        jdatefecha.setEnabled(false);
        spinnerhora.setEnabled(false);
        txtasunto.setEnabled(false);
        comboestado.setEnabled(false);
        aceptarcita.setEnabled(true);

        // Establecer encabezados en la tabla al iniciar (aunque esté vacía)
        tablaCitas.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{}, // Datos vacíos
                new String[]{"Fecha", "Hora", "Asunto", "Estado"} // Encabezados
        ));

        jdatefecha.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    if (jdatefecha.getDate() != null) {
                        // Habilitar componentes restantes
                        spinnerhora.setEnabled(true);
                        txtasunto.setEnabled(true);
                        comboestado.setEnabled(true);
                        aceptarcita.setEnabled(true);
                    }
                }
            }
        });

    }
    public ICita(java.awt.Dialog parent, boolean modal, Tutor tutor) {
    super(parent, modal);
    initComponents();

    this.tutorActual = tutor; // Guardar tutor logueado

    // Inicializar componentes adicionales como en el constructor original
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
    cTutor = new TutorJpaController(emf);
    cCita = new CitaJpaController(emf);

    tutores = cTutor.findTutorEntities(); // Aquí inicializamos la lista
    tutores.sort((t1, t2) -> t1.getIdpersona().compareTo(t2.getIdpersona()));

    // Inicializa comboestado como en el original
    comboestado.removeAllItems();
    comboestado.addItem("Selecciona un estado");
    comboestado.addItem("Pendiente");
    comboestado.addItem("Confirmada");
    comboestado.addItem("Cancelada");
    comboestado.addItem("Finalizada");

    // Si no quieres que seleccione tutores (porque ya es el tutor logueado)
    comboTutor.removeAllItems();
    comboTutor.addItem(tutor.getNombre()); // Solo su propio nombre

    // Configurar el spinnerhora igual que el original
    String[] hours = {
        "07:00 AM", "08:00 AM", "09:00 AM", "10:00 AM",
        "11:00 AM", "12:00 PM", "13:00 PM", "14:00 PM",
        "15:00 PM", "16:00 PM", "17:00 PM", "18:00 PM", "19:00 PM"
    };
    SpinnerListModel hourModel = new SpinnerListModel(hours);
    spinnerhora.setModel(hourModel);

    jdatefecha.setDateFormatString("dd/MM/yyyy");
    Calendar hoy = Calendar.getInstance();
    jdatefecha.setMinSelectableDate(hoy.getTime());

    // Mostrar las citas de ese tutor directamente
    cargarCitasDelTutor(tutor);
}



    public Tutor getTutorSeleccionado() {
        String nombre = (String) comboTutor.getSelectedItem();
        for (Tutor t : tutores) {
            if (t.getNombre().equals(nombre)) {
                return t;
            }
        }
        return null;
    }

    private boolean existeCita(Tutor tutor, Date fecha, int hora) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        EntityManager em = emf.createEntityManager();

        try {
            // Consulta para verificar citas existentes
            Query query = em.createQuery(
                    "SELECT COUNT(c) FROM Cita c WHERE c.idtutor = :tutor AND c.fecha = :fecha AND c.hora = :hora");
            query.setParameter("tutor", tutor);
            query.setParameter("fecha", fecha);
            query.setParameter("hora", hora);

            Long count = (Long) query.getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    private String formatearHora(int horaEntera) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, horaEntera);
        cal.set(java.util.Calendar.MINUTE, 0);
        return sdf.format(cal.getTime());
    }

    public static int convertirHoraSpinnerAEntero(Object spinnerValue) {
        if (spinnerValue == null) {
            throw new IllegalArgumentException("La hora no puede ser nula");
        }

        String horaStr = spinnerValue.toString();

        try {
            // Extraer la parte numérica antes de los ":"
            String parteNumerica = horaStr.split(":")[0].trim();
            int hora = Integer.parseInt(parteNumerica);

            // Validar rango (ya que tu spinner solo tiene horas de 7 a 19)
            if (hora < 7 || hora > 19) {
                throw new IllegalArgumentException("Hora fuera del rango permitido (7-19)");
            }

            return hora;
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de hora inválido. Use 'HH:00 AM/PM'", e);
        }
    }

    private void cargarCitasDelTutor(Tutor tutor) {
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaCitas.getModel();
        modeloTabla.setRowCount(0); // Limpia la tabla

        CitaJpaController citaController = new CitaJpaController(Persistence.createEntityManagerFactory("Tutoria3PU"));
        List<Cita> citas = citaController.findCitaEntities();

        for (Cita cita : citas) {
            if (cita.getIdtutor().getIdpersona().equals(tutor.getIdpersona())) {
                String fecha = new SimpleDateFormat("dd/MM/yyyy").format(cita.getFecha());
                String hora = String.format("%02d:00", cita.getHora());
                modeloTabla.addRow(new Object[]{fecha, hora, cita.getAsunto(), cita.getEstado()});
            }
        }
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jdatefecha = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        txtasunto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        comboestado = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        comboTutor = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCitas = new javax.swing.JTable();
        aceptartutor = new javax.swing.JButton();
        aceptarcita = new javax.swing.JButton();
        spinnerhora = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Registrar Cita");

        jLabel2.setText("Fecha");

        jLabel3.setText("Hora");

        jLabel4.setText("Asunto");

        txtasunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtasuntoActionPerformed(evt);
            }
        });
        txtasunto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtasuntoKeyTyped(evt);
            }
        });

        jLabel5.setText("Estado");

        comboestado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Tutor");

        comboTutor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tablaCitas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaCitas);

        aceptartutor.setText("Aceptar Tutor");
        aceptartutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptartutorActionPerformed(evt);
            }
        });

        aceptarcita.setText("Aceptar Cita");
        aceptarcita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarcitaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(224, 224, 224))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(comboTutor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jdatefecha, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(aceptartutor, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(spinnerhora, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboestado, javax.swing.GroupLayout.Alignment.LEADING, 0, 179, Short.MAX_VALUE)
                                    .addComponent(txtasunto, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addComponent(aceptarcita, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(comboTutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addComponent(aceptartutor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdatefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerhora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtasunto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboestado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aceptarcita)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtasuntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtasuntoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtasuntoActionPerformed

    private void txtasuntoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtasuntoKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A') | c > 'Z')
            evt.consume();
    }//GEN-LAST:event_txtasuntoKeyTyped

    private void aceptartutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptartutorActionPerformed
        String seleccionado = (String) comboTutor.getSelectedItem();

        if (seleccionado == null || seleccionado.equals("Selecciona un tutor")) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor válido.");
            return;
        }

        // Bloquear combo y botón
        comboTutor.setEnabled(false);
        aceptartutor.setEnabled(false);

        // Habilitar fecha
        jdatefecha.setEnabled(true);

        // Obtener el tutor seleccionado
        Tutor tutorSeleccionado = getTutorSeleccionado();

        // Obtener las citas del tutor seleccionado
        CitaJpaController citaController = new CitaJpaController(Persistence.createEntityManagerFactory("Tutoria3PU"));
        List<Cita> citasTutor = citaController.findCitaEntities()
                .stream()
                .filter(c -> c.getIdtutor().getIdpersona().equals(tutorSeleccionado.getIdpersona()))
                .collect(Collectors.toList());

        // Crear el modelo de la tabla
        DefaultTableModel modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Hora");
        modeloTabla.addColumn("Asunto");
        modeloTabla.addColumn("Estado");

        if (!citasTutor.isEmpty()) {
            for (Cita cita : citasTutor) {
                String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy").format(cita.getFecha());

                // CORRECCIÓN: Formatear la hora (entero) a "HH:00"
                String horaFormateada = String.format("%02d:00", cita.getHora());

                String asunto = cita.getAsunto();
                String estado = cita.getEstado();
                modeloTabla.addRow(new Object[]{fechaFormateada, horaFormateada, asunto, estado});
            }
        } else {
            JOptionPane.showMessageDialog(this, "Este tutor no tiene citas registradas.");
        }

        tablaCitas.setModel(modeloTabla);
    }//GEN-LAST:event_aceptartutorActionPerformed

    private void aceptarcitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarcitaActionPerformed
        // Validar tutor
        Tutor tutor = getTutorSeleccionado();
        if (tutor == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un tutor válido.");
            return;
        }

        // Validar fecha
        if (jdatefecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una fecha.");
            return;
        }

        // Validar que no sea sábado ni domingo
        Calendar cal = Calendar.getInstance();
        cal.setTime(jdatefecha.getDate());
        int dia = cal.get(Calendar.DAY_OF_WEEK);

        if (dia == Calendar.SATURDAY || dia == Calendar.SUNDAY) {
            JOptionPane.showMessageDialog(this, "No se pueden programar citas en sábado o domingo.");
            return;
        }

        // Validar asunto
        String asunto = txtasunto.getText().trim();
        if (asunto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo 'Asunto' no puede estar vacío.");
            txtasunto.requestFocus();
            return;
        }

        // Validar estado
        String estado = (String) comboestado.getSelectedItem();
        if (estado == null || estado.equals("Selecciona un estado")) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un estado válido.");
            return;
        }

        // Validar y convertir hora
        int horaSeleccionada;
        try {
            String horaStr = spinnerhora.getValue().toString();
            horaSeleccionada = convertirHoraSpinnerAEntero(horaStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la hora seleccionada: " + e.getMessage());
            return;
        }

        // Validar si ya existe una cita en la misma fecha y hora con el mismo tutor
        if (existeCita(tutor, jdatefecha.getDate(), horaSeleccionada)) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe una cita programada con este tutor el "
                    + new SimpleDateFormat("dd/MM/yyyy").format(jdatefecha.getDate())
                    + " a las " + String.format("%02d:00", horaSeleccionada) + ".\n"
                    + "Por favor seleccione otra hora u otro día.");
            return;
        }

        // Crear nueva cita
        Cita nuevaCita = new Cita();
        nuevaCita.setFecha(jdatefecha.getDate());
        nuevaCita.setHora(horaSeleccionada);
        nuevaCita.setAsunto(asunto);
        nuevaCita.setEstado(estado);
        nuevaCita.setIdtutor(tutor);

        // Guardar la cita
        CitaJpaController citaController = new CitaJpaController(Persistence.createEntityManagerFactory("Tutoria3PU"));
        try {
            citaController.create(nuevaCita);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la cita: " + e.getMessage());
            return;
        }

        // Actualizar tabla
        cargarCitasDelTutor(tutor);

        // Resetear interfaz
        comboTutor.setEnabled(true);
        comboTutor.setSelectedIndex(0);
        aceptartutor.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Cita guardada correctamente.");
        jdatefecha.setDate(null);
        txtasunto.setText("");
        comboestado.setSelectedIndex(0);
        spinnerhora.setValue("07:00 AM");
        comboTutor.setSelectedIndex(0);


    }//GEN-LAST:event_aceptarcitaActionPerformed

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
            java.util.logging.Logger.getLogger(ICita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ICita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ICita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ICita.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                ICita dialog = new ICita(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton aceptarcita;
    private javax.swing.JButton aceptartutor;
    private javax.swing.JComboBox<String> comboTutor;
    private javax.swing.JComboBox<String> comboestado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdatefecha;
    private javax.swing.JSpinner spinnerhora;
    private javax.swing.JTable tablaCitas;
    private javax.swing.JTextField txtasunto;
    // End of variables declaration//GEN-END:variables
}
