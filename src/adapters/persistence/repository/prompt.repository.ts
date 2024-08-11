import { ErrorDomain } from "@src/domain/error.domain";
import { PromptEntity, PromptModel } from "../model/prompt.model";

class PromptRepository {
  public async findByName(name: string): Promise<string> {
    try {
      const promptEntity: PromptEntity | null = await PromptModel.findOne({
        name: name,
      }).exec();
      if (!promptEntity) {
        console.error("Error getting prompt:");
        throw new ErrorDomain("Error getting prompt:", 500);
      }
      return promptEntity.prompt;
    } catch (error) {
      console.error("Error getting prompt: ", error);
      throw new ErrorDomain("Error getting prompt", 500);
    }
  }
}

export { PromptRepository };
