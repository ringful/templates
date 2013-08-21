
CREATE  TABLE IF NOT EXISTS `users` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `recordDate` DATETIME NOT NULL ,
  `status` VARCHAR(32) NOT NULL ,
  `username` VARCHAR(128) NOT NULL ,
  `password` VARCHAR(128) NOT NULL ,
  `firstname` VARCHAR(128) ,
  `lastname` VARCHAR(128) ,
  `dob` DATETIME ,
  `phone` VARCHAR(64) ,
  `phoneConfirmed` INT ,
  `phoneConfirmCode` VARCHAR(128) ,
  `email` VARCHAR(512) ,
  `emailConfirmed` INT ,
  `emailConfirmCode` VARCHAR(128) ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;

CREATE  TABLE IF NOT EXISTS `events` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT ,
  `eventDate` DATETIME ,
  `saveDate` DATETIME ,
  `appName` VARCHAR(128) ,
  `appUserId` VARCHAR(128) ,
  `firstname` VARCHAR(128) ,
  `lastname` VARCHAR(128) ,
  `contact` VARCHAR(1024) ,
  `eventName` VARCHAR(128) ,
  `eventParam` VARCHAR(4096) ,
  `eventMachParam` VARCHAR(4096) ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT CHARACTER SET = utf8;


