import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WordCountGUI extends JFrame {
    private JTextArea resultTextArea;

    public WordCountGUI() {
        setTitle("Word Count");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);

        JButton selectFolderButton = new JButton("Select Folder");
        selectFolderButton.addActionListener(e -> selectFolder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectFolderButton);

        JScrollPane scrollPane = new JScrollPane(resultTextArea);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void selectFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            if (folder.isDirectory()) {
                processFolder(folder);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid folder.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processFolder(File folder) {
        resultTextArea.setText("Processing folder: " + folder.getAbsolutePath() + "\n");
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                new Thread(() -> {
                    try {
                        int wordCount = countWords(file);
                        appendResult("File: " + file.getName() + ", Word Count: " + wordCount + "\n");
                    } catch (IOException e) {
                        appendResult("Error reading file: " + file.getName() + "\n");
                    }
                }).start();
            }
        }
    }

    private int countWords(File file) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.trim().split("\\s+");
                count += words.length;
            }
        }
        return count;
    }

    private void appendResult(String text) {
        SwingUtilities.invokeLater(() -> resultTextArea.append(text));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WordCountGUI gui = new WordCountGUI();
            gui.setVisible(true);
        });
    }
}
