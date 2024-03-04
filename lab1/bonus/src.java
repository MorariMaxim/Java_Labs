package bonus;

public class src {

    public static void main(String[] args) {

        matrix(10);

        cycles(10);
    }

    
    public static void matrix(int N) {

        // N -1 din cel mai mare ciclu
        int n = N - 1;
        char[][] matrix = new char[N][N];

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                matrix[row][col] = '0';
            }
        }
        // pentru nodurile din cel mai mare ciclu
        for (int node = 0; node < n; node++) {

            matrix[node][node] = '1';
            matrix[node][(node + 1) % n] = '1';
            matrix[node][(node - 1 + n) % n] = '1';
            matrix[node][n] = '1';
        }

        // ultimul nod
        for (int node = 0; node < N; node++) {
            matrix[n][node] = '1';
        }

        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {

                System.out.print(matrix[row][col] + " ");
            }
            System.out.println("");
        }
    }

    public static void cycles(int N) {

        int n = N - 1;

        int cyclesNum = 0;

        for (int starNode = 0; starNode < n; starNode++) {

            for (int lastNode = 1; lastNode < n; lastNode++) {

                for (int offset = 0; offset <= lastNode; offset++) {
                    System.out.print(((starNode + offset) % n) + " ");

                }
                System.out.println(n + " " + starNode);
                cyclesNum++;
            }

        }

        for(int firstNode = 0; firstNode < n; firstNode++){
            System.out.print(firstNode + " ");

        }
        cyclesNum++;
        System.out.println("0\nTotal number of found cycles = " + cyclesNum);    

        double expected = Math.pow(N, 2) - 3*N+3;
        System.out.println("- Number of cycles is equal to n^2 -3n + 3?\n- " + (expected == cyclesNum ? "Yes" : "No")  );
    }
}