package tftcomps.domain

object data {
  // generate roles and champions from the official data (https://developer.riotgames.com/docs/tft#static-data_current-set) using
  // jq -r '.[] | "val \(.name): Role = Role(\"\(.name)\", Set(\(.sets | map(.min) | join(", "))))"' traits.json
  // jq -r '.[] | "val \(.name | gsub("[^a-z]"; ""; "i")): Champion = Champion(\"\(.name)\", Set(\(.traits | select(length > 0) | map(. | sub("^Set5_"; "")) | join(", "))), \(.cost))"' champions.json

  object set8 {
    object roles {
      val Ace: Role = Role("Ace", Set(1, 4))
      val Admin: Role = Role("A.D.M.I.N.", Set(2, 4, 6))
      val Aegis: Role = Role("Aegis", Set(2, 3, 4, 5))
      val AnimaSquad: Role = Role("Anima Squad", Set(3, 5, 7))
      val Arsenal: Role = Role("Arsenal", Set(1))
      val Brawler: Role = Role("Brawler", Set(2, 4, 6, 8))
      val Civilian: Role = Role("Civilian", Set(1, 2, 3))
      val Corrupted: Role = Role("Corrupted", Set(1))
      val Defender: Role = Role("Defender", Set(2, 4, 6))
      val Duelist: Role = Role("Duelist", Set(2, 4, 6, 8))
      val Forecaster: Role = Role("Forecaster", Set(1))
      val Gadgeteen: Role = Role("Gadgeteen", Set(3, 5))
      val Hacker: Role = Role("Hacker", Set(2, 3, 4))
      val Heart: Role = Role("Heart", Set(2, 4, 6))
      val LaserCorps: Role = Role("LaserCorps", Set(3, 6, 9))
      val Mascot: Role = Role("Mascot", Set(2, 4, 6, 8))
      val MechaPrime: Role = Role("Mecha: PRIME", Set(3, 5))
      val OxForce: Role = Role("Ox Force", Set(2, 4, 6, 8))
      val Prankster: Role = Role("Prankster", Set(2, 3))
      val Recon: Role = Role("Recon", Set(2, 3, 4))
      val Renegade: Role = Role("Renegade", Set(3, 6))
      val Spellslinger: Role = Role("Spellslinger", Set(2, 4, 6, 8))
      val StarGuardian: Role = Role("Star Guardian", Set(3, 5, 7, 9))
      val Supers: Role = Role("Supers", Set(3))
      val Sureshot: Role = Role("Sureshot", Set(2, 4))
      val Threat: Role = Role("Threat", Set(1))
      val Underground: Role = Role("Underground", Set(3, 5))

      val all: Set[Role] = Set(
        Ace,
        Admin,
        Aegis,
        AnimaSquad,
        Arsenal,
        Brawler,
        Civilian,
        Corrupted,
        Defender,
        Duelist,
        Forecaster,
        Gadgeteen,
        Hacker,
        Heart,
        LaserCorps,
        Mascot,
        MechaPrime,
        OxForce,
        Prankster,
        Recon,
        Renegade,
        Spellslinger,
        StarGuardian,
        Supers,
        Sureshot,
        Threat,
        Underground
      )
    }

    object champions {
      import roles._

