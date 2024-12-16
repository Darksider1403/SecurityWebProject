package DAO;

public class OrderDetailsSignature {
    private final int idProduct;
    private final int quantity;
    private final double price;
    private final String signature;
    private final String publicKey;

    public OrderDetailsSignature(int idProduct, int quantity, double price, String signature, String publicKey) {
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.price = price;
        this.signature = signature;
        this.publicKey = publicKey;
    }

    // Getters
    public int getIdProduct() { return idProduct; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getSignature() { return signature; }
    public String getPublicKey() { return publicKey; }
}

