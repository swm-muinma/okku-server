import { OpenAI } from "openai";
import { encode, decode } from "gpt-tokenizer";
import { SummarizedReviewsDomain } from "@src/domain/summarizedReviews.domain";

export class CallPromptAdapter {
  public async callPrompt(prompt: string) {
    const res = await this.processPrompt(prompt, "gpt-3.5-turbo");
    return res;
  }

  async processPrompt(prompt: string, model: string) {
    const max_length = 16000;

    const encodedTokens = await this.encodeTokens(prompt);

    if (encodedTokens.length > max_length) {
      prompt = await this.decodeTokens(encodedTokens.slice(0, max_length));
    }

    const openai = new OpenAI({
      apiKey: process.env.OPENAI_API_KEY,
    });

    try {
      const response = await openai.chat.completions.create({
        model: model,
        messages: [
          {
            role: "user",
            content: prompt,
          },
        ],
        temperature: 0,
      });

      const jsonData = response.choices[0].message?.content || "";
      const data = JSON.parse(trimToBraces(jsonData));

      const result = new SummarizedReviewsDomain(
        "",
        data.summary.cautions,
        data.summary.positives
      );
      return result;
    } catch (error) {
      console.error("Error:", error);
      const result = new SummarizedReviewsDomain("", [], []);
      return result;
    }
  }

  async encodeTokens(text: string): Promise<number[]> {
    return encode(text);
  }

  async decodeTokens(tokens: number[]): Promise<string> {
    return decode(tokens);
  }
}

function trimToBraces(input: string): string {
  const firstBraceIndex = input.indexOf("{");
  const lastBraceIndex = input.lastIndexOf("}");

  if (
    firstBraceIndex !== -1 &&
    lastBraceIndex !== -1 &&
    firstBraceIndex < lastBraceIndex
  ) {
    return input.substring(firstBraceIndex, lastBraceIndex + 1);
  }

  return input;
}
