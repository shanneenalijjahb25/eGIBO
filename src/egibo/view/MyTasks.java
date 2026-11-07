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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class MyTasks extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MyTasks.class.getName());
    
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

// Set to false if you still want Edit / Mark Done / Delete on past days
private static final boolean READ_ONLY_PAST_DAYS = true;
    
    private Dashboard parentDashboard;
    private TaskManager taskManager;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private LocalDate selectedDate = LocalDate.now();
    private LocalDate minDate;
    private JSpinner spnDate;
    private javax.swing.JButton btnPrevDay, btnNextDay;
    private boolean updatingSpinner = false;

    // Tasks currently shown in the table, in the same order as the model rows
    private List<Task> displayedTasks = new ArrayList<>();
    
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
        String[] columns = {"Task Title", "Due Date", "Hours", "Priority", "Status", "Description", "Completed On"};
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
        
        tblTasks.getTableHeader().putClientProperty("Table.isFileList", Boolean.TRUE);
        
        javax.swing.UIDefaults defaults = new javax.swing.UIDefaults();
        defaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].background", new java.awt.Color(0, 102, 102));
        tblTasks.getTableHeader().putClientProperty("Nimbus.Overrides", defaults);
        tblTasks.getTableHeader().putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        
        tblTasks.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        // Set a custom cell renderer for the Description column (assuming it's column index 5)
        int descriptionColumnIndex = 5; 
        tblTasks.getColumnModel().getColumn(descriptionColumnIndex).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {

                javax.swing.JLabel c = (javax.swing.JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // Wrap text cleanly using HTML if descriptions get long
                if (value != null) {
                    String text = value.toString();
                    if (text.length() > 40) { // Wrap if text is long
                        c.setText("<html><div style='width: 200px;'>" + text + "</div></html>");
                    } else {
                        c.setText(text);
                    }
                }
                return c;
            }
        });
        
        // Give the Description column a flexible initial preferred width
        tblTasks.getColumnModel().getColumn(descriptionColumnIndex).setPreferredWidth(220);
        
        // Setup row sorter for filtering
        rowSorter = new TableRowSorter<>(tableModel);
        tblTasks.setRowSorter(rowSorter);

        setupDateNavigation();
        
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
        tableModel.setRowCount(0);
        displayedTasks = new ArrayList<>();

        for (Task t : taskManager.getAllTasks()) {
            if (selectedDate.equals(t.getDueDate())) {
                displayedTasks.add(t);
            }
        }

        for (Task t : displayedTasks) {
            String completedOn = t.getCompletedAt() != null ? t.getCompletedAt().format(TS_FORMAT) : "—";
            tableModel.addRow(new Object[]{
                t.getTitle(), t.getDueDate(), t.getHours(),
                t.getImportance(), resolveDisplayStatus(t), t.getDescription(), completedOn
            });
        }
        updateTaskCountLabel();
    }