      val Alistar: Champion = Champion("Alistar", Set(Aegis, Mascot, OxForce), 3)
      val Annie: Champion = Champion("Annie", Set(Gadgeteen, OxForce, Spellslinger), 2)
      val Aphelios: Champion = Champion("Aphelios", Set(Arsenal, OxForce, Sureshot), 5)
      val Ashe: Champion = Champion("Ashe", Set(LaserCorps, Recon), 1)
      val AurelionSol: Champion = Champion("Aurelion Sol", Set(Threat), 4)
      val Belveth: Champion = Champion("Belveth", Set(Threat), 4)
      val Blitzcrank: Champion = Champion("Blitzcrank", Set(Admin, Brawler), 1)
      val Camille: Champion = Champion("Camille", Set(Admin, Renegade), 2)
      val Chogath: Champion = Champion("Chogath", Set(Threat), 3)
      val Draven: Champion = Champion("Draven", Set(Ace, MechaPrime), 2)
      val Ekko: Champion = Champion("Ekko", Set(Aegis, Prankster, StarGuardian), 4)
      val Ezreal: Champion = Champion("Ezreal", Set(Recon, Underground), 2)
      val Fiddlesticks: Champion = Champion("Fiddlesticks", Set(Corrupted, Threat), 5)
      val Fiora: Champion = Champion("Fiora", Set(Duelist, OxForce), 2)
      val Galio: Champion = Champion("Galio", Set(Civilian, Mascot), 1)
      val Gangplank: Champion = Champion("Gangplank", Set(Duelist, Supers), 1)
      val Janna: Champion = Champion("Janna", Set(Civilian, Forecaster, Spellslinger), 5)
      val Jax: Champion = Champion("Jax", Set(Brawler, MechaPrime), 3)
      val Jinx: Champion = Champion("Jinx", Set(AnimaSquad, Prankster), 2)
      val Kaisa: Champion = Champion("Kaisa", Set(Recon, StarGuardian), 3)
      val Kayle: Champion = Champion("Kayle", Set(Duelist, Underground), 1)
      val Leblanc: Champion = Champion("Leblanc", Set(Admin, Hacker, Spellslinger), 3)
      val LeeSin: Champion = Champion("Lee Sin", Set(Brawler, Heart, Supers), 2)
      val Leona: Champion = Champion("Leona", Set(Aegis, MechaPrime, Renegade), 5)
      val Lulu: Champion = Champion("Lulu", Set(Gadgeteen, Heart), 1)
      val Lux: Champion = Champion("Lux", Set(Spellslinger, StarGuardian), 1)
      val Malphite: Champion = Champion("Malphite", Set(Mascot, Supers), 2)
      val MissFortune: Champion = Champion("Miss Fortune", Set(Ace, AnimaSquad), 4)
      val Mordekaiser: Champion = Champion("Mordekaiser", Set(Ace, LaserCorps), 5)
      val Nasus: Champion = Champion("Nasus", Set(AnimaSquad, Mascot), 1)
      val Nilah: Champion = Champion("Nilah", Set(Duelist, StarGuardian), 3)
      val Nunu: Champion = Champion("Nunu", Set(Gadgeteen, Mascot), 5)
      val Poppy: Champion = Champion("Poppy", Set(Defender, Gadgeteen), 1)
      val Rammus: Champion = Champion("Rammus", Set(Threat), 3)
      val Rell: Champion = Champion("Rell", Set(Defender, StarGuardian), 2)
      val Renekton: Champion = Champion("Renekton", Set(Brawler, LaserCorps), 1)
      val Riven: Champion = Champion("Riven", Set(AnimaSquad, Brawler, Defender), 3)
      val Samira: Champion = Champion("Samira", Set(Ace, Sureshot, Underground), 4)
      val Sejuani: Champion = Champion("Sejuani", Set(Brawler, LaserCorps), 4)
      val Senna: Champion = Champion("Senna", Set(LaserCorps, Sureshot), 3)
      val Sett: Champion = Champion("Sett", Set(Defender, MechaPrime), 4)
      val Sivir: Champion = Champion("Sivir", Set(Civilian, Sureshot), 2)
      val Sona: Champion = Champion("Sona", Set(Heart, Spellslinger, Underground), 3)
      val Soraka: Champion = Champion("Soraka", Set(Admin, Heart), 4)
      val Sylas: Champion = Champion("Sylas", Set(AnimaSquad, Renegade), 1)
      val Syndra: Champion = Champion("Syndra", Set(Heart, StarGuardian), 5)
      val Taliyah: Champion = Champion("Taliyah", Set(Spellslinger, StarGuardian), 4)
      val Talon: Champion = Champion("Talon", Set(OxForce, Renegade), 1)
      val Urgot: Champion = Champion("Urgot", Set(Threat), 5)
      val Vayne: Champion = Champion("Vayne", Set(AnimaSquad, Duelist, Recon), 3)
      val Velkoz: Champion = Champion("Velkoz", Set(Threat), 3)
      val Vi: Champion = Champion("Vi", Set(Aegis, Brawler, Underground), 2)
      val Viego: Champion = Champion("Viego", Set(OxForce, Renegade), 4)
      val Wukong: Champion = Champion("Wukong", Set(Defender, MechaPrime), 1)
      val Yasuo: Champion = Champion("Yasuo", Set(Duelist, LaserCorps), 2)
      val Yuumi: Champion = Champion("Yuumi", Set(Heart, Mascot, StarGuardian), 2)
      val Zac: Champion = Champion("Zac", Set(Threat), 4)
      val Zed: Champion = Champion("Zed", Set(Duelist, Hacker, LaserCorps), 4)
      val Zoe: Champion = Champion("Zoe", Set(Gadgeteen, Hacker, Prankster), 3)

      val all: Set[Champion] = Set(
        Alistar,
        Annie,
        Aphelios,
        Ashe,
        AurelionSol,
        Belveth,
        Blitzcrank,
        Camille,
        Chogath,
        Draven,
        Ekko,
        Ezreal,
        Fiddlesticks,
        Fiora,
        Galio,
        Gangplank,
        Janna,
        Jax,
        Jinx,
        Kaisa,
        Kayle,
        Leblanc,
        LeeSin,
        Leona,
        Lulu,
        Lux,
        Malphite,
        MissFortune,
        Mordekaiser,
        Nasus,
        Nilah,
        Nunu,
        Poppy,
        Rammus,
        Rell,
        Renekton,
        Riven,
        Samira,
        Sejuani,
        Senna,
        Sett,
        Sivir,
        Sona,
        Soraka,
        Sylas,
        Syndra,
        Taliyah,
        Talon,
        Urgot,
        Vayne,
        Velkoz,
        Vi,
        Viego,
        Wukong,
        Yasuo,
        Yuumi,
        Zac,
        Zed,
        Zoe
      )
    }

  }

  object set7 {

    object roles {
      val Assassin: Role = Role("Assassin", Set(2, 4, 6))
      val Astral: Role = Role("Astral", Set(3, 6, 9))
      val Bard: Role = Role("Bard", Set(1))
      val Bruiser: Role = Role("Bruiser", Set(2, 4, 6, 8))
      val Cannoneer: Role = Role("Cannoneer", Set(2, 3, 4, 5))
      val Cavalier: Role = Role("Cavalier", Set(2, 3, 4, 5))
      val Dragon: Role = Role("Dragon", Set(1))
      val Dragonmancer: Role = Role("Dragonmancer", Set(3, 6, 9))
      val Evoker: Role = Role("Evoker", Set(2, 4, 6))
      val Guardian: Role = Role("Guardian", Set(2, 4, 6))
      val Guild: Role = Role("Guild", Set(1, 2, 3, 4, 5, 6))
      val Jade: Role = Role("Jade", Set(12, 3, 6, 9))
      val Legend: Role = Role("Legend", Set(3))
      val Mage: Role = Role("Mage", Set(3, 5, 7, 9))
      val Mirage: Role = Role("Mirage", Set(2, 4, 6, 8))
      val Mystic: Role = Role("Mystic", Set(2, 3, 4, 5))
      val Ragewing: Role = Role("Ragewing", Set(3, 6, 9))
      val Revel: Role = Role("Revel", Set(2, 3, 4, 5))
      val Scalescorn: Role = Role("Scalescorn", Set(2, 4, 6))
      val Shapeshifter: Role = Role("Shapeshifter", Set(2, 4, 6))
      val Shimmerscale: Role = Role("Shimmerscale", Set(3, 5, 7, 9))
      val SpellThief: Role = Role("Spell-Thief", Set(1))
      val Starcaller: Role = Role("Starcaller", Set(1))
      val Swiftshot: Role = Role("Swiftshot", Set(2, 4, 6))
      val Tempest: Role = Role("Tempest", Set(2, 4, 6, 8))
      val Trainer: Role = Role("Trainer", Set(2, 3))
      val Warrior: Role = Role("Warrior", Set(2, 4, 6))
      val Whispers: Role = Role("Whispers", Set(2, 4, 6, 8))

