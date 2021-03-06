CombatTag Clock
==============
The easy way to interact with Combat Tag.


###Requirements:
- Minecraft Forge 1.7.2-10.12.0.1034 or above


###Installation:
Drop the jar in the .minecraft\mods\1.7.2\ directory.
 
 
###Operation:
CombatTag Clock will automatically send /ct to the server under the following conditions:
 - When the client receives the 'You have been Combat Tagged on Login' message
 - When a player is hurt within visual range (rate-limited to once every 10 seconds)
 - When CombatTag Clock believes the tag time has run out
 
While the player is tagged, a message will be displayed on screen showing an estimate 
of the tag time remaining. Since the whole system works via chat, it's possible that 
this number will not always be accurate. Treat it as a guide.

When CombatTag Clock receives the 'You are not currently in combat!' message, the display 
will turn green and the 'NOT TAGGED' message will appear. It should be safe to log out. 
If you want to be sure, you can manually send a /ct before logging out.


###Configuration
Configuration options can be found in **.minecraft\config\CombatTagClock.cfg**
You'll have to run the plugin at least once for this file to be generated.

The **'chat message suppression'** section lets you ignore unnecessary chat messages. 
By default they are all set to false(i.e. they will not be suppressed).
Setting **'In combat for x seconds'** and/or **'Not in combat'** to true will ignore 
those messages if they have been generated by CombatTag Clock. If you've triggered 
them manually, they should always appear.
**'Tagged on login'** is slightly different because if you set it to true, 
it will drop the 'You have been Combat Tagged on Login' message every time you log in.

The **'display options'** setting lets you move (and hide) the on-screen display. 
**'X Pos'** and **'Y Pos'** will move the on-screen display horizontally and vertically. 
I'm not really sure what kind of voodoo Minecraft uses because the values definitely aren't pixels... 
If you don't want it in the top-left corner, you're going to have to resort to trial and error 
until I get around to implementing a nicer system.
**'Hide display when not tagged'** is will hide the on-screen display when the player isn't tagged.

The **'server settings'** section lets you choose which servers the mod will be active on. 
If the server you connect to isn't in this list, the mod will hide itself and not respond to 
CombatTag chat messages (even if the server is running Combat Tag).
