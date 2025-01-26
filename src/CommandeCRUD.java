
public interface CommandeCRUD {
    void ajouterCommande(Commande commande);
    void modifierCommande(Commande commande);
    void supprimerCommande(int id);
    void listerCommandes();
}
