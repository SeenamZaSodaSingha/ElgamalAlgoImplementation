public class PaddingAlgorithm {
    public static void main(String[] args) {
        int blockSize = 8;
        String message = "Hello";

        String paddedMessage = applyPadding(message, blockSize);
        System.out.println("Padded message: " + paddedMessage);

        String unpaddedMessage = removePadding(paddedMessage, blockSize);
        System.out.println("Unpadded message: " + unpaddedMessage);
    }

    public static String applyPadding(String message, int blockSize) {
        int paddingLength = blockSize - (message.length() % blockSize);
        String padding = String.valueOf(paddingLength);

        StringBuilder paddedMessageBuilder = new StringBuilder(message);
        for (int i = 0; i < paddingLength; i++) {
            paddedMessageBuilder.append(padding);
        }

        return paddedMessageBuilder.toString();
    }

    public static String removePadding(String paddedMessage, int blockSize) {
        String padded = paddedMessage;
        int paddingLength = Integer.parseInt(paddedMessage.substring(paddedMessage.length() - 1));
        System.out.println("Padding length: " + paddingLength);

        int unpaddedMessageLength = paddedMessage.length() - paddingLength;
        System.out.println("Unpadded message length: " + unpaddedMessageLength);
        if (unpaddedMessageLength < 0 || unpaddedMessageLength % blockSize == 0) {
            // Invalid padding or padding does not align with the block size
            System.out.println("IN IF");
            return paddedMessage;
        }
        System.out.println("Padded message: " + padded.substring(0, unpaddedMessageLength));
        String unpaddedMessage = paddedMessage.substring(0, unpaddedMessageLength);
        System.out.println("Unpadded message: " + unpaddedMessage);
        return unpaddedMessage;
    }
}
