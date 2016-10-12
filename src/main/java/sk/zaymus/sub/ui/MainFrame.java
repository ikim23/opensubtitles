package sk.zaymus.sub.ui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.zaymus.sub.opensubtitles.DownloadLimitReachedException;
import sk.zaymus.sub.opensubtitles.OSConfig;
import sk.zaymus.sub.opensubtitles.OSXmlRpcClient;
import sk.zaymus.sub.opensubtitles.Util;
import sk.zaymus.sub.pojo.Language;
import sk.zaymus.sub.pojo.Mapper;
import sk.zaymus.sub.pojo.Movie;
import sk.zaymus.sub.pojo.Subtitles;

/**
 * Main window of application.
 *
 * @author Mikulas Zaymus
 */
public class MainFrame extends javax.swing.JFrame implements OSXmlRpcClient.AsyncListener {

    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static final FileFilter filter = new FileFilter() {

        private static final String VIDEO = "video";

        @Override
        public boolean accept(File file) {
            if (file.isFile()) {
                String contentType = null;
                try {
                    contentType = Files.probeContentType(file.toPath());
                } catch (IOException ex) {
                    log.error("Filtering Files: Files.probeContentType()", ex);
                }
                return contentType != null && contentType.startsWith(VIDEO);
            }
            return true;
        }

        @Override
        public String getDescription() {
            return "Video File";
        }
    };
    private final OSConfig config = OSConfig.getConfig();
    private OSXmlRpcClient client;
    private SubtitleTableModel subTableModel;
    private int settingsTabIdx;

    /**
     * Main window of application.
     */
    public MainFrame() {
        initComponents();
        table.getColumnModel().getColumn(4).setCellRenderer(new DateTableCellRender());
        int[] width = {80, 85, 60, 85, 70, 50};
        int colWidthSum = 0;
        for (int w : width) {
            colWidthSum += w;
        }
        Enumeration<TableColumn> cols = table.getColumnModel().getColumns();
        // maximize first column
        cols.nextElement().setPreferredWidth(table.getWidth() - colWidthSum);
        TableColumn col;
        for (int i = 0; i < width.length && cols.hasMoreElements(); i++) {
            col = cols.nextElement();
            col.setMinWidth(width[i]);
            col.setPreferredWidth(width[i]);
        }
        try {
            client = new OSXmlRpcClient(config);
            client.setListener(this);
        } catch (MalformedURLException ex) {
            log.error("Bad Server URL", ex);
        }
    }

    @Override
    public void onSearchResponse(List<Subtitles> subs) {
        SwingUtilities.invokeLater(() -> {
            subTableModel.resetData(subs);
            txtSubCount.setText(Integer.toString(subs.size()));
        });
    }

    @Override
    public void onSearchException(Exception ex) {
        SwingUtilities.invokeLater(() -> {
            log.error("SearchSubtiltes Request", ex);
        });
    }

    @Override
    public void onDownloadResponse(File dest) {
        SwingUtilities.invokeLater(() -> {
            if (JOptionPane.showConfirmDialog(MainFrame.this, dest.getName() + " is downloaded.\nWould you like to open containing folder?", "Subtitles Downloaded", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(dest.getParentFile());
                } catch (IOException ex) {
                    log.error("Opening Folder", ex);
                }
            }
        });
    }

