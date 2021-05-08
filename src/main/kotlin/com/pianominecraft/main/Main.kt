package com.pianominecraft.main

import com.pianominecraft.main.Constant.blueKing
import com.pianominecraft.main.Constant.blueKingLive
import com.pianominecraft.main.Constant.blueLeap
import com.pianominecraft.main.Constant.bossBar
import com.pianominecraft.main.Constant.compass
import com.pianominecraft.main.Constant.coolTime
import com.pianominecraft.main.Constant.interact
import com.pianominecraft.main.Constant.leaders
import com.pianominecraft.main.Constant.lines
import com.pianominecraft.main.Constant.move
import com.pianominecraft.main.Constant.plugin
import com.pianominecraft.main.Constant.redKing
import com.pianominecraft.main.Constant.redKingLive
import com.pianominecraft.main.Constant.redLeap
import com.pianominecraft.main.Constant.team
import com.pianominecraft.main.Constant.teamColor
import com.pianominecraft.main.Constant.teamInit
import com.pianominecraft.main.Constant.time
import com.pianominecraft.main.Constant.wl
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class Main : JavaPlugin() {

    private val cfgFile = File(dataFolder, "config.txt")
    private val wlFile = File(dataFolder, "whitelist.txt")
    private val kingFile = File(dataFolder, "king.txt")
    private val leaderFile = File(dataFolder, "leaders.txt")

    override fun onEnable() {

        plugin = this

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
            cfgFile.createNewFile()
        }
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
        }
        if (!wlFile.exists()) {
            wlFile.createNewFile()
            PrintWriter(wlFile).use {
                it.println("*")
            }
        }
        if (!kingFile.exists()) {
            kingFile.createNewFile()
            PrintWriter(kingFile).use {
                it.println("Uziuukky")
                it.println("OnlyJunyoung")
            }
        }
        if (!leaderFile.exists()) {
            leaderFile.createNewFile()
        }
        try {
            BufferedReader(FileReader(kingFile)).use {
                val lines = it.readLines()
                if (lines.size >= 2) {
                    redKing = Bukkit.getOfflinePlayer(lines[0]).uniqueId
                    blueKing = Bukkit.getOfflinePlayer(lines[1]).uniqueId
                }
            }
        } catch (e: Exception) {
        }
        try {
            BufferedReader(FileReader(leaderFile)).use {
                it.readLines().forEach { line ->
                    leaders.add(Bukkit.getOfflinePlayer(line).uniqueId)
                }
            }
        } catch (e: Exception) {
        }
        wl = BufferedReader(FileReader(wlFile)).use { it.readLine() } != "*"
        if (wl) BufferedReader(FileReader(wlFile)).use {
            lines = it.readLines()
        }
        Bukkit.getOnlinePlayers().forEach {
            coolTime[it.uniqueId] = 0
            compass[it] = true
        }
        repeat(20) {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (p.health > 0) {
                    if (p.health < p.maxHealth - (p.maxHealth / 20)) {
                        p.health = p.health + (p.maxHealth / 20)
                    } else {
                        p.health = p.maxHealth
                    }
                }
            }
        }
        repeat {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (coolTime[p.uniqueId]!! > 0) {
                    coolTime[p.uniqueId] = coolTime[p.uniqueId]?.minus(1)!!
                }
            }
            if (redLeap > 0) {
                redLeap--
            }
            if (blueLeap > 0) {
                blueLeap--
            }
        }
        repeat {
            Bukkit.getOnlinePlayers().forEach { p ->
                if (compass[p] == true) {
                    if (team[p.uniqueId] == 1) {
                        blueKing?.let { k ->
                            try {
                                p.compassTarget = Bukkit.getPlayer(k)?.location!!
                                if (p.itemInHand.type == Material.COMPASS)
                                    if (p.location.distance(Bukkit.getPlayer(k)?.location!!) > 50) {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.BLUE}${ChatColor.BOLD}블루${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GOLD}${ChatColor.BOLD}추적${ChatColor.WHITE}${ChatColor.BOLD}중"))
                                    } else {
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.BLUE}${ChatColor.BOLD}블루${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GREEN}${ChatColor.BOLD}발견"))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}${ChatColor.BOLD}레드${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GOLD}${ChatColor.BOLD}추적${ChatColor.WHITE}${ChatColor.BOLD}중"))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}${ChatColor.BOLD}레드${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GREEN}${ChatColor.BOLD}발견"))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}${ChatColor.BOLD}레드${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GOLD}${ChatColor.BOLD}추적${ChatColor.WHITE}${ChatColor.BOLD}중"))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.RED}${ChatColor.BOLD}레드${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GREEN}${ChatColor.BOLD}발견"))
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
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.BLUE}${ChatColor.BOLD}블루${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GOLD}${ChatColor.BOLD}추적${ChatColor.WHITE}${ChatColor.BOLD}중"))
                                } else {
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("${ChatColor.BLUE}${ChatColor.BOLD}블루${ChatColor.WHITE}${ChatColor.BOLD}팀의 왕 ${ChatColor.GREEN}${ChatColor.BOLD}발견"))
                                }
                        }
                    }
                }
            }
        }
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
        }
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
        }
        Bukkit.getPluginManager().registerEvents(EventManager(), this)
        Bukkit.getLogger().info("[UziuukkyPlugin] has been enabled!")
    }

    override fun onDisable() {
        bossBar.isVisible = false
        fileRefresh()
        PrintWriter(kingFile).use {
            it.println(redKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
            it.println(blueKing?.let { k -> Bukkit.getOfflinePlayer(k).name })
        }
        PrintWriter(leaderFile).use {
            leaders.forEach { l ->
                it.println(Bukkit.getOfflinePlayer(l).name)
            }
        }
        Bukkit.getLogger().info("[UziuukkyPlugin] has been disabled!")
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
        }
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
                                it.playSound(it.location, Sound.UI_TOAST_IN, 1.0f, 1.0f)
                                it.sendTitle("${ChatColor.GOLD}게임 시작", "${ChatColor.GRAY}20분간 최대한 많은 자원을 얻으세요!", 20, 60, 20)
                                it.sendMessage("${ChatColor.GOLD}게임 시작")
                                it.sendMessage("${ChatColor.GRAY}20분간 최대한 많은 자원을 얻으세요!")
                            }
                            bossBar.isVisible = true
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
                        }
                    }
                    a[0].equals("stop", ignoreCase = true) -> {
                        if (s.isOp) time = 0
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
                        }
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
                                    s.sendMessage("${ChatColor.GREEN}${a[1]}님의 팀을 강제로 설정했습니다")
                                    Bukkit.getPlayer(a[1])?.sendMessage("${ChatColor.GREEN}관리자가 당신의 팀을 강제로 변경했습니다")
                                }
                            }
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
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
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                move = false
                                                return true
                                            }
                                        }
                                        s.sendMessage("Move : $move")
                                    }
                                    a[1].equals("interact", ignoreCase = true) -> {
                                        if (a.size > 2) {
                                            if (a[2].equals("true", ignoreCase = true)) {
                                                interact = true
                                                return true
                                            } else if (a[2].equals("false", ignoreCase = true)) {
                                                interact = false
                                                return true
                                            }
                                        }
                                        s.sendMessage("Interact : $interact")
                                    }
                                }
                            }
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
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
                                    s.sendMessage("${teamColor[1]}${a[1]}${ChatColor.GREEN}(을)를 레드 팀의 왕으로 설정했습니다")
                                    Bukkit.getPlayer(a[1])?.sendMessage("${ChatColor.GREEN}당신이 레드 팀의 왕으로 설정됐습니다")
                                } else if (team[p.uniqueId] == 2) {
                                    blueKing = p.uniqueId
                                    s.sendMessage("${teamColor[2]}${a[1]}${ChatColor.GREEN}(을)를 블루 팀의 왕으로 설정했습니다")
                                    Bukkit.getPlayer(a[1])?.sendMessage("${ChatColor.GREEN}당신이 블루 팀의 왕으로 설정됐습니다")
                                }
                                teamInit()
                            }
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
                        }
                    }
                    a[0].equals("leader", ignoreCase = true) -> {
                        if (s.isOp) {
                            if (a.size > 1) {
                                if (Bukkit.getOfflinePlayer(a[1]).uniqueId !in leaders) {
                                    leaders.add(Bukkit.getOfflinePlayer(a[1]).uniqueId)
                                    s.sendMessage("${teamColor[1]}${a[1]}${ChatColor.GREEN}(을)를 간부 리스트에 추가했습니다")
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage("${ChatColor.GREEN}당신이 간부 리스트에 추가됐습니다")
                                } else {
                                    leaders.remove(Bukkit.getOfflinePlayer(a[1]).uniqueId)
                                    s.sendMessage("${teamColor[1]}${a[1]}${ChatColor.GREEN}(을)를 간부 리스트에서 삭제했습니다")
                                    Bukkit.getPlayer(a[1])
                                        ?.sendMessage("${ChatColor.GREEN}당신이 간부 리스트에서 삭제됐습니다")
                                }
                                teamInit()
                            }
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
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
                                    s.sendMessage("${ChatColor.GREEN}$it")
                                }
                            }
                            pw.close()
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
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
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
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
                        }
                        else {
                            s.sendMessage("${ChatColor.RED}이 명령어는 개발자 pianominecraft만을 위한 명령어로 오직 디버깅을 위해서만 사용되는 명령어입니다")
                        }
                    }
                    a[0].equals("help", ignoreCase = true) -> {
                        s.sendMessage("${ChatColor.GOLD}/tb start ${ChatColor.RESET}: ${ChatColor.GREEN}게임이 시작되며 무적 시간이 활성화됩니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb stop ${ChatColor.RESET}: ${ChatColor.GREEN}게임을 강제로 중지하여 무적 시간을 해제합니다 [왠만하면 사용하지 말 것](OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb team [player(nickname)] [int(1=red,2=blue)] ${ChatColor.RESET}: ${ChatColor.GREEN}특정 플레이어의 팀을 강제로 옮깁니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb manage [event(interact,damage,move)] ${ChatColor.RESET}: ${ChatColor.GREEN}특정 이벤트의 현재 금지 여부를 확인합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb manage [event(interact,damage,move)] [boolean(true,false)] ${ChatColor.RESET}: ${ChatColor.GREEN}특정 이벤트를 금지합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/s ${ChatColor.RESET}: ${ChatColor.GREEN}확성기로 모든 플레이어에게 메세지를 전달합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb king [player(nickname)] ${ChatColor.RESET}: ${ChatColor.GREEN}특정 플레이어를 그 팀의 왕으로 임명합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb leader [player(nickname)] ${ChatColor.RESET}: ${ChatColor.GREEN}특정 플레이어를 간부 리스트에 추가합니다 만약 이미 추가돼있으면 삭제됩니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb whitelist ${ChatColor.RESET}: ${ChatColor.GREEN}현재 접속된 모든 플레이어를 화이트 리스트에 추가하고 화이트 리스트를 활성화합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb rule ${ChatColor.RESET}: ${ChatColor.GREEN}\"세력전쟁\" 컨텐츠의 게임 플레이 방법을 확인합니다")
                        s.sendMessage("${ChatColor.GOLD}/tb reload ${ChatColor.RESET}: ${ChatColor.GREEN}화이트 리스트를 파일에 접근하여 직접 만졌을 경우 화이르 리스트만 따로 리로드합니다(OP)")
                        s.sendMessage("${ChatColor.GOLD}/tb help ${ChatColor.RESET}: ${ChatColor.GREEN}현재 이 도움말을 표시합니다")
                        s.sendMessage("${ChatColor.GOLD}/tb cooltime ${ChatColor.RESET}: ${ChatColor.GREEN}쿨타임을 초기화합니다(OP)(디버그용)")
                        s.sendMessage("${ChatColor.GOLD}/tb world ${ChatColor.RESET}: ${ChatColor.GREEN}월드의 게임 룰을 초기화합니다(OP)")
                    }
                    a[0].equals("coolTime", ignoreCase = true) -> {
                        if (s.isOp) {
//                          s.sendMessage("${ChatColor.RED}디버깅 끝났어 얘야 ^^")
//                          return
                            coolTime[(s as Player).uniqueId] = 0
                            redLeap = 0
                            blueLeap = 0
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
                        }
                    }
                    a[0].equals("world", ignoreCase = true) -> {
                        if (s.isOp) {
                            val world = Bukkit.getWorld("world")!!
                            world.worldBorder.center = Location(world, 0.0, 0.0, 0.0)
                            world.worldBorder.size = 1000.0
                            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
                            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
                            world.setGameRule(GameRule.NATURAL_REGENERATION, false)
                            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false)
                            world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false)
                            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false)
                        }
                        else {
                            if (s is Player) {
                                s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                                delay(0) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(5) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(10) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(15) {
                                    s.sendTitle(
                                        "${ChatColor.GREEN}크리스탈 LLLL",
                                        "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(20) {
                                    s.sendTitle(
                                        "${ChatColor.AQUA}크리스탈 LLLL",
                                        "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(25) {
                                    s.sendTitle(
                                        "${ChatColor.BLUE}크리스탈 LLLL",
                                        "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(30) {
                                    s.sendTitle(
                                        "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                                        "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(35) {
                                    s.sendTitle(
                                        "${ChatColor.RED}크리스탈 LLLL",
                                        "${ChatColor.RED}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(40) {
                                    s.sendTitle(
                                        "${ChatColor.GOLD}크리스탈 LLLL",
                                        "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                                delay(45) {
                                    s.sendTitle(
                                        "${ChatColor.YELLOW}크리스탈 LLLL",
                                        "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                                        0,
                                        10,
                                        0
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (c.name.equals("s", ignoreCase = true)) {
            if (s.isOp) {
                var st = ""
                for (element in a) {
                    st += "$element "
                }
                Bukkit.broadcastMessage("${ChatColor.GRAY}[확성기] ${ChatColor.GOLD}${s.name} ${ChatColor.RESET}: ${ChatColor.GREEN}$st")
            }
            else {
                if (s is Player) {
                    s.playSound(s.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                    delay(0) {
                        s.sendTitle(
                            "${ChatColor.RED}크리스탈 LLLL",
                            "${ChatColor.RED}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(5) {
                        s.sendTitle(
                            "${ChatColor.GOLD}크리스탈 LLLL",
                            "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(10) {
                        s.sendTitle(
                            "${ChatColor.YELLOW}크리스탈 LLLL",
                            "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(15) {
                        s.sendTitle(
                            "${ChatColor.GREEN}크리스탈 LLLL",
                            "${ChatColor.GREEN}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(20) {
                        s.sendTitle(
                            "${ChatColor.AQUA}크리스탈 LLLL",
                            "${ChatColor.AQUA}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(25) {
                        s.sendTitle(
                            "${ChatColor.BLUE}크리스탈 LLLL",
                            "${ChatColor.BLUE}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(30) {
                        s.sendTitle(
                            "${ChatColor.DARK_PURPLE}크리스탈 LLLL",
                            "${ChatColor.DARK_PURPLE}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(35) {
                        s.sendTitle(
                            "${ChatColor.RED}크리스탈 LLLL",
                            "${ChatColor.RED}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(40) {
                        s.sendTitle(
                            "${ChatColor.GOLD}크리스탈 LLLL",
                            "${ChatColor.GOLD}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
                    }
                    delay(45) {
                        s.sendTitle(
                            "${ChatColor.YELLOW}크리스탈 LLLL",
                            "${ChatColor.YELLOW}- pianominecraft (Easter Egg)",
                            0,
                            10,
                            0
                        )
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

}

object Constant {
    val team by lazy { HashMap<UUID, Int>() }
    val coolTime by lazy { HashMap<UUID, Int>() }
    var redLeap = 0
    var blueLeap = 0
    val compass by lazy { HashMap<Player, Boolean>() }
    var bossBar = Bukkit.createBossBar("[무적 시간] 20 : 0", BarColor.YELLOW, BarStyle.SOLID)
    var time = 0
    lateinit var plugin: Main

    val teamColor = HashMap<Int, String>().apply {
        this[1] = "${ChatColor.RED}"
        this[2] = "${ChatColor.AQUA}"
    }

    var move = true
    var interact = true

    var wl = false
    var lines = listOf("Uziuukky", "pianominecraft")

    var blueKing: UUID? = null
    var blueKingLive = true
    var redKing: UUID? = null
    var redKingLive = true
    val leaders = ArrayList<UUID>()

    fun teamInit() {
        Bukkit.getOnlinePlayers().forEach {
            if (team[it.uniqueId] == 1) {
                if (it.uniqueId == redKing) {
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam("DarkRed")?.addEntry(it.name)
                }
                else {
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam("Red")?.addEntry(it.name)
                }
            } else if (team[it.uniqueId] == 2) {
                if (it.uniqueId == blueKing) {
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam("DarkBlue")?.addEntry(it.name)
                }
                else {
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam("Blue")?.addEntry(it.name)
                }
            }
        }
        plugin.fileRefresh()
    }

}