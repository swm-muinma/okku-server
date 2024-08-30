package kr.okku.server.exception;
public enum ErrorCode {
    MUST_INVITE("must invite", 402),
    DOMAIN_INVALID("domain invalid", 400),
    USER_NOT_FOUND("User not found", 404),
    INTERNAL_SERVER_ERROR("Internal Server Error", 500),
    INVALID_CARTID("cartId invalid", 400),
    INVALID_PARAMS("parameters invalid", 400),
    INVALID_PICKIDS("pickIds invalid", 400),
    NOT_OWNER("not owner", 400),
    INVALID_PAGE("Page and size must be larger than 0", 400),
    CART_NOT_EXIST("cart not exist", 404),
    PICK_NOT_EXIST("Picks not exist", 404);
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