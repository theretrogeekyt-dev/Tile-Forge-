package com.example.game

data class FantasyQuote(
    val quote: String,
    val speaker: String,
    val source: String,
    val loreTag: String = "Fantasy Lore"
)

object FantasyQuotes {
    val quotes = listOf(
        FantasyQuote(
            quote = "All we have to decide is what to do with the time that is given us.",
            speaker = "Gandalf",
            source = "The Lord of the Rings: The Fellowship of the Ring",
            loreTag = "Middle-earth"
        ),
        FantasyQuote(
            quote = "There's some good in this world, Mr. Frodo, and it's worth fighting for.",
            speaker = "Samwise Gamgee",
            source = "The Lord of the Rings: The Two Towers",
            loreTag = "Middle-earth"
        ),
        FantasyQuote(
            quote = "Even the smallest person can change the course of the future.",
            speaker = "Galadriel",
            source = "The Lord of the Rings",
            loreTag = "Middle-earth"
        ),
        FantasyQuote(
            quote = "Not all those who wander are lost.",
            speaker = "Bilbo Baggins / Aragorn's Poem",
            source = "The Lord of the Rings",
            loreTag = "Middle-earth"
        ),
        FantasyQuote(
            quote = "It does not do to dwell on dreams and forget to live.",
            speaker = "Albus Dumbledore",
            source = "Harry Potter and the Sorcerer's Stone",
            loreTag = "Wizarding World"
        ),
        FantasyQuote(
            quote = "Happiness can be found, even in the darkest of times, if one only remembers to turn on the light.",
            speaker = "Albus Dumbledore",
            source = "Harry Potter and the Prisoner of Azkaban",
            loreTag = "Wizarding World"
        ),
        FantasyQuote(
            quote = "It is our choices, Harry, that show what we truly are, far more than our abilities.",
            speaker = "Albus Dumbledore",
            source = "Harry Potter and the Chamber of Secrets",
            loreTag = "Wizarding World"
        ),
        FantasyQuote(
            quote = "Words are, in my not-so-humble opinion, our most inexhaustible source of magic.",
            speaker = "Albus Dumbledore",
            source = "Harry Potter and the Deathly Hallows",
            loreTag = "Wizarding World"
        ),
        FantasyQuote(
            quote = "Courage dear heart.",
            speaker = "Aslan",
            source = "The Chronicles of Narnia: The Voyage of the Dawn Treader",
            loreTag = "Narnia"
        ),
        FantasyQuote(
            quote = "Once a king or queen of Narnia, always a king or queen of Narnia.",
            speaker = "Aslan",
            source = "The Chronicles of Narnia: The Lion, the Witch and the Wardrobe",
            loreTag = "Narnia"
        ),
        FantasyQuote(
            quote = "The mind needs books as a sword needs a whetstone, if it is to keep its edge.",
            speaker = "Tyrion Lannister",
            source = "A Song of Ice and Fire / Game of Thrones",
            loreTag = "Westeros"
        ),
        FantasyQuote(
            quote = "A reader lives a thousand lives before he dies. The man who never reads lives only one.",
            speaker = "Jojen Reed",
            source = "A Song of Ice and Fire / Game of Thrones",
            loreTag = "Westeros"
        ),
        FantasyQuote(
            quote = "Never forget what you are, for surely the world will not. Make it your strength. Then it can never be your weapon against you.",
            speaker = "Tyrion Lannister",
            source = "A Song of Ice and Fire / Game of Thrones",
            loreTag = "Westeros"
        ),
        FantasyQuote(
            quote = "The most important step a man can take. It's not the first one, is it? It's the next one. Always the next step.",
            speaker = "Dalinar Kholin",
            source = "The Stormlight Archive (Oathbringer)",
            loreTag = "Cosmere"
        ),
        FantasyQuote(
            quote = "Life before Death. Strength before Weakness. Journey before Destination.",
            speaker = "The First Ideal of the Knights Radiant",
            source = "The Stormlight Archive (The Way of Kings)",
            loreTag = "Cosmere"
        ),
        FantasyQuote(
            quote = "There is always another secret.",
            speaker = "Kelsier",
            source = "Mistborn: The Final Empire",
            loreTag = "Cosmere"
        ),
        FantasyQuote(
            quote = "The Wheel of Time turns, and Ages come and pass, leaving memories that become legend.",
            speaker = "Robert Jordan",
            source = "The Wheel of Time",
            loreTag = "The Wheel of Time"
        ),
        FantasyQuote(
            quote = "Death is lighter than a feather; duty, heavier than a mountain.",
            speaker = "Lan Mandragoran",
            source = "The Wheel of Time",
            loreTag = "The Wheel of Time"
        ),
        FantasyQuote(
            quote = "It's dangerous to go alone! Take this.",
            speaker = "Old Man",
            source = "The Legend of Zelda",
            loreTag = "Hyrule"
        ),
        FantasyQuote(
            quote = "Time passes, people move... Like a river's flow, it never ends. A childish mind will turn to noble ambition.",
            speaker = "Sheik",
            source = "The Legend of Zelda: Ocarina of Time",
            loreTag = "Hyrule"
        ),
        FantasyQuote(
            quote = "The flow of time is always cruel... its speed seems different for each person, but no one can change it.",
            speaker = "Sheik",
            source = "The Legend of Zelda: Ocarina of Time",
            loreTag = "Hyrule"
        ),
        FantasyQuote(
            quote = "What is better: to be born good, or to overcome your evil nature through great effort?",
            speaker = "Paarthurnax",
            source = "The Elder Scrolls V: Skyrim",
            loreTag = "Tamriel"
        ),
        FantasyQuote(
            quote = "May the wind be always at your back and the sun upon your face.",
            speaker = "Ancient Blessing",
            source = "Elven Lore",
            loreTag = "High Fantasy"
        ),
        FantasyQuote(
            quote = "Praise the Sun! Jolly cooperation ahead!",
            speaker = "Solaire of Astora",
            source = "Dark Souls",
            loreTag = "Lordran"
        ),
        FantasyQuote(
            quote = "No matter how dark the night, morning always comes, and our journey begins anew.",
            speaker = "Lulu",
            source = "Final Fantasy X",
            loreTag = "Spira"
        ),
        FantasyQuote(
            quote = "You can't give up on someone you love. Even when it feels like the whole world is falling apart.",
            speaker = "Geralt of Rivia",
            source = "The Witcher 3: Wild Hunt",
            loreTag = "The Continent"
        ),
        FantasyQuote(
            quote = "If I'm to choose between one evil and another, I'd rather not choose at all.",
            speaker = "Geralt of Rivia",
            source = "The Witcher: The Last Wish",
            loreTag = "The Continent"
        ),
        FantasyQuote(
            quote = "I am no man!",
            speaker = "Éowyn",
            source = "The Lord of the Rings: The Return of the King",
            loreTag = "Middle-earth"
        ),
        FantasyQuote(
            quote = "A hero cannot be defeated, simply because he does not give up.",
            speaker = "Peter S. Beagle",
            source = "The Last Unicorn",
            loreTag = "Mythic Fantasy"
        ),
        FantasyQuote(
            quote = "Fear cuts deeper than swords.",
            speaker = "Arya Stark",
            source = "A Game of Thrones",
            loreTag = "Westeros"
        ),
        FantasyQuote(
            quote = "It's fine now. Why? Because I am here!",
            speaker = "All Might",
            source = "My Hero Academia",
            loreTag = "U.A. High • Plus Ultra"
        ),
        FantasyQuote(
            quote = "Go beyond! PLUS ULTRA!",
            speaker = "All Might",
            source = "My Hero Academia",
            loreTag = "U.A. High • Plus Ultra"
        ),
        FantasyQuote(
            quote = "When there's nothing to be gained, rising to the challenge at that moment is surely the mark of a true hero!",
            speaker = "All Might",
            source = "My Hero Academia",
            loreTag = "U.A. High • Plus Ultra"
        ),
        FantasyQuote(
            quote = "Dreams can become reality! You too can become a hero!",
            speaker = "All Might",
            source = "My Hero Academia",
            loreTag = "U.A. High • One For All"
        ),
        FantasyQuote(
            quote = "Sometimes I feel like I'm a failure. But even so, I'm not gonna give up. Ever!",
            speaker = "Izuku Midoriya (Deku)",
            source = "My Hero Academia",
            loreTag = "Class 1-A • Deku"
        ),
        FantasyQuote(
            quote = "A smiling, dependable hero... That's what I want to be! That's why I'm giving it everything. For everyone!",
            speaker = "Izuku Midoriya (Deku)",
            source = "My Hero Academia",
            loreTag = "Class 1-A • Deku"
        ),
        FantasyQuote(
            quote = "It's your power, isn't it?! Whether you want to become a hero or not, it's your own power!",
            speaker = "Izuku Midoriya (Deku)",
            source = "My Hero Academia",
            loreTag = "U.A. Sports Festival"
        ),
        FantasyQuote(
            quote = "Stop talking. I will win. That's what a hero does!",
            speaker = "Katsuki Bakugo (Dynamight)",
            source = "My Hero Academia",
            loreTag = "Class 1-A • Explosion"
        ),
        FantasyQuote(
            quote = "If you wanna stop this, then stand up! Because I've got one thing to say to you: Never forget who you want to become!",
            speaker = "Shoto Todoroki",
            source = "My Hero Academia",
            loreTag = "Class 1-A • Half-Cold Half-Hot"
        ),
        FantasyQuote(
            quote = "A hero doesn't just save people's lives. A hero also saves their hearts. That's what I believe.",
            speaker = "Mirio Togata (Lemillion)",
            source = "My Hero Academia",
            loreTag = "U.A. High • The Big 3"
        ),
        FantasyQuote(
            quote = "There's nothing more cruel than letting someone fall for half-baked dreams.",
            speaker = "Shota Aizawa (Eraser Head)",
            source = "My Hero Academia",
            loreTag = "U.A. High Faculty"
        ),
        FantasyQuote(
            quote = "As long as you've got a passionate heart and never give up, anyone can become unbreakable!",
            speaker = "Eijiro Kirishima (Red Riot)",
            source = "My Hero Academia",
            loreTag = "Class 1-A • Unbreakable"
        )
    )

    fun getRandomQuote(): FantasyQuote {
        return quotes.random()
    }

    fun getQuoteForSeed(seed: Long): FantasyQuote {
        val index = (Math.abs(seed) % quotes.size).toInt()
        return quotes[index]
    }
}