      val all: Set[Role] = Set(
        Assassin,
        Astral,
        Bard,
        Bruiser,
        Cannoneer,
        Cavalier,
        Dragon,
        Dragonmancer,
        Evoker,
        Guardian,
        Guild,
        Jade,
        Legend,
        Mage,
        Mirage,
        Mystic,
        Ragewing,
        Revel,
        Scalescorn,
        Shapeshifter,
        Shimmerscale,
        SpellThief,
        Starcaller,
        Swiftshot,
        Tempest,
        Trainer,
        Warrior,
        Whispers
      )
    }

    object champions {

      import roles._

      val Aatrox: Champion = Champion("Aatrox", Set(Shimmerscale, Warrior), 1)
      val Anivia: Champion = Champion("Anivia", Set(Evoker, Jade, Legend), 3)
      val AoShin: Champion = Champion("Ao Shin", Set(Dragon, Tempest), 10)
      val Ashe: Champion = Champion("Ashe", Set(Dragonmancer, Jade, Swiftshot), 2)
      val AurelionSol: Champion = Champion("Aurelion Sol", Set(Astral, Dragon, Evoker), 10)
      val Bard: Champion = Champion("Bard", Set(roles.Bard, Guild, Mystic), 5)
      val Braum: Champion = Champion("Braum", Set(Guardian, Scalescorn), 2)
      val Corki: Champion = Champion("Corki", Set(Cannoneer, Revel), 4)
      val Daeja: Champion = Champion("Daeja", Set(Dragon, Mirage), 8)
      val Diana: Champion = Champion("Diana", Set(Assassin, Scalescorn), 3)
      val Elise: Champion = Champion("Elise", Set(Shapeshifter, Whispers), 3)
      val Ezreal: Champion = Champion("Ezreal", Set(Swiftshot, Tempest), 1)
      val Gnar: Champion = Champion("Gnar", Set(Jade, Shapeshifter), 2)
      val Hecarim: Champion = Champion("Hecarim", Set(Cavalier, Ragewing), 4)
      val Heimerdinger: Champion = Champion("Heimerdinger", Set(Mage, Trainer), 1)
      val Idas: Champion = Champion("Idas", Set(Dragon, Guardian, Shimmerscale), 8)
      val Illaoi: Champion = Champion("Illaoi", Set(Astral, Bruiser), 3)
      val Jinx: Champion = Champion("Jinx", Set(Cannoneer, Revel), 2)
      val Karma: Champion = Champion("Karma", Set(Dragonmancer, Jade), 1)
      val Kayn: Champion = Champion("Kayn", Set(Assassin, Ragewing), 2)
      val LeeSin: Champion = Champion("Lee Sin", Set(Dragonmancer, Tempest), 3)
      val Leona: Champion = Champion("Leona", Set(Guardian, Mirage), 1)
      val Lillia: Champion = Champion("Lillia", Set(Cavalier, Mage, Scalescorn), 2)
      val Lulu: Champion = Champion("Lulu", Set(Evoker, Mystic, Trainer), 3)
      val Nami: Champion = Champion("Nami", Set(Astral, Mage, Mystic), 2)
      val Neeko: Champion = Champion("Neeko", Set(Jade, Shapeshifter), 4)
      val Nidalee: Champion = Champion("Nidalee", Set(Astral, Shapeshifter), 1)
      val Nunu: Champion = Champion("Nunu", Set(Cavalier, Mirage), 3)
      val Olaf: Champion = Champion("Olaf", Set(Bruiser, Scalescorn, Warrior), 3)
      val Ornn: Champion = Champion("Ornn", Set(Bruiser, Legend, Tempest), 4)
      val Pyke: Champion = Champion("Pyke", Set(Assassin, Whispers), 5)
      val Qiyana: Champion = Champion("Qiyana", Set(Assassin, Tempest), 2)
      val Ryze: Champion = Champion("Ryze", Set(Guild, Mage), 3)
      val Sejuani: Champion = Champion("Sejuani", Set(Cavalier, Guild), 1)
      val Senna: Champion = Champion("Senna", Set(Cannoneer, Ragewing), 1)
      val Sett: Champion = Champion("Sett", Set(Dragonmancer, Ragewing), 1)
      val Shen: Champion = Champion("Shen", Set(Bruiser, Ragewing, Warrior), 2)
      val ShiOhYu: Champion = Champion("Shi Oh Yu", Set(Dragon, Jade, Mystic), 8)
      val Shyvana: Champion = Champion("Shyvana", Set(Dragon, Ragewing, Shapeshifter), 10)
      val Skarner: Champion = Champion("Skarner", Set(Astral, Bruiser), 1)
      val Sona: Champion = Champion("Sona", Set(Evoker, Revel), 4)
      val Soraka: Champion = Champion("Soraka", Set(Jade, Starcaller), 5)
      val Swain: Champion = Champion("Swain", Set(Dragonmancer, Ragewing, Shapeshifter), 3)
      val Syfen: Champion = Champion("Syfen", Set(Bruiser, Dragon, Whispers), 8)
      val Sylas: Champion = Champion("Sylas", Set(Bruiser, Mage, Whispers), 3)
      val TahmKench: Champion = Champion("Tahm Kench", Set(Bruiser, Revel), 1)
      val Talon: Champion = Champion("Talon", Set(Assassin, Guild), 4)
      val Taric: Champion = Champion("Taric", Set(Guardian, Jade), 1)
      val Thresh: Champion = Champion("Thresh", Set(Guardian, Whispers), 2)
      val Tristana: Champion = Champion("Tristana", Set(Cannoneer, Trainer), 2)
      val Twitch: Champion = Champion("Twitch", Set(Guild, Swiftshot), 2)
      val Varus: Champion = Champion("Varus", Set(Astral, Swiftshot), 3)
      val Vladimir: Champion = Champion("Vladimir", Set(Astral, Mage), 1)
      val Volibear: Champion = Champion("Volibear", Set(Dragonmancer, Legend, Shimmerscale), 3)
      val Xayah: Champion = Champion("Xayah", Set(Ragewing, Swiftshot), 4)
      val Yasuo: Champion = Champion("Yasuo", Set(Dragonmancer, Mirage, Warrior), 5)
      val Yone: Champion = Champion("Yone", Set(Mirage, Warrior), 2)
      val Zoe: Champion = Champion("Zoe", Set(Mage, Shimmerscale, SpellThief), 5)

