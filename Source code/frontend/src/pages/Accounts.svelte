<script>
  import { onMount } from "svelte";
  import AccountComp from "./../components/AccountComp.svelte";
  import { getAllUsers } from "./../api";
  import { link } from "svelte-spa-router";
  import Plus from "./../assets/add.png";

  let nAccounts;
  let allAccounts = [];
  let setSize = 100;
  let index = 0;
  let accountsLength = 0;

  onMount(async () => {
    allAccounts = await getAllUsers();
    setSize = allAccounts.length;
    nAccounts = allAccounts.length;
    sortAccounts();
  });

  let accounts = new Array(setSize);

  $: {
    if (!query) {
      accountsLength = 0;
      for (
        let ia = index, ib = 0;
        ia < (nAccounts - index < setSize ? nAccounts - index : setSize),
          ib < setSize;
        ia++, ib++
      ) {
        try {
          let account = allAccounts[ia];
          accounts[ib] = [
            AccountComp,
            {
              userId: account.id,
              accountName: account.name,
              isAdmin: account.isAdmin === "true",
            },
          ];
          accountsLength++;
        } catch (e) {}
      }
    }
  }

  let pages = 1;

  $: {
    pages = Math.ceil(nAccounts / setSize);
  }

  let page = 1;

  let prev = () => {
    index = index - setSize >= 0 ? index - setSize : 0;
    page = index == 0 ? 1 : page - 1;
  };

  let next = () => {
    index = index + setSize > nAccounts ? nAccounts : index + setSize;
    page++;
  };

  let adminComp = (a, b) => {
    if (a.isAdmin && b.isAdmin) return 0;
    else if (a.isAdmin) return 1;
    else if (b.isAdmin) return -1;
    else return 0;
  };

  let sortAccounts = () => {
    allAccounts.sort(adminComp);
  };

  let query = "";
  let results = [];
  let searchedAccounts = [];

  $: {
    if (query) {
      results = [];
      results = allAccounts.filter((account) => {
        return account.name.toLowerCase().match(query.toLowerCase());
      });

      searchedAccounts = [];
      for (let i = 0; i < results.length; i++) {
        let account = results[i];
        searchedAccounts.push([
          AccountComp,
          {
            userId: account.id,
            accountName: account.name,
            isAdmin: account.isAdmin === "true",
          },
        ]);
      }
    }
  }
</script>

<div class="top">
  <div class="toolbar">
    <div>
      <input
        class="search__account"
        placeholder="Search account"
        bind:value={query}
      />
    </div>
    <div />
  </div>

  <div class="row">
    {#if !query}
      {#each (setSize < accountsLength ? accounts : accounts.splice(0, accountsLength)) || [] as [component, props]}
        <svelte:component this={component} {...props}>Account</svelte:component>
      {/each}
    {:else}
      {#each searchedAccounts as [component, props]}
        <svelte:component this={component} {...props}>Account</svelte:component>
      {/each}
    {/if}
  </div>
  <p class="account__nbr">
    Accounts found: {query ? results.length : nAccounts}
  </p>
</div>

<a class="account__link" href="/admin/accounts/create" use:link>
  <div class="account__btn">
    <p>Create account</p>
    <img class="plus" src={Plus} alt="plus" />
  </div>
</a>

<style>
  .top {
    width: 75%;
    margin: auto;
  }

  .row {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    text-align: center;
    align-items: center;
    padding: 1em;
    margin-left: 0;
    margin-right: 0;
  }

  .toolbar {
    display: flex;
    justify-content: space-between;
  }

  .search__account {
    width: 100%;
    height: 40px;
    background: #2c2c2c;
    border: 0px;
    margin-top: 0.5rem;
    border-radius: 5px;
    padding: 0 10px;
    outline: none;
    font-size: large;
    filter: drop-shadow(1px 3px 0px rgba(0, 0, 0, 0.3));
  }

  .account__nbr {
    text-align: center;
    font-size: 1.5rem;
    margin-top: 1rem;
  }

  .account__link {
    text-decoration: none;
    position: fixed;
    right: 100px;
    bottom: 50px;
  }

  .account__btn {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 70px;
    height: 70px;
    background: #000000;
    border: 0px;
    margin-top: 0.5rem;
    border-radius: 500px;
    outline: none;
    font-size: large;
    filter: drop-shadow(1px 3px 0px rgba(0, 0, 0, 0.3));
    float: inline-end;
    transition: all 0.2s ease-in-out;
  }

  .account__btn:hover {
    background: rgb(15, 15, 15);
    width: 250px;
  }

  .account__btn p {
    color: #fff;
    text-decoration: none;
    margin-right: 0.5rem;
    font-size: medium;
    font-weight: bold;
    opacity: 0;
    position: absolute;
    left: 40%;
    transform: translateX(-50%);
    text-overflow: none;
    white-space: nowrap;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .account__btn:hover p {
    opacity: 1;
    transition: opacity 0.5s ease-out;
  }

  p {
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .plus {
    width: 30px;
    height: 30px;
    transition: all 0.2s ease-in-out;
  }

  .account__btn:hover .plus {
    transform: translateX(200%);
    margin-left: 2rem;
  }
</style>
