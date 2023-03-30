<script lang="ts">
  // TODO Show in popup instead of page

  import { createUser } from "./../api";
  import { pop } from "svelte-spa-router";

  let name = "";
  let email = "";
  let phone = "";
  let password = "";
  let admin = false;

  const createAccount = async () => {
    const confirmCreate = confirm("Create account?");

    if (confirmCreate) {
      const data = await createUser({
        name: name,
        email: email,
        phoneNumber: phone,
        password: password,
        isAdmin: admin,
      });

      if (data.id) {
        await pop();
      } else {
        alert(
          "Lösenord ska bestå av minst 6 tecken, måste innehålla minst en stor bokstav (asciivärden 65-90), minst ett specialtecken (asciivärden 33-47, 58-64, 91-96) och minst en siffra (asciivärden 48-57)"
        );
      }
    }
  };

  const cancel = async () => await pop();
</script>

<div class="main">
  <div style="margin: auto;">
    <div class="h2"><h2>Create account</h2></div>

    <div class="accountPanel">
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
          type="password"
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
        <button on:click={createAccount}>Create</button>
        <button on:click={cancel}>Cancel</button>
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
    padding: 0.6em 1.2em;
    font-size: 1em;
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
</style>
