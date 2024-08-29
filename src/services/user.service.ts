import { CartRepository } from "@src/adapters/persistence/repository/cart.repository";
import { PickRepository } from "@src/adapters/persistence/repository/pick.repository";
import { UserRepository } from "@src/adapters/persistence/repository/user.repository";
import { ErrorDomain } from "@src/domain/error.domain";
import { PickDomain } from "@src/domain/pick.domain";
import { UserDomain } from "@src/domain/user.domain";
import { FormEnum } from "@src/enum/form.enum";
import axios from "axios";

const userRepository = new UserRepository();
const pickRepository = new PickRepository();
const cartRepository = new CartRepository();
export class UserService {
  async getProfile(userId: string) {
    const user: UserDomain | null = await userRepository.getById(userId);
    if (!user) {
      throw new ErrorDomain("Nor found user", 404);
    }
    return user;
  }

  async login() {
    const userDomain = new UserDomain(
      "testUser",
      "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFiQ7RUFUqHT9W8Mg6JOvvYWdpAvX_jqB4VA&s",
      165,
      49,
      FormEnum.SLIM
    );
    const user = await userRepository.create(userDomain);
    return user;
  }

  async updateProfile(
    id: string,
    name?: string,
    height?: number,
    weight?: number,
    form?: FormEnum
  ) {
    if (!(form !== undefined && Object.values(FormEnum).includes(form))) {
      throw new ErrorDomain("form is not valid", 400);
    }
    const updatedUser: UserDomain | null = await userRepository.update(
      id,
      name,
      undefined,
      height,
      weight,
      form
    );
    if (!updatedUser) {
      throw new ErrorDomain("updated user info failed", 500);
    }
    return updatedUser;
  }

  async withdrawAccount(userId: string): Promise<boolean> {
    const picks: PickDomain[] = await pickRepository.findByUserIdWithoutPage(
      userId
    );
    const pickIds = picks.map((el) => el.id!);

    await pickRepository.delete(pickIds);
    await cartRepository.deletePickFromAllCart(pickIds);

    const carts = await cartRepository.findByUserIdWithoutPage(userId);
    carts.forEach(async (cart) => {
      if (cart.id) {
        await cartRepository.delete(cart.id);
      }
    });
    await userRepository.delete(userId);
    return true;
  }
}
