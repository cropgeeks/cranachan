SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for datasets
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `version` varchar(255) DEFAULT NULL,
  `filepath` text,
  `description` json DEFAULT NULL,
  `refseqset_id` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `datasets_ibfk_1` (`refseqset_id`) USING BTREE,
  CONSTRAINT `datasets_ibfk_1` FOREIGN KEY (`refseqset_id`) REFERENCES `refseqsets` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of datasets
-- ----------------------------

-- ----------------------------
-- Table structure for genes
-- ----------------------------
DROP TABLE IF EXISTS `genes`;
CREATE TABLE `genes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `geneset_id` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `geneset_id` (`geneset_id`) USING BTREE,
  CONSTRAINT `genes_ibfk_1` FOREIGN KEY (`geneset_id`) REFERENCES `genesets` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of genes
-- ----------------------------

-- ----------------------------
-- Table structure for genes_to_refseqs
-- ----------------------------
DROP TABLE IF EXISTS `genes_to_refseqs`;
CREATE TABLE `genes_to_refseqs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gene_id` int(11) NOT NULL,
  `refseq_id` int(11) NOT NULL,
  `created_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `gene_id` (`gene_id`) USING BTREE,
  KEY `refseq_id` (`refseq_id`) USING BTREE,
  CONSTRAINT `genes_to_refseqs_ibfk_1` FOREIGN KEY (`gene_id`) REFERENCES `genes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `genes_to_refseqs_ibfk_2` FOREIGN KEY (`refseq_id`) REFERENCES `refseqs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of genes_to_refseqs
-- ----------------------------

-- ----------------------------
-- Table structure for genesets
-- ----------------------------
DROP TABLE IF EXISTS `genesets`;
CREATE TABLE `genesets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `description` json DEFAULT NULL,
  `created_on` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of genesets
-- ----------------------------

-- ----------------------------
-- Table structure for refseqs
-- ----------------------------
DROP TABLE IF EXISTS `refseqs`;
CREATE TABLE `refseqs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `refseqset_id` int(11) NOT NULL,
  `length` bigint(20) DEFAULT NULL,
  `type` enum('UNKNOWN') DEFAULT 'UNKNOWN',
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `refseqset_id` (`refseqset_id`) USING BTREE,
  CONSTRAINT `refseqs_ibfk_1` FOREIGN KEY (`refseqset_id`) REFERENCES `refseqsets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of refseqs
-- ----------------------------

-- ----------------------------
-- Table structure for refseqsets
-- ----------------------------
DROP TABLE IF EXISTS `refseqsets`;
CREATE TABLE `refseqsets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `description` json DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of refseqsets
-- ----------------------------

-- ----------------------------
-- Table structure for samples
-- ----------------------------
DROP TABLE IF EXISTS `samples`;
CREATE TABLE `samples` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text,
  `name` varchar(255) NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of samples
-- ----------------------------

-- ----------------------------
-- Table structure for samples_to_datasets
-- ----------------------------
DROP TABLE IF EXISTS `samples_to_datasets`;
CREATE TABLE `samples_to_datasets` (
  `id` bigint(11) NOT NULL,
  `doi` text,
  `sample_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `germplasm_id` (`sample_id`) USING BTREE,
  KEY `dataset_id` (`dataset_id`) USING BTREE,
  CONSTRAINT `samples_to_datasets_ibfk_1` FOREIGN KEY (`sample_id`) REFERENCES `samples` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `samples_to_datasets_ibfk_2` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of samples_to_datasets
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
