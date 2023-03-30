<script lang="ts">
  import { link, push } from "svelte-spa-router";
  import { logout } from "../api";
  import { me } from "../stores/Auth";
  import logo from "./../assets/logo.svg";
</script>

<div class="navbararea">
  <div class="navbar">
    <img src={logo} alt="logo" class="navbar__logo" />
    <div class="navbar__links">
      <a href="/" use:link>Dashboard</a>
      <a href="/statistics" use:link>Statistics</a>

      {#if $me?.isAdmin}
        <h3>Admin</h3>
        <a href="/admin/projects/create" use:link class="noMarginTop"
          >Create Project</a
        >
        <a href="/admin/clockins" use:link>Sign clockins</a>
        <a href="/admin/accounts" use:link>Users</a>
        <a href="/admin/projects" use:link>Projects</a>
      {/if}
    </div>
    <div class="navbar__bottom">
      <!-- svelte-ignore a11y-missing-attribute -->
      <!-- svelte-ignore a11y-click-events-have-key-events -->
      <a
        on:click={async () => {
          await logout();
          me.set(undefined);
          await push("/login");
          location.reload();
        }}>Logout</a
      >
      <a href="/account" use:link>Account</a>
    </div>
  </div>
</div>

<style>
  .navbararea {
    width: 100%;
    height: 100px;
    background-color: #2c2c2c;
    display: flex;
    justify-content: center;
    align-items: center;
    left: 0;
    top: 0;
    width: 300px;
    height: 100vh;
    position: fixed;
    box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.2);
    z-index: 9999;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version */
  }

  .navbar {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .navbar__logo {
    margin-top: 2rem;
  }

  .navbar__links {
    margin-top: 5vh;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    align-items: center;
    width: 100%;
    padding: 0 20px;
  }

  .navbar__links > h3 {
    font-family: "Inter";
    font-style: normal;
    font-weight: 300;
    font-size: 25px;
  }

  .navbar__links > a {
    margin-top: 45px;
    font-family: "Inter";
    font-style: normal;
    font-weight: 300;
    font-size: 25px;
    color: #ffffff6c;
    transition: all 0.2s ease-in-out;
    transform: translate3d(0, 0, 0);
    text-decoration: none;
  }

  .navbar__links > h3 {
    margin-top: 45px;
  }

  .noMarginTop {
    margin-top: 0 !important;
  }

  .navbar__links > a:hover {
    color: #dfdfdf;
  }

  .navbar__bottom {
    position: absolute;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 90%;
    padding: 0 20px;
    bottom: 0;
    margin-bottom: 5px;
  }

  .navbar__bottom > a {
    font-family: "Inter";
    font-style: normal;
    color: #ffffff6c;
    font-size: 12px;
    transition: all 0.2s ease-in-out;
    text-decoration: none;
    font-weight: lighter;
    cursor: pointer;
  }

  .navbar__bottom > a:hover {
    color: #dfdfdf;
  }
</style>
