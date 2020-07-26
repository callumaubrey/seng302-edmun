/*
This script simply disables all foreign key checks and then deletes all the data from
all the necessary tables
*/

/*
There are a few tables that don't need to be wiped as they are read only
These are the following:
    country
    hibernate_sequence
    role
*/

-- Disables foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `activity`;
TRUNCATE TABLE `activity_activity_types`;
TRUNCATE TABLE `activity_history`;
TRUNCATE TABLE `activity_role`;
TRUNCATE TABLE `activity_tags`;
TRUNCATE TABLE `email`;
TRUNCATE TABLE `named_location`;
TRUNCATE TABLE `profile`;
TRUNCATE TABLE `profile_activity_types`;
TRUNCATE TABLE `profile_emails`;
TRUNCATE TABLE `profile_subscriptions`;
TRUNCATE TABLE `subscription_history`;
TRUNCATE TABLE `tag`;
TRUNCATE TABLE `users_countries`;
TRUNCATE TABLE `users_roles`;

-- Enables foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
