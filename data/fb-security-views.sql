-- List of security views for query analysis benchmarks based on the FQL schema.

-- To read the album table you need
-- any valid access_token if it is public.
-- user_photos permissions if it is not public and belongs to the user.
-- friends_photos permissions if it is not public and belongs to a user's friend.
SELECT * FROM album WHERE visible = 'everyone';

SELECT * FROM album WHERE visible = 'friends'
   AND owner IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM album WHERE visible = 'friends'
   AND owner IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM album WHERE visible = 'friends-of-friends'
   AND EXISTS (SELECT * FROM friend f1, friend f2
                WHERE f1.uid1 = me() AND f1.uid2 = f2.uid1 AND f2.uid2 = me());

SELECT * FROM album WHERE visible = 'friends-of-friends'
   AND EXISTS (SELECT * FROM friend f1, friend f2
                WHERE f1.uid1 = me() AND f1.uid2 = f2.uid2 AND f2.uid1 = me());

SELECT * FROM album WHERE visible = 'friends-of-friends'
   AND EXISTS (SELECT * FROM friend f1, friend f2
                WHERE f1.uid2 = me() AND f1.uid1 = f2.uid1 AND f2.uid2 = me());

SELECT * FROM album WHERE visible = 'friends-of-friends'
   AND EXISTS (SELECT * FROM friend f1, friend f2
                WHERE f1.uid2 = me() AND f1.uid1 = f2.uid2 AND f2.uid1 = me());

-- To read the application table you need
-- no access_token for all publicly available properties (indicated in the table below).
-- an app access_token for all properties for that app.
SELECT * FROM application WHERE app_id = 12345;

SELECT android_key_hash, api_key, app_id, app_name, app_type, appcenter_icon_url,
       category, client_config, company_name, configured_ios_sso, daily_active_users,
       daily_active_users_rank, description, developers, display_name, gdpv4_nux_enabled,
       icon_url, ios_bundle_id, is_facebook_app, link, logo_url, migration_status,
       mobile_profile_section_url, monthly_active_users, monthly_active_users_rank,
       namespace, page_tab_url, profile_section_url, restriction_info, subcategory,
       supports_attribution, supports_implicit_sdk_logging, weekly_active_users
  FROM application;

-- To read checkins you need
-- the user_status permission to read the user's checkins.
-- the friends_status permission to read the user's friend's checkins.
SELECT * FROM checkin WHERE author_uid = me();

SELECT * FROM checkin WHERE author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM checkin WHERE author_uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the event table you need:
-- a generic access_token for public events (those whose privacy is set to OPEN)
-- a user access_token with user_events permission for a user who can see the event for non-public events
-- an app access_token with user_events permission (for non-public events, must be the app that created the event)
-- a page access_token with user_events permission (for non-public events, must be the page that created the event)
SELECT * FROM event WHERE privacy = 'open';

SELECT * FROM event WHERE eid IN (SELECT eid FROM event_member WHERE uid = me());

SELECT * FROM event WHERE app_id = 12345;

-- To read the event_member table you need
-- A generic access_token for public events (those whose privacy is set to OPEN)
-- The user_events permission for non-public events or to see all events a user is attending
-- The friends_events permission for non-public events of the user's friends or to see all events your app's user's friends are attending
SELECT * FROM event_member WHERE eid IN (SELECT eid FROM event WHERE privacy = 'open');

SELECT * FROM event_member WHERE uid = me();

SELECT * FROM event_member
 WHERE eid IN (SELECT eid FROM event WHERE privacy = 'friends')
   AND uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM event_member
 WHERE eid IN (SELECT eid FROM event WHERE privacy = 'friends')
   AND uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the friend table you need
-- any valid access_token to retrieve friends of the current session user
-- This is the only user that this table can be queried for, the friends of friends cannot be retrieved.

