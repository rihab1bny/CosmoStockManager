public interface Authentifiable {
    boolean authentifierAdmin(String email, String motDePasse)throws GestionStockException ;
    boolean authentifierClient(String email, String motDePasse)throws GestionStockException;
    void inscription(Utilisateur u)throws GestionStockException;
}
