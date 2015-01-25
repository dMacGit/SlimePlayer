# SlimePlayer -- The Small Java Media Player.

## Desription

SlimePlayer (A place holder name for now) is a simple media player that can play .mp3 files (ID3v1 - 2).

It maintains a small footprint on the desktop, displays basic controls as well as scrolling song info,
and can be freely dragged about on the screen.

## Running

In order for the player to play music, it must know where to search for it.

Currently the program looks for music (.mp3) files in the default windows music directory.
However, there is an included utility method called *__manualLibrarySearch__* which can be used to manualy
(Recursive) search a directory and generate the needed library files: __Lib_MP3player.txt__ and __SongPath.txt__
both of which should be located at the application directory in __../Data_Files__.
