-- This SQL script contains custom data that is loaded for use in a 
-- test/demo/developer database and is not intended to be loaded in production

-- Entity: Salutation
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(47, 15, 'Salutation-Mr'),
(48, 15, 'Salutation-Mrs'),
(228, 15, 'Salutation-Ms');

insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values
(93,1,47,'Mr'),
(95,1,48,'Mrs'),
(438,1,228,'Ms');

-- Entity: PersonnelTitles
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(57, 29, 'PersonnelTitles-Cashier'),
(58, 29, 'PersonnelTitles-CenterManager'),
(59, 29, 'PersonnelTitles-BranchManager'),
(540, 29, 'PersonnelTitles-AreaManager'),
(541, 29, 'PersonnelTitles-DivisionalManager'),
(542, 29, 'PersonnelTitles-RegionalManager'),
(543, 29, 'PersonnelTitles-Coo'),
(544, 29, 'PersonnelTitles-MisTeam'),
(545, 29, 'PersonnelTitles-ItTeam');

insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)values
(113,1,57,'Cashier'),
(114,1,58,'Center Manager'),
(115,1,59,'Branch Manager');

insert into lookup_value_locale values
(878,1,540,'Area Manager'),
(879,1,541,'Divisional Manager'),
(880,1,542,'Regional Manager'),
(881,1,543,'COO'),
(882,1,544,'MIS Team'),
(883,1,545,'IT Team');

-- Entity: MaritalStatus
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(66, 17, 'MaritalStatus-Married'),
(67, 17, 'MaritalStatus-Unmarried'),
(220, 17, 'MaritalStatus-Widowed');

insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values
(131,1,66,'Married'),
(133,1,67,'UnMarried'),
(426,1,220,'Widowed');

-- Entity: Citizenship
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(130, 18, 'Citizenship-Hindu'),
(131, 18, 'Citizenship-Muslim'),
(221, 18, 'Citizenship-Christian');

insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values
(239,1,130,'Hindu'),
(241,1,131,'Muslim'),
(428,1,221,'Christian');

-- Entity: EducationLevel
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(134, 20, 'EducationLevel-OnlyClient'),
(135, 20, 'EducationLevel-OnlyHusband'),
(226, 20, 'EducationLevel-BothLiterate'),
(227, 20, 'EducationLevel-BothIlliterate');

insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)values
(247,1,134,'Only Client'),
(249,1,135,'Only Husband'),
(434,1,226,'Both Literate'),
(436,1,227,'Both Illiterate');

-- Entity: Ethinicity
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(132, 19, 'Ethinicity-Sc'),
(133, 19, 'Ethinicity-Bc'),
(217, 19, 'Ethinicity-St'),
(218, 19, 'Ethinicity-Obc'),
(219, 19, 'Ethinicity-Fc');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)values
(243,1,132,'SC'),
(245,1,133,'BC'),
(420,1,217,'ST'),
(422,1,218,'OBC'),
(424,1,219,'FC');

