# SlimePlayer -- The Slim Java Media Player.

## Desription

SlimePlayer (A place holder name for now) is a simple media player that can play .mp3 files (ID3v1 - 2).

My goal was to create a visually slim media player, that didn't take up much memory. I also wanted to have just basic controls as well as scrolling song info, and can be freely dragged about on the screen.

## Running

At the moment the program required two text files to hold info about the playlist. This is not a long term solution, and will be replaced in future with just one file, and the extra utility class will be removed and incorporated into the player.

Currently the project only runs in a IDE, so you will need to fork the project then import it into your IDE of choice then run the utility class *__manualLibrarySearch__* in order to generate the required text files from your choosen music directory.

The program looks for music (.mp3) files in the default windows music directory.
The included utility method called *__manualLibrarySearch__* can be used to manualy
(Recursive) search a directory and generate the needed library files: __Lib_MP3player.txt__ and __SongPath.txt__
both of which should be located at the application directory in __../Data_Files__.

*_The properties file can be modified to point to another location if desired._

## Status

- **Not in active Development**
