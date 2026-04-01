package views;

import dao.UserDAO;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

public class LoginFrame extends JFrame {

    
    
    /* ---------- PALETTE DE COULEURS PROFESSIONNELLE ---------- */
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250); // Gris bleuté clair
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color PRIMARY_COLOR = new Color(15, 82, 186); // Bleu professionnel
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185); // Bleu secondaire
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41); // Gris foncé
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125); // Gris moyen
    private static final Color BORDER_COLOR = new Color(222, 226, 230); // Gris clair pour bordures
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69); // Vert pour effets
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 15); // Ombre subtile

    /* ---------- DÉGRADÉ BOUTON ---------- */
    private static final Color BTN_GRAD_START = new Color(15, 82, 186);
    private static final Color BTN_GRAD_END = new Color(20, 108, 240);
    private static final Color BTN_HOVER_START = new Color(12, 70, 165);
    private static final Color BTN_HOVER_END = new Color(17, 92, 205);

    /* ---------- COMPOSANTS ---------- */
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private RoundedButton btnLogin;
    private JPanel loadingPanel;
    private JProgressBar progressBar;

    public LoginFrame() {
        
        initComponents();
        setupUI();
        applyModernStyles();
    }

    private void initComponents() {
        setTitle("Système de Gestion Aéronautique - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        /* ========== PANEL PRINCIPAL AVEC DÉGRADÉ SUBTIL ========== */
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(1000, 700));

        /* ========== CARTE DE CONNEXION AVEC OMBRE ========== */
        JPanel loginCard = new RoundedPanel(20);
        loginCard.setBackground(CARD_BACKGROUND);
        loginCard.setLayout(new BorderLayout());
        loginCard.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));
        loginCard.setPreferredSize(new Dimension(450, 550));
        
        // Effet d'ombre
        loginCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1),
            BorderFactory.createEmptyBorder(50, 60, 50, 60)
        ));

        /* --- En-tête avec message de salutation --- */
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);
        
        // Message de salutation
        JLabel lblGreeting = new JLabel("Bienvenue");
        lblGreeting.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblGreeting.setForeground(PRIMARY_COLOR);
        lblGreeting.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Titre
        JLabel lblTitle = createLabel("Système de Gestion Aéronautique", 18, Font.BOLD, TEXT_PRIMARY);
        JLabel lblSubtitle = createLabel("Login", 14, Font.PLAIN, TEXT_SECONDARY);
        
        headerPanel.add(lblGreeting, BorderLayout.NORTH);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        /* --- Formulaire avec espacement amélioré --- */
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Champ login
        gbc.gridy = 0;
        JLabel lblLogin = createLabel("Nom d'utilisateur", 12, Font.BOLD, TEXT_PRIMARY);
        lblLogin.setHorizontalAlignment(SwingConstants.LEFT);
        formPanel.add(lblLogin, gbc);

        gbc.gridy = 1;
        txtLogin = createTextField();
        txtLogin.setPreferredSize(new Dimension(300, 45));
        formPanel.add(txtLogin, gbc);

        // Champ mot de passe
        gbc.gridy = 2;
        gbc.insets.top = 20;
        JLabel lblPassword = createLabel("Mot de passe", 12, Font.BOLD, TEXT_PRIMARY);
        lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
        formPanel.add(lblPassword, gbc);

        gbc.gridy = 3;
        txtPassword = createPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 45));
        formPanel.add(txtPassword, gbc);

        // Bouton "Mot de passe oublié"
        gbc.gridy = 4;
        gbc.insets.top = 10;
        JButton btnForgot = new JButton("Mot de passe oublié ?");
        btnForgot.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnForgot.setForeground(SECONDARY_COLOR);
        btnForgot.setBorderPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForgot.setHorizontalAlignment(SwingConstants.RIGHT);
        btnForgot.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Veuillez contacter l'administrateur système\nEmail : kamelhocine@gmail.com",
                "Assistance", JOptionPane.INFORMATION_MESSAGE);
        });
        formPanel.add(btnForgot, gbc);

        /* --- Bouton de connexion --- */
        gbc.gridy = 5;
        gbc.insets.top = 30;
        btnLogin = new RoundedButton("SE CONNECTER", BTN_GRAD_START, BTN_GRAD_END);
        btnLogin.setPreferredSize(new Dimension(300, 50));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(btnLogin, gbc);

        /* --- Panel de chargement (caché initialement) --- */
        loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setOpaque(false);
        loadingPanel.setVisible(false);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 4));
        progressBar.setForeground(SUCCESS_COLOR);
        progressBar.setBackground(new Color(230, 230, 230));
        
        JLabel loadingLabel = createLabel("Authentification en cours...", 11, Font.PLAIN, TEXT_SECONDARY);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        loadingPanel.add(progressBar, BorderLayout.NORTH);
        loadingPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        gbc.gridy = 6;
        gbc.insets.top = 10;
        formPanel.add(loadingPanel, gbc);

        /* --- Séparateur --- */
        gbc.gridy = 7;
        gbc.insets.top = 30;
        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        formPanel.add(separator, gbc);

        /* --- Informations de version --- */
        gbc.gridy = 8;
        gbc.insets.top = 20;
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setOpaque(false);
        
        JLabel lblVersion = createLabel("Version 3.2.0 • Build 2025.01", 10, Font.PLAIN, TEXT_SECONDARY);
        JLabel lblCopyright = createLabel("© 2025 Système de Gestion Aéronautique. Tous droits réservés.", 10, Font.PLAIN, TEXT_SECONDARY);
        
        lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
        lblCopyright.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(lblVersion);
        infoPanel.add(lblCopyright);
        formPanel.add(infoPanel, gbc);

        /* --- Assemblage final --- */
        loginCard.add(headerPanel, BorderLayout.NORTH);
        loginCard.add(formPanel, BorderLayout.CENTER);

        mainPanel.add(loginCard);
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        /* ========== GESTION DES ÉVÉNEMENTS ========== */
        btnLogin.addActionListener(e -> authenticate());

        // Entrée sur les champs de texte
        txtLogin.addActionListener(e -> txtPassword.requestFocus());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticate();
                }
            }
        });

        // Focus listeners pour améliorer l'UX
        txtLogin.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtLogin.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                txtLogin.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
            }
        });

        // Pour que les champs aient la même apparence d'écriture
        fixTextFieldsAppearance();
    }

    /* ===================== CLASSES PERSONNALISÉES ===================== */
    
    // Panel avec dégradé subtil
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

    // Panel aux coins arrondis
    private static class RoundedPanel extends JPanel {
        private int radius;
        
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ombre portée
            g2.setColor(SHADOW_COLOR);
            g2.fill(new RoundRectangle2D.Float(4, 4, getWidth()-8, getHeight()-8, radius, radius));
            
            // Fond du panel
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Bouton moderne avec coins arrondis et dégradé
    private static class RoundedButton extends JButton {
        private Color startColor, endColor;
        
        public RoundedButton(String text, Color start, Color end) {
            super(text);
            this.startColor = start;
            this.endColor = end;
            setForeground(Color.WHITE);
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startColor = BTN_HOVER_START;
                    endColor = BTN_HOVER_END;
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    startColor = BTN_GRAD_START;
                    endColor = BTN_GRAD_END;
                    repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    startColor = new Color(10, 60, 150);
                    endColor = new Color(15, 80, 180);
                    repaint();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    startColor = BTN_GRAD_START;
                    endColor = BTN_GRAD_END;
                    repaint();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Dégradé du bouton
            GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), 0, endColor);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            
            // Texte
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getHeight();
            
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() - textHeight) / 2 + fm.getAscent();
            
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    /* ===================== MÉTHODES UTILITAIRES ===================== */
    
    private JLabel createLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(new Color(250, 250, 250));
        
        // Style identique pour les deux champs
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Alignement du texte à gauche
        tf.setHorizontalAlignment(JTextField.LEFT);
        
        // Même couleur de texte
        tf.setCaretColor(TEXT_PRIMARY);
        
        return tf;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setForeground(TEXT_PRIMARY);
        pf.setBackground(new Color(250, 250, 250));
        
        // Style identique pour les deux champs
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Alignement du texte à gauche
        pf.setHorizontalAlignment(JTextField.LEFT);
        
        // Même couleur de texte et de caret
        pf.setCaretColor(TEXT_PRIMARY);
        
        return pf;
    }

    private void fixTextFieldsAppearance() {
        // S'assurer que les deux champs ont exactement la même apparence
        txtLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtLogin.setForeground(TEXT_PRIMARY);
        txtPassword.setForeground(TEXT_PRIMARY);
        
        txtLogin.setCaretColor(TEXT_PRIMARY);
        txtPassword.setCaretColor(TEXT_PRIMARY);
        
        txtLogin.setBackground(new Color(250, 250, 250));
        txtPassword.setBackground(new Color(250, 250, 250));
        
        // Même bordure pour les deux
        Border commonBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        );
        
        txtLogin.setBorder(commonBorder);
        txtPassword.setBorder(commonBorder);
    }

    private void applyModernStyles() {
        try {
            // Utiliser le look and feel du système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Personnalisations supplémentaires
            UIManager.put("Button.arc", 20);
            UIManager.put("Component.arc", 20);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 15);
            
        } catch (Exception e) {
            // Continuer avec le look and feel par défaut
        }
    }

    private void setupUI() {
        // Configuration supplémentaire de l'UI
        SwingUtilities.updateComponentTreeUI(this);
    }

    /* ===================== LOGIQUE D'AUTHENTIFICATION ===================== */
    
    private void authenticate() {
        String login = txtLogin.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validation
        if (login.isEmpty() || password.isEmpty()) {
            showErrorDialog("Champs requis", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Afficher l'indicateur de chargement
        btnLogin.setEnabled(false);
        loadingPanel.setVisible(true);
        txtLogin.setEnabled(false);
        txtPassword.setEnabled(false);

        // Simuler un délai pour l'authentification (pour l'effet visuel)
        Timer timer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAuthentication(login, password);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void performAuthentication(String login, String password) {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticate(login, password);

            if (user != null) {
                // Succès
                showSuccessDialog("Connexion réussie", 
                    "Bienvenue " + user.getNomCompagnie() + "!\n\n" +
                    "Accès autorisé au système de gestion.\n" +
                    "Vous allez être redirigé vers le tableau de bord.");
                
                // Ouvrir le dashboard
                SwingUtilities.invokeLater(() -> {
                    new DashboardFrame(user).setVisible(true);
                    dispose();
                });
            } else {
                // Échec
                showErrorDialog("Échec d'authentification", 
                    "Les identifiants saisis sont incorrects.\n" +
                    "Veuillez vérifier votre nom d'utilisateur et mot de passe.");
                
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (Exception ex) {
            showErrorDialog("Erreur système", 
                "Une erreur est survenue lors de la connexion.\n" +
                "Veuillez réessayer ou contacter le support.");
        } finally {
            // Réinitialiser l'interface
            btnLogin.setEnabled(true);
            loadingPanel.setVisible(false);
            txtLogin.setEnabled(true);
            txtPassword.setEnabled(true);
        }
    }

    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 250px;'>" + message.replace("\n", "<br>") + "</body></html>",
            title,
            JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessDialog(String title, String message) {
        JOptionPane.showMessageDialog(this,
            "<html><body style='width: 300px;'>" + message.replace("\n", "<br>") + "</body></html>",
            title,
            JOptionPane.INFORMATION_MESSAGE);
    }

    /* ===================== POINT D'ENTRÉE ===================== */
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
            
            // Animation d'apparition
            frame.setOpacity(0);
            Timer fadeIn = new Timer(10, new ActionListener() {
                float opacity = 0;
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    opacity += 0.05f;
                    frame.setOpacity(Math.min(opacity, 1.0f));
                    
                    if (opacity >= 1.0f) {
                        ((Timer) e.getSource()).stop();
                    }
                }
            });
            fadeIn.start();
        });
    }
}