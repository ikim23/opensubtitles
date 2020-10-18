export const searchByQuery = ({ userAgent, languages, query }) =>
  search({
    userAgent,
    languages,
    url:
      'https://rest.opensubtitles.org/search/query-' +
      encodeURIComponent(query),
  })

export const searchByHash = ({ userAgent, languages, hash, size }) =>
  search({
    userAgent,
    languages,
    url:
      'https://rest.opensubtitles.org/search/moviebytesize-' +
      size +
      '/moviehash-' +
      hash,
  })

const search = ({ url, userAgent, languages }) =>
  Promise.all(
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
