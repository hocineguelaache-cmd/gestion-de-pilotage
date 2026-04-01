package views;

import dao.PiloteDAO;
import dao.AvionDAO;
import dao.VolDAO;
import models.Pilote;
import models.Avion;
import models.Vol;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RechercheFrame extends JFrame {

    /* ---------- PALETTE DE COULEURS COHÉRENTE ---------- */
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(15, 82, 186);
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color TABLE_HEADER_BG = new Color(248, 249, 250);

    private PiloteDAO piloteDAO;
    private AvionDAO avionDAO;
    private VolDAO volDAO;
    
    private JTabbedPane tabbedPane;
    private JTable piloteTable, avionTable, volTable;
    private DefaultTableModel piloteTableModel, avionTableModel, volTableModel;
    
    // Champs de recherche pour pilotes
    private JTextField txtPiloteId, txtPiloteNom, txtPilotePrenom;
    private JButton btnSearchPilote;
    
    // Champs de recherche pour avions
    private JTextField txtAvionId, txtAvionModele, txtAvionImmatriculation;
    private JButton btnSearchAvion;
    
    // Champs de recherche pour vols
    private JTextField txtVolId, txtVolNumero, txtVolVilleDepart, txtVolVilleArrivee;
    private JTextField txtVolDateDepart, txtVolDateArrivee;
    private JButton btnSearchVol;

    public RechercheFrame() {
        this.piloteDAO = new PiloteDAO();
        this.avionDAO = new AvionDAO();
        this.volDAO = new VolDAO();
        
        initComponents();
        setupUI();
        loadInitialData();
        
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Recherche Avancée");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 800);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        /* ========== BARRE SUPÉRIEURE ========== */
        JPanel topBar = createTopBar();
        
        /* ========== TABBED PANE ========== */
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Onglet Pilotes
        tabbedPane.addTab("Pilotes", createPiloteTab());
        
        // Onglet Avions
        tabbedPane.addTab("Avions", createAvionTab());
        
        // Onglet Vols
        tabbedPane.addTab("Vols", createVolTab());
        
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        
        // Raccourci clavier
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
        
        JLabel lblTitle = new JLabel("Recherche Avancée");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        
        JLabel lblSubtitle = new JLabel("Recherchez des informations spécifiques avec des critères précis");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(lblTitle);
        leftPanel.add(lblSubtitle);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        
        return topBar;
    }
    
    private JPanel createPiloteTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        /* ========== PANEL DE RECHERCHE PILOTES ========== */
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(CARD_BACKGROUND);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Critères de recherche pour les pilotes"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID Pilote
        JLabel lblId = new JLabel("ID :");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblId, gbc);
        
        txtPiloteId = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtPiloteId, gbc);
        
        // Nom Pilote
        JLabel lblNom = new JLabel("Nom :");
        lblNom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblNom, gbc);
        
        txtPiloteNom = createFormTextField();
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtPiloteNom, gbc);
        
        // Prénom Pilote
        JLabel lblPrenom = new JLabel("Prénom :");
        lblPrenom.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        searchPanel.add(lblPrenom, gbc);
        
        txtPilotePrenom = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        searchPanel.add(txtPilotePrenom, gbc);
        
        // Bouton Rechercher
        btnSearchPilote = new ModernButton("Rechercher les pilotes");
        btnSearchPilote.addActionListener(e -> searchPilotes());
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(btnSearchPilote, gbc);
        
        /* ========== TABLEAU DES PILOTES ========== */
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String[] columns = {"ID", "Nom", "Prénom", "Matricule", "Date Naissance", "Heures de Vol", "Statut"};
        piloteTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        piloteTable = new JTable(piloteTableModel);
        customizeTable(piloteTable);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = piloteTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(120);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(100);
        columnModel.getColumn(6).setPreferredWidth(100);
        
        // Renderer pour les statuts
        piloteTable.getColumnModel().getColumn(6).setCellRenderer(new StatusPiloteRenderer());
        
        JScrollPane scrollPane = new JScrollPane(piloteTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        /* ========== PANEL DE FILTRES ========== */
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblFilter = new JLabel("Filtrer par statut :");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"Tous", "actif", "inactif", "en_conge"});
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.addActionListener(e -> {
            String statut = (String) cmbStatut.getSelectedItem();
            if ("Tous".equals(statut)) {
                loadAllPilotes();
            } else {
                filterPilotesByStatut(statut);
            }
        });
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtPiloteId.setText("");
            txtPiloteNom.setText("");
            txtPilotePrenom.setText("");
            cmbStatut.setSelectedIndex(0);
            loadAllPilotes();
        });
        
        filterPanel.add(lblFilter);
        filterPanel.add(cmbStatut);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnReset);
        
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAvionTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        /* ========== PANEL DE RECHERCHE AVIONS ========== */
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(CARD_BACKGROUND);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Critères de recherche pour les avions"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID Avion
        JLabel lblId = new JLabel("ID :");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblId, gbc);
        
        txtAvionId = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtAvionId, gbc);
        
        // Modèle Avion
        JLabel lblModele = new JLabel("Modèle :");
        lblModele.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblModele, gbc);
        
        txtAvionModele = createFormTextField();
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtAvionModele, gbc);
        
        // Immatriculation
        JLabel lblImmatriculation = new JLabel("Immatriculation :");
        lblImmatriculation.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        searchPanel.add(lblImmatriculation, gbc);
        
        txtAvionImmatriculation = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        searchPanel.add(txtAvionImmatriculation, gbc);
        
        // Bouton Rechercher
        btnSearchAvion = new ModernButton("Rechercher les avions");
        btnSearchAvion.addActionListener(e -> searchAvions());
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(btnSearchAvion, gbc);
        
        /* ========== TABLEAU DES AVIONS ========== */
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String[] columns = {"ID", "Modèle", "Immatriculation", "Capacité", "Date Mise en Service", "Statut"};
        avionTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        avionTable = new JTable(avionTableModel);
        customizeTable(avionTable);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = avionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(120);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(100);
        
        // Renderer pour les statuts
        avionTable.getColumnModel().getColumn(5).setCellRenderer(new StatusAvionRenderer());
        
        JScrollPane scrollPane = new JScrollPane(avionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        /* ========== PANEL DE FILTRES ========== */
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblFilter = new JLabel("Filtrer par statut :");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"Tous", "disponible", "entretien", "hors_service"});
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.addActionListener(e -> {
            String statut = (String) cmbStatut.getSelectedItem();
            if ("Tous".equals(statut)) {
                loadAllAvions();
            } else {
                filterAvionsByStatut(statut);
            }
        });
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtAvionId.setText("");
            txtAvionModele.setText("");
            txtAvionImmatriculation.setText("");
            cmbStatut.setSelectedIndex(0);
            loadAllAvions();
        });
        
        filterPanel.add(lblFilter);
        filterPanel.add(cmbStatut);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnReset);
        
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createVolTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        /* ========== PANEL DE RECHERCHE VOLS ========== */
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(CARD_BACKGROUND);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Critères de recherche pour les vols"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID Vol
        JLabel lblId = new JLabel("ID :");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblId, gbc);
        
        txtVolId = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtVolId, gbc);
        
        // Numéro Vol
        JLabel lblNumero = new JLabel("N° Vol :");
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.1;
        searchPanel.add(lblNumero, gbc);
        
        txtVolNumero = createFormTextField();
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0.4;
        searchPanel.add(txtVolNumero, gbc);
        
        // Ville Départ
        JLabel lblVilleDepart = new JLabel("Ville Départ :");
        lblVilleDepart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        searchPanel.add(lblVilleDepart, gbc);
        
        txtVolVilleDepart = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.4;
        searchPanel.add(txtVolVilleDepart, gbc);
        
        // Ville Arrivée
        JLabel lblVilleArrivee = new JLabel("Ville Arrivée :");
        lblVilleArrivee.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.1;
        searchPanel.add(lblVilleArrivee, gbc);
        
        txtVolVilleArrivee = createFormTextField();
        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 0.4;
        searchPanel.add(txtVolVilleArrivee, gbc);
        
        // Date Départ
        JLabel lblDateDepart = new JLabel("Date Départ (JJ/MM/AAAA) :");
        lblDateDepart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        searchPanel.add(lblDateDepart, gbc);
        
        txtVolDateDepart = createFormTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.4;
        searchPanel.add(txtVolDateDepart, gbc);
        
        // Date Arrivée
        JLabel lblDateArrivee = new JLabel("Date Arrivée (JJ/MM/AAAA) :");
        lblDateArrivee.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.1;
        searchPanel.add(lblDateArrivee, gbc);
        
        txtVolDateArrivee = createFormTextField();
        gbc.gridx = 3; gbc.gridy = 2; gbc.weightx = 0.4;
        searchPanel.add(txtVolDateArrivee, gbc);
        
        // Bouton Rechercher
        btnSearchVol = new ModernButton("Rechercher les vols");
        btnSearchVol.addActionListener(e -> searchVols());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(btnSearchVol, gbc);
        
        /* ========== TABLEAU DES VOLS ========== */
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        String[] columns = {"ID", "N° Vol", "Départ", "Arrivée", "Date Départ", "Date Arrivée", "Statut"};
        volTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        volTable = new JTable(volTableModel);
        customizeTable(volTable);
        
        // Personnalisation des colonnes
        TableColumnModel columnModel = volTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(120);
        columnModel.getColumn(5).setPreferredWidth(120);
        columnModel.getColumn(6).setPreferredWidth(100);
        
        // Renderer pour les statuts
        volTable.getColumnModel().getColumn(6).setCellRenderer(new StatusVolRenderer());
        
        JScrollPane scrollPane = new JScrollPane(volTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        /* ========== PANEL DE FILTRES ========== */
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblFilter = new JLabel("Filtrer par statut :");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFilter.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> cmbStatut = new JComboBox<>(new String[]{"Tous", "programme", "en_cours", "termine", "annule"});
        cmbStatut.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStatut.addActionListener(e -> {
            String statut = (String) cmbStatut.getSelectedItem();
            if ("Tous".equals(statut)) {
                loadAllVols();
            } else {
                filterVolsByStatut(statut);
            }
        });
        
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnReset.setForeground(SECONDARY_COLOR);
        btnReset.setBorderPainted(false);
        btnReset.setContentAreaFilled(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            txtVolId.setText("");
            txtVolNumero.setText("");
            txtVolVilleDepart.setText("");
            txtVolVilleArrivee.setText("");
            txtVolDateDepart.setText("");
            txtVolDateArrivee.setText("");
            cmbStatut.setSelectedIndex(0);
            loadAllVols();
        });
        
        filterPanel.add(lblFilter);
        filterPanel.add(cmbStatut);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnReset);
        
        tablePanel.add(filterPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createFormTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private void customizeTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Personnalisation de l'en-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setReorderingAllowed(false);
        
        // Centreur de cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    /* ===================== LOGIQUE MÉTIER ===================== */
    
    private void loadInitialData() {
        loadAllPilotes();
        loadAllAvions();
        loadAllVols();
    }
    
    private void loadAllPilotes() {
        try {
            List<Pilote> pilotes = piloteDAO.getAllPilotes();
            updatePiloteTable(pilotes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des pilotes: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllAvions() {
        try {
            List<Avion> avions = avionDAO.getAllAvions();
            updateAvionTable(avions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des avions: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllVols() {
        try {
            List<Vol> vols = volDAO.getAllVols();
            updateVolTable(vols);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des vols: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePiloteTable(List<Pilote> pilotes) {
        piloteTableModel.setRowCount(0);
        for (Pilote p : pilotes) {
            piloteTableModel.addRow(new Object[]{
                p.getId(),
                p.getNom(),
                p.getPrenom(),
                p.getMatricule(),
                p.getDateNaissance() != null ? p.getDateNaissance().toString() : "",
                p.getHeuresVolTotal(),
                p.getStatut()
            });
        }
    }
    
    private void updateAvionTable(List<Avion> avions) {
        avionTableModel.setRowCount(0);
        for (Avion a : avions) {
            avionTableModel.addRow(new Object[]{
                a.getId(),
                a.getModele(),
                a.getImmatriculation(),
                a.getCapacitePassagers(),
                a.getDateMiseEnService() != null ? a.getDateMiseEnService().toString() : "",
                a.getStatut()
            });
        }
    }
    
    private void updateVolTable(List<Vol> vols) {
        volTableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Vol v : vols) {
            String dateDepart = v.getDateDepart() != null ? sdf.format(v.getDateDepart()) : "";
            String dateArrivee = v.getDateArrivee() != null ? sdf.format(v.getDateArrivee()) : "";
            
            volTableModel.addRow(new Object[]{
                v.getId(),
                v.getNumeroVol(),
                v.getAeroportDepart(),
                v.getAeroportArrivee(),
                dateDepart,
                dateArrivee,
                v.getStatut()
            });
        }
    }
    
    private void searchPilotes() {
        try {
            String idStr = txtPiloteId.getText().trim();
            String nom = txtPiloteNom.getText().trim();
            String prenom = txtPilotePrenom.getText().trim();
            
            List<Pilote> results = piloteDAO.getAllPilotes();
            
            // Filtrer par ID si spécifié
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    results.removeIf(p -> p.getId() != id);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide. Veuillez entrer un nombre.",
                        "Erreur de format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Filtrer par nom si spécifié
            if (!nom.isEmpty()) {
                results.removeIf(p -> !p.getNom().toLowerCase().contains(nom.toLowerCase()));
            }
            
            // Filtrer par prénom si spécifié
            if (!prenom.isEmpty()) {
                results.removeIf(p -> !p.getPrenom().toLowerCase().contains(prenom.toLowerCase()));
            }
            
            updatePiloteTable(results);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun pilote trouvé avec les critères spécifiés.",
                    "Aucun résultat",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche des pilotes: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchAvions() {
        try {
            String idStr = txtAvionId.getText().trim();
            String modele = txtAvionModele.getText().trim();
            String immatriculation = txtAvionImmatriculation.getText().trim();
            
            List<Avion> results = avionDAO.getAllAvions();
            
            // Filtrer par ID si spécifié
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    results.removeIf(a -> a.getId() != id);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide. Veuillez entrer un nombre.",
                        "Erreur de format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Filtrer par modèle si spécifié
            if (!modele.isEmpty()) {
                results.removeIf(a -> !a.getModele().toLowerCase().contains(modele.toLowerCase()));
            }
            
            // Filtrer par immatriculation si spécifié
            if (!immatriculation.isEmpty()) {
                results.removeIf(a -> !a.getImmatriculation().toLowerCase().contains(immatriculation.toLowerCase()));
            }
            
            updateAvionTable(results);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun avion trouvé avec les critères spécifiés.",
                    "Aucun résultat",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche des avions: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchVols() {
        try {
            String idStr = txtVolId.getText().trim();
            String numero = txtVolNumero.getText().trim();
            String villeDepart = txtVolVilleDepart.getText().trim();
            String villeArrivee = txtVolVilleArrivee.getText().trim();
            String dateDepartStr = txtVolDateDepart.getText().trim();
            String dateArriveeStr = txtVolDateArrivee.getText().trim();
            
            List<Vol> results = volDAO.getAllVols();
            
            // Filtrer par ID si spécifié
            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    results.removeIf(v -> v.getId() != id);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide. Veuillez entrer un nombre.",
                        "Erreur de format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Filtrer par numéro de vol si spécifié
            if (!numero.isEmpty()) {
                results.removeIf(v -> !v.getNumeroVol().toLowerCase().contains(numero.toLowerCase()));
            }
            
            // Filtrer par ville de départ si spécifié
            if (!villeDepart.isEmpty()) {
                results.removeIf(v -> !v.getAeroportDepart().toLowerCase().contains(villeDepart.toLowerCase()));
            }
            
            // Filtrer par ville d'arrivée si spécifié
            if (!villeArrivee.isEmpty()) {
                results.removeIf(v -> !v.getAeroportArrivee().toLowerCase().contains(villeArrivee.toLowerCase()));
            }
            
            // Filtrer par date de départ si spécifié
            if (!dateDepartStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date searchDate = sdf.parse(dateDepartStr);
                    java.sql.Date sqlDate = new java.sql.Date(searchDate.getTime());
                    
                    results.removeIf(v -> v.getDateDepart() == null || 
                        !v.getDateDepart().toString().equals(sqlDate.toString()));
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this,
                        "Format de date invalide. Utilisez JJ/MM/AAAA.",
                        "Erreur de format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Filtrer par date d'arrivée si spécifié
            if (!dateArriveeStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date searchDate = sdf.parse(dateArriveeStr);
                    java.sql.Date sqlDate = new java.sql.Date(searchDate.getTime());
                    
                    results.removeIf(v -> v.getDateArrivee() == null || 
                        !v.getDateArrivee().toString().equals(sqlDate.toString()));
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this,
                        "Format de date invalide. Utilisez JJ/MM/AAAA.",
                        "Erreur de format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            updateVolTable(results);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Aucun vol trouvé avec les critères spécifiés.",
                    "Aucun résultat",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche des vols: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterPilotesByStatut(String statut) {
        try {
            List<Pilote> pilotes = piloteDAO.getAllPilotes();
            pilotes.removeIf(p -> !p.getStatut().equals(statut));
            updatePiloteTable(pilotes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du filtrage des pilotes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterAvionsByStatut(String statut) {
        try {
            List<Avion> avions = avionDAO.getAvionsByStatut(statut);
            updateAvionTable(avions);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du filtrage des avions",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterVolsByStatut(String statut) {
        try {
            List<Vol> vols = volDAO.getVolsByStatut(statut);
            updateVolTable(vols);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du filtrage des vols",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /* ===================== CLASSES PERSONNALISÉES ===================== */
    
    private static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBackground(PRIMARY_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
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
            });
        }
    }
    
    private static class StatusPiloteRenderer extends DefaultTableCellRenderer {
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
    
    private static class StatusAvionRenderer extends DefaultTableCellRenderer {
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
    
    private static class StatusVolRenderer extends DefaultTableCellRenderer {
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
                        setForeground(SECONDARY_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "en_cours":
                        setForeground(SUCCESS_COLOR);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                    case "termine":
                        setForeground(new Color(128, 128, 128));
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
            e.printStackTrace();
        }
    }
}