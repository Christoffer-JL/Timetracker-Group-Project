<script lang="ts">
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import type { Project } from "../types/Project";
  import type { Role } from "../types/Role";
  import { apiUrl } from "../apiConfig";

  let projects: Project[] = [];
  let roles: Role[] = [];

  const getProjects = async () => {
    const response = await fetch(`${apiUrl}/projects?userId=` + $params.id, {
      credentials: "include",
    });

    const data = await response.json();

    if (Array.isArray(data)) {
      projects = data;
    }
  };

  const getRoles = async () => {
    const response = await fetch(`${apiUrl}/roles?userId=` + $params.id, {
      credentials: "include",
    });

    const data = await response.json();

    if (Array.isArray(data)) {
      roles = data;
    }
  };

  onMount(async () => {
    await getProjects();
    await getRoles();
  });
</script>

<div class="section__wrapper">
  <div class="projects">
    <h2 class="section__title">Projekt</h2>
    <div class="projects__section">
      <!--TODO: Add projects__item for each project that user is assigned to-->
      {#each projects as project}
        <div class="projects__item">
          <p class="time">{project.name}</p>
          <p class="project__name">
            {roles.find((role) => project.name === role.projectName)?.name}
          </p>
        </div>
      {/each}
    </div>
  </div>
</div>

<style>
  .section__wrapper {
    height: auto;
    width: auto;
    position: absolute;
    left: calc(5vw + 300px);
    top: 0;
  }

  .projects {
    margin-top: 100px;
  }

  .projects__item {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 0fr;
    align-items: center;
    width: 50vw;
    height: 50px;
    background-color: #000000;
    border-radius: 5px;
    margin-bottom: 5px;
  }

  .projects__item > * {
    margin-left: 10px;
    margin-right: 10px;
  }

  .project__description__wrapper {
    width: auto;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: wrap;
  }

  .project__description {
    font-size: 14px;
    margin: 0;
  }
</style>
