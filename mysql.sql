-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.21 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for mauritius_smartwater
CREATE DATABASE IF NOT EXISTS `mauritius_smartwater` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `mauritius_smartwater`;

-- Dumping structure for table mauritius_smartwater.connection_details
CREATE TABLE IF NOT EXISTS `connection_details` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CONSUMER_ID` int(11) NOT NULL,
  `CUSTOMER_ID` int(11) NOT NULL,
  `HOUSE_ID` int(11) DEFAULT '1001',
  `VENDOR_ID` int(11) DEFAULT '1001',
  `BLOCK_ID` int(11) DEFAULT '100',
  `HOUSE_NAMENUM` varchar(100) NOT NULL DEFAULT 'MAURITUS TESTING',
  `CURRENT_USAGE` bigint(20) NOT NULL,
  `ISPRIVATE` int(11) DEFAULT '0',
  `NOTIFY_LEAKAGE` int(11) DEFAULT '0',
  `NOTIFY_THRESHOLD_REACH` int(11) DEFAULT '0',
  `DEVICE_LATITUDE` decimal(10,8) DEFAULT '0.00000000',
  `DEVICE_LONGITUDE` decimal(10,8) DEFAULT '0.00000000',
  `devicenum` varchar(50) DEFAULT '0.00000000',
  `LOCATION` point DEFAULT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=1005 DEFAULT CHARSET=latin1;

-- Dumping data for table mauritius_smartwater.connection_details: ~4 rows (approximately)
/*!40000 ALTER TABLE `connection_details` DISABLE KEYS */;
INSERT INTO `connection_details` (`ID`, `CONSUMER_ID`, `CUSTOMER_ID`, `HOUSE_ID`, `VENDOR_ID`, `BLOCK_ID`, `HOUSE_NAMENUM`, `CURRENT_USAGE`, `ISPRIVATE`, `NOTIFY_LEAKAGE`, `NOTIFY_THRESHOLD_REACH`, `DEVICE_LATITUDE`, `DEVICE_LONGITUDE`, `devicenum`, `LOCATION`, `latitude`, `longitude`) VALUES
	(1001, 1001, 1001, 123, 1001, 1, 'CMT - Site 1 - CO MAURICIENNE D TEXTILE LTE', 0, 0, 1, 1, -20.17700457, 57.46664000, '0.00000000', _binary 0x0000000001010000007E64E72B502D34C0B28009DCBABB4C40, 0, 0),
	(1002, 1002, 1001, 124, 1001, 1, 'Cascavelle', 0, 0, 1, 1, -20.27784200, 57.40379000, '0.00000000', _binary 0x0000000001010000008A743FA7204734C0D6390664AFB34C40, 0, 0),
	(1003, 1003, 1001, 125, 1003, 1, 'Maradiva Hotel', 0, 0, 1, 1, -20.31509800, 57.37128800, '0.00000000', _binary 0x000000000101000000F6083543AA5034C0D9B27C5D86AF4C40, 0, 0),
	(1004, 1004, 1001, 126, 1004, 1, 'Le Residence Hotel, Belle Mare (BELLE MARE BEACH DEV CO LTD)', 0, 0, 1, 1, -20.19877800, 57.77989500, '0.00000000', _binary 0x0000000001010000000F2A711DE33234C02FA86F99D3E34C40, 0, 0);
/*!40000 ALTER TABLE `connection_details` ENABLE KEYS */;

-- Dumping structure for table mauritius_smartwater.meter_connection
CREATE TABLE IF NOT EXISTS `meter_connection` (
  `id` bigint(20) NOT NULL,
  `block_id` bigint(20) NOT NULL,
  `consumer_id` bigint(20) NOT NULL,
  `current_usage` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `house_id` bigint(20) NOT NULL,
  `house_namenum` varchar(255) DEFAULT NULL,
  `isprivate` bit(1) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `vendor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- Dumping data for table mauritius_smartwater.meter_connection: 0 rows
/*!40000 ALTER TABLE `meter_connection` DISABLE KEYS */;
/*!40000 ALTER TABLE `meter_connection` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
