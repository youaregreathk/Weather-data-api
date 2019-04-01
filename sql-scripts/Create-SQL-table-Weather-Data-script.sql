-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`weather_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`weather_data` (
  `geo_coordinate` VARCHAR(20) NOT NULL,
  `time_stamp` DATETIME NOT NULL,
  `weather_description` VARCHAR(30) NULL,
  `country` VARCHAR(15) NULL,
  `humidity` BIGINT(20) NULL,
  `temp` BIGINT(20) NULL,
  `temp_min` BIGINT(20) NULL,
  `temp_max` BIGINT(20) NULL,
  `sun_rise` BIGINT(20) NULL,
  `sun_set` BIGINT(20) NULL,
  PRIMARY KEY (`time_stamp`, `geo_coordinate`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
