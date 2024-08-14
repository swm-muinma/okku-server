import { LogModel } from "@src/adapters/persistence/model/log.model";
import { ErrorDomain } from "@src/domain/error.domain";
import { PageInfo } from "@src/dto/pageInfo.dto";
import mongoose, { ClientSession } from "mongoose";
import { PickModel } from "../model/pick.model";

class LogRepository {
  /**
   * Create a new log for a user.
   * @param userId - The user ID to create a log for.
   * @returns A promise that resolves to a boolean indicating success or null.
   */
  public async create(
    api: string,
    request: any,
    response: any,
    user_agent: string,
    ip: string,
    is_error: boolean
  ): Promise<boolean> {
    try {
      const newLog = new LogModel({
        api: api,
        request: request,
        response: response,
        user_agent: user_agent,
        ip: ip,
        is_error: is_error,
      });
      const savedLog = await newLog.save();
      if (savedLog) return true;
    } catch (error) {
      console.error("Error creating log:", error);
      return false;
    }
    return false;
  }
}

export { LogRepository };
