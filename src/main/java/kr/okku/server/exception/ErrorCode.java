package kr.okku.server.exception;
public enum ErrorCode {

    FORBIDDEN("Forbidden",403),

    WRONG_ITEM_FOR_FITTING("fittings are only available on top and bottom",400),
    MAYBE_SCRAPER_WAITING("maybe scraper waiting or down",500),
    IO_EXCEPTION_ON_IMAGE_DELETE("io exception on image delete",500),
    PUT_OBJECT_EXCEPTION("put object exception",500),
    INVALID_FILE_EXTENTION("invalid file exteion",404),
    NO_FILE_EXTENTION("no file extention on s3",404),
    IO_EXCEPTION_ON_IMAGE_UPLOAD("io exception on s3",404),
    EMPTY_FILE_EXCEPTION("empty file in s3",404),
    MUST_INVITE("must invite", 402),
    DOMAIN_INVALID("domain invalid", 400),
    USER_NOT_FOUND("User not found", 404),
    LOG_NOT_FOUND("Log not found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    INVALID_CARTID("cartId invalid", 400),
    IF_IS_DELETE_PERMENANT_IS_FALSE_THEN_CARTID_IS_REQUIRED("if isDeletePermenant is false, cartId is required",400),
    INVALID_PARAMS("parameters invalid", 400),
    NOT_SUPPORTED_OAUTH_PLATFORM("not supported oauth platform",400),
    DATABASE_ERROR_WITH_SAVE_CART("database connection error with saving cart info",500),
    NAME_IS_EMPTY("name parameter must required", 400),
    FORM_IS_EMPTY("form parameter must required", 400),
    TOKEN_IS_EMPTY("token parameter must required", 400),
    REFRESHTOKEN_IS_EMPTY("refreshToken parameter must required", 400),
    CARTID_IS_EMPTY("cartId parameter must required", 400),
    PICKID_IS_EMPTY("pickId parameter must required", 400),
    INVALID_PICKIDS("pickIds invalid", 400),
    NOT_OWNER("not owner", 400),
    NOT_CART_OWNER("not cart owner", 400),
    NOT_PICK_OWNER("not pick owner", 400),
    INVALID_PAGE("Page and size must be larger than 0", 400),
    INVALID_SIZE("Size and size must be larger than 0", 400),
    CART_NOT_EXIST("cart not exist", 404),
    PICK_NOT_EXIST("Picks not exist", 404),
    IS_DELETE_FROM_ORIGIN_REQUIRED("'isDeleteFromOrigin' field is required", 400),
    DESTINATION_CART_ID_REQUIRED("'destinationCartId' field is required", 400),
    ALREADY_EXIST_CART("already exist in cart", 400),
    PICK_IDS_REQUIRED("pickIds is required", 400),
    MUST_LOGIN("must login", 402),
    SCRAPER_ERROR("scraper error", 500),
    IMAGE_CONVERTER_ERROR("Not Convert pick image", 500),
    INVALID_ITEM_IMAGE("Item image is wrong", 404),
    REFRESH_INVALID("Not Valid refreshToken", 400),
    ERROR_ABOUT_JWT_WITHREFRESH("error about jwt with handling refresh token",400),
    DUPLICATED_PICK("Duplicated pick", 400),
    APPLE_LOGIN_TOKEN_HEADER_MAPPING("Parsing Header Error : 토큰의 헤더를 매핑하는 데 실패했습니다", 500),
    APPLE_LOGIN_TOKEN_HEADER("Parsing Header Error : 토큰 처리 중 오류가 발생했습니다",500),
    APPLE_LOGIN_INVALID_TOKEN("Parsing Header Error : 유효하지 않은 토큰 형식입니다.",400),
    RSA_ERROR("RSAPublicKey생성 중 오류가 발생했습니다.",500),
    INVALID_AUTHORIZATION_CODE("Apple login failed: the code has expired or has been revoked.",400),
    APPLE_LOGIN_FAILED("Apple login failed",500);

    private final String message;
    private final int statusCode;

    ErrorCode(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
