<script lang="ts">
</script>

<div class="wrapper">
    <h2>Description</h2>

    <textarea placeholder="Enter a description..." rows="10" cols="50" />
</div>

<style>
    .wrapper {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        width: auto;
        -webkit-touch-callout: none; /* iOS Safari */
        -webkit-user-select: none; /* Safari */
        -khtml-user-select: none; /* Konqueror HTML */
        -moz-user-select: none; /* Old versions of Firefox */
        -ms-user-select: none; /* Internet Explorer/Edge */
        user-select: none; /* Non-prefixed version, currently*/
    }

    .wrapper > h2 {
        font-weight: lighter;
        margin-bottom: 5px;
    }

    .wrapper > textarea {
        font-family: inherit;
        background-color: #000;
        height: 80px;
        width: 350px;
        border: none;
        color: white;
        font-size: 16px;
        resize: none;
        border-radius: 5px;
        padding-left: 15px;
        box-sizing: border-box;
    }

    ::-webkit-input-placeholder {
        font-style: italic;
    }
    :-moz-placeholder {
        font-style: italic;
    }
    ::-moz-placeholder {
        font-style: italic;
    }
    :-ms-input-placeholder {
        font-style: italic;
    }

    .wrapper > textarea:focus {
        outline: none;
    }
</style>
