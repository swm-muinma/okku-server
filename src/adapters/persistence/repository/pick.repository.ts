import { PickDomain } from "@src/domain/pick.domain";
import {
  PickEntity,
  PickModel,
  PickPersistenceMapper,
} from "../model/pick.model";
import { ErrorDomain } from "@src/domain/error.domain";
import { PageInfo } from "@src/dto/pageInfo.dto";
import mongoose, { Types } from "mongoose";

class PickRepository {
  /**
   * Find picks by user ID and pick IDs with pagination.
   * @param userId - The user ID to find picks for.
   * @param pickIds - The array of pick IDs to find.
   * @param page - The page number for pagination.
   * @param size - The number of items per page for pagination.
   * @returns A promise that resolves to an object containing picks and page info.
   */
  public async findByPickIds(
    pickIds: string[],
    page: number,
    size: number
  ): Promise<{ picks: PickDomain[]; page: PageInfo }> {
    try {
      const skip = (page - 1) * size;

      // Get total count of picks
      const totalDataCnt = await PickModel.countDocuments({
        _id: { $in: pickIds },
      }).exec();
      // Get paginated picks
      const picks = await PickModel.find({
        _id: { $in: pickIds },
      })
        .skip(skip)
        .limit(size)
        .exec();
      if (!picks.length) {
        const emptyPageInfo: PageInfo = {
          totalDataCnt: 0,
          totalPages: 0,
          isLastPage: true,
          isFirstPage: true,
          requestPage: page,
          requestSize: size,
        };
        return { picks: [], page: emptyPageInfo };
      }

      // Map to domain models
      const pickDomains = picks.map(PickPersistenceMapper.toDomain);

      // Calculate pagination info
      const totalPages = Math.ceil(totalDataCnt / size);
      const isLastPage = page >= totalPages;
      const isFirstPage = page === 1;

      const pageInfo: PageInfo = {
        totalDataCnt,
        totalPages,
        isLastPage,
        isFirstPage,
        requestPage: page,
        requestSize: size,
      };

      return { picks: pickDomains, page: pageInfo };
    } catch (error) {
      console.error("Error finding picks by user ID and pick IDs:", error);
      throw new ErrorDomain("Error finding picks by user ID and pick IDs", 500);
    }
  }

  public async findById(id: string): Promise<PickDomain | null> {
    if (!Types.ObjectId.isValid(id)) {
      return null;
    }

    try {
      const pick = await PickModel.findById(id).exec();
      if (!pick) {
        throw new ErrorDomain("Cannot find pick with given ID", 404);
      }
      return PickPersistenceMapper.toDomain(pick);
    } catch (err) {
      // Re-throw the error with additional context if needed
      throw new ErrorDomain("Error finding pick by ID", 500);
    }
  }

  /**
   * Find picks by user ID and cart ID with pagination.
   * @param userId - The user ID to find picks for.
   * @param page - The page number for pagination.
   * @param size - The number of items per page for pagination.
   * @returns A promise that resolves to an object containing picks and page info.
   */
  public async findByUserId(
    userId: string,
    page: number,
    size: number
  ): Promise<{ picks: PickDomain[]; page: PageInfo }> {
    try {
      const skip = (page - 1) * size;

      // Get total count of picks
      const totalDataCnt = await PickModel.countDocuments({
        user_id: userId,
      }).exec();

      // Get paginated picks
      const picks = await PickModel.find({
        user_id: userId,
      })
        .skip(skip)
        .limit(size)
        .exec();

      if (!picks.length) {
        const emptyPageInfo: PageInfo = {
          totalDataCnt: 0,
          totalPages: 0,
          isLastPage: true,
          isFirstPage: true,
          requestPage: page,
          requestSize: size,
        };
        return { picks: [], page: emptyPageInfo };
      }

      // Map to domain models
      const pickDomains = picks.map(PickPersistenceMapper.toDomain);

      // Calculate pagination info
      const totalPages = Math.ceil(totalDataCnt / size);
      const isLastPage = page >= totalPages;
      const isFirstPage = page === 1;

      const pageInfo: PageInfo = {
        totalDataCnt,
        totalPages,
        isLastPage,
        isFirstPage,
        requestPage: page,
        requestSize: size,
      };

      return { picks: pickDomains, page: pageInfo };
    } catch (error) {
      console.error("Error finding picks by user ID and cart ID:", error);
      throw new ErrorDomain("Error finding picks by user ID and cart ID", 500);
    }
  }

  public async delete(pickIds: string[]): Promise<boolean> {
    await this.checkPickIdExist(pickIds);
    try {
      const result = await PickModel.deleteMany({
        _id: { $in: pickIds },
      }).exec();
      return result.deletedCount === pickIds.length;
    } catch (error) {
      console.error("Error deleting picks:", error);
      throw new ErrorDomain("Error deleting picks", 500);
    }
  }

  public async create(pick: PickDomain): Promise<PickDomain> {
    try {
      const pickEntity = PickPersistenceMapper.toEntity(pick);
      const newPick = new PickModel(pickEntity);
      const savedPick = await newPick.save();
      return PickPersistenceMapper.toDomain(savedPick);
    } catch (error) {
      console.error("Error creating pick:", error);
      throw new ErrorDomain("Error creating pick", 500);
    }
  }

  isValidObjectId = (id: string): boolean => mongoose.isValidObjectId(id);

  private async checkPickIdExist(pickIds: string[]): Promise<boolean> {
    if (!pickIds || pickIds.length < 1) {
      return true;
    }
    const validPickIds = pickIds.filter((id) => this.isValidObjectId(id));

    if (validPickIds.length != pickIds.length) {
      throw new ErrorDomain("pickId is invalid", 400);
    }
    // 데이터베이스에서 존재하는 pickIds를 조회
    const existingPicks = await PickModel.find({
      _id: { $in: validPickIds },
    }).select("_id"); // _id 필드만 선택

    // 존재하는 pickIds를 배열로 변환
    const existingPickIds = existingPicks.map((pick) => pick._id.toString());

    // 존재하지 않는 pickIds 추출
    const invalidPickIds = pickIds.filter(
      (pickId) => !existingPickIds.includes(pickId)
    );

    if (invalidPickIds.length > 0) {
      const errmsg = "Invalid PickIds: " + invalidPickIds;
      throw new ErrorDomain(errmsg, 400);
    }

    return true;
  }

  public async findByUserIdWithoutPage(userId: string): Promise<PickDomain[]> {
    try {
      // Get total count of picks
      const totalDataCnt = await PickModel.countDocuments({
        user_id: userId,
      }).exec();

      // Get paginated picks
      const picks = await PickModel.find({
        user_id: userId,
      }).exec();

      if (!picks.length) {
        return [];
      }

      // Map to domain models
      const pickDomains = picks.map(PickPersistenceMapper.toDomain);

      return pickDomains;
    } catch (error) {
      console.error("Error finding picks by user ID and cart ID:", error);
      throw new ErrorDomain("Error finding picks by user ID and cart ID", 500);
    }
  }
}

export { PickRepository };
