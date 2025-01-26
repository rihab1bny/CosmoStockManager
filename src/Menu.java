import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static int choix, id, stock, idClient;
    private static boolean retour, verif;
    private static String nom, email, motP, description, reponse;
    private static Utilisateur u;
    private static Produit p;
    private static double prix;

    public static void effacerEcran() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur lors de l'effacement de l'écran : " + e.getMessage());
        }
    }

    public static int lireEntier(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erreur, veuillez entrer un nombre entier valide.");
            }
        }
    }

    public static double lireDecimal(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erreur, veuillez entrer un nombre décimal valide.");
            }
        }
    }

    public static String lireChaine(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }

    public static boolean lireBoolen(String msg) {
        System.out.print(msg);
        return Boolean.parseBoolean(sc.nextLine());
    }

    public static void attendre() {
        System.out.println("Appuyez sur la touche ENTREE pour continuer ...");
        sc.nextLine();
    }

    public static void menuPrincipal(Utilisateur user) throws SQLException {
        boolean localverif = false;
        while (!localverif) {
            effacerEcran();
            System.out.println("--------------- Menu principal --------------");
            System.out.println("Vous êtes : ");
            System.out.println("1. Admin");
            System.out.println("2. Client");
            System.out.println("3. Quitter");
            choix = lireEntier("Votre choix : ");

            switch (choix) {
                case 1:
                    menuAdmin(user);
                    attendre();
                    break;
                case 2:
                    menuClient(user);
                    attendre();
                    break;
                case 3:
                   localverif = true;
                    System.out.println("Fermeture en cours ...");
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer ...");
            }
        }
    }

  
          public static void menuAdmin(Utilisateur user) {
        boolean localRetour = false; 
        while (!localRetour) {
             effacerEcran();
            System.out.println("--------------- Menu Admin --------------");
            System.out.println("1. Authentifier");
            System.out.println("2. Retour au menu principal");
            choix = lireEntier("Votre choix : ");

            switch (choix) {
                case 1:
                    email = lireChaine("Email : ");
                    motP = lireChaine("Mot de passe : ");
                    try {
                        if (user.authentifierAdmin(email, motP)) {
                            menuAdminOperations(user);
                        } else {
                            System.out.println("Authentification échouée.");
                        }
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 2:
                  localRetour = true;
                     System.out.println("Fermeture en cours ...");
                    break;
                default:
                   System.out.println("Choix invalide, veuillez réessayer ...");
                    attendre();
            }
        }
    }

  
      public static void menuClient(Utilisateur user) throws SQLException {
        boolean localRetour = false;
        while (!localRetour) {
            effacerEcran();//clear screen first
            System.out.println("--------------- Menu Client --------------");
            System.out.println("1. Authentifier");
            System.out.println("2. Inscrire");
            System.out.println("3. Retour au menu principal");
            choix = lireEntier("Votre choix : ");

            switch (choix) {
                case 1:
                    email = lireChaine("Email : ");
                    motP = lireChaine("Mot de passe : ");
                     try {
                        if (user.authentifierClient(email, motP)) {
                            Utilisateur authenticatedUser = new Utilisateur();
                            String query = "SELECT * FROM Client WHERE email = ?";
                            try (Connection connection = DatabaseConnection.getConnection();
                                PreparedStatement statement = connection.prepareStatement(query)) {
                                statement.setString(1, email);
                                ResultSet resultSet = statement.executeQuery();
    
                                  if (resultSet.next()) {
                                      authenticatedUser = new Utilisateur(
                                      resultSet.getInt("id"),
                                      resultSet.getString("nom"),
                                      resultSet.getString("email"),
                                      resultSet.getString("motDePasse")
                                      );
                                      System.out.println("Authenticated User ID: " + authenticatedUser.getId());
                                    }
                                }
                                 attendre();
                                menuClientOperations(authenticatedUser);

                        } else {
                            System.out.println("Authentification échouée.");
                        }
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 2:
                    boolean isValid = false;
                     while(!isValid){
                      id = lireEntier("ID : ");
                       nom = lireChaine("Nom : ");
                       email = lireChaine("Email : ");
                       motP = lireChaine("Mot de passe : ");
                      Utilisateur u = new Utilisateur(id, nom, email, motP);
                        try {
                        user.inscription(u);
                        System.out.println("Inscription réussie !");
                         attendre();
                         menuClientOperations(u);
                          isValid = true;

                       } catch (GestionStockException e) {
                             System.err.println("Erreur : " + e.getMessage());
                         }
                       attendre();
                     }
                    break;
                case 3:
                  localRetour = true;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer ...");
                    attendre();
            }
        }
    }
     
    public static void menuAdminOperations(Utilisateur user) throws GestionStockException {
        boolean localRetour = false;
        while (!localRetour) {
            effacerEcran();
            System.out.println("--------------- Menu Admin Opération --------------");
            System.out.println("1. Gérer produits");
            System.out.println("2. Consulter commandes");
            System.out.println("3. Supprimer commandes");
            System.out.println("4. Générer rapports");
            System.out.println("5. Quitter");
            choix = lireEntier("Votre choix : ");

            switch (choix) {
                case 1:
                    menuGestionProduits(user);
                    attendre();
                    break;
                case 2:
                    user.afficherCommandes();
                    attendre();
                    break;
                case 3:
                    id = lireEntier("ID de la commande à supprimer : ");
                    try {
                        user.supprimerCommande(id);
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 4:
                    try {
                        user.ventesTotales();
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                 case 5:
                     localRetour=true;
                     break;
                default:
                     System.out.println("Choix invalide, veuillez réessayer ...");
                      attendre();

            }
        }
    }
    public static void menuGestionProduits(Utilisateur user) throws GestionStockException {
        boolean localRetour = false;
        while (!localRetour) {
            effacerEcran();
            System.out.println("--------------- Menu Gestion Produits --------------");
            System.out.println("1. Ajouter produits");
            System.out.println("2. Supprimer produits");
            System.out.println("3. Lister produits");
            System.out.println("4. Modifier produits");
            System.out.println("5. Retour au menu admin");
            choix = lireEntier("Votre choix : ");

            switch (choix) {
                case 1:
                    id = lireEntier("ID : ");
                    nom = lireChaine("Nom : ");
                    description = lireChaine("Description : ");
                    prix = lireDecimal("Prix : ");
                    stock = lireEntier("Stock : ");
                    p = new Produit(id, nom, description, prix, stock);
                    try {
                        user.ajoutProduit(p);
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 2:
                    id = lireEntier("ID : ");
                    try {
                        user.supprimerProduit(id);
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 3:
                    user.listeProduits();
                    attendre();
                    break;
                case 4:
                    id = lireEntier("ID : ");
                    nom = lireChaine("Nom : ");
                    description = lireChaine("Description : ");
                    prix = lireDecimal("Prix : ");
                    stock = lireEntier("Stock : ");
                    p = new Produit(id, nom, description, prix, stock);
                    try {
                        user.modifierProduit(p);
                    } catch (GestionStockException e) {
                        System.err.println("Erreur : " + e.getMessage());
                    }
                    attendre();
                    break;
                case 5:
                   localRetour = true;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer ...");
            }
        }
    }
   
  public static void menuClientOperations(Utilisateur user) throws GestionStockException, SQLException {
    boolean localRetour = false;
    while (!localRetour) {
        effacerEcran();
        System.out.println("--------------- Menu Client Opération --------------");
        System.out.println("1. Ajouter Produit au Panier");
        System.out.println("2. Retirer Produit du Panier");
        System.out.println("3. Afficher Panier");
        System.out.println("4. Vider Panier");
        System.out.println("5. Calculer Total");
        System.out.println("6. Quitter");
        choix = lireEntier("Votre choix : ");

        switch (choix) {
            case 1:
                nom = lireChaine("Nom : ");
                try {
                    user.ajouterProduit(nom);
                } catch (GestionStockException e) {
                    System.err.println("Erreur : " + e.getMessage());
                }
                attendre();
                break;
            case 2:
                nom = lireChaine("Nom Produit : ");
                try {
                    user.retirerProduit(nom);
                } catch (GestionStockException e) {
                    System.err.println("Erreur : " + e.getMessage());
                }
                attendre();
                break;
            case 3:
                user.listerProduits();
                attendre();
                break;
            case 4:
                user.viderPanier();
                attendre();
                break;
             case 5:
                   try {
                      user.calculerTotal();
                      String query = "SELECT count(*) FROM Produits_Client_" + user.getId();
                       int count = 0;
                       try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query);
                        ResultSet resultSet = statement.executeQuery())
                       {
                         if (resultSet.next())
                            count = resultSet.getInt(1);
                        }
                    if (count > 0)
                       {
                          System.out.println("Voulez-vous valider la commande (o/n) :");
                          reponse = lireChaine("Votre choix : ").toUpperCase();
                        if (reponse.equals("O")) {
                                Utilisateur userForOrderConfirmation = new Utilisateur().recherchebyid(user.getId());
                                userForOrderConfirmation.confirmerCommande();
                                 idClient = user.getId();
                             user.afficherCommandesClient(idClient);
                            }
                        } else {
                            System.out.println("Le panier est vide.");
                        }
                     } catch (GestionStockException e) {
                            System.err.println("Erreur : " + e.getMessage());
                        }
                        attendre();
                        break;
            case 6:
                localRetour = true;
                System.out.println("Fermeture en cours ...");
                break;
            default:
                System.out.println("Choix invalide, veuillez réessayer ...");
        }
    }
}
}