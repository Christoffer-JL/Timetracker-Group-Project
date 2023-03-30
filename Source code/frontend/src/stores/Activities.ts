import { writable } from "svelte/store";
import { getAllActivities } from "../api";
import type { Activity } from "../types/Activity";

export const activities = writable<Activity[]>([]);

export const getById = (id: string): Activity | undefined => {
  let activity: Activity | undefined;

  activities.subscribe(
    (activities) => (activity = activities.find((act) => (act.id = id)))
  );

  return activity;
};

export const refresh = () => {
  getAll();
};

const getAll = async () => {
  const newActivities = await getAllActivities();

  if (Array.isArray(newActivities)) {
    activities.set(newActivities);
  }
};

refresh();

setInterval(() => {
  refresh();
}, 1000 * 60 * 5);
