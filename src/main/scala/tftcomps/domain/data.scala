package tftcomps.domain

object data {
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
    val Sorcerer: Role = Role("Sorcerer", Set(2, 4, 5, 8))
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
