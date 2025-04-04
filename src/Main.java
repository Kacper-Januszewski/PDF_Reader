import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class Main {
    private JFrame frame;
    private JLabel fileLabel;
    private File selectedFile;

    public static void main(String[] args) {
        try {
            // Ustaw natywny wygląd (np. Windows Look & Feel)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("PDF ➜ Word Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new FlowLayout());

        JButton chooseButton = new JButton("Wybierz plik PDF");
        JButton processButton = new JButton("Procesuj");
        fileLabel = new JLabel("Brak wybranego pliku.");

        chooseButton.addActionListener(this::onChooseFile);
        processButton.addActionListener(this::onProcessFile);

        frame.add(chooseButton);
        frame.add(processButton);
        frame.add(fileLabel);

        frame.setVisible(true);
    }

    private void onChooseFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileLabel.setText("Wybrano: " + selectedFile.getName());
        }
    }

    private void onProcessFile(ActionEvent e) {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(frame, "Najpierw wybierz plik PDF.");
            return;
        }

        try {
            PDDocument document = Loader.loadPDF(selectedFile);
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();
            XWPFDocument wordDoc = new XWPFDocument();

            for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                stripper.setStartPage(pageNum);
                stripper.setEndPage(pageNum);
                String pageText = stripper.getText(document);
                String[] lines = pageText.split("\n");

                StringBuilder paragraphBuilder = new StringBuilder();
                for (String line : lines) {
                    if (line.trim().isEmpty()) {
                        if (paragraphBuilder.length() > 0) {
                            XWPFParagraph paragraph = wordDoc.createParagraph();
                            paragraph.setAlignment(ParagraphAlignment.LEFT);

                            XWPFRun run = paragraph.createRun();
                            run.setText(paragraphBuilder.toString().trim());

                            XWPFRun bold = paragraph.createRun();
                            bold.setBold(true);
                            bold.setText(", s. " + pageNum);

                            paragraphBuilder.setLength(0);
                        }
                    } else {
                        paragraphBuilder.append(line.trim()).append(" ");
                    }
                }

                if (paragraphBuilder.length() > 0) {
                    XWPFParagraph paragraph = wordDoc.createParagraph();
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText(paragraphBuilder.toString().trim());

                    XWPFRun bold = paragraph.createRun();
                    bold.setBold(true);
                    bold.setText(", s. " + pageNum);
                }
            }

            String outputPath = selectedFile.getParent() + File.separator + "output.docx";
            FileOutputStream out = new FileOutputStream(outputPath);
            wordDoc.write(out);
            out.close();
            document.close();

            JOptionPane.showMessageDialog(frame, "Zapisano jako output.docx!");

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Wystąpił błąd przy przetwarzaniu.");
        }
    }
}
