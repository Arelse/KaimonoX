package com.kaimono.app.data

object Seed {
    private const val DAY = 24L * 60 * 60 * 1000

    data class SeedManga(
        val id: String, val title: String, val author: String, val description: String,
        val status: String, val sourceId: String, val rating: Float, val genres: String,
        val coverSeed: Int, val inLibrary: Boolean = false, val favorite: Boolean = false,
        val chapterCount: Int,
    )

    val sources = listOf(
        SourceEntity("keiyoshi", "Keiyoshi", "ko,zh", "1.4.27", "ext-keiyoshi", installed = true, nsfw = false),
        SourceEntity("mangabox", "MangaBox", "en,ja", "1.4.12", "ext-mangabox", installed = true),
        SourceEntity("asura", "Asura Scans", "en", "1.4.45", "ext-asura", installed = true),
        SourceEntity("toonxyz", "ToonXYZ", "en,ko", "1.4.9", "ext-toonxyz", installed = false),
    )

    val extensions = listOf(
        ExtensionEntity("ext-keiyoshi", "Keiyoshi", "1.4.27", 1, true, "Korean & Chinese titles. Fast CDN, Webtoon layout."),
        ExtensionEntity("ext-mangabox", "MangaBox", "1.4.12", 1, true, "Huge Japanese catalogue with official translations."),
        ExtensionEntity("ext-asura", "Asura Scans", "1.4.45", 1, true, "Popular fan-translated series, daily updates."),
        ExtensionEntity("ext-toonxyz", "ToonXYZ", "1.4.9", 1, false, "Webtoon aggregator. Enable to browse its catalogue."),
        ExtensionEntity("ext-comicextra", "ComicExtra", "1.4.3", 1, false, "Western comics and manga mirror."),
    )

    val categories = listOf(
        CategoryEntity(1, "Reading", 0),
        CategoryEntity(2, "Plan to Read", 1),
        CategoryEntity(3, "Completed", 2),
    )

    val manga = listOf(
        SeedManga("solo-leveling", "Solo Leveling", "Chugong / h-goon",
            "In a world where hunters battle deadly gates, Sung Jin-Woo is the weakest of all. After a mysterious dungeon grants him a unique system that lets him level up without limit, he begins a relentless climb from the world's weakest hunter to its most terrifying existence.",
            "Completed", "keiyoshi", 4.9f, "Action,Fantasy,Dungeon,System,Manhwa", 11,
            inLibrary = true, favorite = true, chapterCount = 179),
        SeedManga("orv", "Omniscient Reader's Viewpoint", "Sing-Shong / Sleepy-C",
            "Kim Dokja is the sole reader of a trashy webnovel that becomes reality. Using his knowledge of every plot twist, he fights to survive scenarios no one else understands — and to reach the ending he alone has read.",
            "Ongoing", "keiyoshi", 4.8f, "Action,Apocalypse,Fantasy,Manhwa", 22,
            inLibrary = true, favorite = false, chapterCount = 208),
        SeedManga("tbatb", "The Beginning After The End", "TurtleMe / Fuyuki23",
            "King Grey has unrivaled strength in a world of martial arts, yet a lonely life without purpose. Reborn into a new world of magic as Arthur Leywin, he gets a second chance to find the things he never had: family, friends, and a reason to fight.",
            "Ongoing", "keiyoshi", 4.7f, "Action,Adventure,Fantasy,Isekai,Manhwa", 33,
            inLibrary = false, favorite = false, chapterCount = 164),
        SeedManga("tower-god", "Tower of God", "SIU",
            "Bam enters the Tower to chase the only person he ever knew — Rachel. Each floor is a deadly test, and the top promises anything one desires. A sprawling epic of betrayal, friendship, and the price of ambition.",
            "Hiatus", "toonxyz", 4.6f, "Action,Adventure,Drama,Fantasy,Manhwa", 44,
            inLibrary = false, favorite = false, chapterCount = 550),
        SeedManga("csm", "Chainsaw Man", "Tatsuki Fujimoto",
            "Denji is a devil hunter drowning in debt, sawing devils with his chainsaw dog Pochita. When a betrayal kills him, Pochita fuses with his heart — and Denji becomes Chainsaw Man, the devil that devils fear most.",
            "Ongoing", "mangabox", 4.8f, "Action,Horror,Supernatural,Manga", 55,
            inLibrary = true, favorite = true, chapterCount = 157),
        SeedManga("jjk", "Jujutsu Kaisen", "Gege Akutami",
            "Yuji Itadori swallows a cursed finger to save his friends, becoming the vessel of Sukuna, the king of curses. He joins Tokyo Jujutsu High to learn how to exorcise curses — and to find a way to die on his own terms.",
            "Completed", "mangabox", 4.7f, "Action,Dark Fantasy,Supernatural,Manga", 66,
            inLibrary = false, favorite = false, chapterCount = 271),
        SeedManga("kaiju8", "Kaiju No. 8", "Naoya Matsumoto",
            "Kafka Hibino, 32, failed the kaiju defense exam and cleans up monster corpses. When a small kaiju invades his body, he gains the power to transform into one — becoming the very thing the Defense Force hunts.",
            "Ongoing", "mangabox", 4.6f, "Action,Sci-Fi,Kaiju,Manga", 77,
            inLibrary = false, favorite = false, chapterCount = 108),
        SeedManga("bluelock", "Blue Lock", "Muneyuki Kaneshiro / Yusuke Nomura",
            "After Japan's failure in the 2018 World Cup, the JFU hires Jinpachi Ego to create the world's best egotist striker. 300 high-school forwards are locked in a facility where only one can survive — the one who becomes the best in the world.",
            "Ongoing", "asura", 4.7f, "Sports,Soccer,Drama,Manga", 88,
            inLibrary = true, favorite = false, chapterCount = 265),
        SeedManga("dandadan", "Dandadan", "Yukinobu Tatsu",
            "Momo believes in ghosts but not aliens; Okarun believes in aliens but not ghosts. A bet to prove each other wrong drags both into a paranormal war of spirits, UFOs, and the weirdest romance in the galaxy.",
            "Ongoing", "asura", 4.8f, "Action,Comedy,Supernatural,Manga", 99,
            inLibrary = false, favorite = false, chapterCount = 153),
        SeedManga("pick-me", "Pick Me Up, Infinite Gacha", "Manual / Zar Pow",
            "A top-tier gacha game player wakes up inside his own game as a 1-star hunter. To escape the tower, he must climb 100 floors using nothing but game knowledge, ruthless efficiency, and the gacha itself.",
            "Ongoing", "asura", 4.5f, "Action,Fantasy,System,Manhwa", 111,
            inLibrary = false, favorite = false, chapterCount = 86),
    )

