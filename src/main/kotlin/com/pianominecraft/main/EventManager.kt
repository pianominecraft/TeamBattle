package com.pianominecraft.teambattle

import com.pianominecraft.teambattle.Constant.blueEmerald
import com.pianominecraft.teambattle.Constant.blueKing
import com.pianominecraft.teambattle.Constant.blueKingLive
import com.pianominecraft.teambattle.Constant.blueGold
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
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.MagmaCube
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Suppress("DEPRECATION")
class EventManager : Listener {

    private var first = true

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if (e.entity.uniqueId == blueKing || e.entity.uniqueId == redKing) {
            if (e.cause in listOf(EntityDamageEvent.DamageCause.FALL,
                    EntityDamageEvent.DamageCause.DROWNING,
                    EntityDamageEvent.DamageCause.LIGHTNING,
                    EntityDamageEvent.DamageCause.FALLING_BLOCK,
                    EntityDamageEvent.DamageCause.FIRE_TICK,
                    EntityDamageEvent.DamageCause.SUFFOCATION,
                    EntityDamageEvent.DamageCause.HOT_FLOOR,
                    EntityDamageEvent.DamageCause.CONTACT)) {
                e.isCancelled = true
            }
        }
        if (time > 0) {
            if (e.entity is Player) {
                e.isCancelled = true
            }
        }
        if (!damage) {
            if (e.entity is Player) {
                e.isCancelled = true
            }
        }
    } // when player got damage

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        e.entity.inventory.forEachIndexed { index, item ->
            if (item != null) {
                if (Math.random() > 0.5) {
                    e.entity.world.dropItemNaturally(e.entity.location, item)
                    e.entity.inventory.setItem(index, null)
                }
            }
        }
    }

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (e.entityType == EntityType.MAGMA_CUBE) {
            if ((e.entity as MagmaCube).size == 0) {
                e.entity.world.dropItemNaturally(e.entity.location, ItemStack(Material.MAGMA_CREAM))
            }
        }
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        e.isCancelled = true
        if (e.message != "준영은 바보라고 생각한다.") {
            Bukkit.getConsoleSender().sendMessage("[Chat] ${e.player.name} : ${e.message}")
            Bukkit.getOnlinePlayers().forEach {
                if (team[e.player.uniqueId] == team[it.uniqueId]) {
                    if (e.player.uniqueId == redKing) {
                        it.sendMessage(text("%yellow%[Team Chating] %white%<%dark_red%${e.player.name}%white%> ${e.message}"))
                    } else if (e.player.uniqueId == blueKing) {
                        it.sendMessage(text("%yellow%[Team Chating] %white%<%blue%${e.player.name}%white%> ${e.message}"))
                    } else if (e.player.uniqueId in leaders) {
                        if (team[e.player.uniqueId] == 1) {
                            it.sendMessage(text("%yellow%[Team Chating] %white%<#dd3333${e.player.name}%white%> ${e.message}"))
                        } else if (team[e.player.uniqueId] == 2) {
                            it.sendMessage(text("%yellow%[Team Chating] %white%<%dark_aqua%${e.player.name}%white%> ${e.message}"))
                        }
                    } else {
                        it.sendMessage(text("%yellow%[Team Chating] %white%<${teamColor[team[e.player.uniqueId]]}${e.player.name}%white%> ") + e.message)
                    }
                }
            }
        }
        else {
            Bukkit.getOnlinePlayers().forEach { p ->
                plugin.delay (0) {
                    p.sendTitle(text(""), text("%green%Hi Everyone!"), 0, 41, 0)
                    p.playSound(p.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                }
                plugin.delay (40) {
                    p.sendTitle(text("%green%Hi Everyone!"), text("%green%${e.player.name} thinks..."), 0, 41, 0)
                    p.playSound(p.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                }
                plugin.delay (80) {
                    p.sendTitle(text("%green%${e.player.name} thinks..."), text("%green%준영은 바보라고 생각한다."), 0, 41, 0)
                    p.playSound(p.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                }
                plugin.delay (120) {
                    p.sendTitle(text("%red%..."), text("%red%..."), 0, 41, 0)
                    p.playSound(p.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                }
                plugin.delay (160) {
                    p.playSound(p.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                    p.spawnParticle(Particle.EXPLOSION_HUGE, p.eyeLocation, 5, 0.0, 0.0, 0.0, 0.0)
                    var r = 255
                    var g = 0
                    var b = 0
                    for (i in 0..60) {
                        plugin.delay (i.toLong()) {
                            if (r == 255 && g != 255 && b == 0) g += 51
                            else if (r != 0 && g == 255 && b == 0) r -= 51
                            else if (r == 0 && g == 255 && b != 255) b += 51
                            else if (r == 0 && g != 0 && b == 255) g -= 51
                            else if (r != 255 && g == 0 && b == 255) r += 51
                            else if (r == 255 && g == 0 && b != 0) b -= 51
                            var color = "#"
                            if (r < 16) color += "0"
                            color += Integer.toHexString(r)
                            if (g < 16) color += "0"
                            color += Integer.toHexString(g)
                            if (b < 16) color += "0"
                            color += Integer.toHexString(b)
                            p.sendTitle(text("${color}Junyoung LLLLLL"), text("${color}준영 LLLLLL"), 0, 2, 0)
                            p.spawnParticle(Particle.TOTEM, p.eyeLocation, 1000, 0.0, 0.0, 0.0, 0.5)
                        }
                    }
                }
            }
        }
    } // when player chat

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    @EventHandler
    fun onTeamDamage(e: EntityDamageByEntityEvent) {
        if (e.entity is Player) {
            if (e.damager is Player) {
                if (team[e.entity.uniqueId] == team[e.damager.uniqueId]) {
                    e.isCancelled = true
                } else {
                    combat[e.entity.uniqueId] = 200
                }
            } else if (e.damager is Projectile) {
                if ((e.damager as Projectile).shooter is Player) {
                    val shooter = (e.damager as Projectile).shooter as Player
                    if (team[e.entity.uniqueId] == team[shooter.uniqueId]) {
                        e.isCancelled = true
                    }
                } else {
                    combat[e.entity.uniqueId] = 200
                }
            }
        }
    } // when player attack teammate

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        if (wl) {
            if (e.player.name !in lines) {
                plugin.delay { e.player.kickPlayer("당신은 화이트 리스트에 등록되지 못했습니다!") }
                return
            }
        }
        if (e.player.name !in managers) {
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
                Bukkit.getConsoleSender()
                    .sendMessage("${teamColor[team[e.player.uniqueId]]}팀이 없는 사람 접속됨 : ${e.player.name}")
            } else {
                Bukkit.getConsoleSender()
                    .sendMessage("${teamColor[team[e.player.uniqueId]]}이미 팀이 있는 사람 접속됨 : ${e.player.name}")
            }
            compass[e.player] = true
            diamond[e.player.uniqueId] = 0
            combat[e.player.uniqueId] = 0
            if (!o2.containsKey(e.player.uniqueId)) o2[e.player.uniqueId] = 9000
        } else {
            e.player.gameMode = GameMode.SPECTATOR
            team[e.player.uniqueId] = 0
        }
    } // when player join

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if (combat[e.player.uniqueId]!! > 0) {
            e.player.health = 0.0
        }
    } // when player quit

    @EventHandler
    fun onRespawn(e: PlayerRespawnEvent) {
        if (e.player.uniqueId == redKing) {
            e.player.gameMode = GameMode.SPECTATOR
            plugin.delay {
                Bukkit.getOnlinePlayers().forEach {
                    if (team[it.uniqueId] == 2) {
                        it.sendTitle(text("%red%레드 팀의 왕이 죽었습니다!"), text("%gray%이제 모든 상대편을 죽이세요!"), 20, 60, 20)
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                    } else if (team[it.uniqueId] == 1) {
                        it.sendTitle(text("%red%레드 팀의 왕이 죽었습니다!"), text("%gray%이제 리스폰이 불가능합니다!"), 20, 60, 20)
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                    }
                }
                if (blueKingLive) {
                    var first = true
                    var dx = 0.0
                    var dz = 0.0
                    plugin.delay (0) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(750.0, 300)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(750.0, 300)
                    }
                    for (i in 0..5999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%red%자기장의 축소가 시작됩니다"))
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (12000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(500.0, 300)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(500.0, 300)
                        first = true
                    }
                    for (i in 12000..17999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (24000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(250.0, 300)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(250.0, 300)
                        first = true
                    }
                    for (i in 24000..29999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (36000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(0.0, 300)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(0.0, 300)
                        first = true
                    }
                    for (i in 36000..41999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                }
            }
            redKingLive = false
        } // red king die
        else if (e.player.uniqueId == blueKing) {
            e.player.gameMode = GameMode.SPECTATOR
            plugin.delay {
                Bukkit.getOnlinePlayers().forEach {
                    if (team[it.uniqueId] == 1) {
                        it.sendTitle(
                            text("%blue%블루 팀의 왕이 죽었습니다!"),
                            text("%gray%이제 모든 상대편을 죽이세요!"),
                            20,
                            60,
                            20
                        )
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                    } else if (team[it.uniqueId] == 2) {
                        it.sendTitle(
                            text("%blue%블루 팀의 왕이 죽었습니다!"),
                            text("%gray%이제 리스폰이 불가능합니다!"),
                            20,
                            60,
                            20
                        )
                        it.playSound(it.location, Sound.ENTITY_WITHER_DEATH, 1f, 1f)
                    }
                }
                if (redKingLive) {
                    var first = true
                    var dx = 0.0
                    var dz = 0.0
                    plugin.delay (0) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(750.0, 3000)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(750.0, 3000)
                    }
                    for (i in 0..5999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%red%자기장의 축소가 시작됩니다"))
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (12000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(500.0, 3000)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(500.0, 3000)
                        first = true
                    }
                    for (i in 12000..17999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (24000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(250.0, 3000)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(250.0, 3000)
                        first = true
                    }
                    for (i in 24000..29999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                    plugin.delay (36000) {
                        Bukkit.getWorld("world")?.worldBorder?.setSize(0.0, 3000)
                        Bukkit.getWorld("world_nether")?.worldBorder?.setSize(0.0, 3000)
                        first = true
                    }
                    for (i in 36000..41999) {
                        plugin.delay (i.toLong()) {
                            Bukkit.getWorld("world")?.worldBorder?.let {
                                if (first) {
                                    val x = (Math.random() * 251).toInt() - 125
                                    val z = (Math.random() * 251).toInt() - 125
                                    val currentX = it.center.x
                                    val currentZ = it.center.z
                                    dx = x.toDouble() / 6000
                                    dz = z.toDouble() / 6000
                                    Bukkit.getOnlinePlayers().forEach { p ->
                                        p.sendMessage(text("%green%다음 자기장의 중심 %white%: %green%${(x + currentX).toInt()}%white%, %green%${(z + currentZ).toInt()}"))
                                    }
                                    first = false
                                }
                                it.setCenter(it.center.x + dx, it.center.z + dz)
                                with (Bukkit.getWorld("world_nether")?.worldBorder!!) {
                                    this.setCenter(this.center.x + dx, this.center.z + dz)
                                }
                            }
                        }
                    }
                }
            }
            blueKingLive = false
        } // blue king die
        else {
            if (team[e.player.uniqueId] == 1) { // red die
                if (redKingLive) {
                    if (e.player.uniqueId !in leaders) {
                        e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                        plugin.respawn(e.player, 60) {
                            e.player.sendTitle(text("%red%부활!"), text("%gray%다시 전장에 나가 싸우세요!"), 0, 60, 20)
                            val x = (Math.random()*51).toInt()-25
                            val z = (Math.random()*51).toInt()-25
                            var y = 256
                            val location = Bukkit.getPlayer(redKing!!)?.location!!
                            while(Location(e.player.world, location.x + x, y - 1.0, location.z + z).block.type == Material.AIR) {
                                y--
                            }
                            e.player.teleport(Location(e.player.world, location.x+x, y.toDouble(), location.z+z))
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
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                            }
                            Bukkit.getPlayer(redKing!!)?.sendMessage(text("${teamColor[1]}${e.player.name}%white%이(가) 당신의 위치에 %gold%리스폰%white%되었습니다!"))
                        }
                    }
                    else {
                        e.player.teleport(redKing?.let { Bukkit.getPlayer(it) }!!)
                        plugin.respawn(e.player, 40) {
                            e.player.sendTitle(text("%red%부활!"), text("%gray%다시 전장에 나가 싸우세요!"), 0, 60, 20)
                            val x = (Math.random()*51).toInt()-25
                            val z = (Math.random()*51).toInt()-25
                            var y = 256
                            val location = Bukkit.getPlayer(redKing!!)?.location!!
                            while(Location(e.player.world, location.x + x, y - 1.0, location.z + z).block.type == Material.AIR) {
                                y--
                            }
                            e.player.teleport(Location(e.player.world, location.x+x, y.toDouble(), location.z+z))
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
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                            }
                            Bukkit.getPlayer(redKing!!)?.sendMessage(text("${teamColor[1]}${e.player.name}%white%이(가) 당신의 위치에 %gold%리스폰%white%되었습니다!"))
                        }
                    }
                }
                else {
                    e.player.gameMode = GameMode.SPECTATOR
                    e.player.sendTitle(text("%red%죽었습니다"), text("%gray%으악!"), 20, 60, 20)
                    e.player.teleport(Location(Bukkit.getWorld("world")!!, 0.0, 150.0, 0.0))
                    Bukkit.getOnlinePlayers().forEach { p ->
                        if (team[p.uniqueId] == 1) {
                            if (p.gameMode == GameMode.SURVIVAL) return
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach { p ->
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        p.sendTitle(text("%blue%블루 팀 승리"), text("%dark_blue%레드 팀이 전멸했습니다"), 20, 60, 20)
                        p.gameMode = GameMode.SURVIVAL
                    }
                }
            }
            else if (team[e.player.uniqueId] == 2) { // blue die
                if (blueKingLive) {
                    if (e.player.uniqueId !in leaders) {
                        Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                        plugin.respawn(e.player, 60) {
                            e.player.sendTitle(text("%red%부활!"), text("%gray%다시 전장에 나가 싸우세요!"), 0, 60, 20)
                            val x = (Math.random()*51).toInt()-25
                            val z = (Math.random()*51).toInt()-25
                            var y = 256
                            val location = Bukkit.getPlayer(blueKing!!)?.location!!
                            while(Location(e.player.world, location.x + x, y - 1.0, location.z + z).block.type == Material.AIR) {
                                y--
                            }
                            e.player.teleport(Location(e.player.world, location.x+x, y.toDouble(), location.z+z))
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
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                            }
                            Bukkit.getPlayer(blueKing!!)?.sendMessage(text("${teamColor[2]}${e.player.name}%white%이(가) 당신의 위치에 %gold%리스폰%white%되었습니다!"))
                        }
                    }
                    else {
                        Bukkit.getPlayer(blueKing!!)?.let { e.player.teleport(it) }
                        plugin.respawn(e.player, 40) {
                            e.player.sendTitle(text("%red%부활!"), text("%gray%다시 전장에 나가 싸우세요!"), 0, 60, 20)
                            val x = (Math.random()*51).toInt()-25
                            val z = (Math.random()*51).toInt()-25
                            var y = 256
                            val location = Bukkit.getPlayer(blueKing!!)?.location!!
                            while(Location(e.player.world, location.x + x, y - 1.0, location.z + z).block.type == Material.AIR) {
                                y--
                            }
                            e.player.teleport(Location(e.player.world, location.x+x, y.toDouble(), location.z+z))
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
                                it.playSound(e.player.location, Sound.ITEM_TOTEM_USE, 1f, 1f)
                            }
                            Bukkit.getPlayer(blueKing!!)?.sendMessage(text("${teamColor[2]}${e.player.name}%white%이(가) 당신의 위치에 %gold%리스폰%white%되었습니다!"))
                        }
                    }
                }
                else {
                    e.player.gameMode = GameMode.SPECTATOR
                    e.player.sendTitle(text("%red%죽었습니다"), text("%gray%으악!"), 20, 60, 20)
                    e.player.teleport(Location(Bukkit.getWorld("world")!!, 0.0, 150.0, 0.0))
                    Bukkit.getOnlinePlayers().forEach { p ->
                        if (team[p.uniqueId] == 2) {
                            if (p.gameMode == GameMode.SURVIVAL) return
                        }
                    }
                    Bukkit.getOnlinePlayers().forEach { p ->
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        p.sendTitle(text("%red%레드 팀 승리"), text("%dark_red%블루 팀이 전멸했습니다"), 20, 60, 20)
                        p.gameMode = GameMode.SURVIVAL
                    }
                }
            }
        }
    } // when player die

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
        if (e.action in listOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) {
            when (e.player.itemInHand.type) {
                Material.DIAMOND -> {
                    if (diamond[e.player.uniqueId] == 0) {
                        if (e.player.uniqueId in leaders) {
                            if (team[e.player.uniqueId] == 1) {
                                if (redKingLive) {
                                    for (i in 0..2) {
                                        plugin.delay((i * 20).toLong()) {
                                            e.player.sendTitle(
                                                text("남은 시간 %gold%0 : 0${3 - i}"),
                                                text("%red%${Bukkit.getOfflinePlayer(redKing!!).name}을(를) %gold%지원%white%하러 가는 중입니다.."),
                                                0,
                                                20,
                                                0
                                            )
                                        }
                                    }
                                    e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                                    Bukkit.getPlayer(redKing!!)
                                        ?.sendMessage(text("${teamColor[1]}${e.player.name}%white%이(가) %gold%지원%white%하러 오는 중입니다..."))
                                    diamond[e.player.uniqueId] = 3000
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
                                        e.player.sendTitle(text("%red%도착 완료!"), text("%gray%왕을 지원하세요!"))
                                        Bukkit.getPlayer(redKing!!)
                                            ?.sendMessage(text("${teamColor[1]}${e.player.name}%white%이(가) %gold%도착 완료%white%했습니다!"))
                                        Bukkit.getPlayer(redKing!!)
                                            ?.playSound(
                                                e.player.location,
                                                Sound.ENTITY_ENDERMAN_TELEPORT,
                                                1f,
                                                1f
                                            )
                                    }
                                } else {
                                    e.player.sendMessage(text("%red%지원하러 갈 왕이 없습니다"))
                                }
                            } else if (team[e.player.uniqueId] == 2) {
                                if (blueKingLive) {
                                    for (i in 0..2) {
                                        plugin.delay((i * 20).toLong()) {
                                            e.player.sendTitle(
                                                text("남은 시간 %gold%0 : 0${3 - i}"),
                                                text("%blue%${Bukkit.getOfflinePlayer(blueKing!!).name}을(를) %gold%지원%white%하러 가는 중입니다.."),
                                                0,
                                                20,
                                                0
                                            )
                                        }
                                    }
                                    e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                                    Bukkit.getPlayer(blueKing!!)
                                        ?.sendMessage(text("${teamColor[2]}${e.player.name}%white%이(가) %gold%지원%white%하러 오는 중입니다..."))
                                    diamond[e.player.uniqueId] = 3000
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
                                        e.player.sendTitle(text("%red%도착 완료!"), text("%gray%왕을 지원하세요!"))
                                        Bukkit.getPlayer(blueKing!!)
                                            ?.sendMessage(text("${teamColor[2]}${e.player.name}%white%이(가) %gold%도착 완료%white%했습니다!"))
                                        Bukkit.getPlayer(blueKing!!)
                                            ?.playSound(
                                                e.player.location,
                                                Sound.ENTITY_ENDERMAN_TELEPORT,
                                                1f,
                                                1f
                                            )
                                    }
                                }
                            } else {
                                e.player.sendMessage(text("%red%지원하러 갈 왕이 없습니다"))
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
                                    text("%gray%거절 시 %gold%Shift%gray%키"),
                                    text("%red%${e.player.name}%white%님이 당신을 %gold%소환%white%하려고 합니다"),
                                    0,
                                    40,
                                    20
                                )
                            }
                            for (i in 0..2) {
                                plugin.delay((i * 20).toLong()) {
                                    e.player.sendTitle(
                                        text("남은 시간 %gold%0 : 0${3 - i}"),
                                        text("%red%병사%white%들을 %gold%소환%white%하는 중입니다.."),
                                        0,
                                        20,
                                        0
                                    )
                                }
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            diamond[e.player.uniqueId] = 3600
                            plugin.delay(60) {
                                Bukkit.getOnlinePlayers().forEach { p ->
                                    p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                    p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                                }
                                s.forEach {
                                    if (!it.isSneaking) {
                                        it.teleport(Bukkit.getPlayer(redKing!!)!!)
                                        it.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                                        it.sendTitle(text("%red%도착 완료!"), text("%gray%왕을 지원하세요!"))
                                        it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                                    } else {
                                        e.player.sendMessage(text("병사 %red%${it.name}%white%이(가) 소환을 %gold%거부%white%했습니다!"))
                                        it.sendTitle(
                                            text("%red%못 됐네요!"),
                                            text("%gray%왕의 집결 명령을 거부했습니다!"),
                                            0,
                                            40,
                                            20
                                        )
                                        it.playSound(it.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 1f)
                                    }
                                }
                                e.player.sendMessage(text("${teamColor[2]}병사들을 불러내 전장에 %gold%참가%white%시켰습니다"))
                                e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
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
                                    text("%gray%거절 시 %gold%Shift%gray%키"),
                                    text("%blue%${e.player.name}%white%님이 당신을 %gold%소환%white%하려고 합니다"),
                                    0,
                                    40,
                                    20
                                )
                            }
                            for (i in 0..2) {
                                plugin.delay((i * 20).toLong()) {
                                    e.player.sendTitle(
                                        text("남은 시간 %gold%0 : 0${3 - i}"),
                                        text("%blue%병사%white%들을 %gold%소환%white%하는 중입니다.."),
                                        0,
                                        20,
                                        0
                                    )
                                }
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            diamond[e.player.uniqueId] = 3600
                            plugin.delay(60) {
                                Bukkit.getOnlinePlayers().forEach { p ->
                                    p.spawnParticle(Particle.PORTAL, e.player.location, 500, 0.0, 0.0, 0.0, 1.5)
                                    p.spawnParticle(Particle.END_ROD, e.player.location, 150, 0.0, 0.0, 0.0, 0.1)
                                }
                                s.forEach {
                                    if (!it.isSneaking) {
                                        it.teleport(Bukkit.getPlayer(blueKing!!)!!)
                                        it.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                                        it.sendTitle(text("%red%도착 완료!"), text("%gray%왕을 지원하세요!"))
                                        it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
                                    } else {
                                        e.player.sendMessage(text("병사 %red%${it.name}%white%이(가) 소환을 %gold%거부%white%했습니다!"))
                                        it.sendTitle(
                                            text("%gray%못 됐네요!"),
                                            text("%red%왕의 집결 명령을 거부했습니다!"),
                                            0,
                                            40,
                                            20
                                        )
                                        it.playSound(it.location, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1f, 1f)
                                    }
                                }
                                e.player.sendMessage(text("${teamColor[2]}병사들을 불러내 전장에 %gold%참가%white%시켰습니다"))
                                e.player.playSound(e.player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
                            }
                        }
                    } else {
                        if (diamond[e.player.uniqueId]!! % 1200 / 20 >= 10) {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent("%red%%bold%남은 쿨타임 ${diamond[e.player.uniqueId]!! / 1200} : ${diamond[e.player.uniqueId]!! % 1200 / 20}")
                            )
                        } else {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent(text("%red%%bold%남은 쿨타임 ${diamond[e.player.uniqueId]!! / 1200} : 0${diamond[e.player.uniqueId]!! % 1200 / 20}"))
                            )
                        }
                    }
                }
                Material.GOLD_INGOT -> {
                    if (e.player.uniqueId == redKing) {
                        if (redGold == 0) {
                            e.player.velocity = e.player.location.direction.multiply(2)
                            e.player.sendTitle(text("%gold%%bold%도약!"), "", 0, 40, 20)
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.CLOUD, e.player.location, 100, 0.0, 0.0, 0.0, 0.2)
                                it.playSound(e.player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            redGold = 1200
                        } else {
                            if (redGold % 1200 / 20 >= 10) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${redGold / 1200} : ${redGold % 1200 / 20}"))
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${redGold / 1200} : 0${redGold % 1200 / 20}"))
                                )
                            }
                        }
                    } else if (e.player.uniqueId == blueKing) {
                        if (blueGold == 0) {
                            e.player.velocity = e.player.location.direction.multiply(2)
                            e.player.sendTitle(text("%gold%%bold%도약!"), "", 0, 40, 20)
                            Bukkit.getOnlinePlayers().forEach {
                                it.spawnParticle(Particle.CLOUD, e.player.location, 100, 0.0, 0.0, 0.0, 0.2)
                                it.playSound(e.player.location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f)
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            blueGold = 1200
                        } else {
                            if (blueGold % 1200 / 20 >= 10) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${blueGold / 1200} : ${blueGold % 1200 / 20}"))
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${blueGold / 1200} : 0${blueGold % 1200 / 20}"))
                                )
                            }
                        }
                    }
                }
                Material.EMERALD -> {
                    if (team[e.player.uniqueId] == 1) {
                        if (redEmerald == 0) {
                            val r = (Math.random()*71).toInt()
                            Bukkit.getOnlinePlayers().forEach {
                                if (team[it.uniqueId] == 1) {
                                    it.spawnParticle(Particle.SPELL, it.location, 100, 0.0, 0.0, 0.0, 0.2)
                                    it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f)
                                    it.sendTitle(text("${effectTexts[r]} %white%효과 발동!"), text("%red%${e.player.name}%white%이(가) %gold%팀 버프 %white%능력을 사용했습니다!"), 0, 40, 20)
                                    it.addPotionEffect(effects[r])
                                }
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            redEmerald = 6000
                        }
                        else {
                            if (redEmerald % 1200 / 20 >= 10) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${redEmerald / 1200} : ${redEmerald % 1200 / 20}"))
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${redEmerald / 1200} : 0${redEmerald % 1200 / 20}"))
                                )
                            }
                        }
                    }
                    else if (team[e.player.uniqueId] == 2) {
                        if (blueEmerald == 0) {
                            val r = (Math.random()*71).toInt()
                            Bukkit.getOnlinePlayers().forEach {
                                if (team[it.uniqueId] == 2) {
                                    it.spawnParticle(Particle.SPELL, it.location, 100, 0.0, 0.0, 0.0, 0.2)
                                    it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f)
                                    it.sendTitle(text("${effectTexts[r]} %white%효과 발동!"), text("%blue%${e.player.name}%white%이(가) %gold%팀 버프 %white%능력을 사용했습니다!"), 0, 40, 20)
                                    it.addPotionEffect(effects[r])
                                }
                            }
                            e.player.itemInHand.amount = e.player.itemInHand.amount - 1
                            blueEmerald = 6000
                        }
                        else {
                            if (blueEmerald % 1200 / 20 >= 10) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${blueEmerald / 1200} : ${blueEmerald % 1200 / 20}"))
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%red%%bold%남은 쿨타임 ${blueEmerald / 1200} : 0${blueEmerald % 1200 / 20}"))
                                )
                            }
                        }
                    }
                }
            }
        }
    } // when player click

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (!e.player.isOp) {
            e.isCancelled = !move
        }
    } // when player move

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (time > 0) {
            when (e.block.type) {
                Material.IRON_ORE -> {
                    if (e.player.itemInHand.type in listOf(
                            Material.STONE_PICKAXE,
                            Material.IRON_PICKAXE,
                            Material.DIAMOND_PICKAXE,
                            Material.NETHERITE_PICKAXE
                    )) {
                        e.isCancelled = true
                        e.block.type = Material.AIR
                        val r = (Math.random() * 2).toInt()
                        if (r == 0) {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.IRON_INGOT, 2))
                        } else {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.IRON_INGOT))
                        }
                    }
                }
                Material.GOLD_ORE -> {
                    if (e.player.itemInHand.type in listOf(
                            Material.IRON_PICKAXE,
                            Material.DIAMOND_PICKAXE,
                            Material.NETHERITE_PICKAXE
                        )) {
                        e.isCancelled = true
                        e.block.type = Material.AIR
                        val r = (Math.random() * 2).toInt()
                        if (r == 0) {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.GOLD_INGOT, 2))
                        } else {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.GOLD_INGOT))
                        }
                    }
                }
                Material.COAL_ORE -> {
                    if (e.player.itemInHand.type in listOf(
                            Material.WOODEN_PICKAXE,
                            Material.GOLDEN_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.IRON_PICKAXE,
                            Material.DIAMOND_PICKAXE,
                            Material.NETHERITE_PICKAXE
                        )) {
                        e.isCancelled = true
                        e.block.type = Material.AIR
                        val r = (Math.random() * 2).toInt()
                        if (r == 0) {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.COAL, 2))
                        } else {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.COAL))
                        }
                    }
                }
                Material.EMERALD_ORE -> {
                    if (e.player.itemInHand.type in listOf(
                            Material.IRON_PICKAXE,
                            Material.DIAMOND_PICKAXE,
                            Material.NETHERITE_PICKAXE
                        )) {
                        e.isCancelled = true
                        e.block.type = Material.AIR
                        val r = (Math.random() * 2).toInt()
                        if (r == 0) {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.EMERALD, 2))
                        } else {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.EMERALD))
                        }
                    }
                }
                Material.DIAMOND_ORE -> {
                    if (e.player.itemInHand.type in listOf(
                            Material.IRON_PICKAXE,
                            Material.DIAMOND_PICKAXE,
                            Material.NETHERITE_PICKAXE
                        )) {
                        e.isCancelled = true
                        e.block.type = Material.AIR
                        val r = (Math.random() * 2).toInt()
                        if (r == 0) {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.DIAMOND, 2))
                        } else {
                            e.block.world.dropItem(e.block.location, ItemStack(Material.DIAMOND))
                        }
                    }
                }
            }
        }
        if (e.block.type in listOf(Material.ENCHANTING_TABLE, Material.ANVIL)) {
            if (e.player.uniqueId !in leaders && e.player.uniqueId != redKing && e.player.uniqueId != blueKing) {
                e.isCancelled = true
                e.player.sendMessage(text("%red%왕이나 간부만 이것을 파괴할 수 있습니다!"))
            }
        }
    } // when player break ores

    @EventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        if (e.item.type == Material.HONEY_BOTTLE && e.item.itemMeta.displayName == text("%gold%산소통")) {
            if (e.player.world.name == "world_nether") {
                if (o2[e.player.uniqueId]!! < 200) {
                    o2[e.player.uniqueId] = 0
                    charging[e.player.uniqueId] = true
                    for (i in 0..29) {
                        plugin.delay (i.toLong()) {
                            o2[e.player.uniqueId] = o2[e.player.uniqueId]?.plus((9000 - o2[e.player.uniqueId]!!) / 4)!!
                            if (o2[e.player.uniqueId]!! % 1200 >= 10) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%green%%bold% 산소 보충중 %red%%bold%${o2[e.player.uniqueId]!! / 1200} %white%%bold%: %red%%bold%${o2[e.player.uniqueId]!! % 1200 / 20}"))
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent(text("%green%%bold% 산소 보충중 %red%%bold%${o2[e.player.uniqueId]!! / 1200} %white%%bold%: %red%%bold%0${o2[e.player.uniqueId]!! % 1200 / 20}"))
                                )
                            }
                        }
                    }
                    plugin.delay (30) {
                        o2[e.player.uniqueId] = 9000
                        if (o2[e.player.uniqueId]!! % 1200 >= 10) {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent(text("%green%%bold% 산소 보충 완료! %red%%bold%${o2[e.player.uniqueId]!! / 1200} %white%%bold%: %red%%bold%${o2[e.player.uniqueId]!! % 1200 / 20}"))
                            )
                        } else {
                            e.player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent(text("%green%%bold% 산소 보충 완료! %red%%bold%${o2[e.player.uniqueId]!! / 1200} %white%%bold%: %red%%bold%0${o2[e.player.uniqueId]!! % 1200 / 20}"))
                            )
                        }
                        plugin.delay (10) {
                            charging.remove(e.player.uniqueId)
                        }
                    }
                    e.player.sendMessage(text("%green%%bold%산소 보충!"))
                } else {
                    e.isCancelled = true
                    e.player.sendMessage(text("%gold%산소통%red%은 산소가 부족할 때만 사용해주세요"))
                }
            } else {
                e.isCancelled = true
                e.player.sendMessage(text("%gold%산소통%red%은 네더에서 산소가 부족할 때만 사용해주세요"))
            }
        }
    }

    @EventHandler
    fun onPortal(e: PlayerPortalEvent) {
        if (e.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) e.isCancelled = true
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (e.inventory.result?.type == Material.NETHERITE_INGOT) {
            if (e.whoClicked.uniqueId !in leaders && e.whoClicked.uniqueId != redKing && e.whoClicked.uniqueId != blueKing) {
                e.inventory.result = null
                e.inventory.matrix.forEach {
                    it.amount = it.amount - 1
                }
                if (team[e.whoClicked.uniqueId] == 1) {
                    redKing?.let {
                        Bukkit.getPlayer(it)?.let { p ->
                            p.sendMessage(text("%red%${e.whoClicked.name}이(가) 네더라이트를 제작했습니다!"))
                            p.sendMessage(text("%red%3초 뒤에 아이템이 배달옵니다!"))
                            p.sendMessage(text("%red%인벤토리 공간을 확보하세요!"))
                            plugin.delay(60) {
                                p.inventory.addItem(ItemStack(Material.NETHERITE_INGOT))
                            }
                        }
                    }
                } else if (team[e.whoClicked.uniqueId] == 2) {
                    blueKing?.let {
                        Bukkit.getPlayer(it)?.let { p ->
                            p.sendMessage(text("%red%${e.whoClicked.name}이(가) 네더라이트를 제작했습니다!"))
                            p.sendMessage(text("%red%3초 뒤에 아이템이 배달옵니다!"))
                            p.sendMessage(text("%red%인벤토리 공간을 확보하세요!"))
                            plugin.delay(60) {
                                p.inventory.addItem(ItemStack(Material.NETHERITE_INGOT))
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onAnvil(e: PrepareAnvilEvent) {
        if (e.inventory.getItem(1)?.type == Material.MAGMA_CREAM &&
            e.inventory.getItem(1)?.itemMeta?.displayName == text("%aqua%압축된 마그마 크림")) {
            when (e.inventory.getItem(0)?.type) {
                in listOf(
                    Material.LEATHER_BOOTS,
                    Material.LEATHER_LEGGINGS,
                    Material.LEATHER_CHESTPLATE,
                    Material.LEATHER_HELMET,
                    Material.IRON_BOOTS,
                    Material.IRON_LEGGINGS,
                    Material.IRON_CHESTPLATE,
                    Material.IRON_HELMET,
                    Material.DIAMOND_BOOTS,
                    Material.DIAMOND_LEGGINGS,
                    Material.DIAMOND_CHESTPLATE,
                    Material.DIAMOND_HELMET,
                    Material.GOLDEN_BOOTS,
                    Material.GOLDEN_LEGGINGS,
                    Material.GOLDEN_CHESTPLATE,
                    Material.GOLDEN_HELMET,
                    Material.NETHERITE_BOOTS,
                    Material.NETHERITE_LEGGINGS,
                    Material.NETHERITE_CHESTPLATE,
                    Material.NETHERITE_HELMET,
                    Material.CHAINMAIL_BOOTS,
                    Material.CHAINMAIL_LEGGINGS,
                    Material.CHAINMAIL_CHESTPLATE,
                    Material.CHAINMAIL_HELMET
                ) -> { // armor
                    e.result = e.inventory.getItem(0)?.clone()?.apply {
                        itemMeta = itemMeta.apply {
                            addEnchant(Enchantment.PROTECTION_FIRE, 4, false)
                        }
                    }
                }
                in listOf(
                    Material.WOODEN_SWORD,
                    Material.STONE_SWORD,
                    Material.IRON_SWORD,
                    Material.GOLDEN_SWORD,
                    Material.DIAMOND_SWORD,
                    Material.NETHERITE_SWORD
                ) -> { // sword
                    e.result = e.inventory.getItem(0)?.clone()?.apply {
                        itemMeta = itemMeta.apply {
                            addEnchant(Enchantment.FIRE_ASPECT, 1, false)
                        }
                    }
                }
                Material.BOW -> { // bow
                    e.result = e.inventory.getItem(0)?.clone()?.apply {
                        itemMeta = itemMeta.apply {
                            addEnchant(Enchantment.ARROW_FIRE, 1, false)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onAnvil(e: InventoryClickEvent) {
        if (e.whoClicked.inventory.firstEmpty() != -1) {
            if (e.inventory is AnvilInventory) {
                if (e.slotType == InventoryType.SlotType.RESULT) {
                    e.whoClicked.inventory.addItem(e.inventory.getItem(2))
                    e.inventory.setItem(2, null)
                    e.inventory.setItem(0, null)
                    e.inventory.getItem(1)?.apply {
                        amount--
                    }
                }
            }
        }
    }

}

val effects = listOf(
    PotionEffect(PotionEffectType.SPEED, 600, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 0, false, false),

    PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false),
    PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false),

    PotionEffect(PotionEffectType.SPEED, 600, 1, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 1, false, false),
    PotionEffect(PotionEffectType.SPEED, 600, 1, false, false),

    PotionEffect(PotionEffectType.SPEED, 200, 2, false, false),
    PotionEffect(PotionEffectType.SPEED, 200, 2, false, false),

    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0, false, false),

    PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 0, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 0, false, false),

    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1, false, false),

    PotionEffect(PotionEffectType.FAST_DIGGING, 200, 2, false, false),
    PotionEffect(PotionEffectType.FAST_DIGGING, 200, 2, false, false),

    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 0, false, false),

    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1200, 0, false, false),

    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 600, 1, false, false),

    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2, false, false),
    PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 2, false, false),

    PotionEffect(PotionEffectType.JUMP, 600, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 0, false, false),

    PotionEffect(PotionEffectType.JUMP, 1200, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 1200, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 1200, 0, false, false),
    PotionEffect(PotionEffectType.JUMP, 1200, 0, false, false),

    PotionEffect(PotionEffectType.JUMP, 600, 1, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 1, false, false),
    PotionEffect(PotionEffectType.JUMP, 600, 1, false, false),

    PotionEffect(PotionEffectType.JUMP, 200, 2, false, false),
    PotionEffect(PotionEffectType.JUMP, 200, 2, false, false),

    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 0, false, false),

    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0, false, false),

    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600, 1, false, false),

    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2, false, false),
    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 2, false, false),

    PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 400, 3, false, false),
)
val effectTexts = listOf(
    text("%gray%신속 I 30초"),
    text("%gray%신속 I 30초"),
    text("%gray%신속 I 30초"),
    text("%gray%신속 I 30초"),
    text("%gray%신속 I 30초"),

    text("%green%신속 I 60초"),
    text("%green%신속 I 60초"),
    text("%green%신속 I 60초"),
    text("%green%신속 I 60초"),

    text("%red%신속 II 30초"),
    text("%red%신속 II 30초"),
    text("%red%신속 II 30초"),

    text("%light_purple%신속 III 10초"),
    text("%light_purple%신속 III 10초"),

    text("%gray%성급함 I 30초"),
    text("%gray%성급함 I 30초"),
    text("%gray%성급함 I 30초"),
    text("%gray%성급함 I 30초"),
    text("%gray%성급함 I 30초"),

    text("%green%성급함 I 60초"),
    text("%green%성급함 I 60초"),
    text("%green%성급함 I 60초"),
    text("%green%성급함 I 60초"),

    text("%red%성급함 II 30초"),
    text("%red%성급함 II 30초"),
    text("%red%성급함 II 30초"),

    text("%light_purple%성급함 III 10초"),
    text("%light_purple%성급함 III 10초"),

    text("%gray%힘 I 30초"),
    text("%gray%힘 I 30초"),
    text("%gray%힘 I 30초"),
    text("%gray%힘 I 30초"),
    text("%gray%힘 I 30초"),

    text("%green%힘 I 60초"),
    text("%green%힘 I 60초"),
    text("%green%힘 I 60초"),
    text("%green%힘 I 60초"),

    text("%red%힘 II 30초"),
    text("%red%힘 II 30초"),
    text("%red%힘 II 30초"),

    text("%light_purple%힘 III 10초"),
    text("%light_purple%힘 III 10초"),

    text("%gray%점프 강화 I 30초"),
    text("%gray%점프 강화 I 30초"),
    text("%gray%점프 강화 I 30초"),
    text("%gray%점프 강화 I 30초"),
    text("%gray%점프 강화 I 30초"),

    text("%green%점프 강화 I 60초"),
    text("%green%점프 강화 I 60초"),
    text("%green%점프 강화 I 60초"),
    text("%green%점프 강화 I 60초"),

    text("%red%점프 강화 II 30초"),
    text("%red%점프 강화 II 30초"),
    text("%red%점프 강화 II 30초"),

    text("%light_purple%점프 강화 III 10초"),
    text("%light_purple%점프 강화 III 10초"),

    text("%gray%저항 I 30초"),
    text("%gray%저항 I 30초"),
    text("%gray%저항 I 30초"),
    text("%gray%저항 I 30초"),
    text("%gray%저항 I 30초"),

    text("%green%저항 I 60초"),
    text("%green%저항 I 60초"),
    text("%green%저항 I 60초"),
    text("%green%저항 I 60초"),

    text("%red%저항 II 30초"),
    text("%red%저항 II 30초"),
    text("%red%저항 II 30초"),

    text("%light_purple%저항 III 10초"),
    text("%light_purple%저항 III 10초"),

    text("%red%저%gold%항 %yellow%I%green%V %blue%2%dark_purple%0%red%초")
)