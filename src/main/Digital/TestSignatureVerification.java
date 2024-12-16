import Controller.ServletSignatureVerify;

public class TestSignatureVerification {
    public static void main(String[] args) {
        ServletSignatureVerify servletSignatureVerify = new ServletSignatureVerify();

        // Replace with your actual test order ID
        String testOrderId = "order123";

        try {
            boolean result = servletSignatureVerify.verifyOrderSignature(testOrderId);
            if (result) {
                System.out.println("Signature verification passed for order: " + testOrderId);
            } else {
                System.out.println("Signature verification failed for order: " + testOrderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
