export class ErrorDomain extends Error {
  public statusCode: number;

  constructor(message: string, statusCode: number) {
    super(message);
    this.statusCode = statusCode;
    // Error의 기본 클래스가 메시지를 가진 경우, `message`가 정확히 동작하도록 설정합니다.
    Object.setPrototypeOf(this, new.target.prototype);
  }
}
