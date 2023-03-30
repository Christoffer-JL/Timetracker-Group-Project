<script>
  import Pen from "../assets/edit.png";
  import { createRole } from "../api";
  import { apiUrl } from "../apiConfig";

  export let addRole = "";
  export let privileged = false;
  export let projectId = "";

  export let onUpdate = async () => {};

  let newRole = "";

  let toggleShow = false;

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (newRole.trim() != "") {
      addRole = newRole.trim();
      await toggleDialog();
      await createNewRole();
      await onUpdate();
    }
  };

  const createNewRole = async () => {
    const data = {
      name: newRole,
      projectId: projectId,
      isPrivileged: privileged,
    };
    const response = await fetch(`${apiUrl}/roles?projectId=` + projectId, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
      credentials: "include",
    });
    const res = await response.json();
    console.log(res);
  };

  function handlePriviligedBox(event) {
    privileged = event.target.checked;
  }

  const toggleDialog = () => {
    toggleShow = !toggleShow;
  };
</script>

{#if !toggleShow}
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="addRole" on:click={() => toggleDialog()}>
    <img class="pen" src={Pen} alt="+" />
    <h1>Create role</h1>
  </div>
{:else}
  <form
    class="addRole form"
    on:submit={async (event) => await handleSubmit(event)}
  >
    <div class="input-container">
      <input
        class="textField"
        type="text"
        bind:value={newRole}
        placeholder="Role name"
      />
      <label>
        <input
          type="checkbox"
          on:change={handlePriviligedBox}
          bind:value={privileged}
        />
        Privileged
      </label>
      <div class="button-container">
        <button class="btn submit" type="submit">Create Role</button>
        <button class="btn cancel" on:click={toggleDialog}>Cancel</button>
      </div>
    </div>
  </form>
{/if}

<style>
  .addRole {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background-color: #282828;
    border-radius: 0px;
    width: 300px;
    height: 400px;
    border-radius: 5px;
    margin: 40px;
    cursor: pointer;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
    transition: all 0.2s ease-in-out;
  }

  .addRole:hover {
    background-color: #2c2c2c;
    border: none;
  }

  .form {
    cursor: default;
  }

  .textField {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 50%;
    box-sizing: border-box;
    font-weight: bolder;
    font-size: 16px;
    outline: transparent;
    border: 1px solid rgba(255, 255, 255, 0.068);
    padding-left: auto;
    padding-right: auto;
    background-color: #00000000;
    text-align: center;
    border-radius: 5px;
    transition: 0.2s ease-in-out;
  }

  .textField:focus {
    outline: 1px solid rgba(255, 255, 255, 0.253);
  }

  .textField::-webkit-input-placeholder {
    font-style: italic;
    font-weight: lighter;
  }
  .textField::-moz-placeholder {
    font-style: italic;
    font-weight: lighter;
  }
  .textField::-moz-placeholder {
    font-style: italic;
    font-weight: lighter;
  }
  .textField::-ms-input-placeholder {
    font-style: italic;
    font-weight: lighter;
  }

  .btn {
    background-color: #000000;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px;
    margin-top: 10px;
    cursor: pointer;
    transition: all 0.2s ease-in-out;
    box-sizing: border-box;
  }

  .pen {
    width: 5rem;
  }

  .submit {
    margin-left: 1.5rem;
  }

  .submit:hover {
    background-color: #111111;
  }

  .cancel {
    background-color: #00000000;
    transition: all 0.2s ease-in-out;
  }

  .cancel:hover {
    text-decoration: underline;
  }

  h1 {
    font-size: 2em;
    color: white;
  }

  label {
    font-size: 15px;
  }
</style>
