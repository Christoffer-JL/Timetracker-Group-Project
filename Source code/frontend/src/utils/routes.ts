import Dashboard from "./../pages/Dashboard.svelte";
import Login from "./../pages/login.svelte";

export const routes = {
  // Exact path
  "/": Dashboard,

  // Using named parameters, with last being optional
  "/login": Login,

  // Wildcard parameter
  "/book/*": Book,

  // Catch-all
  // This is optional, but if present it must be the last
  "*": NotFound,
};
