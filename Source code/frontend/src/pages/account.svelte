<script lang="ts">
  import Save from "../assets/save.svg";
  import Profilepic from "../assets/profilepic.svg";
  import { me } from "../stores/Auth";
  import type { User } from "../types/User";
  import { getUser, updateUser } from "../api";
  import { pop } from "svelte-spa-router";

  let user: User = {
    id: "",
    name: "",
    email: "",
    phoneNumber: "",
    password: "",
    isAdmin: false,
    updatedAt: new Date(),
    createdAt: new Date(),
  };

  const fetchUser = async () => {
    const data = await getUser(user.id);
    if (data.id) {
      user = data;
    }
  };

  me.subscribe(async (usr) => {
    user.id = usr.id;
    user.isAdmin = usr.isAdmin;

    await fetchUser();
  });

  const saveUser = async () => {
    const data = await updateUser(user.id, {
      password: user.password,
      name: user.name,
      email: user.email,
      phoneNumber: user.phoneNumber,
      isAdmin: user.isAdmin,
    });

    if (data.id) {
      await fetchUser();
    }
  };
</script>

<div class="main__wrapper">
  <h1>Edit profile</h1>
  <div class="profile__wrapper">
    <div class="profile">
      <img src={Profilepic} class="profile__img" alt="profilepic" />
      <div class="profile">
        <div class="profile__info">
          <label for="name">Name</label>
          <input
            type="text"
            id="name"
            placeholder="Name"
            bind:value={user.name}
          />
        </div>
        <div class="profile__info">
          <label for="email">Email</label>
          <input
            type="email"
            id="email"
            placeholder="Email"
            bind:value={user.email}
          />
        </div>
        <div class="profile__info">
          <label for="profile__info__phone">Phone</label>
          <input
            type="text"
            id="phone"
            placeholder="Phone"
            bind:value={user.phoneNumber}
          />
        </div>
      </div>
    </div>
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="save__btn" on:click={async () => await saveUser()}>
      <p>Save</p>
      <img class="save" src={Save} alt="save" />
    </div>
  </div>
</div>

<style>
  .main__wrapper {
    width: 50vw;
    height: 50vh;
    margin-left: calc(300px + 50px);
  }

  .profile__wrapper {
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently*/
  }

  .profile {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 400px;
  }

  .profile__info {
    display: flex;
    flex-direction: column;
    width: 300px;
  }

  .profile__info > label {
    font-weight: normal;
    line-height: 1rem;
    margin-left: 0.1rem;
    margin-top: 2rem;
  }

  .profile__info > input {
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

  .profile__info > input:focus-within {
    outline: 1px solid #ffffff59;
  }

  .save__btn {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    width: 120px;
    height: 60px;
    background: #000;
    border-radius: 5px;
    margin-top: 2rem;
    cursor: pointer;
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.3);
    transition: 0.2s ease-in-out;
  }

  .save {
    width: 2rem;
  }

  .save__btn:hover {
    background: #00000048;
  }

  .save__btn > p {
    color: #fff;
    font-size: 1.5rem;
  }
</style>
