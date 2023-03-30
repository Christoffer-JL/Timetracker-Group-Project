<script lang="ts">
  import RemoveBtn from "../assets/minus.svg";
  import Close from "../assets/close.svg";
  import Add from "../assets/add.png";
  import CustomSelect from "./customSelect.svelte";
  import type { User } from "../types/User";
  import type { Role } from "../types/Role";
  import { apiUrl } from "../apiConfig";
  export let projectId: string = "";
  export let roles: Role[];
  export let users: User[] = [];
  export let onUpdate = async () => {};

  let selectedUser;

  //function to remove role card if closewindowbtn is clicked
  const removeRole = async (role) => {
    const response = await fetch(`${apiUrl}/roles/` + role.id, {
      method: "DELETE",
      credentials: "include",
    });
    await onUpdate();
  };

  const removeMember = async (role, user) => {
    const response = await fetch(`${apiUrl}/roles/` + role.id + "/remove", {
      credentials: "include",
      method: "PUT",
      body: JSON.stringify({
        userId: user.id,
      }),
      headers: {
        "Content-type": "Application/json",
      },
    });
    await onUpdate();
  };

  const addMember = async (role, selectedUser) => {
    if (
      !confirm(
        "Är du säker på att du vill lägga till dessa användare till det aktuella projektet"
      )
    ) {
      return;
    }

    const response = await fetch(`${apiUrl}/roles/` + role.id + "/add", {
      credentials: "include",
      method: "PUT",
      body: JSON.stringify({
        userId: selectedUser,
      }),
      headers: {
        "Content-type": "Application/json",
      },
    });
    await onUpdate();
    if (!response.ok) {
      alert("User already exists in a role");
    }
  };
</script>

{#each roles as role}
  <div class="role__card">
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <img
      class="close"
      src={Close}
      alt="x"
      on:click={async () => await removeRole(role)}
    />
    <p class="role__name">{role.name}:</p>
    <div class="member__list">
      {#each role.users as user}
        <div class="member__list__item">
          <p class="name">{user.name}</p>
          <!-- svelte-ignore a11y-click-events-have-key-events -->
          <img
            src={RemoveBtn}
            alt="x"
            on:click={async () => await removeMember(role, user)}
          />
        </div>
      {/each}
    </div>
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="input__wrapper">
      <CustomSelect
        values={users.map((user) => {
          return {
            id: user.id,
            label: user.name,
          };
        })}
        selected={""}
        onChange={(value) => (selectedUser = value)}
      />
      <div
        class="add__btn"
        on:click={async () => await addMember(role, selectedUser)}
      >
        <p>Add</p>
        <img class="add" src={Add} alt="+" />
      </div>
    </div>
  </div>
{/each}

<style>
  .role__card > img {
    position: absolute;
    top: -10px;
    right: -10px;
    width: 30px;
    height: 30px;
    cursor: pointer;
    filter: drop-shadow(0px 4px 4px rgba(0, 0, 0, 0.25));
  }

  .role__card {
    position: relative;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    width: 300px;
    height: 400px;
    background-color: #282828;
    color: white;
    font-size: 18px;
    border-radius: 5px;
    margin: 40px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }
  .input__wrapper {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    margin-bottom: 0;
    position: relative;
  }

  .add__btn {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: auto;
    height: 50px;
    border: none;
    padding: 0 1rem;
    background-color: transparent;
    border-radius: 5px;
    cursor: pointer;
    outline: none;
    background-color: #234b1b81;
    transition: all 0.2s ease-in-out;
  }
  .add__btn > p {
    font-size: 16px;
    font-weight: bold;
    color: #fff;
    margin-right: 10px;
  }

  .add__btn:hover {
    background-color: #20581591;
  }

  .dropdown__list {
    position: absolute;
    align-self: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: #fff;
    color: black;
    display: block;
    font-size: medium;
    font-weight: bold;
    width: 100%;
    overflow-y: scroll;
    scroll-behavior: smooth;
    scroll-margin: 2px;
    scrollbar-width: thin;
    top: 35px;
    z-index: 9999;
  }

  .member__input {
    box-sizing: border-box;
    font-weight: normal;
    font-size: 16px;
    background-color: #d9d9d9;
    color: black;
    overflow: hidden;
    text-overflow: ellipsis;
    outline: #282828;
    transition: all 0.2s ease-in-out;
    border: 0px solid #50505065;
    border-radius: 5px;
    width: 100%;
    height: 35px;
    margin-bottom: 0;
  }

  .member__input:focus-within {
    outline: 1px solid #a5a5a5;
  }

  .member__list {
    height: 250px;
    width: 100%;
    overflow-y: scroll;
    scroll-behavior: smooth;
    scroll-margin: 2px;
    scrollbar-width: thin;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .member__list__item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-left: 16px;
    padding-right: 16px;
    transition: all 0.2s ease-in-out;
    height: 2rem;
  }

  .add {
    width: 1.5rem;
  }

  .close {
    width: 5rem;
  }
  .member__list__item:hover {
    background-color: #4d4d4d;
    border-radius: 2px;
    padding: auto;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.2);
  }

  .member__list__item > p {
    font-size: 19px;
    margin-top: 0px;
    margin-bottom: 0px;
    box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.01);
  }

  .member__list__item > img {
    height: auto;
    width: 18px;
    box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.05);
    cursor: pointer;
  }

  .role__name {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: auto;
  }

  /*Ugg ugg css här under.....*/
  /*I want to make the scrollbar background transparent on hover, but it doesn't work💀*/
  .member__list:hover {
    scrollbar-color: #acacac transparent;
  }

  /*Makes it work for chrome💀*/
  ::-webkit-scrollbar {
    height: 4px;
    width: 4px;
    background: #000;
    background-color: #282828;
  }

  ::-webkit-scrollbar-thumb {
    background: #acacac;
    -webkit-border-radius: 1ex;
  }
</style>
