CREATE TABLE `storelastapp`.`order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `unit_price` DECIMAL(10,2) NOT NULL,
  `quantity` INT NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `orderForeign_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `orderForeign`
    FOREIGN KEY (`order_id`)
    REFERENCES `storelastapp`.`orders` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
