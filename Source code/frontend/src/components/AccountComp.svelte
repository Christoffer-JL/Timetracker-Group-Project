<script>
  import { link } from "svelte-spa-router";
  import Avatar from "./Avatar.svelte";

  export let userId;
  export let accountAvatar = "/src/assets/nop.jpg";
  export let accountName;
  export let isAdmin = false;
  export let isProject = false;
</script>

<a
  href={isProject ? "/projects/" + userId : "/admin/accounts/" + userId}
  use:link
>
  <div style="margin: 1em;">
    {#if isAdmin}
      <p>󰆥</p>
    {:else}
      <p />
    {/if}
    <Avatar
      avatar={"https://ui-avatars.com/api/?name=" + accountName ||
        accountAvatar}
    />
    <p>{accountName}</p>
  </div>
</a>

<style>
  a {
    text-decoration: none;
    font-size: 18px;
    font-weight: 600;
  }
  div {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 200px;
    height: 250px;
    background-color: #282828;
    color: white;
    border-radius: 5px;
    margin: 40px;
    transition: all 0.2s ease-in-out;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }
  div:hover {
    background-color: #4d4d4d;
    padding: auto;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.2);
  }

  p {
    padding: 0;
  }
</style>
