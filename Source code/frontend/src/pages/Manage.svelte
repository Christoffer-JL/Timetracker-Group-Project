<script lang="ts">
  import { params, push } from "svelte-spa-router";
  // TODO Show in popup instead of page

  import * as api from "../api";
  import { apiUrl } from "../apiConfig";

  let name = "";
  let email = "";
  let phone = "";
  let password = "";
  let admin = false;

  let id: string;

  const getAccount = async () => {
    let userData = await api.getUser(id);

    name = userData.name;
    email = userData.email;
    phone = userData.phoneNumber;
    password = userData.password;
    admin = userData.isAdmin;
  };

  params.subscribe(async (param) => {
    id = param.id;
    await getAccount();
  });

  const deleteAccount = async () => {
    const confirmDelete = confirm("Delete account?");

    if (confirmDelete) {
      const response = await api.deleteUser(id);
      history.back();
    }
  };

  const updateAccount = async () => {
    const confirmChanges = confirm("Confirm changes?");

    if (confirmChanges) {
      const response = await api.updateUser(id, {
        name: name,
        email: email,
        phoneNumber: phone,
        isAdmin: admin,
      });

      if (password) {
        const res = await fetch(`${apiUrl}/users/` + id + "/reset", {
          method: "PUT",
          credentials: "include",
          body: JSON.stringify({
            password: password,
          }),
        });

        if (!res.ok) {
          alert(
            "Lösenord ska bestå av minst 6 tecken, måste innehålla minst en stor bokstav (asciivärden 65-90), minst ett specialtecken (asciivärden 33-47, 58-64, 91-96) och minst en siffra (asciivärden 48-57)"
          );
        }
      }

      if (response.id) {
        getAccount();
      }
    }
  };

  const cancel = () => {
    history.back();
  };
</script>

<div class="main">
  <div style="margin: auto;">
    <div class="h2"><h2>Manage account</h2></div>

    <div style="display: inline-flex;">
      <div class="accountPanel">
        <div class="center" style="margin-top: 2rem; margin-bottom: 2rem;">
          <img
            style="width: 7rem;"
            src={"https://ui-avatars.com/api/?name=" + name}
            alt="Profile avatar"
          />
        </div>

        <label for="name">Name</label>
        <div class="accountInputBox">
          <input type="text" id="name" placeholder="Name" bind:value={name} />
        </div>

        <label for="email">E-mail</label>
        <div class="accountInputBox">
          <input
            type="email"
            id="email"
            placeholder="E-mail"
            bind:value={email}
          />
        </div>

        <label for="phone">Phone</label>
        <div class="accountInputBox">
          <input type="tel" id="phone" placeholder="Phone" bind:value={phone} />
        </div>

        <label for="password">Password</label>
        <div class="accountInputBox">
          <input
            type="text"
            id="password"
            placeholder="Password"
            bind:value={password}
          />
        </div>

        <div class="accountInputCheck">
          <label for="admin">Admin</label>
          <input type="checkbox" id="admin" bind:checked={admin} />
        </div>

        <div class="center">
          <button on:click={updateAccount}>Confirm</button>
          <button class="" on:click={cancel}>Close</button>
        </div>
      </div>

      <div class="accountPanel side">
        <button
          class="sideButton"
          on:click={async () => await push("/admin/accounts/" + id + "/logs")}
          >View logs</button
        >
        <button
          class="sideButton"
          on:click={async () =>
            await push("/admin/accounts/" + id + "/projects")}
          >Project list</button
        >

        <button class="sideButton sideButtonBottom" on:click={deleteAccount}
          >Delete account</button
        >
      </div>
    </div>
  </div>
</div>

<style>
  .main {
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-height: 100vh;
  }

  h2 {
    width: 100%;
    text-align: left;
  }

  .h2 {
    width: 20rem;
  }

  button {
    border-radius: 8px;
    border: 1px solid transparent;
    padding: 0.6rem 1.2rem;
    font-size: 1rem;
    font-weight: 500;
    font-family: inherit;
    background-color: #1a1a1a;
    cursor: pointer;
    transition: border-color 0.25s;
  }

  button:hover {
    border-color: #646cff;
  }

  button:focus,
  button:focus-visible {
    outline: 4px auto -webkit-focus-ring-color;
  }

  .center {
    text-align: center;
  }

  .accountPanel {
    background-color: #141414;
    border-radius: 6px;
    width: 20rem;
    height: 100%;
    padding: 5rem;
    padding-top: 3rem;
  }

  .accountInputBox {
    background-color: #2c2c2c;
    border-radius: 0.5rem;
    padding: 1rem;
    margin-top: 0.5rem;
    margin-bottom: 1.5rem;
  }

  .accountInputCheck {
    margin-top: 0.5rem;
    margin-bottom: 1.5rem;
  }

  .accountInputBox input {
    width: 100%;
    border: none;
    background: #2c2c2c;
    outline: none;
    font-size: medium;
  }

  .side {
    height: auto;
    width: 10em;
    margin-left: 4em;
    position: relative;
  }

  .sideButton {
    width: 10em;
    margin-top: 1em;
  }

  .sideButtonBottom {
    position: absolute;
    left: 5em;
    bottom: 3em;
  }
</style>
