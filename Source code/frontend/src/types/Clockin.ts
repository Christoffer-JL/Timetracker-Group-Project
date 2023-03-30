import type { RouteLoadedEvent } from "svelte-spa-router";
import type { Base } from "./Base";
import type { Role } from "./Role";
import type { User } from "./User";

export interface Clockin extends Base {
  start: Date;
  end: Date;
  description: string;
  user?: User;
  clockinSigned: null | User;
  role?: Role;
}
