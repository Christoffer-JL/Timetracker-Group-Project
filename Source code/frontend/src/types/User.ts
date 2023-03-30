import type { Base } from "./Base";

export interface User extends Base {
  name: string;
  email: string;
  phoneNumber: string;
  password?: string;
  isAdmin: boolean;
}
