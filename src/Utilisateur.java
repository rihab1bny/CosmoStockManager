import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilisateur implements Authentifiable {
    protected int id;
    protected String nom;
    protected String email;
    protected String motDePasse;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String email, String motDePasse) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    // Méthodes de validation
    public static boolean validerId(int id) {
        return id > 0;
    }

    public static boolean validerNom(String nom) {
        return nom != null && !nom.trim().isEmpty() && nom.matches("[a-zA-Z\\s]+");
    }

    public static boolean validerEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean validerMotDePasse(String motDePasse) {
        return motDePasse != null && motDePasse.length() >= 8 ;
    }

    // Recherche d'un utilisateur par ID
    public Utilisateur recherchebyid(int id) throws GestionStockException {
        if (!validerId(id)) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }

        String query = "SELECT * FROM Client WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Utilisateur(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getString("email"),
                    resultSet.getString("motDePasse")
                );
            } else {
                throw new GestionStockException("Aucun client trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de la recherche du client", e);
        }
    }

    // Authentification de l'admin
    @Override
    public boolean authentifierAdmin(String email, String motDePasse) throws GestionStockException {
        if (!validerEmail(email)) {
            throw new GestionStockException("Email n'existe pas.");
        }
        if (!validerMotDePasse(motDePasse)) {
            throw new GestionStockException("Mot de passe n'existe pas.");
        }

        String query = "SELECT * FROM Admin WHERE email = ? AND motDePasse = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, motDePasse);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true; // Admin trouvé
            } else {
                throw new GestionStockException("Email ou mot de passe n'existe pas");
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de l'authentification de l'admin", e);
        }
    }

    // Authentification du client
    @Override
    public boolean authentifierClient(String email, String motDePasse) throws GestionStockException {
        if (!validerEmail(email)) {
            throw new GestionStockException("Email n'existe pas.");
        }
        if (!validerMotDePasse(motDePasse)) {
            throw new GestionStockException("Mot de passe n'existe pas.");
        }

        String query = "SELECT * FROM Client WHERE email = ? AND motDePasse = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, email);
            statement.setString(2, motDePasse);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true; // Client trouvé
            } else {
                throw new GestionStockException("Email ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de l'authentification du client", e);
        }
    }

    // Inscription d'un client
   @Override
