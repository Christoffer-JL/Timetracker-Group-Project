<script lang="ts">
  import { setContext } from "svelte";
  import { push } from "svelte-spa-router";
  import { getUser, login } from "../api";
  import { me, meRoles } from "./../stores/Auth";
  import logo from "../assets/logoslim.svg";
  import { apiUrl } from "../apiConfig";

  let email = "";
  let password = "";
  let wrongCredentials = false;

  const onChange = (
    type: string,
    event: Event & {
      currentTarget: EventTarget & HTMLInputElement;
    }
  ) => {
    wrongCredentials = false;
    if (type === "email") {
      email = event.currentTarget.value;
    } else {
      password = event.currentTarget.value;
    }
  };

  const submit = async (
    event: MouseEvent & {
      currentTarget: EventTarget & HTMLButtonElement;
    }
  ) => {
    event.preventDefault();
    const response = await fetch(`${apiUrl}/auth/login`, {
      method: "Post",
      credentials: "include",
      body: JSON.stringify({
        email,
        password,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    });

    const user = await response.json();

    try {
      if (user.id) {
        me.set(user);

        const response = await fetch(`${apiUrl}/roles?userId=` + user.id, {
          credentials: "include",
        });

        if (response.ok) {
          meRoles.set(await response.json());

          await push("/");
        }
      } else {
        wrongCredentials = true;
      }
    } catch (exception) {
      console.warn(exception);
    }
  };
</script>

<div class="wrapper">
  <div class="login">
    <div class="logotitle">
      <img src={logo} class="logo" alt="Logo" />
      <h1>Login</h1>
    </div>

    <form class="form">
      <div class="userinput">
        <label class="label" for="email">Email</label>
        <input
          type="email"
          id="email"
          placeholder="Email"
          on:change={(event) => onChange("email", event)}
        />
      </div>
      <div class="userinput">
        <label class="label" for="password">Password</label>
        <input
          type="password"
          id="password"
          placeholder="Password"
          on:change={(event) => onChange("password", event)}
        />
        {#if wrongCredentials === true}
          <label for="password" class="wrong__credentials"
            >Wrong username or password</label
          >
        {/if}
      </div>
      <div class="loginbtnarea">
        <button
          class="signinbtn"
          on:click={async (event) => await submit(event)}>Sign in</button
        >
      </div>
    </form>
  </div>
</div>

<style>
  .wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 95vh;
  }
  .login {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 420px;
    height: 450px;
    background: radial-gradient(
      50% 50% at 50% 50%,
      #352836 20%,
      #251f27 60%,
      #1f1c20 100%
    );
    box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
    border-radius: 30px;
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Old versions of Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version*/
  }

  .logotitle {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-top: 0;
  }

  .logotitle h1 {
    font-family: "Inter";
    font-style: normal;
    font-weight: 500;
    font-size: 2em;
    line-height: 39px;
  }

  .form {
    display: flex;
    flex-direction: column;
    width: 65%;
  }

  .userinput {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    margin-top: 1em;
  }

  .wrong__credentials {
    font-style: normal;
    font-weight: 400;
    font-size: 14px;
    line-height: 14px;
    margin-left: 5px;
    color: #b11111;
  }

  .userinput .label {
    font-style: normal;
    font-weight: 400;
    font-size: 14px;
    line-height: 14px;
    margin-left: 5px;
  }

  .userinput input {
    width: 100%;
    height: 3em;
    border: 0.5px solid #ffffff00;
    box-sizing: border-box;
    background: #2e2e2e;
    border-radius: 10px;
    margin-top: 0.5em;
    padding-left: 1em;
    filter: drop-shadow(1px 3px 0px rgba(0, 0, 0, 0.25));
    outline: none;
    transition: 0.2s ease;
    box-sizing: border-box;
    font-size: medium;
    color: #fff;
  }

  .userinput input:focus-within {
    border: 0.5px solid #ffffff3d;
    transform: scale(1.005);
  }

  .signinbtn {
    background-color: #ffffff00;
    border: 1px solid #ffffff;
    box-sizing: border-box;
    border-radius: 50px;
    width: 100%;
    height: 2.5em;
    margin-top: 3em;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: 0.3s ease-in;
    color: #fff;
    font-size: medium;
  }

  .signinbtn:hover {
    background: #ffffff;
    color: #13080c;
  }
</style>
