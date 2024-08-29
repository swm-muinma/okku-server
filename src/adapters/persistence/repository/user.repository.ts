import { UserDomain } from "@src/domain/user.domain";

import { ErrorDomain } from "@src/domain/error.domain";
import {
  UserEntity,
  UserModel,
  UserPersistenceMapper,
} from "../model/user.model";
import { FormEnum } from "@src/enum/form.enum";
import { Types } from "mongoose";

class UserRepository {
  /**
   * Create a new user.
   * @param user - The user domain object to create.
   * @returns A promise that resolves to the created user domain object.
   */
  public async create(user: UserDomain): Promise<UserDomain> {
    try {
      const userEntity = UserPersistenceMapper.toEntity(user);
      const createdUser = await new UserModel(userEntity).save();
      return UserPersistenceMapper.toDomain(createdUser);
    } catch (error) {
      console.error("Error creating user:", error);
      throw new ErrorDomain("Error creating user", 500);
    }
  }

  /**
   * Get a user by ID.
   * @param id - The ID of the user to retrieve.
   * @returns A promise that resolves to the user domain object or null.
   */
  public async getById(id: string): Promise<UserDomain | null> {
    try {
      const userEntity = await UserModel.findById(id).exec();
      if (!userEntity) return null;
      return UserPersistenceMapper.toDomain(userEntity);
    } catch (error) {
      console.error("Error getting user by ID:", error);
      throw new ErrorDomain("Error getting user by ID", 500);
    }
  }

  /**
   * Get a user by Apple ID.
   * @param appleId - The Apple ID of the user to retrieve.
   * @returns A promise that resolves to the user domain object or null.
   */
  public async getByAppleId(appleId: string): Promise<UserDomain | null> {
    try {
      const userEntity: UserEntity | null = await UserModel.findOne({
        apple_id: appleId,
      }).exec();
      if (!userEntity) return null;
      return UserPersistenceMapper.toDomain(userEntity);
    } catch (error) {
      console.error("Error getting user by apple Id:", error);
      throw new ErrorDomain("Error getting user by apple Id", 500);
    }
  }
  /**
   * Get a user by Kakao ID.
   * @param kakaoId - The Kakao ID of the user to retrieve.
   * @returns A promise that resolves to the user domain object or null.
   */
  public async getByKakaoId(kakaoId: string): Promise<UserDomain | null> {
    try {
      const userEntity: UserEntity | null = await UserModel.findOne({
        kakao_id: kakaoId,
      }).exec();
      if (!userEntity) return null;
      return UserPersistenceMapper.toDomain(userEntity);
    } catch (error) {
      console.error("Error getting user by kakao Id:", error);
      throw new ErrorDomain("Error getting user by kakao Id", 500);
    }
  }

  /**
   * Update a user by ID.
   * @param id - The ID of the user to update.
   * @param name - The new name of the user.
   * @param image - The new image of the user.
   * @param height - The new height of the user.
   * @param weight - The new weight of the user.
   * @param form - The new form of the user.
   * @returns A promise that resolves to the updated user domain object or null.
   */
  public async update(
    id: string,
    name?: string,
    image?: string,
    height?: number,
    weight?: number,
    form?: FormEnum
  ): Promise<UserDomain | null> {
    try {
      // Create a partial update object
      const update: Partial<UserDomain> = {};

      if (name !== undefined) {
        update.name = name;
      }
      if (image !== undefined) {
        update.image = image;
      }
      if (height !== undefined) {
        update.height = height;
      }
      if (weight !== undefined) {
        update.weight = weight;
      }
      if (form !== undefined) {
        update.form = form;
      }

      // Convert the partial domain object to a partial entity object
      const partialEntity =
        UserPersistenceMapper.partialDomainToPartialEntity(update);

      // Perform the update in the database
      const updatedUserEntity = await UserModel.findByIdAndUpdate(
        id,
        partialEntity,
        { new: true }
      ).exec();
      if (!updatedUserEntity) return null;
      return UserPersistenceMapper.toDomain(updatedUserEntity);
    } catch (error) {
      console.error("Error updating user:", error);
      throw new ErrorDomain("Error updating user", 500);
    }
  }

  public async updateToPremium(id: string): Promise<UserDomain | null> {
    console.log("call premium");
    try {
      // Perform the update in the database
      const updatedUserEntity = await UserModel.findByIdAndUpdate(
        id,
        { is_premium: true }, // Set isPremium to true
        { new: true } // Return the updated document
      ).exec();

      if (!updatedUserEntity) return null;

      return UserPersistenceMapper.toDomain(updatedUserEntity);
    } catch (error) {
      console.error("Error updating user:", error);
      throw new ErrorDomain("Error updating user", 500);
    }
  }

  /**
   * Delete carts by user ID.
   * @param userId - The user ID to delete carts for.
   * @returns A promise that resolves to an array of CartDomain of deleted carts or null.
   */
  public async delete(userId: string): Promise<string | null> {
    const objectId = new Types.ObjectId(userId);
    try {
      const entity = await UserModel.deleteOne({
        _id: objectId,
      }).exec();
      if (entity.deletedCount == 0) return null;
      return userId;
    } catch (error) {
      console.error("Error deleting carts by userId:", error);
      throw new ErrorDomain("Error deleting carts by userId", 500);
    }
  }
}

export { UserRepository };
