<script>
  const languages = ['eng', 'svk', 'cze']
  let userAgent = 'TemporaryUserAgent'
  let query = ''
</script>

<div class="container">
  <div class="section">
    <form action="#">
      <div class="columns level">
        <div class="column is-2">
          <label class="label" for="userAgent">User Agent:</label>
        </div>
        <div class="column is-10">
          <input
            class="input"
            type="text"
            name="userAgent"
            bind:value={userAgent} />
        </div>
      </div>
      <div class="columns">
        <div class="column is-2">
          <label class="label" for="userAgent">Languages:</label>
        </div>
        <div class="column is-10">
          {#each languages as lng}
            <span class="mr-4">
              <input type="radio" id={lng} />
              <label class="radio ml-1" for={lng}>{lng.toUpperCase()}</label>
            </span>
          {/each}
        </div>
      </div>
      <div class="columns level">
        <div class="column is-2">
          <label class="label" for="query">Query:</label>
        </div>
        <div class="column is-8">
          <input class="input" type="text" name="query" bind:value={query} />
        </div>
        <div class="column is-2">
          <button
            class="button is-fullwidth is-primary"
            type="submit"
            disabled={!query.length}>Search</button>
        </div>
      </div>
    </form>
    <div class="section">// table</div>
  </div>
</div>
