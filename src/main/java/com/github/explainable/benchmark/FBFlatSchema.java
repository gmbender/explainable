/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.explainable.benchmark;

import com.github.explainable.sql.Schema;
import com.github.explainable.sql.table.TypedRelation;
import com.github.explainable.sql.table.TypedRelationImpl;
import com.github.explainable.sql.type.TypeSystem;

import static com.github.explainable.sql.type.TypeSystem.bool;
import static com.github.explainable.sql.type.TypeSystem.numeric;
import static com.github.explainable.sql.type.TypeSystem.primitive;
import static com.github.explainable.sql.type.TypeSystem.primitiveBottom;
import static com.github.explainable.sql.type.TypeSystem.string;

/**
 * Flattened version of the FQL schema. Used for performance benchmarks.
 */
public final class FBFlatSchema {
	public static final TypedRelation ALBUM = TypedRelationImpl.builder()
			.setName("album")
			.addColumn("aid", TypeSystem.string())
			.addColumn("backdated_time", TypeSystem.numeric())
			.addColumn("can_backdate", TypeSystem.bool())
			.addColumn("can_upload", TypeSystem.bool())
			.addColumn("comment_info", TypeSystem.primitive())
			.addColumn("cover_object_id", TypeSystem.string())
			.addColumn("cover_pid", TypeSystem.string())
			.addColumn("created", TypeSystem.numeric())
			.addColumn("description", TypeSystem.string())
			.addColumn("edit_link", TypeSystem.string())
			.addColumn("is_user_facing", TypeSystem.bool())
			.addColumn("like_info", TypeSystem.primitive())
			.addColumn("link", TypeSystem.string())
			.addColumn("location", TypeSystem.string())
			.addColumn("modified", TypeSystem.numeric())
			.addColumn("modified_major", TypeSystem.numeric())
			.addColumn("name", TypeSystem.string())
			.addColumn("object_id", TypeSystem.primitive())
			.addColumn("owner", TypeSystem.primitive())
			.addColumn("owner_cursor", TypeSystem.string())
			.addColumn("photo_count", TypeSystem.numeric())
			.addColumn("place_id", TypeSystem.string())
			.addColumn("type", TypeSystem.string())
			.addColumn("video_count", TypeSystem.numeric())
			.addColumn("visible", TypeSystem.string())
			.addColumn("size", TypeSystem.numeric())
			.build();

	public static final TypedRelation APPLICATION = TypedRelationImpl.builder()
			.setName("application")
			.addColumn("android_key_hash", TypeSystem.primitive())
			.addColumn("api_key", TypeSystem.string())
			.addColumn("app_domains", TypeSystem.primitive())
			.addColumn("app_id", TypeSystem.string())
			.addColumn("app_name", TypeSystem.string())
			.addColumn("app_type", TypeSystem.bool())
			.addColumn("appcenter_icon_url", TypeSystem.string())
			.addColumn("auth_dialog_data_help_url", TypeSystem.string())
			.addColumn("auth_dialog_headline", TypeSystem.string())
			.addColumn("auth_dialog_perms_explanation", TypeSystem.string())
			.addColumn("auth_referral_default_activity_privacy", TypeSystem.string())
			.addColumn("auth_referral_enabled", TypeSystem.bool())
			.addColumn("auth_referral_extended_perms", TypeSystem.primitive())
			.addColumn("auth_referral_friend_perms", TypeSystem.primitive())
			.addColumn("auth_referral_response_type", TypeSystem.string())
			.addColumn("auth_referral_user_perms", TypeSystem.primitive())
			.addColumn("canvas_fluid_height", TypeSystem.bool())
			.addColumn("canvas_fluid_width", TypeSystem.bool())
			.addColumn("canvas_url", TypeSystem.string())
			.addColumn("category", TypeSystem.string())
			.addColumn("client_config", TypeSystem.primitive())
			.addColumn("company_name", TypeSystem.string())
			.addColumn("configured_ios_sso", TypeSystem.bool())
			.addColumn("contact_email", TypeSystem.string())
			.addColumn("created_time", TypeSystem.string())
			.addColumn("creator_uid", TypeSystem.string())
			.addColumn("daily_active_users", TypeSystem.string())
			.addColumn("daily_active_users_rank", TypeSystem.string())
			.addColumn("deauth_callback_url", TypeSystem.string())
			.addColumn("description", TypeSystem.string())
			.addColumn("developers", TypeSystem.primitive())
			.addColumn("display_name", TypeSystem.string())
			.addColumn("gdpv4_nux_enabled", TypeSystem.bool())
			.addColumn("hosting_url", TypeSystem.string())
			.addColumn("icon_url", TypeSystem.string())
			.addColumn("ios_bundle_id", TypeSystem.primitive())
			.addColumn("ipad_app_store_id", TypeSystem.string())
			.addColumn("iphone_app_store_id", TypeSystem.string())
			.addColumn("is_facebook_app", TypeSystem.bool())
			.addColumn("link", TypeSystem.string())
			.addColumn("logo_url", TypeSystem.string())
			.addColumn("migration_status", TypeSystem.primitive())
			.addColumn("mobile_profile_section_url", TypeSystem.string())
			.addColumn("mobile_web_url", TypeSystem.string())
			.addColumn("monthly_active_users", TypeSystem.string())
			.addColumn("monthly_active_users_rank", TypeSystem.string())
			.addColumn("namespace", TypeSystem.string())
			.addColumn("page_tab_default_name", TypeSystem.string())
			.addColumn("page_tab_url", TypeSystem.string())
			.addColumn("privacy_policy_url", TypeSystem.string())
			.addColumn("profile_section_url", TypeSystem.string())
			.addColumn("restriction_info", TypeSystem.primitive())
			.addColumn("secure_canvas_url", TypeSystem.string())
			.addColumn("secure_page_tab_url", TypeSystem.string())
			.addColumn("server_ip_whitelist", TypeSystem.string())
			.addColumn("social_discovery", TypeSystem.bool())
			.addColumn("subcategory", TypeSystem.string())
			.addColumn("supports_attribution", TypeSystem.bool())
			.addColumn("supports_implicit_sdk_logging", TypeSystem.bool())
			.addColumn("terms_of_service_url", TypeSystem.string())
			.addColumn("url_scheme_suffix", TypeSystem.string())
			.addColumn("weekly_active_users", TypeSystem.string())
			.build();

