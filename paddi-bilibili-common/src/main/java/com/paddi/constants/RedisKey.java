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

    public static final String VIDEO_LIKES = APPLICATION_PREFIX + "video-likes";

}
