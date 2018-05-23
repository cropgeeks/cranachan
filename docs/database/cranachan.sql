SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for datasets
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `version` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `filepath` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `description` json NULL,
  `refseqset_id` int(11) NULL DEFAULT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `datasets_ibfk_1`(`refseqset_id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE,
  CONSTRAINT `datasets_ibfk_1` FOREIGN KEY (`refseqset_id`) REFERENCES `refseqsets` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for genes
-- ----------------------------
DROP TABLE IF EXISTS `genes`;
CREATE TABLE `genes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `geneset_id` int(11) NULL DEFAULT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `geneset_id`(`geneset_id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE,
  INDEX `name_2`(`name`, `geneset_id`) USING BTREE,
  INDEX `geneset_id_2`(`geneset_id`, `name`) USING BTREE,
  CONSTRAINT `genes_ibfk_1` FOREIGN KEY (`geneset_id`) REFERENCES `genesets` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for genes_to_refseqs
-- ----------------------------
DROP TABLE IF EXISTS `genes_to_refseqs`;
CREATE TABLE `genes_to_refseqs`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `gene_id` int(11) NOT NULL,
  `refseq_id` int(11) NOT NULL,
  `position_start` bigint(20) NOT NULL,
  `position_end` bigint(20) NOT NULL,
  `coverage` float(64, 10) NOT NULL,
  `percentage_identity` float(64, 10) NOT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `gene_id`(`gene_id`) USING BTREE,
  INDEX `refseq_id`(`refseq_id`) USING BTREE,
  CONSTRAINT `genes_to_refseqs_ibfk_1` FOREIGN KEY (`gene_id`) REFERENCES `genes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `genes_to_refseqs_ibfk_2` FOREIGN KEY (`refseq_id`) REFERENCES `refseqs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for genesets
-- ----------------------------
DROP TABLE IF EXISTS `genesets`;
CREATE TABLE `genesets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `description` json NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for refseqs
-- ----------------------------
DROP TABLE IF EXISTS `refseqs`;
CREATE TABLE `refseqs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `refseqset_id` int(11) NOT NULL,
  `length` bigint(20) NULL DEFAULT NULL,
  `type` enum('UNKNOWN') CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT 'UNKNOWN',
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `refseqset_id`(`refseqset_id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE,
  CONSTRAINT `refseqs_ibfk_1` FOREIGN KEY (`refseqset_id`) REFERENCES `refseqsets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for refseqsets
-- ----------------------------
DROP TABLE IF EXISTS `refseqsets`;
CREATE TABLE `refseqsets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `description` json NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sample_lists
-- ----------------------------
DROP TABLE IF EXISTS `sample_lists`;
CREATE TABLE `sample_lists`  (
  `id` varchar(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `sample_list` longtext CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  CONSTRAINT `sample_lists_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for samples
-- ----------------------------
DROP TABLE IF EXISTS `samples`;
CREATE TABLE `samples`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for samples_to_datasets
-- ----------------------------
DROP TABLE IF EXISTS `samples_to_datasets`;
CREATE TABLE `samples_to_datasets`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `doi` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `sample_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `created_on` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `germplasm_id`(`sample_id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  CONSTRAINT `samples_to_datasets_ibfk_1` FOREIGN KEY (`sample_id`) REFERENCES `samples` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `samples_to_datasets_ibfk_2` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