-- Entity: LoanPurposes
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(230, 82, 'LoanPurposes-0000AnimalHusbandry'),
(231, 82, 'LoanPurposes-0001CowPurchase'),
(232, 82, 'LoanPurposes-0002BuffaloPurchase'),
(233, 82, 'LoanPurposes-0003GoatPurchase'),
(234, 82, 'LoanPurposes-0004OxBuffalo'),
(235, 82, 'LoanPurposes-0005PigRaising'),
(236, 82, 'LoanPurposes-0006ChickenRaising'),
(237, 82, 'LoanPurposes-0007DonkeyRaising'),
(238, 82, 'LoanPurposes-0008AnimalTrading'),
(239, 82, 'LoanPurposes-0009Horse'),
(240, 82, 'LoanPurposes-0010Camel'),
(241, 82, 'LoanPurposes-0011Bear'),
(242, 82, 'LoanPurposes-0012SheepPurchase'),
(243, 82, 'LoanPurposes-0013HybridCow'),
(244, 82, 'LoanPurposes-0014PhotoFrameWork'),
(245, 82, 'LoanPurposes-0021Fishery'),
(246, 82, 'LoanPurposes-0100Trading'),
(247, 82, 'LoanPurposes-0101PaddyBagBusiness'),
(248, 82, 'LoanPurposes-0102VegetableSelling'),
(249, 82, 'LoanPurposes-0103FruitSelling'),
(250, 82, 'LoanPurposes-0104BanglesTrading'),
(251, 82, 'LoanPurposes-0105TeaShop'),
(252, 82, 'LoanPurposes-0106CosmeticsSelling'),
(253, 82, 'LoanPurposes-0107GeneralStores'),
(254, 82, 'LoanPurposes-0108FlourMill'),
(255, 82, 'LoanPurposes-0109HotelTrading'),
(256, 82, 'LoanPurposes-0110ToddyBusiness'),
(257, 82, 'LoanPurposes-0111PanShop'),
(258, 82, 'LoanPurposes-0112PanleafTrading'),
(260, 82, 'LoanPurposes-0113MadicalStors'),
(261, 82, 'LoanPurposes-0114MeatSelling'),
(262, 82, 'LoanPurposes-0115OilSelling'),
(264, 82, 'LoanPurposes-0116ChatShop'),
(265, 82, 'LoanPurposes-0117PaintShop'),
(266, 82, 'LoanPurposes-0118EggShop'),
(267, 82, 'LoanPurposes-0119ShoeMaker'),
(268, 82, 'LoanPurposes-0120PettyShop'),
(269, 82, 'LoanPurposes-0121FlowerBusiness'),
(270, 82, 'LoanPurposes-0122Bakery'),
(271, 82, 'LoanPurposes-0123CoconutBusiness'),
(272, 82, 'LoanPurposes-0124Electricals'),
(273, 82, 'LoanPurposes-0125GroundnutBusiness'),
(274, 82, 'LoanPurposes-0126ScrapBusiness'),
(275, 82, 'LoanPurposes-0127BroomStickBusiness'),
(276, 82, 'LoanPurposes-0128PlasticBusiness'),
(277, 82, 'LoanPurposes-0129PetrolBusiness'),
(278, 82, 'LoanPurposes-0130SteelBusiness'),
(279, 82, 'LoanPurposes-0131BananaLeafBusiness'),
(280, 82, 'LoanPurposes-0132StationaryShop'),
(281, 82, 'LoanPurposes-0200Production'),
(282, 82, 'LoanPurposes-0201BrickMaking'),
(283, 82, 'LoanPurposes-0202MatWeaving'),
(284, 82, 'LoanPurposes-0203ClothSelling'),
(285, 82, 'LoanPurposes-0204SewingMachine'),
(286, 82, 'LoanPurposes-0205WoodSelling'),
(287, 82, 'LoanPurposes-0206BediMaking'),
(288, 82, 'LoanPurposes-0207CarpetWeaving'),
(289, 82, 'LoanPurposes-0208MotorBodyMaking'),
(290, 82, 'LoanPurposes-0209BuildingMaterial'),
(291, 82, 'LoanPurposes-0210ChainPulley'),
(292, 82, 'LoanPurposes-0211ZigZagMachine'),
(293, 82, 'LoanPurposes-0212PapadBusiness'),
(294, 82, 'LoanPurposes-0213TilesBusiness'),
(295, 82, 'LoanPurposes-0214WeldingShop'),
(296, 82, 'LoanPurposes-0215BedBusiness'),
(297, 82, 'LoanPurposes-0216RopeBusiness'),
(298, 82, 'LoanPurposes-0217AgarbattiBusiness'),
(299, 82, 'LoanPurposes-0300Transportation'),
(300, 82, 'LoanPurposes-0301PushCartSagari'),
(301, 82, 'LoanPurposes-0302CycleRickshaw'),
(302, 82, 'LoanPurposes-0303BicycleRepairing'),
(303, 82, 'LoanPurposes-0304AutoRepairing'),
(304, 82, 'LoanPurposes-0305BullockCarts'),
(305, 82, 'LoanPurposes-0306ThresarMachine'),
(306, 82, 'LoanPurposes-0307VideoSet'),
(307, 82, 'LoanPurposes-0308MujackMachine'),
(308, 82, 'LoanPurposes-0309BiskutFery'),
(309, 82, 'LoanPurposes-0310HorseCart'),
(310, 82, 'LoanPurposes-0311AutoPurchase'),
(311, 82, 'LoanPurposes-0400Agriculture'),
(312, 82, 'LoanPurposes-0401Sharecropping'),
(313, 82, 'LoanPurposes-0402TreeLeasing'),
(314, 82, 'LoanPurposes-0403LandReleasing'),
(315, 82, 'LoanPurposes-0404LandLeasing'),
(316, 82, 'LoanPurposes-0405FoodGrainTrading'),
(317, 82, 'LoanPurposes-0406MotorPurchasing'),
(318, 82, 'LoanPurposes-0500Emergency'),
(319, 82, 'LoanPurposes-0501Consumption'),
(320, 82, 'LoanPurposes-0600TraditionalWorks'),
(321, 82, 'LoanPurposes-0601Carpentry'),
(322, 82, 'LoanPurposes-0602StoneCutting'),
(323, 82, 'LoanPurposes-0603Poultry'),
(324, 82, 'LoanPurposes-0604ClothWeaving'),
(325, 82, 'LoanPurposes-0605LeatherSelling'),
(326, 82, 'LoanPurposes-0606BarberShop'),
(327, 82, 'LoanPurposes-0607Blanketweaving'),
(328, 82, 'LoanPurposes-0608WatchShop'),
(329, 82, 'LoanPurposes-0609Blacksmith'),
(330, 82, 'LoanPurposes-0610IronBusiness'),
(331, 82, 'LoanPurposes-0611SoundSystem'),
(332, 82, 'LoanPurposes-0612PotBusiness'),
(333, 82, 'LoanPurposes-0613CookingContract'),
(334, 82, 'LoanPurposes-0614DhobiBusiness'),
(335, 82, 'LoanPurposes-0615StoneBusiness'),
(336, 82, 'LoanPurposes-0616BeautyParlour'),
(337, 82, 'LoanPurposes-0700Marriage'),
(338, 82, 'LoanPurposes-0999CharakhaMachnies'),
(339, 82, 'LoanPurposes-1000Generator'),
(340, 82, 'LoanPurposes-1001BandBaha'),
(341, 82, 'LoanPurposes-1002TentHouse'),
(342, 82, 'LoanPurposes-1003ToiletConstructions'),
(343, 82, 'LoanPurposes-1004HouseConstructions'),
(344, 82, 'LoanPurposes-1005HouseRepairs'),
(345, 82, 'LoanPurposes-1006Education'),
(346, 82, 'LoanPurposes-1007GoldPurchase'),
(347, 82, 'LoanPurposes-1008Hospital'),
(348, 82, 'LoanPurposes-1009Ration'),
(349, 82, 'LoanPurposes-1010Education'),
(350, 82, 'LoanPurposes-1011IgActivity'),
(351, 82, 'LoanPurposes-1012Agriculture'),
(352, 82, 'LoanPurposes-1013AssetsCreations'),
(353, 82, 'LoanPurposes-1014Festivals'),
(354, 82, 'LoanPurposes-1015LoanRepayment'),
(355, 82, 'LoanPurposes-1016CurrentBill'),
(356, 82, 'LoanPurposes-1017Rent'),
(357, 82, 'LoanPurposes-1018Tour'),
(358, 82, 'LoanPurposes-1019FerBusiness'),
(359, 82, 'LoanPurposes-1019FerBusiness2'),
(360, 82, 'LoanPurposes-1020SesionalBusiness');
/*Loan Purposes*/
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)values
(441,1,230,'0000-Animal Husbandry'),
(443,1,231,'0001-Cow Purchase'),
(445,1,232,'0002-Buffalo Purchase'),
(447,1,233,'0003-Goat Purchase'),
(449,1,234,'0004-Ox/Buffalo'),
(451,1,235,'0005-Pig Raising'),
(453,1,236,'0006-Chicken Raising'),
(455,1,237,'0007-Donkey Raising'),
(457,1,238,'0008-Animal Trading'),
(459,1,239,'0009-Horse'),
(461,1,240,'0010-Camel'),
(463,1,241,'0011-Bear'),
(465,1,242,'0012-Sheep Purchase'),
(467,1,243,'0013-Hybrid Cow'),
(469,1,244,'0014-Photo Frame Work'),
(471,1,245,'0021-Fishery'),
(473,1,246,'0100-Trading'),
(475,1,247,'0101-Paddy Bag Business'),
(477,1,248,'0102-Vegetable Selling'),
(479,1,249,'0103-Fruit Selling'),
(481,1,250,'0104-Bangles Trading'),
(483,1,251,'0105-Tea Shop'),
(485,1,252,'0106-Cosmetics Selling'),
(487,1,253,'0107-General Stores'),
(489,1,254,'0108-Flour Mill'),
(491,1,255,'0109-Hotel Trading'),
(493,1,256,'0110-Toddy Business'),
(495,1,257,'0111-Pan Shop'),
(497,1,258,'0112-PanLeaf Trading'),
(499,1,260,'0113-Madical Stors'),
(501,1,261,'0114-Meat Selling'),
(503,1,262,'0115-Oil Selling'),
(505,1,264,'0116-Chat Shop'),
(507,1,265,'0117-Paint Shop'),
(509,1,266,'0118-Egg Shop'),
(511,1,267,'0119-Shoe Maker'),
(513,1,268,'0120-Petty Shop'),
(515,1,269,'0121-Flower Business'),
/**/
(517,1,270,'0122-Bakery'),
(519,1,271,'0123-Coconut Business'),
(521,1,272,'0124-Electricals'),
(523,1,273,'0125-Groundnut Business'),
(525,1,274,'0126-Scrap Business'),
(527,1,275,'0127-Broom Stick Business'),
(529,1,276,'0128-Plastic Business'),
(531,1,277,'0129-Petrol Business'),
(533,1,278,'0130-Steel Business'),
(535,1,279,'0131-Banana leaf Business'),
(537,1,280,'0132-Stationary Shop'),
/***/
(539,1,281,'0200-Production'),
(541,1,282,'0201-Brick Making'),
(543,1,283,'0202-Mat Weaving'),
(545,1,284,'0203-Cloth Selling'),
(547,1,285,'0204-Sewing Machine'),
(549,1,286,'0205-Wood Selling'),
(551,1,287,'0206-Bedi Making'),
(553,1,288,'0207-Carpet Weaving'),
(555,1,289,'0208-Motor Body Making'),
(557,1,290,'0209-Building Material'),
(559,1,291,'0210-Chain Pulley'),
(561,1,292,'0211-Zig-zag Machine'),
(563,1,293,'0212-Papad Business'),
(565,1,294,'0213-Tiles Business'),
(567,1,295,'0214-Welding Shop'),
(569,1,296,'0215-Bed Business'),
(571,1,297,'0216-Rope Business'),
(573,1,298,'0217-Agarbatti Business'),
(575,1,299,'0300-Transportation'),
(577,1,300,'0301-Push Cart/Sagari'),
(579,1,301,'0302-Cycle Rickshaw'),
(581,1,302,'0303-Bicycle Repairing'),
(583,1,303,'0304-Auto Repairing'),
(585,1,304,'0305-Bullock Carts'),
(587,1,305,'0306-Thresar Machine'),
(589,1,306,'0307-Video Set'),
(591,1,307,'0308-Mujack Machine'),
(593,1,308,'0309-Biskut Fery'),
(595,1,309,'0310-Horse Cart'),
(597,1,310,'0311-Auto Purchase'),
(599,1,311,'0400-Agriculture'),
(601,1,312,'0401-Sharecropping'),
(603,1,313,'0402-Tree Leasing'),
(605,1,314,'0403-Land Releasing'),
(607,1,315,'0404-Land Leasing'),
(609,1,316,'0405-Food Grain Trading'),
(611,1,317,'0406-Motor Purchasing'),
/*******************/
(613,1,318,'0500-Emergency'),
(615,1,319,'0501-Consumption'),
(617,1,320,'0600-Traditional Works'),
(619,1,321,'0601-Carpentry'),
(621,1,322,'0602-Stone Cutting'),
(623,1,323,'0603-Poultry'),
(625,1,324,'0604-Cloth Weaving'),
(627,1,325,'0605-Leather Selling'),
(629,1,326,'0606-Barber Shop'),
(631,1,327,'0607-BlanketWeaving'),
(633,1,328,'0608-Watch Shop'),
(635,1,329,'0609-Blacksmith'),
(637,1,330,'0610-Iron Business'),
(639,1,331,'0611-Sound System'),
(641,1,332,'0612-Pot Business'),
(643,1,333,'0613-Cooking Contract'),
(645,1,334,'0614-Dhobi Business'),
(647,1,335,'0615-Stone Business'),
(649,1,336,'0616-Beauty Parlour'),
(651,1,337,'0700-Marriage'),
(653,1,338,'0999-Charakha Machnies'),
(655,1,339,'1000-Generator'),
(657,1,340,'1001-Band Baha'),
(659,1,341,'1002-Tent House'),
(661,1,342,'1003-Toilet Constructions'),
(663,1,343,'1004-House Constructions'),
(665,1,344,'1005-House Repairs'),
(667,1,345,'1006-Education'),
(669,1,346,'1007-Gold Purchase'),
(671,1,347,'1008-Hospital'),
(673,1,348,'1009-Ration'),
(675,1,349,'1010-Education'),
(677,1,350,'1011-IG Activity'),
(679,1,351,'1012-Agriculture'),
(681,1,352,'1013-Assets Creations'),
(683,1,353,'1014-Festivals'),
(685,1,354,'1015-Loan Repayment'),
(687,1,355,'1016-Current Bill'),
(689,1,356,'1017-Rent'),
(691,1,357,'1018-Tour'),
(693,1,358,'1019-Fer Business'),
(695,1,359,'1019-Fer Business2'),
(697,1,360,'1020-Sesional Business');

