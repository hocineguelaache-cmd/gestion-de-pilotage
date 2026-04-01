package views;

import dao.PiloteDAO;
import dao.AvionDAO;
import dao.VolDAO;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardFrame extends JFrame {

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

    private final User currentUser;
    private Timer clockTimer;

    public DashboardFrame(User user) {
        this.currentUser = user;
        initComponents();
        setupUI();
        startClock();
    }

    /* ===================== INITIALISATION ===================== */
    
    private void initComponents() {
        setTitle("Système de Gestion Aéronautique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Panel principal avec layout BorderLayout
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        /* ========== BARRE DE NAVIGATION SUPÉRIEURE ========== */
        JPanel topBar = createTopBar();
        
        /* ========== CONTENU PRINCIPAL ========== */
        JScrollPane mainScrollPane = createMainContent();
        
        /* ========== ASSEMBLAGE FINAL ========== */
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(1200, 700));
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_BACKGROUND);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Logo/Titre à gauche
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JLabel lblLogo = new JLabel("SGA");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblLogo.setForeground(PRIMARY_COLOR);
        
        JLabel lblTitle = new JLabel(" | Système de Gestion Aéronautique");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(TEXT_PRIMARY);
        
        leftPanel.add(lblLogo);
        leftPanel.add(lblTitle);

        // Informations utilisateur à droite
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        
        // Horloge
        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblClock.setForeground(TEXT_SECONDARY);
        
        // Info utilisateur
        JLabel lblUserInfo = new JLabel("Connecté : " + currentUser.getNomCompagnie());
        lblUserInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUserInfo.setForeground(TEXT_PRIMARY);
        
        // Bouton déconnexion
        JButton btnLogout = new ModernButton("Déconnexion");
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(lblClock);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(lblUserInfo);
        rightPanel.add(Box.createHorizontalStrut(20));
        rightPanel.add(btnLogout);
        
        // Timer pour l'horloge
        clockTimer = new Timer(1000, e -> {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            lblClock.setText(date + " | " + time);
        });
        clockTimer.start();

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }

    private JScrollPane createMainContent() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(50, 25, 50, 25));
        
        // En-tête du dashboard
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel lblWelcome = new JLabel("Bienvenue, " + currentUser.getNomCompagnie());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblWelcome.setForeground(TEXT_PRIMARY);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy"));
        JLabel lblSubtitle = new JLabel(today);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(lblWelcome, BorderLayout.NORTH);
        headerPanel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        
        // Section des fonctionnalités principales
        JPanel featuresPanel = createFeaturesPanel();
        
        mainContent.add(Box.createVerticalGlue());
        mainContent.add(headerPanel);
        mainContent.add(Box.createVerticalStrut(50));
        mainContent.add(featuresPanel);
        mainContent.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        return scrollPane;
    }
    
    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Fonctionnalités Principales");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JPanel featuresGrid = new JPanel(new GridLayout(2, 2, 30, 30));
        featuresGrid.setOpaque(false);
        featuresGrid.setMaximumSize(new Dimension(1000, 400));
        
        // Les 4 fonctionnalités principales
        String[] features = {
            "Gestion des Pilotes",
            "Gestion des Avions", 
            "Gestion des Vols",
            "Recherche Rapide"
        };
        
        String[] descriptions = {
            "Gérez les informations des pilotes, leurs qualifications et leurs horaires",
            "Suivez la flotte aérienne, les maintenances et les statuts des avions",
            "Planifiez et gérez les vols, les horaires et les équipages",
            "Recherchez rapidement des informations spécifiques dans le système"
        };
        
        Color[] featureColors = {
            PRIMARY_COLOR,
            SUCCESS_COLOR,
            WARNING_COLOR,
            SECONDARY_COLOR
        };
        
        Runnable[] featureActions = {
            this::openPiloteManagement,
            this::openAvionManagement,
            this::openVolManagement,
            this::openRechercheRapide
        };
        
        for (int i = 0; i < features.length; i++) {
            JPanel featureCard = createFeatureCard(features[i], descriptions[i], featureColors[i], featureActions[i]);
            featuresGrid.add(featureCard);
        }
        
        JPanel gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridContainer.setOpaque(false);
        gridContainer.add(featuresGrid);
        
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(gridContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFeatureCard(String title, String description, Color color, Runnable action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(350, 200));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(color);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDescription = new JLabel("<html><div style='text-align:center;'>" + description + "</div></html>");
        lblDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDescription.setForeground(TEXT_SECONDARY);
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Utiliser ModernButton pour un style cohérent
        JButton btnAction = new ModernButton("Accéder");
        btnAction.addActionListener(e -> action.run());
        btnAction.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(lblTitle);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(lblDescription);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(btnAction);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Garder les effets de survol sur la carte
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 247, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BACKGROUND);
            }
        });
        
        return card;
    }

    /* ===================== MÉTHODES D'OUVERTURE DES FENÊTRES ===================== */
    
    private void openPiloteManagement() {
        SwingUtilities.invokeLater(() -> {
            PiloteManagementFrame frame = new PiloteManagementFrame();
            frame.setVisible(true);
        });
    }
    
    private void openAvionManagement() {
        SwingUtilities.invokeLater(() -> {
            AvionManagementFrame frame = new AvionManagementFrame(currentUser);
            frame.setVisible(true);
        });
    }
    
    private void openVolManagement() {
        SwingUtilities.invokeLater(() -> {
            VolManagementFrame frame = new VolManagementFrame(currentUser);
            frame.setVisible(true);
        });
    }
    
    private void openRechercheRapide() {
        SwingUtilities.invokeLater(() -> {
            RechercheFrame frame = new RechercheFrame();
            frame.setVisible(true);
        });
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
                BorderFactory.createLineBorder(new Color(13, 71, 161), 1),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
            ));
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(25, 95, 205));
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(20, 85, 185), 1),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                    ));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(PRIMARY_COLOR);
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(13, 71, 161), 1),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                    ));
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    setBackground(new Color(10, 65, 150));
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    setBackground(new Color(25, 95, 205));
                }
            });
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
    
    private void startClock() {
        clockTimer.start();
    }
    
    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir vous déconnecter ?",
            "Confirmation de déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            clockTimer.stop();
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
    
    // Méthode pour rafraîchir les données
    public void refreshDashboard() {
        // Cette méthode est maintenant vide car on n'affiche plus de statistiques
    }
}