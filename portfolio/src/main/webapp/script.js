// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random Bible verse to the page.
 */
const BIBLE_VERSES = new Map();
BIBLE_VERSES.set('Romans 5: 3-5', 'Not only so, but we also glory in our ' +
    'sufferings, because we know that suffering produces perseverance; ' +
    'perseverance, character; and character, hope. And hope does not put us ' +
    'to shame, because God’s love has been poured out into our hearts ' +
    'through the Holy Spirit, who has been given to us.');
BIBLE_VERSES.set('Romans 10:13', 'For, “Everyone who calls on the name of ' +
    'the Lord will be saved.”');
BIBLE_VERSES.set('Psalms 94:19', 'When anxiety was great within me, your ' +
    'consolation brought me joy.');
BIBLE_VERSES.set('John 3:16', 'For God so loved the world that he gave his ' +
    'one and only Son, that whoever believes in him shall not perish but ' +
    'have eternal life.');
BIBLE_VERSES.set('James 1:5', 'If any of you lacks wisdom, you should ask ' +
    'God, who gives generously to all without finding fault, and it will be ' +
    'given to you.');
BIBLE_VERSES.set('Joshua 1:9', 'Have I not commanded you? Be strong and ' +
    'courageous. Do not be afraid; do not be discouraged, for the Lord your ' +
    'God will be with you wherever you go.');

/**
 * Selects a random key from BIBLE_VERSES map and return an array with
 * the key and its value.
 * @return {!ARRAY<string>}
 */
function getRandomBibleVerse() {
  // Pick random index.
  const bibleVerseIndex = Math.floor(Math.random() * BIBLE_VERSES.size);
	
  // Select index from map.
  let index = 0;
  let text = '';
  let verse = '';
  for(let key of BIBLE_VERSES.keys()) {
    if(index++ === bibleVerseIndex) {
      text = BIBLE_VERSES.get(key);
      verse = key;
    }
  }
  const bibleVerse = [text, verse];
  return bibleVerse;
}

/**
 * Selects a random Bible verse and add to the page.
 */
function addRandomBibleVerse() {
  // Pick a random Bible verse.
  const bibleVerse = getRandomBibleVerse();

  // Add it to the page.
  const bibleVerseTextContainer = 
      document.getElementById('bible-verse-text-container');
  bibleVerseTextContainer.innerText = bibleVerse[0];
  const bibleVerseContainer = 
      document.getElementById('bible-verse-container');
  bibleVerseContainer.innerText = bibleVerse[1];
}

/**
 * Changes Navbar style depending on scroll position.
 */
window.addEventListener('scroll', function() {
  if (document.body.scrollTop > 30 || 
      document.documentElement.scrollTop > 30) {
    document.getElementById('navbar').style.backgroundPosition = 
        'left bottom';
  }
  else {
    document.getElementById('navbar').style.backgroundPosition = 
        'right bottom';
  }
})

/**
 * Fetches a number of comments (submitted via the form) from the server
 * and adds it to the DOM.
 */
function getComment() {
  let dataUrl = '/data?comment-limit=';
  let limitNumber = 0;
  
  // Make sure the input is an integer greater than or equal to 0.
  // If not, set the comment-limit to 0.
  try {
    limitNumber = document.getElementById("quantity").value;
    if(limitNumber < 0) throw 'Invalid number (cannot be negative): '
        + limitNumber.toString();
  } catch(err) {
    console.log(err);
    limitNumber = 0;
  }
  dataUrl += limitNumber.toString();

  // Fetch the comments from the servlet and append them
  // to the corresponding element.
  fetch(dataUrl).then(response => response.json()).then(comments => {
    const commentContainer = document.getElementById('comment-container');
    commentContainer.innerHTML = '';
    comments.forEach((comment) => {
      commentContainer.appendChild(createCommentElement(comment));
    })
  });
}

/**
 * Creates an <li> element containing the content of a comment.
 * @return {element}
 */
