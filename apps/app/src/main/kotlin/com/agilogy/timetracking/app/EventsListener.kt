package com.agilogy.timetracking.app

import com.agilogy.timetracking.domain.TimeTrackingApp
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EventsListener(private val timeTrackingApp: TimeTrackingApp) : ListenerAdapter() {

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

    override fun onMessageReceived(event: MessageReceivedEvent) = runBlocking {

        try {
            logger.info("Message received: ${event.message.contentRaw} by ${event.author}")

            if (!event.author.isBot){
                val response = if (event.message.contentRaw.trim().lowercase() == "projectes") {
                    timeTrackingApp.listProjects().joinToString(", ") { it.name }
                } else {
                    "Hola ${event.author.name}! I don't understand."
                }
                event.channel.sendMessage(response).queue()
            }
        } catch (e: Exception) {
            logger.error("Unexpected exception", e)
        }

    }
}