/** DONE stays DONE; anything not done on a past day is shown as OVERDUE (missed). */
    private Status resolveDisplayStatus(Task t) {
        if (t.getStatus() == Status.DONE) return Status.DONE;
        if (t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())) return Status.OVERDUE;
        return t.getStatus();
    }

            private Task getSelectedTask() {
                int viewRow = tblTasks.getSelectedRow();
                if (viewRow == -1) return null;
                return displayedTasks.get(tblTasks.convertRowIndexToModel(viewRow));
            }       

            private void updateTaskCountLabel() {
                lblTaskCount.setText("Showing " + tblTasks.getRowCount() + " tasks for " + selectedDate);
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

        // --- Mark Done ---
        btnMarkDone.addActionListener(e -> {
            Task task = getSelectedTask();
            if (task == null) {
                JOptionPane.showMessageDialog(this, "Please select a task from the table first.",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            task.setStatus(task.getStatus() == Status.DONE ? Status.PENDING : Status.DONE);
            refreshTableData();
            if (parentDashboard != null) parentDashboard.refreshDashboard();
        });

        // --- Delete ---
        btnDeleteTask.addActionListener(e -> {
            Task task = getSelectedTask();
            if (task == null) {
                JOptionPane.showMessageDialog(this, "Please select a task to delete.",
                        "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                taskManager.removeTask(task);
                refreshTableData();
                if (parentDashboard != null) parentDashboard.refreshDashboard();
            }
        });

        // --- Edit ---
        btnEditTask.addActionListener(e -> {
            Task task = getSelectedTask();
            if (task == null) {
                JOptionPane.showMessageDialog(this, "Please select a task from the table to edit.",
                        "No Task Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new EditTaskForm(parentDashboard, taskManager, task).setVisible(true);
            });
        // --- Double-click: Task Detail Inspector ---
        tblTasks.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
                    int viewRow = tblTasks.rowAtPoint(evt.getPoint());
                    if (viewRow < 0) return; // clicked empty space
                    tblTasks.setRowSelectionInterval(viewRow, viewRow);
                    Task task = getSelectedTask();
                    if (task != null) showTaskDetails(task);
                }
            }
        });
        }
        
        private void showTaskDetails(Task task) {
            String assigned = task.getDueDate() != null ? task.getDueDate().toString() : "—";
            String created  = task.getCreatedAt() != null ? task.getCreatedAt().format(TS_FORMAT) : "—";

            String completion;
            if (task.getStatus() == Status.DONE && task.getCompletedAt() != null) {
                completion = "✔ " + task.getCompletedAt().format(TS_FORMAT);
            } else if (task.getStatus() == Status.DONE) {
                completion = "✔ Done (no timestamp recorded)";
            } else if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
                completion = "✖ UNACCOMPLISHED (missed)";
            } else {
                completion = "✖ UNACCOMPLISHED (still pending)";
            }

            String desc = (task.getDescription() == null || task.getDescription().isBlank())
                    ? "(no description)" : task.getDescription();

            String text = "Title:  " + task.getTitle() + "\n\n"
                        + "Description:\n" + desc + "\n\n"
                        + "Assigned Date:  " + assigned + "\n"
                        + "Created:  " + created + "\n"
                        + "Completed:  " + completion;

            javax.swing.JTextArea area = new javax.swing.JTextArea(text);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setOpaque(false);
            area.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));

            javax.swing.JScrollPane pane = new javax.swing.JScrollPane(area);
            pane.setBorder(null);
            pane.setPreferredSize(new java.awt.Dimension(380, 230));

            JOptionPane.showMessageDialog(this, pane, "Task Details", JOptionPane.INFORMATION_MESSAGE);
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
                filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 0, 5));
            }

            // 2. Status filter
            // 2. Status filter: Matches Status column (Column 4)
            if (!statusFilter.equals("All Statuses")) {
                String match = statusFilter.equals("Completed") ? "DONE" : statusFilter;
                filters.add(RowFilter.regexFilter("(?i)^" + match + "$", 4));
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
        private void setupDateNavigation() {
            minDate = resolveAccountCreationDate();
            if (selectedDate.isBefore(minDate)) selectedDate = minDate;

            SpinnerDateModel model = new SpinnerDateModel(
                    toDate(selectedDate), toDate(minDate), null, java.util.Calendar.DAY_OF_MONTH);
            spnDate = new JSpinner(model);
            spnDate.setEditor(new JSpinner.DateEditor(spnDate, "yyyy-MM-dd"));
            spnDate.setPreferredSize(new java.awt.Dimension(110, 28));

            btnPrevDay = new javax.swing.JButton("<");
            btnNextDay = new javax.swing.JButton(">");
            btnPrevDay.setPreferredSize(new java.awt.Dimension(45, 28));
            btnNextDay.setPreferredSize(new java.awt.Dimension(45, 28));

            pnlDateNav.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 0));
            pnlDateNav.add(btnPrevDay);
            pnlDateNav.add(spnDate);
            pnlDateNav.add(btnNextDay);

            btnPrevDay.addActionListener(e -> setSelectedDate(selectedDate.minusDays(1)));
            btnNextDay.addActionListener(e -> setSelectedDate(selectedDate.plusDays(1)));

            spnDate.addChangeListener(e -> {
                if (updatingSpinner) return;
                selectedDate = toLocalDate((Date) spnDate.getValue());
                refreshTableData();
                updateNavButtons();
            });

            updateNavButtons();
        }

        private void setSelectedDate(LocalDate date) {
            if (date.isBefore(minDate)) date = minDate;   // never go before account creation
            selectedDate = date;

            updatingSpinner = true;                        // avoid double refresh from the spinner listener
            spnDate.setValue(toDate(date));
            updatingSpinner = false;

            refreshTableData();
            updateNavButtons();
        }

        private void updateNavButtons() {
    btnPrevDay.setEnabled(selectedDate.isAfter(minDate));

    // Past days are a read-only audit trail (double-click inspection still works)
    boolean editable = !READ_ONLY_PAST_DAYS || !selectedDate.isBefore(LocalDate.now());
    btnMarkDone.setEnabled(editable);
    btnEditTask.setEnabled(editable);
    btnDeleteTask.setEnabled(editable);
}

        private LocalDate resolveAccountCreationDate() {
            // TODO: return the logged-in user's real account_creation_date
            return LocalDate.now().minusYears(1);   // placeholder
        }

        private Date toDate(LocalDate d) {
            return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        private LocalDate toLocalDate(Date d) {
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
        pnlDateNav = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlMain.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblMyTasks.setFont(new java.awt.Font("Georgia", 2, 36)); // NOI18N
        lblMyTasks.setText("My Tasks");

        txtSearch.setText("Search tasks...");

        btnAddTask.setBackground(new java.awt.Color(0, 102, 102));
        btnAddTask.setFont(new java.awt.Font("Segoe UI Emoji", 1, 18)); // NOI18N
        btnAddTask.setForeground(new java.awt.Color(255, 255, 255));
        btnAddTask.setText("+    Add Task");
        btnAddTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(102, 102, 102)));
        btnAddTask.setBorderPainted(false);
        btnAddTask.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnAddTask.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnEditTask.setFont(new java.awt.Font("Segoe UI Emoji", 3, 13)); // NOI18N
        btnEditTask.setForeground(new java.awt.Color(0, 102, 102));
        btnEditTask.setText("✎      Edit Task");
        btnEditTask.setAlignmentY(0.0F);
        btnEditTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEditTask.setBorderPainted(false);
        btnEditTask.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEditTask.addActionListener(this::btnEditTaskActionPerformed);

        btnMarkDone.setFont(new java.awt.Font("Segoe UI Emoji", 3, 13)); // NOI18N
        btnMarkDone.setForeground(new java.awt.Color(124, 153, 134));
        btnMarkDone.setText("✓     Mark Done");
        btnMarkDone.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnMarkDone.setBorderPainted(false);
        btnMarkDone.addActionListener(this::btnMarkDoneActionPerformed);

        btnDeleteTask.setFont(new java.awt.Font("Segoe UI Emoji", 3, 13)); // NOI18N
        btnDeleteTask.setForeground(new java.awt.Color(139, 77, 77));
        btnDeleteTask.setText("🗑   Delete Task");
        btnDeleteTask.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnDeleteTask.setBorderPainted(false);

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
        lblTaskCount.setForeground(new java.awt.Color(153, 153, 153));
        lblTaskCount.setText("Showing 0 tasks");

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubtitle)
                    .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(pnlMainLayout.createSequentialGroup()
                            .addComponent(lblMyTasks)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollTasks, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 864, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlMainLayout.createSequentialGroup()
                            .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblTaskCount, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnMarkDone, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDeleteTask, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblMyTasks))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSubtitle)
                .addGap(18, 18, 18)
                .addComponent(scrollTasks, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTaskCount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnMarkDone, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDeleteTask, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
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

        pnlDateNav.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout pnlDateNavLayout = new javax.swing.GroupLayout(pnlDateNav);
        pnlDateNav.setLayout(pnlDateNavLayout);
        pnlDateNavLayout.setHorizontalGroup(
            pnlDateNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );
        pnlDateNavLayout.setVerticalGroup(
            pnlDateNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 41, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblMyTasks1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 372, Short.MAX_VALUE)
                .addComponent(pnlDateNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblMyTasks1)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(pnlDateNav, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
    private javax.swing.JPanel pnlDateNav;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JScrollPane scrollTasks;
    private javax.swing.JTable tblTasks;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
