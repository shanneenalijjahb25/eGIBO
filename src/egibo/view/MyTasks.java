/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package egibo.view;
import egibo.model.Status;
import egibo.model.Task;
import egibo.model.TaskManager;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class MyTasks extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MyTasks.class.getName());
    
    private Dashboard parentDashboard;
    private TaskManager taskManager;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
     public MyTasks() {
        this(null, new TaskManager());
    }

    // Main constructor linked with Dashboard and TaskManager
    public MyTasks(Dashboard parent, TaskManager taskManager) {
        initComponents();
        setTitle("eGIBO - My Tasks");
        setSize(1110, 650);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.parentDashboard = parent;
        this.taskManager = taskManager;

        // 1. Setup Table Columns (Including Description)
        String[] columns = {"Task Title", "Due Date", "Hours", "Priority", "Status", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table cells
            }
        };
        
        tblTasks.setModel(tableModel);
        tblTasks.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTasks.setRowHeight(28);
        tblTasks.setFillsViewportHeight(true);

        // Setup row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        tblTasks.setRowSorter(rowSorter);

        // 2. Load data & wire listeners
        refreshTableData();
        setupCustomListeners();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowOpened(java.awt.event.WindowEvent evt) {
            refreshTableData();
        }
        });
    }
    
    private void refreshTableData() {
        tableModel.setRowCount(0); // Clear old rows
        List<Task> tasks = taskManager.getAllTasks();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Task t : tasks) {
            Status displayStatus = t.getStatus();
            
            // If it's not done and the due date has passed, flag it as OVERDUE
            if (t.getStatus() != Status.DONE && t.getDueDate() != null && t.getDueDate().isBefore(today)) {
                displayStatus = Status.OVERDUE; 
            }
            Object[] rowData = {
                t.getTitle(),
                t.getDueDate(),
                t.getHours(),
                t.getImportance(),
                t.getStatus(),
                t.getDescription()
            };
            tableModel.addRow(rowData);
        }

        updateTaskCountLabel();
    }
    
    private void updateTaskCountLabel() {
        int count = tblTasks.getRowCount();
        lblTaskCount.setText("Showing " + count + " tasks");
    }
    
        private void setupCustomListeners() {
        // --- Sidebar Navigation ---
        buttonDashboard.addActionListener(e -> {
            if (parentDashboard != null) {
                parentDashboard.setVisible(true);
                parentDashboard.refreshDashboard();
            }
            this.dispose();
        });

        btnMyTasks.addActionListener(e -> {
            // Already here, refresh data
            refreshTableData();
        });

        // --- Add Task ---
        btnAddTask.addActionListener(e -> {
            AddTaskForm form = new AddTaskForm(parentDashboard, taskManager);
            form.setVisible(true);
        });

        // --- Search Box Placeholder & Key Listener ---
        txtSearch.setForeground(java.awt.Color.GRAY);
        txtSearch.setText("Search tasks...");
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search tasks...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(java.awt.Color.BLACK);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(java.awt.Color.GRAY);
                    txtSearch.setText("Search tasks...");
                }
            }
        });

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilters();
            }
        });

        // --- Dropdown Filters ---
        cmbStatus.addActionListener(e -> applyFilters());
        cmbPriority.addActionListener(e -> applyFilters());

        // --- Mark Done Action ---
        btnMarkDone.addActionListener(e -> {
            int selectedRow = tblTasks.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = tblTasks.convertRowIndexToModel(selectedRow);
                Task task = taskManager.getAllTasks().get(modelRow);
                
                // Toggle status: if it's already DONE, make it PENDING. Otherwise, make it DONE.
                if (task.getStatus() == Status.DONE) {
                    task.setStatus(Status.PENDING);
                } else {
                    task.setStatus(Status.DONE);
                }
                
                // Refresh both views
                refreshTableData();
                if (parentDashboard != null) {
                    parentDashboard.refreshDashboard();
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                    this, 
                    "Please select a task from the table first.", 
                    "Selection Required", 
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
    }
    });

        // --- Delete Task Action ---
        btnDeleteTask.addActionListener(e -> {
          int selectedRow = tblTasks.getSelectedRow();
          if (selectedRow != -1) {
              int modelRow = tblTasks.convertRowIndexToModel(selectedRow);
              int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
              if (confirm == JOptionPane.YES_OPTION) {
                  // Fetch the actual Task object from taskManager using index
                  Task taskToRemove = taskManager.getAllTasks().get(modelRow);
                  taskManager.removeTask(taskToRemove); // Pass the Task object instead of modelRow

                  refreshTableData();
                  if (parentDashboard != null) parentDashboard.refreshDashboard();
              }
          } else {
              JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
          }
      });

        // --- Edit Task Action ---
        btnEditTask.addActionListener(e -> {
            // 1. Get the selected row index from your table
            int selectedRow = tblTasks.getSelectedRow(); // Replace with your actual JTable variable name

            if (selectedRow == -1) {
                javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Please select a task from the table to edit.",
                    "No Task Selected",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // 2. Retrieve the actual Task object corresponding to that row from your TaskManager / Table Model
            // (Assuming your table maps directly to your pending/all tasks list)
            egibo.model.Task selectedTask = taskManager.getPendingTasks().get(selectedRow); 

            // 3. Open the EditTaskForm and pass the selected task along
            EditTaskForm editForm = new EditTaskForm(parentDashboard, taskManager, selectedTask);
            editForm.setVisible(true);
        });
    }
        
        private void applyFilters() {
        String searchText = txtSearch.getText().trim();
        if (searchText.equals("Search tasks...")) {
            searchText = "";
        }

        String statusFilter = cmbStatus.getSelectedItem().toString();
        String priorityFilter = cmbPriority.getSelectedItem().toString();

        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();

        // 1. Search filter (matches Title or Description)
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 0, 5)); // Column 0 (Title) and 5 (Description)
        }

        // 2. Status filter
        // 2. Status filter: Matches Status column (Column 4)
        if (!statusFilter.equals("All Statuses")) {
            filters.add(RowFilter.regexFilter("(?i)" + statusFilter, 4));
        }

        // 3. Priority filter: Maps dropdown choices to your numeric importance column (Column 3)
        if (!priorityFilter.equals("All Priorities")) {
            if (priorityFilter.equalsIgnoreCase("High")) {
                // Matches high importance values (4 or 5)
                filters.add(RowFilter.regexFilter("^[45]$", 3));
            } else if (priorityFilter.equalsIgnoreCase("Medium")) {
                // Matches normal/medium importance value (3)
                filters.add(RowFilter.regexFilter("^3$", 3));
            } else if (priorityFilter.equalsIgnoreCase("Low")) {
                // Matches low/minor importance values (1 or 2)
                filters.add(RowFilter.regexFilter("^[12]$", 3));
            }
        }

        // Apply filters or clear them if none are active
        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }

        updateTaskCountLabel();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        lblMyTasks = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnAddTask = new javax.swing.JButton();
        btnEditTask = new javax.swing.JButton();
        btnMarkDone = new javax.swing.JButton();
        btnDeleteTask = new javax.swing.JButton();
        cmbPriority = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        scrollTasks = new javax.swing.JScrollPane();
        tblTasks = new javax.swing.JTable();
        lblSubtitle = new javax.swing.JLabel();
        lblTaskCount = new javax.swing.JLabel();
        pnlSidebar = new javax.swing.JPanel();
        btnMyTasks = new javax.swing.JButton();
        buttonDashboard = new javax.swing.JButton();
        btnAboutUs = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblMyTasks1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlMain.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblMyTasks.setFont(new java.awt.Font("Georgia", 2, 36)); // NOI18N
        lblMyTasks.setText("My Tasks");

        txtSearch.setText("Search tasks...");

        btnAddTask.setFont(new java.awt.Font("Segoe UI Emoji", 3, 18)); // NOI18N
        btnAddTask.setForeground(new java.awt.Color(0, 102, 102));
        btnAddTask.setText("+    Add Task");
        btnAddTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(102, 102, 102)));
        btnAddTask.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddTask.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnEditTask.setFont(new java.awt.Font("Segoe UI Emoji", 3, 18)); // NOI18N
        btnEditTask.setForeground(new java.awt.Color(0, 102, 102));
        btnEditTask.setText("✎      Edit Task");
        btnEditTask.setAlignmentY(0.0F);
        btnEditTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(153, 153, 153)));
        btnEditTask.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditTask.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnEditTask.addActionListener(this::btnEditTaskActionPerformed);

        btnMarkDone.setFont(new java.awt.Font("Segoe UI Emoji", 3, 15)); // NOI18N
        btnMarkDone.setForeground(new java.awt.Color(124, 153, 134));
        btnMarkDone.setText("✓     Mark Done");
        btnMarkDone.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnMarkDone.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnMarkDone.addActionListener(this::btnMarkDoneActionPerformed);

        btnDeleteTask.setFont(new java.awt.Font("Segoe UI Emoji", 3, 15)); // NOI18N
        btnDeleteTask.setForeground(new java.awt.Color(139, 77, 77));
        btnDeleteTask.setText("🗑   Delete Task");
        btnDeleteTask.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnDeleteTask.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnDeleteTask.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        cmbPriority.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Priorities", "Low", "Medium", "High" }));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Statuses", "Pending", "Completed", "Overdue" }));

        scrollTasks.setViewportBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tblTasks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Task Title", "Due Date", "Hours", "Priority", "Status", "Description"
            }
        ));
        scrollTasks.setViewportView(tblTasks);

        lblSubtitle.setFont(new java.awt.Font("SimSun", 1, 12)); // NOI18N
        lblSubtitle.setText("Your tasks, your time, your balance");

        lblTaskCount.setFont(new java.awt.Font("SimSun-ExtG", 0, 14)); // NOI18N
        lblTaskCount.setText("Showing 0 tasks");

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblMyTasks)
                        .addGroup(pnlMainLayout.createSequentialGroup()
                            .addComponent(lblTaskCount, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(29, 29, 29)
                            .addComponent(btnEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(104, 104, 104)
                            .addComponent(btnMarkDone, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(4, 4, 4)
                            .addComponent(btnDeleteTask, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSubtitle)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblMyTasks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTaskCount, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(149, 149, 149))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAddTask, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                            .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnMarkDone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEditTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnDeleteTask, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pnlSidebar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnMyTasks.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnMyTasks.setText("My Tasks");
        btnMyTasks.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 2, true));

        buttonDashboard.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonDashboard.setText("Dashboard");
        buttonDashboard.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 102, 102), 2, true));
        buttonDashboard.addActionListener(this::buttonDashboardActionPerformed);

        btnAboutUs.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        btnAboutUs.setForeground(new java.awt.Color(91, 135, 135));
        btnAboutUs.setText("About Us");
        btnAboutUs.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(131, 171, 171), 2, true));
        btnAboutUs.addActionListener(this::btnAboutUsActionPerformed);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/egibo/model/image/egibo (3).png"))); // NOI18N
        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnAboutUs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlSidebarLayout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(buttonDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnMyTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(buttonDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMyTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAboutUs, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel2.setBackground(new java.awt.Color(0, 102, 102));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 60)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(136, 196, 196));
        jLabel3.setText("|");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblMyTasks1.setFont(new java.awt.Font("Georgia", 0, 24)); // NOI18N
        lblMyTasks1.setText("Dashboard > My Task");

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/egibo/view/Screenshot 2026-10-22 210418.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMyTasks1)
                .addContainerGap(644, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblMyTasks1)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMarkDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkDoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMarkDoneActionPerformed

    private void buttonDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDashboardActionPerformed
    if (parentDashboard != null) {
            parentDashboard.setVisible(true);
            parentDashboard.refreshDashboard(); // Forces the dashboard to reload tasks instantly!
        }
    this.dispose();
    }//GEN-LAST:event_buttonDashboardActionPerformed

    private void btnAboutUsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutUsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAboutUsActionPerformed

    private void btnEditTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditTaskActionPerformed
                // TODO add your handling code here:
    }//GEN-LAST:event_btnEditTaskActionPerformed

    /**
     * @param args the command line arguments
     */
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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MyTasks().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAboutUs;
    private javax.swing.JButton btnAddTask;
    private javax.swing.JButton btnDeleteTask;
    private javax.swing.JButton btnEditTask;
    private javax.swing.JButton btnMarkDone;
    private javax.swing.JButton btnMyTasks;
    private javax.swing.JButton buttonDashboard;
    private javax.swing.JComboBox<String> cmbPriority;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblMyTasks;
    private javax.swing.JLabel lblMyTasks1;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTaskCount;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JScrollPane scrollTasks;
    private javax.swing.JTable tblTasks;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
