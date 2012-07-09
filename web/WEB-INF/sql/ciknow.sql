-- MySQL dump 10.13  Distrib 5.5.9, for Win32 (x86)
--
-- Host: localhost    Database: _ciknow
-- ------------------------------------------------------
-- Server version	5.5.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nodes` (
  `node_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `label` text NOT NULL,
  `type` varchar(255) NOT NULL,
  `uri` text,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `mid_name` varchar(255) DEFAULT NULL,
  `addr1` varchar(255) DEFAULT NULL,
  `addr2` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `zipcode` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `cell` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `organization` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodes`
--

LOCK TABLES `nodes` WRITE;
/*!40000 ALTER TABLE `nodes` DISABLE KEYS */;
INSERT INTO `nodes` VALUES (1,1,'CIKNOW ADMIN','user','','admin','admin','CIKNOW','ADMIN','','','','','','','','','','','','','','','');
/*!40000 ALTER TABLE `nodes` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,1,'ROLE_ADMIN'),(2,1,'ROLE_HIDDEN'),(3,1,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES (1,1,'ALL'),(2,1,'USER');
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `node_group`
--

DROP TABLE IF EXISTS `node_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node_group` (
  `group_id` bigint(20) NOT NULL,
  `node_id` bigint(20) NOT NULL,
  PRIMARY KEY (`node_id`,`group_id`),
  KEY `FK5C02FE02AFCFDB07` (`node_id`),
  KEY `FK5C02FE0225033E6D` (`group_id`),
  CONSTRAINT `FK5C02FE0225033E6D` FOREIGN KEY (`group_id`) REFERENCES `groups` (`group_id`),
  CONSTRAINT `FK5C02FE02AFCFDB07` FOREIGN KEY (`node_id`) REFERENCES `nodes` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_group`
--

LOCK TABLES `node_group` WRITE;
/*!40000 ALTER TABLE `node_group` DISABLE KEYS */;
INSERT INTO `node_group` VALUES (1,1);
/*!40000 ALTER TABLE `node_group` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Table structure for table `node_role`
--

DROP TABLE IF EXISTS `node_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node_role` (
  `node_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`node_id`,`role_id`),
  KEY `FK1BC2FC93AFCFDB07` (`node_id`),
  KEY `FK1BC2FC9383DB5687` (`role_id`),
  CONSTRAINT `FK1BC2FC9383DB5687` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `FK1BC2FC93AFCFDB07` FOREIGN KEY (`node_id`) REFERENCES `nodes` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_role`
--

LOCK TABLES `node_role` WRITE;
/*!40000 ALTER TABLE `node_role` DISABLE KEYS */;
INSERT INTO `node_role` VALUES (1,1),(1,2),(1,3);
/*!40000 ALTER TABLE `node_role` ENABLE KEYS */;
UNLOCK TABLES;



--
-- Table structure for table `surveys`
--

DROP TABLE IF EXISTS `surveys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `surveys` (
  `survey_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `designer_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  `timestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`survey_id`),
  KEY `FK91914459FC4C1D7E` (`designer_id`),
  CONSTRAINT `FK91914459FC4C1D7E` FOREIGN KEY (`designer_id`) REFERENCES `nodes` (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surveys`
--

LOCK TABLES `surveys` WRITE;
/*!40000 ALTER TABLE `surveys` DISABLE KEYS */;
INSERT INTO `surveys` VALUES (1,1,1,'new survey','new survey description','2008-01-01 00:00:00');
/*!40000 ALTER TABLE `surveys` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;


--
-- Table structure for table `survey_attributes`
--

DROP TABLE IF EXISTS `survey_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `survey_attributes` (
  `survey_id` bigint(20) NOT NULL,
  `attr_value` varchar(255) NOT NULL,
  `attr_key` varchar(255) NOT NULL,
  PRIMARY KEY (`survey_id`,`attr_key`),
  KEY `FK899E1B5C5ABB4CC7` (`survey_id`),
  CONSTRAINT `FK899E1B5C5ABB4CC7` FOREIGN KEY (`survey_id`) REFERENCES `surveys` (`survey_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `survey_attributes`
--

LOCK TABLES `survey_attributes` WRITE;
/*!40000 ALTER TABLE `survey_attributes` DISABLE KEYS */;
INSERT INTO `survey_attributes` VALUES (1,'@','SURVEY_ADMIN_EMAIL'),(1,'default','SURVEY_DEFAULT_LOGIN_MODE'),(1,'sonic','SURVEY_DEFAULT_PASSWORD'),(1,'N','SURVEY_SHOW_LOGIN_LIST');
/*!40000 ALTER TABLE `survey_attributes` ENABLE KEYS */;
UNLOCK TABLES;