    suspend fun insert(db: AppDatabase) {
        val now = System.currentTimeMillis()
        db.sourceDao().upsertAll(sources)
        db.extensionDao().upsertAll(extensions)
        categories.forEach { db.categoryDao().insert(it) }

        val mangaRows = manga.mapIndexed { i, s ->
            MangaEntity(
                id = s.id, title = s.title, author = s.author, description = s.description,
                status = s.status, sourceId = s.sourceId, inLibrary = s.inLibrary,
                favorite = s.favorite, rating = s.rating, genres = s.genres,
                coverSeed = s.coverSeed,
                categoryId = when {
                    !s.inLibrary -> null
                    i % 3 == 1 -> 2L
                    s.status == "Completed" -> 3L
                    else -> 1L
                },
            )
        }
        db.mangaDao().upsertAll(mangaRows)

        val chapters = mutableListOf<ChapterEntity>()
        manga.forEachIndexed { mi, s ->
            repeat(s.chapterCount) { c ->
                val number = s.chapterCount - c
                val read = s.inLibrary && number <= (if (s.favorite) 12 else 5)
                chapters += ChapterEntity(
                    id = "ch-${s.id}-$number",
                    mangaId = s.id,
                    number = number.toDouble(),
                    title = when {
                        number == 1.0 -> "Prologue"
                        number == s.chapterCount.toDouble() -> "Latest Release"
                        else -> "Chapter $number"
                    },
                    pages = 8 + (s.coverSeed + number).toInt() % 6,
                    read = read,
                    lastReadAt = if (read) now - (c + 1) * 6L * 60 * 60 * 1000 else null,
                    dateAdded = now - (c * 2L + mi) * DAY - mi * 3600_000L,
                )
            }
        }
        db.chapterDao().upsertAll(chapters)

        db.historyDao().upsert(HistoryEntity("solo-leveling", "ch-solo-leveling-145", now - 2 * 3600_000L))
        db.historyDao().upsert(HistoryEntity("orv", "ch-orv-31", now - 26 * 3600_000L))
        db.historyDao().upsert(HistoryEntity("csm", "ch-csm-12", now - 3L * DAY))
        db.historyDao().upsert(HistoryEntity("bluelock", "ch-bluelock-88", now - 6L * DAY))
    }
}
