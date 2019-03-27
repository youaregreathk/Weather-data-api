-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema rotation
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema rotation
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rotation` DEFAULT CHARACTER SET utf8 ;
USE `rotation` ;

-- -----------------------------------------------------
-- Table `rotation`.`manager`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rotation`.`manager` (
  `manager_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  PRIMARY KEY (`manager_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rotation`.`team`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rotation`.`team` (
  `team_id` INT NOT NULL AUTO_INCREMENT,
  `team_name` VARCHAR(45) NULL,
  `position_description` VARCHAR(1000) NULL,
  `manager_id` INT NOT NULL,
  PRIMARY KEY (`team_id`),
  INDEX `fk_team_manger_idx` (`manager_id` ASC),
  CONSTRAINT `fk_team_manger`
    FOREIGN KEY (`manager_id`)
    REFERENCES `rotation`.`manager` (`manager_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rotation`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rotation`.`employee` (
  `employee_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `position` VARCHAR(45) NULL,
  `description` VARCHAR(1000) NULL,
  `num_of_year_experience` INT NULL,
  `current_team_name` VARCHAR(50) NULL,
  `current_manager_name` VARCHAR(45) NULL,
  PRIMARY KEY (`employee_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rotation`.`transition`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rotation`.`transition` (
  `transition_id` INT NOT NULL AUTO_INCREMENT,
  `desired_manager_id` INT NOT NULL,
  `request_date` DATETIME NULL,
  `employee_id` INT NOT NULL,
  `desired_team_id` INT NOT NULL,
  PRIMARY KEY (`transition_id`),
  INDEX `fk_employee_manger1_idx` (`desired_manager_id` ASC),
  INDEX `fk_transition_employee1_idx` (`employee_id` ASC),
  INDEX `fk_transition_team1_idx` (`desired_team_id` ASC),
  CONSTRAINT `fk_employee_manger1`
    FOREIGN KEY (`desired_manager_id`)
    REFERENCES `rotation`.`manager` (`manager_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transition_employee1`
    FOREIGN KEY (`employee_id`)
    REFERENCES `rotation`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_transition_team1`
    FOREIGN KEY (`desired_team_id`)
    REFERENCES `rotation`.`team` (`team_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rotation`.`manger_copy1`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `rotation`.`manger_copy1` (
  `manager_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(45) NULL,
  `last_name` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  PRIMARY KEY (`manager_id`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
