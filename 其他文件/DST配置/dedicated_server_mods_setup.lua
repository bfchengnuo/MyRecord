--There are two functions that will install mods, ServerModSetup and ServerModCollectionSetup. Put the calls to the functions in this file and they will be executed on boot.

--ServerModSetup takes a string of a specific mod's Workshop id. It will download and install the mod to your mod directory on boot.
	--The Workshop id can be found at the end of the url to the mod's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=350811795
	--ServerModSetup("350811795")
	
	ServerModSetup("347079953") --Display Food Values
	ServerModSetup("623749604") --[DST] Storeroom 大箱子
	ServerModSetup("458587300") --Fast Travel 指示牌传送
	ServerModSetup("378160973") --Global Positions 显示队友
	ServerModSetup("501385076") --Quick Pick 快速收取
	ServerModSetup("666155465") --Show Me 显示详细属性
	ServerModSetup("362175979") --Wormhole Marks [DST] 虫洞标识
	ServerModSetup("609051112") --Action Queue(DST) 队列，shift
	ServerModSetup("631648169") --怪物击杀公告
	ServerModSetup("899583698") --人物 托托莉.赫尔蒙德
	ServerModSetup("462434129") --Restart(重生)
	ServerModSetup("656256171") --Slot Machine 抽奖
	ServerModSetup("347360448") --DST Where's My Beefalo? 显示牛等
	ServerModSetup("399799824") --人物，九尾狐
	ServerModSetup("345692228") --Minimap
	ServerModSetup("466732225") --暖石无消耗
	ServerModSetup("462372013") --冰箱无消耗
	ServerModSetup("728459184") --背包扩展
	ServerModSetup("543945797") --显示伤害数值
	ServerModSetup("376333686") --Always On Status 显示状态数值
	ServerModSetup("375859599") --Health Info 显示生命值
	ServerModSetup("450498063") --Illyasviel Von Einzbern(Illya) 人物魔法少女
	ServerModSetup("375850593") --Extra Equip Slots 装备扩展
	ServerModSetup("367546858") --EZInput  中文
	ServerModSetup("572538624") --EZInput  中文增强
	ServerModSetup("364491382") --Faroz Character [DST] 人物，兔子
	ServerModSetup("351325790") --Geometric Placement 几何

--ServerModCollectionSetup takes a string of a specific mod's Workshop id. It will download all the mods in the collection and install them to the mod directory on boot.
	--The Workshop id can be found at the end of the url to the collection's Workshop page.
	--Example: http://steamcommunity.com/sharedfiles/filedetails/?id=379114180
	--ServerModCollectionSetup("379114180")
