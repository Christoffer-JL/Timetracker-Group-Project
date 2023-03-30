<script lang="ts">
  import StatList from "../components/statList.svelte";
  import type { ListData } from "../types/ListItem";
  import { ExportToCsv } from "export-to-csv";
  import { format } from "date-fns";
  import Download from "../assets/download.svg";
  import { onMount } from "svelte";
  import { location } from "svelte-spa-router";
  import { me } from "../stores/Auth";
  import type { Project } from "../types/Project";
  import CustomSelect from "../components/customSelect.svelte";
  import { apiUrl } from "../apiConfig";

  let formattedDate = format(new Date(), "yyyy-MM-dd");
  let lists: ListData[] = [];
  let projects: Project[] = [];
  let projectId: string = "";
  let totalTime: number = 0;
  let csvExporter: ExportToCsv;

  const options = {
    fieldSeparator: ": ",
    quoteStrings: "",
    decimalSeparator: ".",
    showLabels: true,
    showTitle: true,
    title: "Export date: " + formattedDate,
    useTextFile: false,
    useBom: true,
    headers: ["name", "time"],
    excludeKeys: ["id"],
    filename: "Statistics_" + formattedDate,
  };

  const convertToCSVJSON = (list: ListData[]) => {
    let formattedData: any[] = [];

    lists.forEach((list, key) => {
      formattedData.push({ name: list.name, time: list.totalTime });

      list.items.forEach((item) => {
        const formattedItem = {
          name: item.name,
          time: item.time,
        };
        formattedData.push(formattedItem);
      });
    });

    return formattedData;
  };

  const getStatistics = async () => {
    let data;

    if ($location !== "/statistics") {
      const response = await fetch(
        `${apiUrl}/projects/statistics/` + projectId,
        {
          credentials: "include",
        }
      );
      data = await response.json();
    } else {
      const response = await fetch(
        `${apiUrl}/users/` + $me.id + "/statistics?projectId=" + projectId,
        {
          credentials: "include",
        }
      );
      data = await response.json();
    }

    if (data.totalTime) {
      totalTime = data.totalTime;
      lists = Object.keys(data)
        .map((key) => {
          if (key != "totalTime") {
            let listItem: ListData = {
              name: key[0].toUpperCase() + key.slice(1),
              totalTime: data.totalTime,
              items: data[key],
            };

            return listItem;
          }
        })
        .filter(Boolean);

      lists = lists.map((list) => {
        list.items = list.items.sort((a, b) => a.time - b.time);
        return list;
      });

      csvExporter = new ExportToCsv(options);
    } else {
      totalTime = 0;
      lists = [];
    }
  };

  function exportToCsv() {
    csvExporter.generateCsv(convertToCSVJSON(lists));
  }
  const getProjects = async () => {
    const response = await fetch(`${apiUrl}/projects?userId` + $me.id, {
      credentials: "include",
    });

    const data = await response.json();

    console.log(data);

    if (Array.isArray(data)) {
      projects = data;
    }
  };

  onMount(async () => {
    await getProjects();

    if (projects.length) {
      projectId = projects[0].id;
    }

    await getStatistics();
  });
</script>

<main>
  <section>
    <div>
      <h3>Total time</h3>
      <h1>{totalTime}min</h1>
    </div>
    <CustomSelect
      selected={projectId}
      onChange={(selected) => {
        projectId = selected;
        getStatistics();
      }}
      values={projects.map((project) => {
        return { id: project.id, label: project.name };
      })}
    />
  </section>

  <div>
    {#if lists.length == 0}
      <h2>There are no statistics available for this project yet!</h2>
    {:else}
      {#each lists as list}
        <StatList
          items={list.items}
          title={list.name}
          totalTime={list.totalTime}
        />
      {/each}
    {/if}
  </div>
  {#if lists.length > 0}
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="save__btn" on:click={() => exportToCsv()}>
      <p>.csv</p>
      <img class="download" src={Download} alt="Download" />
    </div>
  {/if}
</main>

<style>
  main {
    height: 100vh;
    margin-left: 5vw;
    margin-top: 1rem;
  }

  section {
    margin-bottom: 1rem;
    width: 500px;
    height: auto;
  }

  .download {
    width: 1.5rem;
    height: 1.5rem;
  }

  section div {
    width: auto;
    background-color: #2c2c2c;
    padding: 20px;
    border-radius: 5px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  main > div {
    margin-top: 2rem;
    width: 100%;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  h1 {
    font-size: 5em;
    margin: 0;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .save__btn {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 120px;
    height: 60px;
    background: #000;
    border-radius: 5px;
    margin-top: 2rem;
    cursor: pointer;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.3);
    transition: 0.2s ease-in-out;
  }

  .save__btn:hover {
    background: #00000048;
  }

  .save__btn > p {
    color: #fff;
    font-size: 1.5rem;
  }
</style>
