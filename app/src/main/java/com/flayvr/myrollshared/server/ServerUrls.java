package com.flayvr.myrollshared.server;

import com.flayvr.myrollshared.application.AppConfiguration;

public class ServerUrls
{

    public static final String BACKUP_MEDIA_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("backup/backup_android").toString();
    public static final String CANCEL_UPLOADING_FLAYVR_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/cancel_uploading_for_android").toString();
    public static final String CREATE_APP_EVENT_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/create_new_app_event").toString();
    public static final String CREATE_APP_SESSION_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/create_app_session").toString();
    public static final String CREATE_NEW_FB_USER_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("fb_users/create_new_fb_user_for_android").toString();
    public static final String CREATE_NEW_FLAYVR_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/create_new_flayvr_for_android").toString();
    public static final String CREATE_NEW_USER_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/create_new_user").toString();
    public static final String CREATE_NOTIFICATION_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_notification/create_android").toString();
    public static final String CREATE_SHARED_FLAYVR_VIEW_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("shared_flayvr_views/create_new_shared_flayvr_view_android").toString();
    public static final String CREATE_SHARING_SESSION_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("sharing_sessions/share_on_platform_for_android").toString();
    public static final String CV_INFO_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_analytics/save_photos_and_videos_data").toString();
    public static final String FOLDER_INFO_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_analytics/save_folders_data_for_android").toString();
    public static final String GD_CLASSIFIED_PHOTOS_DATA_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_analytics/save_classified_photos_data").toString();
    public static final String GD_DOCTOR_USER_ACTION_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_analytics/save_doctor_user_action").toString();
    public static final String GD_DOCTOR_USER_DATA_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_analytics/save_doctor_user_data").toString();
    public static final String GD_LABELED_PHOTOS_DATA_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_analytics/save_labeled_photos_data").toString();
    public static final String GET_APP_SETTINGS_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("settings/fetch_app_settings_for_android").toString();
    public static final String GET_FLAYVR_PHOTOS_URLS = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/get_flayvr_photos_urls_for_android").toString();
    public static final String LOGIN_EMAIL_USER = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_account/login_with_email").toString();
    public static final String LOGOUT_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_account/logout").toString();
    public static final String MYROLL_CLOUD_CREATE_SUBSCRIPTION_ACCOUNT = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("myroll_cloud/create_subscription_account").toString();
    public static final String MYROLL_CLOUD_GET_ALL_FOLDERS = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("myroll_cloud/get_all_folders").toString();
    public static final String MYROLL_CLOUD_GET_PHOTOS_OF_FOLDER = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("myroll_cloud/get_photos_of_folder").toString();
    public static final String MYROLL_CLOUD_SAVE_BACKED_UP_PHOTO = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("myroll_cloud/save_backed_up_photo").toString();
    public static final String NOTIFICATION_OPENED_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_notification/opened_android").toString();
    public static final String PICASA_STATISTICS_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_analytics/save_picasa_folders_data").toString();
    public static final String REGISTER_EMAIL_USER = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_account/register_with_email").toString();
    public static final String REGISTER_FB_USER = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_account/register_with_fb").toString();
    public static final String REGISTER_GPLUS_USER = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_account/register_with_google").toString();
    public static final String REMOTE_FLAYVR_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/get_flayvr_data_android").toString();
    public static final String SEND_APP_SESSION_DATA_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/create_app_session_info").toString();
    public static final String SET_PUSH_NOTIFICATIONS_TOKEN_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/set_push_notification_token").toString();
    public static final String SET_REFERRER_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("doctor_users/set_referrer").toString();
    public static final String UPLOAD_FLAYVR_ITEM = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/upload_media_to_flayvr_new").toString();
    public static final String UPLOAD_VIDEO_TO_FLAYVR_URL = (new StringBuilder()).append(AppConfiguration.getConfiguration().getServerUrl()).append("flayvr_canvases/upload_video_to_flayvr_for_android").toString();

    public ServerUrls()
    {
    }

}
