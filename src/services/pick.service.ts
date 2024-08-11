import { ScraperAdapter } from "@src/adapters/crawlling/scraper.adapter";
import { CartRepository } from "@src/adapters/persistence/repository/cart.repository";
import { PickRepository } from "@src/adapters/persistence/repository/pick.repository";
import { ErrorDomain } from "@src/domain/error.domain";
import { PickDomain, PlatformDomain } from "@src/domain/pick.domain";
import { PageInfo } from "@src/dto/pageInfo.dto";
import { UserRepository } from "@src/adapters/persistence/repository/user.repository";

const pickRepository = new PickRepository();
const cartRepository = new CartRepository();
const scraperAdapter = new ScraperAdapter();
const userRepository = new UserRepository();

export class PickService {
  async createPick(userId: string, url: string): Promise<PickDomain> {
    try {
      const user = await userRepository.getById(userId);
      if (!user?.isPremium) {
        const picks = await pickRepository.findByUserId(userId, 1, 10);
        if (picks.page.totalDataCnt > 8) {
          throw new ErrorDomain("must invite", 402);
        }
      }
      const scrapedData = await scraperAdapter.scrape(url);
      console.log("scrape: ", scrapedData);
      const platform = new PlatformDomain(
        scrapedData.platform,
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAe1BMVEUAAACtrarj5eb19vempqY2MzC7uLXk4+Hx8fGmqamusK/P0dD////FxsZHR0X8+/nGx8LFysyRk5B5fX5+fnphY2ago550dXlaWligoKCztLY4OTciIiCsq610cm6Li40tLSsbGxm9wrlMS0u3ubHs8fT0+/+EgoLY1dcDqKGoAAAAlElEQVR4AeSPgwEEQQxFs8b8tW30X+Gt1cINk7yQ/mxxvCBugiQrqvZmvKAzGItgWopuw3kyFx6Rj4BI5YkoRPSAcbK+SCnDKuUo6LNKVFRDXnND/LAaa0Z1zW0jfrNcQLM1BNaC/zCgO5rrhzF7Q/XOpCP6znk5S3DeLIVqyLIxLWLDD/kbGgoPAPpacv5NgmGkAQAbCgckaxy7FQAAAABJRU5ErkJggg==",
        "https://www.29cm.co.kr/home/"
      );

      const pick = new PickDomain(
        url,
        userId,
        scrapedData.name,
        scrapedData.price,
        scrapedData.thumbnail_url,
        platform,
        scrapedData.product_pk
      );

      const savedPick = await pickRepository.create(pick);
      return savedPick;
    } catch (error: any) {
      throw new Error(`Failed to create pick: ${error.message}`);
    }
  }

  async deletePicks(
    userId: string,
    pickIds: string[],
    cartId: string | null,
    isDeletePermenant: boolean
  ): Promise<{ cartId: string; pickIds: string[] }> {
    if (isDeletePermenant == null) {
      throw new ErrorDomain("'isDeletePermenant' filed is required", 400);
    }
    if ((cartId == null || cartId == "") && isDeletePermenant == false) {
      throw new ErrorDomain(
        "if cartId is null, then isDeletePermenant is must true",
        500
      );
    }
    if (pickIds.length == 0) {
      throw new ErrorDomain("pickIds is required", 400);
    }
    const picksInfo = await pickRepository.findByPickIds(
      pickIds,
      1,
      pickIds.length
    );
    picksInfo.picks.forEach((el) => {
      if (el.userId != userId) {
        throw new ErrorDomain("not pick owner", 400);
      }
    });
    if ((cartId == null || cartId == "") && isDeletePermenant == true) {
      const isDeleted: boolean = await pickRepository.delete(pickIds);
      const isDeletedFromCart = await cartRepository.deletePickFromAllCart(
        pickIds
      );
      if (isDeleted && isDeletedFromCart) {
        return {
          cartId: "__all__",
          pickIds: pickIds,
        };
      }
      throw new ErrorDomain("Can not deleted from all", 500);
    }
    if (cartId != null && isDeletePermenant == false) {
      const isDeleted: string[] | null = await cartRepository.deleteFromCart(
        pickIds,
        cartId
      );
      if (!isDeleted) {
        throw new ErrorDomain("Can not deleted from cart", 500);
      }
      return {
        cartId: cartId,
        pickIds: pickIds,
      };
    }

    throw new ErrorDomain("Can not deleted from cart", 500);
  }

  async getMyPicks(
    userId: string,
    cartId: string | undefined,
    page: number,
    size: number
  ): Promise<{
    cart: { name: string; host: { id: string; name: string } };
    picks: PickDomain[];
    page: PageInfo;
  }> {
    if (page < 1) {
      throw new ErrorDomain("page must be larger than 0", 400);
    }

    if (size < 1) {
      throw new ErrorDomain("size must be larger than 0", 400);
    }
    if (cartId != undefined) {
      const cart = await cartRepository.findById(cartId);
      const res = await pickRepository.findByPickIds(
        cart.pickItemIds,
        page,
        size
      );
      const user = await userRepository.getById(cart.userId);
      if (!res) {
        throw new ErrorDomain("Cart not found by userId", 404);
      }
      return {
        cart: {
          name: cart.name,
          host: { id: cart.userId, name: user?.name! },
        },
        picks: res.picks,
        page: res.page,
      };
    } else {
      const res = await pickRepository.findByUserId(userId, page, size);
      if (!res) {
        throw new ErrorDomain("Cart not found by userId", 404);
      }
      return {
        cart: {
          name: "__all__",
          host: { id: "test_user_id", name: "testUser" },
        },
        picks: res.picks,
        page: res.page,
      };
    }
  }
}
