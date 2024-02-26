public class HelloWorld {
    public static void main(String args[]) {
        System.out.println("Hello World!");

        String[] languages = { "C", "C++", "C#", "Python", "Go", "Rust", "JavaScript", "PHP", "Swift", "Java" };

        int n = (int) (Math.random() * 1_000_000);

        n *= 3;

        n += Integer.parseInt("10101", 2); // Adding binary 10101
        n += Integer.parseInt("FF", 16); // Adding hexadecimal FF
        n *= 6;

        int sum = 10;

        while (n > 9) {
            sum = 0;
            while (n > 0) {
                sum += n % 10;
                n /= 10;
            }
            n = sum;
        }

        System.out.println("Willy-nilly, this semester I will learn " + languages[sum]);
    }
}