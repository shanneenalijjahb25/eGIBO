/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package egibo.view;
import java.time.LocalDate;
import java.time.LocalTime;
/**
 *
 * @author Shanneen Alijjah Boarao
 */
public class AddTaskForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AddTaskForm.class.getName());
    
    private final egibo.model.TaskManager taskManager;
    private final Dashboard parentDashboard;
    private final java.awt.CardLayout cardLayout;
    
    public AddTaskForm(Dashboard parent, egibo.model.TaskManager taskManager) {
        setUndecorated(true);
        initComponents();
        
        this.getRootPane().setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 102, 102), 3));
        setLocationRelativeTo(parent);
        
        this.parentDashboard = parent;
        this.taskManager = taskManager;   
        

// 1. Group the radio buttons together programmatically
    btngroupTaskType.add(rbFixedTask);
    btngroupTaskType.add(rbFlexibleTask);
    
    // 2. Setup CardLayout reference from your container panel
    cardLayout = (java.awt.CardLayout) pnlDynamicContainer.getLayout();
    
    // 3. Select Class Schedule (Fixed) by default on load
        rbFixedTask.addActionListener(e -> {
        cardLayout.show(pnlDynamicContainer, "card2"); // Change "card2" to whatever card name you assigned for Fixed tasks
    });

    rbFlexibleTask.addActionListener(e -> {
        cardLayout.show(pnlDynamicContainer, "card3"); // Change "card3" to whatever card name you assigned for Flexible tasks
    });
    
    txtboxDate.setForeground(java.awt.Color.GRAY);
    txtboxDate.setText("YYYY-MM-DD");

    txtboxDate.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (txtboxDate.getText().equals("YYYY-MM-DD")) {
                txtboxDate.setText("");
                txtboxDate.setForeground(java.awt.Color.BLACK); // Change back to normal typing color
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (txtboxDate.getText().isEmpty()) {
                txtboxDate.setForeground(java.awt.Color.GRAY);
                txtboxDate.setText("YYYY-MM-DD");
            }
        }
    });
    // --- START TIME PLACEHOLDER ---
        txtboxStartTime.setForeground(java.awt.Color.GRAY);
        txtboxStartTime.setText("HH:mm (00:00 - 23:59)");

        txtboxStartTime.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtboxStartTime.getText().equals("HH:mm (00:00 - 23:59)")) {
                    txtboxStartTime.setText("");
                    txtboxStartTime.setForeground(java.awt.Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtboxStartTime.getText().isEmpty()) {
                    txtboxStartTime.setForeground(java.awt.Color.GRAY);
                    txtboxStartTime.setText("HH:mm (00:00 - 23:59)");
                }
            }
        });
    // --- END TIME PLACEHOLDER ---
        txtboxEndTime.setForeground(java.awt.Color.GRAY);
        txtboxEndTime.setText("HH:mm (00:00 - 23:59)");

        txtboxEndTime.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtboxEndTime.getText().equals("HH:mm (00:00 - 23:59)")) {
                    txtboxEndTime.setText("");
                    txtboxEndTime.setForeground(java.awt.Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtboxEndTime.getText().isEmpty()) {
                    txtboxEndTime.setForeground(java.awt.Color.GRAY);
                    txtboxEndTime.setText("HH:mm (00:00 - 23:59)");
                }
            }
        });
        
        // --- EXPECTED HOURS PLACEHOLDER ---
        txtboxHours.setForeground(java.awt.Color.GRAY);
        txtboxHours.setText("e.g., 2.5");
        
        // --- FLEXIBLE TASK TIME PLACEHOLDER ---
        txtboxFlexTime.setForeground(java.awt.Color.GRAY);
        txtboxFlexTime.setText("HH:mm (e.g., 14:00)");

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
        
        txtboxFlexTime.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (txtboxFlexTime.getText().equals("HH:mm (e.g., 14:00)")) {
                txtboxFlexTime.setText("");
                txtboxFlexTime.setForeground(java.awt.Color.BLACK);
            }
        }
        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (txtboxFlexTime.getText().isEmpty()) {
                txtboxFlexTime.setForeground(java.awt.Color.GRAY);
                txtboxFlexTime.setText("HH:mm (e.g., 14:00)");
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

        btngroupTaskType = new javax.swing.ButtonGroup();
        lblEmoji = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        txtboxDescription = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        txtboxTitle = new javax.swing.JTextField();
        rbFixedTask = new javax.swing.JRadioButton();
        rbFlexibleTask = new javax.swing.JRadioButton();
        pnlDynamicContainer = new javax.swing.JPanel();
        pnlFixedTask = new javax.swing.JPanel();
        lblStartTime = new javax.swing.JLabel();
        lblEndTime = new javax.swing.JLabel();
        txtboxStartTime = new javax.swing.JTextField();
        txtboxEndTime = new javax.swing.JTextField();
        pnlFlexibleTask = new javax.swing.JPanel();
        txtHours = new javax.swing.JLabel();
        txtboxHours = new javax.swing.JTextField();
        lblFlexTime = new javax.swing.JLabel();
        txtboxFlexTime = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txtImportance = new javax.swing.JLabel();
        comboImportance = new javax.swing.JComboBox<>();
        txtDate = new javax.swing.JLabel();
        txtboxDate = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblEmoji.setFont(new java.awt.Font("Segoe UI Emoji", 1, 36)); // NOI18N
        lblEmoji.setForeground(new java.awt.Color(0, 102, 102));
        lblEmoji.setText("💫˚  :");
        lblEmoji.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblEmoji.setAlignmentY(0.0F);
        lblEmoji.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lblEmoji.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        lblTitle.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblTitle.setText("Title: ");

        txtboxDescription.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        lblDescription.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblDescription.setText("Description: ");

        txtboxTitle.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N

        btngroupTaskType.add(rbFixedTask);
        rbFixedTask.setFont(new java.awt.Font("SimSun-ExtG", 1, 12)); // NOI18N
        rbFixedTask.setForeground(new java.awt.Color(0, 102, 102));
        rbFixedTask.setText("Class Schedule / Event");
        rbFixedTask.addActionListener(this::rbFixedTaskActionPerformed);

        btngroupTaskType.add(rbFlexibleTask);
        rbFlexibleTask.setFont(new java.awt.Font("SimSun-ExtG", 1, 12)); // NOI18N
        rbFlexibleTask.setForeground(new java.awt.Color(0, 102, 102));
        rbFlexibleTask.setText("Assignments / Projects");
        rbFlexibleTask.addActionListener(this::rbFlexibleTaskActionPerformed);

        pnlDynamicContainer.setBackground(new java.awt.Color(162, 192, 192));
        pnlDynamicContainer.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        pnlDynamicContainer.setLayout(new java.awt.CardLayout());

        pnlFixedTask.setBackground(new java.awt.Color(255, 255, 255));

        lblStartTime.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblStartTime.setForeground(new java.awt.Color(0, 153, 153));
        lblStartTime.setText("Start Time:");

        lblEndTime.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblEndTime.setForeground(new java.awt.Color(0, 153, 153));
        lblEndTime.setText("End Time: ");

        txtboxStartTime.setBackground(new java.awt.Color(213, 234, 235));
        txtboxStartTime.setToolTipText("");

        txtboxEndTime.setBackground(new java.awt.Color(213, 234, 235));
        txtboxEndTime.setToolTipText("");

        javax.swing.GroupLayout pnlFixedTaskLayout = new javax.swing.GroupLayout(pnlFixedTask);
        pnlFixedTask.setLayout(pnlFixedTaskLayout);
        pnlFixedTaskLayout.setHorizontalGroup(
            pnlFixedTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFixedTaskLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlFixedTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFixedTaskLayout.createSequentialGroup()
                        .addComponent(lblEndTime)
                        .addGap(19, 19, 19)
                        .addComponent(txtboxEndTime))
                    .addGroup(pnlFixedTaskLayout.createSequentialGroup()
                        .addComponent(lblStartTime)
                        .addGap(18, 18, 18)
                        .addComponent(txtboxStartTime)))
                .addGap(24, 24, 24))
        );
        pnlFixedTaskLayout.setVerticalGroup(
            pnlFixedTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFixedTaskLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(pnlFixedTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtboxStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFixedTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEndTime)
                    .addComponent(txtboxEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pnlDynamicContainer.add(pnlFixedTask, "card2");

        pnlFlexibleTask.setBackground(new java.awt.Color(255, 255, 255));

        txtHours.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        txtHours.setForeground(new java.awt.Color(0, 153, 153));
        txtHours.setText("Estimated Working Hours:");

        txtboxHours.setBackground(new java.awt.Color(213, 234, 235));

        lblFlexTime.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblFlexTime.setForeground(new java.awt.Color(0, 153, 153));
        lblFlexTime.setText("Schedule Work Time:");

        txtboxFlexTime.setBackground(new java.awt.Color(213, 234, 235));

        javax.swing.GroupLayout pnlFlexibleTaskLayout = new javax.swing.GroupLayout(pnlFlexibleTask);
        pnlFlexibleTask.setLayout(pnlFlexibleTaskLayout);
        pnlFlexibleTaskLayout.setHorizontalGroup(
            pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFlexibleTaskLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtHours, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFlexTime, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtboxHours)
                    .addComponent(txtboxFlexTime, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnlFlexibleTaskLayout.setVerticalGroup(
            pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFlexibleTaskLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHours)
                    .addComponent(txtboxHours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFlexibleTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFlexTime)
                    .addComponent(txtboxFlexTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pnlDynamicContainer.add(pnlFlexibleTask, "card3");

        btnSave.setBackground(new java.awt.Color(0, 102, 102));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save");
        btnSave.addActionListener(this::btnSaveActionPerformed);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(this::btnCancelActionPerformed);

        txtImportance.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        txtImportance.setText("Priority Level:");

        comboImportance.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        comboImportance.setForeground(new java.awt.Color(0, 51, 51));
        comboImportance.setMaximumRowCount(5);
        comboImportance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 — Low Priority (Optional / Do when free)", "2 — Minor (Nice to finish today)", "3 — Normal (Standard task)", "4 — High (Important / Strict Deadline)", "5 — Urgent (Critical / Do Immediately)", " ", " " }));
        comboImportance.addActionListener(this::comboImportanceActionPerformed);

        txtDate.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        txtDate.setText("Date:");

        jLabel1.setFont(new java.awt.Font("Georgia", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 102));
        jLabel1.setText("Add Task");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlDynamicContainer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(rbFixedTask, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(rbFlexibleTask)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtboxDate, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(txtImportance)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(comboImportance, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblEmoji)
                                .addGap(28, 28, 28)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle)
                                    .addComponent(lblDescription)
                                    .addComponent(txtDate))
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtboxDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtboxTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(37, 37, 37))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmoji, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblTitle))
                    .addComponent(txtboxTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(txtboxDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtboxDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtImportance)
                    .addComponent(comboImportance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbFixedTask)
                    .addComponent(rbFlexibleTask))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDynamicContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnCancel))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbFixedTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFixedTaskActionPerformed
        cardLayout.show(pnlDynamicContainer, "pnlFixedTask");        // TODO add your handling code here:
    }//GEN-LAST:event_rbFixedTaskActionPerformed

    private void rbFlexibleTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFlexibleTaskActionPerformed
        cardLayout.show(pnlDynamicContainer, "pnlFlexibleTask");        // TODO add your handling code here:
    }//GEN-LAST:event_rbFlexibleTaskActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            // Read common fields
            String title = txtboxTitle.getText().trim();
            String description = txtboxDescription.getText().trim();
            
            if (title.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Please enter a task title.", "Validation Error", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Priority Extraction
            String selectedImportance = comboImportance.getSelectedItem().toString();
            int importance = Character.getNumericValue(selectedImportance.charAt(0));
            
            // Date Validation with Permission Prompt
            String dateText = txtboxDate.getText().trim();
            LocalDate taskDate = null;
            
            if (dateText.isEmpty() || dateText.equals("YYYY-MM-DD")) {
                int choice = javax.swing.JOptionPane.showConfirmDialog(this, 
                    "No date was entered. Would you like the system to set it to today?", 
                    "Default Date Prompt", 
                    javax.swing.JOptionPane.YES_NO_OPTION, 
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
                
                if (choice == javax.swing.JOptionPane.YES_OPTION) {
                    taskDate = LocalDate.now();
                } else {
                    return; 
                }
            } else {
                taskDate = LocalDate.parse(dateText);
            }

            boolean hasConflict = false;

            if (rbFixedTask.isSelected()) {
                LocalTime startTime = LocalTime.parse(txtboxStartTime.getText().trim()); 
                LocalTime endTime = LocalTime.parse(txtboxEndTime.getText().trim()); 
                
                egibo.model.FixedTask newTask = new egibo.model.FixedTask(
                    startTime,  
                    endTime,     
                    title,       
                    description, 
                    0.0,         
                    importance, 
                    taskDate
                );
                
                // Polymorphic conflict check
                for (egibo.model.Task existingTask : taskManager.getPendingTasks()) {
                    if (existingTask.occupiesTime(newTask)) {
                        hasConflict = true;
                        break;
                    }
                }
                
                if (hasConflict) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Warning: This scheduled time slot conflicts with an existing task!", 
                        "Schedule Conflict", 
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                taskManager.addFixedTask(startTime, endTime, title, description, 0.0, importance, taskDate);

            } else {
                // Flexible Task: Read hours and scheduled work start time
                double hours = Double.parseDouble(txtboxHours.getText().trim());
                LocalTime workStartTime = LocalTime.parse(txtboxFlexTime.getText().trim());
                
                // Instantiate FlexibleTask with the new workStartTime parameter
                egibo.model.FlexibleTask newTask = new egibo.model.FlexibleTask(
                    title, 
                    description, 
                    hours, 
                    importance, 
                    taskDate, 
                    workStartTime
                );
                
                // Run the conflict check against pending tasks
                for (egibo.model.Task existingTask : taskManager.getPendingTasks()) {
                    if (existingTask.occupiesTime(newTask)) {
                        hasConflict = true;
                        break;
                    }
                }
                
                if (hasConflict) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Warning: This scheduled working time conflicts with an existing task allocation!", 
                        "Schedule Conflict", 
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Save to taskManager (update parameters here if your addFlexibleTask method also needs workStartTime)
                taskManager.addFlexibleTask(title, description, hours, importance, taskDate, workStartTime);
            }

            if (parentDashboard != null) {
                parentDashboard.refreshDashboard(); 
            }
            
            java.awt.Window parent = this.getOwner();
                if (parent != null) {
                    parent.setVisible(true);
                }
            this.dispose();

        } catch (java.time.format.DateTimeParseException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter valid dates (YYYY-MM-DD) or times in HH:mm format.", "Format Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter a valid number for working hours.", "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void comboImportanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboImportanceActionPerformed
        //TODO add your handling code here:
    }//GEN-LAST:event_comboImportanceActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup btngroupTaskType;
    private javax.swing.JComboBox<String> comboImportance;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblEmoji;
    private javax.swing.JLabel lblEndTime;
    private javax.swing.JLabel lblFlexTime;
    private javax.swing.JLabel lblStartTime;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlDynamicContainer;
    private javax.swing.JPanel pnlFixedTask;
    private javax.swing.JPanel pnlFlexibleTask;
    private javax.swing.JRadioButton rbFixedTask;
    private javax.swing.JRadioButton rbFlexibleTask;
    private javax.swing.JLabel txtDate;
    private javax.swing.JLabel txtHours;
    private javax.swing.JLabel txtImportance;
    private javax.swing.JTextField txtboxDate;
    private javax.swing.JTextField txtboxDescription;
    private javax.swing.JTextField txtboxEndTime;
    private javax.swing.JTextField txtboxFlexTime;
    private javax.swing.JTextField txtboxHours;
    private javax.swing.JTextField txtboxStartTime;
    private javax.swing.JTextField txtboxTitle;
    // End of variables declaration//GEN-END:variables
}
