
public interface ProduitCRUD {
    void ajouterProduit(Produit produit);
    void modifierProduit(Produit produit);
    void supprimerProduit(int id);
    void listerProduits();
}
