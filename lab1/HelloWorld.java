public class HelloWorld {
    public static void main(String args[]) {
        System.out.println("Hello World!");

        String[] languages = { "C", "C++", "C#", "Python", "Go", "Rust", "JavaScript", "PHP", "Swift", "Java" };

        int n = (int) (Math.random() * 1_000_000);

        n *= 3;

        n += 0b10101;
        n += 0xFF;
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