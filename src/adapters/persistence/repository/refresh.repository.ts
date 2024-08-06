import { RefreshModel } from "@src/adapters/persistence/model/refresh.model";
import { ErrorDomain } from "@src/domain/error.domain";

class RefreshRepository {
  // Refresh 토큰이 존재하는지 확인하는 함수
  isExist = async (refreshToken: string): Promise<boolean> => {
    try {
      const refresh = await RefreshModel.findOne({ refreshToken });
      return refresh !== null;
    } catch (error) {
      console.error("Error checking refresh token existence:", error);
      return false;
    }
  };

  // Refresh 토큰을 생성하는 함수
  create = async (refreshToken: string): Promise<boolean> => {
    try {
      const newRefresh = new RefreshModel({ refreshToken });
      await newRefresh.save();
      return true;
    } catch (error) {
      console.error("Error creating refresh token:", error);
      return false;
    }
  };

  // Refresh 토큰을 삭제하는 함수
  delete = async (refreshToken: string): Promise<boolean> => {
    try {
      const result = await RefreshModel.deleteOne({ refreshToken });
      return result.deletedCount === 1;
    } catch (error) {
      console.error("Error deleting refresh token:", error);
      return false;
    }
  };
}

export { RefreshRepository };
