package views;

import dao.PiloteDAO;
import models.Pilote;
import models.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PiloteManagementFrame extends JFrame {

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
    private JTable piloteTable;
    private PiloteDAO PiloteDAO;
    private JTextField txtSearch;
    private User currentUser;
    private JPanel statsPanel;
    private int lastSelectedRow = -1; // Pour garder la trace de la sélection

    public PiloteManagementFrame() {
        
        this.PiloteDAO = new PiloteDAO();
        
        initComponents();
        setupUI();
        loadPilotes();
        updateStats();
        
    }

    /* ===================== INITIALISATION ===================== */
    
    private void initComponents() {
        setTitle("Gestion des Pilotes" );
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
        
        JLabel lblTitle = new JLabel("Gestion des Pilotes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        
       
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(lblTitle);
     
        
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
        JLabel lblActifs = createStatLabel("Actifs : 0", SUCCESS_COLOR);
        JLabel lblInactifs = createStatLabel("Inactifs : 0", WARNING_COLOR);
        JLabel lblConge = createStatLabel("Congé : 0", DANGER_COLOR);
        
        panel.add(lblTotal);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblActifs);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblInactifs);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(lblConge);
        
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
        
        JLabel lblSearch = new JLabel("Recherche :");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(TEXT_PRIMARY);
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        txtSearch.addActionListener(e -> searchPilotes());
        
        JButton btnSearch = new ModernButton("Rechercher");
        btnSearch.addActionListener(e -> searchPilotes());
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadPilotes();
        });
        
        JPanel searchFieldPanel = new JPanel(new BorderLayout(10, 0));
        searchFieldPanel.setOpaque(false);
        searchFieldPanel.add(txtSearch, BorderLayout.CENTER);
        searchFieldPanel.add(btnSearch, BorderLayout.EAST);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(lblSearch);
        leftPanel.add(searchFieldPanel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
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
            "ID", "Nom", "Prénom", "Matricule", 
            "Date Naissance", "Heures de Vol", "Statut"
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
                    case 5: return Integer.class; // Heures de Vol
                    default: return String.class;
                }
            }
        };
        
        piloteTable = new JTable(tableModel);
        
        // Personnalisation du tableau
        piloteTable.setRowHeight(40);
        piloteTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        piloteTable.setShowGrid(false);
        piloteTable.setIntercellSpacing(new Dimension(0, 0));
        piloteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Personnalisation de l'en-tête
        JTableHeader header = piloteTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setReorderingAllowed(false);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = piloteTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(120); // Nom
        columnModel.getColumn(2).setPreferredWidth(120); // Prénom
        columnModel.getColumn(3).setPreferredWidth(100); // Matricule
        columnModel.getColumn(4).setPreferredWidth(120); // Date Naissance
        columnModel.getColumn(5).setPreferredWidth(100); // Heures de Vol
        columnModel.getColumn(6).setPreferredWidth(100); // Statut
        
        // Centreur de cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < piloteTable.getColumnCount(); i++) {
            piloteTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderer pour les statuts
        piloteTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        // Effet de survol - MODIFICATION ICI
        piloteTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = piloteTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    // Ne pas sélectionner la ligne automatiquement, juste changer la couleur de fond temporairement
                    piloteTable.setSelectionBackground(TABLE_ROW_HOVER);
                    piloteTable.setSelectionForeground(TEXT_PRIMARY);
                    // Afficher un effet de survol sans sélectionner réellement la ligne
                    setRowHoverEffect(row);
                }
            }
        });
        
        piloteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Ne pas effacer la sélection, juste enlever l'effet de survol
                clearRowHoverEffect();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editPilote();
                } else if (e.getClickCount() == 1) {
                    int row = piloteTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        // Sélectionner la ligne lors du clic
                        piloteTable.setRowSelectionInterval(row, row);
                        lastSelectedRow = row;
                    }
                }
            }
        });
        
        // ScrollPane avec personnalisation
        JScrollPane scrollPane = new JScrollPane(piloteTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setRowHoverEffect(int row) {
        // Cette méthode met en surbrillance une ligne sans la sélectionner
        // On utilise un renderer personnalisé pour l'effet visuel seulement
        for (int i = 0; i < piloteTable.getRowCount(); i++) {
            if (i == row) {
                piloteTable.setSelectionBackground(TABLE_ROW_HOVER);
                piloteTable.setSelectionForeground(TEXT_PRIMARY);
            }
        }
    }
    
    private void clearRowHoverEffect() {
        // Effacer l'effet de survol sans affecter la sélection réelle
        if (lastSelectedRow >= 0 && lastSelectedRow < piloteTable.getRowCount()) {
            piloteTable.setRowSelectionInterval(lastSelectedRow, lastSelectedRow);
        }
    }
    
    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        String[] buttonLabels = {"Ajouter", "Modifier", "Supprimer", "Actualiser"};
        Runnable[] buttonActions = {
            this::addPilote,
            this::editPilote,
            this::deletePilote,
            this::loadPilotes
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
    
    private void loadPilotes() {
        SwingWorker<List<Pilote>, Void> worker = new SwingWorker<List<Pilote>, Void>() {
            @Override
            protected List<Pilote> doInBackground() throws Exception {
                return PiloteDAO.getAllPilotes();
            }
            
            @Override
            protected void done() {
                try {
                    List<Pilote> allPilotes = get();
                    tableModel.setRowCount(0);
                    
                    for (Pilote p : allPilotes) {
                        tableModel.addRow(new Object[]{
                            p.getId(),
                            p.getNom(),
                            p.getPrenom(),
                            p.getMatricule(),
                            p.getDateNaissance() != null ? p.getDateNaissance().toString() : "",
                            p.getHeuresVolTotal(),
                            p.getStatut()
                        });
                    }
                    
                    updateStats();
                    
                    // Réinitialiser la dernière ligne sélectionnée
                    lastSelectedRow = -1;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PiloteManagementFrame.this,
                        "Erreur lors du chargement des pilotes",
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
            int actifs = 0;
            int inactifs = 0;
            int conge = 0;
            
            @Override
            protected Void doInBackground() throws Exception {
                List<Pilote> allPilotes = PiloteDAO.getAllPilotes();
                total = allPilotes.size();
                
                for (Pilote p : allPilotes) {
                    String statut = p.getStatut().toLowerCase();
                    switch (statut) {
                        case "actif":
                            actifs++;
                            break;
                        case "inactif":
                            inactifs++;
                            break;
                        case "en_conge":
                            conge++;
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
                        } else if (text.startsWith("Actifs :")) {
                            label.setText("Actifs : " + actifs);
                        } else if (text.startsWith("Inactifs :")) {
                            label.setText("Inactifs : " + inactifs);
                        } else if (text.startsWith("Congé :")) {
                            label.setText("Congé : " + conge);
                        }
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void searchPilotes() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadPilotes();
            return;
        }
        
        SwingWorker<List<Pilote>, Void> worker = new SwingWorker<List<Pilote>, Void>() {
            @Override
            protected List<Pilote> doInBackground() throws Exception {
                return PiloteDAO.searchPilotes(keyword);
            }
            
            @Override
            protected void done() {
                try {
                    List<Pilote> searchResults = get();
                    tableModel.setRowCount(0);
                    
                    for (Pilote p : searchResults) {
                        tableModel.addRow(new Object[]{
                            p.getId(),
                            p.getNom(),
                            p.getPrenom(),
                            p.getMatricule(),
                            p.getDateNaissance() != null ? p.getDateNaissance().toString() : "",
                            p.getHeuresVolTotal(),
                            p.getStatut()
                        });
                    }
                    
                    // Réinitialiser la sélection après recherche
                    lastSelectedRow = -1;
                    piloteTable.clearSelection();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void addPilote() {
        // Créer une boîte de dialogue simple pour ajouter un pilote
        JDialog dialog = new JDialog(this, "Ajouter un nouveau pilote", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(CARD_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtNom = createFormTextField();
        JTextField txtPrenom = createFormTextField();
        JTextField txtMatricule = createFormTextField();
        JTextField txtDateNaissance = createFormTextField();
        JTextField txtHeuresVol = createFormTextField();
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"actif", "inactif", "en_conge"});
        
        // Style pour le combobox
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.setBackground(Color.WHITE);
        cmbStatut.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Ajouter les champs au formulaire
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Nom *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtNom, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Prénom *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPrenom, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Matricule *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMatricule, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Date Naissance (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDateNaissance, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFormLabel("Heures de vol"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtHeuresVol, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createFormLabel("Statut"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatut, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton btnSave = new ModernButton("Enregistrer");
        btnSave.addActionListener(e -> {
            if (savePilote(null, txtNom, txtPrenom, txtMatricule, txtDateNaissance, txtHeuresVol, cmbStatut)) {
                dialog.dispose();
                loadPilotes();
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
    
    private void editPilote() {
        int selectedRow = piloteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un pilote à modifier",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int piloteId = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);
        String matricule = (String) tableModel.getValueAt(selectedRow, 3);
        String dateNaissance = (String) tableModel.getValueAt(selectedRow, 4);
        String heuresVol = tableModel.getValueAt(selectedRow, 5).toString();
        String statut = (String) tableModel.getValueAt(selectedRow, 6);
        
        // Créer une boîte de dialogue pour modifier le pilote
        JDialog dialog = new JDialog(this, "Modifier le pilote", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(CARD_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtNom = createFormTextField();
        txtNom.setText(nom);
        
        JTextField txtPrenom = createFormTextField();
        txtPrenom.setText(prenom);
        
        JTextField txtMatricule = createFormTextField();
        txtMatricule.setText(matricule);
        
        JTextField txtDateNaissance = createFormTextField();
        txtDateNaissance.setText(dateNaissance);
        
        JTextField txtHeuresVol = createFormTextField();
        txtHeuresVol.setText(heuresVol);
        
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"actif", "inactif", "en_conge"});
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
        formPanel.add(createFormLabel("Nom *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(txtNom, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Prénom *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPrenom, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Matricule *"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtMatricule, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Date Naissance (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDateNaissance, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFormLabel("Heures de vol"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtHeuresVol, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createFormLabel("Statut"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbStatut, gbc);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        JButton btnSave = new ModernButton("Enregistrer");
        btnSave.addActionListener(e -> {
            Pilote pilote = new Pilote();
            pilote.setId(piloteId);
            if (savePilote(pilote, txtNom, txtPrenom, txtMatricule, txtDateNaissance, txtHeuresVol, cmbStatut)) {
                dialog.dispose();
                loadPilotes();
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
    
    private boolean savePilote(Pilote pilote, JTextField txtNom, JTextField txtPrenom, 
                              JTextField txtMatricule, JTextField txtDateNaissance, 
                              JTextField txtHeuresVol, JComboBox<String> cmbStatut) {
        
        // Validation des champs obligatoires
        if (txtNom.getText().trim().isEmpty() ||
            txtPrenom.getText().trim().isEmpty() ||
            txtMatricule.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Veuillez remplir tous les champs obligatoires (*)",
                "Champs manquants",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Pilote p = (pilote == null) ? new Pilote() : pilote;
            
            p.setNom(txtNom.getText().trim());
            p.setPrenom(txtPrenom.getText().trim());
            p.setMatricule(txtMatricule.getText().trim());
            p.setStatut((String) cmbStatut.getSelectedItem());
            
            // Date de naissance
            if (!txtDateNaissance.getText().trim().isEmpty()) {
                p.setDateNaissance(java.sql.Date.valueOf(txtDateNaissance.getText().trim()));
            }
            
            // Heures de vol
            if (!txtHeuresVol.getText().trim().isEmpty()) {
                p.setHeuresVolTotal(Integer.parseInt(txtHeuresVol.getText().trim()));
            } else {
                p.setHeuresVolTotal(0);
            }
            
            // Sauvegarde
            boolean success;
            if (pilote == null) {
                success = PiloteDAO.addPilote(p);
            } else {
                success = PiloteDAO.updatePilote(p);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Pilote enregistré avec succès",
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
    
    private void deletePilote() {
        int selectedRow = piloteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un pilote à supprimer",
                "Information",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int piloteId = (int) tableModel.getValueAt(selectedRow, 0);
        String nom = (String) tableModel.getValueAt(selectedRow, 1);
        String prenom = (String) tableModel.getValueAt(selectedRow, 2);
        
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer le pilote " + nom + " " + prenom + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return PiloteDAO.deletePilote(piloteId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(PiloteManagementFrame.this,
                                "Pilote supprimé avec succès",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE);
                            loadPilotes();
                        } else {
                            JOptionPane.showMessageDialog(PiloteManagementFrame.this,
                                "Erreur lors de la suppression du pilote",
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
                    case "actif":
                        setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "inactif":
                        setForeground(WARNING_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "en_conge":
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