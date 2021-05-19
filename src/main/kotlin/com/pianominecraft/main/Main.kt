package com.pianominecraft.teambattle

import com.destroystokyo.paper.Namespaced
import com.pianominecraft.teambattle.Constant.blueEmerald
import com.pianominecraft.teambattle.Constant.blueKing
import com.pianominecraft.teambattle.Constant.blueKingLive
import com.pianominecraft.teambattle.Constant.blueGold
import com.pianominecraft.teambattle.Constant.bossBar
import com.pianominecraft.teambattle.Constant.charging
import com.pianominecraft.teambattle.Constant.combat
import com.pianominecraft.teambattle.Constant.compass
import com.pianominecraft.teambattle.Constant.damage
import com.pianominecraft.teambattle.Constant.diamond
import com.pianominecraft.teambattle.Constant.interact
import com.pianominecraft.teambattle.Constant.leaders
import com.pianominecraft.teambattle.Constant.lines
import com.pianominecraft.teambattle.Constant.managers
import com.pianominecraft.teambattle.Constant.move
import com.pianominecraft.teambattle.Constant.o2
import com.pianominecraft.teambattle.Constant.o2time
import com.pianominecraft.teambattle.Constant.plugin
import com.pianominecraft.teambattle.Constant.redEmerald
import com.pianominecraft.teambattle.Constant.redKing
import com.pianominecraft.teambattle.Constant.redKingLive
import com.pianominecraft.teambattle.Constant.redGold
import com.pianominecraft.teambattle.Constant.team
import com.pianominecraft.teambattle.Constant.teamColor
import com.pianominecraft.teambattle.Constant.teamInit
import com.pianominecraft.teambattle.Constant.text
import com.pianominecraft.teambattle.Constant.time
import com.pianominecraft.teambattle.Constant.wl
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
        Bukkit.getOnlinePlayers().forEach {
            diamond[it.uniqueId] = 0
            compass[it] = true
            combat[it.uniqueId] = 0
            o2[it.uniqueId] = 9000
        } // player init
        repeat(20) {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (p.health > 0) {
                    if (combat[p.uniqueId]!! < 140) {
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
            Bukkit.getOnlinePlayers().forEach { p ->
                if (diamond[p.uniqueId]!! > 0) {
                    diamond[p.uniqueId] = diamond[p.uniqueId]?.minus(1)!!
                }
                if (combat[p.uniqueId]!! > 0) {
                    combat[p.uniqueId] = combat[p.uniqueId]?.minus(1)!!
                }
            }
            if (redGold > 0) {
                redGold--
            }
            if (redEmerald > 0) {
                redEmerald--
            }
            if (blueGold > 0) {
                blueGold--
            }
            if (blueEmerald > 0) {
                blueEmerald--
            }
        } // coolTime
        repeat {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (compass[p] == true) {
                    if (team[p.uniqueId] == 1) {
                        blueKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                                if (p.itemInHand.type == Material.COMPASS)
                                    if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%블루%white%%bold%팀의 왕 %gold%%bold%추적%white%%bold%중")))
                                    } else {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%블루%white%%bold%팀의 왕 %green%%bold%발견")))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%레드%white%%bold%팀의 왕 %gold%%bold%추적%white%%bold%중")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%레드%white%%bold%팀의 왕 %green%%bold%발견")))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%레드%white%%bold%팀의 왕 %gold%%bold%추적%white%%bold%중")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%레드%white%%bold%팀의 왕 %green%%bold%발견")))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%블루%white%%bold%팀의 왕 %gold%%bold%추적%white%%bold%중")))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%blue%%bold%블루%white%%bold%팀의 왕 %green%%bold%발견")))
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
                bossBar = Bukkit.createBossBar("[무적 시간] ${time / 1200} : ${time % 1200 / 20}", BarColor.YELLOW, BarStyle.SOLID)
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
                        if (o2[p.uniqueId]!! > 0) {
                            o2[p.uniqueId] = o2[p.uniqueId]?.minus(1)!!
                            if (o2[p.uniqueId]!! % 1200 >= 10) {
                                o2time[p.uniqueId] = Bukkit.createBossBar(
                                    text("%red%%bold%현재 남은 산소 ${o2[p.uniqueId]!! / 1200} %white%%bold%: %red%%bold%${o2[p.uniqueId]!! % 1200 / 20}"),
                                    BarColor.RED,
                                    BarStyle.SOLID
                                )
                                with (o2time[p.uniqueId]!!) {
                                    progress = o2[p.uniqueId]!! / 9000.0
                                    addPlayer(p)
                                    isVisible = true
                                }

                            } else {
                                o2time[p.uniqueId] = Bukkit.createBossBar(
                                    text("%red%%bold%현재 남은 산소 ${o2[p.uniqueId]!! / 1200} %white%%bold%: %red%%bold%0${o2[p.uniqueId]!! % 1200 / 20}"),
                                    BarColor.RED,
                                    BarStyle.SOLID
                                )
                                with (o2time[p.uniqueId]!!) {
                                    progress = o2[p.uniqueId]!! / 9000.0
                                    addPlayer(p)
                                    isVisible = true
                                }
                            }
                            if (o2[p.uniqueId] == 600) {
                                p.sendTitle(text("%dark_red%위험! 산소 부족"), text("%red%산소를 마시거나 오버월드로 이동하세요"), 0, 20, 0)
                                p.playSound(p.location, Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f)
                            }
                        }
                    }
                } else {
                    if (o2[p.uniqueId]!! < 9000) {
                        if (o2[p.uniqueId]!! % 1200 >= 10) {
                            o2time[p.uniqueId] = Bukkit.createBossBar(
                                text("%green%%bold%산소 회복중... %aqua%%bold%현재 산소 ${o2[p.uniqueId]!! / 1200} %white%%bold%: %aqua%%bold%${o2[p.uniqueId]!! % 1200 / 20}"),
                                BarColor.RED,
                                BarStyle.SOLID
                            )
                            with (o2time[p.uniqueId]!!) {
                                progress = o2[p.uniqueId]!! / 9000.0
                                addPlayer(p)
                                isVisible = true
                            }
                        } else {
                            o2time[p.uniqueId] = Bukkit.createBossBar(
                                text("%green%%bold%산소 회복중... %aqua%%bold%현재 산소 ${o2[p.uniqueId]!! / 1200} %white%%bold%: %aqua%%bold%0${o2[p.uniqueId]!! % 1200 / 20}"),
                                BarColor.RED,
                                BarStyle.SOLID
                            )
                            with (o2time[p.uniqueId]!!) {
                                progress = o2[p.uniqueId]!! / 9000.0
                                addPlayer(p)
                                isVisible = true
                            }
                        }
                        o2[p.uniqueId] = o2[p.uniqueId]?.plus(1)!!
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
                if (o2[p.uniqueId]!! == 0) {
                    p.damage(5.0)
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%dark_red%%bold%산소 부족!")))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 마그마 크림"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 네더 벽돌"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 흑요석"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 블레이즈 막대"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 흑요석"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }))
                    setIngredient('N', RecipeChoice.ExactChoice(ItemStack(Material.NETHER_BRICK).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 네더 벽돌"))
                            addItemFlags(ItemFlag.HIDE_ENCHANTS)
                            addEnchant(Enchantment.MENDING, 1, false)
                        }
                    }))
                    setIngredient('B', RecipeChoice.ExactChoice(ItemStack(Material.BLAZE_ROD).apply {
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%aqua%압축된 블레이즈 막대"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                        itemMeta = itemMeta.apply {
                            setDisplayName(text("%gold%산소통"))
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
                            bossBar = Bukkit.createBossBar("[무적 시간] 20 : 00", BarColor.YELLOW, BarStyle.SOLID)
                            Bukkit.getOnlinePlayers().forEach {
                                bossBar.addPlayer(it)
                                it.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 24000, 127, false, false))
                                Bukkit.getWorld("world")?.difficulty = Difficulty.PEACEFUL
                                it.playSound(it.location, Sound.UI_TOAST_IN, 1f, 1f)
                                it.sendTitle(text("%gold%게임 시작"), text("%gray%20분간 최대한 많은 자원을 얻으세요!"), 20, 60, 20)
                                it.sendMessage(text("%gold%게임 시작"))
                                it.sendMessage(text("%gray%20분간 최대한 많은 자원을 얻으세요!"))
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
                                    s.sendMessage(text("%green%${a[1]}님의 팀을 강제로 설정했습니다"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%관리자가 당신의 팀을 강제로 변경했습니다"))
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
                                                s.sendMessage(text("%green%움직이기를 허용했습니다"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                move = false
                                                s.sendMessage(text("%red%움직이기를 금지했습니다"))
                                                return true
                                            }
                                        }
                                        s.sendMessage("Move : $move")
                                    }
                                    a[1].equals("interact", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                interact = true
                                                s.sendMessage(text("%green%상호작용을 허용했습니다"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                interact = false
                                                s.sendMessage(text("%red%상호작용을 금지했습니다"))
                                                return true
                                            }
                                        }
                                        s.sendMessage("Interact : $interact")
                                    }
                                    a[1].equals("damage", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                damage = true
                                                s.sendMessage(text("%green%데미지 입기를 허용했습니다"))
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                damage = false
                                                s.sendMessage(text("%red%데미지 입기를 금지했습니다"))
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
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(을)를 레드 팀의 왕으로 설정했습니다"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%당신이 레드 팀의 왕으로 설정됐습니다"))
                                } else if (team[p.uniqueId] == 2) {
                                    blueKing = p.uniqueId
                                    s.sendMessage(text("${teamColor[2]}${a[1]}%green%(을)를 블루 팀의 왕으로 설정했습니다"))
                                    Bukkit.getPlayer(a[1])?.sendMessage(text("%green%당신이 블루 팀의 왕으로 설정됐습니다"))
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
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(을)를 간부 리스트에 추가했습니다"))
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage(text("%green%당신이 간부 리스트에 추가됐습니다"))
                                } else {
                                    leaders.remove(Bukkit.getOfflinePlayer(a[1]).uniqueId)
                                    s.sendMessage(text("${teamColor[1]}${a[1]}%green%(을)를 간부 리스트에서 삭제했습니다"))
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage(text("%green%당신이 간부 리스트에서 삭제됐습니다"))
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
                            s.sendMessage("규칙")
                            s.sendMessage("- 욕 하지 마세요 ( 욕하는 사람 있으면 스샷 찍어서 개인 디스코드로 보내주세요 )")
                            s.sendMessage("- 최대한 자기 팀에 최선을 다하세요 ( 나한테 잘 보여봤자 죽일겁니다 )")
                            s.sendMessage("- 머리에서 뇌를 빼놓고 행동하지마세요 ( 용암을 붓는다던가 하는 트롤짓은 칼밴입니다 )")
                            s.sendMessage("명")
                            s.sendMessage("- 세력과 세력간의 싸움입니다.")
                            s.sendMessage("- 왕, 간부, 병사로 나누어져있습니다")
                            s.sendMessage("- 왕과 간부는 발광으로 강조됩니다")
                            s.sendMessage(")        - 왕은 한 명, 간부는 5명입니다. 팀은 총 레드와 블루, 두 개로 이루어져 있습니다.")
                            s.sendMessage("- 처음 시작했을 떄 20분의 무적시간(파밍시간)이 주어집니다.")
                            s.sendMessage("- 나침반을 만들어서 손에 들게 되면, 각 팀의 왕의 위치를 확인할 수 있습니다. (우클릭으로 변경)")
                            s.sendMessage("- 왕은 다이아를 하나 소모해서(우클릭) 병사들을 최대 5명까지 소환할 수 있습니다.")
                            s.sendMessage("타임은 3분이며, 여기서 병사들은 쉬프트로 거부할 수 있습니다.")
                            s.sendMessage("- 왕은 금괴를 하나 소모해서(우클릭) 도약을 할 수 있습니다. 쿨타임은 1분입니다.")
                            s.sendMessage("- 왕은 자연 데미지 ( 용암데미지, 익사데미지, 낙사데미지 등 등)을 받지 않습니다.")
                            s.sendMessage("- 왕의 체력은 하트 20칸(40HP), 간부의 체력은 하트 15칸(30HP),")
                            s.sendMessage("사의 체력은 하트 10칸(20HP)")
                            s.sendMessage("- 간부는 5초의 리스폰 시간과, 병사는 10초의 리스폰 시간을 갖습니다.")
                            s.sendMessage("- 죽었을 경우 왕의 위치에서 리스폰하게 됩니다.")
                            s.sendMessage("- 간부는 다이아를 하나 소모해서(우클릭) 왕에게 지원을 갈 수 있습니다.")
                            s.sendMessage("초의 시간이 걸리며, 쿨타임은 2분 30초입니다.")
                            s.sendMessage("- 왕과 간부는 배고픔이 닳지 않습니다.")
                            s.sendMessage("- 왕이 사망하면 그 팀은 리스폰을 할 수 없게 되며, 전멸할경우 패배하게 됩니다.")
                            s.sendMessage("- 여러 전략과 협동심으로 자신의 팀을 승리로 이끌어 가세요!")
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
                            s.sendMessage("${ChatColor.RED}이 명령어는 개발자 pianominecraft만을 위한 명령어로 오직 디버깅을 위해서만 사용되는 명령어입니다")
                        }
                    }
                    a[0].equals("help", ignoreCase = true) -> {
                        s.sendMessage(text("%gold%/tb start %white%: %green%게임이 시작되며 무적 시간이 활성화됩니다(OP)"))
                        s.sendMessage(text("%gold%/tb stop %white%: %green%게임을 강제로 중지하여 무적 시간을 해제합니다 [왠만하면 사용하지 말 것](OP)"))
                        s.sendMessage(text("%gold%/tb team [player(nickname)] [int(1=red,2=blue)] %white%: %green%특정 플레이어의 팀을 강제로 옮깁니다(OP)"))
                        s.sendMessage(text("%gold%/tb manage [event(interact,damage,move)] %white%: %green%특정 이벤트의 현재 금지 여부를 확인합니다(OP)"))
                        s.sendMessage(text("%gold%/tb manage [event(interact,damage,move)] [boolean(true,false)] %white%: %green%특정 이벤트를 금지합니다(OP)"))
                        s.sendMessage(text("%gold%/s %white%: %green%확성기로 모든 플레이어에게 메세지를 전달합니다(OP, Manager)"))
                        s.sendMessage(text("%gold%/tb king [player(nickname)] %white%: %green%특정 플레이어를 그 팀의 왕으로 임명합니다(OP)"))
                        s.sendMessage(text("%gold%/tb leader [player(nickname)] %white%: %green%특정 플레이어를 간부 리스트에 추가합니다 만약 이미 추가돼있으면 삭제됩니다(OP)"))
                        s.sendMessage(text("%gold%/tb whitelist %white%: %green%현재 접속된 모든 플레이어를 화이트 리스트에 추가하고 화이트 리스트를 활성화합니다(OP)"))
                        s.sendMessage(text("%gold%/tb rule %white%: %green%\"세력전쟁\" 컨텐츠의 게임 플레이 방법을 확인합니다"))
                        s.sendMessage(text("%gold%/tb reload %white%: %green%화이트 리스트를 파일에 접근하여 직접 만졌을 경우 화이르 리스트만 따로 리로드합니다(OP)"))
                        s.sendMessage(text("%gold%/tb help %white%: %green%현재 이 도움말을 표시합니다"))
                        s.sendMessage(text("%gold%/tb cooltime %white%: %green%쿨타임을 초기화합니다(OP)(디버그용)"))
                        s.sendMessage(text("%gold%/tb world %white%: %green%월드의 게임 룰을 초기화합니다(OP)"))
                        s.sendMessage(text("%gold%/stp [player(nickname)] %white%: %green%플레이어에게 텔레포트 합니다(OP, Manager, Spectator)"))
                    }
                    a[0].equals("coolTime", ignoreCase = true) -> {
                        if (s.isOp) {
                            diamond[(s as Player).uniqueId] = 0
                            redGold = 0
                            redEmerald = 0
                            blueGold = 0
                            blueEmerald = 0
                        }
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
                Bukkit.broadcastMessage(text("%gray%[확성기] %gold%${s.name} %white%: %green%$st"))
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
                p.sendTitle(text("%red%죽었습니다"), text("%gray%${time - i}초 뒤 부활"), 0, 21, 0)
            }
        }
        delay((time*20).toLong()) {
            p.gameMode = GameMode.SURVIVAL
            task.invoke()
        }
    }

}

