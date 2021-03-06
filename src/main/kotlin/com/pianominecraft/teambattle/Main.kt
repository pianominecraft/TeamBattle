package com.pianominecraft.teambattle

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.*
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class Main : JavaPlugin() {

    private val cfgFile = File(dataFolder, "config.txt")
    private val wlFile = File(dataFolder, "whitelist.txt")
    private val kingFile = File(dataFolder, "king.txt")
    private val leaderFile = File(dataFolder, "leaders.txt")
    private val managerFile = File(dataFolder, "managers.txt")

    override fun onEnable() {

        plugin = this

        setupRecipe()
        setupCooltime()
        setupSkill()

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
            cfgFile.createNewFile()
        } // dataFolder
        if (!wlFile.exists()) {
            wlFile.createNewFile()
            PrintWriter(wlFile).use {
                it.println("*")
            }
        } // whitelist.txt
        if (!kingFile.exists()) {
            kingFile.createNewFile()
            PrintWriter(kingFile).use {
                it.println("Uziuukky")
                it.println("OnlyJunyoung")
            }
        } // king.txt
        if (!leaderFile.exists()) {
            leaderFile.createNewFile()
        } // leaders.txt
        if (!managerFile.exists()) {
            managerFile.createNewFile()
        } // managers.txt
        BufferedReader(FileReader(kingFile)).use {
            val lines = it.readLines()
            println(lines)
            redKing = Bukkit.getOfflinePlayer(lines[0]).uniqueId
            blueKing = Bukkit.getOfflinePlayer(lines[1]).uniqueId
        } // king.txt
        BufferedReader(FileReader(leaderFile)).use {
            it.readLines().forEach { line ->
                leaders.add(Bukkit.getOfflinePlayer(line).uniqueId)
            }
        } // leader.txt
        if (cfgFile.exists()) {
            val br = BufferedReader(FileReader(cfgFile))
            val lines = br.lines()
            lines.forEach {
                val l = it.split(" ")
                if (l[1] == "red") {
                    team[Bukkit.getOfflinePlayer(l[0]).uniqueId] = 1
                } else if (l[1] == "blue") {
                    team[Bukkit.getOfflinePlayer(l[0]).uniqueId] = 2
                }
            }
            teamInit()
        } // config.txt
        wl = BufferedReader(FileReader(wlFile)).use { it.readLine() } != "*"
        if (wl) BufferedReader(FileReader(wlFile)).use {
            lines = it.readLines()
        } // whitelist.txt
        BufferedReader(FileReader(managerFile)).use {
            managers = it.readLines()
        } // managers.txt
        repeat(20) {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (p.health > 0) {
                    if (Cooltime.getOf("combat", p.uniqueId) < 140) {
                        if (p.health < p.maxHealth - (p.maxHealth / 40)) {
                            p.health = p.health + (p.maxHealth / 40)
                        } else {
                            p.health = p.maxHealth
                        }
                    }
                }
            }
        } // health regen
        repeat {
            Cooltime.keys().forEach { k ->
                Cooltime.getMap(k)!!.forEach { (t, u) ->
                    if (u > 0) {
                        Cooltime.subtract(k, t, 1)
                    }
                }
            }
        } //cooltime
        repeat {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (compass[p] == true) {
                    if (team[p.uniqueId] == 1) {
                        blueKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                                if (p.itemInHand.type == Material.COMPASS)
                                    if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%??????%white%%bold%?????? ??? %gold%%bold%??????%white%%bold%???")))
                                    } else {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%??????%white%%bold%?????? ??? %green%%bold%??????")))
                                    }

                            } catch (e: Exception) {
                            }
                        }
                    } else if (team[p.uniqueId] == 2) {
                        redKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                            } catch (e: Exception) {
                            }
                            if (p.itemInHand.type == Material.COMPASS)
                                if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%??????%white%%bold%?????? ??? %gold%%bold%??????%white%%bold%???")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%??????%white%%bold%?????? ??? %green%%bold%??????")))
                                }
                        }
                    }
                } else {
                    if (team[p.uniqueId] == 1) {
                        redKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                            } catch (e: Exception) {
                            }
                            if (p.itemInHand.type == Material.COMPASS)
                                if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%??????%white%%bold%?????? ??? %gold%%bold%??????%white%%bold%???")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%??????%white%%bold%?????? ??? %green%%bold%??????")))
                                }
                        }
                    } else if (team[p.uniqueId] == 2) {
                        blueKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                            } catch (e: Exception) {
                            }
                            if (p.itemInHand.type == Material.COMPASS)
                                if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%??????%white%%bold%?????? ??? %gold%%bold%??????%white%%bold%???")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%??????%white%%bold%?????? ??? %green%%bold%??????")))
                                }
                        }
                    }
                }
            }
        } // compass
        repeat {
            if (time > 0) {
                time--
                bossBar.removeAll()
                bossBar = Bukkit.createBossBar("[?????? ??????] ${time / 1200} : ${time % 1200 / 20}", BarColor.YELLOW, BarStyle.SOLID)
                val d = time
                bossBar.progress = d.toDouble() / 24000
                Bukkit.getOnlinePlayers().forEach {
                    bossBar.addPlayer(it)
                }
                bossBar.isVisible = true
            } else {
                bossBar.removeAll()
                Bukkit.getWorld("world")?.difficulty = Difficulty.NORMAL
                time = -1
            }
        } // time and bossBar
        repeat {
            if (blueKing != null) {
                Bukkit.getPlayer(blueKing!!)?.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 5, 0, false, false))
                Bukkit.getPlayer(blueKing!!)?.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 5, 127, false, false))
                Bukkit.getPlayer(blueKing!!)?.maxHealth = 40.0
            }
            if (redKing != null) {
                Bukkit.getPlayer(redKing!!)?.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 5, 0, false, false))
                Bukkit.getPlayer(redKing!!)?.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 5, 127, false, false))
                Bukkit.getPlayer(redKing!!)?.maxHealth = 40.0
            }
            leaders.forEach {
                Bukkit.getPlayer(it)?.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 5, 0, false, false))
                Bukkit.getPlayer(it)?.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 5, 127, false, false))
                Bukkit.getPlayer(it)?.maxHealth = 30.0
            }
            Bukkit.getOnlinePlayers().forEach {
                if (it.uniqueId != blueKing && it.uniqueId != redKing && it.uniqueId !in leaders) {
                    it.maxHealth = 20.0
                }
            }
        } // glow
        repeat {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (p.world.name == "world_nether") {
                    if (!charging.containsKey(p.uniqueId)) {
                        if (Cooltime.getOf("o2", p.uniqueId) > 0) {
                            Cooltime.subtract("o2", p.uniqueId, 1)
                            if (Cooltime.getOf("o2", p.uniqueId) % 1200 >= 10) {
                                o2time[p.uniqueId]?.isVisible = false
                                o2time[p.uniqueId] = Bukkit.createBossBar(
                                    text("%red%%bold%?????? ?????? ?????? ${Cooltime.getOf("o2", p.uniqueId) / 1200} %white%%bold%: %red%%bold%${Cooltime.getOf("o2", p.uniqueId) % 1200 / 20}"),
                                    BarColor.RED,
                                    BarStyle.SOLID
                                )
                                with (o2time[p.uniqueId]!!) {
                                    progress = Cooltime.getOf("o2", p.uniqueId) / 9000.0
                                    addPlayer(p)
                                    isVisible = true
                                }

                            } else {
                                o2time[p.uniqueId]?.isVisible = false
                                o2time[p.uniqueId] = Bukkit.createBossBar(
                                    text("%red%%bold%?????? ?????? ?????? ${Cooltime.getOf("o2", p.uniqueId) / 1200} %white%%bold%: %red%%bold%0${Cooltime.getOf("o2", p.uniqueId) % 1200 / 20}"),
                                    BarColor.RED,
                                    BarStyle.SOLID
                                )
                                with (o2time[p.uniqueId]!!) {
                                    progress = Cooltime.getOf("o2", p.uniqueId) / 9000.0
                                    addPlayer(p)
                                    isVisible = true
                                }
                            }
                            if (Cooltime.getOf("o2", p.uniqueId) == 600) {
                                p.sendTitle(text("%dark_red%??????! ?????? ??????"), text("%red%????????? ???????????? ??????????????? ???????????????"), 0, 20, 0)
                                p.playSound(p.location, Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f)
                            }
                        }
                    }
                }
                else {
                    if (Cooltime.getOf("o2", p.uniqueId) < 9000) {
                        if (Cooltime.getOf("o2", p.uniqueId) % 1200 >= 10) {
                            o2time[p.uniqueId]?.isVisible = false
                            o2time[p.uniqueId] = Bukkit.createBossBar(
                                text("%green%%bold%?????? ?????????... %aqua%%bold%?????? ?????? ${Cooltime.getOf("o2", p.uniqueId) / 1200} %white%%bold%: %aqua%%bold%${Cooltime.getOf("o2", p.uniqueId) % 1200 / 20}"),
                                BarColor.RED,
                                BarStyle.SOLID
                            )
                            with (o2time[p.uniqueId]!!) {
                                progress = Cooltime.getOf("o2", p.uniqueId) / 9000.0
                                addPlayer(p)
                                isVisible = true
                            }
                        } else {
                            o2time[p.uniqueId]?.isVisible = false
                            o2time[p.uniqueId] = Bukkit.createBossBar(
                                text("%green%%bold%?????? ?????????... %aqua%%bold%?????? ?????? ${Cooltime.getOf("o2", p.uniqueId) / 1200} %white%%bold%: %aqua%%bold%0${Cooltime.getOf("o2", p.uniqueId) % 1200 / 20}"),
                                BarColor.RED,
                                BarStyle.SOLID
                            )
                            with (o2time[p.uniqueId]!!) {
                                progress = Cooltime.getOf("o2", p.uniqueId) / 9000.0
                                addPlayer(p)
                                isVisible = true
                            }
                        }
                        Cooltime.add("o2", p.uniqueId, 1)
                    }
                }
            }
        } // Oxygen
        repeat {
            if (!redKingLive) {
                Bukkit.getOnlinePlayers().forEach { p ->
                    if (team[p.uniqueId] == 1 ) {
                        p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2, 0, false, false))
                        p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 2, 0, false, false))
                    }
                }
            } else if (!blueKingLive) {
                Bukkit.getOnlinePlayers().forEach { p ->
                    if (team[p.uniqueId] == 2) {
                        p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 2, 0, false, false))
                        p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 2, 0, false, false))
                    }
                }
            }
        } // when king die, give buff
        repeat (20) {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (Cooltime.getOf("o2", p.uniqueId) == 0) {
                    p.damage(5.0)
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%dark_red%%bold%?????? ??????!")))
                }
            }
        } // Oxygen damage
        Bukkit.getPluginManager().registerEvents(EventManager(), this)
        Bukkit.getLogger().info("[TeamBattle] has been enabled!")
    }
    private fun setupRecipe() {
        with (server) {
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("compacted_magma_cream"),
                    ItemStack(Material.MAGMA_CREAM).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ????????? ??????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }
                ).apply {
                    shape(
                        " M ",
                        "MMM",
                        " M "
                    )
                    setIngredient('M', Material.MAGMA_CREAM)
                }
            )
        } // Magma
        with (server) {
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("teambattle_blaze_powder"),
                    ItemStack(Material.BLAZE_POWDER)
                ).apply {
                    shape(
                        "QGQ",
                        "QGQ",
                        "QGQ"
                    )
                    setIngredient('G', Material.GOLD_INGOT)
                    setIngredient('Q', Material.QUARTZ)
                }
            )
            this.addRecipe(
                ShapelessRecipe(
                    NamespacedKey.minecraft("blaze_rod"),
                    ItemStack(Material.BLAZE_ROD)
                ).apply {
                    addIngredient(Material.BLAZE_POWDER)
                    addIngredient(Material.BLAZE_POWDER)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("compacted_nether_brick"),
                    ItemStack(Material.NETHER_BRICK).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ?????? ??????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }
                ).apply {
                    shape(
                        "NNN",
                        "NNN",
                        "NNN"
                    )
                    setIngredient('N', Material.NETHER_BRICK)
                }
            )
            this.addRecipe(
                ShapelessRecipe(
                    NamespacedKey.minecraft("compacted_obsidian"),
                    ItemStack(Material.OBSIDIAN).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ?????????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }
                ).apply {
                    addIngredient(Material.OBSIDIAN)
                    addIngredient(Material.OBSIDIAN)
                    addIngredient(Material.OBSIDIAN)
                    addIngredient(Material.OBSIDIAN)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("compacted_blaze_rod"),
                    ItemStack(Material.BLAZE_ROD).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ???????????? ??????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }
                ).apply {
                    shape(
                        " B ",
                        " B ",
                        " B "
                    )
                    setIngredient('B', Material.BLAZE_ROD)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("teambattle_netherite"),
                    ItemStack(Material.NETHERITE_INGOT)
                ).apply {
                    shape(
                        "ONO",
                        "NBN",
                        "ONO"
                    )
                    setIngredient('O', RecipeChoice.ExactChoice(ItemStack(Material.OBSIDIAN).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ?????????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }))
                    setIngredient('N', RecipeChoice.ExactChoice(ItemStack(Material.NETHER_BRICK).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ?????? ??????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }))
                    setIngredient('B', RecipeChoice.ExactChoice(ItemStack(Material.BLAZE_ROD).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%aqua%????????? ???????????? ??????"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }))
                }
            )
        } // Netherite
        with (server) {
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2o"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.OAK_SAPLING)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2s"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.SPRUCE_SAPLING)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2b"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.BIRCH_SAPLING)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2d"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.DARK_OAK_SAPLING)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2j"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.JUNGLE_SAPLING)
                }
            )
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("o2a"),
                    ItemStack(Material.HONEY_BOTTLE).apply {
                        itemMeta = itemMeta?.apply {
                            setDisplayName(text("%gold%?????????"))
                        }
                    }
                ).apply {
                    shape(
                        "GGG",
                        "BBB",
                        "SSS"
                    )
                    setIngredient('G', Material.GLOWSTONE)
                    setIngredient('B', Material.GLASS_BOTTLE)
                    setIngredient('S', Material.ACACIA_SAPLING)
                }
            )
        } // Oxygen
        with (server) {
            this.addRecipe(
                ShapedRecipe(
                    NamespacedKey.minecraft("teambattle_crossbow"),
                    ItemStack(Material.CROSSBOW)
                ).apply {
                    shape(
                        " S ",
                        "SIS",
                        "sss"
                    )
                    setIngredient('S', Material.STICK)
                    setIngredient('I', Material.IRON_INGOT)
                    setIngredient('s', Material.STRING)
                }
            )
        } // Crossbow
    }
    private fun setupCooltime() {
        Cooltime.addKey("o2")
        Cooltime.addKey("combat")
        Cooltime.addKey("leader_diamond")
        Cooltime.addKey("king_diamond")
        Cooltime.addKey("king_gold")
    }
    private fun setupSkill() {
        skills.add(Skill(
            { e ->
                if (team[e.player.uniqueId] == 1) {
                    if (redKingLive) {
                        for (i in 0..2) {
                            plugin.delay((i * 20).toLong()) {
                                e.player.sendTitle(
                                    text("?????? ?????? %gold%0 : 0${3 - i}"),
                                    text("%red%${Bukkit.getOfflinePlayer(redKing!!).name}???(???) %gold%??????%white%?????? ?????? ????????????.."),
                                    0,
                                    20,
                                    0
                                )
                            }
                        }
                        Bukkit.getPlayer(redKing!!)
                            ?.sendMessage(text("${teamColor[1]}${e.player.name}%white%???(???) %gold%??????%white%?????? ?????? ????????????..."))
                        plugin.delay(60) {
                            e.player.teleport(Bukkit.getPlayer(redKing!!)!!)
                            Bukkit.getOnlinePlayers().forEach { p ->
                                p.spawnParticle(
                                    Particle.PORTAL,
                                    e.player.location,
                                    500,
                                    0.0,
                                    0.0,
                                    0.0,
                                    1.5
                                )
                                p.spawnParticle(
                                    Particle.END_ROD,
                                    e.player.location,
                                    150,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.1
                                )
                            }
                            e.player.sendTitle(text("%red%?????? ??????!"), text("%gray%?????? ???????????????!"))
                            Bukkit.getPlayer(redKing!!)
                                ?.sendMessage(text("${teamColor[1]}${e.player.name}%white%???(???) %gold%?????? ??????%white%????????????!"))
                            Bukkit.getPlayer(redKing!!)
                                ?.playSound(
                                    e.player.location,
                                    Sound.ENTITY_ENDERMAN_TELEPORT,
                                    1f,
                                    1f
                                )
                        }
                    }
                    else {
                        e.player.sendMessage(text("%red%???????????? ??? ?????? ????????????"))
                    }
                }
                else if (team[e.player.uniqueId] == 2) {
                    if (blueKingLive) {
                        for (i in 0..2) {
                            plugin.delay((i * 20).toLong()) {
                                e.player.sendTitle(
                                    text("?????? ?????? %gold%0 : 0${3 - i}"),
                                    text("%blue%${Bukkit.getOfflinePlayer(blueKing!!).name}???(???) %gold%??????%white%?????? ?????? ????????????.."),
                                    0,
                                    20,
                                    0
                                )
                            }
                        }
                        Bukkit.getPlayer(blueKing!!)
                            ?.sendMessage(text("${teamColor[2]}${e.player.name}%white%???(???) %gold%??????%white%?????? ?????? ????????????..."))
                        plugin.delay(60) {
                            e.player.teleport(Bukkit.getPlayer(blueKing!!)!!)
                            Bukkit.getOnlinePlayers().forEach { p ->
                                p.spawnParticle(
                                    Particle.PORTAL,
                                    e.player.location,
                                    500,
                                    0.0,
                                    0.0,
                                    0.0,
                                    1.5
                                )
                                p.spawnParticle(
                                    Particle.END_ROD,
                                    e.player.location,
                                    150,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.1
                                )
                            }
                            e.player.playSound(
                                e.player.location,
                                Sound.ENTITY_ENDERMAN_TELEPORT,
                                1f,
                                1f
                            )
                            e.player.sendTitle(text("%red%?????? ??????!"), text("%gray%?????? ???????????????!"))
                            Bukkit.getPlayer(blueKing!!)
                                ?.sendMessage(text("${teamColor[2]}${e.player.name}%white%???(???) %gold%?????? ??????%white%????????????!"))
                            Bukkit.getPlayer(blueKing!!)
                                ?.playSound(
                                    e.player.location,
                                    Sound.ENTITY_ENDERMAN_TELEPORT,
                                    1f,
                                    1f
                                )
                        }
                    }
                    else {
                        e.player.sendMessage(text("%red%???????????? ??? ?????? ????????????"))
                    }
                }
                else {
                    e.player.sendMessage(text("%red%???????????? ??? ?????? ????????????"))
                }
            },
            { e ->
                e.player.uniqueId in leaders
            },
            "leader_diamond",
            3000,
            Material.DIAMOND
        )) // leader diamond skill
        skills.add(Skill(
            { e ->
                val soldiers =
                    (if (e.player.uniqueId == redKing) Bukkit.getOnlinePlayers().filter {
                        team[it.uniqueId] == 1 && it.uniqueId != redKing && it.uniqueId !in leaders
                    }
                    else Bukkit.getOnlinePlayers().filter {
                        team[it.uniqueId] == 2 && it.uniqueId != redKing && it.uniqueId !in leaders
                    }).shuffled().take(5)
                soldiers.forEach {
                    it.sendTitle(
                        text("%gray%?????? ??? %gold%Shift%gray%???"),
                        text("%red%${e.player.name}%white%?????? ????????? %gold%??????%white%????????? ?????????"),
                        0,
                        40,
                        20
                    )
                }
                for (i in 0..2) {
                    plugin.delay((i * 20).toLong()) {
                        e.player.sendTitle(
                            text("?????? ?????? %gold%0 : 0${3 - i}"),
                            text("%red%??????%white%?????? %gold%??????%white%?????? ????????????.."),
                            0,
                            20,
                            0
                        )
                    }
                }
                e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                plugin.delay(60) {
                    Bukkit.getOnlinePlayers().forEach { p ->
                        p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                        p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                    }
                    soldiers.forEach {
                        if (!it.isSneaking) {
                            it.teleport(Bukkit.getPlayer(redKing!!)!!)
                            it.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                            it.sendTitle(text("%red%?????? ??????!"), text("%gray%?????? ???????????????!"))
                            it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                        } else {
                            e.player.sendMessage(text("?????? %red%${it.name}%white%???(???) ????????? %gold%??????%white%????????????!"))
                            it.sendTitle(
                                text("%red%??? ?????????!"),
                                text("%gray%?????? ?????? ????????? ??????????????????!"),
                                0,
                                40,
                                20
                            )
                            it.playSound(it.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 1f)
                        }
                    }
                    e.player.sendMessage(text("${teamColor[2]}???????????? ????????? ????????? %gold%??????%white%???????????????"))
                    e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                }
            },
            { e ->
                e.player.uniqueId in listOf(redKing, blueKing)
            },
            "king_diamond",
            3600,
            Material.DIAMOND
        )) // king diamond skill
    }

    override fun onDisable() {
        bossBar.isVisible = false
        fileRefresh()
        Bukkit.getLogger().info("[TeamBattle] has been disabled!")
    }
    fun fileRefresh() {
        if (!cfgFile.exists()) cfgFile.createNewFile()
        PrintWriter(cfgFile).use {
            team.forEach { (p, t) ->
                if (t == 1) {
                    it.println("${Bukkit.getOfflinePlayer(p).name} red")
                } else if (t == 2) {
                    it.println("${Bukkit.getOfflinePlayer(p).name} blue")
                }
            }
        } // config.txt
        if (!kingFile.exists()) kingFile.createNewFile()
        PrintWriter(kingFile).use {
            it.println(redKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
            it.println(blueKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
            println(redKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
            println(blueKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
        } // king.txt
        if (!leaderFile.exists()) leaderFile.createNewFile()
        PrintWriter(leaderFile).use {
            leaders.forEach { l ->
                it.println(Bukkit.getOfflinePlayer(l).name)
            }
        } // leaders.txt
    }

    override fun onCommand(s: CommandSender, c: Command, l: String, a: Array<out String>): Boolean {

        if (c.name.equals("tb", ignoreCase = true)) {
            if (a.isNotEmpty()) {
                when {
                    a[0].equals("start", ignoreCase = true) -> {
                        if (s.isOp) {
                            time = 24000
                            bossBar = Bukkit.createBossBar("[?????? ??????] 20 : 00", BarColor.YELLOW, BarStyle.SOLID)
                            Bukkit.getOnlinePlayers().forEach {
                                bossBar.addPlayer(it)
                                it.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 24000, 127, false, false))
                                Bukkit.getWorld("world")?.difficulty = Difficulty.PEACEFUL
                                it.playSound(it.location, Sound.UI_TOAST_IN, 1f, 1f)
                                it.sendTitle(text("%gold%?????? ??????"), text("%gray%20?????? ????????? ?????? ????????? ????????????!"), 20, 60, 20)
                                it.sendMessage(text("%gold%?????? ??????"))
                                it.sendMessage(text("%gray%20?????? ????????? ?????? ????????? ????????????!"))
                            }
                            bossBar.isVisible = true
                        }
                    }
                    a[0].equals("stop", ignoreCase = true) -> {
                        if (s.isOp) time = 0
                    }
                    a[0].equals("team", ignoreCase = true) -> {
                        if (s.isOp) {
                            if (a.size > 2) {
                                if (a[2] == "1" || a[2] == "2") {
                                    if (Bukkit.getOfflinePlayer(a[1]).uniqueId == redKing) {
                                        redKing = null
                                        blueKing = Bukkit.getOfflinePlayer(a[1]).uniqueId
                                    }
                                    else if (Bukkit.getOfflinePlayer(a[1]).uniqueId == blueKing) {
                                        blueKing = null
                                        redKing = Bukkit.getOfflinePlayer(a[1]).uniqueId
                                    }
                                    team[Bukkit.getOfflinePlayer(a[1]).uniqueId] = a[2].toInt()
                                    teamInit()
                                    s.sendMessage(text("%green%${a[1]}?????? ?????? ????????? ??????????????????"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%???????????? ????????? ?????? ????????? ??????????????????"))
                                }
                            }
                        }
                    }
                    a[0].equals("manage", ignoreCase = true) -> {
                        if (s.isOp) {
                            if (a.size > 1) {
                                when {
                                    a[1].equals("move", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                move = true
                                                s.sendMessage(text("%green%??????????????? ??????????????????"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                move = false
                                                s.sendMessage(text("%red%??????????????? ??????????????????"))
                                                return true
                                            }
                                        }
                                        s.sendMessage("Move : $move")
                                    }
                                    a[1].equals("interact", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                interact = true
                                                s.sendMessage(text("%green%??????????????? ??????????????????"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                interact = false
                                                s.sendMessage(text("%red%??????????????? ??????????????????"))
                                                return true
                                            }
                                        }
                                        s.sendMessage("Interact : $interact")
                                    }
                                    a[1].equals("damage", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                damage = true
                                                s.sendMessage(text("%green%????????? ????????? ??????????????????"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                damage = false
                                                s.sendMessage(text("%red%????????? ????????? ??????????????????"))
                                                return true
                                            }
                                        }
                                        s.sendMessage("Damage : $damage")
                                    }
                                }
                            }
                        }
                    }
                    a[0].equals("king", ignoreCase = true) -> {
                        if (s.isOp) {
                            if (a.size > 1) {
                                val p = Bukkit.getOfflinePlayer(a[1])
                                if (team[p.uniqueId] == 1) {
                                    redKing = p.uniqueId
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(???)??? ?????? ?????? ????????? ??????????????????"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%????????? ?????? ?????? ????????? ??????????????????"))
                                } else if (team[p.uniqueId] == 2) {
                                    blueKing = p.uniqueId
                                    s.sendMessage(text("${teamColor[2]}${a[1]}%green%(???)??? ?????? ?????? ????????? ??????????????????"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%????????? ?????? ?????? ????????? ??????????????????"))
                                }
                                teamInit()
                            }
                        }
                    }
                    a[0].equals("leader", ignoreCase = true) -> {
                        if (s.isOp) {
                            if (a.size > 1) {
                                if (Bukkit.getOfflinePlayer(a[1]).uniqueId !in leaders) {
                                    leaders.add(Bukkit.getOfflinePlayer(a[1]).uniqueId)
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(???)??? ?????? ???????????? ??????????????????"))
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage(text("%green%????????? ?????? ???????????? ??????????????????"))
                                } else {
                                    leaders.remove(Bukkit.getOfflinePlayer(a[1]).uniqueId)
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(???)??? ?????? ??????????????? ??????????????????"))
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage(text("%green%????????? ?????? ??????????????? ??????????????????"))
                                }
                                teamInit()
                            }
                        }
                    }
                    a[0].equals("whitelist", ignoreCase = true) -> {
                        if (s.isOp) {
                            wl = true
                            lines = ArrayList()
                            if (!wlFile.exists()) {
                                wlFile.createNewFile()
                            }
                            val pw = PrintWriter(wlFile)
                            val array = ArrayList<String>()
                            Bukkit.getOnlinePlayers().forEach {
                                array.add(it.name)
                                pw.println(it.name)
                            }
                            lines = array
                            BufferedReader(FileReader(wlFile)).use { it ->
                                it.readLines().forEach {
                                    s.sendMessage(text("%green%$it"))
                                }
                            }
                            pw.close()
                        }
                    }
                    a[0].equals("rule", ignoreCase = true) -> {
                        if (s is Player) {
                            s.sendMessage("??????")
                            s.sendMessage("- ??? ?????? ????????? ( ????????? ?????? ????????? ?????? ????????? ?????? ??????????????? ??????????????? )")
                            s.sendMessage("- ????????? ?????? ?????? ????????? ???????????? ( ????????? ??? ???????????? ??????????????? )")
                            s.sendMessage("- ???????????? ?????? ????????? ????????????????????? ( ????????? ??????????????? ?????? ???????????? ??????????????? )")
                            s.sendMessage("???")
                            s.sendMessage("- ????????? ???????????? ???????????????.")
                            s.sendMessage("- ???, ??????, ????????? ????????????????????????")
                            s.sendMessage("- ?????? ????????? ???????????? ???????????????")
                            s.sendMessage(")        - ?????? ??? ???, ????????? 5????????????. ?????? ??? ????????? ??????, ??? ?????? ???????????? ????????????.")
                            s.sendMessage("- ?????? ???????????? ??? 20?????? ????????????(????????????)??? ???????????????.")
                            s.sendMessage("- ???????????? ???????????? ?????? ?????? ??????, ??? ?????? ?????? ????????? ????????? ??? ????????????. (??????????????? ??????)")
                            s.sendMessage("- ?????? ???????????? ?????? ????????????(?????????) ???????????? ?????? 5????????? ????????? ??? ????????????.")
                            s.sendMessage("????????? 3?????????, ????????? ???????????? ???????????? ????????? ??? ????????????.")
                            s.sendMessage("- ?????? ????????? ?????? ????????????(?????????) ????????? ??? ??? ????????????. ???????????? 1????????????.")
                            s.sendMessage("- ?????? ?????? ????????? ( ???????????????, ???????????????, ??????????????? ??? ???)??? ?????? ????????????.")
                            s.sendMessage("- ?????? ????????? ?????? 20???(40HP), ????????? ????????? ?????? 15???(30HP),")
                            s.sendMessage("?????? ????????? ?????? 10???(20HP)")
                            s.sendMessage("- ????????? 5?????? ????????? ?????????, ????????? 10?????? ????????? ????????? ????????????.")
                            s.sendMessage("- ????????? ?????? ?????? ???????????? ??????????????? ?????????.")
                            s.sendMessage("- ????????? ???????????? ?????? ????????????(?????????) ????????? ????????? ??? ??? ????????????.")
                            s.sendMessage("?????? ????????? ?????????, ???????????? 2??? 30????????????.")
                            s.sendMessage("- ?????? ????????? ???????????? ?????? ????????????.")
                            s.sendMessage("- ?????? ???????????? ??? ?????? ???????????? ??? ??? ?????? ??????, ??????????????? ???????????? ?????????.")
                            s.sendMessage("- ?????? ????????? ??????????????? ????????? ?????? ????????? ????????? ?????????!")
                        }
                    }
                    a[0].equals("reload", ignoreCase = true) -> {
                        if (s.isOp) {
                            wl = BufferedReader(FileReader(wlFile)).use { it.readLine() } != "*"
                            if (wl) {
                                lines = BufferedReader(FileReader(wlFile)).use { it.readLines() }
                            }
                        }
                    }
                    a[0].equals("debug", ignoreCase = true) -> {
                        if (s.name == "pianominecraft") {
                            s.sendMessage("time : $time")
                            redKing?.let {
                                s.sendMessage("redKing : ${Bukkit.getOfflinePlayer(redKing!!).name}")
                                s.sendMessage("redKing's team : ${team[redKing]}")
                            }
                            blueKing?.let {
                                s.sendMessage("blueKing : ${Bukkit.getOfflinePlayer(blueKing!!).name}")
                                s.sendMessage("blueKing's team : ${team[blueKing]}")
                            }
                            leaders.forEach {
                                s.sendMessage("leaders : ${Bukkit.getOfflinePlayer(it).name}")
                            }
                            s.sendMessage("redKingLive : $redKingLive")
                            s.sendMessage("blueKingLive : $blueKingLive")
                            s.sendMessage("wl : $wl")
                            s.sendMessage(text("#00ff00Text! %green%this is green!"))
                        }
                        else {
                            s.sendMessage("${ChatColor.RED}??? ???????????? ????????? pianominecraft?????? ?????? ???????????? ?????? ???????????? ???????????? ???????????? ??????????????????")
                        }
                    }
                    a[0].equals("help", ignoreCase = true) -> {
                        s.sendMessage(text("%gold%/tb start %white%: %green%????????? ???????????? ?????? ????????? ??????????????????(OP)"))
                        s.sendMessage(text("%gold%/tb stop %white%: %green%????????? ????????? ???????????? ?????? ????????? ??????????????? [???????????? ???????????? ??? ???](OP)"))
                        s.sendMessage(text("%gold%/tb team [player(nickname)] [int(1=red,2=blue)] %white%: %green%?????? ??????????????? ?????? ????????? ????????????(OP)"))
                        s.sendMessage(text("%gold%/tb manage [event(interact,damage,move)] %white%: %green%?????? ???????????? ?????? ?????? ????????? ???????????????(OP)"))
                        s.sendMessage(text("%gold%/tb manage [event(interact,damage,move)] [boolean(true,false)] %white%: %green%?????? ???????????? ???????????????(OP)"))
                        s.sendMessage(text("%gold%/s %white%: %green%???????????? ?????? ?????????????????? ???????????? ???????????????(OP, Manager)"))
                        s.sendMessage(text("%gold%/tb king [player(nickname)] %white%: %green%?????? ??????????????? ??? ?????? ????????? ???????????????(OP)"))
                        s.sendMessage(text("%gold%/tb leader [player(nickname)] %white%: %green%?????? ??????????????? ?????? ???????????? ??????????????? ?????? ?????? ?????????????????? ???????????????(OP)"))
                        s.sendMessage(text("%gold%/tb whitelist %white%: %green%?????? ????????? ?????? ??????????????? ????????? ???????????? ???????????? ????????? ???????????? ??????????????????(OP)"))
                        s.sendMessage(text("%gold%/tb rule %white%: %green%\"????????????\" ???????????? ?????? ????????? ????????? ???????????????"))
                        s.sendMessage(text("%gold%/tb reload %white%: %green%????????? ???????????? ????????? ???????????? ?????? ????????? ?????? ????????? ???????????? ?????? ??????????????????(OP)"))
                        s.sendMessage(text("%gold%/tb help %white%: %green%?????? ??? ???????????? ???????????????"))
                        s.sendMessage(text("%gold%/tb cooltime %white%: %green%???????????? ??????????????????(OP)(????????????)"))
                        s.sendMessage(text("%gold%/tb world %white%: %green%????????? ?????? ?????? ??????????????????(OP)"))
                        s.sendMessage(text("%gold%/stp [player(nickname)] %white%: %green%?????????????????? ???????????? ?????????(OP, Manager, Spectator)"))
                    }
                    a[0].equals("world", ignoreCase = true) -> {
                        if (s.isOp) {
                            val world = Bukkit.getWorld("world")!!
                            val worldNether = Bukkit.getWorld("world_nether")!!
                            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
                            world.worldBorder.size = 1000.0
                            worldNether.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
                            worldNether.worldBorder.size = 1000.0
                            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
                            world.setGameRule(GameRule.NATURAL_REGENERATION, false)
                            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
                            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
                            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
                            world.setGameRule(GameRule.KEEP_INVENTORY, true)
                            worldNether.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                            worldNether.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
                            worldNether.setGameRule(GameRule.NATURAL_REGENERATION, false)
                            worldNether.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
                            worldNether.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
                            worldNether.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
                            worldNether.setGameRule(GameRule.KEEP_INVENTORY, true)
                        }
                    }
                }
            }
        }
        else if (c.name.equals("s", ignoreCase = true)) {
            if (s.isOp || s.name in managers) {
                var st = ""
                for (element in a) {
                    st += "$element "
                }
                Bukkit.broadcastMessage(text("%gray%[?????????] %gold%${s.name} %white%: %green%$st"))
            }
        }
        else if (c.name.equals("stp", ignoreCase = true)) {
            if (a.isNotEmpty()) {
                if (team[(s as Player).uniqueId] == 1 && s.gameMode == GameMode.SPECTATOR) {
                    if (Bukkit.getPlayer(a[0])?.gameMode == GameMode.SURVIVAL) {
                        Bukkit.getPlayer(a[0])?.let { s.teleport(it) }
                    }
                } else if (team[s.uniqueId] == 2 && s.gameMode == GameMode.SPECTATOR) {
                    if (Bukkit.getPlayer(a[0])?.gameMode == GameMode.SURVIVAL) {
                        Bukkit.getPlayer(a[0])?.let { s.teleport(it) }
                    }
                } else if (s.name in managers) {
                    if (Bukkit.getPlayer(a[0])?.gameMode == GameMode.SURVIVAL) {
                        Bukkit.getPlayer(a[0])?.let { s.teleport(it) }
                    }
                }
            }
        }

        return false
    }

    private fun repeat(delay: Long = 1, task: () -> Unit) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, 0, delay)
    }
    fun delay(delay: Long = 1, task: () -> Unit) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, delay)
    }
    fun respawn(p: Player, time: Int, task: () -> Unit) {
        p.gameMode = GameMode.SPECTATOR
        for (i in 0 until time) {
            delay((i*20).toLong()) {
                p.sendTitle(text("%red%???????????????"), text("%gray%${time - i}??? ??? ??????"), 0, 21, 0)
            }
        }
        delay((time*20).toLong()) {
            p.gameMode = GameMode.SURVIVAL
            task.invoke()
        }
    }

}

