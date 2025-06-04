package vista;

import control.AdmDatos;
import control.TutorJpaController;
import control.TutoradoJpaController;
import java.awt.event.ItemEvent;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
        ltutores.setEnabled(false);
        btntutor.setEnabled(false);
        aceptar.setEnabled(false);
        btncarrera.setEnabled(false);
        btnregistrar.setEnabled(false);

        configurarValidacionRegistro();

        comboCarrera1.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String seleccion = (String) comboCarrera1.getSelectedItem();
                btncarrera.setEnabled(!seleccion.equals("Selecciona Carrera"));
            }
        });
        ltutores.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {

                btntutor.setEnabled(!ltutores.getSelectedItem().equals(SELECCIONA));

                selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

        mestudiantes = new DefaultListModel<>();
        mtutorados = new DefaultListModel<>();

        // Inicialmente ambas listas vacías
        selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());

        combogenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Selecciona Género", "M", "F"
        }));
        comboCarrera1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Selecciona Carrera", "Sistemas", "Electrónica", "Industrial", "Gestión Empresarial",
            "Civil", "Electrica", "Administracion", "Mecanica", "Quimica"
        }));
        comboCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Selecciona Carrera", "Sistemas", "Electrónica", "Industrial", "Gestión Empresarial",
            "Civil", "Electrica", "Administracion", "Mecanica", "Quimica"
        }));

        adm = new AdmDatos();
        cTutor = new TutorJpaController(adm.getEmf());
        tutores = cTutor.findTutorEntities();

        cTutorado = new TutoradoJpaController(adm.getEmf());
        listaTutorados = cTutorado.findTutoradoEntities();

        cargarTutores();

        cargarTablaEstudiantes();

        selectorListas1.setBotonDerechaHabilitado(false);

        ltutores.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                onTutorSeleccionado();
            }
        });

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
        if (tutor != null) {
            cargarEstudiantesFiltrados(tutor);
        } else {
            listaTutorados = cTutorado.findTutoradoEntities();
            tutorado_nom.clear();
            List<String> sinTutor = new ArrayList<>();

            for (Tutorado t : listaTutorados) {
                if (t.getIdtutor() == null) {
                    sinTutor.add(t.getNombre());
                }
                tutorado_nom.put(t.getNombre(), t);
            }

            selectorListas1.setListas(sinTutor, new ArrayList<>());
        }
    }

    public void cargarTablaEstudiantes() {
        listaTutorados = cTutorado.findTutoradoEntities();
        String[] columnas = {"Número de Control", "Nombre", "Género", "Fecha de Nacimiento", "Carrera"};
        Object[][] datos = new Object[listaTutorados.size()][5];

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < listaTutorados.size(); i++) {
            Tutorado t = listaTutorados.get(i);
            datos[i][0] = t.getNc();
            datos[i][1] = t.getNombre();
            datos[i][2] = t.getGenero();
            datos[i][3] = (t.getFechanac() != null) ? formatoFecha.format(t.getFechanac()) : "";
            datos[i][4] = t.getCarrera();
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
            comboCarrera.setSelectedItem(tutoradoSeleccionado.getCarrera());

            txtnumcontrol.setEditable(false);
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

                // Llenar lista de estudiantes sin tutor y que coincidan en carrera
                for (Tutorado t : todosTutorados) {
                    tutorado_nom.put(t.getNombre(), t); // Siempre guardamos para buscar rápido

                    if (t.getIdtutor() == null
                            && t.getCarrera() != null
                            && t.getCarrera().equalsIgnoreCase(tutorActual.getCarrera())) {
                        sinTutor.add(t.getNombre());
                    }
                }

                // Llenar lista de tutorados del tutor seleccionado y que coincidan en carrera
                for (Tutorado t : tutoradosDelTutor) {
                    if (t.getCarrera() != null && t.getCarrera().equalsIgnoreCase(tutorActual.getCarrera())) {
                        conTutor.add(t.getNombre());
                    }
                }

                if (sinTutor.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "No hay estudiantes disponibles en la carrera de este tutor.");
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
        comboCarrera.setSelectedIndex(0);

        tutoradoSeleccionado = null;
        enModoEdicion = false;
        btnregistrar.setText("Registrar");
    }

    public void actualizarTutores() {
        try {
            cTutor = new TutorJpaController(adm.getEmf());
            tutores = cTutor.findTutorEntities();
            cargarTutores(); // Método que ya tienes para llenar el ComboBox
        } catch (Exception ex) {
            Logger.getLogger(ITutorado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void cargarEstudiantesFiltrados(Tutor tutorSeleccionado) {
        listaTutorados = cTutorado.findTutoradoEntities();
        tutorado_nom.clear();
        List<String> sinTutor = new ArrayList<>();
        List<String> conEsteTutor = new ArrayList<>();

        for (Tutorado t : listaTutorados) {
            if (t.getCarrera() != null && t.getCarrera().equalsIgnoreCase(tutorSeleccionado.getCarrera())) {
                tutorado_nom.put(t.getNombre(), t);

                if (t.getIdtutor() == null) {
                    sinTutor.add(t.getNombre());
                } else if (t.getIdtutor().equals(tutorSeleccionado)) {
                    conEsteTutor.add(t.getNombre());
                }
            }
        }

        selectorListas1.setListas(sinTutor, conEsteTutor);

        selectorListas1.setBotonDerechaHabilitado(!sinTutor.isEmpty());

        if (sinTutor.isEmpty() && conEsteTutor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay estudiantes registrados en la carrera " + tutorSeleccionado.getCarrera(),
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cargarEstudiantesPorCarreraYTutor(Tutor tutorSeleccionado) {
        List<Tutorado> todosTutorados = cTutorado.findTutoradoEntities();

        List<String> estudiantesSinTutor = new ArrayList<>();
        List<String> estudiantesConEsteTutor = new ArrayList<>();

        for (Tutorado t : todosTutorados) {
            // Verificar que el estudiante es de la misma carrera que el tutor
            if (t.getCarrera() != null && t.getCarrera().equalsIgnoreCase(tutorSeleccionado.getCarrera())) {
                if (t.getIdtutor() == null) {
                    estudiantesSinTutor.add(t.getNombre());
                } else if (t.getIdtutor().equals(tutorSeleccionado)) {
                    estudiantesConEsteTutor.add(t.getNombre());
                }
                tutorado_nom.put(t.getNombre(), t);
            }
        }

        selectorListas1.setListas(estudiantesSinTutor, estudiantesConEsteTutor);
        selectorListas1.setBotonDerechaHabilitado(!estudiantesSinTutor.isEmpty());

        if (estudiantesSinTutor.isEmpty() && estudiantesConEsteTutor.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay estudiantes registrados en la carrera " + tutorSeleccionado.getCarrera(),
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validarCamposRegistro() {
        if (txtnumcontrol.getText().trim().isEmpty()) {
            return false;
        }

        if (txtnombre.getText().trim().isEmpty()) {
            return false;
        }

        String genero = (String) combogenero.getSelectedItem();
        if (genero == null || genero.equals("Selecciona Género")) {
            return false;
        }

        String carrera = (String) comboCarrera.getSelectedItem();
        if (carrera == null || carrera.equals("Selecciona Carrera")) {
            return false;
        }

        if (jDateChooser.getDate() == null) {
            return false;
        }

        java.util.Date hoy = new java.util.Date();
        if (jDateChooser.getDate().after(hoy)) {
            return false;
        }

        return true;
    }

    private void configurarValidacionRegistro() {

        txtnumcontrol.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }

            public void removeUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }

            public void insertUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }
        });

        txtnombre.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }

            public void removeUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }

            public void insertUpdate(DocumentEvent e) {
                validarYHabilitarRegistro();
            }
        });

        combogenero.addActionListener(e -> validarYHabilitarRegistro());

        comboCarrera.addActionListener(e -> validarYHabilitarRegistro());

        jDateChooser.addPropertyChangeListener("date", evt -> validarYHabilitarRegistro());
    }

    private void validarYHabilitarRegistro() {
        btnregistrar.setEnabled(validarCamposRegistro());
    }

    private void mostrarErroresRegistro() {
        StringBuilder errores = new StringBuilder("Corrige los siguientes campos:\n");

        // Validar número de control
        if (txtnumcontrol.getText().trim().isEmpty()) {
            errores.append("- Número de control es obligatorio\n");
        }

        // Validar nombre
        if (txtnombre.getText().trim().isEmpty()) {
            errores.append("- Nombre es obligatorio\n");
        }

        // Validar género
        String genero = (String) combogenero.getSelectedItem();
        if (genero == null || genero.equals("Selecciona Género")) {
            errores.append("- Selecciona un género válido\n");
        }

        // Validar carrera
        String carrera = (String) comboCarrera.getSelectedItem();
        if (carrera == null || carrera.equals("Selecciona Carrera")) {
            errores.append("- Selecciona una carrera válida\n");
        }

        // Validar fecha
        if (jDateChooser.getDate() == null) {
            errores.append("- Fecha de nacimiento es obligatoria\n");
        } else if (jDateChooser.getDate().after(new java.util.Date())) {
            errores.append("- La fecha no puede ser futura\n");
        }

        JOptionPane.showMessageDialog(this, errores.toString(), "Error en campos", JOptionPane.ERROR_MESSAGE);
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
        btntutor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        selectorListas1 = new componentevisual.SelectorListas();
        jLabel9 = new javax.swing.JLabel();
        btncarrera = new javax.swing.JButton();
        ltutores = new javax.swing.JComboBox<>();
        comboCarrera1 = new javax.swing.JComboBox<>();
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
        jLabel8 = new javax.swing.JLabel();
        comboCarrera = new javax.swing.JComboBox<>();

        jButton3.setText("jButton3");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Seleccionar Carrera");

        jLabel2.setText("Estudiantes");

        aceptar.setText("Aceptar");
        aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarActionPerformed(evt);
            }
        });

        btntutor.setText("Aceptar Tutor");
        btntutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntutorActionPerformed(evt);
            }
        });

        jLabel3.setText("Tutorados");

        jLabel9.setText("Seleccionar Tutor");

        btncarrera.setText("Aceptar Carrera");
        btncarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncarreraActionPerformed(evt);
            }
        });

        ltutores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        comboCarrera1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ltutores, 0, 224, Short.MAX_VALUE)
                            .addComponent(comboCarrera1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(43, 43, 43))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btntutor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btncarrera, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel3)))
                .addGap(83, 83, 83))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(289, 289, 289)
                .addComponent(aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(selectorListas1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboCarrera1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncarrera))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(ltutores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btntutor))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectorListas1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(aceptar)
                .addContainerGap(90, Short.MAX_VALUE))
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

        jLabel8.setText("Carrera");

        comboCarrera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboCarrera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboCarreraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addGap(49, 49, 49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtnumcontrol, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(combogenero, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtnombre)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                    .addComponent(comboCarrera, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(205, 205, 205))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtnumcontrol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(combogenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboCarrera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btnregistrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    }//GEN-LAST:event_txtnombreActionPerformed

    private void btntutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntutorActionPerformed
        if (ltutores.getSelectedItem().equals(SELECCIONA)) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor válido.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la selección del tutor " + ltutores.getSelectedItem() + "?",
                "Confirmar tutor",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Obtener el tutor seleccionado
            String nombreTutor = (String) ltutores.getSelectedItem();
            tutor = tutores.stream()
                    .filter(t -> t.getNombre().equals(nombreTutor))
                    .findFirst().orElse(null);

            if (tutor == null) {
                JOptionPane.showMessageDialog(this, "Tutor no válido.");
                return;
            }

            // Actualizar interfaz
            btntutor.setText(SELECCIONADO);
            btntutor.setEnabled(false);
            ltutores.setEnabled(false);

            // Cargar estudiantes SOLO AHORA, después de aceptar tutor
            cargarEstudiantesPorCarreraYTutor(tutor);

            // Habilitar el botón final de aceptar
            aceptar.setEnabled(true);
        }
    }//GEN-LAST:event_btntutorActionPerformed

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

        // Variables para rastrear cambios
        boolean cambiosRealizados = false;
        int asignaciones = 0;
        int desasignaciones = 0;

        // Procesar estudiantes que fueron movidos a tutorados (asignar tutor)
        for (String nombreTutorado : tutoradosActuales) {
            Tutorado t = tutorado_nom.get(nombreTutorado);
            if (t != null && (t.getIdtutor() == null || !t.getIdtutor().equals(tutor))) {
                t.setIdtutor(tutor);
                try {
                    cTutorado.edit(t);
                    System.out.println("Asignado: " + t.getNombre() + " → " + tutor.getNombre());
                    cambiosRealizados = true;
                    asignaciones++;
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
                    cambiosRealizados = true;
                    desasignaciones++;
                } catch (Exception ex) {
                    Logger.getLogger(ITutorado.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this,
                            "Error al remover tutorado: " + t.getNombre(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Mostrar mensaje apropiado según si hubo cambios o no
        if (cambiosRealizados) {
            String mensaje = "Cambios guardados correctamente.\n";
            if (asignaciones > 0) {
                mensaje += "Estudiantes asignados: " + asignaciones + "\n";
            }
            if (desasignaciones > 0) {
                mensaje += "Estudiantes desasignados: " + desasignaciones;
            }

            JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se realizaron cambios. No hay estudiantes para asignar/remover.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
        }

        // Actualizar la vista
        cargarEstudiantes();
        cargarTablaEstudiantes();

        // Restaurar para poder elegir otro tutor
        ltutores.setEnabled(true);
        btntutor.setText("Añadir Tutor");
        btntutor.setEnabled(true);
        ltutores.setSelectedItem(SELECCIONA);

        cargarEstudiantes();
        selectorListas1.setBotonDerechaHabilitado(false);

        // Restaurar selección de carrera y tutor
        comboCarrera1.setEnabled(true);
        btncarrera.setEnabled(true);
        comboCarrera1.setSelectedIndex(0);
        ltutores.setEnabled(false);
        ltutores.removeAllItems();
        ltutores.addItem(SELECCIONA);
        btntutor.setEnabled(false);
        btntutor.setText("Añadir Tutor");
    }//GEN-LAST:event_aceptarActionPerformed

    private void btnregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnregistrarActionPerformed
        if (!validarCamposRegistro()) {
            mostrarErroresRegistro();
            return;
        }

        String nombre = txtnombre.getText().trim();
        String nc = txtnumcontrol.getText().trim();
        java.util.Date fecha = jDateChooser.getDate();
        String genero = (String) combogenero.getSelectedItem();
        String carrera = (String) comboCarrera.getSelectedItem();

        if (enModoEdicion && tutoradoSeleccionado != null) {
            // MODIFICACIÓN
            tutoradoSeleccionado.setNombre(nombre);
            tutoradoSeleccionado.setFechanac(fecha);
            tutoradoSeleccionado.setGenero(genero.charAt(0));
            tutoradoSeleccionado.setNc(nc);
            tutoradoSeleccionado.setCarrera(carrera);

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
            nuevo.setCarrera(carrera);

            try {
                cTutorado.create(nuevo);
                JOptionPane.showMessageDialog(this, "Tutorado registrado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        // Actualización controlada
        listaTutorados = cTutorado.findTutoradoEntities();
        cargarTablaEstudiantes(); // Solo actualiza la tabla

        // Si hay un tutor seleccionado, recarga las listas FILTRADAS
        if (tutor != null) {
            cargarEstudiantesPorCarreraYTutor(tutor);
        } else {
            // Si no hay tutor seleccionado, deja las listas vacías
            selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());
        }

        limpiarFormulario();
        btnregistrar.setEnabled(false);
    }//GEN-LAST:event_btnregistrarActionPerformed

    private void txtnombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnombreKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isLetter(c) && c != ' ') {
            evt.consume();
        }
    }//GEN-LAST:event_txtnombreKeyTyped

    private void comboCarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboCarreraActionPerformed

    }//GEN-LAST:event_comboCarreraActionPerformed

    private void btncarreraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncarreraActionPerformed
        String carreraSeleccionada = (String) comboCarrera1.getSelectedItem();

        if (carreraSeleccionada.equals("Selecciona Carrera")) {
            JOptionPane.showMessageDialog(this, "Selecciona una carrera válida.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Agregar confirmación de carrera
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas la selección de la carrera " + carreraSeleccionada + "?",
                "Confirmar carrera",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return; // El usuario canceló la selección
        }

        // Limpiar combo de tutores
        ltutores.removeAllItems();
        ltutores.addItem(SELECCIONA);

        // Cargar solo nombres de tutores de esa carrera (sin cargar estudiantes aún)
        int tutoresEncontrados = 0;
        for (Tutor t : tutores) {
            if (t.getCarrera() != null && t.getCarrera().equals(carreraSeleccionada)) {
                ltutores.addItem(t.getNombre());
                tutoresEncontrados++;
            }
        }

        if (tutoresEncontrados > 0) {
            // Caso con tutores disponibles
            ltutores.setEnabled(true); // Habilitar combo de tutores
            comboCarrera1.setEnabled(false); // Bloquear combo de carrera
            btncarrera.setText("Carrera Seleccionada");
            btncarrera.setEnabled(false);

            // Asegurar que las listas de estudiantes estén vacías
            selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());
        } else {
            // Caso sin tutores disponibles
            JOptionPane.showMessageDialog(this,
                    "No hay tutores disponibles para esta carrera. Por favor seleccione otra.",
                    "Sin tutores",
                    JOptionPane.WARNING_MESSAGE);

            // Resetear selección
            comboCarrera1.setSelectedIndex(0);
            ltutores.setEnabled(false);
            btntutor.setEnabled(false);
            comboCarrera1.setEnabled(true); // Permitir nueva selección
            btncarrera.setEnabled(false);
            btncarrera.setText("Aceptar Carrera");

            // Mantener listas vacías
            selectorListas1.setListas(new ArrayList<>(), new ArrayList<>());
        }
    }//GEN-LAST:event_btncarreraActionPerformed

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
    private javax.swing.JButton btncarrera;
    private javax.swing.JButton btnregistrar;
    private javax.swing.JButton btntutor;
    private javax.swing.JComboBox<String> comboCarrera;
    private javax.swing.JComboBox<String> comboCarrera1;
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
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
