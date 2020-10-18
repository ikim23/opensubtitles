export function search({ userAgent, languages, query }) {
  const url =
    'https://rest.opensubtitles.org/search/query-' + encodeURIComponent(query)
  return Promise.all(
    languages.map((lng) =>
      fetch(`${url}/sublanguageid-${lng}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'X-User-Agent': userAgent,
        },
      }).then((data) => data.json())
    )
  ).then((results) =>
    results
      .flatMap((x) => x)
      .map((x) => ({
        added: x.SubAddDate.substr(0, 10),
        name: x.SubFileName,
        downloadCount: parseInt(x.SubDownloadsCnt),
        downloadUrl: x.SubDownloadLink,
      }))
  )
}
