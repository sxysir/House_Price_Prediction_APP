

import java.text.DecimalFormat;

public class NumberToWordsConverter {

    private static final String[] units = {
            "", "Thousand", "Million", "Billion", "Trillion"
    };

    private static final String[] belowTwenty = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static String convertBelowThousand(double number) {
        int num = (int) number;
        StringBuilder result = new StringBuilder();

        if (num >= 100) {
            result.append(belowTwenty[num / 100]).append(" Hundred ");
            num %= 100;
        }

        if (num >= 20) {
            result.append(tens[num / 10]).append(" ");
            num %= 10;
        }

        if (num > 0) {
            result.append(belowTwenty[num]).append(" ");
        }

        return result.toString().trim();
    }

    public static String convertToWords(double number) {
        if (number == 0) {
            return "Zero";
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#########");
        String formattedNumber = decimalFormat.format(number);

        String[] parts = formattedNumber.split("\\.");
        int integerPart = Integer.parseInt(parts[0]);

        int index = 0;
        StringBuilder result = new StringBuilder();

        while (integerPart > 0) {
            int part = integerPart % 1000;
            if (part > 0) {
                result.insert(0, convertBelowThousand(part) + " " + units[index] + " ");
            }
            integerPart /= 1000;
            index++;
        }

        if (parts.length > 1) {
            result.append("Point ");
            String decimalPart = parts[1];
            for (char digit : decimalPart.toCharArray()) {
                result.append(belowTwenty[Character.getNumericValue(digit)]).append(" ");
            }
        }

        return result.toString().trim();
    }

    public static void main(String[] args) {
        double number = 1234567.89123456789;
        String words = convertToWords(number);
        System.out.println("Number: " + number);
        System.out.println("Words: " + words);
    }
}
