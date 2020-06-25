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
 * Adds a random greeting to the page.
 */
function addRandomBibleVerse() {
  const bibleVersesText =
      ['Not only so, but we also glory in our sufferings, because we know that suffering produces perseverance; perseverance, character; and character, hope. And hope does not put us to shame, because God’s love has been poured out into our hearts through the Holy Spirit, who has been given to us.',
      'For, “Everyone who calls on the name of the Lord will be saved.”',
      'When anxiety was great within me, your consolation brought me joy.',
      'For God so loved the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life.',
      'If any of you lacks wisdom, you should ask God, who gives generously to all without finding fault, and it will be given to you.',
      'Have I not commanded you? Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.'];

  const bibleVerses = 
      ['Romans 5: 3-5',
      'Romans 10:13',
      'Psalms 94:19',
      'John 3:16',
      'James 1:5',
      'Joshua 1:9']

  // Pick a random greeting.
  const bibleVerseIndex = Math.floor(Math.random() * bibleVersesText.length);
  const bibleVerseText = bibleVersesText[bibleVerseIndex];
  const bibleVerse = bibleVerses[bibleVerseIndex];

  // Add it to the page.
  const bibleVerseTextContainer = document.getElementById('bible-verse-text-container');
  bibleVerseTextContainer.innerText = bibleVerseText;
  const bibleVerseContainer = document.getElementById('bible-verse-container');
  bibleVerseContainer.innerText = bibleVerse;
}
