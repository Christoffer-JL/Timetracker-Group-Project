<script lang="ts">
  export let items = [];
  export let headers;
</script>

<div class="container">
  <table class="List">
    <thead>
      <tr class="header">
        {#each headers as header}
          <th>{header}</th>
        {/each}
      </tr>
    </thead>
    <tbody>
      {#each items as item}
        <tr>
          {#each item as values}
            {#if values.isComponent == true}
              <td class="comp"><svelte:component this={values.value} /></td>
            {:else}
              <td>{values.value}</td>
            {/if}
          {/each}
        </tr>
      {/each}
    </tbody>
  </table>
</div>

<style>
  .container {
    grid-template-rows: (15, 1fr);
    display: inline-block;
    background-color: #868686;
    min-height: 500px;
    max-height: 500px;
    width: 800px;
    border-radius: 8px;
  }
  .List {
    border-collapse: collapse;
    font-size: 0.9em;
    min-width: 800px;
    overflow: hidden;
    border-radius: 8px;
  }



  .List tbody tr td {
    padding: 8px;
    overflow: hidden;
    border-left: 8px;
  }

  .List th,
  td {
    padding: 15px 20px;
    text-align: left;
  }

  .List tbody,
  tr {
    background-color: black;
    border-bottom: 5px solid gray;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.45);
  }

  .List tbody tr td:nth-child(1) {
    border-radius: 8px 0px 0px 8px;
  }
  .List tbody tr td:nth-last-child(1) {
    border-radius: 0px 8px 8px 0px;
  }

  .List tbody {
    background-color: #868686;
  }
</style>
