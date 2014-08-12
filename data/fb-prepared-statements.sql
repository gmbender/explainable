SELECT R1.icon, R1.pic FROM group_ AS R1 WHERE R1.gid = '$1' AND R1.privacy = 'open';

SELECT R1.administrator, R1.gid, R1.uid FROM group_member AS R1 WHERE R1.gid = '$1' AND R1.uid = '$2' AND EXISTS (SELECT DISTINCT 1 FROM group_ AS R2, group_member AS R3, friend AS R4, group_member AS R5 WHERE R1.gid = R2.gid AND R2.privacy = 'closed' AND R1.gid = R3.gid AND R3.uid = R4.uid1 AND R4.uid2 = 4 AND R1.gid = R5.gid AND R5.uid = 4);

SELECT R1.latitude, R1.longitude FROM location_post AS R1 WHERE R1.latitude = '$1' AND R1.longitude = '$2' AND R1.type = 'checkin' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.author_uid = R2.uid2);

SELECT R1.coords, R1.message, R1.post_id, R1.tagged_uids FROM location_post AS R1 WHERE R1.author_uid = '$1' AND R1.page_id = '$2' AND R1.type = 'video' AND EXISTS (SELECT DISTINCT 1 FROM location_post_tagged_uids AS R2, friend AS R3 WHERE R1.id = R2.id AND R2.uid = 4 AND R3.uid1 = 4 AND R3.uid2 = 4);

SELECT R1.can_upload, R1.description, R1.edit_link, R1.video_count FROM album AS R1 WHERE aid = '$1' AND R1.visible = 'friends-of-friends' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2, friend AS R3 WHERE R2.uid2 = 4 AND R2.uid1 = R3.uid1 AND R3.uid2 = 4);

SELECT R1.id, R1.uid FROM location_post_tagged_uids AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.latitude FROM location_post AS R1 WHERE R1.post_id = '$1' AND R1.author_uid = 4 AND R1.type = 'photo';

SELECT R1.id, R1.uid FROM location_post_tagged_uids AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.uid = R2.uid2);

SELECT R1.longitude, R1.tagged_uids FROM location_post AS R1 WHERE R1.id = '$1' AND R1.type = 'video' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.author_uid = R2.uid2);

SELECT 1 FROM location_post AS R1 WHERE R1.author_uid = '$1' AND R1.page_id = '$2' AND R1.type = 'photo' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.author_uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.id, R1.uid FROM location_post_tagged_uids AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.uid = R2.uid2);

SELECT R1.eid, R1.eid_cursor, R1.inviter, R1.start_time, R1.uid_cursor FROM event_member AS R1 WHERE R1.uid = 4 AND R1.eid = '$1';

SELECT R1.created_time, R1.embed_html, R1.length, R1.link FROM video AS R1 WHERE R1.owner = '$1' AND EXISTS (SELECT DISTINCT 1 FROM album AS R2, friend AS R3 WHERE R1.album_id = R2.aid AND R2.visible = 'friends' AND R2.owner = R3.uid1 AND R3.uid2 = 4);

SELECT R1.id FROM location_post_tagged_uids AS R1 WHERE R1.uid = 4;

SELECT R1.created_time, R1.owner, R1.updated_time FROM question AS R1 WHERE R1.owner = '$1' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.owner = R2.uid2);

SELECT R1.eid, R1.start_time, R1.uid, R1.uid_cursor FROM event_member AS R1 WHERE eid = '$1' AND inviter = '$2' AND EXISTS (SELECT DISTINCT 1 FROM event AS R2, friend AS R3, event_member AS R4 WHERE R1.eid = R2.eid AND R2.privacy = 'friends' AND R3.uid1 = 4 AND R1.uid = R3.uid2 AND R1.eid = R4.eid AND R4.uid = 4);

SELECT R1.bookmark_order, R1.gid, R1.positions, R1.unread FROM group_member AS R1 WHERE administrator = '$1' AND EXISTS (SELECT DISTINCT 1 FROM group_ AS R2, group_member AS R3, friend AS R4, group_member AS R5 WHERE R1.gid = R2.gid AND R2.privacy = 'closed' AND R1.gid = R3.gid AND R4.uid1 = 4 AND R3.uid = R4.uid2 AND R1.gid = R5.gid AND R5.uid = 4);

SELECT R1.height, R1.photo_id, R1.size, R1.src, R1.width FROM photo_src AS R1 WHERE photo_id = '$1' AND size = '$2' AND EXISTS (SELECT DISTINCT 1 FROM photo AS R2, album AS R3, friend AS R4 WHERE R1.photo_id = R2.pid AND R2.aid = R3.aid AND R3.visible = 'friends' AND R3.owner = R4.uid1 AND R4.uid2 = 4);

