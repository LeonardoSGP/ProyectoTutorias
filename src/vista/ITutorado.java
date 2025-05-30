package vista;

import control.AdmDatos;
import control.TutorJpaController;
import control.TutoradoJpaController;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import modelo.MTabla;
import modelo.Tutor;
import modelo.Tutorado;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ITutorado extends javax.swing.JDialog {

    private boolean enModoEdicion = false;
    private Tutorado tutoradoSeleccionado = null;

    private Tutor tutor;
    private TutorJpaController cTutor;
    private List<Tutor> tutores;
    private Tutorado tutorado;
    private TutoradoJpaController cTutorado;
    private AdmDatos adm;
    private List<Tutorado> listaTutorados;
    private MTabla mtbla;
    private ComboBoxModel<String> mtutores;
    private DefaultListModel<String> mestudiantes;
    private DefaultListModel<String> mtutorados;
    private Map<String, Tutorado> tutorado_nom = new HashMap<>();

    private final String SELECCIONA = "Selecciona Tutor";
    private final String SELECCIONADO = "Tutor Seleccionado";

    public ITutorado(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

        mestudiantes = new DefaultListModel<>();
        mtutorados = new DefaultListModel<>();

        // Inicialmente la lista derecha (tutorados) estará vacía
        selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());

        combogenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Selecciona Género", "M", "F"
        }));

        adm = new AdmDatos();
        cTutor = new TutorJpaController(adm.getEmf());
        tutores = cTutor.findTutorEntities();

        cTutorado = new TutoradoJpaController(adm.getEmf());
        listaTutorados = cTutorado.findTutoradoEntities();

        cargarTutores();
        cargarEstudiantes();
        cargarTablaEstudiantes();

        selectorListas1.setBotonDerechaHabilitado(false);

        ltutores.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                onTutorSeleccionado();
            }
        });

        // Impedir fechas futuras en el JDateChooser
        jDateChooser.setMaxSelectableDate(new java.util.Date());
    }

    public void cargarTutores() {
        ltutores.removeAllItems();
        ltutores.addItem(SELECCIONA);
        for (Tutor dTutor : tutores) {
            ltutores.addItem(dTutor.getNombre());
        }
    }

    public void cargarEstudiantes() {
        // Obtener todos los tutorados de la base de datos
        listaTutorados = cTutorado.findTutoradoEntities();

        tutorado_nom.clear();
        List<String> sinTutor = new ArrayList<>();

        // Filtrar solo los que no tienen tutor asignado
        for (Tutorado t : listaTutorados) {
            if (t.getIdtutor() == null) {
                sinTutor.add(t.getNombre());
            }
            tutorado_nom.put(t.getNombre(), t);
        }

        // Mostrar solo estudiantes sin tutor (lista izquierda)
        // y lista derecha vacía inicialmente
        selectorListas1.setListas(sinTutor, new ArrayList<>());
    }

    public void cargarTablaEstudiantes() {
        listaTutorados = cTutorado.findTutoradoEntities();
        String[] columnas = {"Número de Control", "Nombre", "Género", "Fecha de Nacimiento"};
        Object[][] datos = new Object[listaTutorados.size()][4];

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < listaTutorados.size(); i++) {
            Tutorado t = listaTutorados.get(i);
            datos[i][0] = t.getNc();
            datos[i][1] = t.getNombre();
            datos[i][2] = t.getGenero();
            datos[i][3] = (t.getFechanac() != null) ? formatoFecha.format(t.getFechanac()) : "";
        }

        tablaestudiantes.setModel(new javax.swing.table.DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        tablaestudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaestudiantes.rowAtPoint(evt.getPoint());
                if (fila >= 0) {
                    String nc = tablaestudiantes.getValueAt(fila, 0).toString();
                    cargarDatosTutoradoSeleccionado(nc);
                    txtnumcontrol.setEnabled(false);
                }
            }
        });

    }

    private void cargarDatosTutoradoSeleccionado(String nc) {
        tutoradoSeleccionado = listaTutorados.stream()
                .filter(t -> t.getNc().equals(nc))
                .findFirst().orElse(null);

        if (tutoradoSeleccionado != null) {
            txtnumcontrol.setText(tutoradoSeleccionado.getNc());
            txtnombre.setText(tutoradoSeleccionado.getNombre());
            jDateChooser.setDate(tutoradoSeleccionado.getFechanac());
            combogenero.setSelectedItem(String.valueOf(tutoradoSeleccionado.getGenero()));

            txtnumcontrol.setEditable(false); // No se puede cambiar NC al editar
            btnregistrar.setText("Modificar");
            enModoEdicion = true;
        }
    }

    private List<String> mestudiantesToList() {
        List<String> lista = new ArrayList<>();
        for (int i = 0; i < mestudiantes.getSize(); i++) {
            lista.add(mestudiantes.getElementAt(i));
        }
        return lista;
    }

    private boolean isTutorSeleccionado() {
        return !ltutores.getSelectedItem().equals(SELECCIONA);
    }

    private void onTutorSeleccionado() {
        if (isTutorSeleccionado()) {
            selectorListas1.setBotonDerechaHabilitado(true);

            String nombreTutor = (String) ltutores.getSelectedItem();
            Tutor tutorActual = tutores.stream()
                    .filter(t -> t.getNombre().equals(nombreTutor))
                    .findFirst().orElse(null);

            if (tutorActual != null) {
                // Obtener los tutorados de este tutor desde la base de datos
                List<Tutorado> tutoradosDelTutor = cTutorado.findTutoradosByTutorId(tutorActual.getIdpersona());

                // Obtener todos los tutorados sin tutor
                List<Tutorado> todosTutorados = cTutorado.findTutoradoEntities();

                List<String> sinTutor = new ArrayList<>();
                List<String> conTutor = new ArrayList<>();

                // Llenar lista de estudiantes sin tutor
                for (Tutorado t : todosTutorados) {
                    if (t.getIdtutor() == null) {
                        sinTutor.add(t.getNombre());
                    }
                    tutorado_nom.put(t.getNombre(), t);
                }

                // Llenar lista de tutorados del tutor seleccionado
                for (Tutorado t : tutoradosDelTutor) {
                    conTutor.add(t.getNombre());
                }

                selectorListas1.setListas(sinTutor, conTutor);
            }
        } else {
            selectorListas1.setBotonDerechaHabilitado(false);
            cargarEstudiantes();
        }
    }

    private void limpiarFormulario() {
        txtnombre.setText("");
        txtnumcontrol.setText("");
        txtnumcontrol.setEditable(true);
        jDateChooser.setDate(null);
        combogenero.setSelectedIndex(0);

        tutoradoSeleccionado = null;
        enModoEdicion = false;
        btnregistrar.setText("Registrar");
    }

    public void actualizarTutores() {
        try {
            // Recarga los tutores desde la base de datos
            cTutor = new TutorJpaController(adm.getEmf());
            tutores = cTutor.findTutorEntities();
            cargarTutores(); // Método que ya tienes para llenar el ComboBox
        } catch (Exception ex) {
            Logger.getLogger(ITutorado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        aceptar = new javax.swing.JButton();
        ltutores = new javax.swing.JComboBox<>();
        btnAñadir = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        selectorListas1 = new componentevisual.SelectorListas();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        combogenero = new javax.swing.JComboBox<>();
        jDateChooser = new com.toedter.calendar.JDateChooser();
        txtnombre = new javax.swing.JTextField();
        txtnumcontrol = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaestudiantes = new javax.swing.JTable();
        btnregistrar = new javax.swing.JButton();

        jButton3.setText("jButton3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Seleccionar Tutor");

        jLabel2.setText("Estudiantes");

        aceptar.setText("Aceptar");
        aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarActionPerformed(evt);
            }
        });

        ltutores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ltutores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ltutoresActionPerformed(evt);
            }
        });

        btnAñadir.setText("Aceptar Tutor");
        btnAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirActionPerformed(evt);
            }
        });

        jLabel3.setText("Tutorados");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(138, 138, 138))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectorListas1, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(ltutores, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnAñadir))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(280, 280, 280)
                        .addComponent(aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ltutores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAñadir))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(selectorListas1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(aceptar)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Asignar Tutor", jPanel1);

        jLabel4.setText("Nombre");

        jLabel5.setText("Numero de Control");

        jLabel6.setText("Genero");

        jLabel7.setText("Fecha de Nacimiento");

        combogenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtnombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnombreActionPerformed(evt);
            }
        });
        txtnombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnombreKeyTyped(evt);
            }
        });

        tablaestudiantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tablaestudiantes);

        btnregistrar.setText("Registrar");
        btnregistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnregistrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)))
                .addGap(57, 57, 57)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtnumcontrol, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(combogenero, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtnombre)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(205, 205, 205))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 34, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtnumcontrol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combogenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(btnregistrar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Registrar Tutorado", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
        if (ltutores.getSelectedItem().equals(SELECCIONA))
            JOptionPane.showMessageDialog(this, "Selecciona un tutor");
        else if (JOptionPane.showConfirmDialog(this, "Seleccionaste al tutor " + ltutores.getSelectedItem() + "?") == 0) {
            tutor = tutores.get(ltutores.getSelectedIndex() - 1);
            ltutores.setEnabled(false);
            btnAñadir.setText(SELECCIONADO);
            btnAñadir.setEnabled(false);
            selectorListas1.setBotonDerechaHabilitado(true);
            if (!mestudiantes.isEmpty()) {
            }
        }
    }//GEN-LAST:event_btnAñadirActionPerformed

    private void aceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarActionPerformed
        if (!isTutorSeleccionado()) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener el tutor seleccionado
        String nombreTutor = (String) ltutores.getSelectedItem();
        tutor = tutores.stream()
                .filter(t -> t.getNombre().equals(nombreTutor))
                .findFirst().orElse(null);

        if (tutor == null) {
            JOptionPane.showMessageDialog(this, "Tutor no válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener las listas actuales del selector
        List<String> estudiantesActuales = selectorListas1.obtenerLista1(); // Sin tutor
        List<String> tutoradosActuales = selectorListas1.obtenerLista2();   // Con tutor

        // Procesar estudiantes que fueron movidos a tutorados (asignar tutor)
        for (String nombreTutorado : tutoradosActuales) {
            Tutorado t = tutorado_nom.get(nombreTutorado);
            if (t != null && (t.getIdtutor() == null || !t.getIdtutor().equals(tutor))) {
                t.setIdtutor(tutor);
                try {
                    cTutorado.edit(t);
                    System.out.println("Asignado: " + t.getNombre() + " → " + tutor.getNombre());
                } catch (Exception ex) {
                    Logger.getLogger(ITutorado.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this,
                            "Error al asignar tutorado: " + t.getNombre(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Procesar estudiantes que fueron quitados del tutor (remover tutor)
        for (String nombreEstudiante : estudiantesActuales) {
            Tutorado t = tutorado_nom.get(nombreEstudiante);
            if (t != null && t.getIdtutor() != null && t.getIdtutor().equals(tutor)) {
                t.setIdtutor(null);
                try {
                    cTutorado.edit(t);
                    System.out.println("Removido: " + t.getNombre() + " ← " + tutor.getNombre());
                } catch (Exception ex) {
                    Logger.getLogger(ITutorado.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this,
                            "Error al remover tutorado: " + t.getNombre(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Actualizar la vista
        cargarEstudiantes();
        cargarTablaEstudiantes();
        JOptionPane.showMessageDialog(this,
                "Cambios guardados correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
// Restaurar para poder elegir otro tutor
        ltutores.setEnabled(true);
        btnAñadir.setText("Añadir Tutor");
        btnAñadir.setEnabled(true);
        ltutores.setSelectedItem(SELECCIONA); // vuelve a mostrar la opción por defecto

        cargarEstudiantes();
        selectorListas1.setBotonDerechaHabilitado(false);


    }//GEN-LAST:event_aceptarActionPerformed

    private void btnregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnregistrarActionPerformed
        String nombre = txtnombre.getText().trim();
        String nc = txtnumcontrol.getText().trim();
        java.util.Date fecha = jDateChooser.getDate();
        String genero = (String) combogenero.getSelectedItem();

        // Validar campos vacíos
        if (nombre.isEmpty() || nc.isEmpty() || fecha == null || genero == null || genero.equals("Selecciona Género")) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos antes de continuar.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar fecha futura
        java.util.Date hoy = new java.util.Date();
        if (fecha.after(hoy)) {
            JOptionPane.showMessageDialog(this, "La fecha de nacimiento no puede ser futura.", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (enModoEdicion && tutoradoSeleccionado != null) {
            // MODIFICACIÓN
            tutoradoSeleccionado.setNombre(nombre);
            tutoradoSeleccionado.setFechanac(fecha);
            tutoradoSeleccionado.setGenero(genero.charAt(0));

            try {
                cTutorado.edit(tutoradoSeleccionado);
                JOptionPane.showMessageDialog(this, "Tutorado modificado correctamente.");
                txtnumcontrol.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

        } else {
            // REGISTRO NUEVO
            for (Tutorado t : listaTutorados) {
                if (t.getNc().equalsIgnoreCase(nc)) {
                    JOptionPane.showMessageDialog(this, "Ya existe un tutorado con ese número de control.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Tutorado nuevo = new Tutorado();
            nuevo.setNombre(nombre);
            nuevo.setNc(nc);
            nuevo.setFechanac(fecha);
            nuevo.setGenero(genero.charAt(0));

            try {
                cTutorado.create(nuevo);
                JOptionPane.showMessageDialog(this, "Tutorado registrado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        // En ambos casos, actualiza la tabla y limpia el formulario
        listaTutorados = cTutorado.findTutoradoEntities();
        cargarEstudiantes();
        cargarTablaEstudiantes();
        limpiarFormulario();
    }//GEN-LAST:event_btnregistrarActionPerformed

    private void txtnombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isLetter(c) && c != ' ') {
            evt.consume();
        }
    }//GEN-LAST:event_txtnombreKeyTyped

    private void ltutoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ltutoresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ltutoresActionPerformed

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
            java.util.logging.Logger.getLogger(ITutorado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ITutorado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ITutorado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ITutorado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ITutorado dialog = new ITutorado(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton btnAñadir;
    private javax.swing.JButton btnregistrar;
    private javax.swing.JComboBox<String> combogenero;
    private javax.swing.JButton jButton3;
    private com.toedter.calendar.JDateChooser jDateChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> ltutores;
    private componentevisual.SelectorListas selectorListas1;
    private javax.swing.JTable tablaestudiantes;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JTextField txtnumcontrol;
    // End of variables declaration//GEN-END:variables
}