      val all: Set[Champion] = Set(
        Aatrox,
        Anivia,
        AoShin,
        Ashe,
        AurelionSol,
        Bard,
        Braum,
        Corki,
        Daeja,
        Diana,
        Elise,
        Ezreal,
        Gnar,
        Hecarim,
        Heimerdinger,
        Idas,
        Illaoi,
        Jinx,
        Karma,
        Kayn,
        LeeSin,
        Leona,
        Lillia,
        Lulu,
        Nami,
        Neeko,
        Nidalee,
        Nunu,
        Olaf,
        Ornn,
        Pyke,
        Qiyana,
        Ryze,
        Sejuani,
        Senna,
        Sett,
        Shen,
        ShiOhYu,
        Shyvana,
        Skarner,
        Sona,
        Soraka,
        Swain,
        Syfen,
        Sylas,
        TahmKench,
        Talon,
        Taric,
        Thresh,
        Tristana,
        Twitch,
        Varus,
        Vladimir,
        Volibear,
        Xayah,
        Yasuo,
        Yone,
        Zoe
      )
    }

  }

  object set5_5 {

    object roles {
      val Abomination: Role = Role("Abomination", Set(3, 4, 5))
      val Assassin: Role = Role("Assassin", Set(2, 4, 6))
      val Brawler: Role = Role("Brawler", Set(2, 4, 6))
      val Caretaker: Role = Role("Caretaker", Set(1))
      val Cavalier: Role = Role("Cavalier", Set(2, 3, 4))
      val Cannoneer: Role = Role("Cannoneer", Set(2, 4, 6))
      val Cruel: Role = Role("Cruel", Set(1))
      val Dawnbringer: Role = Role("Dawnbringer", Set(2, 4, 6, 8))
      val Draconic: Role = Role("Draconic", Set(3, 5))
      val Forgotten: Role = Role("Forgotten", Set(2, 4, 6, 8))
      val Hellion: Role = Role("Hellion", Set(2, 4, 6, 8))
      val Inanimate: Role = Role("Inanimate", Set(1))
      val Invoker: Role = Role("Invoker", Set(2, 4))
      val Ironclad: Role = Role("Ironclad", Set(2, 3, 4))
      val Knight: Role = Role("Knight", Set(2, 4, 6))
      val Legionnaire: Role = Role("Legionnaire", Set(2, 4, 6, 8))
      val Mystic: Role = Role("Mystic", Set(2, 3, 4, 5))
      val Nightbringer: Role = Role("Nightbringer", Set(2, 4, 6, 8))
      val Ranger: Role = Role("Ranger", Set(2, 4, 6))
      val Redeemed: Role = Role("Redeemed", Set(3, 6, 9))
      val Renewer: Role = Role("Renewer", Set(2, 4, 6))
      val Revenant: Role = Role("Revenant", Set(2, 3, 4, 5))
      val Sentinel: Role = Role("Sentinel", Set(3, 6, 9))
      val Skirmisher: Role = Role("Skirmisher", Set(3, 6, 9))
      val Spellweaver: Role = Role("Spellweaver", Set(2, 4, 6))
      val Victorious: Role = Role("Victorious", Set(1))

      val all: Set[Role] = Set(
        Abomination,
        Assassin,
        Brawler,
        Caretaker,
        Cavalier,
        Cannoneer,
        Cruel,
        Dawnbringer,
        Draconic,
        Forgotten,
        Hellion,
        Inanimate,
        Invoker,
        Ironclad,
        Knight,
        Legionnaire,
        Mystic,
        Nightbringer,
        Ranger,
        Redeemed,
        Renewer,
        Revenant,
        Sentinel,
        Skirmisher,
        Spellweaver,
        Victorious
      )
    }

    object champions {

      import roles._

