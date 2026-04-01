package views;

import dao.VolDAO;
import dao.PiloteDAO;
import dao.AvionDAO;
import models.Vol;
import models.Pilote;
import models.Avion;
import models.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VolManagementFrame extends JFrame {

    /* ---------- PALETTE DE COULEURS COHÉRENTE ---------- */
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color PRIMARY_COLOR = new Color(15, 82, 186);
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color INFO_COLOR = new Color(23, 162, 184);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 10);
    private static final Color TABLE_HEADER_BG = new Color(248, 249, 250);
    private static final Color TABLE_ROW_HOVER = new Color(248, 249, 250);
    private static final Color TABLE_ROW_SELECTED = new Color(15, 82, 186, 20);

    /* ---------- DÉGRADÉ BOUTON ---------- */
    private static final Color BTN_GRAD_START = new Color(15, 82, 186);
    private static final Color BTN_GRAD_END = new Color(20, 108, 240);
    private static final Color BTN_HOVER_START = new Color(12, 70, 165);
    private static final Color BTN_HOVER_END = new Color(17, 92, 205);

    private DefaultTableModel tableModel;
    private JTable volTable;
    private final VolDAO volDAO;
    private PiloteDAO piloteDAO;
    private AvionDAO avionDAO;
    private JTextField txtSearch;
    private User currentUser;
    private JPanel statsPanel;
    private JComboBox<String> cmbStatutFilter;
    private int lastSelectedRow = -1; // Ajout de cette variable pour garder la trace de la sélection

    public VolManagementFrame(User user) {
        this.currentUser = user;
        this.volDAO = new VolDAO();
        this.piloteDAO = new PiloteDAO();
        this.avionDAO = new AvionDAO();
        
        initComponents();
        setupUI();
        loadVols();
        updateStats();
        
    }

    /* ===================== INITIALISATION ===================== */
    
    private void initComponents() {
        setTitle("Gestion des Vols - " + currentUser.getNomCompagnie());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        
        // Panel principal avec layout BorderLayout
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        /* ========== BARRE SUPÉRIEURE ========== */
        JPanel topBar = createTopBar();
        
        /* ========== STATISTIQUES ========== */
        statsPanel = createStatsPanel();
        
        /* ========== ZONE DE RECHERCHE ========== */
        JPanel searchPanel = createSearchPanel();
        
        /* ========== TABLEAU ========== */
        JPanel tablePanel = createTablePanel();
        
        /* ========== BARRE D'OUTILS ========== */
        JPanel toolbarPanel = createToolbarPanel();

        /* ========== ASSEMBLAGE FINAL ========== */
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        contentPanel.add(topBar, BorderLayout.NORTH);
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(toolbarPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Raccourci clavier pour fermer la fenêtre
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_BACKGROUND);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitle = new JLabel("Gestion des Vols");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        
        JLabel lblCompany = new JLabel("Compagnie : " + currentUser.getNomCompagnie());
        lblCompany.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCompany.setForeground(TEXT_SECONDARY);
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(lblTitle);
        leftPanel.add(lblCompany);
        
        JLabel lblDate = new JLabel("Date : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDate.setForeground(TEXT_SECONDARY);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(lblDate, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Les statistiques seront mises à jour dynamiquement
        JLabel lblTotal = createStatLabel("Total : 0", PRIMARY_COLOR);
        JLabel lblProgrammes = createStatLabel("Programmés : 0", INFO_COLOR);
        JLabel lblEnCours = createStatLabel("En cours : 0", WARNING_COLOR);
        JLabel lblTermines = createStatLabel("Terminés : 0", SUCCESS_COLOR);
        JLabel lblAnnules = createStatLabel("Annulés : 0", DANGER_COLOR);
        
        panel.add(lblTotal);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblProgrammes);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblEnCours);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblTermines);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(lblAnnules);
        
        return panel;
    }
    
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(color);
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return label;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Recherche :");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_PRIMARY);
        
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtSearch.addActionListener(e -> searchVols());
        
        JButton btnSearch = new ModernButton("Rechercher");
        btnSearch.addActionListener(e -> searchVols());
        
        leftPanel.add(lblSearch);
        leftPanel.add(txtSearch);
        leftPanel.add(btnSearch);
        
        // Filtre par statut
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JLabel lblFilter = new JLabel("Filtrer par statut :");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(TEXT_PRIMARY);
        
        cmbStatutFilter = new JComboBox<>(new String[]{"Tous", "programme", "en_cours", "termine", "annule"});
        cmbStatutFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatutFilter.setBackground(Color.WHITE);
        cmbStatutFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbStatutFilter.addActionListener(e -> filterVols());
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatutFilter.setSelectedIndex(0);
            loadVols();
        });
        
        JButton btnToday = new ModernButton("Vols du Jour");
        btnToday.addActionListener(e -> showTodayVols());
        
        rightPanel.add(lblFilter);
        rightPanel.add(cmbStatutFilter);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(btnToday);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(btnReset);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // En-têtes du tableau
        String[] columns = {
            "ID", "Numéro Vol", "Pilote", "Avion", 
            "Départ", "Arrivée", "Date Départ", "Date Arrivée", "Statut"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêcher l'édition directe
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class; // Toutes les colonnes sont des String
            }
        };
        
        volTable = new JTable(tableModel);
        
        // Personnalisation du tableau
        volTable.setRowHeight(35);
        volTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        volTable.setShowGrid(false);
        volTable.setIntercellSpacing(new Dimension(0, 0));
        volTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Personnalisation de l'en-tête
        JTableHeader header = volTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setReorderingAllowed(false);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = volTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(100); // Numéro Vol
        columnModel.getColumn(2).setPreferredWidth(150); // Pilote
        columnModel.getColumn(3).setPreferredWidth(120); // Avion
        columnModel.getColumn(4).setPreferredWidth(100); // Départ
        columnModel.getColumn(5).setPreferredWidth(100); // Arrivée
        columnModel.getColumn(6).setPreferredWidth(150); // Date Départ
        columnModel.getColumn(7).setPreferredWidth(150); // Date Arrivée
        columnModel.getColumn(8).setPreferredWidth(100); // Statut
        
        // Centreur de cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < volTable.getColumnCount(); i++) {
            volTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderer pour les statuts
        volTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());
        
        // Effet de survol - MODIFICATION ICI
        volTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = volTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    // Appliquer un effet visuel de survol sans sélectionner réellement
                    volTable.setSelectionBackground(TABLE_ROW_HOVER);
                    volTable.setSelectionForeground(TEXT_PRIMARY);
                    // Ne pas sélectionner réellement la ligne ici
                }
            }
        });
        
        volTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Ne pas effacer la sélection actuelle
                // On laisse la sélection telle quelle
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = volTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    if (e.getClickCount() == 2) {
                        // Double-clic pour modifier
                        editVol();
                    } else if (e.getClickCount() == 1) {
                        // Clic simple pour sélectionner
                        volTable.setRowSelectionInterval(row, row);
                        lastSelectedRow = row;
                    }
                }
            }
        });
        
        // ScrollPane avec personnalisation
        JScrollPane scrollPane = new JScrollPane(volTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        String[] buttonLabels = {"Ajouter", "Modifier", "Supprimer", "Actualiser", "Statistiques"};
        Runnable[] buttonActions = {
            this::addVol,
            this::editVol,
            this::deleteVol,
            this::loadVols,
            this::showStatistics
        };
        
        for (int i = 0; i < buttonLabels.length; i++) {
            ModernButton button = new ModernButton(buttonLabels[i]);
            final int index = i;
            button.addActionListener(e -> buttonActions[index].run());
            panel.add(button);
        }
        
        return panel;
    }

    /* ===================== LOGIQUE MÉTIER ===================== */
    
    private void loadVols() {
        SwingWorker<List<Vol>, Void> worker = new SwingWorker<List<Vol>, Void>() {
            @Override
            protected List<Vol> doInBackground() throws Exception {
                return volDAO.getAllVols();
            }
            
            @Override
            protected void done() {
                try {
                    List<Vol> allVols = get();
                    tableModel.setRowCount(0);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    
                    for (Vol v : allVols) {
                        String dateDepart = v.getDateDepart() != null ? 
                            v.getDateDepart().toLocalDateTime().format(formatter) : "";
                        String dateArrivee = v.getDateArrivee() != null ? 
                            v.getDateArrivee().toLocalDateTime().format(formatter) : "";
                        
                        tableModel.addRow(new Object[]{
                            v.getId(),
                            v.getNumeroVol(),
                            v.getPiloteNomComplet() != null ? v.getPiloteNomComplet() : "N/A",
                            v.getAvionModele() != null ? v.getAvionModele() : "N/A",
                            v.getAeroportDepart(),
                            v.getAeroportArrivee(),
                            dateDepart,
                            dateArrivee,
                            v.getStatut()
                        });
                    }
                    
                    updateStats();
                    
                    // Réinitialiser la sélection après chargement
                    lastSelectedRow = -1;
                    volTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(VolManagementFrame.this,
                        "Erreur lors du chargement des vols",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateStats() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int total = 0;
            int programmes = 0;
            int enCours = 0;
            int termines = 0;
            int annules = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<Vol> allVols = volDAO.getAllVols();
                total = allVols.size();
                
                for (Vol v : allVols) {
                    String statut = v.getStatut().toLowerCase();
                    switch (statut) {
                        case "programme":
                            programmes++;
                            break;
                        case "en_cours":
                            enCours++;
                            break;
                        case "termine":
                            termines++;
                            break;
                        case "annule":
                            annules++;
                            break;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                Component[] components = statsPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        String text = label.getText();
                        
                        if (text.startsWith("Total :")) {
                            label.setText("Total : " + total);
                        } else if (text.startsWith("Programmés :")) {
                            label.setText("Programmés : " + programmes);
                        } else if (text.startsWith("En cours :")) {
                            label.setText("En cours : " + enCours);
                        } else if (text.startsWith("Terminés :")) {
                            label.setText("Terminés : " + termines);
                        } else if (text.startsWith("Annulés :")) {
                            label.setText("Annulés : " + annules);
                        }
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void searchVols() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadVols();
            return;
        }
        
        SwingWorker<List<Vol>, Void> worker = new SwingWorker<List<Vol>, Void>() {
            @Override
            protected List<Vol> doInBackground() throws Exception {
                return volDAO.searchVols(keyword);
            }
            
            @Override
            protected void done() {
                try {
                    List<Vol> searchResults = get();
                    tableModel.setRowCount(0);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    
                    for (Vol v : searchResults) {
                        String dateDepart = v.getDateDepart() != null ? 
                            v.getDateDepart().toLocalDateTime().format(formatter) : "";
                        String dateArrivee = v.getDateArrivee() != null ? 
                            v.getDateArrivee().toLocalDateTime().format(formatter) : "";
                        
                        tableModel.addRow(new Object[]{
                            v.getId(),
                            v.getNumeroVol(),
                            v.getPiloteNomComplet() != null ? v.getPiloteNomComplet() : "N/A",
                            v.getAvionModele() != null ? v.getAvionModele() : "N/A",
                            v.getAeroportDepart(),
                            v.getAeroportArrivee(),
                            dateDepart,
                            dateArrivee,
                            v.getStatut()
                        });
                    }
                    
                    // Réinitialiser la sélection après recherche
                    lastSelectedRow = -1;
                    volTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void filterVols() {
        String selectedStatut = (String) cmbStatutFilter.getSelectedItem();
        if ("Tous".equals(selectedStatut)) {
            loadVols();
            return;
        }
        
        SwingWorker<List<Vol>, Void> worker = new SwingWorker<List<Vol>, Void>() {
            @Override
            protected List<Vol> doInBackground() throws Exception {
                return volDAO.getVolsByStatut(selectedStatut);
            }
            
            @Override
            protected void done() {
                try {
                    List<Vol> filteredVols = get();
                    tableModel.setRowCount(0);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    
                    for (Vol v : filteredVols) {
                        String dateDepart = v.getDateDepart() != null ? 
                            v.getDateDepart().toLocalDateTime().format(formatter) : "";
                        String dateArrivee = v.getDateArrivee() != null ? 
                            v.getDateArrivee().toLocalDateTime().format(formatter) : "";
                        
                        tableModel.addRow(new Object[]{
                            v.getId(),
                            v.getNumeroVol(),
                            v.getPiloteNomComplet() != null ? v.getPiloteNomComplet() : "N/A",
                            v.getAvionModele() != null ? v.getAvionModele() : "N/A",
                            v.getAeroportDepart(),
                            v.getAeroportArrivee(),
                            dateDepart,
                            dateArrivee,
                            v.getStatut()
                        });
                    }
                    
                    // Réinitialiser la sélection après filtrage
                    lastSelectedRow = -1;
                    volTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void addVol() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            List<Pilote> pilotes;
            List<Avion> avions;
            
            @Override
            protected Void doInBackground() throws Exception {
                pilotes = piloteDAO.getAllPilotes();
                avions = avionDAO.getAllAvions();
                return null;
            }
            
            @Override
            protected void done() {
                // Créer une boîte de dialogue pour ajouter un vol
                JDialog dialog = new JDialog(VolManagementFrame.this, "Ajouter un nouveau vol", true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(500, 600);
                dialog.setLocationRelativeTo(VolManagementFrame.this);
                
                JPanel formPanel = new JPanel(new GridBagLayout());
                formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                formPanel.setBackground(CARD_BACKGROUND);
                
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                
                JTextField txtNumeroVol = createFormTextField();
                JComboBox<String> cmbPilote = createPiloteComboBox(pilotes);
                JComboBox<String> cmbAvion = createAvionComboBox(avions);
                JTextField txtAeroportDepart = createFormTextField();
                JTextField txtAeroportArrivee = createFormTextField();
                JTextField txtDateDepart = createFormTextField();
                JTextField txtDateArrivee = createFormTextField();
                JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"programme", "en_cours", "termine", "annule"});
                
                // Style pour les combobox
                cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                cmbStatut.setBackground(Color.WHITE);
                cmbStatut.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                
                // Ajouter les champs au formulaire
                gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
                formPanel.add(createFormLabel("Numéro Vol *"), gbc);
                gbc.gridx = 1; gbc.weightx = 0.7;
                formPanel.add(txtNumeroVol, gbc);
                
                gbc.gridx = 0; gbc.gridy = 1;
                formPanel.add(createFormLabel("Pilote *"), gbc);
                gbc.gridx = 1;
                formPanel.add(cmbPilote, gbc);
                
                gbc.gridx = 0; gbc.gridy = 2;
                formPanel.add(createFormLabel("Avion *"), gbc);
                gbc.gridx = 1;
                formPanel.add(cmbAvion, gbc);
                
                gbc.gridx = 0; gbc.gridy = 3;
                formPanel.add(createFormLabel("Aéroport Départ *"), gbc);
                gbc.gridx = 1;
                formPanel.add(txtAeroportDepart, gbc);
                
                gbc.gridx = 0; gbc.gridy = 4;
                formPanel.add(createFormLabel("Aéroport Arrivée *"), gbc);
                gbc.gridx = 1;
                formPanel.add(txtAeroportArrivee, gbc);
                
                gbc.gridx = 0; gbc.gridy = 5;
                formPanel.add(createFormLabel("Date Départ (AAAA-MM-JJ HH:MM:SS) *"), gbc);
                gbc.gridx = 1;
                formPanel.add(txtDateDepart, gbc);
                
                gbc.gridx = 0; gbc.gridy = 6;
                formPanel.add(createFormLabel("Date Arrivée (AAAA-MM-JJ HH:MM:SS)"), gbc);
                gbc.gridx = 1;
                formPanel.add(txtDateArrivee, gbc);
                
                gbc.gridx = 0; gbc.gridy = 7;
                formPanel.add(createFormLabel("Statut"), gbc);
                gbc.gridx = 1;
                formPanel.add(cmbStatut, gbc);
                
                // Boutons
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                buttonPanel.setBackground(CARD_BACKGROUND);
                
                JButton btnSave = new ModernButton("Enregistrer");
                btnSave.addActionListener(e -> {
                    if (saveVol(null, txtNumeroVol, cmbPilote, cmbAvion, txtAeroportDepart, 
                                txtAeroportArrivee, txtDateDepart, txtDateArrivee, cmbStatut)) {
                        dialog.dispose();
                        loadVols();
                    }
                });
                
                JButton btnCancel = new JButton("Annuler");
                btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                btnCancel.setForeground(TEXT_SECONDARY);
                btnCancel.setBorderPainted(false);
                btnCancel.setContentAreaFilled(false);
                btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnCancel.addActionListener(e -> dialog.dispose());
                
                buttonPanel.add(btnSave);
                buttonPanel.add(btnCancel);
                
                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        };
        worker.execute();
    }
    
    private void editVol() {
        int selectedRow = volTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un vol à modifier",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int volId = (int) tableModel.getValueAt(selectedRow, 0);
        String numeroVol = (String) tableModel.getValueAt(selectedRow, 1);
        String piloteInfo = (String) tableModel.getValueAt(selectedRow, 2);
        String avionInfo = (String) tableModel.getValueAt(selectedRow, 3);
        String aeroportDepart = (String) tableModel.getValueAt(selectedRow, 4);
        String aeroportArrivee = (String) tableModel.getValueAt(selectedRow, 5);
        String dateDepart = (String) tableModel.getValueAt(selectedRow, 6);
        String dateArrivee = (String) tableModel.getValueAt(selectedRow, 7);
        String statut = (String) tableModel.getValueAt(selectedRow, 8);
        
        // Récupérer le vol complet de la base de données
        SwingWorker<Vol, Void> worker = new SwingWorker<Vol, Void>() {
            Vol vol;
            List<Pilote> pilotes;
            List<Avion> avions;
            
            @Override
            protected Vol doInBackground() throws Exception {
                vol = volDAO.getVolById(volId);
                pilotes = piloteDAO.getAllPilotes();
                avions = avionDAO.getAllAvions();
                return vol;
            }
            
            @Override
            protected void done() {
                try {
                    vol = get();
                    
                    if (vol != null) {
                        // Créer une boîte de dialogue pour modifier le vol
                        JDialog dialog = new JDialog(VolManagementFrame.this, "Modifier le vol", true);
                        dialog.setLayout(new BorderLayout());
                        dialog.setSize(500, 600);
                        dialog.setLocationRelativeTo(VolManagementFrame.this);
                        
                        JPanel formPanel = new JPanel(new GridBagLayout());
                        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                        formPanel.setBackground(CARD_BACKGROUND);
                        
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.insets = new Insets(10, 10, 10, 10);
                        gbc.fill = GridBagConstraints.HORIZONTAL;
                        
                        JTextField txtNumeroVol = createFormTextField();
                        txtNumeroVol.setText(vol.getNumeroVol());
                        
                        JComboBox<String> cmbPilote = createPiloteComboBox(pilotes);
                        selectInComboBox(cmbPilote, vol.getPiloteId());
                        
                        JComboBox<String> cmbAvion = createAvionComboBox(avions);
                        selectInComboBox(cmbAvion, vol.getAvionId());
                        
                        JTextField txtAeroportDepart = createFormTextField();
                        txtAeroportDepart.setText(vol.getAeroportDepart());
                        
                        JTextField txtAeroportArrivee = createFormTextField();
                        txtAeroportArrivee.setText(vol.getAeroportArrivee());
                        
                        JTextField txtDateDepart = createFormTextField();
                        txtDateDepart.setText(vol.getDateDepart() != null ? vol.getDateDepart().toString() : "");
                        
                        JTextField txtDateArrivee = createFormTextField();
                        txtDateArrivee.setText(vol.getDateArrivee() != null ? vol.getDateArrivee().toString() : "");
                        
                        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"programme", "en_cours", "termine", "annule"});
                        cmbStatut.setSelectedItem(vol.getStatut());
                        
                        // Style pour les combobox
                        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        cmbStatut.setBackground(Color.WHITE);
                        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                                BorderFactory.createEmptyBorder(5, 10, 5, 10)
                        ));
                        
                        // Ajouter les champs au formulaire (même structure que pour l'ajout)
                        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
                        formPanel.add(createFormLabel("Numéro Vol *"), gbc);
                        gbc.gridx = 1; gbc.weightx = 0.7;
                        formPanel.add(txtNumeroVol, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 1;
                        formPanel.add(createFormLabel("Pilote *"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(cmbPilote, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 2;
                        formPanel.add(createFormLabel("Avion *"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(cmbAvion, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 3;
                        formPanel.add(createFormLabel("Aéroport Départ *"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(txtAeroportDepart, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 4;
                        formPanel.add(createFormLabel("Aéroport Arrivée *"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(txtAeroportArrivee, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 5;
                        formPanel.add(createFormLabel("Date Départ (AAAA-MM-JJ HH:MM:SS) *"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(txtDateDepart, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 6;
                        formPanel.add(createFormLabel("Date Arrivée (AAAA-MM-JJ HH:MM:SS)"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(txtDateArrivee, gbc);
                        
                        gbc.gridx = 0; gbc.gridy = 7;
                        formPanel.add(createFormLabel("Statut"), gbc);
                        gbc.gridx = 1;
                        formPanel.add(cmbStatut, gbc);
                        
                        // Boutons
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                        buttonPanel.setBackground(CARD_BACKGROUND);
                        
                        JButton btnSave = new ModernButton("Enregistrer");
                        btnSave.addActionListener(e -> {
                            if (saveVol(vol, txtNumeroVol, cmbPilote, cmbAvion, txtAeroportDepart,
                                    txtAeroportArrivee, txtDateDepart, txtDateArrivee, cmbStatut)) {
                                dialog.dispose();
                                loadVols();
                            }
                        });
                        
                        JButton btnCancel = new JButton("Annuler");
                        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        btnCancel.setForeground(TEXT_SECONDARY);
                        btnCancel.setBorderPainted(false);
                        btnCancel.setContentAreaFilled(false);
                        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btnCancel.addActionListener(e -> dialog.dispose());
                        
                        buttonPanel.add(btnSave);
                        buttonPanel.add(btnCancel);
                        
                        dialog.add(formPanel, BorderLayout.CENTER);
                        dialog.add(buttonPanel, BorderLayout.SOUTH);
                        dialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(VolManagementFrame.this,
                                "Vol non trouvé",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(VolManagementFrame.this,
                        "Erreur lors du chargement des informations du vol",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private JComboBox<String> createPiloteComboBox(List<Pilote> pilotes) {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("-- Sélectionner un pilote --");
        for (Pilote p : pilotes) {
            combo.addItem(p.getNom() + " " + p.getPrenom() + " [" + p.getId() + "]");
        }
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return combo;
    }
    
    private JComboBox<String> createAvionComboBox(List<Avion> avions) {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("-- Sélectionner un avion --");
        for (Avion a : avions) {
            combo.addItem(a.getModele() + " (" + a.getImmatriculation() + ") [" + a.getId() + "]");
        }
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return combo;
    }
    
    private void selectInComboBox(JComboBox<String> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.contains("[" + id + "]")) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private int extractIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null && selected.contains("[")) {
            try {
                String idStr = selected.substring(selected.indexOf("[") + 1, selected.indexOf("]"));
                return Integer.parseInt(idStr);
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }
    
    private boolean saveVol(Vol vol, JTextField txtNumeroVol, JComboBox<String> cmbPilote, 
                           JComboBox<String> cmbAvion, JTextField txtAeroportDepart,
                           JTextField txtAeroportArrivee, JTextField txtDateDepart,
                           JTextField txtDateArrivee, JComboBox<String> cmbStatut) {
        
        // Validation des champs obligatoires
        if (txtNumeroVol.getText().trim().isEmpty() ||
            cmbPilote.getSelectedIndex() == 0 ||
            cmbAvion.getSelectedIndex() == 0 ||
            txtAeroportDepart.getText().trim().isEmpty() ||
            txtAeroportArrivee.getText().trim().isEmpty() ||
            txtDateDepart.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires (*)",
                "Champs manquants",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Vol v = (vol == null) ? new Vol() : vol;
            
            v.setNumeroVol(txtNumeroVol.getText().trim());
            v.setPiloteId(extractIdFromCombo(cmbPilote));
            v.setAvionId(extractIdFromCombo(cmbAvion));
            v.setAeroportDepart(txtAeroportDepart.getText().trim());
            v.setAeroportArrivee(txtAeroportArrivee.getText().trim());
            v.setDateDepart(Timestamp.valueOf(txtDateDepart.getText().trim()));
            
            if (!txtDateArrivee.getText().trim().isEmpty()) {
                v.setDateArrivee(Timestamp.valueOf(txtDateArrivee.getText().trim()));
            }
            
            v.setStatut((String) cmbStatut.getSelectedItem());
            
            // Sauvegarde
            boolean success;
            if (vol == null) {
                success = volDAO.addVol(v);
            } else {
                success = volDAO.updateVol(v);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Vol enregistré avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Format de date invalide. Utilisez AAAA-MM-JJ HH:MM:SS",
                "Erreur de format",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void deleteVol() {
        int selectedRow = volTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un vol à supprimer",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int volId = (int) tableModel.getValueAt(selectedRow, 0);
        String numeroVol = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le vol " + numeroVol + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return volDAO.deleteVol(volId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(VolManagementFrame.this,
                                "Vol supprimé avec succès",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                            loadVols();
                        } else {
                            JOptionPane.showMessageDialog(VolManagementFrame.this,
                                "Erreur lors de la suppression du vol",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void showTodayVols() {
        SwingWorker<List<Vol>, Void> worker = new SwingWorker<List<Vol>, Void>() {
            @Override
            protected List<Vol> doInBackground() throws Exception {
                java.util.Date today = new java.util.Date();
                Timestamp start = new Timestamp(today.getTime()); 
                start.setHours(0); start.setMinutes(0); start.setSeconds(0);
                Timestamp end = new Timestamp(today.getTime()); 
                end.setHours(23); end.setMinutes(59); end.setSeconds(59);
                return volDAO.getVolsByDateRange(start, end);
            }
            
            @Override
            protected void done() {
                try {
                    List<Vol> todayVols = get();
                    if (todayVols.isEmpty()) {
                        JOptionPane.showMessageDialog(VolManagementFrame.this,
                            "Aucun vol programmé pour aujourd'hui",
                            "Vols du jour",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    StringBuilder message = new StringBuilder();
                    message.append("<html><body style='width: 400px;'>");
                    message.append("<h3>Vols programmés pour aujourd'hui</h3>");
                    message.append("<table border='0' cellpadding='5'>");
                    message.append("<tr><th>Vol</th><th>Départ</th><th>Arrivée</th><th>Heure</th><th>Statut</th></tr>");
                    
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    
                    for (Vol v : todayVols) {
                        String heureDepart = v.getDateDepart() != null ? 
                            v.getDateDepart().toLocalDateTime().format(timeFormatter) : "";
                        
                        message.append(String.format(
                            "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                            v.getNumeroVol(),
                            v.getAeroportDepart(),
                            v.getAeroportArrivee(),
                            heureDepart,
                            v.getStatut()
                        ));
                    }
                    
                    message.append("</table></body></html>");
                    
                    JOptionPane.showMessageDialog(VolManagementFrame.this,
                        message.toString(),
                        "Vols du jour",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int total = 0;
            int programmes = 0;
            int enCours = 0;
            int termines = 0;
            int annules = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<Vol> allVols = volDAO.getAllVols();
                total = allVols.size();
                
                for (Vol v : allVols) {
                    String statut = v.getStatut().toLowerCase();
                    switch (statut) {
                        case "programme":
                            programmes++;
                            break;
                        case "en_cours":
                            enCours++;
                            break;
                        case "termine":
                            termines++;
                            break;
                        case "annule":
                            annules++;
                            break;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    double pourcentageProgrammes = total > 0 ? (programmes * 100.0) / total : 0;
                    double pourcentageEnCours = total > 0 ? (enCours * 100.0) / total : 0;
                    double pourcentageTermines = total > 0 ? (termines * 100.0) / total : 0;
                    double pourcentageAnnules = total > 0 ? (annules * 100.0) / total : 0;
                    
                    String message = String.format(
                        "<html><body style='width: 350px;'>" +
                        "<h3>📊 Statistiques des Vols</h3>" +
                        "<p><b>Total :</b> %d vols</p>" +
                        "<p><b>Programmés :</b> %d (%.1f%%)</p>" +
                        "<p><b>En cours :</b> %d (%.1f%%)</p>" +
                        "<p><b>Terminés :</b> %d (%.1f%%)</p>" +
                        "<p><b>Annulés :</b> %d (%.1f%%)</p>" +
                        "<br>" +
                        "<p><b>Taux de réussite :</b> %.1f%%</p>" +
                        "</body></html>",
                        total,
                        programmes, pourcentageProgrammes,
                        enCours, pourcentageEnCours,
                        termines, pourcentageTermines,
                        annules, pourcentageAnnules,
                        pourcentageTermines
                    );
                    
                    JOptionPane.showMessageDialog(VolManagementFrame.this,
                        message,
                        "Statistiques",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private JTextField createFormTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    /* ===================== CLASSES PERSONNALISÉES ===================== */
    
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color color1 = new Color(248, 249, 250);
            Color color2 = new Color(233, 236, 239);
            
            GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    private static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBackground(PRIMARY_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
            ));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(PRIMARY_COLOR.darker());
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(PRIMARY_COLOR);
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    setBackground(PRIMARY_COLOR.darker().darker());
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    setBackground(PRIMARY_COLOR.darker());
                }
            });
        }
    }
    
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString().toLowerCase();
                setHorizontalAlignment(JLabel.CENTER);
                
                switch (status) {
                    case "programme":
                        setForeground(INFO_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "en_cours":
                        setForeground(WARNING_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "termine":
                        setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "annule":
                        setForeground(DANGER_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    default:
                        setForeground(TEXT_PRIMARY);
                }
            }
            
            return c;
        }
    }

    /* ===================== MÉTHODES UTILITAIRES ===================== */
    
    private void setupUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            // Continuer avec le look and feel par défaut
        }
    }
}