package homework;

public class src {

    public static void main(String[] args) {
        long start = System.nanoTime();
      

        if (args.length != 3) {
            System.err.println("invalid number of arguments!");
            return;
        }

        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int k = Integer.parseInt(args[2]);

        System.out.println(reduce_interval(a, b, k));

        long finish = System.nanoTime();


        long difference = finish - start; 

        System.out.println("It took " + difference + " nanoseconds");
    }

    public static String reduce_interval(int a,int b,int k) {

        StringBuilder result = new StringBuilder();

        for (int current = a;current != b;current++) {

            if (reduce(current) == k) {
                result.append(current + " " ); 
            }
        }
        return result.toString();
    }

    public static int reduce(int number) {

        while (number > 9) { 
            int temp = 0;

            while (number != 0) {

                temp += Math.pow(number % 10, 2);
                number /= 10;

            }
            number = temp;
        }

        return number;

    }
}