      val Aatrox: Champion = Champion("Aatrox", Set(Redeemed, Legionnaire), 1)
      val Akshan: Champion = Champion("Akshan", Set(Sentinel, Ranger), 5)
      val Aphelios: Champion = Champion("Aphelios", Set(Nightbringer, Ranger), 4)
      val Ashe: Champion = Champion("Ashe", Set(Draconic, Ranger), 3)
      val Brand: Champion = Champion("Brand", Set(Abomination, Spellweaver), 2)
      val Diana: Champion = Champion("Diana", Set(Nightbringer, Assassin), 4)
      val Draven: Champion = Champion("Draven", Set(Forgotten, Legionnaire), 4)
      val Fiddlesticks: Champion = Champion("Fiddlesticks", Set(Abomination, Revenant, Mystic), 4)
      val Galio: Champion = Champion("Galio", Set(Draconic, Sentinel, Knight), 4)
      val Garen: Champion = Champion("Garen", Set(Victorious, Dawnbringer, Knight), 5)
      val Gragas: Champion = Champion("Gragas", Set(Dawnbringer, Brawler), 1)
      val Gwen: Champion = Champion("Gwen", Set(Inanimate, Mystic), 5)
      val Hecarim: Champion = Champion("Hecarim", Set(Forgotten, Cavalier), 2)
      val Heimerdinger: Champion = Champion("Heimerdinger", Set(Draconic, Renewer, Caretaker), 5)
      val Ivern: Champion = Champion("Ivern", Set(Revenant, Invoker, Renewer), 4)
      val Irelia: Champion = Champion("Irelia", Set(Sentinel, Skirmisher, Legionnaire), 2)
      val Jax: Champion = Champion("Jax", Set(Ironclad, Skirmisher), 4)
      val Kalista: Champion = Champion("Kalista", Set(Abomination, Legionnaire), 1)
      val Karma: Champion = Champion("Karma", Set(Dawnbringer, Invoker), 4)
      val Kayle: Champion = Champion("Kayle", Set(Redeemed, Legionnaire), 5)
      val Kennen: Champion = Champion("Kennen", Set(Hellion, Skirmisher), 2)
      val KhaZix: Champion = Champion("Kha'Zix", Set(Dawnbringer, Assassin), 1)
      val Kled: Champion = Champion("Kled", Set(Hellion, Cavalier), 1)
      val LeeSin: Champion = Champion("Lee Sin", Set(Nightbringer, Skirmisher), 3)
      val Leona: Champion = Champion("Leona", Set(Redeemed, Knight), 1)
      val Lucian: Champion = Champion("Lucian", Set(Sentinel, Cannoneer), 4)
      val Lulu: Champion = Champion("Lulu", Set(Hellion, Mystic), 3)
      val Lux: Champion = Champion("Lux", Set(Redeemed, Mystic), 3)
      val MissFortune: Champion = Champion("Miss Fortune", Set(Forgotten, Cannoneer), 3)
      val Nautilus: Champion = Champion("Nautilus", Set(Ironclad, Knight), 2)
      val Nidalee: Champion = Champion("Nidalee", Set(Dawnbringer, Skirmisher), 3)
      val Nocturne: Champion = Champion("Nocturne", Set(Revenant, Assassin), 3)
      val Nunu: Champion = Champion("Nunu", Set(Abomination, Brawler), 3)
      val Olaf: Champion = Champion("Olaf", Set(Sentinel, Skirmisher), 1)
      val Poppy: Champion = Champion("Poppy", Set(Hellion, Knight), 1)
      val Pyke: Champion = Champion("Pyke", Set(Sentinel, Assassin), 2)
      val Rakan: Champion = Champion("Rakan", Set(Sentinel, Renewer), 3)
      val Rell: Champion = Champion("Rell", Set(Redeemed, Ironclad, Cavalier), 4)
      val Riven: Champion = Champion("Riven", Set(Dawnbringer, Legionnaire), 3)
      val Sejuani: Champion = Champion("Sejuani", Set(Brawler, Nightbringer, Cavalier), 2)
      val Senna: Champion = Champion("Senna", Set(Sentinel, Cannoneer), 1)
      val Sett: Champion = Champion("Sett", Set(Draconic, Brawler), 2)
      val Soraka: Champion = Champion("Soraka", Set(Dawnbringer, Renewer), 2)
      val Syndra: Champion = Champion("Syndra", Set(Redeemed, Invoker), 2)
      val Teemo: Champion = Champion("Teemo", Set(Cruel, Hellion, Invoker), 5)
      val Thresh: Champion = Champion("Thresh", Set(Forgotten, Knight), 2)
      val Tristana: Champion = Champion("Tristana", Set(Hellion, Cannoneer), 2)
      val Udyr: Champion = Champion("Udyr", Set(Draconic, Skirmisher), 1)
      val Varus: Champion = Champion("Varus", Set(Redeemed, Ranger), 2)
      val Vayne: Champion = Champion("Vayne", Set(Forgotten, Ranger), 1)
      val VelKoz: Champion = Champion("Vel'Koz", Set(Redeemed, Spellweaver), 4)
      val Viego: Champion = Champion("Viego", Set(Forgotten, Skirmisher, Assassin), 5)
      val Vladimir: Champion = Champion("Vladimir", Set(Nightbringer, Renewer), 1)
      val Volibear: Champion = Champion("Volibear", Set(Revenant, Brawler), 5)
      val Yasuo: Champion = Champion("Yasuo", Set(Nightbringer, Legionnaire), 3)
      val Ziggs: Champion = Champion("Ziggs", Set(Hellion, Spellweaver), 1)
      val Zyra: Champion = Champion("Zyra", Set(Draconic, Spellweaver), 3)

      val all: Set[Champion] = Set(
        Aatrox,
        Akshan,
        Aphelios,
        Ashe,
        Brand,
        Diana,
        Draven,
        Fiddlesticks,
        Galio,
        Garen,
        Gragas,
        Gwen,
        Hecarim,
        Heimerdinger,
        Ivern,
        Irelia,
        Jax,
        Kalista,
        Karma,
        Kayle,
        Kennen,
        KhaZix,
        Kled,
        LeeSin,
        Leona,
        Lucian,
        Lulu,
        Lux,
        MissFortune,
        Nautilus,
        Nidalee,
        Nocturne,
        Nunu,
        Olaf,
        Poppy,
        Pyke,
        Rakan,
        Rell,
        Riven,
        Sejuani,
        Senna,
        Sett,
        Soraka,
        Syndra,
        Teemo,
        Thresh,
        Tristana,
        Udyr,
        Varus,
        Vayne,
        VelKoz,
        Viego,
        Vladimir,
        Volibear,
        Yasuo,
        Ziggs,
        Zyra
      )
    }

  }

