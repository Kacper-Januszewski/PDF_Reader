import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class Updater {

    private static final String CURRENT_VERSION = "0.1.0"; // zmień ręcznie przy nowej wersji
    private static final String VERSION_URL = "https://raw.githubusercontent.com/twoj-user/moja-aplikacja/main/version.txt";
    private static final String DOWNLOAD_URL = "https://github.com/Kacper-Januszewski/PDF_Reader/releases/download/latest/app-latest.jar";

    public static boolean checkForUpdateAndRun() {
        try {
            // Pobierz najnowszą wersję z GitHuba
            String latestVersion = new BufferedReader(new InputStreamReader(new URL(VERSION_URL).openStream())).readLine();

            if (!CURRENT_VERSION.equals(latestVersion.trim())) {
                System.out.println("Nowa wersja dostępna: " + latestVersion);

                // Ścieżka gdzie zapisujemy nowy .jar
                Path newJar = Paths.get("app-new.jar");

                try (InputStream in = new URL(DOWNLOAD_URL).openStream()) {
                    Files.copy(in, newJar, StandardCopyOption.REPLACE_EXISTING);
                }

                // Uruchom nową wersję
                ProcessBuilder pb = new ProcessBuilder("java", "-jar", newJar.toString());
                pb.start();

                return true; // kończymy starą wersję
            } else {
                System.out.println("Masz najnowszą wersję.");
            }

        } catch (IOException e) {
            System.out.println("Błąd przy sprawdzaniu aktualizacji: " + e.getMessage());
        }
        return false;
    }
}
