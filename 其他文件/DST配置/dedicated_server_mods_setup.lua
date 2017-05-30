--There are two functions that will install mods, ServerModSetup and ServerModCollectionSetup. Put the calls to the functions in this file and they will be executed on boot.

--ServerModSetup takes a string of a specific mod's Workshop id. It will download and install the mod to your mod directory on boot.
	--The Workshop id can be found at the end of the url to the mod's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=350811795
	--ServerModSetup("350811795")
	
	ServerModSetup("347079953")
	ServerModSetup("623749604")
	ServerModSetup("458587300")
	ServerModSetup("378160973")
	ServerModSetup("501385076")
	ServerModSetup("666155465")
	ServerModSetup("362175979")
	ServerModSetup("609051112")
	ServerModSetup("631648169")
	ServerModSetup("899583698")
	ServerModSetup("462434129")
	ServerModSetup("656256171")
	ServerModSetup("347360448")
	ServerModSetup("399799824")
	ServerModSetup("345692228") --Minimap
	ServerModSetup("376333686") --Always On Status
	ServerModSetup("375859599") --Health Info
	ServerModSetup("450498063") --Illyasviel Von Einzbern(Illya)
	ServerModSetup("375850593") --Extra Equip Slots
	ServerModSetup("367546858") --EZInput
	ServerModSetup("364491382") --Faroz Character [DST]
	ServerModSetup("351325790") --Geometric Placement

--ServerModCollectionSetup takes a string of a specific mod's Workshop id. It will download all the mods in the collection and install them to the mod directory on boot.
	--The Workshop id can be found at the end of the url to the collection's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=379114180
	--ServerModCollectionSetup("379114180")
