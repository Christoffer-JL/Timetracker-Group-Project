<script lang="ts">
  import { formatDuration, intervalToDuration, format } from "date-fns";
  import { sv } from "date-fns/locale";
  import { onMount } from "svelte";
  import { push } from "svelte-spa-router";
  import { me, meRoles } from "../stores/Auth";
  import NewLogPopup from "../components/NewLogPopup.svelte";
  import { deleteClockin } from "../api";
  import type { Clockin } from "../types/Clockin";
  import Start from "../assets/start.png";
  import Stop from "../assets/stop.png";
  import Edit from "../assets/edit.png";
  import Info from "../assets/info.png";
  import Add from "../assets/add.png";
  import Remove from "../assets/remove.svg";
  import { apiUrl } from "../apiConfig";

  let open = false;
  let isClockingIn = false;
  let start: Date | undefined;
  let end: Date | undefined;
  let currentDate = new Date(Date.now());
  let clockinProjectId: string;
  let fromHour = "0";
  let fromMinute = "0";
  let toHour = "0";
  let toMinute = "0";
  let projectId;
  let activities = [];
  let clockIn: Clockin;
  let newClockin = false;
  let clockins = [];

  let projectInfo: any[] = [];

  const getClockins = async () => {
    const response = await fetch(`${apiUrl}/clockins?userId=` + $me.id, {
      credentials: "include",
    });

    clockins = await response.json();
  };

  const deleteAndRefresh = async (id: string) => {
    try {
      await deleteClockin(id);
    } catch (e) {}
    await getClockins();
  };

  const onUpdate = async () => {
    await getClockins();
    newClockin = false;
    clockIn = undefined;
    end = undefined;
    start = undefined;
    open = false;
  };

  const setFromAndToValues = (start: Date, end: Date) => {
    fromHour = new Date(start).getHours().toString();
    if (fromHour.length === 1) fromHour = "0" + fromHour;

    fromMinute = new Date(start).getMinutes().toString();
    if (fromMinute.length === 1) fromMinute = "0" + fromMinute;

    toHour = new Date(end).getHours().toString();
    if (toHour.length === 1) toHour = "0" + toHour;

    toMinute = new Date(end).getMinutes().toString();
    if (toMinute.length === 1) toMinute = "0" + toMinute;
  };

  const startClockin = (id: string) => {
    start = new Date(Date.now());

    isClockingIn = true;
    clockinProjectId = id;

    localStorage.setItem("clockinStart", Number(start).toString());
    localStorage.setItem("clockinProjectId", clockinProjectId);
  };

  const stopClockin = async () => {
    end = new Date(Date.now());
    projectId = clockinProjectId;
    await getActivities();

    isClockingIn = false;
    newClockin = true;
    clockIn = {
      start,
      end,
      description: "",
      id: "",
      clockinSigned: null,
      updatedAt: new Date(Date.now()),
      createdAt: new Date(Date.now()),
    };
    open = true;

    setFromAndToValues(start, end);

    localStorage.removeItem("clockinStart");
    localStorage.removeItem("clockinProjectId");
  };

  const checkIfClockingIn = () => {
    if (localStorage.getItem("clockinStart")) {
      console.log(localStorage.getItem("clockinStart"));
      start = new Date(Number(localStorage.getItem("clockinStart")));
      clockinProjectId = localStorage.getItem("clockinProjectId");
      isClockingIn = true;
    }
  };

  const getActivities = async () => {
    const response = await fetch(
      `${apiUrl}/activities?projectId=` + projectId,
      {
        credentials: "include",
      }
    );
    const data = await response.json();
    activities = data;
  };

  const getProjectInfo = async () => {
    const response = await fetch(`${apiUrl}/projects?userId=` + $me.id, {
      credentials: "include",
    });
    const data = await response.json();
    projectInfo = data;
    //sort projects by name
    try {
      if (projectInfo.length > 1)
        projectInfo.sort((a, b) => (a.name > b.name ? 1 : -1));
    } catch (e) {
      console.log("No projects found");
    }
  };

  onMount(async () => {
    await getProjectInfo();
    await getClockins();
    checkIfClockingIn();
    setInterval(() => (currentDate = new Date(Date.now())), 100);
  });
</script>

<NewLogPopup
  {open}
  {fromHour}
  {toHour}
  {fromMinute}
  {toMinute}
  {projectId}
  {activities}
  {clockIn}
  {onUpdate}
  {newClockin}
