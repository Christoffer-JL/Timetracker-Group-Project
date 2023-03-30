import { writable } from "svelte/store";
import type { Role } from "../types/Role";
import type { User } from "./../types/User";

export const me = writable<User | undefined>(undefined);
export const meRoles = writable<Role[]>(undefined);
