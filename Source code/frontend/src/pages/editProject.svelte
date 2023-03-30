<script lang="ts">
  import RoleSection from "../components/roleSection.svelte";
  import Chip from "../components/chip.svelte";
  import CreateRoleBtn from "../components/createRolebtn.svelte";
  import Save from "../assets/save.svg";
  import Add from "../assets/add.png";
  import trashcan from "../assets/trashcan.svg";
  import { onMount } from "svelte";
  import { params, push } from "svelte-spa-router";
  import { deleteProject, getProject } from "../api";
  import AddActivityPopup from "../components/addActivityPopup.svelte";
  import { apiUrl } from "../apiConfig";

  let newRole = "";
  let isPrivileged = false;
  let activities = [];
  let projectId: string = "";
  let users = [];
  let roles = [];
  let open = false;
  let projectName = "";

  params.subscribe(async (val) => {
    if (val) {
      projectId = val.id;
      await getMembers();
      await getProjectRoles();
      await getsProject(projectId);
      await getActivities(projectId);
    }
  });

  const openPopup = () => {
    if (!open) {
      open = true;
    } else open = false;
  };

  const removeActivity = async (projectid) => {
    const response = await fetch(`${apiUrl}/activities/` + projectid, {
      credentials: "include",
      method: "DELETE",
    });
    let data = await fetch(`${apiUrl}/activities?projectId=` + projectId, {
      credentials: "include",
    });
    activities = await data.json();
    console.log(activities);
  };

  //function to get all users
  const getMembers = async () => {
    const response = await fetch(`${apiUrl}/users`, {
      credentials: "include",
    });
    const data = await response.json();
    users = data;
  };

  const remove = async () => {
    const data = await deleteProject(projectId);
    push("/");
  };

  const getProjectRoles = async () => {
    const response = await fetch(`${apiUrl}/roles?projectId=` + projectId, {
      credentials: "include",
    });
    const data = await response.json();
    roles = data;
  };

  const getsProject = async (id) => {
    if (id === "") {
      return;
    }
    const response = await fetch(`${apiUrl}/projects/` + id, {
      credentials: "include",
    });
    const data = await response.json();
    projectName = data.name;
    return data;
  };

  const getActivities = async (id) => {
    const response = await fetch(`${apiUrl}/activities?projectId=` + id, {
      credentials: "include",
    });
    const data = await response.json();
    activities = data;
  };

  const updateProject = async () => {
    const response = await fetch(`${apiUrl}/projects/` + projectId, {
      method: "PUT",
      credentials: "include",
      body: JSON.stringify({
        name: projectName,
      }),
      headers: {
        "Content-type": "Application/json",
      },
    });
    const data = await response.json();
    push("/");
  };

  /*TODO: REMOVE WHEN DB IS CHANGED!!!*/
  onMount(async () => {
    await getMembers();
    await getProjectRoles();
    await getsProject(projectId);
    await getActivities(projectId);
  });

  const onUpdate = async () => {
    open = false;
    await getProjectRoles();
    await getActivities(projectId);
  };
</script>

<div class="main__wrapper">
  <h1>Edit project</h1>
  <div class="project__details__wrapper">
    <div class="title__wrapper">
      <h2>Project name</h2>
      {#await getsProject(projectId) then project}
        <input
          type="text"
          placeholder="Enter a name"
          bind:value={projectName}
        />
      {:catch Error}
        error
      {/await}
    </div>
  </div>

  <h1>Manage team</h1>
  <div class="role__item__wrapper">
    <div class="role__item">
      <RoleSection {projectId} {users} {roles} {onUpdate} />
      <CreateRoleBtn
        bind:addRole={newRole}
        bind:privileged={isPrivileged}
        {projectId}
        {onUpdate}
      />
    </div>
  </div>

  <div
    class="btn save__btn"
    on:click={async () => await push("/projects/" + projectId + "/clockins")}
  >
    <p>Sign logs</p>
  </div>

  <h1>Activities</h1>
  <AddActivityPopup {projectId} {open} {onUpdate} />
  <div class="activity__wrapper">
    {#each activities as activity}
      <div class="activity__item__wrapper">
        <Chip
          text={activity.name}
          id={activity.id}
          removeChip={removeActivity}
        />
      </div>
    {/each}
  </div>
  <!-- svelte-ignore a11y-click-events-have-key-events -->
  <div class="btn add__btn" on:click={() => openPopup()}>
    <p>Add</p>
    <img class="add" src={Add} alt="add" />
  </div>
  <div class="button__wrapper">
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="btn save__btn" on:click={async () => await updateProject()}>
      <p>Save</p>
      <img class="save" src={Save} alt="save" />
    </div>
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="delete__btn" on:click={async () => await remove()}>
      <img class="trashcan" src={trashcan} alt="X" />
    </div>
  </div>
</div>

<style>
  .button__wrapper {
    display: flex;
    flex-direction: row;
    align-items: center;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .delete__btn {
    margin: auto;
    margin-left: 1rem;
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 120px;
    height: 60px;
    background: #bd1212b4;
    border-radius: 5px;
    cursor: pointer;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.3);
    transition: 0.2s ease-in-out;
    margin-bottom: 100px;
  }

  .delete__btn:hover {
    background: #bd1212;
  }

  .main__wrapper {
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    height: auto;
    width: calc(100vw - 350px);
    margin-left: 300px;
    left: 0;
    overflow: hidden;
    overflow-x: hidden;
  }

  h1 {
    position: relative;
    left: 0;
  }

  .project__details__wrapper {
    display: flex;
    flex-direction: row;
    justify-content: center;
    width: 100%;
  }

  .role__item {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
  }

  /*TODO: fix this*/
  .activity__wrapper {
    width: 80%;
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    align-items: center;
    justify-content: center;
  }

  ::-webkit-scrollbar {
    width: 20px;
  }

  ::-webkit-scrollbar-track {
    box-shadow: inset 0 0 5px grey;
    border-radius: 10px;
  }
  ::-webkit-scrollbar-thumb {
    background: #666666;
    border-radius: 10px;
  }

  h1 {
    color: white;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .btn {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 120px;
    height: 60px;
    background: #000;
    border-radius: 5px;
    cursor: pointer;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.3);
    transition: 0.2s ease-in-out;
    margin-bottom: 100px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .btn:hover {
    background: #00000048;
  }

  .btn > p {
    color: #fff;
    font-size: 1.5rem;
  }

  .save__btn {
    margin-top: 2.5rem;
  }

  .activity__item__wrapper {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    width: auto;
  }

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
  }

  .save {
    width: 2rem;
  }

  .trashcan {
    width: 2rem;
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

  @media only screen and (min-width: 900px) {
    .activity__wrapper {
      width: 50%;
    }
  }

  .add {
    width: 2rem;
  }
</style>
