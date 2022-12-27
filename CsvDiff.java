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

    public static List<Difference> compare(String file1, String file2) throws IOException {
        List<Difference> differences = new ArrayList<>();

        try (BufferedReader reader1 = new BufferedReader(new FileReader(file1));
             BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();
            int row = 1;

            while (line1 != null && line2 != null) {
                String[] values1 = line1.split(",");
                String[] values2 = line2.split(",");

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

                line1 = reader1.readLine();
                line2 = reader2.readLine();
                row++;
            }
        }

        return differences;
    }

    public static void main(String[] args) throws IOException {
        List<Difference> differences = compare("file1.csv", "file2.csv");
        for (Difference diff : differences) {
            System.out.println("Difference at row " + diff.row + ", col " + diff.col + ": " + diff.val1 + " vs " + diff.val2);
        }
    }
}
