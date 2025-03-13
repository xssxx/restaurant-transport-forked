CREATE TABLE IF NOT EXISTS `user` (
  `user_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_role` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `username` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `food` (
  `f_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `f_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `f_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `f_price` double DEFAULT NULL,
  `f_status` enum('AVAILABLE','OUT_OF_STOCK') COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`f_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=UTF8MB4_GENERAL_CI;

CREATE TABLE IF NOT EXISTS `ingredient` (
  `i_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `expire_date` datetime(6) DEFAULT NULL,
  `i_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `i_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `i_price` double DEFAULT NULL,
  `i_qty` int DEFAULT NULL,
  `i_status` enum('AVAILABLE','OUT_OF_STOCK') COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`i_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `recipe` (
  `qty` int DEFAULT NULL,
  `f_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `i_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`f_id`,`i_id`),
  KEY `FKmwjvb90tqvbxf6w1fhm137sfu` (`i_id`),
  CONSTRAINT `FK886dmvuw19j7kcx40bc29dfle` FOREIGN KEY (`f_id`) REFERENCES `food` (`f_id`),
  CONSTRAINT `FKmwjvb90tqvbxf6w1fhm137sfu` FOREIGN KEY (`i_id`) REFERENCES `ingredient` (`i_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `receipt` (
  `b_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `b_total` double DEFAULT NULL,
  PRIMARY KEY (`b_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `order` (
  `o_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `payment_link` text COLLATE utf8mb4_general_ci,
  `o_status` enum('CANCEL','COMPLETE','PENDING', 'COOKING', 'READY', 'SUCCESS','DELIVERING', 'DELIVERED') COLLATE utf8mb4_general_ci NOT NULL,
  `o_total` double NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `b_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`o_id`),
  UNIQUE KEY `UKpe4cy32c7wsmbx33p669m0um9` (`b_id`),
  KEY `FKcpl0mjoeqhxvgeeeq5piwpd3i` (`user_id`),
  CONSTRAINT `FK6glxwv2n7xrimx8ra6dnuefda` FOREIGN KEY (`b_id`) REFERENCES `receipt` (`b_id`),
  CONSTRAINT `FKcpl0mjoeqhxvgeeeq5piwpd3i` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `order_line` (
  `o_qty` int DEFAULT NULL,
  `f_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `o_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`f_id`,`o_id`),
  KEY `FKkwkeey9eqd4wcoeehc7fxc4gt` (`o_id`),
  CONSTRAINT `FKb18dij6dbe2a6am9ma2pjow8g` FOREIGN KEY (`f_id`) REFERENCES `food` (`f_id`),
  CONSTRAINT `FKkwkeey9eqd4wcoeehc7fxc4gt` FOREIGN KEY (`o_id`) REFERENCES `order` (`o_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `review` (
  `review_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `review_date` datetime(6) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `customer_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `like_count` int DEFAULT 0,
  PRIMARY KEY (`review_id`),
  KEY `FKrrkqlt8co52qjdj34nqv97xn4` (`customer_id`),
  KEY `FK80acgchiskxpcqegik62mf1jg` (`order_id`),
  CONSTRAINT `FK80acgchiskxpcqegik62mf1jg` FOREIGN KEY (`order_id`) REFERENCES `order` (`o_id`),
  CONSTRAINT `FKrrkqlt8co52qjdj34nqv97xn4` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `financial` (
  `date` date NOT NULL,
  `expense` double NOT NULL,
  `income` double NOT NULL,
  `total` double NOT NULL,
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=UTF8MB4_GENERAL_CI;

CREATE TABLE IF NOT EXISTS `liked_by` (
  `review_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`review_id`,`user_id`),
  KEY `FKo6qhypgn2bf8gqis41ebg0nl1` (`user_id`),
  CONSTRAINT `FKcrqh58co48rh641bn58ms7nu5` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`),
  CONSTRAINT `FKo6qhypgn2bf8gqis41ebg0nl1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `comment` (
  `comment_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `comment` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `comment_at` datetime(6) DEFAULT NULL,
  `review_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`comment_id`),
  KEY `FKnf4ni761w29tmtgdxymmgvg8r` (`review_id`),
  KEY `FK8kcum44fvpupyw6f5baccx25c` (`user_id`),
  CONSTRAINT `FK8kcum44fvpupyw6f5baccx25c` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKnf4ni761w29tmtgdxymmgvg8r` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `review_comment` (
  `comment_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  `review_id` varchar(36) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`comment_id`,`review_id`),
  KEY `FK9j7pkcpuestrwjre1a1902biu` (`review_id`),
  CONSTRAINT `FK9j7pkcpuestrwjre1a1902biu` FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`),
  CONSTRAINT `FKpljpgtuaj54qaod48639ekslo` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/* ---- INSERT ---- */

DELETE FROM `user`;
INSERT INTO `user` (`user_id`, `password`, `phone`, `user_role`, `username`) VALUES
	('18817ab1-0e27-4ed6-adfa-2f7fa51271b7', '$2a$10$OvhEw66x5GbUvm3O1CS/Nul4rk55/HwhYFuiveOSX.rSgUyJaSng6', '0958738843', 'RIDER', 'giwi'),
	('70696dc0-f986-41f1-b59b-276b1c1ca66c', '$2a$10$vsu2gj19CeCjPOy/qAuPse1MtfJm65qPkxPr3G9Mwcynev7H.Wiy2', '0958738843', 'ADMIN', 'admin'),
	('b0d0b18c-774c-4c4a-9851-5ead92851f6e', '$2a$10$976oiad6zK8C48xSFKLOMOhOx4fRVcLob32KlYtjkql5L1Sf2/ZKO', '0958778844', 'CUSTOMER', 'banana'),
	('b2e4894c-4649-49bb-8c52-1a5c04502f4b', '$2a$10$qoy4zojbU2RrQroQvK7NvutX8U0bpUo0h94Ltw0TZ.6l.UzmYzzOi', '0958777744', 'COOK', 'apple');


DELETE FROM `food`;
INSERT INTO `food` (`f_id`, `f_image`, `f_name`, `f_price`, `f_status`) VALUES
	('1d3392c5-6b8e-4148-a9d5-9d2df91f431d', 'src\\main\\resources\\images\\foods\\1730788575225_duck-noodle.jpg', 'Noodle Duck', 65, 'AVAILABLE'),
	('2ac03bed-3f26-4c7c-b808-006e300bbf5a', 'src\\main\\resources\\images\\foods\\1730247506882_noodle.jpg', 'Noodle', 50, 'AVAILABLE'),
	('88679d7f-74e7-427b-a62b-e4b54f4d1d3a', 'src\\main\\resources\\images\\foods\\1730788460667_duck.jpg', 'Duck Rice', 70, 'AVAILABLE'),
	('91186d12-c393-47fa-ad75-8d7349f85656', 'src\\main\\resources\\images\\foods\\1730788760434_duck-fried.jpg', 'Fried Duck', 75, 'AVAILABLE'),
	('c6b47382-855b-499d-b00a-3b28a3cb4f34', 'src\\main\\resources\\images\\foods\\1730188320174_pork-rice.jpg', 'pork rice', 60, 'AVAILABLE'),
	('ed6f1c95-4887-4248-9051-9d33218b0e05', 'src\\main\\resources\\images\\foods\\1730788685166_pork-fried.jpg', 'Fried Pork', 35, 'AVAILABLE');

DELETE FROM `ingredient`;
INSERT INTO `ingredient` (`i_id`, `expire_date`, `i_image`, `i_name`, `i_price`, `i_qty`, `i_status`) VALUES
	('007a587c-b861-413f-bfa6-11f24d58924c', '2024-10-31 07:00:00.000000', 'src\\main\\resources\\images\\ingredients\\1730199260204_pork.jpg', 'Pork', 30, 82, 'AVAILABLE'),
	('11016aab-fd83-4c1b-a0cf-6de026e5f4ae', '2024-11-30 07:00:00.000000', 'src\\main\\resources\\images\\ingredients\\1730788415631_duck_meat.jpg', 'Duck meat', 50, 26, 'AVAILABLE'),
	('40b5d8d7-b01c-4674-981f-99cc4bcacd92', '2024-10-30 07:00:00.000000', 'src\\main\\resources\\images\\ingredients\\1730210989683_rice.jpg', 'Rice', 100, 8, 'AVAILABLE'),
	('c6b2ef97-e829-45c5-9bc8-7ce7f40db141', '2024-11-30 07:00:00.000000', 'src\\main\\resources\\images\\ingredients\\1730788525994_line.jpg', 'Noodle', 30, 41, 'AVAILABLE'),
	('f9ad834b-4a1d-47f7-99a3-c30df090001a', '2025-02-02 07:00:00.000000', 'src\\main\\resources\\images\\ingredients\\1730188262474_fish_sauce.jpg', 'fish sauce', 15, 13, 'AVAILABLE');

DELETE FROM `recipe`;
INSERT INTO `recipe` (`qty`, `f_id`, `i_id`) VALUES
	(1, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', '11016aab-fd83-4c1b-a0cf-6de026e5f4ae'),
	(2, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', 'c6b2ef97-e829-45c5-9bc8-7ce7f40db141'),
	(1, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', 'f9ad834b-4a1d-47f7-99a3-c30df090001a'),
	(3, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', '007a587c-b861-413f-bfa6-11f24d58924c'),
	(1, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', 'f9ad834b-4a1d-47f7-99a3-c30df090001a'),
	(1, '88679d7f-74e7-427b-a62b-e4b54f4d1d3a', '11016aab-fd83-4c1b-a0cf-6de026e5f4ae'),
	(1, '88679d7f-74e7-427b-a62b-e4b54f4d1d3a', '40b5d8d7-b01c-4674-981f-99cc4bcacd92'),
	(1, '91186d12-c393-47fa-ad75-8d7349f85656', '11016aab-fd83-4c1b-a0cf-6de026e5f4ae'),
	(1, '91186d12-c393-47fa-ad75-8d7349f85656', '40b5d8d7-b01c-4674-981f-99cc4bcacd92'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'f9ad834b-4a1d-47f7-99a3-c30df090001a'),
	(1, 'ed6f1c95-4887-4248-9051-9d33218b0e05', '007a587c-b861-413f-bfa6-11f24d58924c');

DELETE FROM `receipt`;
INSERT INTO `receipt` (`b_id`, `created_at`, `b_total`) VALUES
	('0b8f7e46-dd07-48ed-bd48-1e5b29e31b4c', '2024-10-29 21:07:37.800016', 60),
	('4911d574-eb35-468e-91c1-4df0089cdefe', '2024-10-29 17:42:00.235056', 60),
	('4df183d3-2fda-44c8-b253-4121aa8c2446', '2024-10-29 17:31:29.149050', 60),
	('4f43558a-ec13-430c-9d1c-0cb9873e0b24', '2024-10-30 08:47:43.452634', 330),
	('6006fc4b-4289-470a-8c89-cbd71ea945d6', '2024-11-05 13:15:39.200348', 50),
	('741546b6-a5c3-48a0-85d9-d515086691ce', '2024-10-29 17:35:36.194491', 60),
	('76d4cd9c-b320-4064-a375-ce44b8426edd', '2024-10-29 17:37:21.263673', 60),
	('87798dcd-321d-4cdb-b04f-55775f3ce56b', '2025-02-11 21:13:07.852737', 130),
	('8eea6952-551b-41dd-9c1a-df90f03df302', '2024-10-30 07:26:11.595596', 50),
	('906c6793-b0ee-4269-af59-61187216902b', '2025-02-05 21:13:32.372018', 200),
	('97dd69e3-270a-47f2-873c-80b84ba611a9', '2024-10-29 17:32:38.481401', 60),
	('b734a0ae-341f-414c-a84f-bf3f1dc3e1ec', '2024-10-30 09:00:46.337514', 240),
	('bf37d59f-8d86-4717-b74c-a651f3314cb4', '2024-11-05 14:03:13.554523', 135),
	('c6f3484c-3d05-4c38-b00c-9e8777fbcd07', '2024-10-30 08:43:44.251040', 60),
	('d04f11a7-4191-4f12-be71-c325e470008e', '2024-10-29 14:53:39.708959', 60),
	('d34263f4-5bb3-44ec-bea9-0c00bdf364ac', '2024-10-30 09:02:45.245601', 170),
	('d946f7c1-fcbe-4fa4-9e1b-57457c4b2841', '2024-10-29 15:26:48.931710', 60),
	('e447a3a1-03ba-409a-bf89-4b591e5ce915', '2024-10-30 09:03:19.078621', 170);

DELETE FROM `order`;
INSERT INTO `order` (`o_id`, `created_at`, `payment_link`, `o_status`, `o_total`, `updated_at`, `b_id`, `user_id`) VALUES
	('04969a4f-e3f6-47f5-b824-dd14ea99b20e', '2024-10-28 09:02:45.245601', 'https://checkout.stripe.com/c/pay/cs_test_a1Il10E5TGRXorbFNbGnSZIr97YxotxICsKpSSLFupRPcIf8wcuai5ZSKQ#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'CANCEL', 170, '2024-10-30 12:09:00.182181', 'd34263f4-5bb3-44ec-bea9-0c00bdf364ac', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('18e61136-25ce-437d-a380-2b593184ed4b', '2024-10-29 14:53:39.709979', 'https://checkout.stripe.com/c/pay/cs_test_a1ptGa0csi5ZOiIoIeeiYzymXOdbSXO23IhYIskRgshcXSl6JwG1RXQa4M#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'SUCCESS', 60, '2024-10-29 17:26:19.930935', 'd04f11a7-4191-4f12-be71-c325e470008e', 'b0d0b18c-774c-4c4a-9851-5ead92851f6e'),
	('21ac55ae-2ecf-4468-a515-755ad6a616cc', '2024-10-29 17:37:21.269727', 'https://checkout.stripe.com/c/pay/cs_test_a1xskiNNMYrhGv70u97IuyRWUo8MFJMHemjVLCp1WkV4VoGht7U2Gl73Og#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'SUCCESS', 60, '2024-10-29 17:58:26.622905', '76d4cd9c-b320-4064-a375-ce44b8426edd', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('2780baa7-6867-4d51-a735-f87bdef7dab4', '2024-11-05 14:03:13.563225', 'https://checkout.stripe.com/c/pay/cs_test_a1j6oYkTRkKSLwVxbqt7VGoEhcBkYqq5j2WcDgV6zfrIoB4YWqRFHcK2eE#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 135, '2024-11-05 14:03:31.800151', 'bf37d59f-8d86-4717-b74c-a651f3314cb4', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('35ec448b-8aeb-414c-921f-87d198dabbbf', '2024-10-29 15:26:48.931710', 'https://checkout.stripe.com/c/pay/cs_test_a1LBTCXUuCaNnzYwSKpLerYBGJeERLboCWvWNl2AgTsxjvKHzmThLnHrhq#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'CANCEL', 60, '2024-11-03 18:13:14.043597', 'd946f7c1-fcbe-4fa4-9e1b-57457c4b2841', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('4682bbe2-d9e1-4e62-b3ef-afee2d80a384', '2024-10-29 17:32:38.481401', 'https://checkout.stripe.com/c/pay/cs_test_a1Ddrl7TvkEvLoZkqNKt20xgzuXRrwbtbqeZP76zts9z0aQu0Nlk3wMBFU#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'SUCCESS', 60, '2024-10-30 07:28:12.142530', '97dd69e3-270a-47f2-873c-80b84ba611a9', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('4a31f4ea-d0fe-4ce2-b384-26ccaceaaed8', '2024-10-29 17:35:36.196492', 'https://checkout.stripe.com/c/pay/cs_test_a1fgpHv7VTdsl54kEH2XPjgVYS56iA4Y5Jz3S3jqOfBMF8luL4Q4hKC59M#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 60, '2024-10-29 17:35:55.926404', '741546b6-a5c3-48a0-85d9-d515086691ce', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('4b266dfd-857e-4e76-ba87-a116426e5d25', '2024-11-05 13:15:39.216247', 'https://checkout.stripe.com/c/pay/cs_test_a1LssA87NZTRy1eoyQpcbHEHNJhplbtdvZwdIukDcQ2ET2MmcmNyEZa4rd#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 50, '2024-11-05 13:16:09.829869', '6006fc4b-4289-470a-8c89-cbd71ea945d6', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('80b7cfa8-1995-488a-b76a-4bedd318d15c', '2024-10-30 08:47:43.452634', 'https://checkout.stripe.com/c/pay/cs_test_a11RMtE1WCEbiXJeR3FJ5UAFC83X5KxH4ZtrwAWf4CPoDpyHIL993xKR1x#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 330, '2024-10-30 08:48:12.669701', '4f43558a-ec13-430c-9d1c-0cb9873e0b24', 'b2e4894c-4649-49bb-8c52-1a5c04502f4b'),
	('86bb2691-e322-4543-9584-e2f45c9e9727', '2024-10-29 17:31:29.155999', 'https://checkout.stripe.com/c/pay/cs_test_a1gPhrIuzU77ruFyGir5rtwaqi6djAuWbwuhA6tyeulYjFQKFbRsHVby4D#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 60, '2024-10-29 17:31:40.578555', '4df183d3-2fda-44c8-b253-4121aa8c2446', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('8f1a6c31-95a2-45e0-96d7-399ac3d00038', '2024-10-30 07:26:11.611253', 'https://checkout.stripe.com/c/pay/cs_test_a1fhqbzmsnjeRHyTTvGige1ZIe0xoXnEpQsVWfZdtA3FfDVubxOS4lr1Wx#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 50, '2024-10-30 07:26:36.997199', '8eea6952-551b-41dd-9c1a-df90f03df302', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('9a9dd5c5-d59d-4b82-ae45-1f6d57d638c4', '2024-10-30 09:00:46.353218', 'https://checkout.stripe.com/c/pay/cs_test_a1zJedZL40YfbEpQHClKcja90eRv0ZIHNTnas1Rv3P2LNr3D9IuvxsJEAi#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 240, '2024-10-30 09:01:25.597618', 'b734a0ae-341f-414c-a84f-bf3f1dc3e1ec', 'b2e4894c-4649-49bb-8c52-1a5c04502f4b'),
	('a27d9081-625d-4abf-b2d4-d31962c35548', '2025-02-11 21:13:07.878616', 'https://checkout.stripe.com/c/pay/cs_test_a1xYL5O1ZPiPxGc3qVgPggE7md0S8KCJcktaW4YCjpaYYqFIyFFPps8imI#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'PENDING', 130, '2025-02-11 21:13:09.129473', '87798dcd-321d-4cdb-b04f-55775f3ce56b', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('bf2f74e6-350c-4118-b293-8bad5ccf9b57', '2025-02-05 21:13:32.385657', 'https://checkout.stripe.com/c/pay/cs_test_a1QDlKFe1kCluqx5pEG5Snhy0dHylGsIiUCs83z8CYbI6tpuJGfKtg711Y#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 200, '2025-02-05 21:13:48.548026', '906c6793-b0ee-4269-af59-61187216902b', 'b0d0b18c-774c-4c4a-9851-5ead92851f6e'),
	('d05d311f-7f3e-457f-ae17-b6725518816e', '2024-10-29 21:07:37.826221', 'https://checkout.stripe.com/c/pay/cs_test_a1FdrQmC2ewCvJZjtObl8ON3DGmkVPBETeNep1H2bHEIeaJPkxJhuELEnM#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 60, '2024-10-29 21:07:55.064009', '0b8f7e46-dd07-48ed-bd48-1e5b29e31b4c', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('d6061e8b-baeb-40ca-bce2-0a1d1e42031e', '2024-10-30 09:03:19.078621', 'https://checkout.stripe.com/c/pay/cs_test_a1OcepvMkGHkuG5Gd7r1HUM2kqxB3wHx4b4eaN9lvwkydQxM1A7IlxBSFZ#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 170, '2024-10-30 09:03:52.063306', 'e447a3a1-03ba-409a-bf89-4b591e5ce915', '70696dc0-f986-41f1-b59b-276b1c1ca66c'),
	('eabea277-ec96-4eff-b7c8-8b125990b264', '2024-10-30 08:43:44.251040', 'https://checkout.stripe.com/c/pay/cs_test_a1NgyC22i7S4AakfS0rJ4t1uMHekatlDsaZnd7wV7wzPhrW90TjchBnwpK#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'CANCEL', 60, '2024-11-03 18:13:14.046702', 'c6f3484c-3d05-4c38-b00c-9e8777fbcd07', 'b2e4894c-4649-49bb-8c52-1a5c04502f4b'),
	('fcc24975-04c4-4fca-8547-1dee3a86f3b1', '2024-10-29 17:42:00.245679', 'https://checkout.stripe.com/c/pay/cs_test_a1YZdAeKrbsoEVy9DokY7W89VaZqLm8lfFDr6PNRNvyZuS6MYW1vdxbrkD#fidkdWxOYHwnPyd1blpxYHZxWjA0VDNTUVVHZzdrT0dcNlJpUEFqNF9gaWRfY0h2MUlHVHddVVJOZ0RoT0dSTkA9MF93dE98fWh%2FMk5ObFRmN0x8bmJiMzdBUlJRRH1Gd2I3YXE2VGkyfE9INTV1cGd9NzBgPCcpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWAnPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl', 'COMPLETE', 60, '2024-10-29 17:42:30.649680', '4911d574-eb35-468e-91c1-4df0089cdefe', '70696dc0-f986-41f1-b59b-276b1c1ca66c');

DELETE FROM `order_line`;
INSERT INTO `order_line` (`o_qty`, `f_id`, `o_id`) VALUES
	(1, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', '2780baa7-6867-4d51-a735-f87bdef7dab4'),
	(2, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', 'a27d9081-625d-4abf-b2d4-d31962c35548'),
	(1, '1d3392c5-6b8e-4148-a9d5-9d2df91f431d', 'bf2f74e6-350c-4118-b293-8bad5ccf9b57'),
	(1, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', '04969a4f-e3f6-47f5-b824-dd14ea99b20e'),
	(1, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', '4b266dfd-857e-4e76-ba87-a116426e5d25'),
	(3, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', '80b7cfa8-1995-488a-b76a-4bedd318d15c'),
	(1, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', '8f1a6c31-95a2-45e0-96d7-399ac3d00038'),
	(1, '2ac03bed-3f26-4c7c-b808-006e300bbf5a', 'd6061e8b-baeb-40ca-bce2-0a1d1e42031e'),
	(1, '88679d7f-74e7-427b-a62b-e4b54f4d1d3a', '2780baa7-6867-4d51-a735-f87bdef7dab4'),
	(1, '91186d12-c393-47fa-ad75-8d7349f85656', 'bf2f74e6-350c-4118-b293-8bad5ccf9b57'),
	(2, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '04969a4f-e3f6-47f5-b824-dd14ea99b20e'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '18e61136-25ce-437d-a380-2b593184ed4b'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '21ac55ae-2ecf-4468-a515-755ad6a616cc'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '35ec448b-8aeb-414c-921f-87d198dabbbf'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '4682bbe2-d9e1-4e62-b3ef-afee2d80a384'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '4a31f4ea-d0fe-4ce2-b384-26ccaceaaed8'),
	(3, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '80b7cfa8-1995-488a-b76a-4bedd318d15c'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '86bb2691-e322-4543-9584-e2f45c9e9727'),
	(4, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', '9a9dd5c5-d59d-4b82-ae45-1f6d57d638c4'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'bf2f74e6-350c-4118-b293-8bad5ccf9b57'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'd05d311f-7f3e-457f-ae17-b6725518816e'),
	(2, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'd6061e8b-baeb-40ca-bce2-0a1d1e42031e'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'eabea277-ec96-4eff-b7c8-8b125990b264'),
	(1, 'c6b47382-855b-499d-b00a-3b28a3cb4f34', 'fcc24975-04c4-4fca-8547-1dee3a86f3b1');

DELETE FROM `review`;
INSERT INTO `review` 
	(`review_id`, `comment`, `review_date`, `rating`, `customer_id`, `order_id`) VALUES
	('669fdff3-656c-416e-93fc-cf93964c3115', 'fucking shit', '2025-02-08 08:15:16.122175', 5, '70696dc0-f986-41f1-b59b-276b1c1ca66c', '04969a4f-e3f6-47f5-b824-dd14ea99b20e');

DELETE FROM `review`;
INSERT INTO `review` (`review_id`, `comment`, `review_date`, `rating`, `customer_id`, `order_id`, `like_count`) VALUES
	('669fdff3-656c-416e-93fc-cf93964c3115', 'fucking shit', '2025-02-08 08:15:16.122175', 5,
	 '70696dc0-f986-41f1-b59b-276b1c1ca66c', '04969a4f-e3f6-47f5-b824-dd14ea99b20e', 10),
	('b5350ffd-53c9-4477-9964-0cb41134304e', 'Kak sus', '2025-02-24 11:54:29.332677', 3,
	 'b0d0b18c-774c-4c4a-9851-5ead92851f6e', '18e61136-25ce-437d-a380-2b593184ed4b', 0);

DELETE FROM `financial`;
INSERT INTO `financial` (`date`, `expense`, `income`, `total`) VALUES
	('2024-10-29', 6455, 420, -6035),
	('2024-10-30', 8805, 790, -8015),
	('2024-11-05', 11495, 185, -11310),
	('2025-02-05', 5085, 200, -4885);
