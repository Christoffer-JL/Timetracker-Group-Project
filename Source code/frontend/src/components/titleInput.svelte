<script lang="ts">
</script>

<div class="title__wrapper">
    <h2>Title</h2>
    <input type="text" placeholder="Enter a title" />
</div>

<style>
    .title__wrapper {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        width: 50%;
        margin-right: 1rem;
        -webkit-touch-callout: none; /* iOS Safari */
        -webkit-user-select: none; /* Safari */
        -khtml-user-select: none; /* Konqueror HTML */
        -moz-user-select: none; /* Old versions of Firefox */
        -ms-user-select: none; /* Internet Explorer/Edge */
        user-select: none; /* Non-prefixed version, currently*/
    }

    .title__wrapper > h2 {
        font-weight: lighter;
        margin-bottom: 5px;
    }

    .title__wrapper > input {
        border: none;
        background-color: #000000;
        height: 80px;
        color: white;
        font-size: 16px;
        width: 100%;
        border-radius: 5px;
        font-size: 25px;
        padding-left: 15px;
        box-sizing: border-box;
    }

    .title__wrapper > input:focus {
        outline: none;
    }
</style>
