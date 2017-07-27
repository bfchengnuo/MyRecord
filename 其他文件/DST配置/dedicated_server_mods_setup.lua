--There are two functions that will install mods, ServerModSetup and ServerModCollectionSetup. Put the calls to the functions in this file and they will be executed on boot.

--ServerModSetup takes a string of a specific mod's Workshop id. It will download and install the mod to your mod directory on boot.
	--The Workshop id can be found at the end of the url to the mod's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=350811795
	--ServerModSetup("350811795")
	ServerModSetup("362175979") --Wormhole Marks [DST] 虫洞标识
	ServerModSetup("367546858") --EZInput  中文
	ServerModSetup("375859599") --Health Info 显示生命值
	ServerModSetup("378160973") --Global Positions 显示队友
	ServerModSetup("399799824") --人物，九尾狐
	ServerModSetup("447092740") --More Actions 更多动作
	ServerModSetup("458587300") --Fast Travel 指示牌传送
	ServerModSetup("466732225") --暖石无消耗
	ServerModSetup("543945797") --显示伤害数值
	ServerModSetup("572538624") --EZInput  中文增强
	ServerModSetup("631648169") --怪物击杀公告
	ServerModSetup("666155465") --Show Me 显示详细属性
	ServerModSetup("668708075") --地图标识
	ServerModSetup("700236083") --Customizable Quick Actions 快速收取
	ServerModSetup("462372013") --冰箱无消耗
	ServerModSetup("462434129") --Restart(重生)
	ServerModSetup("375850593") --Extra Equip Slots 装备扩展
	ServerModSetup("770901818") --Hound Attack  预测狗
--	ServerModSetup("356398534") --DST PickyPickyPicky 快速收集，备用
	

--ServerModCollectionSetup takes a string of a specific mod's Workshop id. It will download all the mods in the collection and install them to the mod directory on boot.
	--The Workshop id can be found at the end of the url to the collection's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=379114180
	--ServerModCollectionSetup("379114180")
