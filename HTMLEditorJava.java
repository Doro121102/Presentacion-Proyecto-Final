import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class HTMLTextEditor extends JFrame {
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JLabel lineLabel;
    private JFileChooser fileChooser;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem, openMenuItem, saveMenuItem, saveAsMenuItem, printMenuItem;
    private JMenu editMenu;
    private JMenuItem findMenuItem, replaceMenuItem, goToMenuItem;
    private List<String> reservedWords = Arrays.asList("html", "head", "title", "body", "div", "p", "span", "h1", "h2",
            "h3", "h4", "h5", "h6");

    public HTMLTextEditor() {
        setTitle("Editor de Texto HTML");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        lineLabel = new JLabel();

        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                updateLineLabel();
                highlightReservedWords();
            }
        });

        fileChooser = new JFileChooser();

        newMenuItem = new JMenuItem("Nuevo");
        newMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newDocument();
            }
        });

        openMenuItem = new JMenuItem("Abrir");
        openMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDocument();
            }
        });

        saveMenuItem = new JMenuItem("Guardar");
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDocument();
            }
        });

        saveAsMenuItem = new JMenuItem("Guardar Como");
        saveAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDocumentAs();
            }
        });

        printMenuItem = new JMenuItem("Imprimir");
        printMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                printDocument();
            }
        });

        editMenu = new JMenu("Editar");

        findMenuItem = new JMenuItem("Buscar");
        findMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findText();
            }
        });

        replaceMenuItem = new JMenuItem("Reemplazar");
        replaceMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceText();
            }
        });

        goToMenuItem = new JMenuItem("Ir a");
        goToMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goToLine();
            }
        });

        fileMenu = new JMenu("Archivo");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(printMenuItem);

        editMenu.add(findMenuItem);
        editMenu.add(replaceMenuItem);
        editMenu.add(goToMenuItem);

        menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(lineLabel, BorderLayout.SOUTH);

        setJMenuBar(menuBar);
        setVisible(true);
    }

    private void updateLineLabel() {
        int totalLines = textArea.getLineCount();
        lineLabel.setText("Líneas: " + totalLines);
    }

    private void highlightReservedWords() {
        String text = textArea.getText();
        StyledDocument doc = textArea.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style reservedWordStyle = doc.addStyle("ReservedWordStyle", defaultStyle);

        StyleConstants.setForeground(reservedWordStyle, Color.BLUE);

        for (String word : reservedWords) {
            int index = text.indexOf(word);
            while (index >= 0) {
                try {
                    doc.setCharacterAttributes(index, word.length(), reservedWordStyle, true);
                    index = text.indexOf(word, index + word.length());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void newDocument() {
        textArea.setText("");
    }

    private void openDocument() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                textArea.setText(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDocument() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveDocumentAs() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printDocument() {
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    private void findText() {
        String searchText = JOptionPane.showInputDialog(this, "Buscar texto:");
        if (searchText != null && !searchText.isEmpty()) {
            String text = textArea.getText();
            int index = text.indexOf(searchText);
            if (index >= 0) {
                textArea.setSelectionStart(index);
                textArea.setSelectionEnd(index + searchText.length());
            } else {
                JOptionPane.showMessageDialog(this, "Texto no encontrado.");
            }
        }
    }

    private void replaceText() {
        String searchText = JOptionPane.showInputDialog(this, "Buscar texto:");
        if (searchText != null && !searchText.isEmpty()) {
            String replaceText = JOptionPane.showInputDialog(this, "Reemplazar por:");
            if (replaceText != null) {
                String text = textArea.getText();
                String replacedText = text.replace(searchText, replaceText);
                textArea.setText(replacedText);
            }
        }
    }

    private void goToLine() {
        String lineNumberStr = JOptionPane.showInputDialog(this, "Ir a línea:");
        if (lineNumberStr != null && !lineNumberStr.isEmpty()) {
            try {
                int lineNumber = Integer.parseInt(lineNumberStr);
                int offset = textArea.getLineStartOffset(lineNumber - 1);
                textArea.setCaretPosition(offset);
                textArea.requestFocus();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número de línea inválido.");
            } catch (BadLocationException e) {
                JOptionPane.showMessageDialog(this, "Número de línea fuera de rango.");
            }
        }
    }
