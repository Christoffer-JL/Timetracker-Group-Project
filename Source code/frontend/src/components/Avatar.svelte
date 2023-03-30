<script lang="js">
    // Om bilder sparas i databasen, ange logik för att ladda dessa

    export let avatar;
</script>

<div>
    <!-- svelte-ignore a11y-img-redundant-alt -->
    <img src={avatar} alt="Profile picture" />
</div>

<style>
    img {
        object-fit: cover;
        border-radius: 50%;
        height: 6vw;
        width: 6vw;
    }
</style>
