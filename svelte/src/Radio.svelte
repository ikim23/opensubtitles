<script>
  export let id
  export let label
</script>

<span class="mr-4">
  <input type="radio" {id} />
  <label class="radio ml-1" for={id}>{label}</label>
</span>
