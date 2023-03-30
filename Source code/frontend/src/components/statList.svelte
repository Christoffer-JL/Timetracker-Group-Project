<script lang="ts">
  import type { ListItem } from "../types/ListItem";

  export let title: string;
  export let items: ListItem[];
  export let totalTime: number;
  let longestTime: number = 0;

  const getLongestTime = () => {
    items.forEach((item) => {
      if (item.time > longestTime) {
        longestTime = item.time;
      }
    });
    return longestTime;
  };

  const getWidth = (time) => {
    console.log("time" + time);
    console.log("Totaltime" + totalTime);

    return `${(time / getLongestTime()) * 100}% `;
  };

  //This is a test
  /* <StatList
    items={[
      { id: "1", name: "Item 1", time: 10 },
      { id: "2", name: "Item 2", time: 20 },
      { id: "2", name: "Item 3", time: 30 },
    ]}
    title="Statistik"
    totalTime={60}
  /> */
</script>

<div class="wrapper">
  <p class="title">{title}</p>
  <div class="list">
    {#each items as item, i}
      <div class="list__item" style="width: {getWidth(item.time)}">
        <p class="item__name">{item.name}</p>
        <p class="item__time">
          {item.time}
        </p>
      </div>
    {/each}
  </div>
</div>

<style>
  .title {
    margin: 0;
    padding: 0.5rem;
    font-size: 1.5rem;
    font-weight: 600;
    color: #fff;
  }

  .wrapper {
    width: 300px;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    background-color: #2c2c2c;
    margin-right: 1.5rem;
    border-radius: 5px;
    align-self: flex-start;
    padding: 10px;
    padding-right: 20px;
  }

  .list {
    display: flex;
    flex-wrap: wrap;
    flex-direction: column;
    align-items: flex-start;
    margin-top: 1rem;
    height: auto;
    width: 100%;
    position: relative;
  }

  .list__item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 1.8rem;
    background-color: #000000;
    background: #000000;
    color: #fff;
    padding: 0.25rem;
    margin-right: 0.5rem;
    margin-bottom: 0.5rem;
    border-radius: 0.25rem;
    font-size: 0.75rem;
    transition: all 0.3s ease-in-out;
    box-sizing: border-box;
    margin-left: 10px;
  }

  .list__item > .item__name {
    font-size: medium;
    font-weight: 600;
    padding: 0;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
  }

  .list__item > .item__time {
    width: auto;
    font-size: small;
  }

  .item__name {
    z-index: 9999999;
    position: absolute;
    text-overflow: clip;
    transition: all 0.3s ease-in-out;
  }

  .item__time {
    position: absolute;
    right: 0;
  }

  .list__item:hover {
    background: #14002b98;
  }

  .list__item p:last-child {
    text-align: right;
  }
</style>
