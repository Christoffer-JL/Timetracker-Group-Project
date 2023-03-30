<script>
  import { onMount } from "svelte";
  import projectComp from "../components/AccountComp.svelte";
  import { getAllProjects, getAllUsers } from "../api";
  import { link } from "svelte-spa-router";
  import Plus from "./../assets/add.png";
  import { apiUrl } from "../apiConfig";

  let nProjects;
  let allProjects = [];
  let setSize = 100;
  let index = 0;
  let ProjectsLength = 0;

  onMount(async () => {
    allProjects = await getAllProjects();
    setSize = allProjects.length;
    nProjects = allProjects.length;
    sortprojects();
  });

  let projects = new Array(setSize);

  $: {
    if (!query) {
      ProjectsLength = 0;
      for (
        let ia = index, ib = 0;
        ia < (nProjects - index < setSize ? nProjects - index : setSize),
          ib < setSize;
        ia++, ib++
      ) {
        try {
          let project = allProjects[ia];
          projects[ib] = [
            projectComp,
            {
              userId: project.id,
              accountName: project.name,
              isAdmin: project.isAdmin === "true",
              isProject: true,
            },
          ];
          ProjectsLength++;
        } catch (e) {}
      }
    }
  }

  let pages = 1;

  $: {
    pages = Math.ceil(nProjects / setSize);
  }

  let page = 1;

  let prev = () => {
    index = index - setSize >= 0 ? index - setSize : 0;
    page = index == 0 ? 1 : page - 1;
  };

  let next = () => {
    index = index + setSize > nProjects ? nProjects : index + setSize;
    page++;
  };

  let adminComp = (a, b) => {
    if (a.isAdmin && b.isAdmin) return 0;
    else if (a.isAdmin) return 1;
    else if (b.isAdmin) return -1;
    else return 0;
  };

  let sortprojects = () => {
    allProjects.sort(adminComp);
  };

  let query = "";
  let results = [];
  let searchedprojects = [];

  $: {
    if (query) {
      results = [];
      results = allProjects.filter((project) => {
        return project.name.toLowerCase().match(query.toLowerCase());
      });

      searchedprojects = [];
      for (let i = 0; i < results.length; i++) {
        let project = results[i];
        searchedprojects.push([
          projectComp,
          {
            userId: project.id,
            projectName: project.name,
            isAdmin: true,
          },
        ]);
      }
    }
  }
</script>

<div class="top">
  <div class="toolbar">
    <div>
      <input
        class="search__project"
        placeholder="Search project"
        bind:value={query}
      />
    </div>
    <div />
  </div>

  <div class="row">
    {#if !query}
      {#each (setSize < ProjectsLength ? projects : projects.splice(0, ProjectsLength)) || [] as [component, props]}
        <svelte:component this={component} {...props}>project</svelte:component>
      {/each}
    {:else}
      {#each searchedprojects as [component, props]}
        <svelte:component this={component} {...props}>project</svelte:component>
      {/each}
    {/if}
  </div>
  <p class="project__nbr">
    projects found: {query ? results.length : nProjects}
  </p>
</div>

<a class="project__link" href="/admin/projects/create" use:link>
  <div class="project__btn">
    <p>Create project</p>
    <img class="plus" src={Plus} alt="plus" />
  </div>
</a>

<style>
  .top {
    width: 75%;
    margin: auto;
  }

  .row {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    text-align: center;
    align-items: center;
    padding: 1em;
    margin-left: 0;
    margin-right: 0;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
  }

  .search__project {
    width: 100%;
    height: 40px;
    background: #2c2c2c;
    border: 0px;
    margin-top: 0.5rem;
    border-radius: 5px;
    padding: 0 10px;
    outline: none;
    font-size: large;
    filter: drop-shadow(1px 3px 0px rgba(0, 0, 0, 0.3));
  }

  .project__nbr {
    text-align: center;
    font-size: 1.5rem;
    margin-top: 1rem;
  }

  .project__link {
    text-decoration: none;
    position: fixed;
    right: 100px;
    bottom: 50px;
  }

  .project__btn {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 70px;
    height: 70px;
    background: #000000;
    border: 0px;
    margin-top: 0.5rem;
    border-radius: 500px;
    outline: none;
    font-size: large;
    filter: drop-shadow(1px 3px 0px rgba(0, 0, 0, 0.3));
    float: inline-end;
    transition: all 0.2s ease-in-out;
  }

  .project__btn:hover {
    background: rgb(15, 15, 15);
    width: 250px;
  }

  .project__btn p {
    color: #fff;
    text-decoration: none;
    margin-right: 0.5rem;
    font-size: medium;
    font-weight: bold;
    opacity: 0;
    position: absolute;
    left: 40%;
    transform: translateX(-50%);
    text-overflow: none;
    white-space: nowrap;
  }

  .project__btn:hover p {
    opacity: 1;
    transition: opacity 0.5s ease-out;
  }

  .plus {
    width: 30px;
    height: 30px;
    transition: all 0.2s ease-in-out;
  }

  .project__btn:hover .plus {
    transform: translateX(200%);
    margin-left: 2rem;
  }

  p {
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }
</style>
