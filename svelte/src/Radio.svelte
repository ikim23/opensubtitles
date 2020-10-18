<script>
  export let group
  export let value
  export let label
</script>

<span class="mr-4">
  <label class="radio">
    <input type="radio" bind:group {value} />
    {label}
  </label>
</span>