/>
<div class="section__wrapper">
  <div class="project__section">
    <h2 class="section__title">Active projects</h2>

    <div class="active__projects">
      <!--TODO: Add project__item for each project that user is assigned to-->
      {#each projectInfo as project}
        <!-- svelte-ignore a11y-click-events-have-key-events -->
        <div class="project__item">
          <div class="project__info">
            <div class="project__name">
              <h3>{project.name}</h3>
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              {#if $me.isAdmin || $meRoles.find((role) => project.roles.find((projRole) => projRole.id === role.id) && role.isPrivileged)}
                <img
                  class="info"
                  src={Info}
                  alt="info"
                  on:click={async () => {
                    await push("/projects/statistics?projectId=" + project.id);
                  }}
                />
                <div
                  class="project__edit"
                  on:click={async () => {
                    await push("/projects/" + project.id);
                  }}
                >
                  Edit
                </div>
              {/if}
              <!-- svelte-ignore a11y-click-events-have-key-events -->
              <img
                class="stopStart"
                src={isClockingIn && clockinProjectId === project.id
                  ? Stop
                  : Start}
                alt="Stop/Start"
                on:click={async () => {
                  if (isClockingIn) {
                    await stopClockin();
                  } else {
                    startClockin(project.id);
                  }
                }}
              />
            </div>

            <div class="activity">
              {#if isClockingIn && clockinProjectId === project.id}
                <p>
                  {formatDuration(
                    intervalToDuration({ start, end: currentDate }),
                    {
                      locale: sv,
                    }
                  )}
                </p>
              {/if}
            </div>
          </div>
        </div>
      {/each}
      <!-- svelte-ignore a11y-click-events-have-key-events -->
      {#if $me.isAdmin}
        <div
          class="project__item create__project"
          on:click={async () => await push("/admin/projects/create")}
        >
          <img class="add" src={Add} alt="add" />
        </div>
      {/if}
    </div>
  </div>
  <div class="Timelogs">
    <h2 class="section__title">Timelogs</h2>
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
          <p class="timelog__name">{clockin?.activity?.project?.name}</p>
          <div class="timelog__description__wrapper">
            <p class="timelog__description">
              {clockin.description}
            </p>
          </div>
          <p class="timelog__name">
            {clockin?.clockinSigner ? "Signed" : "Unsigned"}
          </p>
          <!-- svelte-ignore a11y-click-events-have-key-events -->
          <img
            class="timelog__edit"
            src={Edit}
            alt="edit"
            on:click={async () => {
              if (open) {
                open = false;
              } else {
                open = true;
                clockIn = clockin;

                setFromAndToValues(clockin.start, clockin.end);

                projectId = clockin.activity.project.id;

                console.log(clockIn);
                await getActivities();
              }
            }}
          />

          <!-- svelte-ignore a11y-click-events-have-key-events -->
          <img
            class="timelog__delete"
            src={Remove}
            alt="delete"
            on:click={async () => {
              let ans = confirm(
                "Are you sure you want to delete this clockin?"
              );
              if (!ans) return;
              deleteAndRefresh(clockin.id);
            }}
          />
        </div>
      {/each}
    </div>
  </div>
</div>

<style>
  .create__project:hover {
    cursor: pointer;
    background-color: #000000c9;
  }

  .section__wrapper {
    height: auto;
    width: auto;
    position: absolute;
    left: calc(5vw + 300px);
    top: 0;
  }

  .active__projects {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
  }

  .section__title {
    font-family: Inter;
    font-size: 24px;
    font-weight: 500;
    line-height: 29px;
    letter-spacing: 0em;
    text-align: left;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .project__edit {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    color: #868686;
    margin-left: auto;
    margin-right: auto;
    text-align: center;
    transition: all 0.2s ease-in-out;
    text-decoration: underline #ffffff00;
  }

  .project__edit:hover {
    cursor: pointer;
    text-decoration: underline #868686;
  }

  .project__item {
    width: 320px;
    height: 160px;
    background-color: #000000;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 20px;
    margin-right: 20px;
    box-shadow: 0px 0px 8px rgba(0, 0, 0, 0.2);
    border-radius: 5px;
    transition: all 0.2s ease-in-out;
  }

  .project__info {
    width: 100%;
    height: 140px;
    display: flex;
    flex-direction: column;
    margin-top: 10px;
    margin-left: 10px;
    margin-right: 10px;
    margin-bottom: 10px;
  }

  .add {
    width: 3rem;
  }

  .project__name {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }
  .project__name > h3 {
    display: inline-block;
    font-family: "Inter";
    font-style: normal;
    font-weight: 200;
    font-size: 24px;
    line-height: 29px;
    margin: 0;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .info {
    width: 1.5rem;
    margin-left: 10px;
    cursor: pointer;
  }

  .stopStart {
    width: 2rem;
    margin-left: auto;
    cursor: pointer;
    align-self: start;
  }

  .activity {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-top: auto;
    margin-bottom: 0;
  }

  .activity > p {
    color: rgb(175, 175, 175);
    margin: 0;
    font-weight: 200;
    font-size: 16px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .Timelogs {
    margin-top: 100px;
  }

  .timelogs__item {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 0fr 0fr;
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
    justify-content: left;
    align-items: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: wrap;
  }

  .timelog__description {
    font-size: 14px;
    margin: 0;
  }

  .timelog__edit {
    width: 1.5rem;
    cursor: pointer;
    margin-left: auto;
    filter: grayscale(100%) brightness(50%) sepia(300%) hue-rotate(50deg)
      saturate(900%);
  }

  .timelog__delete {
    width: 1.5rem;
    cursor: pointer;
    margin-left: auto;
    fill: red !important;
  }
</style>
