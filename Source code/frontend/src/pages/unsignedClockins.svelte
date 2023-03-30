<script lang="ts">
  import { format } from "date-fns";
  import { onMount } from "svelte";
  import { params, location } from "svelte-spa-router";
  import type { Clockin } from "../types/Clockin";
  import { apiUrl } from "../apiConfig";

  let clockins: Clockin[] = [];
  let isProjectSpecific: boolean = false;
  let locationValue: string = "";

  location.subscribe(async (loc) => {
    locationValue = loc;

    if (locationValue !== "/admin/clockins") {
      isProjectSpecific = true;

      await getUnsignedClockins();
    }
  });

  const getUnsignedClockins = async () => {
    try {
      const urlAddition = isProjectSpecific ? "?projectId=" + $params.id : "";

      const response = await fetch(
        `${apiUrl}/clockins/unsigned` + urlAddition,
        {
          credentials: "include",
        }
      );

      const data = await response.json();

      if (Array.isArray(data)) {
        clockins = data;
      }
    } catch (error) {
      return;
    }
  };

  const signClockin = async (id) => {
    const response = await fetch(`${apiUrl}/clockins/` + id + "/sign", {
      method: "PUT",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data = await response.json();
    if (data) {
      await getUnsignedClockins();
    }
  };

  onMount(async () => {
    await getUnsignedClockins();
  });
</script>

<div class="section__wrapper">
  <div class="Timelogs">
    {#if clockins.length > 0}
      <h2 class="section__title">Unsigned time reports</h2>
      <div class="timelogs__section">
        <!--TODO: Add timelogs__item for each project that user is assigned to-->
        {#each clockins as clockin}
          <div class="timelogs__item">
            <p class="time">
              {format(new Date(clockin.start), "d MMM")}
            </p>
            <p class="time">
              {format(new Date(clockin.start), "HH:mm")} - {format(
                new Date(clockin.end),
                "HH:mm"
              )}
            </p>
            <p class="timelog__name">{clockin.user.name || ""}</p>
            <p class="timelog__name">{clockin.role.projectName || ""}</p>
            <div class="timelog__description__wrapper">
              <p class="timelog__description">
                {clockin.description}
              </p>
            </div>
            <button
              on:click={async () => await signClockin(clockin.id)}
              class="timelog__sign">sign</button
            >
          </div>
        {/each}
      </div>
    {:else}
      <h1 class="timelogs__no__clockins">You have no reports to sign!</h1>
    {/if}
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
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 0fr;
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

  .timelog__sign {
    background-color: transparent;
    border: none;
    color: #00ff00d3;
    font-size: large;
    transition: all 0.2s ease;
    text-decoration: underline #ffffff00;
    outline: transparent;
  }

  .timelog__sign:hover {
    cursor: pointer;
    color: #0cff0c;
    text-decoration: underline #0cff0c;
  }
</style>