public void inscription(Utilisateur u) throws GestionStockException {
    if (!validerId(u.getId())) {
        throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
    }
    if (!validerNom(u.getNom())) {
        throw new GestionStockException("Nom invalide. Le nom ne doit contenir que des lettres.");
    }
    if (!validerEmail(u.getEmail())) {
        throw new GestionStockException("Email invalide. Veuillez entrer une adresse email valide.");
    }
    if (!validerMotDePasse(u.getMotDePasse())) {
        throw new GestionStockException("Mot de passe invalide. Le mot de passe doit contenir au moins 8 caractères.");
    }

    Connection connection = null;
    try {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        // Insérer le client dans la table Client
        String query = "INSERT INTO Client (id, nom, email, motDePasse) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, u.getId());
            statement.setString(2, u.getNom());
            statement.setString(3, u.getEmail());
            statement.setString(4, u.getMotDePasse());
            statement.executeUpdate();
        }

        // Créer une table spécifique pour les produits du client
        String createTableQuery = "CREATE TABLE Produits_Client_" + u.getId() + " (" +
                                  "idProduit INT PRIMARY KEY, " +
                                  "nom VARCHAR(255), " +
                                  "description TEXT, " +
                                  "prix DECIMAL(10, 2), " +
                                  "quantite INT, " +
                                  "FOREIGN KEY (idProduit) REFERENCES ProduitAdmin(id)" +
                                  ")";
        try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)) {
            createTableStatement.executeUpdate();
        }

        connection.commit();
        System.out.println("Inscription réussie et table Produits_Client_"+u.getId()+ "cree!");
    } catch (SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
            }
        }
        throw new GestionStockException("Erreur lors de l'inscription", e);
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
}

    // Confirmation d'une commande
    public void confirmerCommande() throws GestionStockException {
    Connection connection = null;
    try {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        // Récupérer les produits du panier (table Produits_Client_[ID])
        String selectQuery = "SELECT * FROM Produits_Client_" + this.id;
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = selectStatement.executeQuery();

            // Insérer chaque produit dans la table Commandes
            String insertQuery = "INSERT INTO Commandes (idClient, idProduit, date, statut) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                while (resultSet.next()) {
                    int idProduit = resultSet.getInt("idProduit");
                    String date = java.time.LocalDate.now().toString();
                    String statut = "En attente";

                    insertStatement.setInt(1, this.id);
                    insertStatement.setInt(2, idProduit);
                    insertStatement.setString(3, date);
                    insertStatement.setString(4, statut);
                    insertStatement.executeUpdate();
                }
            }

            // Vider le panier (table Produits_Client_[ID])
            String deleteQuery = "DELETE FROM Produits_Client_" + this.id;
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.executeUpdate();
            }

            connection.commit();
            System.out.println("Commande confirmée et enregistrée dans la base de données.");
        }
    } catch (SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
            }
        }
        throw new GestionStockException("Erreur lors de la confirmation de la commande", e);
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
}

    // Afficher les commandes d'un client
    public void afficherCommandesClient(int idClient) throws GestionStockException {
        if (!validerId(idClient)) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }

        String query = "SELECT * FROM Commandes WHERE idClient = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idClient);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Aucune commande trouvée pour ce client.");
            } else {
                do {
                    System.out.println("ID Commande: " + resultSet.getInt("id"));
                    System.out.println("ID Produit: " + resultSet.getInt("idProduit"));
                    System.out.println("Date: " + resultSet.getString("date"));
                    System.out.println("Statut: " + resultSet.getString("statut"));
                    System.out.println("-------------------------");
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de la récupération des commandes du client", e);
        }
    }

    // Vider le panier
    public void viderPanier() throws GestionStockException {
    Connection connection = null;
    try {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        // Vider la table spécifique au client (Produits_Client_[ID])
        String query = "DELETE FROM Produits_Client_" + this.id;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                connection.commit();
                System.out.println("Panier vidé avec succès !");
            } else {
                System.out.println("Le panier est déjà vide.");
            }
        }
    } catch (SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
            }
        }
        throw new GestionStockException("Erreur lors de la suppression des produits du panier", e);
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
}

    // Ajouter un produit
    public void ajoutProduit(Produit p) throws GestionStockException {
        if (!validerId(p.getId())) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }
        if (!validerNom(p.getNom())) {
            throw new GestionStockException("Nom invalide. Le nom ne doit contenir que des lettres.");
        }
        if (p.getDescription() == null || p.getDescription().trim().isEmpty()) {
            throw new GestionStockException("Description invalide. La description ne peut pas être vide.");
        }
        if (p.getPrix() <= 0) {
            throw new GestionStockException("Prix invalide. Le prix doit être un nombre positif.");
        }
        if (p.getStock() < 0) {
            throw new GestionStockException("Stock invalide. Le stock ne peut pas être négatif.");
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String query = "INSERT INTO ProduitAdmin (id, nom, description, prix, stock) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, p.getId());
                statement.setString(2, p.getNom());
                statement.setString(3, p.getDescription());
                statement.setDouble(4, p.getPrix());
                statement.setInt(5, p.getStock());

                statement.executeUpdate();
                connection.commit();
                System.out.println("Produit ajouté avec succès !");
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
                }
            }
            throw new GestionStockException("Erreur lors de l'ajout du produit", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    // Supprimer une commande
    public void supprimerCommande(int idCommande) throws GestionStockException {
        if (!validerId(idCommande)) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String query = "DELETE FROM Commandes WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idCommande);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    connection.commit();
                    System.out.println("Commande supprimée avec succès !");
                } else {
                    throw new GestionStockException("Aucune commande trouvée avec cet ID.");
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
                }
            }
            throw new GestionStockException("Erreur lors de la suppression de la commande", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    // Supprimer un produit
    public void supprimerProduit(int id) throws GestionStockException {
        if (!validerId(id)) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String query = "DELETE FROM ProduitAdmin WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    connection.commit();
                    System.out.println("Produit supprimé avec succès !");
                } else {
                    throw new GestionStockException("Aucun produit trouvé avec cet ID.");
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
                }
            }
            throw new GestionStockException("Erreur lors de la suppression du produit", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    // Lister les produits
    public void listeProduits() throws GestionStockException {
        String query = "SELECT * FROM ProduitAdmin";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Nom: " + resultSet.getString("nom"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Prix: " + resultSet.getDouble("prix"));
                System.out.println("Stock: " + resultSet.getInt("stock"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de la récupération des produits", e);
        }
    }

    // Afficher les commandes
    public void afficherCommandes() throws GestionStockException {
        String query = "SELECT * FROM Commandes";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("ID Commande: " + resultSet.getInt("id"));
                System.out.println("ID Client: " + resultSet.getInt("idClient"));
                System.out.println("ID Produit: " + resultSet.getInt("idProduit"));
                System.out.println("Date: " + resultSet.getString("date"));
                System.out.println("Statut: " + resultSet.getString("statut"));
                System.out.println("-------------------------");
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors de la récupération des commandes", e);
        }
    }

    // Calculer les ventes totales
    public void ventesTotales() throws GestionStockException {
        String query = "SELECT SUM(p.prix) AS total FROM ProduitAdmin p " +
                       "JOIN Commandes c ON p.id = c.idProduit";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                double total = resultSet.getDouble("total");
                System.out.println("Total des ventes: " + total);
            }
        } catch (SQLException e) {
            throw new GestionStockException("Erreur lors du calcul des ventes totales", e);
        }
    }

    // Modifier un produit
    public void modifierProduit(Produit produit) throws GestionStockException {
        if (!validerId(produit.getId())) {
            throw new GestionStockException("ID invalide. L'ID doit être un entier positif.");
        }
        if (!validerNom(produit.getNom())) {
            throw new GestionStockException("Nom invalide. Le nom ne doit contenir que des lettres.");
        }
        if (produit.getDescription() == null || produit.getDescription().trim().isEmpty()) {
            throw new GestionStockException("Description invalide. La description ne peut pas être vide.");
        }
        if (produit.getPrix() <= 0) {
            throw new GestionStockException("Prix invalide. Le prix doit être un nombre positif.");
        }
        if (produit.getStock() < 0) {
            throw new GestionStockException("Stock invalide. Le stock ne peut pas être négatif.");
        }

        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String query = "UPDATE ProduitAdmin SET nom = ?, description = ?, prix = ?, stock = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, produit.getNom());
                statement.setString(2, produit.getDescription());
                statement.setDouble(3, produit.getPrix());
                statement.setInt(4, produit.getStock());
                statement.setInt(5, produit.getId());

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    connection.commit();
                    System.out.println("Produit modifié avec succès !");
                } else {
                    throw new GestionStockException("Aucun produit trouvé avec cet ID.");
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
                }
            }
            throw new GestionStockException("Erreur lors de la modification du produit", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    // Lister les produits du panier
    public void listerProduits() throws GestionStockException {
    String query = "SELECT * FROM Produits_Client_" + this.id;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {

        if (!resultSet.next()) {
            System.out.println("Le panier est vide.");
        } else {
            do {
                System.out.println("ID: " + resultSet.getInt("idProduit"));
                System.out.println("Nom: " + resultSet.getString("nom"));
                System.out.println("Description: " + resultSet.getString("description"));
                System.out.println("Prix: " + resultSet.getDouble("prix"));
                System.out.println("Quantité: " + resultSet.getInt("quantite"));
                System.out.println("-------------------------");
            } while (resultSet.next());
        }
    } catch (SQLException e) {
        throw new GestionStockException("Erreur lors de la récupération des produits du panier", e);
    }
}

    // Calculer le total du panier
   public void calculerTotal() throws GestionStockException {
    String query = "SELECT SUM(prix * quantite) AS total FROM Produits_Client_" + this.id;
    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            double total = resultSet.getDouble("total");
            System.out.println("Total à payer: " + total);
        } else {
            System.out.println("Le panier est vide.");
        }
    } catch (SQLException e) {
        throw new GestionStockException("Erreur lors du calcul du total du panier", e);
    }
}

    // Ajouter un produit au panier
    public void ajouterProduit(String nom) throws GestionStockException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new GestionStockException("Nom invalide. Le nom ne peut pas être vide ou un entier.");
        }
          if(!nom.matches("[a-zA-Z\\s]+")) {
               throw new GestionStockException("Nom invalide. Le nom ne doit contenir que des lettres.");
          }
        nom = nom.trim();
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

             // Vérifier si le produit existe dans la table ProduitAdmin
            String checkProductQuery = "SELECT * FROM ProduitAdmin WHERE LOWER(nom) = LOWER(?)";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkProductQuery)) {
                checkStatement.setString(1, nom);
                ResultSet checkResultSet = checkStatement.executeQuery();
                if (!checkResultSet.next()) {
                   throw new GestionStockException("Le produit n'existe pas.");
                }
            }

            // Vérifier si le produit est disponible en stock
            String selectQuery = "SELECT * FROM ProduitAdmin WHERE LOWER(nom) = LOWER(?) AND stock > 0";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setString(1, nom);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    // Produit trouvé et en stock
                    int idProduit = resultSet.getInt("id");
                    String description = resultSet.getString("description");
                    double prix = resultSet.getDouble("prix");

                    // Ajouter le produit au panier (table Produits_Client_[ID])
                    String insertQuery = "INSERT INTO Produits_Client_" + this.id + " (idProduit, nom, description, prix, quantite) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                        insertStatement.setInt(1, idProduit);
                        insertStatement.setString(2, nom);
                        insertStatement.setString(3, description);
                        insertStatement.setDouble(4, prix);
                        insertStatement.setInt(5, 1); // Quantité par défaut
                        insertStatement.executeUpdate();
                        connection.commit();
                        System.out.println("Produit ajouté au panier.");
                    }
                } else {
                   throw new GestionStockException("Produit en rupture de stock.");
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
                }
            }
            throw new GestionStockException("Erreur lors de l'ajout du produit au panier", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
                }
            }
        }
    }

    // Retirer un produit du panier
    public void retirerProduit(String nom) throws GestionStockException {
    if (nom == null || nom.trim().isEmpty()) {
        throw new GestionStockException("Nom invalide. Le nom ne peut pas être vide.");
    }

    Connection connection = null;
    try {
        connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        // Supprimer le produit du panier (table Produits_Client_[ID])
        String deleteQuery = "DELETE FROM Produits_Client_" + this.id + " WHERE nom = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setString(1, nom);
            int rowsDeleted = deleteStatement.executeUpdate();

            if (rowsDeleted > 0) {
                connection.commit();
                System.out.println("Produit retiré du panier.");
            } else {
                throw new GestionStockException("Aucun produit trouvé avec ce nom dans le panier.");
            }
        }
    } catch (SQLException e) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new GestionStockException("Erreur lors de l'annulation de la transaction", ex);
            }
        }
        throw new GestionStockException("Erreur lors de la suppression du produit du panier", e);
    } finally {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                throw new GestionStockException("Erreur lors de la fermeture de la connexion", e);
            }
        }
    }
    
}
    
}