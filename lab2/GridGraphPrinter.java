public class GridGraphPrinter {

    public static void main(String[] args) {
        int rows = 5;
        int cols = 10;
        printGridGraph(rows, cols);
    }

    public static void printGridGraph(int rows, int cols) {
        // Print top border
        System.out.print("┌");
        for (int i = 0; i < cols - 1; i++) {
            System.out.print("─┬");
        }
        System.out.println("─┐");

        // Print rows with vertical lines
        for (int i = 0; i < rows; i++) {
            if (i > 0) {
                // Print separator row
                System.out.print("├");
                for (int j = 0; j < cols - 1; j++) {
                    System.out.print("─┼");
                }
                System.out.println("─┤");
            }

            // Print row with cell contents
            for (int j = 0; j < cols; j++) {
                if (j > 0) {
                    System.out.print("│");
                }
                System.out.print("  "); // Placeholder for cell content
            }
            System.out.println();
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < cols - 1; i++) {
            System.out.print("─┴");
        }
        System.out.println("─┘");
    }
}