	public static final TypedRelation CHECKIN = TypedRelationImpl.builder()
			.setName("checkin")
			.addColumn("app_id", TypeSystem.primitive())
			.addColumn("author_uid", TypeSystem.primitive())
			.addColumn("checkin_id", TypeSystem.primitive())
			.addColumn("coords", TypeSystem.primitive())
			.addColumn("message", TypeSystem.string())
			.addColumn("post_id", TypeSystem.string())
			.addColumn("tagged_uids", TypeSystem.primitive())
			.addColumn("target_id", TypeSystem.primitive())
			.addColumn("target_type", TypeSystem.string())
			.addColumn("timestamp", TypeSystem.numeric())
			.build();

	public static final TypedRelation EVENT = TypedRelationImpl.builder()
			.setName("event")
			.addColumn("all_members_count", TypeSystem.numeric())
			.addColumn("app_id", TypeSystem.string())
			.addColumn("attending_count", TypeSystem.numeric())
			.addColumn("can_invite_friends", TypeSystem.bool())
			.addColumn("creator", TypeSystem.primitive())
			.addColumn("creator_cursor", TypeSystem.string())
			.addColumn("declined_count", TypeSystem.numeric())
			.addColumn("description", TypeSystem.string())
			.addColumn("eid", TypeSystem.primitive())
			.addColumn("end_time", TypeSystem.string())
			.addColumn("feed_targeting", TypeSystem.primitive())
			.addColumn("has_profile_pic", TypeSystem.bool())
			.addColumn("hide_guest_list", TypeSystem.bool())
			.addColumn("host", TypeSystem.string())
			.addColumn("is_date_only", TypeSystem.bool())
			.addColumn("location", TypeSystem.string())
			.addColumn("name", TypeSystem.string())
			.addColumn("not_replied_count", TypeSystem.numeric())
			.addColumn("parent_group_id", TypeSystem.string())
			.addColumn("pic", TypeSystem.string())
			.addColumn("pic_big", TypeSystem.string())
			.addColumn("pic_cover", TypeSystem.primitive())
			.addColumn("pic_small", TypeSystem.string())
			.addColumn("pic_square", TypeSystem.string())
			.addColumn("privacy", TypeSystem.string())
			.addColumn("start_time", TypeSystem.string())
			.addColumn("ticket_uri", TypeSystem.string())
			.addColumn("timezone", TypeSystem.string())
			.addColumn("unsure_count", TypeSystem.numeric())
			.addColumn("update_time", TypeSystem.numeric())
			.addColumn("venue", TypeSystem.primitive())
			.addColumn("version", TypeSystem.numeric())
			.build();

