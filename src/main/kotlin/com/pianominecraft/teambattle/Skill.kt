package com.pianominecraft.teambattle

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.lang.Exception

@Suppress("DEPRECATION")
class SkillHandler : Listener {

    @EventHandler
    fun onCast(e: PlayerInteractEvent) {
        if (e.action in listOf(
                Action.RIGHT_CLICK_BLOCK,
                Action.RIGHT_CLICK_AIR
            )) { // on rightclick
            skills.forEach { skill -> // all skill loop
                try { // try
                    if (e.player.itemInHand.type == skill.type) { // wand material
                        if (skill.displayName == null || e.player.itemInHand.itemMeta?.displayName == skill.displayName) { // wand name
                            if (skill.condition(e)) { // skill condition
                                if (Cooltime.getOf(skill.coolKey, e.player.uniqueId) <= 0) {
                                    if (skill.use) e.player.itemInHand.amount--
                                    Cooltime.setOf(skill.coolKey, e.player.uniqueId, skill.cooltime)
                                    skill.task(e)
                                } // success
                                else {
                                    if (Cooltime.getOf(skill.coolKey, e.player.uniqueId) % 1200 / 20 > 9) {
                                        e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%남은 시간 ${Cooltime.getOf(skill.coolKey, e.player.uniqueId) / 1200} %white%%bold%: %red%%bold%${Cooltime.getOf(skill.coolKey, e.player.uniqueId) % 1200 / 20}")))
                                    } else {
                                        e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(text("%red%%bold%남은 시간 ${Cooltime.getOf(skill.coolKey, e.player.uniqueId) / 1200} %white%%bold%: %red%%bold%0${Cooltime.getOf(skill.coolKey, e.player.uniqueId) % 1200 / 20}")))
                                    }
                                } // fail
                            }
                        }
                    }
                }
                catch (ex: Exception) {
                } // exception
            }
        }
    }

}

class Skill(
    val task: (PlayerInteractEvent) -> Unit,
    val condition: (PlayerInteractEvent) -> Boolean,
    val coolKey: String,
    val cooltime: Int,
    val type: Material,
    val use:  Boolean = true,
    val displayName: String? = null)
val skills = arrayListOf<Skill>()