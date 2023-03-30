import { writable } from "svelte/store";
import { getAllActivities, getAllClockins } from "../api";
import type { Activity } from "../types/Activity";
import type { Clockin } from "../types/Clockin";

export const clockins = writable<Clockin[]>([]);

export const getById = (id: string): Clockin | undefined => {
  let clockin: Clockin | undefined;

  clockins.subscribe(
    (clockins) => (clockin = clockins.find((cloc) => (cloc.id = id)))
  );

  return clockin;
};

export const refresh = async () => {
  await getAll();
};

const getAll = async () => {
  const newClockins = await getAllClockins();

  if (Array.isArray(newClockins)) {
    clockins.set(newClockins);
  }
};

refresh();

setInterval(() => {
  refresh();
}, 1000 * 60 * 5);
