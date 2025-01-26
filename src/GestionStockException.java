public class GestionStockException extends Exception {
    // Constructeur par défaut
    public GestionStockException() {
        super();
    }

    // Constructeur avec un message d'erreur
    public GestionStockException(String message) {
        super(message);
    }

    // Constructeur avec un message d'erreur et une cause
    public GestionStockException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructeur avec une cause
    public GestionStockException(Throwable cause) {
        super(cause);
    }
}