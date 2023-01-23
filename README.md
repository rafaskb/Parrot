# Parrot 🔊🦜

Parrot is an audio management library for libGDX that eases the process of dealing with music and sound effects in your game.

Parrot was originally made for the [Grashers](http://grashers.com/) game and transformed into an open source project due to many developers having to implement something very similar in their own games.

## Features

- Play your audio through channels, similar to real-life sound mixers.
- Register multiple sound variations under the same sound type (see ParrotSoundType).
- Sound categories, so you can easily control groups of sound types.
- Voice limitting! Control the amount of sounds that are allowed to play simultaneously, by type or category.
- Optional [Boom](https://github.com/rafaskb/Boom) support for basic OpenAL effects. (Boom is only available for the lwjgl3 backend)

## Example App

In case you want to run the example app to see how the library is shaping up:
1. Clone this repository.
2. Open terminal at the project's root folder.
3. Run `gradlew example:run` _(If that doesn't work, try `./gradlew` instead)_

## Install

**Step 1.** Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add Parrot as a dependency to your core module:

```groovy
dependencies {
    compile 'com.github.rafaskb.parrot:core:master-SNAPSHOT'
}
```

_**Note:** Use `master-SNAPSHOT` to always fetch the most recent changes (although Jitpack has issues with Snapshots), or replace it by the release versions or commit hashes, e.g. `com.github.rafaskb.parrot:core:245fdf5`_


## Roadmap

#### In Progress
- [ ] Review the entire documentation to make sure everything is as clear as possible.
- [ ] Create a simple wiki with instructions.
- [ ] Release v1.0.0 through Jitpack.
- [ ] Improve error handling by throwing exceptions.

#### Done
- [x] Add priority to SoundTypes and SoundInstances.
- [x] Write a proper README. (Including lwjgl3 and Boom limitations.)
- [x] Implement pitch multipliers based on SoundCategories.
- [x] Create a very clean and straightforward API in the Parrot class.
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