	public static final TypedRelation EVENT_MEMBER = TypedRelationImpl.builder()
			.setName("event_member")
			.addColumn("eid", TypeSystem.primitive())
			.addColumn("eid_cursor", TypeSystem.string())
			.addColumn("inviter", TypeSystem.primitive())
			.addColumn("inviter_type", TypeSystem.string())
			.addColumn("rsvp_status", TypeSystem.string())
			.addColumn("start_time", TypeSystem.string())
			.addColumn("uid", TypeSystem.primitive())
			.addColumn("uid_cursor", TypeSystem.string())
			.build();

	public static final TypedRelation FRIEND = TypedRelationImpl.builder()
			.setName("friend")
			.addColumn("uid1", TypeSystem.primitive())
			.addColumn("uid2", TypeSystem.primitive())
			.build();

	public static final TypedRelation GROUP = TypedRelationImpl.builder()
			.setName("group_")
			.addColumn("creator", TypeSystem.primitive())
			.addColumn("description", TypeSystem.string())
			.addColumn("email", TypeSystem.string())
			.addColumn("gid", TypeSystem.primitive())
			.addColumn("icon", TypeSystem.string())
			.addColumn("icon34", TypeSystem.string())
			.addColumn("icon50", TypeSystem.string())
			.addColumn("icon68", TypeSystem.string())
			.addColumn("name", TypeSystem.string())
			.addColumn("nid", TypeSystem.numeric())
			.addColumn("office", TypeSystem.string())
			.addColumn("parent_id", TypeSystem.string())
			.addColumn("pic", TypeSystem.string())
			.addColumn("pic_big", TypeSystem.string())
			.addColumn("pic_cover", TypeSystem.primitive())
			.addColumn("pic_small", TypeSystem.string())
			.addColumn("pic_square", TypeSystem.string())
			.addColumn("privacy", TypeSystem.string())
			.addColumn("recent_news", TypeSystem.string())
			.addColumn("update_time", TypeSystem.numeric())
			.addColumn("venue", TypeSystem.primitive())
			.addColumn("website", TypeSystem.string())
			.build();

	public static final TypedRelation GROUP_MEMBER = TypedRelationImpl.builder()
			.setName("group_member")
			.addColumn("administrator", TypeSystem.bool())
			.addColumn("bookmark_order", TypeSystem.numeric())
			.addColumn("gid", TypeSystem.string())
			.addColumn("positions", TypeSystem.primitive())
			.addColumn("uid", TypeSystem.string())
			.addColumn("unread", TypeSystem.numeric())
			.build();

	public static final TypedRelation LOCATION_POST = TypedRelationImpl.builder()
			.setName("location_post")
			.addColumn("app_id", TypeSystem.primitive())
			.addColumn("author_uid", TypeSystem.primitive())
			.addColumn("coords", TypeSystem.primitive())
			.addColumn("id", TypeSystem.primitive())
			.addColumn("latitude", TypeSystem.numeric())
			.addColumn("longitude", TypeSystem.numeric())
			.addColumn("message", TypeSystem.string())
			.addColumn("page_id", TypeSystem.primitive())
			.addColumn("page_type", TypeSystem.string())
			.addColumn("post_id", TypeSystem.string())
			.addColumn("tagged_uids", TypeSystem.primitive())
			.addColumn("timestamp", TypeSystem.numeric())
			.addColumn("type", TypeSystem.string())
			.build();

	// This relation is a result of unnesting location_post.tagged_uids in the FQL schema
	public static final TypedRelation LOCATION_POST_TAGGED_UIDS = TypedRelationImpl.builder()
			.setName("location_post_tagged_uids")
			.addColumn("id", TypeSystem.primitive()) // location_post.id
			.addColumn("uid", TypeSystem.primitive())
			.build();

	public static final TypedRelation NOTE = TypedRelationImpl.builder()
			.setName("note")
			.addColumn("comment_info", TypeSystem.primitive())
			.addColumn("content", TypeSystem.string())
			.addColumn("content_html", TypeSystem.string())
			.addColumn("created_time", TypeSystem.numeric())
			.addColumn("like_info", TypeSystem.primitive())
			.addColumn("note_id", TypeSystem.string())
			.addColumn("title", TypeSystem.string())
			.addColumn("uid", TypeSystem.primitive())
			.addColumn("uid_cursor", TypeSystem.string())
			.addColumn("updated_time", TypeSystem.numeric())
			.build();

