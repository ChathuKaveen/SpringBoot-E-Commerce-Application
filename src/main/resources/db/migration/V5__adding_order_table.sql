CREATE TABLE `storelastapp`.`orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` DATETIME NULL DEFAULT current_timestamp,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `user_idx` (`customer_id` ASC) VISIBLE,
  CONSTRAINT `user`
    FOREIGN KEY (`customer_id`)
    REFERENCES `storelastapp`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);