<script lang="ts">
  import { format, formatDuration, intervalToDuration } from "date-fns";
  import sv from "date-fns/locale/sv";
  import { onMount } from "svelte";
  import { params } from "svelte-spa-router";
  import { apiUrl } from "../apiConfig";
  import List from "../components/List.svelte";
  import type { Clockin } from "../types/Clockin";

  let clockins: Clockin[] = [];

  onMount(async () => {
    const response = await fetch(`${apiUrl}/clockins?userId=` + $params.id, {
      credentials: "include",
    });

    const data = await response.json();

    if (Array.isArray(data)) {
      clockins = data;
    }
  });
</script>

<div class="section__wrapper">
  <div class="Timelogs">
    <h2 class="section__title">Logs</h2>
    <div class="timelogs__section">
      <!--TODO: Add timelogs__item for each project that user is assigned to-->
      {#each clockins as clockin}
        <div class="timelogs__item">
          <p class="time">
            {format(new Date(clockin.start), "HH:mm")} - {format(
              new Date(clockin.end),
              "HH:mm"
            )}
          </p>
          <p class="timelog__name">{clockin.role.projectName}</p>
          <div class="timelog__description__wrapper">
            <p class="timelog__description">
              {clockin.description}
            </p>
          </div>
          <!-- svelte-ignore a11y-click-events-have-key-events -->
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

  .Timelogs {
    margin-top: 100px;
  }

  .timelogs__item {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 0fr;
    align-items: center;
    width: 50vw;
    height: 50px;
    background-color: #000000;
    border-radius: 5px;
    margin-bottom: 5px;
  }

  .timelogs__item > * {
    margin-left: 10px;
    margin-right: 10px;
  }

  .timelog__description__wrapper {
    width: auto;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: wrap;
  }

  .timelog__description {
    font-size: 14px;
    margin: 0;
  }
</style>
