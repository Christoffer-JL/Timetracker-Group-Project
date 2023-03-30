<script lang="ts">
  import type { MenuItem } from "../types/MenuItem";

  export let values: MenuItem[] = [];

  export let selected: string;

  export let onChange = (selected: string) => {
    alert(selected);
  };
</script>

<select
  bind:value={selected}
  on:change={(e) => onChange(e.currentTarget.value)}
>
  <option value="" disabled selected hidden>Select person</option>
  {#each values as value}
    <option value={value.id}>{value.label}</option>
  {/each}
</select>

<style>
  select {
    width: 100%;
    height: 100%;
    border: 0px solid #212121;
    background-color: #212121;
    color: #fff;
    font-size: 1.2rem;
    font-weight: 500;
    padding: 0.5rem;
    outline: none;
    border-radius: 5px;
    box-sizing: border-box;
    transition: all 0.2s ease-in-out;
    outline: 0px solid #212121;
    -moz-appearance: none;
    -webkit-appearance: none;
    appearance: none;
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
  }

  select:hover {
    outline: 1px solid #414040;
  }
</style>