  object set3_5 {

    object roles {
      val Astro: Role = Role("Astro", Set(3))
      val Battlecast: Role = Role("Battlecast", Set(2, 4, 6, 8))
      val Blademaster: Role = Role("Blademaster", Set(3, 6, 9))
      val Blaster: Role = Role("Blaster", Set(2, 4))
      val Brawler: Role = Role("Brawler", Set(2, 4))
      val Celestial: Role = Role("Celestial", Set(2, 4, 6))
      val Chrono: Role = Role("Chrono", Set(2, 4, 6, 8))
      val Cybernetic: Role = Role("Cybernetic", Set(3, 6))
      val DarkStar: Role = Role("DarkStar", Set(3, 6, 9))
      val Demolitionist: Role = Role("Demolitionist", Set(2))
      val Infiltrator: Role = Role("Infiltrator", Set(2, 4, 6))
      val ManaReaver: Role = Role("Mana-Reaver", Set(2))
      val MechPilot: Role = Role("Mech-Pilot", Set(3))
      val Mercenary: Role = Role("Mercenary", Set(1))
      val Mystic: Role = Role("Mystic", Set(2, 4))
      val Paragon: Role = Role("Paragon", Set(1))
      val Protector: Role = Role("Protector", Set(2, 4, 6))
      val Rebel: Role = Role("Rebel", Set(3, 6, 9))
      val Sniper: Role = Role("Sniper", Set(2, 4))
      val Sorcerer: Role = Role("Sorcerer", Set(2, 4, 6, 8))
      val SpacePirate: Role = Role("Space Pirate", Set(2, 4))
      val StarGuardian: Role = Role("Star Guardian", Set(3, 6, 9))
      val Starship: Role = Role("Starship", Set(1))
      val Vanguard: Role = Role("Vanguard", Set(2, 4))

      val all: Set[Role] = Set(
        Astro,
        Battlecast,
        Blademaster,
        Blaster,
        Brawler,
        Celestial,
        Chrono,
        Cybernetic,
        DarkStar,
        Demolitionist,
        ManaReaver,
        MechPilot,
        Infiltrator,
        Mercenary,
        Mystic,
        Paragon,
        Protector,
        Rebel,
        Sniper,
        Sorcerer,
        SpacePirate,
        StarGuardian,
        Starship,
        Vanguard
      )
    }

    object champions {

      import roles._

      val Ahri: Champion = Champion("Ahri", Set(StarGuardian, Sorcerer), 2)
      val Annie: Champion = Champion("Annie", Set(MechPilot, Sorcerer), 2)
      val Ashe: Champion = Champion("Ashe", Set(Celestial, Sniper), 3)
      val AurelionSol: Champion = Champion("Aurelion Sol", Set(Rebel, Starship), 5)
      val Bard: Champion = Champion("Bard", Set(Astro, Mystic), 3)
      val Blitzcrank: Champion = Champion("Blitzcrank", Set(Chrono, Brawler), 2)
      val Caitlyn: Champion = Champion("Caitlyn", Set(Chrono, Sniper), 1)
      val Cassiopeia: Champion = Champion("Cassiopeia", Set(Battlecast, Mystic), 3)
      val Darius: Champion = Champion("Darius", Set(SpacePirate, ManaReaver), 2)
      val Ekko: Champion = Champion("Ekko", Set(Cybernetic, Infiltrator), 5)
      val Ezreal: Champion = Champion("Ezreal", Set(Chrono, Blaster), 3)
      val Fiora: Champion = Champion("Fiora", Set(Cybernetic, Blademaster), 1)
      val Fizz: Champion = Champion("Fizz", Set(MechPilot, Infiltrator), 4)
      val Gangplank: Champion = Champion("Gangplank", Set(SpacePirate, Mercenary, Demolitionist), 5)
      val Gnar: Champion = Champion("Gnar", Set(Astro, Brawler), 4)
      val Graves: Champion = Champion("Graves", Set(SpacePirate, Blaster), 1)
      val Illaoi: Champion = Champion("Illaoi", Set(Battlecast, Brawler), 1)
      val Irelia: Champion = Champion("Irelia", Set(Cybernetic, ManaReaver, Blademaster), 4)
      val Janna: Champion = Champion("Janna", Set(StarGuardian, Paragon), 5)
      val JarvanIV: Champion = Champion("Jarvan IV", Set(DarkStar, Protector), 1)
      val Jayce: Champion = Champion("Jayce", Set(SpacePirate, Vanguard), 3)
      val Jhin: Champion = Champion("Jhin", Set(DarkStar, Sniper), 4)
      val Jinx: Champion = Champion("Jinx", Set(Rebel, Blaster), 4)
      val Karma: Champion = Champion("Karma", Set(DarkStar, Mystic), 3)
      val KogMaw: Champion = Champion("Kog'Maw", Set(Battlecast, Blaster), 2)
      val Leona: Champion = Champion("Leona", Set(Cybernetic, Vanguard), 1)
      val Lucian: Champion = Champion("Lucian", Set(Cybernetic, Blaster), 2)
      val Lulu: Champion = Champion("Lulu", Set(Celestial, Mystic), 5)
      val Malphite: Champion = Champion("Malphite", Set(Rebel, Brawler), 1)
      val MasterYi: Champion = Champion("Master Yi", Set(Rebel, Blademaster), 3)
      val Mordekaiser: Champion = Champion("Mordekaiser", Set(DarkStar, Vanguard), 2)
      val Nautilus: Champion = Champion("Nautilus", Set(Astro, Vanguard), 2)
      val Neeko: Champion = Champion("Neeko", Set(StarGuardian, Protector), 3)
      val Nocturne: Champion = Champion("Nocturne", Set(Battlecast, Infiltrator), 1)
      val Poppy: Champion = Champion("Poppy", Set(StarGuardian, Vanguard), 1)
      val Rakan: Champion = Champion("Rakan", Set(Celestial, Protector), 2)
      val Riven: Champion = Champion("Riven", Set(Chrono, Blademaster), 4)
      val Rumble: Champion = Champion("Rumble", Set(MechPilot, Demolitionist), 3)
      val Shaco: Champion = Champion("Shaco", Set(DarkStar, Infiltrator), 3)
      val Shen: Champion = Champion("Shen", Set(Chrono, Blademaster), 2)
      val Soraka: Champion = Champion("Soraka", Set(StarGuardian, Mystic), 4)
      val Syndra: Champion = Champion("Syndra", Set(StarGuardian, Sorcerer), 3)
      val Teemo: Champion = Champion("Teemo", Set(Astro, Sniper), 4)
      val Thresh: Champion = Champion("Thresh", Set(Chrono, ManaReaver), 5)
      val TwistedFate: Champion = Champion("Twisted Fate", Set(Chrono, Sorcerer), 1)
      val Urgot: Champion = Champion("Urgot", Set(Battlecast, Protector), 5)
      val Vayne: Champion = Champion("Vayne", Set(Cybernetic, Sniper), 3)
      val Vi: Champion = Champion("Vi", Set(Cybernetic, Brawler), 3)
      val Viktor: Champion = Champion("Viktor", Set(Battlecast, Sorcerer), 4)
      val WuKong: Champion = Champion("Wukong", Set(Chrono, Vanguard), 4)
      val Xayah: Champion = Champion("Xayah", Set(Celestial, Blademaster), 1)
      val Xerath: Champion = Champion("Xerath", Set(DarkStar, Sorcerer), 5)
      val XinZhao: Champion = Champion("Xin Zhao", Set(Celestial, Protector), 2)
      val Yasuo: Champion = Champion("Yasuo", Set(Rebel, Blademaster), 2)
      val Zed: Champion = Champion("Zed", Set(Rebel, Infiltrator), 2)
      val Ziggs: Champion = Champion("Ziggs", Set(Rebel, Demolitionist), 1)
      val Zoe: Champion = Champion("Zoe", Set(StarGuardian, Sorcerer), 1)

