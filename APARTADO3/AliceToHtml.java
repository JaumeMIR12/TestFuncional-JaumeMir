import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliceToHtml {

    public static void main(String[] args) {
        String inputFile = "alice.txt";
        String outputFile = "alice.html";

        List<String> textLines = readFile(inputFile);
        int numLinesWithText = countNonEmptyLines(textLines);
        String longestWord = findLongestWord(textLines);
        List<Chapter> chapters = parseChapters(textLines);

        writeHtml(outputFile, chapters, numLinesWithText, longestWord);
    }

    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static int countNonEmptyLines(List<String> lines) {
        int count = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public static String findLongestWord(List<String> lines) {
        String longestWord = "";
        Pattern pattern = Pattern.compile("\\b\\w+\\b");
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String word = matcher.group();
                if (word.length() > longestWord.length()) {
                    longestWord = word;
                }
            }
        }
        return longestWord;
    }

    public static List<Chapter> parseChapters(List<String> lines) {
        List<Chapter> chapters = new ArrayList<>();
        String currentChapterTitle = null;
        List<String> currentChapterLines = new ArrayList<>();

        Pattern chapterPattern = Pattern.compile("^CHAPTER\\s+\\w+");

        for (String line : lines) {
            Matcher chapterMatcher = chapterPattern.matcher(line);
            if (chapterMatcher.find()) {
                if (currentChapterTitle != null) {
                    chapters.add(new Chapter(currentChapterTitle, currentChapterLines));
                    currentChapterLines = new ArrayList<>();
                }
                currentChapterTitle = line.trim();
            } else if (!line.trim().isEmpty()) {
                currentChapterLines.add(line.trim());
            }
        }

        if (currentChapterTitle != null) {
            chapters.add(new Chapter(currentChapterTitle, currentChapterLines));
        }

        return chapters;
    }

    public static void writeHtml(String filePath, List<Chapter> chapters, int numLines, String longestWord) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("<html>\n<head>\n<title>Parte tres</title>\n</head>\n<body>\n");

            // Writing the index
            writer.write("<h1>Index</h1>\n<ul>\n");
            for (Chapter chapter : chapters) {
                String chapterId = chapter.getTitle().replaceAll("\\s+", "_");
                writer.write(String.format("<li><a href=\"#%s\">%s</a></li>\n", chapterId, chapter.getTitle()));
            }
            writer.write("</ul>\n");

            // Writing the chapters
            for (Chapter chapter : chapters) {
                String chapterId = chapter.getTitle().replaceAll("\\s+", "_");
                writer.write(String.format("<h1 id=\"%s\">%s</h1>\n", chapterId, chapter.getTitle()));
                for (String line : chapter.getLines()) {
                    writer.write(String.format("<p>%s</p>\n", line));
                }
            }

            // Writing the statistics
            writer.write(String.format("<p>Numero de lineas: %d</p>\n", numLines));
            writer.write(String.format("<p>Palabra mas larga: %s</p>\n", longestWord));

            writer.write("</body>\n</html>\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Chapter {
    private String title;
    private List<String> lines;

    public Chapter(String title, List<String> lines) {
        this.title = title;
        this.lines = lines;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getLines() {
        return lines;
    }
}