	public static final TypedRelation PHOTO = TypedRelationImpl.builder()
			.setName("photo")
			.addColumn("aid", TypeSystem.string())
			.addColumn("aid_cursor", TypeSystem.string())
			.addColumn("album_object_id", TypeSystem.primitive())
			.addColumn("album_object_id_cursor", TypeSystem.string())
			.addColumn("backdated_time", TypeSystem.numeric())
			.addColumn("backdated_time_granularity", TypeSystem.primitive())
			.addColumn("can_backdate", TypeSystem.bool())
			.addColumn("can_delete", TypeSystem.bool())
			.addColumn("can_tag", TypeSystem.bool())
			.addColumn("caption", TypeSystem.string())
			.addColumn("caption_tags", TypeSystem.primitive())
			.addColumn("comment_info", TypeSystem.primitive())
			.addColumn("created", TypeSystem.numeric())
			.addColumn("images", TypeSystem.primitive())
			.addColumn("like_info", TypeSystem.primitive())
			.addColumn("link", TypeSystem.string())
			.addColumn("modified", TypeSystem.numeric())
			.addColumn("object_id", TypeSystem.primitive())
			.addColumn("offline_id", TypeSystem.numeric())
			.addColumn("owner", TypeSystem.primitive())
			.addColumn("owner_cursor", TypeSystem.string())
			.addColumn("page_story_id", TypeSystem.string())
			.addColumn("pid", TypeSystem.string())
			.addColumn("place_id", TypeSystem.primitive())
			.addColumn("src", TypeSystem.string())
			.addColumn("src_big", TypeSystem.string())
			.addColumn("src_big_height", TypeSystem.numeric())
			.addColumn("src_big_webp", TypeSystem.string())
			.addColumn("src_big_width", TypeSystem.numeric())
			.addColumn("src_height", TypeSystem.numeric())
			.addColumn("src_small", TypeSystem.string())
			.addColumn("src_small_height", TypeSystem.numeric())
			.addColumn("src_small_webp", TypeSystem.string())
			.addColumn("src_small_width", TypeSystem.numeric())
			.addColumn("src_webp", TypeSystem.string())
			.addColumn("src_width", TypeSystem.numeric())
			.addColumn("target_id", TypeSystem.string())
			.addColumn("target_type", TypeSystem.string())
			.build();

	public static final TypedRelation PHOTO_SRC = TypedRelationImpl.builder()
			.setName("photo_src")
			.addColumn("height", TypeSystem.numeric())
			.addColumn("photo_id", TypeSystem.string())
			.addColumn("size", TypeSystem.string())
			.addColumn("src", TypeSystem.string())
			.addColumn("width", TypeSystem.numeric())
			.build();

	public static final TypedRelation PHOTO_TAG = TypedRelationImpl.builder()
			.setName("photo_tag")
			.addColumn("created", TypeSystem.numeric())
			.addColumn("object_id", TypeSystem.primitive())
			.addColumn("pid", TypeSystem.string())
			.addColumn("subject", TypeSystem.string())
			.addColumn("text", TypeSystem.string())
			.addColumn("xcoord", TypeSystem.numeric())
			.addColumn("ycoord", TypeSystem.numeric())
			.build();

	public static final TypedRelation QUESTION = TypedRelationImpl.builder()
			.setName("question")
			.addColumn("created_time", TypeSystem.numeric())
			.addColumn("id", TypeSystem.primitive())
			.addColumn("is_published", TypeSystem.bool())
			.addColumn("owner", TypeSystem.primitive())
			.addColumn("question", TypeSystem.string())
			.addColumn("updated_time", TypeSystem.numeric())
			.build();

	public static final TypedRelation URL_LIKE = TypedRelationImpl.builder()
			.setName("url_like")
			.addColumn("url", TypeSystem.string())
			.addColumn("user_id", TypeSystem.string())
			.build();

