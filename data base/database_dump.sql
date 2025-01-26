-- MySQL dump 10.13  Distrib 8.1.0, for Win64 (x86_64)
--
-- Host: localhost    Database: gestion_produits_cosmetiques
-- ------------------------------------------------------
-- Server version	8.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `motDePasse` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'Admin1','admin1@example.com','admin123'),(2,'Admin2','admin2@example.com','admin456');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `motDePasse` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES (30,'ines','ines@gmail.com','ines1234'),(50,'tasnim','tasnim@gmail.com','tasnim123'),(60,'maram','maram@gmail.com','maram123'),(80,'rihab','rihab@gmail.com','rihab123'),(90,'ons','ons@gmail.com','ons12345'),(99,'farah','farah@gmail.com','farah123'),(111,'asma','asma@gmail.com','asma1234');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commandes`
--

DROP TABLE IF EXISTS `commandes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commandes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idClient` int DEFAULT NULL,
  `idProduit` int DEFAULT NULL,
  `date` date DEFAULT NULL,
  `statut` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idClient` (`idClient`),
  KEY `idProduit` (`idProduit`),
  CONSTRAINT `commandes_ibfk_1` FOREIGN KEY (`idClient`) REFERENCES `client` (`id`),
  CONSTRAINT `commandes_ibfk_2` FOREIGN KEY (`idProduit`) REFERENCES `produitadmin` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commandes`
--

LOCK TABLES `commandes` WRITE;
/*!40000 ALTER TABLE `commandes` DISABLE KEYS */;
INSERT INTO `commandes` VALUES (7,80,4,'2025-01-22','En attente');
/*!40000 ALTER TABLE `commandes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produitadmin`
--

DROP TABLE IF EXISTS `produitadmin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produitadmin` (
  `id` int NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `description` text,
  `prix` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produitadmin`
--

LOCK TABLES `produitadmin` WRITE;
/*!40000 ALTER TABLE `produitadmin` DISABLE KEYS */;
INSERT INTO `produitadmin` VALUES (1,'Creme hydratante','Crème hydratante pour peau sèche',25.99,100),(2,'masque cheuvuex','cheuvuex secs',10.00,10),(3,'Serum anti-age','Sérum anti-rides et raffermissant',45.00,50),(4,'shampoo','cheveux normal',10.00,30);
/*!40000 ALTER TABLE `produitadmin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produits_client_111`
--

DROP TABLE IF EXISTS `produits_client_111`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produits_client_111` (
  `idProduit` int NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` text,
  `prix` decimal(10,2) DEFAULT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`idProduit`),
  CONSTRAINT `produits_client_111_ibfk_1` FOREIGN KEY (`idProduit`) REFERENCES `produitadmin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produits_client_111`
--

LOCK TABLES `produits_client_111` WRITE;
/*!40000 ALTER TABLE `produits_client_111` DISABLE KEYS */;
INSERT INTO `produits_client_111` VALUES (4,'shampoo','cheveux normal',10.00,1);
/*!40000 ALTER TABLE `produits_client_111` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produits_client_80`
--

DROP TABLE IF EXISTS `produits_client_80`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produits_client_80` (
  `idProduit` int NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` text,
  `prix` decimal(10,2) DEFAULT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`idProduit`),
  CONSTRAINT `produits_client_80_ibfk_1` FOREIGN KEY (`idProduit`) REFERENCES `produitadmin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produits_client_80`
--

LOCK TABLES `produits_client_80` WRITE;
/*!40000 ALTER TABLE `produits_client_80` DISABLE KEYS */;
INSERT INTO `produits_client_80` VALUES (2,'masque cheuvuex','cheuvuex secs',10.00,1);
/*!40000 ALTER TABLE `produits_client_80` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produits_client_90`
--

DROP TABLE IF EXISTS `produits_client_90`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produits_client_90` (
  `idProduit` int NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` text,
  `prix` decimal(10,2) DEFAULT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`idProduit`),
  CONSTRAINT `produits_client_90_ibfk_1` FOREIGN KEY (`idProduit`) REFERENCES `produitadmin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produits_client_90`
--

LOCK TABLES `produits_client_90` WRITE;
/*!40000 ALTER TABLE `produits_client_90` DISABLE KEYS */;
/*!40000 ALTER TABLE `produits_client_90` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produits_client_99`
--

DROP TABLE IF EXISTS `produits_client_99`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produits_client_99` (
  `idProduit` int NOT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` text,
  `prix` decimal(10,2) DEFAULT NULL,
  `quantite` int DEFAULT NULL,
  PRIMARY KEY (`idProduit`),
  CONSTRAINT `produits_client_99_ibfk_1` FOREIGN KEY (`idProduit`) REFERENCES `produitadmin` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produits_client_99`
--

LOCK TABLES `produits_client_99` WRITE;
/*!40000 ALTER TABLE `produits_client_99` DISABLE KEYS */;
/*!40000 ALTER TABLE `produits_client_99` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-26 13:54:50