-- Note: I ignore the last restriction because it's possible to configure privacy settings so that
-- certain data is only made available to the current user, his friends, and their friends.
SELECT * FROM friend WHERE uid1 = me();

SELECT * FROM friend WHERE uid2 = me();

SELECT * FROM friend WHERE uid1 IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM friend WHERE uid1 IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM friend WHERE uid2 IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM friend WHERE uid2 IN (SELECT uid2 FROM friend WHERE uid1 = me());

-- To read the group table you need
-- any valid access_token if the group is public (i.e. the group's privacy setting is OPEN)
-- user_groups permission for a user's non-public groups or to see the bookmark_order or unread fields for a user's groups
-- friends_groups permission to see a user's friend's group membership

-- Note: The last "bookmark_order" and "unread" fields live in the group_member relation rather
-- than the group relation
SELECT * FROM group_ WHERE privacy = 'open';

SELECT * FROM group_ WHERE gid IN (SELECT gid FROM group_member WHERE uid = me());

SELECT * FROM group_ WHERE privacy = 'closed' AND gid IN
    (SELECT gid FROM group_member WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()));

SELECT * FROM group_ WHERE privacy = 'closed' AND gid IN
    (SELECT gid FROM group_member WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me()));

-- To read the group_member table you need:
-- any valid access_token if the group is public (i.e. the group's privacy setting is OPEN)
-- user_groups permission for a user's non-public groups or to see the bookmark_order or unread fields for a user's groups
-- friends_groups permission for a user's friend's non-public groups
SELECT administrator, gid, positions, uid FROM group_member
 WHERE gid IN (SELECT gid FROM group_ WHERE privacy = 'open');

SELECT * FROM group_member WHERE gid IN
    (SELECT gid FROM group_ WHERE gid IN
        (SELECT gid FROM group_member WHERE uid = me()));

SELECT administrator, gid, positions, uid FROM group_member WHERE gid IN
    (SELECT gid FROM group_ WHERE privacy = 'closed' AND gid IN
        (SELECT gid FROM group_member WHERE uid IN (
            SELECT uid2 FROM friend WHERE uid1 = me())));

SELECT administrator, gid, positions, uid FROM group_member WHERE gid IN
    (SELECT gid FROM group_ WHERE privacy = 'closed' AND gid IN
        (SELECT gid FROM group_member WHERE uid IN (
            SELECT uid1 FROM friend WHERE uid2 = me())));

-- An FQL table that returns Posts that have locations associated with them and that satisfy at least one of the following conditions:
--  * you were tagged in the Post
--  * a friend was tagged in the Post
--  * you authored the Post
--  * a friend authored the Post
-- To read Posts with location information, you need:
--  * user_photos or friends_photos for Photos
--  * user_status or friends_status for Posts (not including Photos)
--  * Requesting the user_status or friends_status permissions will return information about checkins as well as other types of objects.
SELECT * FROM location_post WHERE type = 'photo'
   AND author_uid = me();

SELECT * FROM location_post WHERE type = 'checkin'
   AND author_uid = me();

SELECT * FROM location_post WHERE type = 'video'
   AND author_uid = me();

SELECT * FROM location_post WHERE type = 'status'
   AND author_uid = me();

SELECT * FROM location_post WHERE type = 'photo'
   AND author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM location_post WHERE type = 'photo'
   AND author_uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM location_post WHERE type = 'checkin'
   AND author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM location_post WHERE type = 'checkin'
   AND author_uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM location_post WHERE type = 'video'
   AND author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM location_post WHERE type = 'video'
   AND author_uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM location_post WHERE type = 'status'
   AND author_uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM location_post WHERE type = 'status'
   AND author_uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

SELECT * FROM location_post WHERE type ='photo'
   AND id IN (SELECT id FROM location_post_tagged_uids WHERE uid = me());

SELECT * FROM location_post WHERE type ='checkin'
   AND id IN (SELECT id FROM location_post_tagged_uids WHERE uid = me());

SELECT * FROM location_post WHERE type ='video'
   AND id IN (SELECT id FROM location_post_tagged_uids WHERE uid = me());

SELECT * FROM location_post WHERE type ='status'
   AND id IN (SELECT id FROM location_post_tagged_uids WHERE uid = me());

SELECT * FROM location_post WHERE type ='photo'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()));

