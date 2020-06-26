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

let bibleVerses = new Map();
bibleVerses.set('Romans 5: 3-5', 'Not only so, but we also glory in our ' +
		'sufferings, because we know that suffering produces perseverance; ' +
		'perseverance, character; and character, hope. And hope does not put us ' +
		'to shame, because God’s love has been poured out into our hearts ' +
		'through the Holy Spirit, who has been given to us.');
bibleVerses.set('Romans 10:13', 'For, “Everyone who calls on the name of ' +
		'the Lord will be saved.”');
bibleVerses.set('Psalms 94:19', 'When anxiety was great within me, your ' +
		'consolation brought me joy.');
bibleVerses.set('John 3:16', 'For God so loved the world that he gave his ' +
		'one and only Son, that whoever believes in him shall not perish but ' +
		'have eternal life.');
bibleVerses.set('James 1:5', 'If any of you lacks wisdom, you should ask ' +
		'God, who gives generously to all without finding fault, and it will be ' +
		'given to you.');
bibleVerses.set('Joshua 1:9', 'Have I not commanded you? Be strong and ' +
		'courageous. Do not be afraid; do not be discouraged, for the Lord your ' +
		'God will be with you wherever you go.');

/**
 * Select a random key from bibleVerses map and return and array with
 * the key and its value.
 * @return {!ARRAY<string>}
 */
function getRandomBibleVerse() {
	// Pick random index
  const bibleVerseIndex = Math.floor(Math.random() * bibleVerses.size);
	
	// Select index from map
	let index = 0;
	let text = '';
	let verse = '';
	for(let key of bibleVerses.keys()) {
		if(index++ === bibleVerseIndex) {
			text = bibleVerses.get(key);
			verse = key;
		}
	}
	const bibleVerse = [text, verse];
	return bibleVerse;
}

/**
 * Select a random Bible verse and add to the page.
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
