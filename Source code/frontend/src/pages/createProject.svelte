<script lang="ts">
  import DescriptionInput from "../components/descriptionInput.svelte";
  import Add from "../assets/add.png";
  import { createProject } from "../api";
  import { push } from "svelte-spa-router";

  let name: string = "";

  const create = async () => {
    const data = await createProject({ name });

    if (data.id) {
      push("/projects/" + data.id);
    }
  };
</script>

<div class="wrapper">
  <h1>Create project</h1>
  <div class="project__details__wrapper">
    <div class="title__wrapper">
      <h2>Title</h2>
      <input type="text" placeholder="Enter a title" bind:value={name} />
    </div>
  </div>
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="btn save__btn" on:click={() => create()}>
    <p>Create</p>
    <img class="create" src={Add} alt="Create" />
  </div>
</div>

<style>
  .title__wrapper {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    width: 50%;
    margin-right: 1rem;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .title__wrapper > h2 {
    font-weight: lighter;
    margin-bottom: 5px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .title__wrapper > input {
    border: none;
    background-color: #000000;
    height: 80px;
    color: white;
    font-size: 16px;
    width: 100%;
    border-radius: 5px;
    font-size: 25px;
    padding-left: 15px;
    box-sizing: border-box;
  }

  .title__wrapper > input:focus {
    outline: none;
  }

  .wrapper {
    height: auto;
    width: calc(100vw - 350px);
    position: relative;
    left: 20px;
    top: 0;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .project__details__wrapper {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 50vh;
  }

  .btn {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 150px;
    height: 60px;
    background: #000;
    border-radius: 5px;
    cursor: pointer;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.3);
    transition: 0.2s ease-in-out;
    margin-right: auto;
    margin-left: auto;
  }

  .btn:hover {
    background: #00000048;
  }

  .btn > p {
    color: #fff;
    font-size: 1.5rem;
  }

  .create {
    width: 2rem;
    filter: brightness(80%);
  }
</style>