    @Override
    public void onDownloadException(Exception ex) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage(), ex.getClass().getSimpleName(), ex instanceof DownloadLimitReachedException ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
            log.error("DownloadSubtiltes Request", ex);
        });
    }

    @Override
    public void onGetSubLanguagesResponse(List<Language> langs) {
        SwingUtilities.invokeLater(() -> {
            SettingsPanel settings = new SettingsPanel(settingsTabIdx, langs);
            if (JOptionPane.showConfirmDialog(MainFrame.this, settings, "Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                settings.saveChanges();
                txtLanguage.setText(config.getSubLanguageIDs());
            }
        });
    }

    @Override
    public void onGetSubLanguagesException(Exception ex) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(MainFrame.this, ex.getMessage() + "\nYour computer is offline. Try to reconnect.", ex.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
            log.error("GetSubLanguages Request", ex);
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

        tableScrollPane = new javax.swing.JScrollPane();
        subTableModel = new SubtitleTableModel();
        table = new javax.swing.JTable(subTableModel);
        searchPanel = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblSeason = new javax.swing.JLabel();
        spinSeason = new javax.swing.JSpinner();
        lblEpisode = new javax.swing.JLabel();
        spinEpisode = new javax.swing.JSpinner();
        btnSearch = new javax.swing.JButton();
        lblFileName = new javax.swing.JLabel();
        btnChooseFile = new javax.swing.JButton();
        txtLanguage = new javax.swing.JTextField(config.getSubLanguageIDs());
        lblLanguage = new javax.swing.JLabel();
        txtFileName = new javax.swing.JTextField();
        btnClearFile = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        lblSubCountTitle = new javax.swing.JLabel();
        txtSubCount = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuChooseFile = new javax.swing.JMenuItem();
        menuExit = new javax.swing.JMenuItem();
        menuSettings = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("OpenSubtitles Downloader");

        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true);
        table.getColumn(table.getColumnName(table.getColumnCount() - 1)).setCellRenderer((JTable jt, Object value, boolean isSelected, boolean hasFocus, int r, int c) -> {
            return (JButton) value;
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        tableScrollPane.setViewportView(table);

        getContentPane().add(tableScrollPane, java.awt.BorderLayout.CENTER);

        lblName.setText("Name:");

        txtName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameActionPerformed(evt);
            }
        });

        lblSeason.setText("Season:");

        spinSeason.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        lblEpisode.setText("Episode:");

        spinEpisode.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblFileName.setText("Movie File:");

        btnChooseFile.setText("Choose File");
        btnChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFileActionPerformed(evt);
            }
        });

        txtLanguage.setEditable(false);
        txtLanguage.setMaximumSize(new java.awt.Dimension(6, 20));
        txtLanguage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtLanguageMouseClicked(evt);
            }
        });

        lblLanguage.setText("Language:");

        txtFileName.setEditable(false);
        txtFileName.setBackground(null);
        txtFileName.setBorder(null);

        btnClearFile.setText("x");
        btnClearFile.setEnabled(false);
        btnClearFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout searchPanelLayout = new javax.swing.GroupLayout(searchPanel);
        searchPanel.setLayout(searchPanelLayout);
        searchPanelLayout.setHorizontalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addComponent(lblName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblSeason)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinSeason, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblEpisode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinEpisode, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblLanguage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnChooseFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch))
                    .addGroup(searchPanelLayout.createSequentialGroup()
                        .addComponent(lblFileName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFileName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClearFile)))
                .addContainerGap())
        );
        searchPanelLayout.setVerticalGroup(
            searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSeason)
                    .addComponent(spinSeason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEpisode)
                    .addComponent(spinEpisode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLanguage)
                    .addComponent(txtLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChooseFile)
                    .addComponent(btnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(searchPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFileName)
                    .addComponent(btnClearFile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(searchPanel, java.awt.BorderLayout.NORTH);

        lblSubCountTitle.setText("Subtitles:");

        txtSubCount.setEditable(false);
        txtSubCount.setBackground(null);
        txtSubCount.setBorder(null);

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSubCountTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSubCount, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(634, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSubCountTitle)
                    .addComponent(txtSubCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(statusPanel, java.awt.BorderLayout.SOUTH);

        menuFile.setText("File");

        menuChooseFile.setText("Choose File...");
        menuChooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuChooseFileActionPerformed(evt);
            }
        });
        menuFile.add(menuChooseFile);

        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        menuBar.add(menuFile);

        menuSettings.setText("Settings");
        menuSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuSettingsMouseClicked(evt);
            }
        });
        menuBar.add(menuSettings);

        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void searchSubtitles() {
        Movie movie = null;
        String filePath = txtFileName.getText();
        if (filePath.length() > 0) {
            try {
                movie = Util.getMovie(filePath);
            } catch (IOException ex) {
                log.error("Creating Movie Hash", ex);
            }
        } else {
            String movieName = txtName.getText();
            if (movieName.length() > 0) {
                movie = new Movie();
                movie.setQuery(movieName);
                int i = (int) spinSeason.getValue();
                if (i > 0) {
                    movie.setSeason(i);
                }
                i = (int) spinEpisode.getValue();
                if (i > 0) {
                    movie.setEpisode(i);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You have to specify movie name or file.", "Search Requirements", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        if (movie != null) {
            movie.setSublanguageid(txtLanguage.getText());
            client.asyncSearchSubtitles(movie);
            log.info("SearchSubtitles: {}", Mapper.toString(movie));
        }
    }

    private void chooseMovieFile() {
        JFileChooser chooser = new JFileChooser();
        String filePath = txtFileName.getText();
        if (filePath.length() > 0) {
            chooser.setCurrentDirectory(new File(filePath));
        }
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtFileName.setText(chooser.getSelectedFile().getAbsolutePath());
            btnClearFile.setEnabled(true);
        }
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchSubtitles();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameActionPerformed
        searchSubtitles();
    }//GEN-LAST:event_txtNameActionPerformed

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        int col = table.getColumnModel().getColumnIndexAtX(evt.getX());
        int row = evt.getY() / table.getRowHeight();
        if (row < table.getRowCount() && row >= 0 && col < table.getColumnCount() && col >= 0) {
            if (table.getValueAt(row, col) instanceof JButton) {
                Subtitles sub = subTableModel.getSubtitles(row);
                JFileChooser chooser = new JFileChooser();
                String subFilePath = txtFileName.getText();
                if (subFilePath.length() > 0) {
                    subFilePath = new File(subFilePath).getParent() + File.separator;
                }
                subFilePath += sub.getSubFileName();
                chooser.setSelectedFile(new File(subFilePath));
                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    client.asyncDownloadSubtitles(sub.getSubDownloadLink(), chooser.getSelectedFile());
                    log.info("DownloadSubtitles: {}", Mapper.toString(sub));
                }
            }
        }
    }//GEN-LAST:event_tableMouseClicked

    private void btnChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFileActionPerformed
        chooseMovieFile();
    }//GEN-LAST:event_btnChooseFileActionPerformed

    private void menuSettingsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuSettingsMouseClicked
        settingsTabIdx = 0;
        client.asyncGetSubLanguages();
    }//GEN-LAST:event_menuSettingsMouseClicked

    private void txtLanguageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtLanguageMouseClicked
        settingsTabIdx = 1;
        client.asyncGetSubLanguages();
    }//GEN-LAST:event_txtLanguageMouseClicked

    private void menuChooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuChooseFileActionPerformed
        chooseMovieFile();
    }//GEN-LAST:event_menuChooseFileActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuExitActionPerformed

    private void btnClearFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFileActionPerformed
        txtFileName.setText(null);
        btnClearFile.setEnabled(false);
    }//GEN-LAST:event_btnClearFileActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            log.error("Setting Look And Feel", ex);
        }
        final MainFrame frame = new MainFrame();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                frame.client.close();
            } catch (Exception ex) {
                log.error("Closing Client", ex);
            }
            OSConfig.saveChanges();
            log.info("Closing Application");
        }));
        java.awt.EventQueue.invokeLater(() -> {
            log.info("Starting Application");
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseFile;
    private javax.swing.JButton btnClearFile;
    private javax.swing.JButton btnSearch;
    private javax.swing.JLabel lblEpisode;
    private javax.swing.JLabel lblFileName;
    private javax.swing.JLabel lblLanguage;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSeason;
    private javax.swing.JLabel lblSubCountTitle;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuChooseFile;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuSettings;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JSpinner spinEpisode;
    private javax.swing.JSpinner spinSeason;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTextField txtFileName;
    private javax.swing.JTextField txtLanguage;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSubCount;
    // End of variables declaration//GEN-END:variables

}
