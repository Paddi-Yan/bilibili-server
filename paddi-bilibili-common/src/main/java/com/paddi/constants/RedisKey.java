package com.paddi.constants;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月18日 11:45:25
 */
public class RedisKey {
    public static final String APPLICATION_PREFIX = "bilibili:";

    public static final String SUBSCRIBED = APPLICATION_PREFIX + "subscribed:";

    public static final String ACCESS_TOKEN_BLACKLIST = APPLICATION_PREFIX + "access-token-blacklist:";

    public static final String PATH_KEY = APPLICATION_PREFIX + "path:";

    public static final String UPLOADED_SIZE_KEY = APPLICATION_PREFIX + "uploaded-size:";

    public static final String UPLOADED_NUM_KEY = APPLICATION_PREFIX + "uploaded-num:";

    public static final String VIDEO_LIKES = APPLICATION_PREFIX + "video-likes:";

    public static final String VIDEO_COLLECTIONS = APPLICATION_PREFIX + "video-collections:";
    public static final String VIDEO_COINS = APPLICATION_PREFIX + "video-coins:";
    public static final String VIDEO_COMMENTS_LIKE = APPLICATION_PREFIX + "video-comments-like:";

    public static final String VIDEO_ROOT_COMMENTS_POPULARITY = APPLICATION_PREFIX + "video-root-comments-popularity";

    public static final String VIDEO_CHILD_COMMENTS_POPULARITY = APPLICATION_PREFIX + "video-child-comments-popularity";
    public static final String VIDEO_COMMENT_AREA = APPLICATION_PREFIX + "video-comment-area";

    public static final String VIDEO_COMMENT_COUNTER = APPLICATION_PREFIX + "video-comment-counter";
}