var redEmerald = 0
var blueEmerald = 0
val team by lazy { HashMap<UUID, Int>() }
val charging by lazy { HashMap<UUID, Boolean>() }
val compass by lazy { HashMap<Player, Boolean>() }
var bossBar = Bukkit.createBossBar("[?????? ??????] 20 : 0", BarColor.YELLOW, BarStyle.SOLID)
val o2time by lazy { HashMap<UUID, BossBar>() }
var time = 0
lateinit var plugin: Main

val teamColor = HashMap<Int, String>().apply {
    this[0] = "${ChatColor.GRAY}"
    this[1] = "${ChatColor.RED}"
    this[2] = "${ChatColor.AQUA}"
}

var move = true
var interact = true
var damage = true

var wl = false
var lines = listOf("Uziuukky", "pianominecraft")

var managers = listOf("")

var blueKing: UUID? = null
var blueKingLive = true
var redKing: UUID? = null
var redKingLive = true
val leaders = ArrayList<UUID>()

fun teamInit() {
    Bukkit.getOnlinePlayers().forEach {
        if (it.name !in managers) {
            if (team[it.uniqueId] == 1) {
                if (it.uniqueId == redKing) {
                    Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("DarkRed")?.addEntry(it.name) ?: Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("DarkRed").apply { color = ChatColor.DARK_RED }
                } else {
                    Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("Red")?.addEntry(it.name) ?: Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("Red").apply { color = ChatColor.RED }
                }
            } else if (team[it.uniqueId] == 2) {
                if (it.uniqueId == blueKing) {
                    Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("DarkBlue")?.addEntry(it.name) ?: Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("DarkBlue").apply { color = ChatColor.DARK_BLUE }
                } else {
                    Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("Blue")?.addEntry(it.name) ?: Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("Blue").apply { color = ChatColor.BLUE }
                }
            }
        } else {
            Bukkit.getScoreboardManager()!!.mainScoreboard.getTeam("Gray")?.addEntry(it.name) ?: Bukkit.getScoreboardManager()!!.mainScoreboard.registerNewTeam("Gray").apply { color = ChatColor.GRAY }
        }
    }
    plugin.fileRefresh()
}

fun text(string: String) : String {
    var s = string
    val rgb = Pattern.compile("#[0-9a-f]{6}").matcher(string)
    while (rgb.find()) {
        try {
            s = s.replaceFirst(rgb.group(), net.md_5.bungee.api.ChatColor.of(rgb.group()).toString())
        } catch (e: Exception) {
        }
    }
    val color = Pattern.compile("%[a-zA-Z_]*%").matcher(string)
    while (color.find()) {
        try {
            s = s.replaceFirst(
                color.group(),
                net.md_5.bungee.api.ChatColor.of(color.group().replace("%", "")).toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return s
}