import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DifferenceHashFunctions {
    private static String files_path = System.getProperty("user.dir") + "/files";
    private static String out_path = System.getProperty("user.dir") + "/diff.txt";
    private static String[] hash_functions = {"md5sum", "sha1sum", "sha224sum", "sha256sum", "sha384sum", "sha512sum"};

    public static void main(String[] args) {
        try {
            if (args.length > 0)
                System.out.print("You shouldn't provide any arguments, type: java DifferenceHashFunctions\n");

            ArrayList<String> hashList = readHashFile();
            if (hashList != null) {
                printArrayList(hashList);
                compareHashFunctions(hashList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> readHashFile() {
        Scanner scanner;
        try {
            File file = new File(files_path + "/hash.txt");
            boolean file_exists = file.exists();

            if (file_exists) {
                ArrayList<String> arrayList = new ArrayList<>();
                scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    arrayList.add(line.substring(0, line.length() - 3));
                }

                System.out.print("INFO: Loaded hash file\n");
                return arrayList;
            }

            return null;
        } catch (Exception e) {
            System.out.print("ERROR: something goes wrong, can't load the hash file\n");
        }

        return null;
    }

    private static void printArrayList(ArrayList<String> arrayList) {
        for (String s : arrayList) System.out.printf("%s\n", s);
    }

    private static void printStringArray(String bin_arr) {
        for (char c : bin_arr.toCharArray()) System.out.print(c);
        System.out.print("\n");
    }

    private static String hexToBinary(String hex) {
        String[] hex_arr = new String[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < hex_arr.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(hex.charAt(j++));
            stringBuilder.append(hex.charAt(j++));
            hex_arr[i] = stringBuilder.toString();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : hex_arr) {
            stringBuilder.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(s, 16)))
                    .replace(' ', '0'))
                    .append(' ');
        }


        printStringArray(stringBuilder.toString());
        return stringBuilder.toString();
    }

    private static String difference(String hash1, String hash2) {
        String hash_binary_1 = hexToBinary(hash1).replaceAll("\\s", "");
        String hash_binary_2 = hexToBinary(hash2).replaceAll("\\s", "");

        int qty_of_bits = hash_binary_1.length();
        int number_of_diff = 0;
        for (int i = 0; i < qty_of_bits; i++) if (hash_binary_1.charAt(i) != hash_binary_2.charAt(i)) number_of_diff++;
        int diff_percent = Math.round(((float) number_of_diff / (float) qty_of_bits) * 100);

        return "Liczba bitów różniąca wyniki: " + number_of_diff
                + " tj. " + diff_percent + "% z " + qty_of_bits + "\n";
    }

    private static void compareHashFunctions(ArrayList<String> hashList) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(out_path));
            writer.write("Wykonywane polecenia i wyniki:\n\n");

            int j = 0;
            for (String hash_function : hash_functions) {
                String hash1 = hashList.get(j++);
                String hash2 = hashList.get(j++);

                writer.write("cat hash.pdf personal.txt | " + hash_function + "\n");
                writer.write("cat hash.pdf personal_.txt | " + hash_function + "\n");
                writer.write(hash1 + "\n");
                writer.write(hash2 + "\n");
                writer.write(difference(hash1, hash2));
                if (!hash_function.equals(hash_functions[hash_functions.length - 1])) writer.write("\n");
            }

            writer.close();
            System.out.print("INFO: compared!\n");
        } catch (Exception e) {
            System.out.print("ERROR: something goes wrong, can't load the hash file\n");
        }
    }
}
