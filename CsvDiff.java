import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDiff {

    public static class Difference {
        public final int row;
        public final int col;
        public final String val1;
        public final String val2;

        public Difference(int row, int col, String val1, String val2) {
            this.row = row;
            this.col = col;
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    public static List<Difference> compare(String file1, String file2, int sortColumn) throws IOException {
        List<Difference> differences = new ArrayList<>();

        // Read both files into lists of rows
        List<String[]> rows1 = readRows(file1);
        List<String[]> rows2 = readRows(file2);

        // Sort the rows based on the specified column
        rows1.sort((r1, r2) -> r1[sortColumn].compareTo(r2[sortColumn]));
        rows2.sort((r1, r2) -> r1[sortColumn].compareTo(r2[sortColumn]));

        // Compare the rows
        int row = 1;
        int i1 = 0;
        int i2 = 0;
        while (i1 < rows1.size() || i2 < rows2.size()) {
            if (i1 == rows1.size()) {
                differences.add(new Difference(row, 0, "", "Row not found in file1"));
                i2++;
            } else if (i2 == rows2.size()) {
                differences.add(new Difference(row, 0, "Row not found in file2", ""));
                i1++;
            } else {
                String[] values1 = rows1.get(i1);
                String[] values2 = rows2.get(i2);

                int col = 1;
                for (int i = 0; i < Math.min(values1.length, values2.length); i++) {
                    if (!values1[i].equals(values2[i])) {
                        differences.add(new Difference(row, col, values1[i], values2[i]));
                    }
                    col++;
                }

                if (values1.length != values2.length) {
                    int maxLength = Math.max(values1.length, values2.length);
                    String[] maxValues = values1.length > values2.length ? values1 : values2;
                    for (int i = Math.min(values1.length, values2.length); i < maxLength; i++) {
                        differences.add(new Difference(row, col, "", maxValues[i]));
                        col++;
                    }
                }

                i1++;
                i2++;
            }
            row++;
        }

        return differences;
    }
    
    private static List<String[]> readRows(String file) throws IOException {
    List<String[]> rows = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line = reader.readLine();
        while (line != null) {
            rows.add(line.split(","));
            line = reader.readLine();
        }
    }
    return rows;
}


    public static void main(String[] args) throws IOException {
        List<Difference> differences = compare("file1.csv", "file2.csv");
        for (Difference diff : differences) {
            System.out.println("Difference at row " + diff.row + ", col " + diff.col + ": " + diff.val1 + " vs " + diff.val2);
        }
    }
}