-- Entity: Handicapped
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(138, 22, 'Handicapped-Yes'),
(139, 22, 'Handicapped-No');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value) values
(255,1,138,'Yes'),
(257,1,139,'No');

-- Entity: Titles
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(39, 9, 'Titles-President'),
(40, 9, 'Titles-VicePresident');
insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)values
(77,1,39,'President'),
(79,1,40,'Vice President');

-- BusinessActivities
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(136, 21, 'BusinessActivities-DailyLabour');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(251, 1, 136, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(137, 21, 'BusinessActivities-Agriculture');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(253, 1, 137, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(144, 21, 'BusinessActivities-AnimalHusbandry');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(265, 1, 144, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(145, 21, 'BusinessActivities-MicroEnterprise');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(266, 1, 145, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(222, 21, 'BusinessActivities-Production');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(430, 1, 222, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(225, 21, 'BusinessActivities-Trading');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(432, 1, 225, null);

-- CollateralTypes
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(109, 43, 'CollateralTypes-Type1');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(199, 1, 109, null);
 insert into lookup_value(lookup_id,entity_id,lookup_name) values(110, 43, 'CollateralTypes-Type2');
 insert into lookup_value_locale(lookup_value_id,locale_id,lookup_id,lookup_value)
 values(200, 1, 110, null);
 
insert into lookup_value(lookup_id,entity_id,lookup_name) values
(65, 34, 'DBUpgrade.OfficeLevels.Unsued'),
(71, 35, 'DBUpgrade.PrdApplicableMaster.Unused'),
(90, 39, 'DBUpgrade.InterestCalcRule.Unused'),
(223, 79, 'DBUpgrade.Address3.Unused'),
(224, 80, 'DBUpgrade.City.Unused'),
(259, 82, 'DBUpgrade.LoanPurposes1.Unused'),
(263, 82, 'DBUpgrade.LoanPurposes2.Unused');