SELECT R1.id, R1.longitude FROM location_post AS R1 WHERE R1.type = 'status' AND author_uid = '$1' AND app_id = '$2' AND EXISTS (SELECT DISTINCT 1 FROM location_post_tagged_uids AS R2, friend AS R3, friend AS R4 WHERE R1.id = R2.id AND R2.uid = R3.uid1 AND R3.uid2 = 4 AND R2.uid = R4.uid1 AND R4.uid2 = 4);

SELECT R1.like_info FROM album AS R1 WHERE aid = '$1' AND size = '$2' AND R1.visible = 'friends' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.owner = R2.uid2);

SELECT R1.message, R1.target_type FROM checkin AS R1 WHERE checkin_id = '$1' AND R1.author_uid = 4;

SELECT R1.eid, R1.uid_cursor FROM event_member AS R1 WHERE eid = '$1' AND R1.uid = 4;

SELECT R1.author_uid, R1.message FROM location_post AS R1 WHERE id = '$1' AND latitude = '$2' AND longitude = '$3' AND R1.type = 'checkin' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.author_uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.can_invite_friends, R1.pic_small, R1.unsure_count FROM event AS R1 WHERE creator = '$1' AND name = '$2' AND EXISTS (SELECT DISTINCT 1 FROM event_member AS R2, event AS R3, friend AS R4 WHERE R1.eid = R2.eid AND R2.uid = 4 AND R1.eid = R3.eid AND R3.privacy = 'friends' AND R4.uid1 = 4 AND R4.uid2 = 4);

SELECT R1.id, R1.latitude, R1.page_id, R1.page_type, R1.tagged_uids FROM location_post AS R1 WHERE post_id = '$1' AND R1.author_uid = 4 AND R1.type = 'status';

SELECT R1.unsure_count FROM event AS R1 WHERE creator = '$1' AND venue = '$2' AND EXISTS (SELECT DISTINCT 1 FROM event_member AS R2 WHERE R1.eid = R2.eid AND R2.uid = 4);

SELECT R1.album_id, R1.thumbnail_link FROM video AS R1 WHERE vid = '$1' AND EXISTS (SELECT DISTINCT 1 FROM album AS R2, friend AS R3, friend AS R4 WHERE R1.album_id = R2.aid AND R2.visible = 'friends-of-friends' AND R3.uid1 = 4 AND R3.uid2 = R4.uid1 AND R4.uid2 = 4);

SELECT R1.app_id, R1.checkin_id, R1.post_id, R1.tagged_uids, R1.timestamp FROM checkin AS R1 WHERE checkin_id = '$1' AND R1.author_uid = 4;

SELECT R1.bookmark_order, R1.uid FROM group_member AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM group_ AS R2, group_member AS R3, friend AS R4, group_member AS R5, friend AS R6 WHERE R1.gid = R2.gid AND R2.privacy = 'closed' AND R1.gid = R3.gid AND R4.uid1 = 4 AND R3.uid = R4.uid2 AND R1.gid = R5.gid AND R5.uid = R6.uid1 AND R6.uid2 = 4);

SELECT R1.id, R1.uid FROM location_post_tagged_uids AS R1 WHERE id = '$1' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.uid = R2.uid2);

SELECT R1.can_tag, R1.caption_tags, R1.src, R1.target_id, R1.target_type FROM photo AS R1 WHERE album_object_id = '#1' AND pid = '$2' AND EXISTS (SELECT DISTINCT 1 FROM album AS R2, friend AS R3, friend AS R4 WHERE R1.aid = R2.aid AND R2.visible = 'friends-of-friends' AND R3.uid2 = 4 AND R3.uid1 = R4.uid1 AND R4.uid2 = 4);

SELECT R1.can_upload, R1.comment_info, R1.description, R1.modified, R1.object_id FROM album AS R1 WHERE object_id = '$1' AND R1.visible = 'friends-of-friends' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2, friend AS R3 WHERE R2.uid2 = 4 AND R2.uid1 = R3.uid1 AND R3.uid2 = 4);

SELECT R1.app_id, R1.checkin_id, R1.coords FROM checkin AS R1 WHERE target_type = '$1' AND app_id = '$2' AND R1.author_uid = 4;

SELECT R1.message, R1.post_id, R1.tagged_uids FROM location_post AS R1 WHERE R1.type = 'photo' AND latitude = '$1' AND longitude = '$2' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.author_uid = R2.uid2);

