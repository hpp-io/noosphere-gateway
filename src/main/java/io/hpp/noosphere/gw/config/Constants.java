package io.hpp.noosphere.gw.config;

// TODO: Move these constants to the common library
public class Constants extends io.hpp.noosphere.common.config.Constants {

  public static final String PROPERTY_NAME_VERIFIED = "verified";
  public static final String PROPERTY_NAME_CONTAINER = "container";
  public static final String PROPERTY_NAME_USER_SUBSCRIPTION = "userSubscription";
  public static final String PROPERTY_NAME_AGENT = "agent";
  public static final String PROPERTY_NAME_AGENT_REQUEST = "agentRequest";
  public static final String PROPERTY_NAME_VERIFIER = "verifier";

  public static final String COLUMN_NAME_ID = "id";
  public static final String SERVICE_NAME_NOOSPHERE_HUB = "noosphere-hub";
  public static final String SERVICE_API_PREFIX = "/api";
  public static final String SERVICE_API_MINE_WALLET = "/users/mine/wallet";
  public static final String SERVICE_API_MINE_API_KEY = "/users/mine/api-key";
  public static final String SERVICE_API_USER_PROFILE = "/users/profile";
  public static final String SERVICE_API_CONTAINERS = "/containers";
  public static final String SERVICE_API_AGENT_REQUESTS = "/agent-requests";
  public static final String SERVICE_API_VERIFIERS = "/verifiers";
  public static final String SERVICE_API_SEARCH = "/search";
  public static final String API_URL_SLASH = "/";

  public static final String HTTP_HEADER_API_GROUP = "X-API-Group";
  public static final String HTTP_HEADER_USER_ID = "X-User-ID";
  public static final String HTTP_HEADER_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
  public static final String HTTP_HEADER_RATE_LIMIT_RETRY_AFTER_MILLISECONDS = "X-Rate-Limit-Retry-After-Milliseconds";
}
