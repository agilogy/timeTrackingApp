package com.agilogy.timetracking.app

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands

class BasicBot {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val token = ""

            //Create a new JDA instance
            val jda = JDABuilder.createDefault(token)
                .addEventListeners(PingCommand())
                .build()

            //Register our first command /ping (don't use this method to often ;))
            jda.updateCommands().addCommands(
                Commands.slash("ping2", "Repeats messages back to you.")
                .addOption(OptionType.STRING, "message", "The message to repeat.")).queue()

            //Set the activity to "I'm Ready"
            jda.presence.activity = Activity.playing("Hola Agilogy")
        }
    }
}