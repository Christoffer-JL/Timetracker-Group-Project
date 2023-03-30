import { writable } from "svelte/store";
import type { User } from "../types/User";

export const users = writable<User[]>([]);