function createCommentElement(comment) {
  // Create element for comment.
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  // Create element for nickname and append it to comment.
  const nicknameElement = document.createElement('span');
  nicknameElement.innerText = comment.nickname + ': ';
  commentElement.appendChild(nicknameElement);

  // Create element for content and append it to comment.
  const contentElement = document.createElement('span');
  contentElement.innerText = comment.content;
  commentElement.appendChild(contentElement);

  // Create element for image if it exists and append it to comment.
  if (comment.imageUrl != null) {
    const imageSourceElement = document.createElement('img');
    imageSourceElement.src = comment.imageUrl;
    imageSourceElement.alt = 'Image included with the comment';
    const imageAnchorElement = document.createElement('a');
    imageAnchorElement.href = comment.imageUrl;
    imageAnchorElement.appendChild(imageSourceElement);
    const imageElement = document.createElement('span');
    imageElement.appendChild(imageAnchorElement);
    commentElement.appendChild(imageElement);
  }
  
  return commentElement;
}

/** Deletes all comments from the server */
function deleteComments() {
  const request = new Request('delete-data', {method: 'POST'});
  fetch(request).then(response => getComment());
}

/** Fetches Login and Blobstore servlets. */
function fetchHome() {
  fetchLogin();
  fetchBlobstoreUrl();
}

/**
 * Fetches the Blobstore URL and appends it to the comments form
 * in the action attribute.
 */
function fetchBlobstoreUrl() {
  fetch('/blobstore-upload-url')
      .then(response => response.text())
      .then(imageUploadUrl => {
        const messageForm = document.getElementById('comments-form');
        messageForm.action = imageUploadUrl;
      });
}

/**
 * Fetches the Login Servlet and displayes a message
 * according to the condition of the user being logged in or out.
 */
function fetchLogin() {
  fetch('/login').then(response => response.json()).then(user => {
    if (user.loggedIn == true) {
      // If logged in user has no nickname, redirect to nickname setup page.
      if(user.nickname == "") {
        window.location.replace(user.authenticationUrl);
      }

      // Create element that welcomes the user and prompts them to log out.
      // "Hello, {nickname}!"
      const welcomeMessage = document.createElement('h1');
      welcomeMessage.innerHTML = 'Hello, ' + user.nickname + '!';

      // "To change nickname, click here."
      const changeNicknameUrl = document.createElement('a');
      changeNicknameUrl.href = '/nickname.html';
      changeNicknameUrl.innerText = 'here';

      const changeNickname = document.createElement('p');
      changeNickname.innerHTML = 'To change nickname, click ';
      changeNickname.appendChild(changeNicknameUrl);
      changeNickname.innerHTML += '.';

      // "To log out, click here."
      const logoutUrl = document.createElement('a');
      logoutUrl.href = user.authenticationUrl;
      logoutUrl.innerText = 'here';

      const logoutPrompt = document.createElement('p');
      logoutPrompt.innerHTML = 'To log out, click ';
      logoutPrompt.appendChild(logoutUrl);
      logoutPrompt.innerHTML += '.';

      const loggedInMessage = document.getElementById('authentication');
      loggedInMessage.innerHTML = '';
      loggedInMessage.appendChild(welcomeMessage);
      loggedInMessage.appendChild(changeNickname);
      loggedInMessage.appendChild(logoutPrompt);

      // Show comments form.
      const commentForm = document.getElementById('comments-form');
      commentForm.classList.remove('hidden');
    } else {
      // Create element that prompts the user to login.
      // "To leave a comment, log in!"
      const loginUrl = document.createElement('a');
      loginUrl.href = user.authenticationUrl;
      loginUrl.innerText = 'log in';

      const loginPrompt = document.createElement('p');
      loginPrompt.innerHTML = 'To leave a comment, ';
      loginPrompt.appendChild(loginUrl);
      loginPrompt.innerHTML += '!';

      const loggedOutMessage = document.getElementById('authentication');
      loggedOutMessage.innerHTML = '';
      loggedOutMessage.appendChild(loginPrompt);

      // Hide comments form.
      const commentForm = document.getElementById('comments-form');
      commentForm.classList.add('hidden');
    }
  });
}
