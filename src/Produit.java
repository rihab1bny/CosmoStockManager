public class Produit {
    int id;
    String nom;
    String description;
    double prix;
    int stock;

    public Produit(int id, String nom, String description, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nom: " + nom + ", Description: " + description + ", Prix: " + prix + ", Stock: " + stock;
    }

    public void afficheProduit() {
        System.out.println("---------------------------------");
        System.out.println("Id produit : " + this.id);
        System.out.println("Nom produit : " + this.nom);
        System.out.println("Description produit : " + this.description);
        System.out.println("Prix produit : " + this.prix);
        System.out.println("Stock produit : " + this.stock);
    }
}