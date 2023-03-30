<script lang="ts">
  import { onMount } from "svelte";
  import Save from "../assets/save.svg";
  import { me } from "../stores/Auth";
  import close from "../assets/close.svg";
  import { setHours, setMinutes, parse, setDate, getMonth } from "date-fns";
  import { apiUrl } from "../apiConfig";
  export let open: boolean = false;
  export let fromHour;
  export let fromMinute;
  export let toHour;
  export let toMinute;
  export let projectId;
  export let activities = [];
  export let clockIn;
  export let newClockin = false;
  export let onUpdate = async () => {
    console.log("update");
  };

  let toDate: Date = new Date(clockIn?.end);
  let fromDate: Date = new Date(clockIn?.start);

  let activityId = "none";

  let dialog;
  let projects = [];

  const checkIfValid = () => {
    if (Number.isNaN(Number(fromHour))) {
      fromHour = "";
    }
    if (Number.isNaN(Number(fromMinute))) {
      fromMinute = "";
    }
    if (Number.isNaN(Number(toHour))) {
      toHour = "";
    }
    if (Number.isNaN(Number(toMinute))) {
      toMinute = "";
    }
  };

  const getProjects = async () => {
    const response = await fetch(`${apiUrl}/projects?userId=` + $me.id, {
      credentials: "include",
    });
    const data = await response.json();
    projects = data;
  };

  const saveCheckin = async () => {
    let response;

    if (!newClockin) {
      response = await fetch(`${apiUrl}/clockins/` + clockIn.id, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          description: clockIn.description,
          activityId: activityId === "none" ? clockIn.activity.id : activityId,
          start: Number(
            new Date(
              setHours(
                setMinutes(fromDate, Number(fromMinute)),
                Number(fromHour)
              )
            )
          ),
          end: Number(
            new Date(
              setHours(setMinutes(toDate, Number(toMinute)), Number(toHour))
            )
          ),
        }),
      });
    } else {
      response = await fetch(`${apiUrl}/clockins`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({
          start: Number(
            new Date(
              setHours(
                setMinutes(fromDate, Number(fromMinute)),
                Number(fromHour)
              )
            )
          ),
          end: Number(
            new Date(
              setHours(setMinutes(toDate, Number(toMinute)), Number(toHour))
            )
          ),
          description: clockIn.description,
          activityId,
          projectId: projectId,
        }),
      });
    }
    if (response.ok) {
      open = false;
      dialog.close();
      await onUpdate();
    } else
      alert("Something went wrong, please fill in all fields or try again.");
  };

  onMount(async () => {
    await getProjects();
  });
</script>

{#if open}
  <dialog bind:this={dialog} class={open ? "open" : "closed"}>
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <img class="closebtn" src={close} alt="x" on:click={() => (open = false)} />
    <div class="inner__wrap">
      <div class="formDiv">
        <p class="header">Add new log</p>
        <!-- <slot /> -->
        <form action="addLog">
          <p class="fromToText">From:</p>
          <p class="fromToText">To:</p>
          <input
            type="text"
            class="numberInput"
            bind:value={fromHour}
            on:change={() => checkIfValid()}
            style="margin-left: 0;"
            maxlength="2"
          />
          <input
            type="text"
            class="numberInput"
            bind:value={fromMinute}
            on:change={() => checkIfValid()}
            maxlength="2"
          />
          <input
            type="text"
            class="numberInput"
            bind:value={toHour}
            on:change={() => checkIfValid()}
            style="margin-left: 7.5%;"
            maxlength="2"
          />
          <input
            type="text"
            class="numberInput"
            bind:value={toMinute}
            on:change={() => checkIfValid()}
            maxlength="2"
          />
          <p class="fromToText">From:</p>
          <p class="fromToText">To:</p>
          <input
            type="date"
            style="margin-right: 5px;"
            value={fromDate.getFullYear() +
              "-" +
              ("0" + (fromDate.getMonth() + 1)).slice(-2) +
              "-" +
              ("0" + fromDate.getDate()).slice(-2)}
            on:change={(e) =>
              (fromDate = new Date(Date.parse(e.currentTarget.value)))}
          />
          <input
            type="date"
            value={toDate.getFullYear() +
              "-" +
              ("0" + (toDate.getMonth() + 1)).slice(-2) +
              "-" +
              ("0" + toDate.getDate()).slice(-2)}
            on:change={(e) =>
              (toDate = new Date(Date.parse(e.currentTarget.value)))}
          />
          <textarea
            class="logDesc"
            name="description"
            bind:value={clockIn.description}
            cols="30"
            rows="10"
            placeholder="Add description..."
          />
          <select
            class="chooseActivity"
            on:change={(e) => (activityId = e.currentTarget.value)}
          >
            <option value="none" selected>Select activity</option>
            {#each activities as activity}
              <option value={activity.id}>{activity.name}</option>
            {/each}
          </select>
          <div class="chooseProjectControl">
            <!-- svelte-ignore a11y-click-events-have-key-events -->
            <div
              class="saveLog"
              on:click={async () => {
                dialog.close();
                await saveCheckin();
              }}
            >
              <img class="savebtn" src={Save} alt="save" />
            </div>
          </div>
        </form>
      </div>
    </div>
  </dialog>
{/if}

<style>
  p {
    color: #fff;
  }

  dialog {
    margin-top: 5vh;
    max-width: 40rem;
    width: 20rem;
    min-height: 20rem;
    padding: 2rem;
    padding-top: 0rem;
    height: fit-content;
    border-radius: 5px;
    border: 2px solid black;
    color: white;
    background-color: black;
    position: absolute;
    filter: drop-shadow(0 0 0.75rem black);
  }

  form {
    display: flex;
    justify-content: flex-start;
    width: 100%;
    flex-wrap: wrap;
  }
  .header {
    font-size: 2rem;
    width: 100%;
    margin-bottom: 1rem;
  }
  .numberInput {
    all: unset;
    width: 4rem;
    height: 4rem;
    background-color: #282828;
    text-align: center;
    font-size: 2rem;
    border-radius: 5px;
    margin-left: 0.2rem;
    margin-right: 0.2rem;
    text-shadow: 2px 2px black;
    cursor: text;
  }
  .fromToText {
    width: 50%;
    margin-bottom: 0.2rem;
  }
  .logDesc {
    all: unset;
    margin-top: 1rem;
    background-color: #282828;
    width: 100%;
    height: 5rem;
    padding: 0.7rem 0.7rem;
    box-sizing: border-box;
    cursor: text;
  }
  .chooseActivity {
    color: #fff;
    float: left;
    margin-top: 1rem;
    width: 8rem;
    height: 2.5rem;
    text-align: center;
    border-radius: 5px;
    font-size: 0.8rem;
    box-sizing: border-box;
    background-color: #282828;
    outline: none;
    border: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
  }
  .chooseProjectControl {
    width: 100%;
    display: flex;
    justify-content: center;
    margin-top: 2.4rem;
    cursor: pointer;
  }
  .savebtn {
    width: 2rem;
  }
  .saveLog {
    width: 3rem;
    height: 3rem;
    background-color: #282828;
    border-radius: 10px;
    margin-left: 1rem;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .open {
    display: block;
    z-index: 999;
    position: absolute;
  }

  .closed {
    display: none;
  }

  /*This places close btn on the right place*/
  .closebtn {
    cursor: pointer;
    position: absolute;
    top: -1rem;
    right: -1rem;
    display: none;
  }
</style>
