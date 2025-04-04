import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String pdfFilePath = "C:\\Users\\kacpe\\Downloads\\1.pdf";  // Ścieżka do pliku PDF
        String wordFilePath = "output.docx"; // Ścieżka do pliku Word

        try {
            // Załaduj plik PDF
            File file = new File(pdfFilePath);
            PDDocument document = Loader.loadPDF(file);

            // Przygotuj obiekt do zapisu tekstu z PDF
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPages = document.getNumberOfPages();
            XWPFDocument wordDoc = new XWPFDocument();

            // Iteruj przez wszystkie strony w PDF
            for (int pageNum = 1; pageNum <= totalPages; pageNum++) {
                stripper.setStartPage(pageNum);
                stripper.setEndPage(pageNum);
                String pageText = stripper.getText(document);  // Wydobądź tekst z danej strony

                // Podziel tekst na linie
                String[] lines = pageText.split("\n");

                StringBuilder paragraphBuilder = new StringBuilder();
                for (String line : lines) {
                    // Jeśli linia jest pusta (pusty wiersz), traktujemy to jako nowy akapit
                    if (line.trim().isEmpty()) {
                        // Dodajemy akapit, jeśli nie jest pusty
                        if (paragraphBuilder.length() > 0) {
                            // Tworzymy nowy akapit w Wordzie
                            XWPFParagraph wordParagraph = wordDoc.createParagraph();
                            wordParagraph.setAlignment(ParagraphAlignment.LEFT);
                            XWPFRun run = wordParagraph.createRun();
                            run.setText(paragraphBuilder.toString().trim());

                            // Dodaj numer strony w tym samym run, ale pogrubiony
                            XWPFRun pageNumberRun = wordParagraph.createRun();
                            pageNumberRun.setText(", s. " + pageNum);
                            pageNumberRun.setBold(true);  // Pogrubienie numeru strony
                        }

                        // Zresetuj builder do nowego akapitu
                        paragraphBuilder.setLength(0);
                    } else {
                        // Dodaj linię do aktualnego akapitu
                        paragraphBuilder.append(line.trim()).append(" ");
                    }
                }

                // Dodaj ostatni akapit na stronie, jeśli istnieje
                if (paragraphBuilder.length() > 0) {
                    XWPFParagraph wordParagraph = wordDoc.createParagraph();
                    wordParagraph.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun run = wordParagraph.createRun();
                    run.setText(paragraphBuilder.toString().trim());

                    // Dodaj numer strony w tym samym run, ale pogrubiony
                    XWPFRun pageNumberRun = wordParagraph.createRun();
                    pageNumberRun.setText(", s. " + pageNum);
                    pageNumberRun.setBold(true);  // Pogrubienie numeru strony
                }
            }

            // Zapisz dokument Word
            FileOutputStream out = new FileOutputStream(wordFilePath);
            wordDoc.write(out);
            out.close();

            // Zamknij dokument PDF
            document.close();
            System.out.println("Przetwarzanie zakończone, plik 'output.docx' został zapisany.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
