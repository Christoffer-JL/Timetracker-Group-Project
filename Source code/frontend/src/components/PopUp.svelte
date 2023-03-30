<script lang="ts">
    import { createEventDispatcher } from "svelte";
    export let showDialog = false;
    export let userName = '';
    export let type = '';


    const dispatch = createEventDispatcher();

    function handleAction() {
        dispatch('action', {
            type: type,
            userName: userName
        });

        showDialog = false;
    }

    function handleCancel() {
    showDialog = false;
  }
</script>

{#if showDialog}
  <div class="modal">
    <div class="modal-content">
      {#if type === 'create'}
        <h3>Are you sure you want to create {userName}?</h3>
      {:else}
        <h3>Are you sure you want to delete {userName}?</h3>
      {/if}
      <div class="button-container">
        <button on:click={handleAction}>{type === 'create' ? 'Create' : 'Delete'}</button>
        <button on:click={handleCancel}>Cancel</button>
      </div>
    </div>
  </div>
{/if}

<style>
      .modal {
    position: fixed;
    z-index: 1;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .modal-content {
    background-color: black;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  }

  .button-container {
    display: flex;
    justify-content: center;
    gap: 10px;
  }
</style>