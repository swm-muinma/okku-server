import { CartRepository } from "src/adapters/persistence/repository/cart.repository";
import { PickRepository } from "src/adapters/persistence/repository/pick.repository";
import { ErrorDomain } from "src/domain/error.domain";
import { PickDomain } from "src/domain/pick.domain";
import { PageInfo } from "src/dto/pageInfo.dto";
import { FormEnum } from "src/enum/form.enum";

const pickRepository = new PickRepository();
const cartRepository = new CartRepository();
export class PickService {
  async createPick(): Promise<PickDomain> {
    return "ㅎㅇ";
  }

  async deletePicks(
    pickIds: string[],
    cartId: string[] | null,
    isDeletePernenant: boolean
  ): Promise<{ cartId: string; pickIds: string[] }> {
    pickRepository.delete(pickIds);
    return "ㄹ";
  }

  getComparisonView(): any {
    return {
      user: {
        name: "testUser",
        image: "https://cdn-icons-png.flaticon.com/512/4140/4140051.png",
        height: "165",
        weight: "49",
        form: FormEnum.SLIM,
      },
      picks: [
        {
          id: "dummy_pick_id1",
          name: "가짜 옷 1호",
          price: 30000,
          image:
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSbdg4cbR-mZsRLhUw7U8LPx5DW99HGLhKlMQ&s",
          platform: {
            name: "29cm",
            image:
              "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAMAAABF0y+mAAAAe1BMVEUAAACtrarj5eb19vempqY2MzC7uLXk4+Hx8fGmqamusK/P0dD////FxsZHR0X8+/nGx8LFysyRk5B5fX5+fnphY2ago550dXlaWligoKCztLY4OTciIiCsq610cm6Li40tLSsbGxm9wrlMS0u3ubHs8fT0+/+EgoLY1dcDqKGoAAAAlElEQVR4AeSPgwEEQQxFs8b8tW30X+Gt1cINk7yQ/mxxvCBugiQrqvZmvKAzGItgWopuw3kyFx6Rj4BI5YkoRPSAcbK+SCnDKuUo6LNKVFRDXnND/LAaa0Z1zW0jfrNcQLM1BNaC/zCgO5rrhzF7Q/XOpCP6znk5S3DeLIVqyLIxLWLDD/kbGgoPAPpacv5NgmGkAQAbCgckaxy7FQAAAABJRU5ErkJggg==",
          },
          review: {
            fit: "기장이 짧다(2명), 핏이 예쁘다(3명)",
            size: "작다(3명), 정사이즈다(1명) ",
            color: "사진과 색이 다르다(1명), 색감이 무난하다(1명)",
            quality: "소재가 시원하다(6명), 질이 좋다(1명)",
            reviews: [
              {
                id: "dummy_id1",
                image:
                  "https://sitem.ssgcdn.com/24/78/21/item/1000561217824_i1_750.jpg",
              },
              {
                id: "dummy_id2",
                image:
                  "https://m.w-girlz.co.kr/web/product/big/202207/1ecc2287df3577b18f3b792d67ffaf0a.jpg",
              },
              {
                id: "dummy_id3",
                image:
                  "https://cafe24.poxo.com/ec01/egoodshoping/jNWAR67N/rbqGfE/mXzgXenuQUJtwGtZYw45Y9EoR3mKewIlgLHTxQActRFQCuAL/6YE7idJBikKVcoq96BA/Q==/_/web/product/big/202312/db2cc4e275aea26dde477d2e71c61209.jpg",
              },
            ],
          },
        },
        {
          id: "dummy_pick_id2",
          name: "너무 이쁜 가짜 옷 2호",
          price: 35000,
          image:
            "https://m.w-girlz.co.kr/web/product/big/202201/64b2c98016a02d38bd9ae86550816306.jpg",
          platform: {
            name: "zigzag",
            image:
              "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAmVBMVEX5buMAAAD/cOjXYcOhSZTgY8z/cuxIH0D2atv5buL/cun7beP4buT/cOlNI0jaYMn/c+/tadibRo5uMmXKWbg6GzJdKla/VKwRBg2OQ4XmZs5YKFC4VKgiDR6GPnvybN5+OG0yFSutTp9UJkgABgAjCyCYR4oYDBwpFiphKVsxFzAYDBZvMGQ3GTh9N3AeDRobCw9CID3LWbU3fk36AAADr0lEQVR4nO2d21IbMRBEhbDXrHbXd7DBgDEJBMIlgf//uBR54CE41aI8KU07fb5gT0lujaTZdQhCCCGEEEIIIYQQQgghhBBCCCGEEEIIIYQQQgjxnxJNSaV1PjKZ9iyZrtvSRn/SP7Al7bthL5YW+oCx4aS0z0dsDWf+htDYcF5aZwumhsd1aZ0tmBpOHU5SW8OzprTOFiwNT7p9n6WLrrTNNgwNl1Vpma0YGp66HEJLwzN3Jelv7AxXPofQ0PDc42IYDA0v1qVV/oKZ4aXTSWpnOHI6Sc0Mh9FjxfaGlaHXnDEzXHrNGTPDK58V2xtGhiOvv0Irw02174bXbnPGytDhIeI7JoZXDq8r3ukfDwDDC2h46HiShlAhxlBwUNphN9ICGjq8rPgM1Rck+NVzzmSAo+jebz2TQ3cKDefR5wFNHvX6DgneVB7PgbPpcM7cet3c5xFPoOGEeY6GMIGCK+qcabtbaDj3XLFh6m9I8O6o9DPuRDOHQ3jLPYTVPTQclX7G3TiDgiuXl6LZpCk0XFDXM6H5jgQfqEcwRJwzbi8r8qgeoWGfegwbXM8MxqUfcicycsbvZUUW9RM05K5nEs6ZZ+qiO1QzaDiiTtJmDQWHNXWSdj1o6PmyIoN4jASX3IeIGfXMjHrfVGfUM74vKxDdZIkEN9xLRcQ5Q35ZEQfQkDxnDqHgjHq1D90zNJwH6s19DQVdvlmRT7yGhuSXFQnvm/qln3EnWpwzJ9SLYZ1wziyoK7aw/oEEL7hzpjuHQ3hKfgK1gobc9UwzgoLDMfUsTZfQkDxnWrhv+kk9gjlNXpfUQ1iP8b7JccdzBg1u8lolasOMJi/yQ8QW1jMP3IthxDnz7O/bHp+hw/WM2ze4ssjImSfqpSIk3ORFnjM1bL448PsGVwZtzqVox50zuJl0xH2IiJsvXko/4m50uPmiR52kbYLNpF4/m5BHTs48crflJ5wzr9STNOPlmA23YMQ5M+U2rF+g4aQ+Anj+mWbkTAaeL2wSbr4gN8T1DLlhws0X5Ibjmz03TPhSlNxwjJtJyQ1tcsaxoVHO+DVsM5q8uA3Tq5GgX0PcfMFt2FrljFfDNuEmL27DUOF9E7dhRjMpuWFGMym5IX45htww4SYvbsM2DffcMOImL3LDDjd5cRs2a/ytMm7DjC/ssBvCL3mxGxr/CYs/Q9uc8WgYbQX9GbbrQ1vc9aK0re1/knF3EwkhhBBCCCGEEEIIIYQQQgghhBBCCCGEEEIIIf4hvwBDyjkTw12CjgAAAABJRU5ErkJggg==",
          },
          review: {
            fit: "옷이 너무 예뻐요(10명)",
            size: "작다(3명), 정사이즈다(1명) ",
            color: "사진과 색이 다르다(1명), 색감이 무난하다(1명)",
            quality: "소재가 시원하다(6명), 질이 좋다(1명)",
            reviews: [
              {
                id: "dummy_id1",
                image:
                  "https://sitem.ssgcdn.com/24/78/21/item/1000561217824_i1_750.jpg",
              },
              {
                id: "dummy_id2",
                image:
                  "https://m.w-girlz.co.kr/web/product/big/202207/1ecc2287df3577b18f3b792d67ffaf0a.jpg",
              },
              {
                id: "dummy_id3",
                image:
                  "https://cafe24.poxo.com/ec01/egoodshoping/jNWAR67N/rbqGfE/mXzgXenuQUJtwGtZYw45Y9EoR3mKewIlgLHTxQActRFQCuAL/6YE7idJBikKVcoq96BA/Q==/_/web/product/big/202312/db2cc4e275aea26dde477d2e71c61209.jpg",
              },
              {
                id: "dummy_id4",
                image:
                  "https://sitem.ssgcdn.com/24/78/21/item/1000561217824_i1_750.jpg",
              },
              {
                id: "dummy_id5",
                image:
                  "https://m.w-girlz.co.kr/web/product/big/202207/1ecc2287df3577b18f3b792d67ffaf0a.jpg",
              },
              {
                id: "dummy_id6",
                image:
                  "https://cafe24.poxo.com/ec01/egoodshoping/jNWAR67N/rbqGfE/mXzgXenuQUJtwGtZYw45Y9EoR3mKewIlgLHTxQActRFQCuAL/6YE7idJBikKVcoq96BA/Q==/_/web/product/big/202312/db2cc4e275aea26dde477d2e71c61209.jpg",
              },
            ],
          },
        },
      ],
    };
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
      if (!res) {
        throw new ErrorDomain("Cart not found by userId", 404);
      }
      return {
        cart: {
          name: cart.name,
          host: { id: cart.userId, name: "testUser" },
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

const sampleItems = [
  {
    image: "dummy/picks/image1.png",
    price: "53,677원",
    name: "Halter-neck Knit top",
  },
  {
    image: "dummy/picks/image2.png",
    price: "29,900원",
    name: "무테드 슬로건 반팔 티",
  },
  {
    image: "dummy/picks/image3.png",
    price: "31,900원",
    name: "골지 브라 오프더숄더",
  },
  {
    image: "dummy/picks/image3.png",
    price: "31,900원",
    name: "골지 브라 오프더숄더",
  },
];
