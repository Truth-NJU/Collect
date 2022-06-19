export function parseToObject(text) {
  const list = text.split('/')
  const passage = []
  const annotation = []
  for (let i = 1; i < list.length - 1; i++) {
    const string = list[i].split('%')
    passage.push(string[0])
    const temp = []
    for (let j = 1; j < string.length - 1; j++) {
      temp.push(string[j])
    }
    annotation.push(temp)
  }
  return {
    'passage': passage,
    'annotation': annotation
  }
}

export function parseToText(object) {
  const passage = object.passage
  const annotation = object.annotation
  let text = '/'
  for (let i = 0; i < passage.length; i++) {
    text = text.concat(passage[i])
    if (annotation[i].length !== 0) {
      text = text.concat('%')
      for (let j = 0; j < annotation[i].length; j++) {
        text = text.concat(annotation[i][j])
        text = text.concat('%')
      }
    }
    text = text.concat('/')
  }
  return text
}

export function parseNormal(text) {
  const object = parseToObject(text)
  let ans = ''
  for (let i = 0; i < object.passage.length; i++) {
    ans = ans.concat(object.passage[i])
    if (i !== object.passage.length - 1) {
      ans = ans.concat('\n')
    }
  }
  return ans
}