      val all: Set[Champion] = Set(
        Ahri,
        Annie,
        Ashe,
        AurelionSol,
        Bard,
        Blitzcrank,
        Caitlyn,
        Cassiopeia,
        Darius,
        Ekko,
        Ezreal,
        Fiora,
        Fizz,
        Gangplank,
        Gnar,
        Graves,
        Illaoi,
        Irelia,
        Janna,
        JarvanIV,
        Jayce,
        Jhin,
        Jinx,
        Karma,
        KogMaw,
        Leona,
        Lucian,
        Lulu,
        Malphite,
        MasterYi,
        Mordekaiser,
        Nautilus,
        Neeko,
        Nocturne,
        Poppy,
        Rakan,
        Riven,
        Rumble,
        Shaco,
        Shen,
        Soraka,
        Syndra,
        Teemo,
        Thresh,
        TwistedFate,
        Urgot,
        Vayne,
        Vi,
        Viktor,
        WuKong,
        Xayah,
        Xerath,
        XinZhao,
        Yasuo,
        Zed,
        Ziggs,
        Zoe
      )
    }

  }

  object set3_0 {
    object roles {
      val Blademaster: Role = Role("Blademaster", Set(3, 6, 9))
      val Blaster: Role = Role("Blaster", Set(2, 4))
      val Brawler: Role = Role("Brawler", Set(2, 4))
      val Celestial: Role = Role("Celestial", Set(2, 4, 6))
      val Chrono: Role = Role("Chrono", Set(2, 4, 6))
      val Cybernetic: Role = Role("Cybernetic", Set(3, 6))
      val DarkStar: Role = Role("DarkStar", Set(3, 6, 9))
      val Demolitionist: Role = Role("Demolitionist", Set(2))
      val Infiltrator: Role = Role("Infiltrator", Set(2, 4, 6))
      val ManaReaver: Role = Role("ManaReaver", Set(2, 4))
      val MechPilot: Role = Role("MechPilot", Set(3))
      val Mercenary: Role = Role("Mercenary", Set(1))
      val Mystic: Role = Role("Mystic", Set(2, 4))
      val Protector: Role = Role("Protector", Set(2, 4, 6))
      val Rebel: Role = Role("Rebel", Set(3, 6, 9))
      val Sniper: Role = Role("Sniper", Set(2))
      val Sorcerer: Role = Role("Sorcerer", Set(2, 4, 6, 8))
      val SpacePirate: Role = Role("SpacePirate", Set(2, 4))
      val StarGuardian: Role = Role("StarGuardian", Set(3, 6))
      val Starship: Role = Role("Starship", Set(1))
      val Valkyrie: Role = Role("Valkyrie", Set(2))
      val Vanguard: Role = Role("Vanguard", Set(2, 4))
      val Void: Role = Role("Void", Set(3))

      val all: Set[Role] = Set(
        Blademaster,
        Blaster,
        Brawler,
        Celestial,
        Chrono,
        Cybernetic,
        DarkStar,
        Demolitionist,
        Infiltrator,
        ManaReaver,
        MechPilot,
        Mercenary,
        Mystic,
        Protector,
        Rebel,
        Sniper,
        Sorcerer,
        SpacePirate,
        StarGuardian,
        Starship,
        Valkyrie,
        Vanguard,
        Void
      )
    }

    object champions {
      import roles._

