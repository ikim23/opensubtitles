<script>
  import { createEventDispatcher } from 'svelte'

  export let disabled
  export let checked
  export let value
  export let label

  const dispatch = createEventDispatcher()

  function onChange(evt) {
    dispatch('change', {
      checked: evt.target.checked,
      value,
    })
  }
</script>

<span class="mr-4">
  <label class="checkbox">
    <input type="checkbox" {disabled} {checked} on:change={onChange} />
    {label}
  </label>
</span>