	public static final TypedRelation USER = TypedRelationImpl.builder().setName("user")
			.addColumn("about_me", string())
			.addColumn("activities", string())
			.addColumn("affiliations", primitive())
			.addColumn("age_range", primitive())
			.addColumn("allowed_restrictions", string())
			.addColumn("birthday", string())
			.addColumn("birthday_date", string())
			.addColumn("books", string())
			.addColumn("can_message", bool())
			.addColumn("can_post", bool())
			.addColumn("contact_email", string())
			.addColumn("currency", primitive())
			.addColumn("current_address", primitive())
			.addColumn("current_location", primitive())
			.addColumn("devices", primitive())
			.addColumn("education", primitive())
			.addColumn("email", string())
			.addColumn("email_hashes", primitive())
			.addColumn("first_name", string())
			.addColumn("friend_count", numeric())
			.addColumn("friend_request_count", numeric())
			.addColumn("has_timeline", bool())
			.addColumn("hometown_location", primitive())
			.addColumn("inspirational_people", primitive())
			.addColumn("install_type", string())
			.addColumn("interests", string())
			.addColumn("is_app_user", bool())
			.addColumn("is_blocked", bool())
			.addColumn("is_verified", bool())
			.addColumn("languages", primitive())
			.addColumn("last_name", string())
			.addColumn("likes_count", numeric())
			.addColumn("locale", string())
			.addColumn("meeting_for", primitive())
			.addColumn("meeting_sex", primitive())
			.addColumn("middle_name", string())
			.addColumn("movies", string())
			.addColumn("music", string())
			.addColumn("mutual_friend_count", numeric())
			.addColumn("name", string())
			.addColumn("name_format", primitiveBottom()) // can be number or bool
			.addColumn("notes_count", numeric())
			.addColumn("online_presence", string())
			.addColumn("payment_instruments", primitive())
			.addColumn("payment_pricepoints", primitive())
			.addColumn("pic", string())
			.addColumn("pic_big", string())
			.addColumn("pic_big_with_logo", string())
			.addColumn("pic_cover", primitive())
			.addColumn("pic_small", string())
			.addColumn("pic_small_with_logo", string())
			.addColumn("pic_square", string())
			.addColumn("pic_square_with_logo", string())
			.addColumn("pic_with_logo", string())
			.addColumn("political", string())
			.addColumn("profile_blurb", string())
			.addColumn("profile_update_time", numeric())
			.addColumn("profile_url", string())
			.addColumn("proxied_email", string())
			.addColumn("quotes", string())
			.addColumn("relationship_status", string())
			.addColumn("religion", string())
			.addColumn("search_tokens", primitive())
			.addColumn("security_settings", primitive())
			.addColumn("sex", string())
			.addColumn("shipping_information", primitive())
			.addColumn("significant_other_id", numeric())
			.addColumn("sort_first_name", string())
			.addColumn("sort_last_name", string())
			.addColumn("sports", primitive())
			.addColumn("status", primitive())
			.addColumn("subscriber_count", numeric())
			.addColumn("third_party_id", string())
			.addColumn("timezone", numeric())
			.addColumn("tv", string())
			.addColumn("uid", numeric())
			.addColumn("username", string())
			.addColumn("verified", bool())
			.addColumn("video_upload_limits", primitive())
			.addColumn("viewer_can_send_gift", bool())
			.addColumn("wall_count", numeric())
			.addColumn("website", string())
			.addColumn("work", primitive())
			.build();

	public static final TypedRelation VIDEO = TypedRelationImpl.builder()
			.setName("video")
			.addColumn("album_id", TypeSystem.primitive())
			.addColumn("created_time", TypeSystem.numeric())
			.addColumn("description", TypeSystem.string())
			.addColumn("embed_html", TypeSystem.string())
			.addColumn("format", TypeSystem.primitive())
			.addColumn("length", TypeSystem.numeric())
			.addColumn("link", TypeSystem.string())
			.addColumn("owner", TypeSystem.primitive())
			.addColumn("src", TypeSystem.string())
			.addColumn("src_hq", TypeSystem.string())
			.addColumn("thumbnail_link", TypeSystem.string())
			.addColumn("title", TypeSystem.string())
			.addColumn("updated_time", TypeSystem.numeric())
			.addColumn("vid", TypeSystem.primitive())
			.build();

	public static final TypedRelation VIDEO_TAG = TypedRelationImpl.builder()
			.setName("video_tag")
			.addColumn("created_time", TypeSystem.numeric())
			.addColumn("subject", TypeSystem.string())
			.addColumn("updated_time", TypeSystem.numeric())
			.addColumn("vid", TypeSystem.string())
			.build();

	public static final Schema SCHEMA = Schema.of(
			ALBUM,
			APPLICATION,
			CHECKIN,
			EVENT,
			EVENT_MEMBER,
			FRIEND,
			GROUP,
			GROUP_MEMBER,
			LOCATION_POST,
			LOCATION_POST_TAGGED_UIDS,
			NOTE,
			PHOTO,
			PHOTO_SRC,
			PHOTO_TAG,
			QUESTION,
			URL_LIKE,
			USER,
			VIDEO,
			VIDEO_TAG);
}
