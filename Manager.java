import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Manager extends JFrame {
    private final CompetitorList competitorList;
    private static final String COMPETITOR_FILE = "RunCompetitor.csv";
    private static final String REPORT_FILE = "Report.txt";

    private JTextArea outputArea;

    //GUI and main functions
    public Manager() {
        competitorList = new CompetitorList();
        outputArea = new JTextArea(20, 30);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        loadFileData();

        setTitle("Competition Management System");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        add(mainPanel, BorderLayout.NORTH);

        JLabel headingLabel = new JLabel("Competition Management System");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headingLabel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(11, 1));
        mainPanel.add(inputPanel);

        JButton registerButton = new JButton("Register Competitor");
        registerButton.addActionListener(new RegisterButtonListener());
        inputPanel.add(registerButton);

        JButton modifyButton = new JButton("Competitor Modification");
        modifyButton.addActionListener(new ModifyButtonListener());
        inputPanel.add(modifyButton);

        JButton removeButton = new JButton("Delete Competitor");
        removeButton.addActionListener(new RemoveButtonListener());
        inputPanel.add(removeButton);

        JButton awardButton = new JButton("Award Scores");
        awardButton.addActionListener(new awardButtonListener());
        inputPanel.add(awardButton);

        JButton viewAllButton = new JButton("View All Competitors");
        viewAllButton.addActionListener(new ViewAllButtonListener());
        inputPanel.add(viewAllButton);

        JButton fullDetailsButton = new JButton("Full Details");
        fullDetailsButton.addActionListener(new ViewDetailsButtonListener("FULL"));
        inputPanel.add(fullDetailsButton);

        JButton shortDetailsButton = new JButton("Short Details");
        shortDetailsButton.addActionListener(new ViewDetailsButtonListener("SHORT"));
        inputPanel.add(shortDetailsButton);

        JButton reportButton = new JButton("Display Report");
        reportButton.addActionListener(new ShowReportButtonListener());
        inputPanel.add(reportButton);

        JButton saveButton = new JButton("Save Data");
        saveButton.addActionListener(new SaveDataButtonListener());
        inputPanel.add(saveButton);

        JButton saveReportButton = new JButton("Save Report");
        saveReportButton.addActionListener(new SaveReportButtonListener());
        inputPanel.add(saveReportButton);

        outputArea = new JTextArea(30, 30);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        loadFileData();
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    // FIle handling

    // load data
    private void loadFileData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COMPETITOR_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Competitor competitor = parseCompetitor(line);
                if (competitor != null) {
                    competitorList.register_competitor(competitor);
                }
            }
            outputArea.append("\nCompetitor Data loaded successfully!\n");
        } catch (IOException e) {
            outputArea.append("Error loading competitor data from file: " + e.getMessage() + "\n");
        }
    }

    private Competitor parseCompetitor(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 7) {
            int competitor_number = Integer.parseInt(parts[0]);
            String name = parts[1];
            String email = parts[2];
            String country = parts[3];
            String dob = parts[4];
            String category = parts[5];
            String level = parts[6];

            Competitor competitor = new Competitor(competitor_number, name, email, country, dob, category, level);

            for (int i = 7; i < parts.length; i++) {
                competitor.addScore(Integer.parseInt(parts[i]));
            }

            return competitor;
        } else {
            outputArea.append("Invalid data format in competitor file: " + line + "\n");
            return null;
        }
    }

    // save data
    private void saveCompetitorData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMPETITOR_FILE))) {
            for (Competitor competitor : competitorList.getAllCompetitors()) {
                writer.write(competitorToFileString(competitor));
                writer.newLine();
            }
            outputArea.append("\nCompetitor data saved successfully!\n");
        } catch (IOException e) {
            outputArea.append("Error saving competitor data to file: " + e.getMessage() + "\n");
        }
    }

    // conversion
    private String competitorToFileString(Competitor competitor) {
        StringBuilder sb = new StringBuilder();
        sb.append(competitor.getCompetitorNumber()).append(",");
        sb.append(competitor.getCompetitorName()).append(",");
        sb.append(competitor.getEmail()).append(",");
        sb.append(competitor.getCountry()).append(",");
        sb.append(competitor.getCompetitorDob()).append(",");
        sb.append(competitor.getCategory()).append(",");
        sb.append(competitor.getLevel());

        for (int score : competitor.getScores()) {
            sb.append(",").append(score);
        }

        return sb.toString();
    }

    // save data
    private void saveReportData() {
        String report = getReportString();
        try (PrintWriter writer = new PrintWriter(new FileWriter(REPORT_FILE))) {
            writer.println(report);
            outputArea.append("\nSuccess! Report data saved.\n");
        } catch (IOException e) {
            outputArea.append("Error saving report data to file: " + e.getMessage() + "\n");
        }
    }

    // report data
    private String getReportString() {
        StringBuilder report = new StringBuilder();

        report.append("\n***** Competition Report *****\n\n");
        report.append(getCompetitorTable());

        Competitor winner = competitorList.getWinner();
        report.append("\nCompetitor with Highest average score\n\n");
        report.append("****************************************\n\n");
        if (winner != null) {
            report.append(winner.getFullDetails()).append("\n");
        } else {
            report.append("No competitors found.\n");
        }
        report.append("****************************************\n\n");


        report.append("\n*** Summary Statistics ***\n");
        report.append("Total Score: ").append(competitorList.getOverallScore()).append("\n");
        report.append("Average Score: ").append(competitorList.getAverageScore()).append("\n");
        report.append("Highest Score: ").append(competitorList.getMaxScore()).append("\n");
        report.append("Lowest Score: ").append(competitorList.getMinScore()).append("\n");

        report.append("\n*** Frequency Report ***\n");
        Map<Integer, Integer> scoreFrequency = competitorList.getScoreFrequency();
        for (Map.Entry<Integer, Integer> entry : scoreFrequency.entrySet()) {
            report.append(String.format("Score %d: %d times%n", entry.getKey(), entry.getValue()));
        }
        return report.toString();
    }

    // competitor table
    private String getCompetitorTable() {
        StringBuilder output = new StringBuilder();

        output.append("\nCompetitor Table\n");
        output.append("**************************************************************\n");
        output.append(String.format("%-6s %-20s %-15s %-10s %-18s %n", "Number", "Name", "Level", "Scores", "Average"));
        output.append("--------------------------------------------------------------\n");
        for (Competitor competitor : competitorList.getAllCompetitors()) {
            output.append(competitor.toString()).append("\n");
        }
        output.append("**************************************************************\n\n");
        return output.toString();
    }

    // register competitor
    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField competitorNumberField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField countryField = new JTextField();
            JTextField dobField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField levelField = new JTextField();
            Object[] message = {
                    "Competitor No.", competitorNumberField,
                    "Competitor Name:", nameField,
                    "Email:", emailField,
                    "Country:", countryField,
                    "Date of Birth (yyyy-MM-dd):", dobField,
                    "Category:", categoryField,
                    "Level:", levelField
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Register Competitor", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int competitor_number = Integer.parseInt(competitorNumberField.getText());
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String country = countryField.getText();
                    String dob = dobField.getText();
                    String category = categoryField.getText();
                    String level = levelField.getText();

                    Competitor newCompetitor = new Competitor(competitor_number, name, email, country, dob, category, level);
                    boolean success = competitorList.register_competitor(newCompetitor);

                    if (success) {
                        outputArea.append("Success! Competitor registered.\n");
                    } else {
                        outputArea.append("Error! Competitor exists with same email & category.\n");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error! Invalid input for competitor.\n");
                }
            }
        }
    }

    // modify competitor
    private class ModifyButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JTextField competitorNumberField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            JTextField countryField = new JTextField();
            JTextField dobField = new JTextField();
            JTextField categoryField = new JTextField();
            JTextField levelField = new JTextField();
            Object[] message = {
                    "Competitor No.", competitorNumberField,
                    "Competitor Name:", nameField,
                    "Email:", emailField,
                    "Country:", countryField,
                    "Date of Birth (yyyy-MM-dd):", dobField,
                    "Category:", categoryField,
                    "Level:", levelField
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Modify Competitor", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    int competitor_number = Integer.parseInt(competitorNumberField.getText());
                    String name = nameField.getText();
                    String email = emailField.getText();
                    String country = countryField.getText();
                    String dob = dobField.getText();
                    String category = categoryField.getText();
                    String level = levelField.getText();
                    Competitor updatedCompetitor = new Competitor(competitor_number, name, email, country, dob, category, level);
                    boolean success = competitorList.update_details(competitor_number, updatedCompetitor);

                    if (success) {
                        outputArea.append("Success! Competitor details updated.\n");
                    } else {
                        outputArea.append("Error in updating competitor details.\n");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error! Invalid input for competitor number.\n");
                }
            }
        }
    }

    // remove competitor
    private class RemoveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String competitorNumberStr = JOptionPane.showInputDialog("Enter Competitor Number:");
            if (competitorNumberStr != null) {
                try {
                    int competitor_number = Integer.parseInt(competitorNumberStr);
                    boolean success = competitorList.remove(competitor_number);

                    if (success) {
                        outputArea.append("Success! Competitor Removed.\n");
                    } else {
                        outputArea.append("Error in removing competitor.\n");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error! Invalid input for competitor number.\n");
                }
            }
        }
    }

    // award scores
    private class awardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String competitorNumberStr = JOptionPane.showInputDialog("Enter Competitor Number:");
            if (competitorNumberStr != null) {
                try {
                    int competitor_number = Integer.parseInt(competitorNumberStr);
                    Competitor competitor = competitorList.getCompetitorByNumber(competitor_number);
                    if (competitor != null) {
                        List<Integer> scores = new ArrayList<>();
                        for (int i = 0; i < 5; i++) {
                            String scoreInput = JOptionPane.showInputDialog("Enter score " + (i + 1) + ":");
                            int score = Integer.parseInt(scoreInput);
                            if (score < 0 || score > 5) {
                                JOptionPane.showMessageDialog(null, "Invalid score. Please award a score between 0 and 5.");
                                return;
                            }
                            scores.add(score);
                        }
                        competitor.setScores(scores);
                        outputArea.append("Success! Scores awarded.\n");
                    } else {
                        outputArea.append("Error! Competitor not found.\n");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error! Invalid input for competitor number.\n");
                }
            }
        }
    }

    // show all competitors
    private class ViewAllButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.setText(getCompetitorTable());
        }
    }

    // full details / short details
    private class ViewDetailsButtonListener implements ActionListener {
        private String viewType;

        public ViewDetailsButtonListener(String viewType) {
            this.viewType = viewType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String competitorNumberStr = JOptionPane.showInputDialog("Enter Competitor Number:");
            if (competitorNumberStr != null) {
                try {
                    int competitor_number = Integer.parseInt(competitorNumberStr);
                    Competitor competitor = competitorList.getCompetitorByNumber(competitor_number);
                    if (competitor != null) {
                        if ("FULL".equalsIgnoreCase(viewType)) {
                            outputArea.setText(competitor.getFullDetails());
                        } else if ("SHORT".equalsIgnoreCase(viewType)) {
                            outputArea.setText(competitor.getShortDetails());
                        }
                    } else {
                        outputArea.append("Competitor not found.\n");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.append("Error: Invalid input for competitor number.\n");
                }
            }
        }
    }

    // show report data
    private class ShowReportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.setText(getReportString());
        }
    }

    // save data in csv file
    private class SaveDataButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveCompetitorData();
        }
    }

    // save data in report
    private class SaveReportButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveReportData();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Manager().setVisible(true));
    }
}