package egibo.view;

public class LoginForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginForm.class.getName());

    public LoginForm() {
        initComponents();
        setLocationRelativeTo(null);
        
    txtboxHours.setForeground(java.awt.Color.GRAY);
    txtboxHours.setText("e.g., 2.5");
    
    txtboxHours.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (txtboxHours.getText().equals("e.g., 2.5")) {
                txtboxHours.setText("");
                txtboxHours.setForeground(java.awt.Color.BLACK);
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (txtboxHours.getText().isEmpty()) {
                txtboxHours.setForeground(java.awt.Color.GRAY);
                txtboxHours.setText("e.g., 2.5");
            }
        }
}); 

    // 1. Get the CardLayout manager from your gray panel
    java.awt.CardLayout cl = (java.awt.CardLayout) cardContainerPanel.getLayout();

    // 2. Explicitly add your custom panels to the card container with string identifiers
    cardContainerPanel.add(menuCard, "MENU");
    cardContainerPanel.add(loginCard, "LOGIN");
    cardContainerPanel.add(registerCard, "REGISTER");

    // 3. Button Click Listeners to switch between them seamlessly
    
    //From Menu -> Go to Login or Register form
    btnGoToLogin.addActionListener(e -> cl.show(cardContainerPanel, "LOGIN"));
    btnGoToRegister.addActionListener(e -> cl.show(cardContainerPanel, "REGISTER"));
    
    //From Login or Register -> Go back to Menu
    btnBackFromLogin.addActionListener(e -> cl.show(cardContainerPanel, "MENU"));
    btnBackFromRegister.addActionListener(e -> cl.show(cardContainerPanel, "MENU"));
    
    // 4. Submit Listeners
        // Login Submit
        btnLoginEnter.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(jPasswordField1.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Please enter your username and password.",
                        "Missing Information",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                try {
                    egibo.database.UserDAO userDAO =
                        new egibo.database.UserDAO();

                    double capacity =
                        userDAO.loginAndGetCapacity(username, password);

                    if (capacity < 0) {
                        javax.swing.JOptionPane.showMessageDialog(
                            LoginForm.this,
                            "Unable to identify your account.",
                            "Login Failed",
                            javax.swing.JOptionPane.WARNING_MESSAGE
                        );
                    return;
                    }

               int userId = userDAO.getUserId(username);

            if (userId <= 0) {
                javax.swing.JOptionPane.showMessageDialog(
                LoginForm.this,
                    "Unable to identify your account.",
                    "Login Failed",
                javax.swing.JOptionPane.ERROR_MESSAGE
                );
            return;
            }
            
            egibo.model.StudentProfile studentProfile =
                new egibo.model.StudentProfile(username, capacity);

            egibo.model.TaskManager taskManager;

            try {
                taskManager = new egibo.model.TaskManager(userId);
            } catch (java.sql.SQLException sqlEx) {
                logger.log(
                java.util.logging.Level.SEVERE,
                    "Failed to load tasks from database.",
                sqlEx
            );
                
            javax.swing.JOptionPane.showMessageDialog(
                LoginForm.this,
                "Could not load your tasks from the database. "
                    + "Please check your database connection.",
                "Database Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Dashboard dashboard =
            new Dashboard(studentProfile, taskManager);

            dashboard.setVisible(true);
            LoginForm.this.dispose();

                } catch (Exception ex) {
                    logger.severe("Login error: " + ex.getMessage());

                    javax.swing.JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Unable to log in. Please check your database connection.",
                        "Database Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                        );
                }
            }
        });
        
        // Register Submit (Validates fields and opens dashboard like usual)
        btnRegisterEnter.addActionListener(new java.awt.event.ActionListener() {
            @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                String username = txtUsername1.getText().trim();
                String password = new String(jPasswordField2.getPassword());
                String capacityText = txtboxHours.getText().trim();

                if (username.isEmpty()
                    || password.isEmpty()
                    || capacityText.isEmpty()
                    || capacityText.equals("e.g., 2.5")) {

                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "Please fill out all fields correctly.",
                    "Missing Information",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                return;
                }

            double dailyCapacity;

            try {
                dailyCapacity = Double.parseDouble(capacityText);
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "Please enter a valid number for work capacity.",
                    "Format Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (!Double.isFinite(dailyCapacity) || dailyCapacity <= 0.0) {
                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "Your capacity must be greater than 0 hours.",
                    "Invalid Capacity",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (dailyCapacity > 16.0) {
                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "Your capacity cannot be more than 16 hours.",
                    "Capacity Too High",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try {
                egibo.database.UserDAO userDAO =
                    new egibo.database.UserDAO();

                boolean registered =
                    userDAO.register(username, password, dailyCapacity);

                if (!registered) {
                    javax.swing.JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Account registration failed. Please try again.",
                        "Registration Failed",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "Account created successfully! You can now log in.",
                    "Registration Successful",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
                );

                // Return to the Login card instead of opening Dashboard.
                java.awt.CardLayout cardLayout =
                    (java.awt.CardLayout) cardContainerPanel.getLayout();

                cardLayout.show(cardContainerPanel, "loginCard");

                // Pre-fill the login username.
                txtUsername.setText(username);

                // Clear registration fields.
                txtUsername1.setText("");
                jPasswordField2.setText("");
                txtboxHours.setText("");

                jPasswordField1.requestFocusInWindow();

            } catch (java.sql.SQLException ex) {
                if ("23000".equals(ex.getSQLState())) {
                    javax.swing.JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "That username is already registered. Please choose another.",
                        "Username Already Exists",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                } else {
                    logger.severe("Registration SQL error: " + ex.getMessage());

                    javax.swing.JOptionPane.showMessageDialog(
                        LoginForm.this,
                        "Unable to save your account. Please check your database.",
                        "Database Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (Exception ex) {
                logger.severe("Registration error: " + ex.getMessage());

                javax.swing.JOptionPane.showMessageDialog(
                    LoginForm.this,
                    "An error occurred while creating your account.",
                    "Registration Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        }
    });
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlePanel = new javax.swing.JPanel();
        cardContainerPanel = new javax.swing.JPanel();
        menuCard = new javax.swing.JPanel();
        btnGoToLogin = new javax.swing.JButton();
        btnGoToRegister = new javax.swing.JButton();
        loginCard = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        btnLoginEnter = new javax.swing.JButton();
        btnBackFromLogin = new javax.swing.JButton();
        registerCard = new javax.swing.JPanel();
        lblUsername1 = new javax.swing.JLabel();
        txtUsername1 = new javax.swing.JTextField();
        lblPassword1 = new javax.swing.JLabel();
        jPasswordField2 = new javax.swing.JPasswordField();
        lblCapacity = new javax.swing.JLabel();
        txtboxHours = new javax.swing.JTextField();
        btnRegisterEnter = new javax.swing.JButton();
        btnBackFromRegister = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("eGIBO mo na!");
        setBackground(new java.awt.Color(199, 213, 214));
        setName(""); // NOI18N

        titlePanel.setBackground(new java.awt.Color(255, 255, 255));
        titlePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(167, 195, 195), 4, true));

        cardContainerPanel.setBackground(new java.awt.Color(255, 255, 255));
        cardContainerPanel.setLayout(new java.awt.CardLayout());

        menuCard.setBackground(new java.awt.Color(255, 255, 255));
        menuCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnGoToLogin.setBackground(new java.awt.Color(163, 218, 218));
        btnGoToLogin.setFont(new java.awt.Font("SimSun-ExtB", 1, 18)); // NOI18N
        btnGoToLogin.setText("Login");
        btnGoToLogin.addActionListener(this::btnGoToLoginActionPerformed);
        menuCard.add(btnGoToLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 210, 40));

        btnGoToRegister.setBackground(new java.awt.Color(163, 218, 218));
        btnGoToRegister.setFont(new java.awt.Font("SimSun-ExtB", 1, 18)); // NOI18N
        btnGoToRegister.setText("Create Account");
        menuCard.add(btnGoToRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 70, 210, 40));

        cardContainerPanel.add(menuCard, "card6");

        loginCard.setBackground(new java.awt.Color(255, 255, 255));

        lblUsername.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(67, 96, 96));
        lblUsername.setText("Username:");
        lblUsername.setName("txtName"); // NOI18N

        txtUsername.setFont(new java.awt.Font("Calisto MT", 0, 12)); // NOI18N
        txtUsername.addActionListener(this::txtUsernameActionPerformed);

        lblPassword.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(67, 96, 96));
        lblPassword.setText("Password: ");

        btnLoginEnter.setBackground(new java.awt.Color(101, 220, 220));
        btnLoginEnter.setFont(new java.awt.Font("SimSun-ExtG", 1, 12)); // NOI18N
        btnLoginEnter.setText("Login");

        btnBackFromLogin.setBackground(new java.awt.Color(214, 243, 243));
        btnBackFromLogin.setFont(new java.awt.Font("SimSun-ExtB", 0, 12)); // NOI18N
        btnBackFromLogin.setForeground(new java.awt.Color(0, 102, 102));
        btnBackFromLogin.setText("Back");

        javax.swing.GroupLayout loginCardLayout = new javax.swing.GroupLayout(loginCard);
        loginCard.setLayout(loginCardLayout);
        loginCardLayout.setHorizontalGroup(
            loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginCardLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginCardLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(btnLoginEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBackFromLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(loginCardLayout.createSequentialGroup()
                        .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword)
                            .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14))
        );
        loginCardLayout.setVerticalGroup(
            loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginCardLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(loginCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLoginEnter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBackFromLogin))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        lblUsername.getAccessibleContext().setAccessibleName("txtName");

        cardContainerPanel.add(loginCard, "card8");

        registerCard.setBackground(new java.awt.Color(255, 255, 255));

        lblUsername1.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        lblUsername1.setForeground(new java.awt.Color(67, 96, 96));
        lblUsername1.setText("Username:");
        lblUsername1.setName("txtName"); // NOI18N

        txtUsername1.setFont(new java.awt.Font("Calisto MT", 0, 12)); // NOI18N
        txtUsername1.addActionListener(this::txtUsername1ActionPerformed);

        lblPassword1.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        lblPassword1.setForeground(new java.awt.Color(67, 96, 96));
        lblPassword1.setText("Password: ");

        lblCapacity.setFont(new java.awt.Font("Franklin Gothic Medium", 0, 12)); // NOI18N
        lblCapacity.setForeground(new java.awt.Color(67, 96, 96));
        lblCapacity.setText("Work Capacity (hr):");

        txtboxHours.setFont(new java.awt.Font("Calisto MT", 0, 12)); // NOI18N
        txtboxHours.addActionListener(this::txtboxHoursActionPerformed);

        btnRegisterEnter.setBackground(new java.awt.Color(101, 220, 220));
        btnRegisterEnter.setFont(new java.awt.Font("SimSun-ExtG", 1, 12)); // NOI18N
        btnRegisterEnter.setText("Register & Login");

        btnBackFromRegister.setBackground(new java.awt.Color(214, 243, 243));
        btnBackFromRegister.setFont(new java.awt.Font("SimSun-ExtB", 0, 12)); // NOI18N
        btnBackFromRegister.setForeground(new java.awt.Color(0, 102, 102));
        btnBackFromRegister.setText("Back");

        javax.swing.GroupLayout registerCardLayout = new javax.swing.GroupLayout(registerCard);
        registerCard.setLayout(registerCardLayout);
        registerCardLayout.setHorizontalGroup(
            registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerCardLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(registerCardLayout.createSequentialGroup()
                        .addComponent(lblCapacity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtboxHours))
                    .addGroup(registerCardLayout.createSequentialGroup()
                        .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword1)
                            .addComponent(lblUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername1)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(16, 16, 16))
            .addGroup(registerCardLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(btnRegisterEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBackFromRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        registerCardLayout.setVerticalGroup(
            registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(registerCardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername1)
                    .addComponent(txtUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtboxHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCapacity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(registerCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegisterEnter)
                    .addComponent(btnBackFromRegister, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
        );

        cardContainerPanel.add(registerCard, "card7");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 3, 10)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(205, 224, 224));
        jLabel8.setText("A Student Workload Management System");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel8.setName("txtName"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Screenshot 2026-10-22 205723.png"))); // NOI18N

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cardContainerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(titlePanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, titlePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(39, 39, 39))
        );
        titlePanelLayout.setVerticalGroup(
            titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titlePanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cardContainerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/egibo/model/image/Screenshot 2026-10-20 112315.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI Emoji", 1, 18)); // NOI18N
        jLabel2.setText(" ⌨️. 🌿+-------------------- ");
        jLabel2.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(13, 13, 13))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**/
    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtboxHoursActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtboxHoursActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtboxHoursActionPerformed

    private void txtUsername1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsername1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsername1ActionPerformed

    private void btnGoToLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoToLoginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGoToLoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackFromLogin;
    private javax.swing.JButton btnBackFromRegister;
    private javax.swing.JButton btnGoToLogin;
    private javax.swing.JButton btnGoToRegister;
    private javax.swing.JButton btnLoginEnter;
    private javax.swing.JButton btnRegisterEnter;
    private javax.swing.JPanel cardContainerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JLabel lblCapacity;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPassword1;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblUsername1;
    private javax.swing.JPanel loginCard;
    private javax.swing.JPanel menuCard;
    private javax.swing.JPanel registerCard;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtUsername1;
    private javax.swing.JTextField txtboxHours;
    // End of variables declaration//GEN-END:variables
}
