/**
 * https://trac.opensubtitles.org/projects/opensubtitles/wiki/HashSourceCodes#Javascript
 */
export function hash(file, callback) {
  var HASH_CHUNK_SIZE = 65536, // 64 * 1024
    longs = [],
    temp = file.size

  function read(start, end, callback) {
    var reader = new FileReader()
    reader.onload = function (e) {
      callback.call(reader, process(e.target.result))
    }

    if (end === undefined) {
      reader.readAsBinaryString(file.slice(start))
    } else {
      reader.readAsBinaryString(file.slice(start, end))
    }
  }

  function process(chunk) {
    for (var i = 0; i < chunk.length; i++) {
      longs[(i + 8) % 8] += chunk.charCodeAt(i)
    }
  }

  function binl2hex(a) {
    var b = 255,
      d = '0123456789abcdef',
      e = '',
      c = 7

    a[1] += a[0] >> 8
    a[0] = a[0] & b
    a[2] += a[1] >> 8
    a[1] = a[1] & b
    a[3] += a[2] >> 8
    a[2] = a[2] & b
    a[4] += a[3] >> 8
    a[3] = a[3] & b
    a[5] += a[4] >> 8
    a[4] = a[4] & b
    a[6] += a[5] >> 8
    a[5] = a[5] & b
    a[7] += a[6] >> 8
    a[6] = a[6] & b
    a[7] = a[7] & b
    for (d, e, c; c > -1; c--) {
      e += d.charAt((a[c] >> 4) & 15) + d.charAt(a[c] & 15)
    }
    return e
  }

  for (var i = 0; i < 8; i++) {
    longs[i] = temp & 255
    temp = temp >> 8
  }

  read(0, HASH_CHUNK_SIZE, function () {
    read(file.size - HASH_CHUNK_SIZE, undefined, function () {
      callback.call(null, file, binl2hex(longs))
    })
  })
}