      val Ahri: Champion = Champion("Ahri", Set(Sorcerer, StarGuardian), 2)
      val Annie: Champion = Champion("Annie", Set(Sorcerer, MechPilot), 2)
      val Ashe: Champion = Champion("Ashe", Set(Sniper, Celestial), 3)
      val AurelionSol: Champion = Champion("Aurelion Sol", Set(Rebel, Starship), 5)
      val Blitzcrank: Champion = Champion("Blitzcrank", Set(Chrono, Brawler), 2)
      val Caitlyn: Champion = Champion("Caitlyn", Set(Sniper, Chrono), 1)
      val Chogath: Champion = Champion("Chogath", Set(Void, Brawler), 4)
      val Darius: Champion = Champion("Darius", Set(SpacePirate, ManaReaver), 2)
      val Ekko: Champion = Champion("Ekko", Set(Cybernetic, Infiltrator), 5)
      val Ezreal: Champion = Champion("Ezreal", Set(Chrono, Blaster), 3)
      val Fiora: Champion = Champion("Fiora", Set(Blademaster, Cybernetic), 1)
      val Fizz: Champion = Champion("Fizz", Set(MechPilot, Infiltrator), 4)
      val Gangplank: Champion = Champion("Gangplank", Set(SpacePirate, Demolitionist, Mercenary), 5)
      val Graves: Champion = Champion("Graves", Set(SpacePirate, Blaster), 1)
      val Irelia: Champion = Champion("Irelia", Set(Blademaster, ManaReaver, Cybernetic), 4)
      val JarvanIV: Champion = Champion("Jarvan IV", Set(DarkStar, Protector), 1)
      val Jayce: Champion = Champion("Jayce", Set(SpacePirate, Vanguard), 3)
      val Jhin: Champion = Champion("Jhin", Set(Sniper, DarkStar), 4)
      val Jinx: Champion = Champion("Jinx", Set(Rebel, Blaster), 4)
      val Kaisa: Champion = Champion("Kaisa", Set(Infiltrator, Valkyrie), 2)
      val Karma: Champion = Champion("Karma", Set(Mystic, DarkStar), 3)
      val Kassadin: Champion = Champion("Kassadin", Set(ManaReaver, Celestial), 3)
      val Kayle: Champion = Champion("Kayle", Set(Valkyrie, Blademaster), 4)
      val Khazix: Champion = Champion("Khazix", Set(Void, Infiltrator), 1)
      val Leona: Champion = Champion("Leona", Set(Vanguard, Cybernetic), 1)
      val Lucian: Champion = Champion("Lucian", Set(Blaster, Cybernetic), 2)
      val Lulu: Champion = Champion("Lulu", Set(Mystic, Celestial), 5)
      val Lux: Champion = Champion("Lux", Set(Sorcerer, DarkStar), 3)
      val Malphite: Champion = Champion("Malphite", Set(Brawler, Rebel), 1)
      val MasterYi: Champion = Champion("Master Yi", Set(Blademaster, Rebel), 3)
      val MissFortune: Champion = Champion("Miss Fortune", Set(Valkyrie, Blaster, Mercenary), 5)
      val Mordekaiser: Champion = Champion("Mordekaiser", Set(DarkStar, Vanguard), 2)
      val Neeko: Champion = Champion("Neeko", Set(StarGuardian, Protector), 3)
      val Poppy: Champion = Champion("Poppy", Set(Vanguard, StarGuardian), 1)
      val Rakan: Champion = Champion("Rakan", Set(Celestial, Protector), 2)
      val Rumble: Champion = Champion("Rumble", Set(Demolitionist, MechPilot), 3)
      val Shaco: Champion = Champion("Shaco", Set(DarkStar, Infiltrator), 3)
      val Shen: Champion = Champion("Shen", Set(Blademaster, Chrono), 2)
      val Sona: Champion = Champion("Sona", Set(Mystic, Rebel), 2)
      val Soraka: Champion = Champion("Soraka", Set(Mystic, StarGuardian), 4)
      val Syndra: Champion = Champion("Syndra", Set(StarGuardian, Sorcerer), 3)
      val Thresh: Champion = Champion("Thresh", Set(Chrono, ManaReaver), 5)
      val TwistedFate: Champion = Champion("Twisted Fate", Set(Sorcerer, Chrono), 1)
      val Velkoz: Champion = Champion("Velkoz", Set(Sorcerer, Void), 4)
      val Vi: Champion = Champion("Vi", Set(Cybernetic, Brawler), 3)
      val Wukong: Champion = Champion("Wukong", Set(Chrono, Vanguard), 4)
      val Xayah: Champion = Champion("Xayah", Set(Celestial, Blademaster), 1)
      val Xerath: Champion = Champion("Xerath", Set(Sorcerer, DarkStar), 5)
      val XinZhao: Champion = Champion("Xin Zhao", Set(Protector, Celestial), 2)
      val Yasuo: Champion = Champion("Yasuo", Set(Blademaster, Rebel), 2)
      val Ziggs: Champion = Champion("Ziggs", Set(Demolitionist, Rebel), 1)
      val Zoe: Champion = Champion("Zoe", Set(StarGuardian, Sorcerer), 1)

      val all: Set[Champion] = Set(
        Ahri,
        Annie,
        Ashe,
        AurelionSol,
        Blitzcrank,
        Caitlyn,
        Chogath,
        Darius,
        Ekko,
        Ezreal,
        Fiora,
        Fizz,
        Gangplank,
        Graves,
        Irelia,
        JarvanIV,
        Jayce,
        Jhin,
        Jinx,
        Kaisa,
        Karma,
        Kassadin,
        Kayle,
        Khazix,
        Leona,
        Lucian,
        Lulu,
        Lux,
        Malphite,
        MasterYi,
        MissFortune,
        Mordekaiser,
        Neeko,
        Poppy,
        Rakan,
        Rumble,
        Shaco,
        Shen,
        Sona,
        Soraka,
        Syndra,
        Thresh,
        TwistedFate,
        Velkoz,
        Vi,
        Wukong,
        Xayah,
        Xerath,
        XinZhao,
        Yasuo,
        Ziggs,
        Zoe
      )
    }
  }

  val CurrentSet: set8.type = set8

}
