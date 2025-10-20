ALTER TABLE `storelastapp`.`users` 
ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'USER' AFTER `password`;