SELECT R1.latitude, R1.longitude, R1.timestamp FROM location_post AS R1 WHERE R1.type = 'checkin' AND page_id = '$1' AND EXISTS (SELECT DISTINCT 1 FROM location_post_tagged_uids AS R2, friend AS R3 WHERE R1.id = R2.id AND R2.uid = 4 AND R3.uid1 = 4 AND R3.uid2 = 4);

SELECT R1.can_backdate, R1.is_user_facing, R1.like_info, R1.modified_major, R1.object_id, R1.photo_count FROM album AS R1 WHERE location = '$1' AND type = '$2' AND R1.visible = 'friends' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.owner = R2.uid1 AND R2.uid2 = 4);

SELECT R1.rsvp_status FROM event_member AS R1 WHERE inviter_type = '$1' AND EXISTS (SELECT DISTINCT 1 FROM event AS R2, friend AS R3, event_member AS R4 WHERE R1.eid = R2.eid AND R2.privacy = 'friends' AND R1.uid = R3.uid1 AND R3.uid2 = 4 AND R1.eid = R4.eid AND R4.uid = 4);

SELECT R1.photo_id, R1.size FROM photo_src AS R1 WHERE width = '$1' AND height = '$2' AND EXISTS (SELECT DISTINCT 1 FROM photo AS R2, album AS R3, friend AS R4, friend AS R5 WHERE R1.photo_id = R2.pid AND R2.aid = R3.aid AND R3.visible = 'friends-of-friends' AND R4.uid2 = 4 AND R4.uid1 = R5.uid1 AND R5.uid2 = 4);

SELECT R1.aid, R1.link, R1.place_id, R1.src_big_width, R1.src_small, R1.src_small_webp, R1.src_webp FROM photo AS R1 WHERE owner = '$1' AND pid = '$2' AND EXISTS (SELECT DISTINCT 1 FROM album AS R2, friend AS R3 WHERE R1.aid = R2.aid AND R2.visible = 'friends' AND R3.uid1 = 4 AND R2.owner = R3.uid2);

SELECT R1.id, R1.message, R1.page_id FROM location_post AS R1 WHERE page_id = '$1' AND R1.author_uid = 4 AND R1.type = 'status';

SELECT R1.created_time, R1.like_info, R1.title FROM note AS R1 WHERE note_id = '$1' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.url, R1.user_id FROM url_like AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.user_id = R2.uid2);

SELECT R1.location, R1.photo_count, R1.type FROM album AS R1 WHERE owner = '$1' AND R1.visible = 'everyone';

SELECT R1.page_type, R1.post_id FROM location_post AS R1 WHERE page_id = '$1' AND R1.type = 'photo' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.author_uid = R2.uid2);

SELECT R1.author_uid, R1.coords, R1.id, R1.longitude, R1.page_id, R1.page_type, R1.post_id, R1.tagged_uids, R1.timestamp FROM location_post AS R1 WHERE R1.type = 'video' AND EXISTS (SELECT DISTINCT 1 FROM location_post_tagged_uids AS R2, friend AS R3 WHERE R1.id = R2.id AND R2.uid = 4 AND R3.uid1 = 4 AND R3.uid2 = 4);

SELECT R1.app_id, R1.longitude, R1.page_type FROM location_post AS R1 WHERE id = '$1' AND R1.type = 'photo' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.author_uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.created_time, R1.id, R1.is_published, R1.owner, R1.question, R1.updated_time FROM question AS R1 WHERE EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.owner = R2.uid2);

SELECT R1.app_id, R1.author_uid, R1.checkin_id, R1.coords, R1.message, R1.post_id, R1.target_type, R1.timestamp FROM checkin AS R1 WHERE target_id = '$1' AND app_id = '$2' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R1.author_uid = R2.uid1 AND R2.uid2 = 4);

SELECT R1.aid, R1.album_object_id, R1.can_delete, R1.src_width FROM photo AS R1 WHERE caption = '$1' AND link = '$2' AND EXISTS (SELECT DISTINCT 1 FROM album AS R2, friend AS R3, friend AS R4 WHERE R1.aid = R2.aid AND R2.visible = 'friends-of-friends' AND R3.uid1 = 4 AND R3.uid2 = R4.uid1 AND R4.uid2 = 4);

SELECT 1 FROM url_like AS R1 WHERE url = '$1' AND EXISTS (SELECT DISTINCT 1 FROM friend AS R2 WHERE R2.uid1 = 4 AND R1.user_id = R2.uid2);
