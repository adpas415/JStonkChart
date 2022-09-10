package info.monitorenter.gui.chart.demos;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleCSV {

    public static String csv_pattern = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void importCsvFile(String filePath, Consumer<Map<String, String>> rowConsumer) {
        importCsvFile(filePath, "", rowConsumer);
    }

    private static String removeQuotes(String value) {
        //remove quotes from beginning or end of string
        return value.replaceAll("^\"+|\"+$", "");
    }

    public static void importCsvFile(String filePath, String headers_str, Consumer<Map<String, String>> rowConsumer) {

        Path input = Paths.get(filePath);

        if (input.toFile().canRead()) {

            try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"))) {

                LinkedList<String> headers = new LinkedList<>();

                if(!headers_str.isEmpty())
                    headers.addAll(Arrays.asList(headers_str.split(csv_pattern)));

                int lineNum = 0;
                String line;
                while ((line = reader.readLine()) != null) {

                    try {

                        lineNum++;
                        String[] contents = line.split(csv_pattern);

                        //consume first row as headers
                        if(headers.isEmpty()) {

                            for(String header : contents)
                                headers.add(removeQuotes(header));

                        } else {

                            if(contents.length != headers.size())
                                throw new Exception("Column Count Mismatch on Line #"+lineNum);

                            Map<String, String> row = new LinkedHashMap<>();
                            for(int column = 0; column < headers.size(); column++)
                                row.put(headers.get(column), removeQuotes(contents[column]));

                            rowConsumer.accept(row);

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            } catch (Exception ex) {
                System.out.println(" > Failed to import file: " + input.toAbsolutePath() + "\n" + ex.getMessage());
            }

        }
    }
}
