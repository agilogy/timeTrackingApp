package com.agilogy.timetracking.app

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PingCommand : ListenerAdapter() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    //This gets called when a slash command gets used.
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        //Check if this is our /ping command
        if (event.name == "ping2") {
            logger.info("Command /ping got used")

            //Reply to the user
            val startTime = System.currentTimeMillis()
            event.reply("Ping ...").setEphemeral(true).queue {
                it.editOriginal("Pong: ${System.currentTimeMillis() - startTime}ms").queue()
            }
        }
    }
}