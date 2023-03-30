<script lang="ts">
  import { getUser } from "./api";
  import type { User } from "./types/User";
  import EditProject from "./pages/editProject.svelte";
  import Dashboard from "./pages/Dashboard.svelte";
  import Login from "./pages/Login.svelte";
  import Account from "./pages/account.svelte";
  import Statistics from "./pages/Statistics.svelte";
  import UnsignedClockins from "./pages/unsignedClockins.svelte";
  import { me, meRoles } from "./stores/Auth";
  import Router, { push, location } from "svelte-spa-router";
  import { wrap } from "svelte-spa-router/wrap";
  import Navbar from "./components/Navbar.svelte";
  import CreateProject from "./pages/createProject.svelte";
  import Accounts from "./pages/Accounts.svelte";
  import Manage from "./pages/Manage.svelte";
  import NotFound from "./pages/notFound.svelte";
  import Create from "./pages/Create.svelte";
  import Logs from "./pages/logs.svelte";
  import Projects from "./pages/userProjects.svelte";
  import UserProjects from "./pages/userProjects.svelte";
  import ProjectsView from "./pages/ProjectsView.svelte";
  import { onMount } from "svelte";
  import { apiUrl } from "./apiConfig";

  const updateStore = async () => {
    try {
      const user: User = await getUser("@me");

      if (user.id) {
        me.set(user);

        const response = await fetch(`${apiUrl}/roles?userId=` + user.id, {
          credentials: "include",
        });

        if (response.ok) {
          meRoles.set(await response.json());

          await push("/");
        }
      } else {
        await push("/login");
      }
    } catch (exception) {
      await push("/login");
      console.warn(exception);
    }
  };

  let user: User | undefined;
  let locationValue: string;

  onMount(async () => {
    await updateStore();
  });

  me.subscribe((usr) => {
    user = usr;
  });

  location.subscribe((loc) => {
    locationValue = loc;
  });

  const routes = {
    // Using named parameters, with last being optional
    "/login": wrap({
      component: Login,
      conditions: [
        () => {
          return !user ? true : false;
        },
      ],
    }),

    "/projects/statistics": wrap({
      component: Statistics,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "/projects/:id/clockins": wrap({
      component: UnsignedClockins,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "/projects/:id": wrap({
      component: EditProject,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "/account": wrap({
      component: Account,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "/statistics": wrap({
      component: Statistics,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "/admin/accounts/:id/logs": wrap({
      component: Logs,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/accounts/create": wrap({
      component: Create,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/accounts": wrap({
      component: Accounts,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/projects": wrap({
      component: ProjectsView,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/projects/create": wrap({
      component: CreateProject,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/accounts/:id/projects": wrap({
      component: UserProjects,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/accounts/:id": wrap({
      component: Manage,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    "/admin/clockins": wrap({
      component: UnsignedClockins,
      conditions: [
        () => {
          return user.isAdmin ? true : false;
        },
      ],
    }),

    // Exact path
    "/": wrap({
      component: Dashboard,
      conditions: [
        () => {
          return !!user ? true : false;
        },
      ],
    }),

    "*": NotFound,
  };
</script>

{#if locationValue !== "/login"}
  <Navbar />
{/if}
<main class={locationValue !== "/login" && "main"}>
  <Router {routes} />
</main>

<style>
  .main {
    width: calc(100vw - 300px);
    float: right;
  }
</style>