SELECT * FROM location_post WHERE type ='photo'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me()));

SELECT * FROM location_post WHERE type ='checkin'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()));

SELECT * FROM location_post WHERE type ='checkin'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me()));

SELECT * FROM location_post WHERE type ='video'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()));

SELECT * FROM location_post WHERE type ='video'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me()));

SELECT * FROM location_post WHERE type ='status'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()));

SELECT * FROM location_post WHERE type ='status'
   AND id IN (SELECT id FROM location_post_tagged_uids
               WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me()));

SELECT * FROM location_post_tagged_uids WHERE uid = me();

SELECT * FROM location_post_tagged_uids WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM location_post_tagged_uids WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the note table you need
-- any valid access_token if it is public note written by a page.
-- user_notes permissions if it is a written by the user.
-- friend_notes permissions if it is a written by the user's friend.

-- Note: There's no security view defined for public notes because I don't see any way to check the
-- privacy settings of notes.
SELECT * FROM note WHERE uid = me();

SELECT * FROM note WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM note WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the photo table you need
-- any valid access_token if it is public and owned by the Page.
-- user_photos permissions to access photos and albums uploaded by the user, and photos in which the user has been tagged.
-- friends_photos permissions to access friends' photos and photos in which the user's friends have been tagged.

-- Note: It's not actually clear where the public/private permissions come from. Here, I'm assuming
-- that photos inherit permissions from the album that they belong to. If a user is sufficiently
-- privileged that he can ascertain an album's existence then he can also look at photos in that
-- album.
SELECT * FROM photo WHERE aid IN (SELECT aid FROM album);

-- Similarly, permissions for photo_src and photo_tag inherit from the corresponding row in photo
SELECT * FROM photo_src WHERE photo_id IN (SELECT pid FROM photo);

SELECT * FROM photo_tag WHERE pid IN (SELECT pid FROM photo);

-- To read from the question table you need the following permissions:
-- user_questions for questions asked by the current user.
-- friends_questions for questions asked by friends of the current user.
SELECT * FROM question WHERE owner = me();

SELECT * FROM question WHERE owner IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM question WHERE owner IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the url_like table you need
-- user_likes permissions for all Open Graph URLs liked by the current session user
-- friend_likes permissions for all Open Graph URLs like by friends of the current session user
SELECT * FROM url_like WHERE user_id = me();

SELECT * FROM url_like WHERE user_id IN (SELECT uid2 FROM friend WHERE uid1 = me());

