<script lang="ts">
    import Chipremove from "../assets/remove.svg";
    export let text;
    export let id;

    export let removeChip = (id) => {
        console.log(id);
    };
</script>

<div class="chip">
    <input type="text" value={text} placeholder="activity" />
    <img
        class="remove"
        src={Chipremove}
        alt="x"
        on:click={() => removeChip(id)}
    />
</div>

<style>
    .chip {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: space-evenly;
        background: rgba(134, 134, 134, 0.37);
        box-shadow: 0px 2px 0px #2c2c2c;
        border-radius: 999px;
        margin: 10px;
        height: 2rem;
        padding: 0 10px;
        transition: all 0.2s ease-in-out;
        -webkit-touch-callout: none; /* iOS Safari */
        -webkit-user-select: none; /* Safari */
        -khtml-user-select: none; /* Konqueror HTML */
        -moz-user-select: none; /* Old versions of Firefox */
        -ms-user-select: none; /* Internet Explorer/Edge */
        user-select: none; /* Non-prefixed version, currently*/
    }

    .chip > input {
        font-size: 0.8rem;
        color: white;
        margin: 0;
        padding: 0;
        border: none;
        box-sizing: border-box;
        background: transparent;
        outline: none;
        width: auto;
    }

    .chip:hover {
        background: rgba(134, 134, 134, 0.308);
    }

    .remove {
        width: auto;
        height: 18px;
        margin-left: 10px;
        cursor: pointer;
    }
</style>