object Constant {
    val team by lazy { HashMap<UUID, Int>() }
    val diamond by lazy { HashMap<UUID, Int>() }
    val combat by lazy { HashMap<UUID, Int>() }
    val o2 by lazy { HashMap<UUID, Int>() }
    val charging by lazy { HashMap<UUID, Boolean>() }
    var redEmerald = 0
    var blueEmerald = 0
    var redGold = 0
    var blueGold = 0
    val compass by lazy { HashMap<Player, Boolean>() }
    var bossBar = Bukkit.createBossBar("[무적 시간] 20 : 0", BarColor.YELLOW, BarStyle.SOLID)
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
                        Bukkit.getScoreboardManager().mainScoreboard.getTeam("DarkRed")?.addEntry(it.name)
                    } else {
                        Bukkit.getScoreboardManager().mainScoreboard.getTeam("Red")?.addEntry(it.name)
                    }
                } else if (team[it.uniqueId] == 2) {
                    if (it.uniqueId == blueKing) {
                        Bukkit.getScoreboardManager().mainScoreboard.getTeam("DarkBlue")?.addEntry(it.name)
                    } else {
                        Bukkit.getScoreboardManager().mainScoreboard.getTeam("Blue")?.addEntry(it.name)
                    }
                }
            } else {
                Bukkit.getScoreboardManager().mainScoreboard.getTeam("Gray")?.addEntry(it.name)
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

}