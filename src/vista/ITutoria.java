package vista;

import control.CitaJpaController;
import control.TutorJpaController;
import control.TutoriaJpaController;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import modelo.AsistenciaRenderer;
import modelo.Cita;
import modelo.DatosTablaCitas;
import modelo.MTablaCita;
import modelo.Tutor;
import modelo.Tutorado;
import modelo.Tutoria;

public class ITutoria extends javax.swing.JDialog {

    private TutorJpaController cTutor;
    private CitaJpaController cCita;
    private TutoriaJpaController cTutoria;
    private Tutor tutorActual;

    private List<Tutor> tutores;

    public ITutoria(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
            String fechaHoraActual = sdf.format(new Date());
            labelfecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelfecha.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            labelfecha.setText("Fecha " + fechaHoraActual);
        });
        timer.start();

        tablaasistencias.setModel(new MTablaCita(new ArrayList<>()));

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        cTutor = new TutorJpaController(emf);
        cCita = new CitaJpaController(emf);
        cTutoria = new TutoriaJpaController(emf);
        tutores = cTutor.findTutorEntities();
        llenarDatosTutor();
    }

    //construcotr nuevo
    public ITutoria(java.awt.Dialog parent, boolean modal, Tutor tutor) {
        super(parent, modal);
        initComponents();

        // Inicia el reloj como en el original
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
            String fechaHoraActual = sdf.format(new Date());
            labelfecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            labelfecha.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            labelfecha.setText("Fecha " + fechaHoraActual);
        });
        timer.start();

        // Inicializa tabla vacía
        tablaasistencias.setModel(new MTablaCita(new ArrayList<>()));

        // Cierra ventana correctamente
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                dispose();
            }
        });

        // Inicializa controladores
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Tutoria3PU");
        cTutor = new TutorJpaController(emf);
        cCita = new CitaJpaController(emf);
        cTutoria = new TutoriaJpaController(emf);

        // Guarda tutor actual recibido
        this.tutorActual = tutor;

        // Cargar y ordenar tutores si necesitas toda la lista
        tutores = cTutor.findTutorEntities();
        tutores.sort((t1, t2) -> t1.getIdpersona().compareTo(t2.getIdpersona()));

        // Llenar datos del tutor recibido
        llenarDatosTutorSeleccionado(tutor);
    }

    public void llenarDatosTutor() {
        // Ordenar tutores por ID
        tutores.sort((t1, t2) -> t1.getIdpersona().compareTo(t2.getIdpersona()));

        // Llenar mapa con tutores ordenados
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        for (Tutor t : tutores) {
            mapa.put(t.getNombre(), new ArrayList<>()); // fechas vacías
        }

        // Establecer mensajes y datos
        seleccion1.setMensajes("Selecciona un tutor", "Selecciona una fecha");
        seleccion1.setDatos(mapa);
    }

    private void llenarDatosTutorSeleccionado(Tutor tutor) {
        // Creamos un mapa con un solo tutor y su lista de citas del día
        Map<String, List<String>> mapa = new LinkedHashMap<>();

        List<String> fechasHoras = new ArrayList<>();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String hoy = formatoFecha.format(new Date());

        // Recorremos las citas del tutor recibido
        if (tutor.getCitaList() != null) {
            for (Cita cita : tutor.getCitaList()) {
                if (cita.getFecha() != null) {
                    String fechaCita = formatoFecha.format(cita.getFecha());
                    if (fechaCita.equals(hoy)) {
                        String hora = String.format("%02d:00", cita.getHora()); // HH:00
                        fechasHoras.add(fechaCita + " - " + hora);
                    }
                }
            }
        }

        // Ordenamos las fechasHoras por fecha y hora
        fechasHoras.sort((fh1, fh2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                return sdf.parse(fh1).compareTo(sdf.parse(fh2));
            } catch (Exception e) {
                return 0;
            }
        });

        // Si no hay citas para hoy, agregamos mensaje vacío
        if (fechasHoras.isEmpty()) {
            fechasHoras.add("Sin citas programadas para hoy");
        }

        // Llenar el mapa con el nombre del tutor y sus citas
        mapa.put(tutor.getNombre(), fechasHoras);

        // Configurar los datos en el componente seleccion1
        seleccion1.setMensajes("Selecciona un Tutor", "Citas disponibles para hoy");
        seleccion1.setDatos(mapa);
    }

    public void actualizarTutoresYCitas() {
        // Recarga los tutores desde la base de datos
        tutores = cTutor.findTutorEntities();
        tutores.sort((t1, t2) -> t1.getIdpersona().compareTo(t2.getIdpersona()));
        llenarDatosTutor(); // Vuelve a llenar el componente con tutores actualizados
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        AceptarCita = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaasistencias = new javax.swing.JTable();
        RegistrarTutoria = new javax.swing.JButton();
        AceptarTutor = new javax.swing.JButton();
        seleccion1 = new combovisual.Seleccion();
        labelfecha = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("Realización de una Tutoria");

        jLabel2.setText("Tutor");

        jLabel3.setText("Cita");

        AceptarCita.setText("Aceptar");
        AceptarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceptarCitaActionPerformed(evt);
            }
        });

        jLabel4.setText("Asistencia");

        tablaasistencias.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tablaasistencias);

        RegistrarTutoria.setText("Registrar Tutoria");
        RegistrarTutoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegistrarTutoriaActionPerformed(evt);
            }
        });

        AceptarTutor.setText("Aceptar");
        AceptarTutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AceptarTutorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addComponent(seleccion1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AceptarTutor)
                            .addComponent(AceptarCita)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(RegistrarTutoria)))
                .addContainerGap(91, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(54, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelfecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 181, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelfecha, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 7, Short.MAX_VALUE)
                        .addComponent(AceptarTutor)
                        .addGap(18, 18, 18)
                        .addComponent(AceptarCita)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(19, 19, 19))
                            .addComponent(seleccion1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RegistrarTutoria)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AceptarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AceptarCitaActionPerformed
        String nombreTutor = seleccion1.getSeleccion1();

        if (nombreTutor == null || nombreTutor.equals("Selecciona un tutor")) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor válido");
            return;
        }

        // Buscar el tutor
        Tutor tutorSeleccionado = null;
        for (Tutor t : tutores) {
            if (t.getNombre().equals(nombreTutor)) {
                tutorSeleccionado = t;
                break;
            }
        }

        if (tutorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Tutor no encontrado");
            return;
        }

        // Obtener la fecha y hora seleccionadas
        String seleccion = seleccion1.getSeleccion2(); // Ejemplo: "15/05/2025 - 13:00"
        if (seleccion == null || seleccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una fecha y hora");
            return;
        }

        String[] partes = seleccion.split(" - ");
        if (partes.length != 2) {
            JOptionPane.showMessageDialog(this, "Formato de fecha y hora incorrecto");
            return;
        }

        String fechaStr = partes[0];
        String horaStr = partes[1];

        java.sql.Date fechaSQL = null;
        int hora = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaUtil = sdf.parse(fechaStr);
            fechaSQL = new java.sql.Date(fechaUtil.getTime());
            hora = Integer.parseInt(horaStr.split(":")[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al interpretar fecha y hora: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        EntityManager em = Persistence.createEntityManagerFactory("Tutoria3PU").createEntityManager();
        try {
            // Refrescar el tutor
            tutorSeleccionado = em.merge(tutorSeleccionado);
            em.refresh(tutorSeleccionado);

            // Obtener tutorados actualizados
            Query queryTutorados = em.createQuery("SELECT t FROM Tutorado t WHERE t.idtutor = :tutor");
            queryTutorados.setParameter("tutor", tutorSeleccionado);
            List<Tutorado> tutoradosActualizados = queryTutorados.getResultList();
            tutorSeleccionado.setTutoradoList(tutoradosActualizados);

            // Buscar si existe una cita con esa fecha y hora
            Query queryCita = em.createQuery(
                    "SELECT c FROM Cita c WHERE c.idtutor = :tutor AND c.fecha = :fecha AND c.hora = :hora");
            queryCita.setParameter("tutor", tutorSeleccionado);
            queryCita.setParameter("fecha", fechaSQL);
            queryCita.setParameter("hora", hora);
            List<Cita> citas = queryCita.getResultList();

            ArrayList<DatosTablaCitas> listaDatos = new ArrayList<>();

            if (citas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró la cita con esa fecha y hora.");
                return;
            }

            Cita cita = citas.get(0);

// Obtener todas las tutorías de esta cita
            Query queryTutorias = em.createQuery("SELECT t FROM Tutoria t WHERE t.cita = :cita");
            queryTutorias.setParameter("cita", cita);
            List<Tutoria> tutoriasCita = queryTutorias.getResultList();

// Crear un mapa para saber quién ya tiene tutoría en esta cita
            Map<Integer, Tutoria> tutoriasMap = new HashMap<>();
            for (Tutoria tutoria : tutoriasCita) {
                tutoriasMap.put(tutoria.getTutorado().getIdtuto(), tutoria);
            }

// Cargar todos los tutorados del tutor
            for (Tutorado tutorado : tutoradosActualizados) {
                DatosTablaCitas dato = new DatosTablaCitas(tutorado);
                if (tutoriasMap.containsKey(tutorado.getIdtuto())) {
                    Tutoria tut = tutoriasMap.get(tutorado.getIdtuto());
                    dato.setAsistencia(true);
                    dato.setAccion(tut.getAcciones());
                } else {
                    dato.setAsistencia(false);
                    dato.setAccion("");
                }
                listaDatos.add(dato);
            }

            // Actualizar tabla
            MTablaCita modelo = new MTablaCita(listaDatos);
            tablaasistencias.setModel(modelo);
            tablaasistencias.getColumnModel().getColumn(1).setCellRenderer(new AsistenciaRenderer());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }//GEN-LAST:event_AceptarCitaActionPerformed

    private void AceptarTutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AceptarTutorActionPerformed
        String seleccionado = seleccion1.getSeleccion1();

        if (seleccionado == null || seleccionado.equals("Selecciona un tutor")) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor válido");
            return;
        }

        // Buscar el tutor correspondiente
        Tutor tutorSeleccionado = null;
        for (Tutor t : tutores) {
            if (t.getNombre().equals(seleccionado)) {
                tutorSeleccionado = t;
                break;
            }
        }

        if (tutorSeleccionado != null) {
            // RECARGAMOS el tutor desde la base de datos para obtener la lista actualizada de citas
            Tutor tutorDesdeBD = cTutor.findTutor(tutorSeleccionado.getIdpersona());
            if (tutorDesdeBD != null) {
                tutorSeleccionado.setCitaList(tutorDesdeBD.getCitaList());
            }

            List<String> fechasHoras = new ArrayList<>();

            if (tutorSeleccionado.getCitaList() == null || tutorSeleccionado.getCitaList().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Este tutor no tiene citas agendadas.");
                seleccion1.actualizarCombo2(new ArrayList<>());
                return;
            }

            SimpleDateFormat soloFecha = new SimpleDateFormat("dd/MM/yyyy");
            String hoy = soloFecha.format(new Date()); // Fecha actual

            for (Cita cita : tutorSeleccionado.getCitaList()) {
                if (cita.getFecha() != null) {
                    String fechaCita = soloFecha.format(cita.getFecha());
                    if (fechaCita.equals(hoy)) {
                        // Si la cita es de hoy, agregarla
                        String hora = String.format("%02d:00", cita.getHora()); // HH:00
                        fechasHoras.add(fechaCita + " - " + hora);
                    }
                }
            }

            if (fechasHoras.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Este tutor no tiene citas programadas para el dia de hoy.");
            } else {
                // Ordenar por fecha y hora
                fechasHoras.sort((fh1, fh2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                        return sdf.parse(fh1).compareTo(sdf.parse(fh2));
                    } catch (Exception e) {
                        return 0;
                    }
                });

                seleccion1.actualizarCombo2(fechasHoras);
                JOptionPane.showMessageDialog(this, "Citas cargadas correctamente");
            }
        }

    }//GEN-LAST:event_AceptarTutorActionPerformed

    private void RegistrarTutoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegistrarTutoriaActionPerformed
        String nombreTutor = seleccion1.getSeleccion1();
        String fechaHoraSeleccionada = seleccion1.getSeleccion2(); // Ahora contiene "dd/MM/yyyy - HH:00"

        // Validaciones básicas
        if (nombreTutor == null || nombreTutor.equals("Selecciona un tutor")) {
            JOptionPane.showMessageDialog(this, "Selecciona un tutor válido.");
            return;
        }

        if (fechaHoraSeleccionada == null || fechaHoraSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una cita válida.");
            return;
        }

        // Buscar tutor
        Tutor tutorSeleccionado = tutores.stream()
                .filter(t -> t.getNombre().equals(nombreTutor))
                .findFirst()
                .orElse(null);

        if (tutorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el tutor.");
            return;
        }

        // Parsear fecha y hora de la selección
        String[] partes = fechaHoraSeleccionada.split(" - ");
        if (partes.length != 2) {
            JOptionPane.showMessageDialog(this, "Formato de fecha/hora inválido.");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaSeleccionada = sdf.parse(partes[0]);
            int horaSeleccionada = Integer.parseInt(partes[1].split(":")[0]);

            // Buscar la cita exacta (fecha + hora)
            Cita citaSeleccionada = tutorSeleccionado.getCitaList().stream()
                    .filter(c -> {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(c.getFecha());
                        return sdf.format(c.getFecha()).equals(partes[0])
                                && c.getHora() == horaSeleccionada;
                    })
                    .findFirst()
                    .orElse(null);

            if (citaSeleccionada == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la cita para " + fechaHoraSeleccionada
                        + "\nVerifica que la cita exista en la base de datos.");
                return;
            }

            // Validar modelo de tabla
            if (!(tablaasistencias.getModel() instanceof MTablaCita)) {
                JOptionPane.showMessageDialog(this, "No hay datos para registrar.");
                return;
            }

            MTablaCita modelo = (MTablaCita) tablaasistencias.getModel();
            ArrayList<DatosTablaCitas> datos = modelo.getDatos();

            // Validar que solo un tutorado esté marcado con asistencia
            int contadorAsistenciasNuevas = 0;
            for (DatosTablaCitas dt : datos) {
                if (Boolean.TRUE.equals(dt.getAsistencia())) {
                    // Verificar si ya existe una tutoría para esa cita y tutorado
                    TypedQuery<Tutoria> consultaPrev = Persistence
                            .createEntityManagerFactory("Tutoria3PU")
                            .createEntityManager()
                            .createQuery("SELECT t FROM Tutoria t WHERE t.tutorado = :tutorado AND t.cita = :cita", Tutoria.class);
                    consultaPrev.setParameter("tutorado", dt.getT());
                    consultaPrev.setParameter("cita", citaSeleccionada);

                    if (consultaPrev.getResultList().isEmpty()) {
                        contadorAsistenciasNuevas++;
                    }
                }
            }

            int registrados = 0;
            EntityManager em = null;
            EntityTransaction tx = null;

            try {
                em = Persistence.createEntityManagerFactory("Tutoria3PU").createEntityManager();
                tx = em.getTransaction();
                tx.begin();

                for (DatosTablaCitas dt : datos) {
                    if (Boolean.TRUE.equals(dt.getAsistencia())) {
                        if (dt.getAccion() == null || dt.getAccion().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(this,
                                    "Falta acción para el tutorado: " + dt.getT().getNombre());
                            if (tx.isActive()) {
                                tx.rollback();
                            }
                            return;
                        }

                        // Verificar si ya existe una tutoría para ese tutorado y esa cita
                        TypedQuery<Tutoria> consultaExistente = em.createQuery(
                                "SELECT t FROM Tutoria t WHERE t.tutorado = :tutorado AND t.cita = :cita",
                                Tutoria.class);
                        consultaExistente.setParameter("tutorado", dt.getT());
                        consultaExistente.setParameter("cita", citaSeleccionada);

                        List<Tutoria> resultados = consultaExistente.getResultList();
                        Tutoria tutoria;

                        if (!resultados.isEmpty()) {
                            // Ya existe: actualizarla
                            tutoria = resultados.get(0);
                            tutoria.setAcciones(dt.getAccion());
                            em.merge(tutoria);
                        } else {
                            // No existe: crear nueva
                            tutoria = new Tutoria();
                            tutoria.setAcciones(dt.getAccion());
                            tutoria.setCita(citaSeleccionada);
                            tutoria.setTutorado(dt.getT());
                            em.persist(tutoria);
                        }

                        registrados++;
                    }
                }

                tx.commit();

                if (registrados > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Tutorías registradas correctamente: " + registrados);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No se registró ninguna tutoría (¿faltó marcar asistencia?).");
                }

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                JOptionPane.showMessageDialog(this,
                        "Error al registrar tutorías: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (em != null) {
                    em.close();
                }
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Formato de hora inválido.");
        }

        // --- RESTABLECER CAMPOS Y TABLA ---
        seleccion1.setSeleccion1("Selecciona un tutor"); // Reinicia combo1
        seleccion1.setSeleccion2("Selecciona Fecha");    // Limpia combo2
        seleccion1.setEnabledCombo1(true);               // Habilita combo1 (tutor)
        seleccion1.setEnabledCombo2(true);               // Habilita combo2 (fecha)

        tablaasistencias.setModel(new MTablaCita(new ArrayList<>())); // Vacía la tabla

        AceptarTutor.setEnabled(true);
        AceptarCita.setEnabled(true);
        RegistrarTutoria.setEnabled(true);
    }//GEN-LAST:event_RegistrarTutoriaActionPerformed

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
            java.util.logging.Logger.getLogger(ITutoria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ITutoria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ITutoria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ITutoria.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                ITutoria dialog = new ITutoria(new javax.swing.JDialog(), true);
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
    private javax.swing.JButton AceptarCita;
    private javax.swing.JButton AceptarTutor;
    private javax.swing.JButton RegistrarTutoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel labelfecha;
    private combovisual.Seleccion seleccion1;
    private javax.swing.JTable tablaasistencias;
    // End of variables declaration//GEN-END:variables
}
