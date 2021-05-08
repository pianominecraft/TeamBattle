package com.pianominecraft.main

import com.pianominecraft.main.Constant.blueKing
import com.pianominecraft.main.Constant.blueKingLive
import com.pianominecraft.main.Constant.blueLeap
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
import com.pianominecraft.main.Constant.wl
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

@Suppress("DEPRECATION")
class EventManager : Listener {

    private var first = true

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity.uniqueId == blueKing || e.entity.uniqueId == redKing) {
            if (e.cause in listOf(EntityDamageEvent.DamageCause.FALL,
                    EntityDamageEvent.DamageCause.DROWNING,
                    EntityDamageEvent.DamageCause.FIRE,
                    EntityDamageEvent.DamageCause.LAVA,
                    EntityDamageEvent.DamageCause.LIGHTNING,
                    EntityDamageEvent.DamageCause.FALLING_BLOCK,
                    EntityDamageEvent.DamageCause.FIRE_TICK,
                    EntityDamageEvent.DamageCause.SUFFOCATION,
                    EntityDamageEvent.DamageCause.HOT_FLOOR,
                    EntityDamageEvent.DamageCause.CONTACT)) {
                e.isCancelled = true
            }
        }
        if (Constant.time > 0) {
            if (e.entity is Player) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.isCancelled = true
        Bukkit.getConsoleSender().sendMessage("[Chat] ${e.player.name} : ${e.message}")
        Bukkit.getOnlinePlayers().forEach {
            if (team[e.player.uniqueId] == team[it.uniqueId]) {
                if (e.player.uniqueId == redKing) {
                    it.sendMessage("${ChatColor.YELLOW}[Team Chating] ${ChatColor.RESET}<${ChatColor.DARK_RED}${e.player.name}${ChatColor.RESET}> ${e.message}")
                } else if (e.player.uniqueId == blueKing) {
                    it.sendMessage("${ChatColor.YELLOW}[Team Chating] ${ChatColor.RESET}<${ChatColor.BLUE}${e.player.name}${ChatColor.RESET}> ${e.message}")
                } else if (e.player.uniqueId in leaders) {
                    if (team[e.player.uniqueId] == 1) {
                        it.sendMessage("${ChatColor.YELLOW}[Team Chating] ${ChatColor.RESET}<${net.md_5.bungee.api.ChatColor.of("#DD3333")}${e.player.name}${ChatColor.RESET}> ${e.message}")
                    } else if (team[e.player.uniqueId] == 2) {
                        it.sendMessage("${ChatColor.YELLOW}[Team Chating] ${ChatColor.RESET}<${ChatColor.DARK_AQUA}${e.player.name}${ChatColor.RESET}> ${e.message}")
                    }
                } else {
                    it.sendMessage("${ChatColor.YELLOW}[Team Chating] ${ChatColor.RESET}<${teamColor[team[e.player.uniqueId]]}${e.player.name}${ChatColor.RESET}> ${e.message}")
                }
            }
        }
    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    @EventHandler
    fun onTeamDamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            if (e.damager is Player) {
                if (team[e.entity.uniqueId] == team[e.damager.uniqueId]) {
                    e.isCancelled = true
                }
            } else if (e.damager is Projectile) {
                if ((e.damager as Projectile).shooter is Player) {
                    val shooter = (e.damager as Projectile).shooter as Player
                    if (team[e.entity.uniqueId] == team[shooter.uniqueId]) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (wl) {
            if (e.player.name !in lines) {
                plugin.delay { e.player.kickPlayer("당신은 화이트 리스트에 등록되지 못했습니다!") }
                return
            }
        }
        if (!team.containsKey(e.player.uniqueId)) {
            if (first) {
                team[e.player.uniqueId] = 1
                teamInit()
                first = false
            } else {
                team[e.player.uniqueId] = 2
                teamInit()
                first = true
            }
            Bukkit.getConsoleSender().sendMessage("${teamColor[team[e.player.uniqueId]]}팀이 없는 사람 접속됨 : ${e.player.name}")
        } else {
            Bukkit.getConsoleSender().sendMessage("${teamColor[team[e.player.uniqueId]]}이미 팀이 있는 사람 접속됨 : ${e.player.name}")
        }
        compass[e.player] = true
        coolTime[e.player.uniqueId] = 0
    }

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        if (e.player.uniqueId == redKing) {
            e.player.gameMode = GameMode.SPECTATOR
            plugin.delay {
                Bukkit.getOnlinePlayers().forEach {
                    if (team[it.uniqueId] == 2) {
                        it.sendTitle("${ChatColor.RED}레드 팀의 왕이 죽었습니다!", "${ChatColor.GRAY}이제 모든 상대편을 죽이세요!", 20, 60, 20)
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f)
                    } else if (team[it.uniqueId] == 1) {
                        it.sendTitle("${ChatColor.RED}레드 팀의 왕이 죽었습니다!", "${ChatColor.GRAY}이제 리스폰이 불가능합니다!", 20, 60, 20)
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f)
                    }
                }
            }
            redKingLive = false
        }
        else if (e.player.uniqueId == blueKing) {
            e.player.gameMode = GameMode.SPECTATOR
            plugin.delay {
                Bukkit.getOnlinePlayers().forEach {
                    if (team[it.uniqueId] == 1) {
                        it.sendTitle(
                            "${ChatColor.BLUE}블루 팀의 왕이 죽었습니다!",
                            "${ChatColor.GRAY}이제 모든 상대편을 죽이세요!",
                            20,
                            60,
                            20
                        )
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f)
                    } else if (team[it.uniqueId] == 2) {
                        it.sendTitle(
                            "${ChatColor.BLUE}블루 팀의 왕이 죽었습니다!",
                            "${ChatColor.GRAY}이제 리스폰이 불가능합니다!",
                            20,
                            60,
                            20
                        )
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.0f)
                    }
                }
            }
            blueKingLive = false
        }
        else {
            if (team[e.player.uniqueId] == 1) {
                if (redKingLive) {
                    if (e.player.uniqueId !in leaders) {
                        e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                        e.player.gameMode = GameMode.SPECTATOR
                        e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}10초 뒤 부활", 0, 21, 0)
                        plugin.delay(20) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}9초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}8초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(60) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}7초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(80) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}6초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(100) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}5초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(120) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}4초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(140) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}3초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(160) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}2초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(180) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}1초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(200) {
                            e.player.sendTitle("${ChatColor.RED}부활!", "${ChatColor.GRAY}다시 전장에 나가 싸우세요!", 0, 60, 20)
                            e.player.gameMode = GameMode.SURVIVAL
                            e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                plugin.delay(4) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(8) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(12) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(16) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                            }
                            Bukkit.getPlayer(redKing!!)?.sendMessage("${teamColor[1]}${e.player.name}${ChatColor.RESET}이(가) 당신의 위치에 ${ChatColor.GOLD}리스폰${ChatColor.RESET}되었습니다!")
                        }
                    }
                    else {
                        e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                        e.player.gameMode = GameMode.SPECTATOR
                        e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}5초 뒤 부활", 0, 21, 0)
                        plugin.delay(20) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}4초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}3초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(60) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}2초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(80) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}1초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(100) {
                            e.player.sendTitle("${ChatColor.RED}부활!", "${ChatColor.GRAY}다시 전장에 나가 싸우세요!", 0, 60, 20)
                            e.player.gameMode = GameMode.SURVIVAL
                            e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                plugin.delay(4) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(8) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(12) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(16) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                            }
                            Bukkit.getPlayer(redKing!!)?.sendMessage("${teamColor[1]}${e.player.name}${ChatColor.RESET}이(가) 당신의 위치에 ${ChatColor.GOLD}리스폰${ChatColor.RESET}되었습니다!")
                        }
                    }
                } else {
                    e.player.gameMode = GameMode.SPECTATOR
                    e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}으악!", 20, 60, 20)
                    e.player.teleport(Location(Bukkit.getWorld("world")!!, 0.0, 150.0, 0.0))
                    Bukkit.getOnlinePlayers().forEach { p ->
                        if (team[p.uniqueId] == 1) {
                            if (p.gameMode == GameMode.SURVIVAL) return
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach { p ->
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f)
                        p.sendTitle("${ChatColor.BLUE}블루 팀 승리", "${ChatColor.DARK_BLUE}레드 팀이 전멸했습니다", 20, 60, 20)
                        p.gameMode = GameMode.SURVIVAL
                    }
                }
            }
            else if (team[e.player.uniqueId] == 2) {
                if (blueKingLive) {
                    if (e.player.uniqueId !in leaders) {
                        Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                        e.player.gameMode = GameMode.SPECTATOR
                        e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}10초 뒤 부활", 0, 21, 0)
                        plugin.delay(20) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}9초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}8초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(60) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}7초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(80) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}6초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(100) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}5초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(120) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}4초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(140) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}3초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(160) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}2초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(180) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}1초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(200) {
                            e.player.sendTitle("${ChatColor.RED}부활!", "${ChatColor.GRAY}다시 전장에 나가 싸우세요!", 0, 60, 20)
                            e.player.gameMode = GameMode.SURVIVAL
                            Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                plugin.delay(4) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(8) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(12) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(16) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                            }
                            Bukkit.getPlayer(blueKing!!)?.sendMessage("${teamColor[2]}${e.player.name}${ChatColor.RESET}이(가) 당신의 위치에 ${ChatColor.GOLD}리스폰${ChatColor.RESET}되었습니다!")
                        }
                    }
                    else {
                        Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                        e.player.gameMode = GameMode.SPECTATOR
                        e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}5초 뒤 부활", 0, 21, 0)
                        plugin.delay(20) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}4초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}3초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(60) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}2초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(80) {
                            e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}1초 뒤 부활", 0, 21, 0)
                        }
                        plugin.delay(100) {
                            e.player.sendTitle("${ChatColor.RED}부활!", "${ChatColor.GRAY}다시 전장에 나가 싸우세요!", 0, 60, 20)
                            e.player.gameMode = GameMode.SURVIVAL
                            Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                plugin.delay(4) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(8) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(12) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                plugin.delay(16) {
                                    it.spawnParticle(Particle.TOTEM, e.player.eyeLocation, 50, 0.0, 0.0, 0.0, 0.5)
                                }
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1.0f, 1.0f)
                            }
                            Bukkit.getPlayer(blueKing!!)?.sendMessage("${teamColor[2]}${e.player.name}${ChatColor.RESET}이(가) 당신의 위치에 ${ChatColor.GOLD}리스폰${ChatColor.RESET}되었습니다!")
                        }
                    }
                } else {
                    e.player.gameMode = GameMode.SPECTATOR
                    e.player.sendTitle("${ChatColor.RED}죽었습니다", "${ChatColor.GRAY}으악!", 20, 60, 20)
                    e.player.teleport(Location(Bukkit.getWorld("world")!!, 0.0, 150.0, 0.0))
                    Bukkit.getOnlinePlayers().forEach { p ->
                        if (team[p.uniqueId] == 2) {
                            if (p.gameMode == GameMode.SURVIVAL) return
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach { p ->
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f)
                        p.sendTitle("${ChatColor.RED}레드 팀 승리", "${ChatColor.DARK_RED}블루 팀이 전멸했습니다", 20, 60, 20)
                        p.gameMode = GameMode.SURVIVAL
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (!e.player.isOp) {
            e.isCancelled = !interact
            if (!interact) return
        }
        if (e.player.itemInHand.type == Material.COMPASS) {
            if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK) {
                compass[e.player] = compass[e.player] != true
            }
        }
        if (e.player.itemInHand.type == Material.DIAMOND) {
            if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
                if (coolTime[e.player.uniqueId] == 0) {
                    if (e.player.uniqueId in leaders) {
                        if (team[e.player.uniqueId] == 1) {
                            if (redKingLive) {
                                e.player.sendTitle(
                                    "남은 시간 ${ChatColor.GOLD}0 : 03",
                                    "${ChatColor.RED}${Bukkit.getOfflinePlayer(redKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                    0,
                                    20,
                                    0
                                )
                                e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                plugin.delay(20) {
                                    e.player.sendTitle(
                                        "남은 시간 ${ChatColor.GOLD}0 : 02",
                                        "${ChatColor.RED}${Bukkit.getOfflinePlayer(redKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                        0,
                                        20,
                                        0
                                    )
                                    e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                }
                                plugin.delay(40) {
                                    e.player.sendTitle(
                                        "남은 시간 ${ChatColor.GOLD}0 : 01",
                                        "${ChatColor.RED}${Bukkit.getOfflinePlayer(redKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                        0,
                                        20,
                                        0
                                    )
                                    e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                }
                                e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                                Bukkit.getPlayer(redKing!!)
                                    ?.sendMessage("${teamColor[1]}${e.player.name}${ChatColor.RESET}이(가) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 오는 중입니다...")
                                coolTime[e.player.uniqueId] = 3000
                                plugin.delay(60) {
                                    e.player.teleport(Bukkit.getPlayer(redKing!!)!!)
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                        p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                                    }
                                    e.player.sendTitle("${ChatColor.RED}도착 완료!", "${ChatColor.GRAY}왕을 지원하세요!")
                                    Bukkit.getPlayer(redKing!!)
                                        ?.sendMessage("${teamColor[1]}${e.player.name}${ChatColor.RESET}이(가) ${ChatColor.GOLD}도착 완료${ChatColor.RESET}했습니다!")
                                    Bukkit.getPlayer(redKing!!)
                                        ?.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                                }
                            } else {
                                e.player.sendMessage("${ChatColor.RED}지원하러 갈 왕이 없습니다")
                            }
                        } else if (team[e.player.uniqueId] == 2) {
                            if (blueKingLive) {
                                e.player.sendTitle(
                                    "남은 시간 ${ChatColor.GOLD}0 : 03",
                                    "${ChatColor.BLUE}${Bukkit.getOfflinePlayer(blueKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                    0,
                                    20,
                                    0
                                )
                                e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                plugin.delay(20) {
                                    e.player.sendTitle(
                                        "남은 시간 ${ChatColor.GOLD}0 : 02",
                                        "${ChatColor.BLUE}${Bukkit.getOfflinePlayer(blueKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                        0,
                                        20,
                                        0
                                    )
                                    e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                }
                                plugin.delay(40) {
                                    e.player.sendTitle(
                                        "남은 시간 ${ChatColor.GOLD}0 : 01",
                                        "${ChatColor.BLUE}${Bukkit.getOfflinePlayer(blueKing!!).name}을(를) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 가는 중입니다..",
                                        0,
                                        20,
                                        0
                                    )
                                    e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                                }
                                e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                                Bukkit.getPlayer(blueKing!!)
                                    ?.sendMessage("${teamColor[2]}${e.player.name}${ChatColor.RESET}이(가) ${ChatColor.GOLD}지원${ChatColor.RESET}하러 오는 중입니다...")
                                coolTime[e.player.uniqueId] = 3000
                                plugin.delay(60) {
                                    e.player.teleport(Bukkit.getPlayer(blueKing!!)!!)
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                        p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                                    }
                                    e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                                    e.player.sendTitle("${ChatColor.RED}도착 완료!", "${ChatColor.GRAY}왕을 지원하세요!")
                                    Bukkit.getPlayer(blueKing!!)
                                        ?.sendMessage("${teamColor[2]}${e.player.name}${ChatColor.RESET}이(가) ${ChatColor.GOLD}도착 완료${ChatColor.RESET}했습니다!")
                                    Bukkit.getPlayer(blueKing!!)
                                        ?.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                                }
                            }
                        } else {
                            e.player.sendMessage("${ChatColor.RED}지원하러 갈 왕이 없습니다")
                        }
                    } else if (e.player.uniqueId == redKing) {
                        val soldiers = ArrayList<Player>()
                        val s = ArrayList<Player>()
                        Bukkit.getOnlinePlayers().forEach {
                            if (team[it.uniqueId] == 1 && it.uniqueId != redKing && it.uniqueId !in leaders) {
                                soldiers.add(it)
                            }
                        }
                        while (s.size != 5 && s.size != soldiers.size) {
                            soldiers.random().let {
                                if (it !in s) {
                                    s.add(it)
                                }
                            }
                        }
                        s.forEach {
                            it.sendTitle(
                                "${ChatColor.GRAY}거절 시 ${ChatColor.GOLD}Shift${ChatColor.GRAY}키",
                                "${ChatColor.RED}${e.player.name}${ChatColor.RESET}님이 당신을 ${ChatColor.GOLD}소환${ChatColor.RESET}하려고 합니다",
                                0,
                                40,
                                20
                            )
                        }
                        e.player.sendTitle(
                            "남은 시간 ${ChatColor.GOLD}0 : 03",
                            "${ChatColor.RED}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                            0,
                            20,
                            0
                        )
                        e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        plugin.delay(20) {
                            e.player.sendTitle(
                                "남은 시간 ${ChatColor.GOLD}0 : 02",
                                "${ChatColor.RED}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                                0,
                                20,
                                0
                            )
                            e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle(
                                "남은 시간 ${ChatColor.GOLD}0 : 01",
                                "${ChatColor.RED}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                                0,
                                20,
                                0
                            )
                            e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        }
                        e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                        coolTime[e.player.uniqueId] = 3600
                        plugin.delay(60) {
                            Bukkit.getOnlinePlayers().forEach { p ->
                                p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                            }
                            s.forEach {
                                if (!it.isSneaking) {
                                    it.teleport(Bukkit.getPlayer(redKing!!)!!)
                                    it.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                                    it.sendTitle("${ChatColor.RED}도착 완료!", "${ChatColor.GRAY}왕을 지원하세요!")
                                    it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                                } else {
                                    e.player.sendMessage("병사 ${ChatColor.RED}${it.name}${ChatColor.RESET}이(가) 소환을 ${ChatColor.GOLD}거부${ChatColor.RESET}했습니다!")
                                    it.sendTitle("${ChatColor.RED}못 됐네요!", "${ChatColor.GRAY}왕의 집결 명령을 거부했습니다!", 0, 40, 20)
                                    it.playSound(it.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 1.0f)
                                }
                            }
                            e.player.sendMessage("${teamColor[2]}병사들을 불러내 전장에 ${ChatColor.GOLD}참가${ChatColor.RESET}시켰습니다")
                            e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                        }
                    } else if (e.player.uniqueId == blueKing) {
                        val soldiers = ArrayList<Player>()
                        val s = ArrayList<Player>()
                        Bukkit.getOnlinePlayers().forEach {
                            if (team[it.uniqueId] == 2 && it.uniqueId != blueKing && it.uniqueId !in leaders) {
                                soldiers.add(it)
                            }
                        }
                        while (s.size != 5 && s.size != soldiers.size) {
                            soldiers.random().let {
                                if (it !in s) {
                                    s.add(it)
                                }
                            }
                        }
                        s.forEach {
                            it.sendTitle(
                                "${ChatColor.GRAY}거절 시 ${ChatColor.GOLD}Shift${ChatColor.GRAY}키",
                                "${ChatColor.BLUE}${e.player.name}${ChatColor.RESET}님이 당신을 ${ChatColor.GOLD}소환${ChatColor.RESET}하려고 합니다",
                                0,
                                40,
                                20
                            )
                        }
                        e.player.sendTitle(
                            "남은 시간 ${ChatColor.GOLD}0 : 03",
                            "${ChatColor.BLUE}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                            0,
                            20,
                            0
                        )
                        e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        plugin.delay(20) {
                            e.player.sendTitle(
                                "남은 시간 ${ChatColor.GOLD}0 : 02",
                                "${ChatColor.BLUE}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                                0,
                                20,
                                0
                            )
                            e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        }
                        plugin.delay(40) {
                            e.player.sendTitle(
                                "남은 시간 ${ChatColor.GOLD}0 : 01",
                                "${ChatColor.BLUE}병사${ChatColor.RESET}들을 ${ChatColor.GOLD}소환${ChatColor.RESET}하는 중입니다..",
                                0,
                                20,
                                0
                            )
                            e.player.playSound(e.player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
                        }
                        e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                        coolTime[e.player.uniqueId] = 3600
                        plugin.delay(60) {
                            Bukkit.getOnlinePlayers().forEach { p ->
                                p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                            }
                            s.forEach {
                                if (!it.isSneaking) {
                                    it.teleport(Bukkit.getPlayer(blueKing!!)!!)
                                    it.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                                    it.sendTitle("${ChatColor.RED}도착 완료!", "${ChatColor.GRAY}왕을 지원하세요!")
                                    it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                                } else {
                                    e.player.sendMessage("병사 ${ChatColor.RED}${it.name}${ChatColor.RESET}이(가) 소환을 ${ChatColor.GOLD}거부${ChatColor.RESET}했습니다!")
                                    it.sendTitle("${ChatColor.GRAY}못 됐네요!", "${ChatColor.RED}왕의 집결 명령을 거부했습니다!", 0, 40, 20)
                                    it.playSound(it.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 1.0f)
                                }
                            }
                            e.player.sendMessage("${teamColor[2]}병사들을 불러내 전장에 ${ChatColor.GOLD}참가${ChatColor.RESET}시켰습니다")
                            e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f)
                        }
                    }
                }
                else {
                    if (coolTime[e.player.uniqueId]!! % 1200 / 20 >= 10) {
                        e.player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${coolTime[e.player.uniqueId]!! / 1200} : ${coolTime[e.player.uniqueId]!! % 1200 / 20}")
                        )
                    } else {
                        e.player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${coolTime[e.player.uniqueId]!! / 1200} : 0${coolTime[e.player.uniqueId]!! % 1200 / 20}")
                        )
                    }
                }
            }
        } else if (e.player.itemInHand.type == Material.GOLD_INGOT) {
            if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
                if (e.player.uniqueId == redKing) {
                    if (redLeap == 0) {
                        e.player.velocity = e.player.location.direction.multiply(2)
                        e.player.sendTitle("${ChatColor.GOLD}${ChatColor.BOLD}도약!", "", 0, 40, 20)
                        Bukkit.getOnlinePlayers().forEach {
                            it.spawnParticle(Particle.CLOUD, e.player.location, 100, 0.0, 0.0, 0.0, 0.2)
                            it.playSound(e.player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
                        }
                        e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                        redLeap = 1200
                    } else {
                        if (redLeap % 1200 / 20 >= 10) {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${redLeap / 1200} : ${redLeap % 1200 / 20}")
                            )
                        } else {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${redLeap / 1200} : 0${redLeap % 1200 / 20}")
                            )
                        }
                    }
                } else if (e.player.uniqueId == blueKing) {
                    if (blueLeap == 0) {
                        e.player.velocity = e.player.location.direction.multiply(2)
                        e.player.sendTitle("${ChatColor.GOLD}${ChatColor.BOLD}도약!", "", 0, 40, 20)
                        Bukkit.getOnlinePlayers().forEach {
                            it.spawnParticle(Particle.CLOUD, e.player.location, 100, 0.0, 0.0, 0.0, 0.2)
                            it.playSound(e.player.location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
                        }
                        e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                        blueLeap = 1200
                    } else {
                        if (blueLeap % 1200 / 20 >= 10) {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${blueLeap / 1200} : ${blueLeap % 1200 / 20}")
                            )
                        } else {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("${ChatColor.RED}${ChatColor.BOLD}남은 쿨타임 ${blueLeap / 1200} : 0${blueLeap % 1200 / 20}")
                            )
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (!e.player.isOp) {
            e.isCancelled = !move
        }
    }

    @EventHandler
    fun onPortal(e: PlayerPortalEvent) {
        e.isCancelled = true
    }

}