SELECT * FROM url_like WHERE user_id IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- To read the user table you need
-- no access_token for publicly available data: uid, name, first_name, middle_name, last_name, sex, locale, pic_small_with_logo, pic_big_with_logo, pic_square_with_logo, pic_with_logo, username
-- any valid access_token for additional user data, except for the follow-on exceptions:
-- user_likes or friends_likes permissions to view: movies, music, books, tv, languages, quotes, games, sports, favorite_athletes, favorite_teams, inspirational_people, likes_count
-- user_education_history or friends_education_history permissions to view: hs_info, education_history, education
-- user_work_history or friends_work_history permissions to view: work_history, work
-- user_religion_politics or friends_religion_politics permissions to view: political, religion
-- user_about_me or friends_about_me permissions to view: about_me
-- user_notes or friends_notes permissions to view: notes_count
-- user_status or friends_status permissions to view: status
-- user_online_presence or friends_online_presence permissions to view: online_presence
-- email permissions to view: proxied_email, contact_email
-- user_birthday or friends_birthday permissions to view: birthday, birthday_date
-- user_hometown or friends_hometown permissions to view: hometown_location
-- user_relationships or friends_relationships permissions to view: family, significant_other_id
-- user_relationship_details or friends_relationship_details permissions to view: meeting_sex, meeting_for
-- user_activities or friends_activities permissions to view: activities
-- user_interests or friends_interests permissions to view: interests
-- user_location or friends_location permissions to view: current_location
-- user_website or friends_website permissions to view: website
--SELECT uid, name, first_name, middle_name, last_name, sex, locale, pic_small_with_logo,
--       pic_big_with_logo, pic_square_with_logo, pic_with_logo, username
--  FROM user;
--
--SELECT uid, wall_count, is_verified, relationship_status, allowed_restrictions, timezone, can_message,
--       verified, pic_big, search_tokens, affiliations, sort_last_name, has_timeline, install_type,
--       email, can_post, devices, payment_pricepoints, video_upload_limits,
--       friend_request_count, profile_blurb, pic_small, pic_cover, profile_update_time, currency,
--       is_blocked, friend_count, is_app_user, pic_square, email_hashes, profile_url,
--       current_address, name_format, sort_first_name, pic, shipping_information,
--       mutual_friend_count, age_range, third_party_id, subscriber_count, payment_instruments,
--       security_settings, viewer_can_send_gift
--  FROM user;
--
--SELECT uid, movies, music, books, tv, languages, quotes, sports, inspirational_people, likes_count
--  FROM user
--WHERE uid = me();
--
--SELECT uid, movies, music, books, tv, languages, quotes, sports, inspirational_people, likes_count
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, movies, music, books, tv, languages, quotes, sports, inspirational_people, likes_count
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, education
--  FROM user
--WHERE uid = me();
--
--SELECT uid, education
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, education
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, work
--  FROM user
--WHERE uid = me();
--
--SELECT uid, work
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, work
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, political, religion
--  FROM user
--WHERE uid = me();
--
--SELECT uid, political, religion
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, political, religion
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, about_me
--  FROM user
--WHERE uid = me();
--
--SELECT uid, notes_count
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, notes_count
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, status
--  FROM user
--WHERE uid = me();
--
--SELECT uid, status
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, status
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, online_presence
--  FROM user
--WHERE uid = me();
--
--SELECT uid, online_presence
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, online_presence
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, email, proxied_email, contact_email
--  FROM user
--WHERE uid = me();
--
--SELECT uid, email, proxied_email, contact_email
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, email, proxied_email, contact_email
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, birthday, birthday_date
--  FROM user
--WHERE uid = me();
--
--SELECT uid, birthday, birthday_date
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, birthday, birthday_date
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, hometown_location
--  FROM user
--WHERE uid = me();
--
--SELECT uid, hometown_location
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, hometown_location
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, significant_other_id
--  FROM user
--WHERE uid = me();
--
--SELECT uid, significant_other_id
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, significant_other_id
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, meeting_sex, meeting_for
--  FROM user
--WHERE uid = me();
--
--SELECT uid, meeting_sex, meeting_for
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, meeting_sex, meeting_for
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, activities
--  FROM user
--WHERE uid = me();
--
--SELECT uid, activities
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, activities
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, interests
--  FROM user
--WHERE uid = me();
--
--SELECT uid, interests
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, interests
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, current_location
--  FROM user
--WHERE uid = me();
--
--SELECT uid, current_location
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, current_location
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());
--
--SELECT uid, website
--  FROM user
--WHERE uid = me();
--
--SELECT uid, website
--  FROM user
--WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me());
--
--SELECT uid, website
--  FROM user
--WHERE uid IN (SELECT uid1 FROM friend WHERE uid2 = me());

-- Policies for video and video_tag are inherited from album; see the comments for the photo
-- relation above for details
SELECT * FROM video WHERE album_id IN (SELECT aid FROM album);

SELECT * FROM video_tag WHERE vid IN (SELECT vid FROM video);