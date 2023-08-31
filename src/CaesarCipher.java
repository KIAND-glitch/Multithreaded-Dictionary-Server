/*
 * Kian Dsouza - 1142463
 * Caesar Cipher
 * */

public class CaesarCipher {

    private static final int SHIFT = 3;

    public static String encrypt(String message) {
        StringBuilder encryptedMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                char encryptedChar = (char) (base + (c - base + SHIFT) % 26);
                encryptedMessage.append(encryptedChar);
            } else {
                encryptedMessage.append(c);
            }
        }
        return encryptedMessage.toString();
    }

    public static String decrypt(String encryptedMessage) {
        StringBuilder decryptedMessage = new StringBuilder();
        for (char c : encryptedMessage.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isLowerCase(c) ? 'a' : 'A';
                char decryptedChar = (char) (base + (c - base - SHIFT + 26) % 26);
                decryptedMessage.append(decryptedChar);
            } else {
                decryptedMessage.append(c);
            }
        }
        return decryptedMessage.toString();
    }
}
