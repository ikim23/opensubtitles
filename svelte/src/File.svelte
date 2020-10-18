<script>
  export let accept
  export let files
</script>

<div class="file has-name is-fullwidth is-right">
  <label class="file-label">
    <input class="file-input" type="file" name="file" {accept} bind:files />
    <span class="file-cta"> <span class="file-label">Choose file</span> </span>
    <span class="file-name">{files && files.length ? files[0].name : ''}</span>
  </label>
</div>
