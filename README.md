# Parrot 🔊🦜

Parrot is a library that augments the audio support in libGDX. It provides flexible ways to manage sound effects and music tracks.

Warning! This library is still in early development!

Parrot was originally made for the Grashers game and transformed into an open source project due to way too many developers having to implement something very similar in their own games.



## This library isn't functional yet.

But I'm working on it! :)

In case you want to run the example app to see how the library is shaping up:
1. Clone this repository.
2. Open terminal and go to the project's root folder.
3. Run `gradlew example:run` _(If that doesn't work, try `./gradlew` instead)_



## Roadmap

#### In Progress
- [ ] Create a very clean and straightforward API in the Parrot class.
- [ ] Review the entire documentation to make sure everything is as clear as possible.
- [ ] Implement pitch multipliers based on SoundCategories.
- [ ] Write a proper README. (Including lwjgl3 and Boom limitations.)
- [ ] Create a simple wiki with instructions.
- [ ] Release v1.0.0 through Jitpack.

#### Done
- [x] Port Sound system.
- [x] Port Music system.
- [x] Implement Boom.
- [x] ~~Most of the library uses volume, but should it use decibels everywhere instead?~~ No, volume is easier to understand.
- [x] ~~Put a flag in the settings to control whether or not the maximum volume played by the app should be 1. (True by default)~~ Not applicable.
- [x] Get rid of Camera references in SoundPlayer, use raw coordinates instead.
- [x] Implement volume multipliers per music channel.
- [x] Create an example module with a fully functional implementation, which will also be used as a test application.
    - [x] Music player with play and stop buttons, plus a label of the music track and its author.
    - [x] Ambient music tracks (Rain, restaurant, underwater).
    - [x] UI sounds. (non spatial).
    - [x] Basic Boom implementation.
    - [x] Flamethrower sound to demonstrate continuous playback mode (spatial).
    - [x] Warning Beep sound to demonstrate eternal playback mode (non spatial).
    - [x] Footstep sounds (spatial).
    - [x] Allow user to change the listener's position.
