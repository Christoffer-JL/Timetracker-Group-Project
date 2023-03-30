<script lang="ts">
  import { clockins } from "../stores/Clockins";
  import { activities } from "../stores/Activities";
  import { formatDuration, intervalToDuration, setWeek } from "date-fns";
  export let showModal = false;
  export let clockID;

  let dialog; // HTMLDialogElement
  let clockinValues, activityValues, selectedTag;
  let newDesc = "";

  clockins.subscribe((value) => {
    clockinValues = value;
  });

  activities.subscribe((value) => {
    activityValues = value;
  });

  function updateValues() {
    const updatedValues = clockinValues.map((element) => {
      if (element.id === clockID) {
        return {
          ...element,
          activity: selectedTag,
          description: newDesc.trim() ? newDesc : element.description,
        };
      } else {
        return element;
      }
    });

    clockins.set(updatedValues);
    showModal = false;
  }
</script>

<!--<ConfirmClockin {open} totalHour={69} totalMinute={42} id={getClockinId()} />-->

<!-- svelte-ignore a11y-click-events-have-key-events -->
{#if showModal}
  {#each clockinValues as logs}
    {#if logs.id == clockID}
      <dialog
        bind:this={dialog}
        on:close={() => (showModal = false)}
        on:click|self={() => dialog.close()}
      >
        <div class="formDiv" on:click|stopPropagation>
          <p class="header">Example Project</p>
          <!-- <slot /> -->
          <form action="addLog">
            <p class="fromToText">Total time:</p>
            <input
              type="text"
              class="totalTime"
              value={formatDuration(
                intervalToDuration({
                  start: new Date(logs.start),
                  end: new Date(logs.end),
                })
              )}
              style="margin-left: 0;"
            />

            <ul>
              <li>
                <div class="desc">
                  <h4>Description:</h4>
                  <br />
                  <p>{logs.description}</p>
                  <input
                    type="text"
                    bind:value={newDesc}
                    placeholder="New Description"
                  />
                </div>
                <div class="tags">
                  <h4>Tags:</h4>

                  <select bind:value={selectedTag}>
                    {#each activityValues as option}
                      <option value={option}>{option.name}</option>
                    {/each}
                  </select>
                  <p class="tagItem">
                    {#if selectedTag}
                      {selectedTag.name}
                    {/if}
                  </p>
                </div>
                <div class="logTimeFiller" />
                <div class="logTime" />
              </li>
            </ul>
            <div class="chooseProjectControl">
              <button class="signReport" on:click={() => updateValues()}
                >Confirm</button
              >
              <button class="discardReport" on:click={() => (showModal = false)}
                >Cancel</button
              >
            </div>
          </form>
        </div>
      </dialog>
    {/if}
  {/each}
{/if}

<style>
  dialog {
    display: flex;
    justify-content: center;
    max-width: 100%;
    width: 30rem;
    padding: 2rem;
    padding-top: 0rem;
    height: fit-content;
    max-height: 60rem;
    border-radius: 5px;
    border: 2px solid black;
    color: white;
    background-color: black;
    box-shadow: 1px 1px 3px 1px rgb(0, 0, 0, 0.4);
    cursor: default;
    display: none;
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin: 0 auto;
  }

  .open {
    display: block;
    z-index: 999999;
  }

  ul {
    all: unset;
    margin-top: 1rem;
    background-color: #171717;
    width: 100%;
    min-height: 8rem;
    max-height: 50vh;
    padding: 0.3rem;
    overflow: scroll;
    height: fit-content;
    box-sizing: border-box;
    cursor: default;
    padding-bottom: 15rem;
  }
  ul li {
    /* padding: 0.5rem 1rem; */
    box-sizing: border-box;
    display: flex;
    flex-wrap: wrap;
    margin: 0;
    background-color: #282828;
    width: 100%;
    height: fit-content;
    margin-bottom: 1.5rem;
  }
  .desc {
    width: 60%;
  }
  .tags {
    width: 40%;
  }
  ul h4 {
    color: white;
    width: 100%;
    margin: 0;
    font-weight: 100;
  }
  ul p {
    padding: 0rem 1rem;
    padding-bottom: 1rem;
    margin: 0rem;
    font-style: italic;
  }
  .tagItem {
    background-color: #260f3d;
    box-sizing: border-box;
    width: fit-content;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 1rem;
    margin: 0.5rem 1rem;
    padding: 0.5rem 1rem;
    border-radius: 10px;
    font-style: normal;
  }
  .logTimeFiller {
    width: 60%;
  }
  .logTime {
    width: 40%;
    margin-top: 1rem;
  }
  .logTime p {
    font-style: normal;
    padding: 0;
    margin-bottom: 1rem;
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
  .totalTime {
    all: unset;
    width: fit-content;
    max-width: 12rem;
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
    width: 100%;
    margin-bottom: 0.2rem;
  }
  .logDesc {
    all: unset;
    margin-top: 1rem;
    background-color: #282828;
    width: 100%;
    height: 5rem;
    padding: 0.7rem 1rem;
    box-sizing: border-box;
    cursor: text;
  }
  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    /* margin: 0; */
  }
  dialog::backdrop {
    background: rgba(0, 0, 0, 0.7);
  }
  dialog[open] {
    animation: zoom 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  }
  @keyframes zoom {
    from {
      transform: scale(0.95);
    }
    to {
      transform: scale(1);
    }
  }
  dialog[open]::backdrop {
    animation: fade 0.2s ease-out;
  }
  @keyframes fade {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
  .chooseProjectControl {
    width: 100%;
    display: flex;
    justify-content: center;
    align-self: flex-end;
    margin-top: 2.4rem;
  }
  .chooseProject {
    all: unset;
    text-align: center;
    width: 10rem;
    height: 3rem;
    font-size: 1.2rem;
    background-color: #282828;
    color: white;
    border-radius: 10px;
    padding-left: 1rem;
    padding-right: 1rem;
  }
  .signReport {
    all: unset;
    text-align: center;
    width: fit-content;
    padding: 0.5rem 2rem;
    height: 3rem;
    font-size: 1.8rem;
    background-color: #282828;
    border-radius: 5px;
    margin-left: 1rem;
  }
  .discardReport {
    all: unset;
    text-align: center;
    width: fit-content;
    padding: 0.5rem 1rem;
    height: 3rem;
    font-size: 1.2rem;
    background-color: none;
    text-decoration: underline;
    margin-left: 1rem;
  }
</style>
