package views;

import dao.AvionDAO;
import models.Avion;
import models.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AvionManagementFrame extends JFrame {

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
    private JTable avionTable;
    private AvionDAO avionDAO;
    private JTextField txtSearch;
    private User currentUser;
    private JPanel statsPanel;
    private JComboBox<String> cmbStatutFilter;
    private int lastSelectedRow = -1; // Pour garder la trace de la sélection

    public AvionManagementFrame(User user) {
        this.currentUser = user;
        this.avionDAO = new AvionDAO();
        
        initComponents();
        setupUI();
        loadAvions();
        updateStats();
        
    }

    /* ===================== INITIALISATION ===================== */
    
    private void initComponents() {
        setTitle("Gestion des Avions - " + currentUser.getNomCompagnie());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 800);
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
        
        JLabel lblTitle = new JLabel("Gestion des Avions");
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
        JLabel lblDisponibles = createStatLabel("Disponibles : 0", SUCCESS_COLOR);
        JLabel lblEntretien = createStatLabel("Entretien : 0", WARNING_COLOR);
        JLabel lblHorsService = createStatLabel("Hors Service : 0", DANGER_COLOR);
        
        panel.add(lblTotal);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblDisponibles);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblEntretien);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblHorsService);
        
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
        txtSearch.addActionListener(e -> searchAvions());
        
        JButton btnSearch = new ModernButton("Rechercher");
        btnSearch.addActionListener(e -> searchAvions());
        
        leftPanel.add(lblSearch);
        leftPanel.add(txtSearch);
        leftPanel.add(btnSearch);
        
        // Filtre par statut
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JLabel lblFilter = new JLabel("Filtrer par statut :");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(TEXT_PRIMARY);
        
        cmbStatutFilter = new JComboBox<>(new String[]{"Tous", "disponible", "entretien", "hors_service"});
        cmbStatutFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatutFilter.setBackground(Color.WHITE);
        cmbStatutFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        cmbStatutFilter.addActionListener(e -> filterAvions());
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cmbStatutFilter.setSelectedIndex(0);
            loadAvions();
        });
        
        rightPanel.add(lblFilter);
        rightPanel.add(cmbStatutFilter);
        rightPanel.add(Box.createHorizontalStrut(20));
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
            "ID", "Modèle", "Immatriculation", "Capacité", 
            "Date Mise en Service", "Statut"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Empêcher l'édition directe
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Integer.class; // ID
                    case 3: return Integer.class; // Capacité
                    default: return String.class;
                }
            }
        };
        
        avionTable = new JTable(tableModel);
        
        // Personnalisation du tableau
        avionTable.setRowHeight(40);
        avionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        avionTable.setShowGrid(false);
        avionTable.setIntercellSpacing(new Dimension(0, 0));
        avionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Personnalisation de l'en-tête
        JTableHeader header = avionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setReorderingAllowed(false);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = avionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Modèle
        columnModel.getColumn(2).setPreferredWidth(120); // Immatriculation
        columnModel.getColumn(3).setPreferredWidth(80);  // Capacité
        columnModel.getColumn(4).setPreferredWidth(120); // Date Mise en Service
        columnModel.getColumn(5).setPreferredWidth(100); // Statut
        
        // Centreur de cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < avionTable.getColumnCount(); i++) {
            avionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderer pour les statuts
        avionTable.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        // Effet de survol - MODIFICATION ICI
        avionTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = avionTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    // Ne pas sélectionner la ligne automatiquement, juste changer la couleur de fond temporairement
                    avionTable.setSelectionBackground(TABLE_ROW_HOVER);
                    avionTable.setSelectionForeground(TEXT_PRIMARY);
                    // Afficher un effet de survol sans sélectionner réellement la ligne
                    setRowHoverEffect(row);
                }
            }
        });
        
        avionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Ne pas effacer la sélection, juste enlever l'effet de survol
                clearRowHoverEffect();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editAvion();
                } else if (e.getClickCount() == 1) {
                    int row = avionTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // Sélectionner la ligne lors du clic
                        avionTable.setRowSelectionInterval(row, row);
                        lastSelectedRow = row;
                    }
                }
            }
        });
        
        // ScrollPane avec personnalisation
        JScrollPane scrollPane = new JScrollPane(avionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setRowHoverEffect(int row) {
        // Cette méthode met en surbrillance une ligne sans la sélectionner
        // On utilise un renderer personnalisé pour l'effet visuel seulement
        for (int i = 0; i < avionTable.getRowCount(); i++) {
            if (i == row) {
                avionTable.setSelectionBackground(TABLE_ROW_HOVER);
                avionTable.setSelectionForeground(TEXT_PRIMARY);
            }
        }
    }
    
    private void clearRowHoverEffect() {
        // Effacer l'effet de survol sans affecter la sélection réelle
        if (lastSelectedRow >= 0 && lastSelectedRow < avionTable.getRowCount()) {
            avionTable.setRowSelectionInterval(lastSelectedRow, lastSelectedRow);
        }
    }
    
    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        String[] buttonLabels = {"Ajouter", "Modifier", "Supprimer", "Actualiser", "Statistiques"};
        Runnable[] buttonActions = {
            this::addAvion,
            this::editAvion,
            this::deleteAvion,
            this::loadAvions,
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
    
    private void loadAvions() {
        SwingWorker<List<Avion>, Void> worker = new SwingWorker<List<Avion>, Void>() {
            @Override
            protected List<Avion> doInBackground() throws Exception {
                return avionDAO.getAllAvions();
            }
            
            @Override
            protected void done() {
                try {
                    List<Avion> allAvions = get();
                    tableModel.setRowCount(0);
                    
                    for (Avion a : allAvions) {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getModele(),
                            a.getImmatriculation(),
                            a.getCapacitePassagers(),
                            a.getDateMiseEnService() != null ? a.getDateMiseEnService().toString() : "",
                            a.getStatut()
                        });
                    }
                    
                    updateStats();
                    
                    // Réinitialiser la dernière ligne sélectionnée
                    lastSelectedRow = -1;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AvionManagementFrame.this,
                        "Erreur lors du chargement des avions",
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
            int disponibles = 0;
            int entretien = 0;
            int horsService = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<Avion> allAvions = avionDAO.getAllAvions();
                total = allAvions.size();
                
                for (Avion a : allAvions) {
                    String statut = a.getStatut().toLowerCase();
                    switch (statut) {
                        case "disponible":
                            disponibles++;
                            break;
                        case "entretien":
                            entretien++;
                            break;
                        case "hors_service":
                            horsService++;
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
                        } else if (text.startsWith("Disponibles :")) {
                            label.setText("Disponibles : " + disponibles);
                        } else if (text.startsWith("Entretien :")) {
                            label.setText("Entretien : " + entretien);
                        } else if (text.startsWith("Hors Service :")) {
                            label.setText("Hors Service : " + horsService);
                        }
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void searchAvions() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAvions();
            return;
        }
        
        SwingWorker<List<Avion>, Void> worker = new SwingWorker<List<Avion>, Void>() {
            @Override
            protected List<Avion> doInBackground() throws Exception {
                return avionDAO.searchAvions(keyword);
            }
            
            @Override
            protected void done() {
                try {
                    List<Avion> searchResults = get();
                    tableModel.setRowCount(0);
                    
                    for (Avion a : searchResults) {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getModele(),
                            a.getImmatriculation(),
                            a.getCapacitePassagers(),
                            a.getDateMiseEnService() != null ? a.getDateMiseEnService().toString() : "",
                            a.getStatut()
                        });
                    }
                    
                    // Réinitialiser la sélection après recherche
                    lastSelectedRow = -1;
                    avionTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void filterAvions() {
        String selectedStatut = (String) cmbStatutFilter.getSelectedItem();
        if ("Tous".equals(selectedStatut)) {
            loadAvions();
            return;
        }
        
        SwingWorker<List<Avion>, Void> worker = new SwingWorker<List<Avion>, Void>() {
            @Override
            protected List<Avion> doInBackground() throws Exception {
                return avionDAO.getAvionsByStatut(selectedStatut);
            }
            
            @Override
            protected void done() {
                try {
                    List<Avion> filteredAvions = get();
                    tableModel.setRowCount(0);
                    
                    for (Avion a : filteredAvions) {
                        tableModel.addRow(new Object[]{
                            a.getId(),
                            a.getModele(),
                            a.getImmatriculation(),
                            a.getCapacitePassagers(),
                            a.getDateMiseEnService() != null ? a.getDateMiseEnService().toString() : "",
                            a.getStatut()
                        });
                    }
                    
                    // Réinitialiser la sélection après filtrage
                    lastSelectedRow = -1;
                    avionTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void addAvion() {
        // Créer une boîte de dialogue simple pour ajouter un avion
        JDialog dialog = new JDialog(this, "Ajouter un nouvel avion", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(CARD_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtModele = createFormTextField();
        JTextField txtImmatriculation = createFormTextField();
        JTextField txtCapacite = createFormTextField();
        JTextField txtDateMiseEnService = createFormTextField();
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"disponible", "entretien", "hors_service"});
        
        // Style pour le combobox
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.setBackground(Color.WHITE);
        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Ajouter les champs au formulaire
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Modèle *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtModele, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Immatriculation *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtImmatriculation, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Capacité *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCapacite, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Date Mise en Service (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDateMiseEnService, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFormLabel("Statut"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatut, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton btnSave = new ModernButton("Enregistrer");
        btnSave.addActionListener(e -> {
            if (saveAvion(null, txtModele, txtImmatriculation, txtCapacite, txtDateMiseEnService, cmbStatut)) {
                dialog.dispose();
                loadAvions();
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
    
    private void editAvion() {
        int selectedRow = avionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un avion à modifier",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int avionId = (int) tableModel.getValueAt(selectedRow, 0);
        String modele = (String) tableModel.getValueAt(selectedRow, 1);
        String immatriculation = (String) tableModel.getValueAt(selectedRow, 2);
        String capacite = tableModel.getValueAt(selectedRow, 3).toString();
        String dateMiseEnService = (String) tableModel.getValueAt(selectedRow, 4);
        String statut = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Créer une boîte de dialogue pour modifier l'avion
        JDialog dialog = new JDialog(this, "Modifier l'avion", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(CARD_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtModele = createFormTextField();
        txtModele.setText(modele);
        
        JTextField txtImmatriculation = createFormTextField();
        txtImmatriculation.setText(immatriculation);
        
        JTextField txtCapacite = createFormTextField();
        txtCapacite.setText(capacite);
        
        JTextField txtDateMiseEnService = createFormTextField();
        txtDateMiseEnService.setText(dateMiseEnService);
        
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"disponible", "entretien", "hors_service"});
        cmbStatut.setSelectedItem(statut);
        
        // Style pour le combobox
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.setBackground(Color.WHITE);
        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Ajouter les champs au formulaire
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Modèle *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtModele, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Immatriculation *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtImmatriculation, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Capacité *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCapacite, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Date Mise en Service (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDateMiseEnService, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFormLabel("Statut"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatut, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton btnSave = new ModernButton("Enregistrer");
        btnSave.addActionListener(e -> {
            Avion avion = new Avion();
            avion.setId(avionId);
            if (saveAvion(avion, txtModele, txtImmatriculation, txtCapacite, txtDateMiseEnService, cmbStatut)) {
                dialog.dispose();
                loadAvions();
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
    
    private boolean saveAvion(Avion avion, JTextField txtModele, JTextField txtImmatriculation, 
                              JTextField txtCapacite, JTextField txtDateMiseEnService, 
                              JComboBox<String> cmbStatut) {
        
        // Validation des champs obligatoires
        if (txtModele.getText().trim().isEmpty() ||
            txtImmatriculation.getText().trim().isEmpty() ||
            txtCapacite.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires (*)",
                "Champs manquants",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Avion a = (avion == null) ? new Avion() : avion;
            
            a.setModele(txtModele.getText().trim());
            a.setImmatriculation(txtImmatriculation.getText().trim());
            a.setCapacitePassagers(Integer.parseInt(txtCapacite.getText().trim()));
            a.setStatut((String) cmbStatut.getSelectedItem());
            
            // Date de mise en service
            if (!txtDateMiseEnService.getText().trim().isEmpty()) {
                a.setDateMiseEnService(Date.valueOf(txtDateMiseEnService.getText().trim()));
            }
            
            // Sauvegarde
            boolean success;
            if (avion == null) {
                success = avionDAO.addAvion(a);
            } else {
                success = avionDAO.updateAvion(a);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Avion enregistré avec succès",
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
                "Format de date invalide. Utilisez AAAA-MM-JJ",
                "Erreur de format",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void deleteAvion() {
        int selectedRow = avionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un avion à supprimer",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int avionId = (int) tableModel.getValueAt(selectedRow, 0);
        String modele = (String) tableModel.getValueAt(selectedRow, 1);
        String immatriculation = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer l'avion " + modele + " (" + immatriculation + ") ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return avionDAO.deleteAvion(avionId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AvionManagementFrame.this,
                                "Avion supprimé avec succès",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                            loadAvions();
                        } else {
                            JOptionPane.showMessageDialog(AvionManagementFrame.this,
                                "Erreur lors de la suppression de l'avion",
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
    
    private void showStatistics() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            int total = 0;
            int disponibles = 0;
            int entretien = 0;
            int horsService = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<Avion> allAvions = avionDAO.getAllAvions();
                total = allAvions.size();
                
                for (Avion a : allAvions) {
                    String statut = a.getStatut().toLowerCase();
                    switch (statut) {
                        case "disponible":
                            disponibles++;
                            break;
                        case "entretien":
                            entretien++;
                            break;
                        case "hors_service":
                            horsService++;
                            break;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    double pourcentageDisponibles = total > 0 ? (disponibles * 100.0) / total : 0;
                    double pourcentageEntretien = total > 0 ? (entretien * 100.0) / total : 0;
                    double pourcentageHorsService = total > 0 ? (horsService * 100.0) / total : 0;
                    
                    String message = String.format(
                        "<html><body style='width: 300px;'>" +
                        "<h3>📊 Statistiques des Avions</h3>" +
                        "<p><b>Total :</b> %d avions</p>" +
                        "<p><b>Disponibles :</b> %d (%.1f%%)</p>" +
                        "<p><b>En entretien :</b> %d (%.1f%%)</p>" +
                        "<p><b>Hors service :</b> %d (%.1f%%)</p>" +
                        "<br>" +
                        "<p><b>Taux de disponibilité :</b> %.1f%%</p>" +
                        "</body></html>",
                        total,
                        disponibles, pourcentageDisponibles,
                        entretien, pourcentageEntretien,
                        horsService, pourcentageHorsService,
                        pourcentageDisponibles
                    );
                    
                    JOptionPane.showMessageDialog(AvionManagementFrame.this,
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
                    case "disponible":
                        setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "entretien":
                        setForeground(WARNING_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "hors_service":
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