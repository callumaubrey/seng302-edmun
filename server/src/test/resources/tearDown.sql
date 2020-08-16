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

-- Please list tables in alphabetical order
truncate table `activity`;
truncate table `activity_activity_types`;
truncate table `activity_history`;
truncate table `activity_qualification_metric`;
truncate table `activity_result`;
truncate table `activity_role`;
truncate table `activity_tags`;
truncate table `email`;
truncate table `named_location`;
truncate table `profile`;
truncate table `profile_activity_types`;
truncate table `profile_emails`;
truncate table `profile_subscriptions`;
truncate table `subscription_history`;
truncate table `tag`;
truncate table `users_countries`;
truncate table `users_roles`;

-- Enables foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
