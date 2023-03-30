<script lang="ts">
  import Add from "../assets/add.png";
  import Close from "../assets/close.svg";
  import { apiUrl } from "../apiConfig";
  export let projectId;
  export let open;
  export let onUpdate = async () => {
    open = false;
  };
  let name = "",
    code = "";

  const addActivity = async () => {
    if (name == "" || code == "") {
      alert("Please fill in all fields before submitting");
      return;
    }
    const response = await fetch(`${apiUrl}/activities`, {
      method: "POST",
      body: JSON.stringify({
        name: name,
        code: code,
        projectId: projectId,
      }),
      headers: {
        "Content-type": "Application/json",
      },
      credentials: "include",
    });
    await onUpdate();
    open = false;
  };
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->

{#if open}
  <dialog class={open ? "open" : "closed"}>
    <img
      class="closewindow"
      src={Close}
      alt="x"
      on:click={() => (open = false)}
    />
    <div class="popup__area">
      <h1>Add activity</h1>
      <div class="input__wrapper">
        <div class="input__area">
          <label for="name">Name</label>
          <input
            class="name__input"
            type="text"
            placeholder="Activity name"
            bind:value={name}
          />
        </div>
        <div class="input__area">
          <label for="code">Code</label>
          <input
            class="code__input"
            type="text"
            placeholder="num"
            bind:value={code}
          />
        </div>
      </div>
      <div on:click={() => addActivity()} class="button">
        <a>Add</a>
        <img src={Add} alt="add" />
      </div>
    </div>
  </dialog>
{/if}

<style>
  h1 {
    color: white;
  }

  .open {
    position: fixed;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    display: block;
    background-color: transparent;
    outline: transparent;
    border: transparent;
  }

  .closed {
    display: none;
  }
  .closewindow {
    position: absolute;
    top: -10px;
    right: -10px;
    width: 2rem;
    height: 2rem;
    margin: 1rem;
    cursor: pointer;
  }
  .popup__area {
    display: flex;
    flex-direction: column;
    align-items: center;
    top: 40%;
    left: 50%;
    background-color: #000000;
    border-radius: 10px;
    padding: 1rem;
  }

  .input__wrapper {
    display: flex;
    flex-direction: row;
    width: 100%;
    height: 100%;
    justify-content: space-evenly;
    align-items: center;
  }

  .input__area {
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    height: 100%;
    margin: 1rem;
  }

  .name__input {
    width: 100%;
    height: 2rem;
    border: none;
    border-radius: 5px;
    padding: 0.5rem;
    box-sizing: border-box;
    background-color: #282828;
    color: white;
  }

  .code__input {
    width: 50px;
    height: 2rem;
    border: none;
    border-radius: 5px;
    padding: 0.5rem;
    box-sizing: border-box;
    background-color: #282828;
    color: white;
  }

  input {
    transition: all 0.2s ease-in-out;
    outline: 1px solid #ffffff00;
    box-sizing: border-box;
  }

  input:focus-within {
    outline: 1px solid #ffffff4f;
  }

  .button {
    margin: 1rem;
    display: flex;
    flex-direction: row;
    align-items: space-evenly;
    width: auto;
    height: 2rem;
    border: none;
    border-radius: 5px;
    padding: 0.5rem;
    box-sizing: border-box;
    background-color: #282828;
    color: white;
    cursor: pointer;
    transition: all 0.2s ease-in-out;
  }
  .button > img {
    width: 1rem;
    height: 1rem;
    margin-left: 0.5rem;
  }

  .button:hover {
    background-color: #282828af;
    color: white;
  }
</style>
